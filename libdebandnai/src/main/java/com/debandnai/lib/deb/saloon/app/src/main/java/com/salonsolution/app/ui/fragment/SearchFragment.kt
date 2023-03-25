package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.PaginationLoadStateAdapter
import com.salonsolution.app.adapter.ServiceListPagingAdapter
import com.salonsolution.app.data.model.ServicesList
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.SearchViewModel
import com.salonsolution.app.databinding.FragmentSearchBinding
import com.salonsolution.app.interfaces.ServiceListClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.UtilsCommon
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException

@AndroidEntryPoint
class SearchFragment : Fragment(),ServiceListClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var serviceListPagingAdapter: ServiceListPagingAdapter
    private val mTag = "tag"
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        binding.viewModel = searchViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = false,
            isShowBottomNav = false,
            isShowToolbar = false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        handleLoadStateOfAdapter()
    }


    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        with(binding){
            val animation = TransitionInflater.from(root.context).inflateTransition(
                android.R.transition.move
            )
            sharedElementEnterTransition = animation
            sharedElementReturnTransition = animation
            serviceListPagingAdapter = ServiceListPagingAdapter( this@SearchFragment)
            binding.rvServiceList.run {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = serviceListPagingAdapter.withLoadStateFooter(
                    footer = PaginationLoadStateAdapter { serviceListPagingAdapter.retry() }
                )
            }
            edtSearch.requestFocus()
            UtilsCommon.showKeyboard(edtSearch)

            ivBackArrow.setOnClickListener {
                UtilsCommon.hideKeyboard(it)
                findNavController().popBackStack()
            }
        }
    }

    private fun setObserver() {
        searchViewModel.searchData.observe(viewLifecycleOwner){
            searchViewModel.searchDebounced(it)

        }
        /*searchViewModel.serviceSearchResponseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is ResponseState.Loading -> {
                    binding.pbLoader.visibility = View.VISIBLE
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    binding.pbLoader.visibility = View.GONE
                    Log.d(mTag,it.toString())
                    searchViewModel.totalServicesCount.value = it.data?.response?.data?.totalCount.toString()
                    serviceSearListAdapter.submitList(it.data?.response?.data?.servicesList?.map { list ->
                        list.copy()
                    })
                }
                is ResponseState.Error -> {
                    binding.pbLoader.visibility = View.GONE
                    Log.d(mTag,it.toString())
                    handleApiError(it.isNetworkError,it.errorCode, it.errorMessage)
                }
            }
        }*/

        searchViewModel.serviceSearchListLiveData.observe(viewLifecycleOwner) {
            serviceListPagingAdapter.submitData(lifecycle, it)
        }
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
                binding.tvNoService.visibility = View.VISIBLE
                binding.rvServiceList.visibility = View.GONE
                Log.d("tag", "No services")
            } else {
                Log.d("tag", "Data present")
                binding.tvNoService.visibility = View.GONE
                binding.rvServiceList.visibility = View.VISIBLE
            }
        }

    }

    private fun hideLoader() {
        binding.pbLoader.visibility = View.GONE
    }

    private fun showLoading() {
        binding.pbLoader.visibility = View.VISIBLE
    }

    override fun onItemClick(position: Int, serviceList: ServicesList) {
        gotoServiceDetailsPage(position,serviceList)
    }

    override fun onAddClick(position: Int, serviceList: ServicesList) {
        gotoServiceDetailsPage(position,serviceList)
    }

    private fun gotoServiceDetailsPage(position: Int, serviceList: ServicesList){
        serviceList.id?.let {
            val action = SearchFragmentDirections.actionSearchFragmentToServiceDetailsFragment(it)
            findNavController().navigate(action)
        }
    }


}