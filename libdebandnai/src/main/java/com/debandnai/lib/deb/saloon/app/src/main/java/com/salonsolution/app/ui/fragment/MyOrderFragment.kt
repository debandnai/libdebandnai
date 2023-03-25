package com.salonsolution.app.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.MyOrderListAdapter
import com.salonsolution.app.data.model.BuyAgainModel
import com.salonsolution.app.data.model.OrderList
import com.salonsolution.app.data.model.genericModel.Response
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.MyOrderViewModel
import com.salonsolution.app.databinding.FragmentMyOrderBinding
import com.salonsolution.app.interfaces.DialogButtonClick
import com.salonsolution.app.interfaces.MyOrderListClickListener
import com.salonsolution.app.interfaces.PositiveNegativeCallBack
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyOrderFragment : BaseFragment(), MyOrderListClickListener {
    private lateinit var binding: FragmentMyOrderBinding
    private lateinit var myOrderViewModel: MyOrderViewModel
    private lateinit var myOrderListAdapter: MyOrderListAdapter
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_order, container, false)
        myOrderViewModel = ViewModelProvider(this)[MyOrderViewModel::class.java]
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = true,
            isShowBottomNav = false,
            isShowToolbar = true
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
            myOrderListAdapter = MyOrderListAdapter(this@MyOrderFragment)
            rvOrderList.run {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = myOrderListAdapter
            }
        }
        loadData()
    }

    private fun loadData() {
        myOrderViewModel.orderList(requestBodyHelper.getMyOrderListRequest())
    }

    private fun setObserver() {
        myOrderViewModel.orderListResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    myOrderListAdapter.submitList(it.data?.response?.data?.orderList?.map { list ->
                        list.copy()
                    })
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        myOrderViewModel.cancelOrderResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.data?.response.toString())
                    showSuccessPopup(binding.root.context.getString(R.string.order_cancelled_successfully), false)
                    myOrderViewModel.clearUpdateState()

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                    myOrderViewModel.clearUpdateState()
                }
            }
        }
        myOrderViewModel.buyAgainResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    manageStatus(it.data?.response)
                    myOrderViewModel.clearUpdateState()

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                    myOrderViewModel.clearUpdateState()
                }
            }
        }

    }

    private fun manageStatus(data: Response<BuyAgainModel>?) {
        data?.let {
            when (it.data?.buyStatus) {
                1 -> {
                    Log.d("tag","BuyAgainStatus: ${it.status?.msg}")
                    showSuccessPopup(binding.root.context.getString(R.string.order_placed_successfully), true)
                }
                2 -> {
                    errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.services_or_products_or_foods_are_unavailable))
                }
                3 -> {
                    errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.staff_not_available))

                }
                else -> {
                    errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.something_went_wrong))
                }

            }
        }

    }
    private fun showSuccessPopup(description: String?, isCart: Boolean = false) {
        CustomPopup.showPopupMessageButtonClickDialog(
            mContext = binding.root.context,
            title = null,
            desc = description,
            buttonText = binding.root.context.getString(R.string.ok),
            false,
            object : DialogButtonClick {
                override fun dialogButtonCallBack(dialog: Dialog) {
                    if(isCart)
                        gotoCartPage()
                    else
                        loadData()
                }

            }
        )
    }


    private fun gotoCartPage() {
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.orderFragment, true)
            .setLaunchSingleTop(true)
            .build()
        val action = MyOrderFragmentDirections.actionGlobalCartFragment()
        findNavController().navigate(action,navOptions)
    }

    override fun onViewDetailsClick(position: Int, orderList: OrderList) {
        val action = MyOrderFragmentDirections.actionOrderFragmentToOrderDetailsFragment(orderList.id?:0)
        findNavController().navigate(action)
    }

    override fun onBuyItAgainClick(position: Int, orderList: OrderList) {
        myOrderViewModel.buyAgain(requestBodyHelper.getBuyAgainRequest(orderList.id))
    }

    override fun onCancelOrderClick(position: Int, orderList: OrderList) {
        showOrderCancelPopup(orderList.id)
    }

    private fun showOrderCancelPopup(id: Int?) {
        CustomPopup.showCancelOrderPopup(
            mContext = binding.root.context,
           isCancelable = true,
            positiveNegativeCallBack = object :
                PositiveNegativeCallBack {
                override fun onPositiveButtonClick() {
                    myOrderViewModel.orderCancel(requestBodyHelper.getOrderCancelRequest(id))
                }

                override fun onNegativeButtonClick() {

                }
            })
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if(this::myOrderViewModel.isInitialized)
            loadData()
    }


}