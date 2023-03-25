package com.salonsolution.app.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salonsolution.app.R
import com.salonsolution.app.adapter.CartFooterListAdapter
import com.salonsolution.app.adapter.CartListAdapter
import com.salonsolution.app.data.model.*
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.model.genericModel.Response
import com.salonsolution.app.data.network.ResponseMessageConverter
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.data.viewModel.CartViewModel
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.databinding.FragmentCartBinding
import com.salonsolution.app.interfaces.CartItemClickListener
import com.salonsolution.app.interfaces.DialogButtonClick
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.UtilsCommon.userLogout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CartFragment : BaseFragment(), CartItemClickListener, DialogButtonClick {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartListAdapter: CartListAdapter
    private lateinit var cartFooterListAdapter: CartFooterListAdapter
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper
    @Inject
    lateinit var userSharedPref: UserSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        binding.viewModel = cartViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = true,
            isShowBottomNav = false,
            isShowToolbar = true,
            isCartPage = false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setObserver()
    }


    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)
        with(binding) {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
            cartListAdapter = CartListAdapter( this@CartFragment)
            rvCartList.run {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = cartListAdapter
            }

            cartFooterListAdapter = CartFooterListAdapter()
            rvCartFooterList.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = cartFooterListAdapter
            }

            rvCartList.setOnScrollChangeListener { _, _, _, _, _ ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            bottomSheetLayout.setOnClickListener {
                bottomSheetBehavior.state =
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                        BottomSheetBehavior.STATE_COLLAPSED
                    else
                        BottomSheetBehavior.STATE_EXPANDED
            }
            btProceedToBuy.setOnClickListener {
                checkCart()
            }
        }

        loadData()
    }

    private fun loadData() {
        cartViewModel.cartList(requestBodyHelper.getCartListRequest())
    }


    private fun setObserver() {
        cartViewModel.cartListResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    manageCartData(it)
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                }
            }
        }

        cartViewModel.cartDeleteResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadData()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                }
            }


        }

        cartViewModel.foodUpdateResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadData()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                }
            }
        }

        cartViewModel.checkCartResponseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    Log.d("tag","CheckCart: ${it.data?.response?.data.toString()}")
                    checkStatus(it.data?.response)
                    cartViewModel.clearUpdateState()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                    cartViewModel.clearUpdateState()
                }
            }
        }
    }


    private fun manageCartData(it: ResponseState.Success<BaseResponse<CartListModel>>) {
        dashboardViewModel.cartCount.value = it.data?.response?.data?.cartServiceList?.size
        if((it.data?.response?.data?.cartServiceList?.size ?: 0) == 0){
            binding.tvEmptyCart.visibility = View.VISIBLE
            binding.rvCartList.visibility = View.GONE
            binding.bottomSheetLayout.visibility = View.GONE
            binding.btProceedToBuy.visibility = View.GONE
            (activity as? DashboardActivity)?.manageNavigationView(
                isToolbarMenuShow = false,
                isTopLevelDestination = true,
                isShowBottomNav = false,
                isShowToolbar = true,
                isCartPage = false
            )

        }else {
            binding.tvEmptyCart.visibility = View.GONE
            binding.rvCartList.visibility = View.VISIBLE
            binding.bottomSheetLayout.visibility = View.VISIBLE
            binding.btProceedToBuy.visibility = View.VISIBLE
            (activity as? DashboardActivity)?.manageNavigationView(
                isToolbarMenuShow = false,
                isTopLevelDestination = true,
                isShowBottomNav = false,
                isShowToolbar = true,
                isCartPage = true
            )
            binding.tvTotalCartValue.text = it.data?.response?.data?.totalValue
            dashboardViewModel.cartTotalValue.value = it.data?.response?.data?.totalValue
            cartViewModel.cartData = it.data?.response?.data
            cartListAdapter.submitList(it.data?.response?.data?.cartServiceList?.map { list ->
                list.copy()
            })
            cartFooterListAdapter.submitList(it.data?.response?.data?.footerBar?.map { list ->
                list.copy()
            })
        }

    }

    override fun onProductAddClick(position: Int, cartServiceList: CartServiceList) {
        if (cartServiceList.serviceId != null && cartServiceList.staffId != null) {
            val action = CartFragmentDirections.actionCartFragmentToProductListFragment(
                cartServiceList.serviceId!!,
                cartServiceList.staffId!!
            )
            findNavController().navigate(action)
        }

    }

    override fun onFoodAddClick(position: Int, cartServiceList: CartServiceList) {
        if (cartServiceList.serviceId != null && cartServiceList.staffId != null) {
            val action = CartFragmentDirections.actionCartFragmentToFoodListFragment(
                cartServiceList.serviceId!!,
                cartServiceList.staffId!!
            )
            findNavController().navigate(action)
        }
    }

    override fun onServiceDeleteClick(position: Int, cartServiceList: CartServiceList) {
        cartViewModel.cartDelete(
            requestBodyHelper.getCartDeleteRequest(
                "1",
                cartServiceList.serviceCartId
            )
        )
    }

    override fun onProductDeleteClick(
        position: Int,
        productListPosition: Int,
        productList: CartProductList,
        cartServiceList: CartServiceList
    ) {
        cartViewModel.cartDelete(
            requestBodyHelper.getCartDeleteRequest(
                "2",
                productList.productCartId
            )
        )
    }

    override fun onFoodDeleteClick(
        position: Int,
        foodListPosition: Int,
        foodList: CartFoodList,
        cartServiceList: CartServiceList
    ) {
        cartViewModel.cartDelete(requestBodyHelper.getCartDeleteRequest("3", foodList.foodCartId))
    }

    override fun onFoodUpdateClick(
        position: Int,
        foodListPosition: Int,
        foodList: CartFoodList,
        cartServiceList: CartServiceList
    ) {
        foodList.foodId?.let {
            cartViewModel.foodUpdate(
                requestBodyHelper.getFoodUpdateRequest(
                    cartServiceList.serviceId,
                    cartServiceList.staffId,
                    it
                )
            )
        }
    }

    override fun onFoodRemovedClick(
        position: Int,
        foodListPosition: Int,
        foodList: CartFoodList,
        cartServiceList: CartServiceList
    ) {
        foodList.foodId?.let {
            cartViewModel.foodUpdate(
                requestBodyHelper.getFoodRemovedRequest(
                    cartServiceList.serviceId,
                    cartServiceList.staffId,
                    it
                )
            )
        }
    }

    private fun checkCart() {
       cartViewModel.checkCart(requestBodyHelper.getCheckCartRequest())
    }


    private fun checkStatus(response: Response<CheckCartModel>?) {
        // 1 -> All Good,
        // 2 -> service/product/food price updated,
        // 3 -> service/product/food status updated,
        // 4 -> service/product/food deleted,
        // 5 -> Customer deleted/inactive
      if(response?.data?.placeOrderStatus!=null) {
          when (response.data?.placeOrderStatus) {
              1 -> {
                    gotoNextDestination()
              }
              2 -> {
                  showPopup(binding.root.context.getString(R.string.price_changed))
//                    showPopup(ResponseMessageConverter.getCartPriceChangedMessage(binding.root.context,response.status?.msg))
              }
              3 -> {
                  showPopup(binding.root.context.getString(R.string.services_or_products_or_foods_are_unavailable))
//                    showPopup(ResponseMessageConverter.getCartStatusChangedMessage(binding.root.context,response.status?.msg))
              }
              4 -> {
                  showPopup(binding.root.context.getString(R.string.services_or_products_or_foods_are_unavailable))
//                    showPopup(ResponseMessageConverter.getCartItemDeletedMessage(binding.root.context,response.status?.msg))
              }
              5 -> {
                userSharedPref.clearUserData()
                  showPopup(binding.root.context.getString(R.string.your_account_is_deactivate))
              }
              else -> {
                  errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.something_went_wrong))
              }
          }
      }else{
          errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.something_went_wrong))
      }
    }

    private fun gotoNextDestination() {
        cartViewModel.cartData?.let {
            val action = CartFragmentDirections.actionCartFragmentToOrderSummaryFragment(it)
            findNavController().navigate(action)
        }

    }

    private fun showPopup(msg:String){
        CustomPopup.showPopupMessageButtonClickDialog(binding.root.context,
            null,
            msg,
            binding.root.context.getString(R.string.ok),
            false,
            this@CartFragment
        )
    }
    override fun dialogButtonCallBack(dialog: Dialog) {
        if(userSharedPref.getUserToken().isNotEmpty())
            loadData()
        else
            binding.root.context.userLogout()
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if(this::cartViewModel.isInitialized)
         loadData()
    }

}