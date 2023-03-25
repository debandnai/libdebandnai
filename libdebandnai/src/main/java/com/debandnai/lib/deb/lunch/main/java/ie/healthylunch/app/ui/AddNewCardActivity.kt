package ie.healthylunch.app.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.StripeIntent
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.AddNewCardRepository
import ie.healthylunch.app.data.viewModel.AddNewCardViewModel
import ie.healthylunch.app.databinding.ActivityAddNewCardBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.YES
import ie.healthylunch.app.utils.CustomDialog
import ie.healthylunch.app.utils.DialogOkListener
import ie.healthylunch.app.utils.MethodClass
import java.lang.Exception
import java.util.*

class AddNewCardActivity : BaseActivity<AddNewCardViewModel, AddNewCardRepository>() {
    lateinit var binding: ActivityAddNewCardBinding
    private lateinit var params: PaymentMethodCreateParams
    private var stripe: Stripe? = null

      

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_new_card)
        binding.viewModel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
        viewModel.activity = this
        viewModel.cardInputWidget = binding.cardInputWidget

        //get initiate Payment Intent Response
        viewModel.initiatePaymentIntentResponse()

        //get update Stripe Balance Response
        viewModel.updateStripeBalanceResponse()
    }

    override fun getViewModel() = AddNewCardViewModel::class.java

    override fun getRepository() =
        AddNewCardRepository(remoteDataSource.buildApi(ApiInterface::class.java))
    private fun getIntentValue() {
        //get data from intent
        if (intent.extras != null) {
            if (!intent.getStringExtra(Constants.FROM).isNullOrBlank())
                viewModel.from=intent.getStringExtra(Constants.FROM)
        }
    }

    fun startCheckout() {
        params = binding.cardInputWidget.paymentMethodCreateParams!!
        if (!Objects.equals(params, null)) {
            MethodClass.showProgressDialog(this)
            val confirmParams: ConfirmPaymentIntentParams = if (viewModel.saveCard == YES) {
                ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                    params, viewModel.clientSecret,
                    false, null, null, ConfirmPaymentIntentParams.SetupFutureUsage.OffSession
                )
            } else {
                ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                    params,
                    viewModel.clientSecret
                )
            }
            stripe = Stripe(applicationContext, viewModel.finalStripeKey)
            stripe?.confirmPayment(this, confirmParams)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("onActivityResult: ", "requestCode$requestCode")
        stripe?.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
              
            override fun onSuccess(result: PaymentIntentResult) {
                // If confirmation and authentication succeeded,
                // the PaymentIntent will have user actions resolved;
                // otherwise, handle the failure as appropriate
                // (e.g. the customer may need to choose a new payment
                // method)
                Log.e("result", result.toString())
                val paymentIntent: PaymentIntent = result.intent
                val status = paymentIntent.status
                if (status == StripeIntent.Status.Succeeded) {
                    viewModel.paymentMethod = paymentIntent.paymentMethodId.toString()
                    viewModel.transactionId = paymentIntent.id.toString()
                    viewModel.updateStripeBalanceApiCall()
                } else {
                    viewModel.initialStateOfCard()
                    var getErrorMsg: String? = "Payment failed, please try again later"
                    try {
                        if (paymentIntent.lastPaymentError != null) getErrorMsg =
                            paymentIntent.lastPaymentError!!.message
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    if (getErrorMsg == null) getErrorMsg = "Payment failed, please try again later"

                    if (status == StripeIntent.Status.RequiresPaymentMethod
                        && viewModel.saveCard == "yes" &&
                        paymentIntent.lastPaymentError?.code == "payment_intent_authentication_failure"
                    )
                        getErrorMsg =
                            "Your bank card does not allow auto top up. Please try again using manual top up"
                    CustomDialog.showOkTypeDialog(
                        this@AddNewCardActivity,
                        getErrorMsg,
                        object : DialogOkListener {
                            override fun okOnClick(dialog: Dialog, activity: Activity) {
                                dialog.dismiss()
                            }
                        })
                }
                MethodClass.hideProgressDialog(this@AddNewCardActivity)
            }

            override fun onError(e: Exception) {
                Log.e("onError: ", e.message!!)
                viewModel.initialStateOfCard()
                CustomDialog.showOkTypeDialog(
                    this@AddNewCardActivity,
                    e.message!!,
                    object : DialogOkListener {
                        override fun okOnClick(dialog: Dialog, activity: Activity) {
                            dialog.dismiss()
                        }
                    })
                MethodClass.hideProgressDialog(this@AddNewCardActivity)
            }


        })
    }


}