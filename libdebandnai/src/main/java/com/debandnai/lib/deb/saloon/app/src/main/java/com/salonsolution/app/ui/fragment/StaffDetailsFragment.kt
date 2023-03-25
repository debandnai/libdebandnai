package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.salonsolution.app.R
import com.salonsolution.app.adapter.PaginationLoadStateAdapter
import com.salonsolution.app.adapter.StaffImageSlideAdapter
import com.salonsolution.app.adapter.StaffReviewsPagingAdapter
import com.salonsolution.app.data.model.StaffDetailsModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.data.viewModel.StaffDetailsViewModel
import com.salonsolution.app.databinding.FragmentStaffDetailsBinding
import com.salonsolution.app.interfaces.ImageSliderClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.UtilsCommon
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
class StaffDetailsFragment : BaseFragment(), ImageSliderClickListener {
    private lateinit var binding: FragmentStaffDetailsBinding
    private lateinit var staffDetailsViewModel: StaffDetailsViewModel
    private lateinit var imageSlideAdapter: StaffImageSlideAdapter
    private lateinit var staffReviewsPagingAdapter: StaffReviewsPagingAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private val navArgs: StaffDetailsFragmentArgs by navArgs()
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private var currentPage = 0
    private var pageSize = 0

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_staff_details, container, false)

        staffDetailsViewModel = ViewModelProvider(this)[StaffDetailsViewModel::class.java]
        binding.viewModel = staffDetailsViewModel
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
        staffDetailsViewModel.staffId = navArgs.staffId
        staffDetailsViewModel.serviceTime = navArgs.serviceTime
        staffDetailsViewModel.serviceId = navArgs.serviceId
        staffDetailsViewModel.staffName = navArgs.staffName

    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        dashboardViewModel.toolbarTitle.value = staffDetailsViewModel.staffName
        staffReviewsPagingAdapter = StaffReviewsPagingAdapter()
        context?.let {
            with(binding) {

                rvReviewList.run {
                    setHasFixedSize(true)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = staffReviewsPagingAdapter.withLoadStateFooter(
                        footer = PaginationLoadStateAdapter { staffReviewsPagingAdapter.retry() }
                    )
                }

                viewPagerImage.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        Log.d("Page", "onPageScrolled: $position")
                    }

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        Log.d("Page", "onPageSelected: $position")
                        currentPage = position
                        // autoImageSlider()

                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)

                    }

                })

                btBookSchedule.setOnClickListener {
                    gotoNextDestination()
                }
                pbReview.visibility = View.GONE
            }
        }

        loadData()
    }

    private fun loadData() {
        staffDetailsViewModel.staffDetails(
            requestBodyHelper.getStaffDetailsRequest(
                staffDetailsViewModel.serviceId,
                staffDetailsViewModel.staffId
            )
        )
        staffDetailsViewModel.staffReviews(
            requestBodyHelper.getStaffReviewsRequest(
                staffDetailsViewModel.serviceId,
                staffDetailsViewModel.staffId
            )
        )
    }

    private fun setObserver() {
        staffDetailsViewModel.staffDetailsResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    updateView(it.data?.response?.data)
                    Log.d(mTag, it.toString())
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode,  binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
        staffDetailsViewModel.staffReviewsLiveData.observe(viewLifecycleOwner) {
            staffReviewsPagingAdapter.submitData(lifecycle, it)
        }
    }

    private fun updateView(data: StaffDetailsModel?) {
        data?.let {
            with(binding) {
                dashboardViewModel.toolbarTitle.value = it.staffName
                staffDetailsViewModel.staffName = it.staffName?:""
                staffDetailsViewModel.images = it.images
                tvAbout.text = it.about
                pageSize = it.images.size
                tvTotalRating.text = it.avgReview
                tvNoOfReview.text = binding.root.context.getString(R.string.total_review,it.totalReview?:0)
                ratingBar.rating = try {
                    it.avgReview?.toFloat()?:0F
                }catch (e:Exception){
                    0F
                }
                lpFiveStar.progress = UtilsCommon.calculateProgress(it.fiveStar, it.totalReview)
                lpFourStar.progress = UtilsCommon.calculateProgress(it.fourStar, it.totalReview)
                lpThreeStar.progress = UtilsCommon.calculateProgress(it.threeStar, it.totalReview)
                lpTwoStar.progress = UtilsCommon.calculateProgress(it.twoStar, it.totalReview)
                lpOneStar.progress = UtilsCommon.calculateProgress(it.oneStar, it.totalReview)

                //image slider
                imageSlideAdapter = StaffImageSlideAdapter(it.images, this@StaffDetailsFragment)
                viewPagerImage.adapter = imageSlideAdapter
                TabLayoutMediator(tabIndicator, viewPagerImage) { tab, _ ->
                    //tab.text = adapter.getPageTitle(position)
                    viewPagerImage.setCurrentItem(tab.position, true)
                }.attach()
                //autoImageSlider()
            }
        }

    }

    private fun handleLoadStateOfAdapter() {
        staffReviewsPagingAdapter.addLoadStateListener { loadStates ->

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
                (loadStates.append.endOfPaginationReached && staffReviewsPagingAdapter.itemCount == 0)
            Log.d("tag", "ServiceList ${loadStates.append.endOfPaginationReached}")
            Log.d("tag", "ServiceList itemCount: ${staffReviewsPagingAdapter.itemCount}")
            if (displayEmptyMessage) {
                //Toast.makeText(binding.root.context, "No services", Toast.LENGTH_LONG).show()
                Log.d("tag", "No services")
            } else {
                Log.d("tag", "Data present")
            }
        }

    }

    private fun hideLoader() {
        binding.pbReview.visibility = View.GONE
    }

    private fun showLoading() {
        binding.pbReview.visibility = View.VISIBLE
    }

    private fun gotoNextDestination() {
        val action =
            StaffDetailsFragmentDirections.actionStaffDetailsFragmentToStaffCalendarFragment(staffDetailsViewModel.serviceId,staffDetailsViewModel.staffId,staffDetailsViewModel.serviceTime)
        findNavController().navigate(action)
    }

    override fun onItemClick(position: Int, link: String?, imageView: ImageView) {
        val action = ServiceDetailsFragmentDirections.actionGlobalPhotoViewerFragment(
            images = staffDetailsViewModel.images.map {
                it.imageLink?:""
            }.toTypedArray(),
            position = position
        )
        val extra = FragmentNavigatorExtras(
            imageView to imageView.transitionName
        )
        findNavController().navigate(action, extra)
    }

    private fun getDestinationResult() {
        val currentBackStackEntry = findNavController().currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Int>(PhotoViewerFragment.CURRENT_POSITION)?.observe(currentBackStackEntry) {
            binding.viewPagerImage.currentItem = it
        }
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if(this::staffDetailsViewModel.isInitialized)
             loadData()
    }


}