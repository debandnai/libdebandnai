package com.salonsolution.app.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.LatestReviewsListAdapter
import com.salonsolution.app.adapter.PopularServicesListAdapter
import com.salonsolution.app.adapter.RecentServicesListAdapter
import com.salonsolution.app.adapter.UpcomingOrdersListAdapter
import com.salonsolution.app.data.model.BookedServiceList
import com.salonsolution.app.data.model.PopularCategoryList
import com.salonsolution.app.data.model.UpcomingOrderList
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.HomeViewModel
import com.salonsolution.app.databinding.FragmentHomeBinding
import com.salonsolution.app.interfaces.HomePageClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(), HomePageClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var upcomingOrdersListAdapter: UpcomingOrdersListAdapter
    private lateinit var recentServicesListAdapter: RecentServicesListAdapter
    private lateinit var popularServicesListAdapter: PopularServicesListAdapter
    private lateinit var latestReviewsListAdapter: LatestReviewsListAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = true,
            isTopLevelDestination = true,
            isShowBottomNav = true,
            isShowToolbar = true
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)
        with(binding) {
            topLayout.visibility = View.INVISIBLE
            upcomingOrdersListAdapter = UpcomingOrdersListAdapter(this@HomeFragment)
            rvOrdersList.run {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = upcomingOrdersListAdapter
            }
            recentServicesListAdapter = RecentServicesListAdapter(this@HomeFragment)
            rvServiceList.run {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = recentServicesListAdapter
            }
            popularServicesListAdapter = PopularServicesListAdapter(this@HomeFragment)
            rvPopularList.run {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = popularServicesListAdapter
            }
            latestReviewsListAdapter = LatestReviewsListAdapter()
            rvReviewList.run {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = latestReviewsListAdapter
            }
            tvUpcomingOrders.setOnClickListener {
                homeViewModel.orderTabPosition = 0
                manageOrderTab()
            }
            tvRecentOrders.setOnClickListener {
                homeViewModel.orderTabPosition = 1
                manageOrderTab()
            }
            tvBookService.setOnClickListener {
                homeViewModel.serviceTabPosition = 0
                managerServiceTab()
            }
            tvRecentService.setOnClickListener {
                homeViewModel.serviceTabPosition = 1
                managerServiceTab()
            }

        }
        loadData()
    }

    private fun managerServiceTab() {
        if (homeViewModel.serviceTabPosition == 0) {
            binding.tvBookService.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.brand_color
                )
            )
            binding.tvRecentService.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.recent_text_color
                )
            )
            recentServicesListAdapter.submitList(null)
            recentServicesListAdapter.submitList(
                homeViewModel.bookedServicesList.map {
                    it.copy()
                }
            )
        } else {
            binding.tvBookService.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.recent_text_color
                )
            )
            binding.tvRecentService.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.brand_color
                )
            )
            recentServicesListAdapter.submitList(null)
            recentServicesListAdapter.submitList(
                homeViewModel.recentServicesList.map {
                    it.copy()
                }
            )
        }
    }

    private fun manageOrderTab() {
        if (homeViewModel.orderTabPosition == 0) {
            binding.tvUpcomingOrders.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.brand_color
                )
            )
            binding.tvRecentOrders.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.recent_text_color
                )
            )
            upcomingOrdersListAdapter.submitList(null)
            upcomingOrdersListAdapter.submitList(
                homeViewModel.upcomingOrdersList.map {
                    it.copy()
                }
            )
        } else {
            binding.tvUpcomingOrders.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.recent_text_color
                )
            )
            binding.tvRecentOrders.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.brand_color
                )
            )
            upcomingOrdersListAdapter.submitList(null)
            upcomingOrdersListAdapter.submitList(
                homeViewModel.recentOrdersList.map {
                    it.copy()
                }
            )
        }
    }

    private fun loadData() {
        homeViewModel.getAllData(requestBodyHelper.getDashboardAllRequest())
    }

    private fun setObserver() {
        homeViewModel.allDataResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    binding.topLayout.visibility = View.VISIBLE
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        homeViewModel.upcomingOrdersResponseLiveData.observe(viewLifecycleOwner) {
            if ((it.response?.data?.upcomingOrderList?.size ?: 0) > 0) {
                binding.tvUpcomingOrders.visibility = View.VISIBLE
                homeViewModel.upcomingOrdersList =
                    it.response?.data?.upcomingOrderList ?: arrayListOf()
                homeViewModel.orderTabPosition = 0
                homeViewModel.isShowOrdersLayout.value = true
                manageOrderTab()
            } else {
                binding.tvUpcomingOrders.visibility = View.GONE
                homeViewModel.orderTabPosition = 1
                manageOrderTab()
            }
        }
        homeViewModel.recentOrdersResponseLiveData.observe(viewLifecycleOwner) {
            if ((it.response?.data?.recentOrderList?.size ?: 0) > 0) {
                homeViewModel.isShowOrdersLayout.value = true
                binding.tvRecentOrders.visibility = View.VISIBLE
                homeViewModel.recentOrdersList = it.response?.data?.recentOrderList ?: arrayListOf()
                manageOrderTab()
            } else {
                binding.tvRecentOrders.visibility = View.GONE

            }
        }
        homeViewModel.bookedServicesResponseLiveData.observe(viewLifecycleOwner) {
            if ((it.response?.data?.bookedServiceList?.size ?: 0) > 0) {
                binding.tvBookService.visibility = View.VISIBLE
                homeViewModel.bookedServicesList =
                    it.response?.data?.bookedServiceList ?: arrayListOf()
                homeViewModel.serviceTabPosition = 0
                homeViewModel.isShowServiceLayout.value = true
                managerServiceTab()
            } else {
                homeViewModel.serviceTabPosition = 1
                binding.tvBookService.visibility = View.GONE
                managerServiceTab()
            }

        }
        homeViewModel.recentServicesResponseLiveData.observe(viewLifecycleOwner) {
            if ((it.response?.data?.recentServiceList?.size ?: 0) > 0) {
                homeViewModel.recentServicesList =
                    it.response?.data?.recentServiceList ?: arrayListOf()
                homeViewModel.isShowServiceLayout.value = true
                binding.tvBookService.visibility = View.VISIBLE
                managerServiceTab()
            } else {
                binding.tvRecentService.visibility = View.GONE
            }
        }
        homeViewModel.popularServicesResponseLiveData.observe(viewLifecycleOwner) {
            if ((it.response?.data?.popularCategoryList?.size ?: 0) > 0) {
                binding.popularLayout.visibility = View.VISIBLE
                popularServicesListAdapter.submitList(it.response?.data?.popularCategoryList?.map { item ->
                    item.copy()
                })
            } else {
                binding.popularLayout.visibility = View.GONE
            }
        }

        homeViewModel.latestReviewsResponseLiveData.observe(viewLifecycleOwner) {
            if ((it.response?.data?.latestReviewList?.size ?: 0) > 0) {
                binding.reviewLayout.visibility = View.VISIBLE
                latestReviewsListAdapter.submitList(it.response?.data?.latestReviewList?.map { item ->
                    item.copy()
                })
            } else {
                binding.reviewLayout.visibility = View.GONE
            }
        }

        homeViewModel.isShowOrdersLayout.observe(viewLifecycleOwner) {
            binding.ordersLayout.visibility = if (it) View.VISIBLE else View.GONE
            manageOrderTab()
        }

        homeViewModel.isShowServiceLayout.observe(viewLifecycleOwner) {
            binding.serviceLayout.visibility = if (it) View.VISIBLE else View.GONE
            managerServiceTab()
        }

    }


    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if (this::homeViewModel.isInitialized)
            loadData()
    }

    override fun onOrderItemClickListener(position: Int, upcomingOrderList: UpcomingOrderList) {
        upcomingOrderList.id?.let {
            val action = HomeFragmentDirections.actionHomeFragmentToOrderDetailsFragment(it)
            findNavController().navigate(action)
        }

    }

    override fun onServiceItemClickListener(position: Int, bookedServiceList: BookedServiceList) {
        bookedServiceList.id?.let {
            val action = HomeFragmentDirections.actionHomeFragmentToServiceDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onCategoryItemClickListener(
        position: Int,
        popularCategoryList: PopularCategoryList
    ) {
        popularCategoryList.id?.let {
            val action = HomeFragmentDirections.actionHomeFragmentToServiceListFragment(
                categoryId = popularCategoryList.id ?: -1
            )
            findNavController().navigate(action)
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                binding.root.context,
                "" + Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

            } else {
//                    showPermissionAlert(
//                        resources.getString(R.string.obr_needs_camera_permission_Enable_it_from_settings_question),
//                        true
//                    )
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            //showPermissionAlert(binding.root.context.getString(R.string.salon_needs_notification_permission_Enable_it_from_settings_question))
            }


            }


}