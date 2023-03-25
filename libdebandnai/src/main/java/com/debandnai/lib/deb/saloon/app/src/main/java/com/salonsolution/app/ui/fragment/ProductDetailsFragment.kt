package com.salonsolution.app.ui.fragment

import android.app.Dialog
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
import com.salonsolution.app.adapter.ProductImageSlideAdapter
import com.salonsolution.app.adapter.ServiceDetailsTabAdapter
import com.salonsolution.app.data.model.Description
import com.salonsolution.app.data.model.ProductDetailsModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.data.viewModel.ProductDetailsViewModel
import com.salonsolution.app.databinding.FragmentProductDetailsBinding
import com.salonsolution.app.interfaces.DialogButtonClick
import com.salonsolution.app.interfaces.ImageSliderClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : BaseFragment(), ImageSliderClickListener {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var productDetailsViewModel: ProductDetailsViewModel
    private lateinit var imageSlideAdapter: ProductImageSlideAdapter
    private lateinit var serviceDetailsTabAdapter: ServiceDetailsTabAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private val navArgs: ProductDetailsFragmentArgs by navArgs()
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false)

        productDetailsViewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]
        binding.viewModel = productDetailsViewModel
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
        productDetailsViewModel.serviceId = navArgs.serviceId
        productDetailsViewModel.staffId = navArgs.staffId
        productDetailsViewModel.productId = navArgs.productId
       loadData()
    }

    private fun loadData() {
        productDetailsViewModel.productDetails(requestBodyHelper.getProductDetailsRequest(navArgs.productId,navArgs.serviceId, navArgs.staffId))
    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        dashboardViewModel.toolbarTitle.value = ""
        context?.let {
            with(binding) {
                btSelectFood.visibility = View.GONE
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
                    if(productDetailsViewModel.inCart==1)
                        gotoCart()
                    else
                        addToCart()
                }

                btSelectFood.setOnClickListener {
                    gotoFoodList()
                }

            }
        }
    }

    private fun addToCart() {
        productDetailsViewModel.addProduct(requestBodyHelper.getProductAddRequest(
            serviceId = productDetailsViewModel.serviceId,
            staffId = productDetailsViewModel.staffId,
            productId = productDetailsViewModel.productId
        ))
    }

    private fun setObserver() {
        productDetailsViewModel.productDetailsResponseLiveData.observe(viewLifecycleOwner) {
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
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        productDetailsViewModel.productAddResponseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    productDetailsViewModel.clearUpdateState()
                    showSuccessfullyUpdatePopup()
                    Log.d(mTag, it.toString())
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                    productDetailsViewModel.clearUpdateState()
                }
            }

        }
    }

    private fun gotoFoodList() {
        val action =
            ProductDetailsFragmentDirections.actionProductDetailsFragmentToFoodListFragment(
                serviceId =
                productDetailsViewModel.serviceId,
                staffId = productDetailsViewModel.staffId
            )
        findNavController().navigate(action)
    }

    private fun gotoCart(){
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.categoriseFragment, true)
            .setLaunchSingleTop(true)
            .build()
        val action = ProductDetailsFragmentDirections.actionGlobalCartFragment()
        findNavController().navigate(action,navOptions)
    }

    private fun updateView(data: ProductDetailsModel?) {
        data?.let {
            with(binding) {
                productDetailsViewModel.images = it.images
                tvServiceName.text = it.productName
                tvPrice.text = it.price
                pageSize = it.images.size
                productDetailsViewModel.inCart = it.inCart ?: 0
                btAddToCart.text = if (it.inCart == 1) {
                    binding.root.context.getString(R.string.go_to_cart)
                } else {
                    binding.root.context.getString(R.string.add_to_cart)
                }
                btSelectFood.visibility = if(it.inCart==1) View.VISIBLE else View.GONE

                //image slider
                imageSlideAdapter = ProductImageSlideAdapter(it.images, this@ProductDetailsFragment)
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
                        it.productDesc
                    )
                )
                detailsList.add(
                    Description(
                        binding.root.context.getString(R.string.terms_and_conditions_2),
                        it.productTerm
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

    private fun showSuccessfullyUpdatePopup() {
        context?.let {
            CustomPopup.showPopupMessageButtonClickDialog(it,
                null,
                it.getString(R.string.product_successfully_added_to_cart),
                it.getString(R.string.ok),
                false,
                object : DialogButtonClick {
                    override fun dialogButtonCallBack(dialog: Dialog) {
                        loadData()
                    }

                }
            )
        }

    }

    override fun onItemClick(position: Int, link: String?, imageView: ImageView) {
        val action = ServiceDetailsFragmentDirections.actionGlobalPhotoViewerFragment(
            images = productDetailsViewModel.images.map {
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
        if(this::productDetailsViewModel.isInitialized)
             loadData()

    }


}