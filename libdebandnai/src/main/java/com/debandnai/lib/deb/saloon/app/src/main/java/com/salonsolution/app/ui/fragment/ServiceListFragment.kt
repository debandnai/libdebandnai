package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.PaginationLoadStateAdapter
import com.salonsolution.app.adapter.ServiceListPagingAdapter
import com.salonsolution.app.adapter.SubCategoriesListAdapter
import com.salonsolution.app.data.model.CategoryListModel
import com.salonsolution.app.data.model.ServicesList
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.data.viewModel.ServicesListViewModel
import com.salonsolution.app.databinding.FragmentServiceListBinding
import com.salonsolution.app.interfaces.CategoriesItemClickListener
import com.salonsolution.app.interfaces.ServiceListClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
class ServiceListFragment : BaseFragment(), CategoriesItemClickListener, ServiceListClickListener {

    private lateinit var binding: FragmentServiceListBinding
    private lateinit var servicesListViewModel: ServicesListViewModel
    private lateinit var subCategoriesListAdapter: SubCategoriesListAdapter
    private lateinit var serviceListPagingAdapter: ServiceListPagingAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val navArgs: ServiceListFragmentArgs by navArgs()
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_service_list, container, false)

        servicesListViewModel = ViewModelProvider(this)[ServicesListViewModel::class.java]
        binding.viewModel = servicesListViewModel
        binding.lifecycleOwner = this
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = false,
            isShowBottomNav = false,
            isShowToolbar = true
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDestinationResult()
        getIntentData()
        initView()
        setObserver()
        handleLoadStateOfAdapter()
    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        with(binding) {

            subCategoriesListAdapter =
                SubCategoriesListAdapter(this@ServiceListFragment)
            rvSubCategoriesList.run {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = subCategoriesListAdapter
            }

            serviceListPagingAdapter = ServiceListPagingAdapter(this@ServiceListFragment)

            binding.rvServiceList.run {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = serviceListPagingAdapter.withLoadStateFooter(
                    footer = PaginationLoadStateAdapter { serviceListPagingAdapter.retry() }
                )
            }

            ivSortBy.setOnClickListener {
                val action =
                    ServiceListFragmentDirections.actionGlobalServiceListSortDialogFragment(
                        servicesListViewModel.sortBy.value?.first ?: Constants.PRICE,
                        servicesListViewModel.sortBy.value?.second ?: 2

                    )
                findNavController().navigate(action)
            }

            includeSearchLayout.edtSearch.isFocusable = false
            includeSearchLayout.edtSearch.setOnClickListener {
                gotoSearch()
            }

            loadData()

        }

    }

    private fun setObserver() {
        servicesListViewModel.categoriesList.observe(viewLifecycleOwner) {
            subCategoriesListAdapter.submitList(it.map { list ->
                list.copy()
            }) {
                binding.rvSubCategoriesList.scrollToPosition(servicesListViewModel.selectedPosition)
            }
        }
        servicesListViewModel.selectedCategory.observe(viewLifecycleOwner) {
            dashboardViewModel.toolbarTitle.value = it.categoryName
            loadData()
        }

        servicesListViewModel.serviceListLiveData.observe(viewLifecycleOwner) {
            serviceListPagingAdapter.submitData(lifecycle, it)
        }
        servicesListViewModel.sortBy.observe(viewLifecycleOwner) {
            serviceListPagingAdapter.submitData(lifecycle, PagingData.empty())
            loadData()
        }

        servicesListViewModel.categoriesResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    servicesListViewModel.setCategoriesData(it.data?.response?.data)

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun getIntentData() {
        if (servicesListViewModel.selectedCategory.value?.id == null) {
            servicesListViewModel.selectedCategoryId.value = navArgs.categoryId
            servicesListViewModel.setCategoriesData(navArgs.categoriesList)
        }
    }

    private fun loadData() {
        servicesListViewModel.getServiceList(
            requestBodyHelper.getServiceListRequest(
                servicesListViewModel.sortBy.value?.first,
                servicesListViewModel.sortBy.value?.second,
                servicesListViewModel.selectedCategory.value?.id.toString()
            )
        )


    }

    private fun handleLoadStateOfAdapter() {
        serviceListPagingAdapter.addLoadStateListener { loadStates ->

            when (loadStates.refresh) {
                is LoadState.Loading -> {
                    Log.d("tag", "ServiceList Refresh Loading")
                    showLoading()
                }
                is LoadState.NotLoading -> {
                    Log.d("tag", "ServiceList Refresh Not Loading")
                    hideLoader()

                }
                is LoadState.Error -> {
                    Log.d("tag", "ServiceList Refresh Not Error")
                    hideLoader()
                    val loadStateError = loadStates.refresh as LoadState.Error
                    if (loadStateError.error is HttpException) {
                        handleApiError(
                            false,
                            (loadStateError.error as HttpException).code(),
//                            (loadStateError.error as HttpException).message()
                            binding.root.context.getString(R.string.something_went_wrong)
                        )
                    } else {
                        //no network
                        // loadingError = true
                        handleApiError(true, 0, "")

                    }

                }
            }

            val displayEmptyMessage =
                (loadStates.append.endOfPaginationReached && serviceListPagingAdapter.itemCount == 0)
            Log.d("tag", "ServiceList ${loadStates.append.endOfPaginationReached}")
            Log.d("tag", "ServiceList itemCount: ${serviceListPagingAdapter.itemCount}")
            if (displayEmptyMessage) {
                //Toast.makeText(binding.root.context, "No services", Toast.LENGTH_LONG).show()
                Log.d("tag", "No services")
            } else {
                Log.d("tag", "Data present")
            }
        }

    }

    private fun hideLoader() {
        loadingPopup?.dismiss()
    }

    private fun showLoading() {
        loadingPopup?.show()
    }

    override fun onItemClick(position: Int, categoryListModel: CategoryListModel) {
        servicesListViewModel.categoriesItemClick(categoryListModel)
    }

    private fun getDestinationResult() {
        val currentBackStackEntry = findNavController().currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Pair<String, Int>>(ListSortDialogFragment.SORT_BY)
            ?.observe(currentBackStackEntry) {
                servicesListViewModel.sortBy.value = it
            }

    }

    override fun onItemClick(position: Int, serviceList: ServicesList) {
        gotoNextDestination(position, serviceList)
    }

    override fun onAddClick(position: Int, serviceList: ServicesList) {
        gotoNextDestination(position, serviceList)
    }

    private fun gotoNextDestination(position: Int, serviceList: ServicesList) {
        serviceList.id?.let {
            val action =
                ServiceListFragmentDirections.actionServiceListFragmentToServiceDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun gotoSearch() {
        val extra = FragmentNavigatorExtras(
            binding.includeSearchLayout.llSearch to binding.root.context.getString(R.string.transition_name_to)
        )
        findNavController().navigate(R.id.action_global_searchFragment, null, null, extra)
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if (this::servicesListViewModel.isInitialized)
            loadData()
    }


}