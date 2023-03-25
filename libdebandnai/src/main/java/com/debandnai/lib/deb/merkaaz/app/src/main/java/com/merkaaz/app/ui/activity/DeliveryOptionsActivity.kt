package com.merkaaz.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.data.model.DeliveryOptionsModel
import com.merkaaz.app.data.viewModel.DeliveryOptionsViewModel
import com.merkaaz.app.databinding.ActivityDeliveryOptionsBinding
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeliveryOptionsActivity : BaseActivity() {

    private lateinit var binding: ActivityDeliveryOptionsBinding
    private lateinit var deliveryOptionsViewModel: DeliveryOptionsViewModel
    private  var loader: Dialog ?=null


    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_options)

        deliveryOptionsViewModel = ViewModelProvider(this)[DeliveryOptionsViewModel::class.java]
        binding.viewModel = deliveryOptionsViewModel
        binding.activity = this
        binding.lifecycleOwner = this
        initialise()
        setLanguageCode()
        viewOnClick()
        observe_data()
    }

    private fun viewOnClick() {
        binding.btnProceedToPay.setOnClickListener {
            var delivery_amnt = "0.0 AOA"
            if (deliveryOptionsViewModel.deliveryMethod.value == Constants.deliver)
                delivery_amnt = deliveryOptionsViewModel.delivery_options.value?.deliveryCharge.toString()

            startActivity(
                Intent(this,PaymentActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra(Constants.DELIVERYMETHOD, deliveryOptionsViewModel.deliveryMethod.value)
                    .putExtra(Constants.TOTALAMOUNT, deliveryOptionsViewModel.delivery_options.value?.total?:"0.00")
                    .putExtra(Constants.DELIVERYAMOUNT, delivery_amnt)
                    .putExtra(Constants.GRANDTOTAL, deliveryOptionsViewModel.grandTotal.value)
            )
            finish()
        }


        //Default delivery Method 1 for pickup
        deliveryOptionsViewModel.deliveryMethod.value = Constants.pickup
        deliveryOptionsViewModel.getDeliveryOptions()

        set_data(Constants.pickup)

        binding.cardViewPickupLocation.setOnClickListener {
            //Delivery Method 1 for pickup
            deliveryOptionsViewModel.deliveryMethod.value = Constants.pickup
//            deliveryOptionsViewModel.getDeliveryOptions()

            set_data(Constants.pickup)
        }
        binding.cardViewDeliveryTo.setOnClickListener {
            //Delivery Method 1 for shipping
            deliveryOptionsViewModel.deliveryMethod.value = Constants.deliver
//            deliveryOptionsViewModel.getDeliveryOptions()
            set_data(Constants.deliver)
        }

        binding.imgHelp.setOnClickListener {
            startActivity(Intent(this@DeliveryOptionsActivity, CustomerServiceActivity::class.java))
        }

    }

    private fun set_data(stat: Int) {
        if (stat == Constants.pickup){
            binding.imgPickupCircle.setBackgroundResource(R.drawable.ic_selection)
            binding.imgDeliverCircle.setBackgroundResource(R.drawable.ic_circle)
            binding.clPickupLocation.setBackgroundResource(R.drawable.card_edge)
            binding.clDeliverLocation.setBackgroundResource(android.R.color.transparent)
            binding.tvPickupAddressTitle.isSelected = true
            binding.tvDeliveryAddressTitle.isSelected = false
            deliveryOptionsViewModel.delivery_options.value?.total?.let {total->
                deliveryOptionsViewModel.grandTotal.value = total
            }

        }else if (stat == Constants.deliver){
            binding.imgPickupCircle.setBackgroundResource(R.drawable.ic_circle)
            binding.imgDeliverCircle.setBackgroundResource(R.drawable.ic_selection)
            binding.clPickupLocation.setBackgroundResource(android.R.color.transparent)
            binding.clDeliverLocation.setBackgroundResource(R.drawable.card_edge)
            binding.tvPickupAddressTitle.isSelected = false
            binding.tvDeliveryAddressTitle.isSelected = true
            deliveryOptionsViewModel.delivery_options.value?.grandTotal?.let {grandTotal->
                deliveryOptionsViewModel.grandTotal.value = grandTotal
            }

        }
    }


    private fun initialise() {
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))
    }

    private fun setLanguageCode() {
        if (sharedPreff.getlanguage_code().equals(Constants.ENGLISH_CODE,true)) {
            binding.imgTimeIcon1.setBackgroundResource(R.drawable.ic_time_english)
            binding.imgDeliveryTime.setBackgroundResource(R.drawable.ic_delivery_msg_icon)
        } else {
            binding.imgTimeIcon1.setBackgroundResource(R.drawable.ic_time_pt)
            binding.imgDeliveryTime.setBackgroundResource(R.drawable.ic_delivery_msg_icon_pt)
        }
    }


    //For Delivery Option
    private fun observe_data() {
        deliveryOptionsViewModel.deliveryOptionsData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val deliveryOptionsdata = data.response?.data
                            val gson = Gson()
                            val deliveryOptionsType =
                                object : TypeToken<DeliveryOptionsModel>() {}.type
                            val deliveryOptionsModel : DeliveryOptionsModel =gson.fromJson(deliveryOptionsdata, deliveryOptionsType)
                            deliveryOptionsViewModel.delivery_options.value = deliveryOptionsModel
                            deliveryOptionsViewModel.grandTotal.value =deliveryOptionsModel?.total
                            deliveryOptionsModel?.deliveryCharge?.let { dlvry_chrg ->
                                    binding.tvDeliveryFee.text = binding.root.context.resources.getString(R.string.delivery_fee)+dlvry_chrg
                            }

                        }
                    }
                    }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
            }
        }
    }

}