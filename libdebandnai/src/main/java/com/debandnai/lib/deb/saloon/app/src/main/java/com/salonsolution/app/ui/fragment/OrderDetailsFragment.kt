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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.OrderDetailsListAdapter
import com.salonsolution.app.data.model.BuyAgainModel
import com.salonsolution.app.data.model.DetailsServiceList
import com.salonsolution.app.data.model.OrderDetailsModel
import com.salonsolution.app.data.model.genericModel.Response
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.OrderDetailsViewModel
import com.salonsolution.app.databinding.FragmentOrderDetailsBinding
import com.salonsolution.app.interfaces.DialogButtonClick
import com.salonsolution.app.interfaces.OrderDetailsListClickListener
import com.salonsolution.app.interfaces.PositiveNegativeCallBack
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment : BaseFragment(), OrderDetailsListClickListener {
    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var orderDetailsViewModel: OrderDetailsViewModel
    private lateinit var orderDetailsListAdapter: OrderDetailsListAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private val navArgs: OrderDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_details, container, false)
        orderDetailsViewModel = ViewModelProvider(this)[OrderDetailsViewModel::class.java]
        binding.viewModel = orderDetailsViewModel
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
        getIntentData()
        initView()
        setObserver()
    }

    private fun getIntentData() {
        orderDetailsViewModel.orderId = navArgs.orderId
        loadData()
    }

    private fun loadData() {
        orderDetailsViewModel.orderDetails(
            requestBodyHelper.getOrderDetailsRequest(
                orderDetailsViewModel.orderId
            )
        )
    }


    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)
        with(binding) {
            orderDetailsListAdapter =
                OrderDetailsListAdapter(this@OrderDetailsFragment)
            rvServiceList.run {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = orderDetailsListAdapter
            }

            ivCancelOrderClick.setOnClickListener {
                if (orderDetailsViewModel.isCancelable.value?.first == true) {
                    showOrderCancelPopup(orderDetailsViewModel.orderId)
                }
            }

            ivBuyAgainClick.setOnClickListener {
                orderDetailsViewModel.buyAgain(
                    requestBodyHelper.getBuyAgainRequest(
                        orderDetailsViewModel.orderId
                    )
                )
            }
        }
    }

    private fun setObserver() {
        orderDetailsViewModel.orderDetailsResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    setupData(it.data?.response?.data)
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(
                        it.isNetworkError,
                        it.errorCode,
                        binding.root.context.getString(R.string.something_went_wrong)
                    )
                }
            }
        }

        orderDetailsViewModel.cancelOrderResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.data?.response.toString())
                    showSuccessPopup(
                        binding.root.context.getString(R.string.order_cancelled_successfully),
                        false
                    )

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(
                        it.isNetworkError,
                        it.errorCode,
                        binding.root.context.getString(R.string.something_went_wrong)
                    )
                }
            }
        }
        orderDetailsViewModel.buyAgainResponseLiveData.observe(viewLifecycleOwner) {
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

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(
                        it.isNetworkError,
                        it.errorCode,
                        binding.root.context.getString(R.string.something_went_wrong)
                    )
                }
            }
        }

        orderDetailsViewModel.reviewAddResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.data?.response.toString())
                    showSuccessPopup(it.data?.response?.status?.msg, false)

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(
                        it.isNetworkError,
                        it.errorCode,
                        binding.root.context.getString(R.string.something_went_wrong)
                    )
                }
            }
        }
    }

    private fun manageStatus(data: Response<BuyAgainModel>?) {
        data?.let {
            when (it.data?.buyStatus) {
                1 -> {
                    Log.d("tag", "BuyAgainStatus: ${it.status?.msg}")
                    showSuccessPopup(
                        binding.root.context.getString(R.string.order_placed_successfully),
                        true
                    )
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

    private fun setupData(data: OrderDetailsModel?) {
        data?.let {
            orderDetailsViewModel.orderDetailsModel.value = it
            // canbeCancelled =1 : user can cancel the order, canbeCancelled = 0 : check order status to show order status
            orderDetailsViewModel.isCancelable.value = if (it.canBeCancelled == 0) {
                when (it.orderStatus) {
                    2 -> {
                        Pair(false, binding.root.context.getString(R.string.order_cancelled))
                    }
                    3 -> {
                        Pair(false, binding.root.context.getString(R.string.order_completed))
                    }
                    else -> {
                        Pair(true, binding.root.context.getString(R.string.cancel_order))
                    }
                }
            } else {
                Pair(true, binding.root.context.getString(R.string.cancel_order))
            }
            orderDetailsListAdapter.submitList(it.serviceList.map { list ->
                list.copy()
            })
            orderDetailsViewModel.isShowData.value = true
        }

    }

    override fun onReviewSubmitClick(
        position: Int,
        detailsServiceList: DetailsServiceList,
        reviewStar: Float,
        reviewComment: String
    ) {
        orderDetailsViewModel.reviewAdd(
            requestBodyHelper.getReviewAddRequest(
                orderDetailsViewModel.orderId,
                detailsServiceList.serviceId,
                detailsServiceList.staffId,
                reviewStar,
                reviewComment
            )
        )
    }

    private fun showOrderCancelPopup(id: Int?) {
        CustomPopup.showCancelOrderPopup(
            mContext = binding.root.context,
            isCancelable = true,
            positiveNegativeCallBack = object :
                PositiveNegativeCallBack {
                override fun onPositiveButtonClick() {
                    orderDetailsViewModel.orderCancel(requestBodyHelper.getOrderCancelRequest(id))
                }

                override fun onNegativeButtonClick() {

                }
            })
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
                    if (isCart)
                        gotoCartPage()
                    else
                        loadData()

                }

            }
        )
    }

    private fun gotoCartPage() {
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.orderDetailsFragment, true)
            .setLaunchSingleTop(true)
            .build()
        val action = OrderDetailsFragmentDirections.actionGlobalCartFragment()
        findNavController().navigate(action, navOptions)
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if (this::orderDetailsViewModel.isInitialized)
            loadData()
    }


}