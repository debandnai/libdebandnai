package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.PaginationLoadStateAdapter
import com.salonsolution.app.adapter.StaffListPagingAdapter
import com.salonsolution.app.data.model.StaffList
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.StaffListViewModel
import com.salonsolution.app.databinding.FragmentStaffListBinding
import com.salonsolution.app.interfaces.StaffListClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
class StaffListFragment : BaseFragment(), StaffListClickListener {

    private lateinit var binding: FragmentStaffListBinding
    private lateinit var staffListViewModel: StaffListViewModel
    private lateinit var staffListPagingAdapter: StaffListPagingAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val navArgs: StaffListFragmentArgs by navArgs()
    // private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_staff_list, container, false)

        staffListViewModel = ViewModelProvider(this)[StaffListViewModel::class.java]
        binding.viewModel = staffListViewModel
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

    private fun getIntentData() {
        staffListViewModel.serviceId = navArgs.serviceId
        staffListViewModel.serviceTime = navArgs.serviceTime
        loadData()
    }


    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        context?.let {
            with(binding) {

                staffListPagingAdapter =
                    StaffListPagingAdapter(this@StaffListFragment)

                binding.rvServiceList.run {
                    setHasFixedSize(true)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = staffListPagingAdapter.withLoadStateFooter(
                        footer = PaginationLoadStateAdapter { staffListPagingAdapter.retry() }
                    )
                }

                ivSortBy.setOnClickListener {
                    val action =
                        FoodDetailsFragmentDirections.actionGlobalServiceListSortDialogFragment(
                            staffListViewModel.sortBy.value?.first ?: Constants.NAME,
                            staffListViewModel.sortBy.value?.second ?: 2,
                            isShowPrice = false

                        )
                    findNavController().navigate(action)
                }

                btChooseAnyOne.setOnClickListener {
                    staffListViewModel.anyStaffId.value?.let {
                        gotoNextDestination(it,"",staffListViewModel.serviceId, staffListViewModel.serviceTime)
                    }

                }

                loadData()

            }
        }

    }

    private fun setObserver() {

        staffListViewModel.staffListLiveData.observe(viewLifecycleOwner) {
            staffListPagingAdapter.submitData(lifecycle, it)
        }
        staffListViewModel.sortBy.observe(viewLifecycleOwner) {
            staffListPagingAdapter.submitData(lifecycle, PagingData.empty())
            loadData()
        }
    }

    private fun loadData() {
        staffListViewModel.getStaffList(
            requestBodyHelper.getStaffListRequest(
                staffListViewModel.sortBy.value?.first,
                staffListViewModel.sortBy.value?.second,
                staffListViewModel.serviceId
            )
        )


    }

    private fun handleLoadStateOfAdapter() {
        staffListPagingAdapter.addLoadStateListener { loadStates ->

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
                (loadStates.append.endOfPaginationReached && staffListPagingAdapter.itemCount == 0)
            Log.d("tag", "ServiceList ${loadStates.append.endOfPaginationReached}")
            Log.d("tag", "ServiceList itemCount: ${staffListPagingAdapter.itemCount}")
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

    private fun getDestinationResult() {
        val currentBackStackEntry = findNavController().currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Pair<String, Int>>(ListSortDialogFragment.SORT_BY)
            ?.observe(currentBackStackEntry) {
                staffListViewModel.sortBy.value = it
            }

    }

    override fun onItemClick(position: Int, staffList: StaffList) {
        gotoNextDestination(
            staffList.id?:-1,
            staffList.staffName?:"",
            staffListViewModel.serviceId,
            staffListViewModel.serviceTime
        )
    }

    private fun gotoNextDestination(id: Int, name: String, serviceId: Int, serviceTime: String) {
        val action = StaffListFragmentDirections.actionStaffListFragmentToStaffDetailsFragment(id,serviceId,name,serviceTime)
        findNavController().navigate(action)
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if(this::staffListViewModel.isInitialized)
             loadData()
    }

}