package com.salonsolution.app.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.salonsolution.app.R
import com.salonsolution.app.adapter.ImageSlideAdapter
import com.salonsolution.app.adapter.ServiceDetailsTabAdapter
import com.salonsolution.app.data.model.Description
import com.salonsolution.app.data.model.ServiceDetailsModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.data.viewModel.ServicesDetailsViewModel
import com.salonsolution.app.databinding.FragmentServiceDetailsBinding
import com.salonsolution.app.interfaces.ImageSliderClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ServiceDetailsFragment : BaseFragment(), ImageSliderClickListener, Runnable {
    private lateinit var binding: FragmentServiceDetailsBinding
    private lateinit var servicesDetailsViewModel: ServicesDetailsViewModel
    private lateinit var imageSlideAdapter: ImageSlideAdapter
    private lateinit var serviceDetailsTabAdapter: ServiceDetailsTabAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private val navArgs: ServiceDetailsFragmentArgs by navArgs()
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private var currentPage = 0
    private var pageSize = 0
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val mScrollTimeInMillis: Long = 1000

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_service_details, container, false)

        servicesDetailsViewModel = ViewModelProvider(this)[ServicesDetailsViewModel::class.java]
        binding.viewModel = servicesDetailsViewModel
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
    }

    private fun getIntentData() {
        Log.d("tag", "ServiceId: ${navArgs.serviceId}")
        servicesDetailsViewModel.serviceId = navArgs.serviceId
        loadData()
    }

    private fun loadData() {
        servicesDetailsViewModel.serviceDetails(requestBodyHelper.getServiceDetailsRequest(navArgs.serviceId))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        dashboardViewModel.toolbarTitle.value = ""
        with(binding) {
            viewPagerImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d("Page", "onPageSelected: $position")
                    currentPage = position
                    // autoImageSlider()
                    mHandler.postDelayed({ startAutoCycle() }, 2000)

                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    stopAutoCycle()
                }

            })


            /*// adjust the shared element mapping when entering
            setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(
                        names: List<String?>, sharedElements: MutableMap<String?, View?>
                    ) {
                        val holder =
                            (viewPagerImage[0] as? RecyclerView)?.findViewHolderForAdapterPosition(
                                dashboardViewModel.photoViewPosition.value ?: 0
                            )
                        val view = holder?.itemView ?: return
                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = view.findViewById(R.id.imageView)
                    }
                })
            //applied as an exit transition
            exitTransition = TransitionInflater.from(root.context).inflateTransition(
                android.R.transition.move
            )*/
            binding.btAddToCart.setOnClickListener {
                gotoStaffList()
            }

        }
    }

    private fun gotoStaffList() {
        val action =
            ServiceDetailsFragmentDirections.actionServiceDetailsFragmentToStaffListFragment(
                servicesDetailsViewModel.serviceId,
                servicesDetailsViewModel.serviceTime ?: ""
            )
        findNavController().navigate(action)
    }

    private fun setObserver() {
        servicesDetailsViewModel.serviceDetailsResponseLiveData.observe(viewLifecycleOwner) {
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
                    Log.d(mTag, it.errorMessage ?: "")
                    handleApiError(
                        it.isNetworkError,
                        it.errorCode,
                        binding.root.context.getString(R.string.something_went_wrong)
                    )
                }
            }
        }
    }

    private fun updateView(data: ServiceDetailsModel?) {
        data?.let {
            with(binding) {
                servicesDetailsViewModel.serviceTime = it.timeHr
                servicesDetailsViewModel.images = it.images
                tvServiceName.text = it.serviceName
                tvPrice.text = it.price
                tvServiceTime.text = it.timeHr
                pageSize = it.images.size

                //image slider
                imageSlideAdapter = ImageSlideAdapter(it.images, this@ServiceDetailsFragment)
                viewPagerImage.adapter = imageSlideAdapter
                TabLayoutMediator(tabIndicator, viewPagerImage) { tab, _ ->
                    //tab.text = adapter.getPageTitle(position)
                    viewPagerImage.setCurrentItem(tab.position, true)
                }.attach()
                //autoImageSlider()

                //details tab
                val detailsList: ArrayList<Description> = arrayListOf()
                detailsList.add(
                    Description(
                        binding.root.context.getString(R.string.description),
                        it.serviceDesc
                    )
                )
                detailsList.add(
                    Description(
                        binding.root.context.getString(R.string.terms_and_conditions_2),
                        it.serviceTerm
                    )
                )
                serviceDetailsTabAdapter = ServiceDetailsTabAdapter(detailsList)
                viewPagerDetails.adapter = serviceDetailsTabAdapter
                TabLayoutMediator(tabDetails, viewPagerDetails) { tab, position ->
                    tab.text = detailsList[position].title
                    viewPagerDetails.setCurrentItem(tab.position, true)
                }.attach()
            }
        }

    }

    override fun onItemClick(position: Int, link: String?, imageView: ImageView) {
        val action = ServiceDetailsFragmentDirections.actionGlobalPhotoViewerFragment(
            images = servicesDetailsViewModel.images.map {
                it.imageLink ?: ""
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
        savedStateHandle?.getLiveData<Int>(PhotoViewerFragment.CURRENT_POSITION)
            ?.observe(currentBackStackEntry) {
                binding.viewPagerImage.currentItem = it
            }
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if (this::servicesDetailsViewModel.isInitialized)
            loadData()
    }

    /**
     * This method stars the auto cycling
     */
    fun startAutoCycle() {
        //clean previous callbacks
        mHandler.removeCallbacks(this)
        //Run the loop for the first time
        if (imageSlideAdapter.itemCount > 1)
            mHandler.postDelayed(this, 1000)
    }

    /**
     * This method cancels the auto cycling
     */
    fun stopAutoCycle() {
        //clean callback
        mHandler.removeCallbacks(this)
    }

    override fun run() {
        try {
            slideToNextPosition()
        } finally {
            // continue the loop
            mHandler.postDelayed(this, mScrollTimeInMillis)
        }
    }

    private fun slideToNextPosition() {
        var currentPosition = binding.viewPagerImage.currentItem
        if (currentPosition == (imageSlideAdapter.itemCount - 1))
            currentPosition = -1
        binding.viewPagerImage.setCurrentItem(currentPosition + 1, true)
    }


}