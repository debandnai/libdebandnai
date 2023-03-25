package com.merkaaz.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.model.CartProductAvailabilityCheckModel
import com.merkaaz.app.data.model.SaveOrderModel
import com.merkaaz.app.data.viewModel.PaymentViewModel
import com.merkaaz.app.databinding.ActivityPaymentBinding
import com.merkaaz.app.databinding.CustomDialogWithTwoButtonsBinding
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class PaymentActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var paymentViewModel: PaymentViewModel
    private var loader: Dialog? = null

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)

        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]
        binding.viewModel = paymentViewModel
        binding.activity = this
        binding.lifecycleOwner = this
        init()
        viewOnClickListener()
        observeData()
    }

    fun init() {
        //initialize loader
        binding.root.context?.let {
            loader = MethodClass.custom_loader(it, getString(R.string.please_wait))
            if (intent.extras != null) {
                paymentViewModel.deliveryMethod.value = intent.getIntExtra(Constants.DELIVERYMETHOD,0)
                paymentViewModel.totalAmt.value = intent.getStringExtra(Constants.TOTALAMOUNT)
                paymentViewModel.deliveryAmt.value = intent.getStringExtra(Constants.DELIVERYAMOUNT)
                paymentViewModel.grandTotal.value = intent.getStringExtra(Constants.GRANDTOTAL)

            }
            //  paymentViewModel.getPaymentDetails()
        }
    }

    private fun viewOnClickListener() {
        binding.tvEditDeliveryCharge.setOnClickListener {
            delivery_options()
        }
        binding.tvEditTotalItems.setOnClickListener {
            finish()
        }
        binding.imgBackBtn.setOnClickListener{
            delivery_options()
        }
        binding.imgHelp.setOnClickListener {
            startActivity(Intent(this@PaymentActivity, CustomerServiceActivity::class.java))
        }


//        binding.rbOnlinePayment.setOnClickListener {
//            MethodClass.custom_msg_dialog_callback(
//                this, resources.getString(R.string.pay_by_Card_machine),
//                object : DialogCallback {
//                    override fun dialog_clickstate() {}
//                },
//            )
//        }

        //For default Pay By Card Machine selection function
        setDefaultPayByCardMachine()

        //On click  Pay By Card Machine selection function
        binding.rbPayByCardMachine.setOnClickListener {
            setDefaultPayByCardMachine()
        }
        binding.rbPayByCash.setOnClickListener {
            setPayByCash()
        }
        binding.btnProceedToPay.setOnClickListener{
            val payment_rg_id: Int = binding.rgPaymentOptions.checkedRadioButtonId
            if (payment_rg_id == -1){
                MethodClass.custom_msg_dialog(this,resources.getString(R.string.select_payment_options))?.show()
            }else{

                val radioButton = findViewById<View>(payment_rg_id) as RadioButton
                val rd_btn_indx: Int = binding.rgPaymentOptions.indexOfChild(radioButton)
                if (rd_btn_indx == 0){
                    MethodClass.custom_msg_dialog(this,resources.getString(R.string.pay_by_Card_machine))?.show()
                }else /*if (rd_btn_indx == 1){*/
                    paymentViewModel.cartProductAvailabilityCheck()
                //}

            }




//            paymentViewModel.cartProductAvailabilityCheck()

        }

    }

    private fun setPayByCash() {
        paymentViewModel.paymentMethod.value = "1"
        paymentViewModel.paymentStatus.value = "3"
    }

    private fun delivery_options() {
        startActivity(Intent(this@PaymentActivity, DeliveryOptionsActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    private fun setDefaultPayByCardMachine() {
        //If payment Method Pay By Card Machine paymentMethod status 1
        paymentViewModel.paymentMethod.value = "1"
        paymentViewModel.paymentStatus.value = "2"

        //This code need remove after setup multiple payment options
       // binding.rbPayByCardMachine.isChecked=true

//        binding.rbPayByCardMachine.setCompoundDrawablesWithIntrinsicBounds(
//            ContextCompat.getDrawable(
//                this,
//                R.drawable.ic_card_machine_icon
//            ), null, ContextCompat.getDrawable(this, R.drawable.ic_payment_selection_icon), null
//        )
//        binding.rbOnlinePayment.setCompoundDrawablesWithIntrinsicBounds(
//            ContextCompat.getDrawable(
//                this,
//                R.drawable.ic_online_pay_icon
//            ), null, ContextCompat.getDrawable(this, R.drawable.ic_circle_payment_options), null
//        )
    }

    private fun observeData() {
        paymentViewModel.cartProductAvailabilityCheckLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
//                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            val cartProductAvailabilityCheckResponse = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<CartProductAvailabilityCheckModel>() {}.type
                            val cartProductAvailabilityCheckModel: CartProductAvailabilityCheckModel? =
                                gson.fromJson(cartProductAvailabilityCheckResponse, dataType)
                            Log.d(TAG, "observeData: test "+cartProductAvailabilityCheckModel?.placeStatus )

                            if (cartProductAvailabilityCheckModel?.placeStatus == 4)
                                paymentViewModel.deliveryMethod.value?.let { value ->
                                    paymentViewModel.getMoveonPrice(value)
                                }
                            else {
                                paymentViewModel.deliveryMethod.value?.let { value ->
                                    other_status_cart(cartProductAvailabilityCheckModel,value)
                                }

                            }

                        } else {
                                MethodClass.custom_msg_dialog(this, data.response?.status?.msg)

                        }

                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                        MethodClass.custom_msg_dialog(this, it.errorMessage)?.show()
                }
            }
        }

        paymentViewModel.moveOnPrice.observe(this) {
            when (it) {
                is Response.Loading -> {
                    if (!loader?.isShowing!!)
                        loader?.show()
                }
                is Response.Success -> {
//                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            /*val jsonobj  = JSONObject(data.response!!.status.toString())
                            val priceCheck = jsonobj.getString("action_status")
                            if (priceCheck.equals("true",ignoreCase = true)) {
                                paymentViewModel.save_data_afterpayment(
                                    Constants.ONLINE,
                                    Constants.PAYMENT_STATUS_COMPLETED,
                                    paymentViewModel.deliveryMethod.value
                                )
                            }
                            else{
                                MethodClass.showErrorMsg(this@PaymentActivity,0,jsonobj.getString("msg"))
                            }*/
                           /*
                                    {
                                    "lang_name":"1",
                                    "payment_method":1/2/3, // 1 -> Online Payment; 2 -> Pay by card machine; 3 -> Pay by cash;
                                    "payment_status":1/2/3, // 1 -> Completed; 2 -> Pending; 3 -> Declined;
                                    "delivery_method":1/2, // 1 -> Pick Up; 2 -> Shipping;
                                    "appversion": "1.2.4.1",
                                    "device_os": "and"
                                    }
                            */

                            paymentViewModel.paymentStatus.value?.let { paymentStatus ->
                                paymentViewModel.save_data_afterpayment(
                                    paymentStatus,
                                    Constants.PAYMENT_STATUS_COMPLETED,
                                    paymentViewModel.deliveryMethod.value
                                )
                            }
                        }
                        else{
                            commonFunctions.showErrorMsg(this@PaymentActivity,0,data.response?.status?.msg)
                        }
                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
                else -> {}
            }

        }

        paymentViewModel.saveResponse.observe(this) {
            when (it) {
                is Response.Loading -> {
                    if (!loader?.isShowing!!)
                        loader?.show()
                }
                is Response.Success -> {
                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            val saveOrderResponse = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<SaveOrderModel>() {}.type
                            val saveOrderModel: SaveOrderModel? =
                                gson.fromJson(saveOrderResponse, dataType)
                            startActivity(
                                Intent(
                                    this@PaymentActivity,
                                    OrderPlacedActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            )
                            finish()
                        }
                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
                else -> {}
            }

        }
    }

    private fun check_price(price: String) {
        var dialog: Dialog? = null
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding: CustomDialogWithTwoButtonsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.custom_dialog_with_two_buttons,
            null,
            false
        )
        dialog.setContentView(binding.root)

        var msg =resources.getString(R.string.price_change_1)+paymentViewModel.grandTotal.value+resources.getString(R.string.to)+
                price+resources.getString(R.string.price_change_2)

        binding.tvTitle.text = resources.getString(R.string.amount_changed)

        binding.tvMsg.text = msg
        binding.btnYes.text = resources.getString(R.string.proceed)
        binding.btnNo.text = resources.getString(R.string.check_cart)

        binding.btnYes.setOnClickListener {
        paymentViewModel.paymentStatus.value?.let { paymentStatus ->
            paymentViewModel.save_data_afterpayment(
                paymentStatus,Constants.PAYMENT_STATUS_COMPLETED,paymentViewModel.deliveryMethod.value)
        }
        dialog.dismiss()
        }
        binding.btnNo.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun other_status_cart(chk_prod_stat: CartProductAvailabilityCheckModel?, value: Int) {
        binding.root.context?.let {ctx->
            when (chk_prod_stat?.placeStatus) {
                1 -> {
                    val msg = chk_prod_stat.unavailableProducts +resources.getString(R.string.products_unavialable)
                    MethodClass.custom_msg_dialog_callback(ctx,msg,object :DialogCallback{
                        override fun dialog_clickstate() {
                            finish()
                        }
                    })
                }
                2 -> {
                    show_dialog_for_payment_increase(chk_prod_stat,value)
                }
                3 -> {
                    val msg = resources.getString(R.string.min_order_msg)+ chk_prod_stat.minOrderValue +resources.getString(R.string.add_more_msg)
                    MethodClass.custom_msg_dialog_callback(ctx,msg,object : DialogCallback{
                        override fun dialog_clickstate() {
                            finish()
                        }
                    })
                }
                else -> {
                    finish()
                }
            }
        }

    }

    private fun show_dialog_for_payment_increase(
        prod_stat: CartProductAvailabilityCheckModel,
        value: Int
    ) {
        var dialog: Dialog? = null
            dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: CustomDialogWithTwoButtonsBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.custom_dialog_with_two_buttons,
                null,
                false
            )
        dialog.setContentView(binding.root)

            var msg =resources.getString(R.string.cart_amount_chng_msg)+" "+ prod_stat.oldCartTotal+" " +resources.getString(R.string.to)+" "+
                    prod_stat.newCartTotal +" "+resources.getString(R.string.proceed_msg)

            binding.tvTitle.text = resources.getString(R.string.amount_changed)

            binding.tvMsg.text = msg
            binding.btnYes.text = resources.getString(R.string.proceed)
            binding.btnNo.text = resources.getString(R.string.check_cart)

            binding.btnYes.setOnClickListener {
                paymentViewModel.getMoveonPrice(value)
                //paymentViewModel.save_data_afterpayment(Constants.ONLINE,Constants.PAYMENT_STATUS_COMPLETED,paymentViewModel.deliveryMethod.value)
                dialog.dismiss()

            }
            binding.btnNo.setOnClickListener {
                dialog.dismiss()
                finish()
            }
        dialog.show()
        }


}