package com.salonsolution.app.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CheckCartModel
import com.salonsolution.app.data.model.OrderSaveModel
import com.salonsolution.app.data.model.genericModel.Response
import com.salonsolution.app.data.network.ResponseMessageConverter
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.data.viewModel.OrderSummaryViewModel
import com.salonsolution.app.databinding.FragmentOrderSummaryBinding
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
class OrderSummaryFragment : BaseFragment(), DialogButtonClick {
    private lateinit var binding: FragmentOrderSummaryBinding
    private lateinit var orderSummaryViewModel: OrderSummaryViewModel
    private val navArgs: OrderSummaryFragmentArgs by navArgs()
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_summary, container, false)
        orderSummaryViewModel = ViewModelProvider(this)[OrderSummaryViewModel::class.java]
        binding.viewModel = orderSummaryViewModel
        binding.lifecycleOwner = viewLifecycleOwner
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
        getIntentData()
        initView()
        setObserver()
    }

    private fun getIntentData() {
        orderSummaryViewModel.cartData = navArgs.cartData
        orderSummaryViewModel.cartServiceList.value = navArgs.cartData.cartServiceList[0]
        orderSummaryViewModel.moreItem.value = navArgs.cartData.cartServiceList.size - 1
        orderSummaryViewModel.cartActualValue.value = navArgs.cartData.cartActualValue
        orderSummaryViewModel.cartTotal.value = navArgs.cartData.totalValue
        orderSummaryViewModel.grandTotal.value = navArgs.cartData.totalValue

    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)
        with(binding) {
            edCoupon.filters = arrayOf<InputFilter>(AllCaps())
        }
    }

    private fun setObserver() {
        orderSummaryViewModel.matchCouponResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    it.data?.response?.data?.let { couponModel ->
                        orderSummaryViewModel.isCouponApplied.value = true
                        orderSummaryViewModel.couponPrice.value = couponModel.couponApplied
                        orderSummaryViewModel.grandTotal.value = couponModel.newTotal
                    }

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d("tag",it.errorMessage.toString())
                    val msg = if(it.errorCode==400)
                        ResponseMessageConverter.getMatchCouponErrorMessage(binding.root.context,it.errorMessage)
                    else
                        it.errorMessage
                    handleApiError(it.isNetworkError,it.errorCode, msg)
                }
            }
        }
        orderSummaryViewModel.checkCartResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    checkStatus(it.data?.response)
                    Log.d("tag","CheckCart: ${it.data?.response?.data.toString()}")
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                }
            }
        }

        orderSummaryViewModel.orderSaveResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    gotoNextDestination(it.data?.response?.data)
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode,  binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        orderSummaryViewModel.errorResetObserver.observe(viewLifecycleOwner) {
            Log.d("tag", "changed.")
            //This blank observer is required for MediatorLiveData
            //MediatorLiveData observe other LiveData objects
        }

    }

    private fun gotoNextDestination(data: OrderSaveModel?) {
        data?.let {
            val action =
                OrderSummaryFragmentDirections.actionOrderSummaryFragmentToOrderConfirmationFragment(
                    it.orderId ?: "",
                    orderSummaryViewModel.cartTotal.value ?: "",
                    it.id ?: 0
                )
            findNavController().navigate(action)
        }

    }

    private fun checkStatus(response: Response<CheckCartModel>?) {
        // 1 -> All Good,
        // 2 -> service/product/food price updated,
        // 3 -> service/product/food status updated,
        // 4 -> service/product/food deleted,
        // 5 -> Customer deleted/inactive
        if (response?.data?.placeOrderStatus != null) {
            when (response.data?.placeOrderStatus) {
                1 -> {
                    orderSummaryViewModel.orderSave()
                }
                2 -> {
                    loadingPopup?.dismiss()
                    showPopup(binding.root.context.getString(R.string.price_changed))
//                    showPopup(ResponseMessageConverter.getCartPriceChangedMessage(binding.root.context,response.status?.msg))
                }
                3 -> {
                    loadingPopup?.dismiss()
                    showPopup(binding.root.context.getString(R.string.services_or_products_or_foods_are_unavailable))
//                    showPopup(ResponseMessageConverter.getCartStatusChangedMessage(binding.root.context,response.status?.msg))

                }
                4 -> {
                    loadingPopup?.dismiss()
                    showPopup(binding.root.context.getString(R.string.services_or_products_or_foods_are_unavailable))
//                    showPopup(ResponseMessageConverter.getCartItemDeletedMessage(binding.root.context,response.status?.msg))
                }
                5 -> {
                    loadingPopup?.dismiss()
                    userSharedPref.clearUserData()
                    showPopup(binding.root.context.getString(R.string.your_account_is_deactivate))
                }
                else -> {
                    loadingPopup?.dismiss()
                    errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        } else {
            errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.something_went_wrong))
            loadingPopup?.dismiss()
        }
    }

    private fun showPopup(msg: String) {
        CustomPopup.showPopupMessageButtonClickDialog(
            binding.root.context,
            null,
            msg,
            binding.root.context.getString(R.string.ok),
            false,
            this@OrderSummaryFragment
        )
    }

    override fun dialogButtonCallBack(dialog: Dialog) {
        if (userSharedPref.getUserToken().isNotEmpty())
            gotoCart()
        else
            binding.root.context.userLogout()
    }

    private fun gotoCart() {
        findNavController().popBackStack()
    }

}