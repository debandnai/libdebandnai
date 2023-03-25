package com.merkaaz.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.OrderDetailsProductAdapter
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.model.OrderList
import com.merkaaz.app.data.model.ProductList
import com.merkaaz.app.data.model.ReOrderModel
import com.merkaaz.app.data.viewModel.OrderDetailsViewModel
import com.merkaaz.app.databinding.ActivityOrderDetailsBinding
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.DEFAULT_RECYCLE_VIEW_HEIGHT
import com.merkaaz.app.utils.Constants.PICK_UP
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class OrderDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var orderDetailsViewModel: OrderDetailsViewModel
    var productList: ArrayList<ProductList> = ArrayList()
    private var orderList: OrderList? = null
    private var loader: Dialog? = null
    private var orderDetailsProductAdapter: OrderDetailsProductAdapter? = null

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details)

        orderDetailsViewModel = ViewModelProvider(this)[OrderDetailsViewModel::class.java]
        binding.viewModel = orderDetailsViewModel
        binding.activity = this
        binding.lifecycleOwner = this
        initialise()
        setIntentValue()
        onViewClick()
        observeData()
    }

    private fun initialise() {
        //initialize loader
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))
    }


    private fun onViewClick() {
        binding.btnReorder.setOnClickListener {
            orderDetailsViewModel.reOrder()
        }
        binding.imgHelp.setOnClickListener {
            startActivity(Intent(this@OrderDetailsActivity, CustomerServiceActivity::class.java))
        }
    }


    private fun setIntentValue() {
        if (intent.extras != null) {
            orderList = intent.getSerializableExtra(Constants.ORDER_ID) as? OrderList
            Log.d(TAG, "setIntentValue: orderId  " + orderList?.orderId)
            setData()
        }
    }

    private fun setData() {
        orderList?.let {
            orderDetailsViewModel.waitingForPickup.value = it.orderStatus
            orderDetailsViewModel.orderId.value = it.orderId
            orderDetailsViewModel.id.value = it.id.toString()
            orderDetailsViewModel.orderDate.value = it.orderDate
            orderDetailsViewModel.orderValue.value = it.orderValue
            orderDetailsViewModel.deliveryCharge.value = it.deliveryCharge
            orderDetailsViewModel.totalAmt.value = it.totalAmount
            orderDetailsViewModel.total.value = it.totalAmount

            orderDetailsViewModel.paymentStatus.value = it.paymentStatus
            orderDetailsViewModel.paymentDate.value = it.paymentDate
            if (Objects.equals(it.shippingType, PICK_UP)) {
                orderDetailsViewModel.deliveryAddress.value = it.pickupAddress
                orderDetailsViewModel.shippingType.value = getString(R.string.pickup_address)

            } else {
                orderDetailsViewModel.deliveryAddress.value = it.deliveryAddress
                orderDetailsViewModel.shippingType.value = getString(R.string.delivery_address)
            }
            productList = it.productList
            adapterSetup()
            setRecycleViewHeight(DEFAULT_RECYCLE_VIEW_HEIGHT)
        }
    }

    private fun adapterSetup() {
        orderDetailsProductAdapter = OrderDetailsProductAdapter(productList, this)
        binding.rvProduct.adapter = orderDetailsProductAdapter
        binding.rvProduct.isFocusable = false
    }

    fun setRecycleViewHeight(itemHeight: Int) {
        Log.d(TAG, "setRecycleViewHeight: $itemHeight")
        if (productList.size > 10) {
            val params: ViewGroup.LayoutParams = binding.rvProduct.layoutParams
            params.height = (itemHeight * 10)
            binding.rvProduct.layoutParams = params
        }
    }


    private fun observeData() {
        //For Update Location
        orderDetailsViewModel.reOrderLivedata.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            Log.d(TAG, "observeData: response  " + data)
                            val reorderResponse = data.response?.data
                            val gson = Gson()
                            val reOrderType = object : TypeToken<ReOrderModel>() {}.type
                            val reOrderModel: ReOrderModel? =
                                gson.fromJson(reorderResponse, reOrderType)
                            Log.d(TAG, "observeData: test  " + reOrderModel?.placeStatus)
                            if (reOrderModel?.placeStatus == 4) {
                                startActivity(Intent(this, DashBoardActivity::class.java).apply {
                                    putExtra(Constants.FROM, Constants.ORDER_DETAILS)
                                })
                            } else {

                                reOrderModel?.unavailableProducts?.let { it1 ->
                                    MethodClass.custom_msg_dialog_reorder_callback(this,
                                        reOrderModel?.placeStatus,
                                        reOrderModel?.newCartTotal,
                                        reOrderModel?.oldCartTotal,
                                        it1,
                                        reOrderModel?.minOrderValue,
                                        object : DialogCallback {
                                            override fun dialog_clickstate() {
                                                startActivity(
                                                    Intent(
                                                        this@OrderDetailsActivity,
                                                        DashBoardActivity::class.java
                                                    ).apply {
                                                        putExtra(
                                                            Constants.FROM,
                                                            Constants.ORDER_DETAILS
                                                        )
                                                    })
                                            }
                                        })
                                }

                            }


                            /*when (reOrderModel?.placeStatus) {
                                1 -> {
                                    val msg = reOrderModel?.unavailableProducts+resources.getString(R.string.products_unavialable)
                                    MethodClass.custom_msg_dialog_callback(ctx,msg,object : DialogCallback {
                                        override fun dialog_clickstate() {
                                           // cartViewModel.getCartList()
                                        }
                                    })
                                }
                                2 -> {
                                    show_dialog_for_payment_increase(chk_prod_stat)
                                }
                                3 -> {
                                    val msg = resources.getString(R.string.min_order_msg)+chk_prod_stat?.minOrderValue+resources.getString(R.string.add_more_msg)
                                    MethodClass.custom_msg_dialog_callback(ctx,msg,object : DialogCallback{
                                        override fun dialog_clickstate() {
                                            cartViewModel.getCartList()
                                        }
                                    })
                                }
                                else -> {
                                    cartViewModel.getCartList()
                                }
                            }*/

                            Log.d(TAG, "observeData: aaaa " + data.response?.status?.msg)


                        }
                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(this, it.errorCode, it.errorMessage)
                }
            }
        }
    }


    override fun onBackPressed() {

        //isTaskRoot which return true if current activity is only activity in your stack
        if (isTaskRoot) {
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        } else
            super.onBackPressed()
    }
}