package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.PaginationLoadStateAdapter
import com.salonsolution.app.adapter.ProductListPagingAdapter
import com.salonsolution.app.data.model.ProductList
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.ProductListViewModel
import com.salonsolution.app.databinding.FragmentProductListBinding
import com.salonsolution.app.interfaces.ProductListClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
class ProductListFragment : BaseFragment(), ProductListClickListener {

    private lateinit var binding: FragmentProductListBinding
    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var productListPagingAdapter: ProductListPagingAdapter
    private val mTag = "tag"
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val navArgs: ProductListFragmentArgs by navArgs()
    // private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false)

        productListViewModel = ViewModelProvider(this)[ProductListViewModel::class.java]
        binding.viewModel = productListViewModel
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

        context?.let {
            with(binding) {

                productListPagingAdapter =
                    ProductListPagingAdapter( this@ProductListFragment)

                binding.rvServiceList.run {
                    setHasFixedSize(true)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = productListPagingAdapter.withLoadStateFooter(
                        footer = PaginationLoadStateAdapter { productListPagingAdapter.retry() }
                    )
                }

                ivSortBy.setOnClickListener {
                    val action =
                        ProductListFragmentDirections.actionGlobalServiceListSortDialogFragment(
                            productListViewModel.sortBy.value?.first ?: Constants.PRICE,
                            productListViewModel.sortBy.value?.second ?: 2

                        )
                    findNavController().navigate(action)
                }

                btGoToCart.setOnClickListener {
                    gotoCart()
                }
                btDontNeed.setOnClickListener {
                    doNotNeedProduct()
                }
                btSelectFood.setOnClickListener {
                    goToFoodList()
                }

                // loadData()

            }
        }

    }

    private fun setObserver() {

        productListViewModel.productListLiveData.observe(viewLifecycleOwner) {
            productListPagingAdapter.submitData(lifecycle, it)
        }
        productListViewModel.sortBy.observe(viewLifecycleOwner) {
            productListPagingAdapter.submitData(lifecycle, PagingData.empty())
            loadData()
        }

        productListViewModel.productAddResponseLiveData.observe(viewLifecycleOwner) {

            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    // loadingPopup?.dismiss()
//                    if(productListViewModel.selectedProduct!=null){
//
//                    }else{
                    productListPagingAdapter.refresh()
//                    }


                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        productListViewModel.doNotNeedResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    productListViewModel.clearUpdateState()
                    goToFoodList()
                }
                is ResponseState.Error -> {
                    productListViewModel.clearUpdateState()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun loadData() {
        productListViewModel.getProductList(
            requestBodyHelper.getProductListRequest(
                productListViewModel.sortBy.value?.first,
                productListViewModel.sortBy.value?.second,
                productListViewModel.serviceId,
                productListViewModel.staffId
            )
        )


    }

    private fun getIntentData() {
        productListViewModel.serviceId = navArgs.serviceId
        productListViewModel.staffId = navArgs.staffId
        loadData()
    }

    private fun handleLoadStateOfAdapter() {
        productListPagingAdapter.addLoadStateListener { loadStates ->

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
                (loadStates.append.endOfPaginationReached && productListPagingAdapter.itemCount == 0)
            Log.d("tag", "ServiceList ${loadStates.append.endOfPaginationReached}")
            Log.d("tag", "ServiceList itemCount: ${productListPagingAdapter.itemCount}")
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
                productListViewModel.sortBy.value = it
            }

    }

    override fun onItemClick(position: Int, productList: ProductList) {
        productList.id?.let {
            val action =
                ProductListFragmentDirections.actionProductListFragmentToProductDetailsFragment(
                    productId = it,
                    serviceId = productListViewModel.serviceId,
                    staffId = productListViewModel.staffId
                )
            findNavController().navigate(action)
        }
    }

    override fun onAddClick(position: Int, productList: ProductList) {
        productList.id?.let {
            productListViewModel.selectedProduct = productList
            productListViewModel.addProduct(
                requestBodyHelper.getProductAddRequest(
                    productListViewModel.serviceId,
                    productListViewModel.staffId,
                    it
                )
            )
        }
    }

    override fun goToCartClick(position: Int, productList: ProductList) {
        gotoCart()
    }

    private fun goToFoodList() {
        val action = ProductListFragmentDirections.actionProductListFragmentToFoodListFragment(
            serviceId = productListViewModel.serviceId,
            staffId = productListViewModel.staffId
        )
        findNavController().navigate(action)
    }

    private fun gotoCart() {
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.categoriseFragment, true)
            .setLaunchSingleTop(true)
            .build()
        val action = ProductListFragmentDirections.actionGlobalCartFragment()
        findNavController().navigate(action,navOptions)
    }


    private fun doNotNeedProduct() {
//        "type":"1/2/" //1-> Product, 2 -> Food
        productListViewModel.doNotNeed(
            requestBodyHelper.getDoNotNeedRequest(
                "1",
                productListViewModel.serviceId,
                productListViewModel.staffId
            )
        )
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if(this::productListViewModel.isInitialized)
            loadData()
    }

}