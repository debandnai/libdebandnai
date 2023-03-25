package com.salonsolution.app.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.salonsolution.app.R
import com.salonsolution.app.adapter.FoodImageSlideAdapter
import com.salonsolution.app.adapter.ServiceDetailsTabAdapter
import com.salonsolution.app.data.model.Description
import com.salonsolution.app.data.model.FoodDetailsModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.data.viewModel.FoodDetailsViewModel
import com.salonsolution.app.databinding.FragmentFoodDetailsBinding
import com.salonsolution.app.interfaces.ImageSliderClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FoodDetailsFragment : BaseFragment(), ImageSliderClickListener {
    private lateinit var binding: FragmentFoodDetailsBinding
    private lateinit var foodDetailsViewModel: FoodDetailsViewModel
    private lateinit var imageSlideAdapter: FoodImageSlideAdapter
    private lateinit var serviceDetailsTabAdapter: ServiceDetailsTabAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private val navArgs: FoodDetailsFragmentArgs by navArgs()
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_food_details, container, false)

        foodDetailsViewModel = ViewModelProvider(this)[FoodDetailsViewModel::class.java]
        binding.viewModel = foodDetailsViewModel
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
        foodDetailsViewModel.foodId = navArgs.foodId
        foodDetailsViewModel.serviceId = navArgs.serviceId
        foodDetailsViewModel.staffId = navArgs.staffId
        loadData()
    }

    private fun loadData() {
        foodDetailsViewModel.foodDetails(
            requestBodyHelper.getFoodDetailsRequest(
                navArgs.foodId,
                navArgs.serviceId,
                navArgs.staffId
            )
        )
    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        dashboardViewModel.toolbarTitle.value = ""
        context?.let {
            with(binding) {
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

                btAddToCart.setOnClickListener {
                    addToCart()
                }
                btGoToCart.setOnClickListener {
                    gotoNextDestination()
                }
                ivAdd.setOnClickListener {
                    updateFood(true)
                }

                ivRemove.setOnClickListener {
                    updateFood(false)
                }
            }
        }
    }

    private fun setObserver() {
        foodDetailsViewModel.foodDetailsResponseLiveData.observe(viewLifecycleOwner) {
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

        foodDetailsViewModel.foodAddResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadData()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                }
            }
        }

        foodDetailsViewModel.foodUpdateResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadData()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                }
            }
        }
    }

    private fun updateView(data: FoodDetailsModel?) {
        data?.let {
            with(binding) {
                foodDetailsViewModel.images = it.images
                tvServiceName.text = it.foodName
                tvPrice.text = it.price
                pageSize = it.images.size

                if (it.inCart== 1) {
                    btAddToCart.visibility = View.GONE
                    llInCart.visibility = View.VISIBLE
                } else {
                    btAddToCart.visibility = View.VISIBLE
                    llInCart.visibility = View.GONE
                }
                tvQuantity.text =it.qty.toString()
                //image slider
                imageSlideAdapter = FoodImageSlideAdapter(it.images, this@FoodDetailsFragment)
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
                        it.foodDesc
                    )
                )
                detailsList.add(
                    Description(
                        binding.root.context.getString(R.string.terms_and_conditions_2),
                        it.foodTerm
                    )
                )
                detailsList.add(
                    Description(
                        binding.root.context.getString(R.string.what_is_contains),
                        it.foodContain
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

    private fun gotoNextDestination() {
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.categoriseFragment, true)
            .setLaunchSingleTop(true)
            .build()
        val action = FoodDetailsFragmentDirections.actionGlobalCartFragment()
        findNavController().navigate(action,navOptions)
    }

    private fun addToCart() {
        foodDetailsViewModel.addFood(
            requestBodyHelper.getFoodAddRequest(
                foodDetailsViewModel.serviceId,
                foodDetailsViewModel.staffId,
                foodDetailsViewModel.foodId
            )
        )
    }

    private fun updateFood(isAdd:Boolean){
        if(isAdd){
            foodDetailsViewModel.updateFood(
                requestBodyHelper.getFoodUpdateRequest(
                    foodDetailsViewModel.serviceId,
                    foodDetailsViewModel.staffId,
                    foodDetailsViewModel.foodId
                )
            )
        }else{
            foodDetailsViewModel.updateFood(
                requestBodyHelper.getFoodRemovedRequest(
                    foodDetailsViewModel.serviceId,
                    foodDetailsViewModel.staffId,
                    foodDetailsViewModel.foodId
                )
            )
        }
    }


    override fun onItemClick(position: Int, link: String?, imageView: ImageView) {
        val action = ServiceDetailsFragmentDirections.actionGlobalPhotoViewerFragment(
            images = foodDetailsViewModel.images.map {
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
        if(this::foodDetailsViewModel.isInitialized)
            loadData()
    }


}