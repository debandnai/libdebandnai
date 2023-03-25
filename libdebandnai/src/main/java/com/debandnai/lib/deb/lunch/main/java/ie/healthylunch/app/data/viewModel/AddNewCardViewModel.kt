package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.stripe.android.PaymentConfiguration
import com.stripe.android.view.CardInputWidget
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.PriceAdapter
import ie.healthylunch.app.data.model.initiatePaymentModel.InitiatePaymentResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.updateStripeBalanceModel.UpdateStripeBalanceResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.AddNewCardRepository
import ie.healthylunch.app.ui.AddNewCardActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants.Companion.DASHBOARD_ID
import ie.healthylunch.app.utils.MethodClass.keyDecryptionMethod
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class AddNewCardViewModel(
    private val repository: AddNewCardRepository
) : ViewModel(), AdapterItemOnclickListener {

    lateinit var activity: AddNewCardActivity
    var dropDownListVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var topUpErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var cardErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var topUpError: MutableLiveData<String> = MutableLiveData("")
    var cardError: MutableLiveData<String> = MutableLiveData("")
    var chooseAmountText: MutableLiveData<String> = MutableLiveData("Choose Top Up Amount")
    var saveCard = "no"
    private var myPrice = ""
    var cardNumber = ""
    var expDate = ""
    var cvc = ""
    var finalStripeKey = ""
    var clientSecret = ""
    var cardInvalid = true
    var expDateInvalid = true
    var cvcInvalid = true
    var paymentMethod = ""
    var transactionId = ""
    @SuppressLint("StaticFieldLeak")
    lateinit var cardInputWidget: CardInputWidget
    private lateinit var priceAdapter: PriceAdapter
    @SuppressLint("StaticFieldLeak")
    lateinit var recyclerView: RecyclerView
    private var priceList: MutableList<PriceItem> = ArrayList()
    var from:String?=null
    //initiate payment
    private val _initiatePaymentResponse: SingleLiveEvent<Resource<InitiatePaymentResponse>?> =
        SingleLiveEvent()
    private var initiatePaymentResponse: SingleLiveEvent<Resource<InitiatePaymentResponse>?>? = null
        get() = _initiatePaymentResponse

    private fun initiatePaymentIntent(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _initiatePaymentResponse.value = repository.initiatePaymentIntent(jsonObject, token)
    }

    //initiate Payment Intent api call
    fun initiatePaymentIntentApiCall() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("amount", myPrice)
        jsonObject.addProperty("save_card", saveCard)
        MethodClass.showProgressDialog(activity)
        initiatePaymentIntent(jsonObject, Constants.TOKEN)


    }

    //initiate PaymentIntent Response
      
    fun initiatePaymentIntentResponse() {
        initiatePaymentResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)

                    finalStripeKey =
                        keyDecryptionMethod(it.value.response.raws.data.stripeKey)
                    clientSecret = it.value.response.raws.data.clientSecret

                    PaymentConfiguration.init(
                        activity,
                        finalStripeKey
                    )

                    activity.startCheckout()
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->


                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        activity, null,
                                        Constants.INITIATE_PAYMENT,
                                        Constants.REFRESH_TOKEN
                                    )
                            else
                                it.errorString.let { it1 ->

                                    MethodClass.showErrorDialog(
                                        activity,
                                        it1,
                                        it.errorCode
                                    )
                                }
                        }


                    }
                    MethodClass.hideProgressDialog(activity)
                    _initiatePaymentResponse.value = null
                    initiatePaymentResponse = _initiatePaymentResponse

                }


                else -> {}
            }

        }
    }


    //update stripe balance
    private val _updateStripeBalanceResponse: SingleLiveEvent<Resource<UpdateStripeBalanceResponse>?> =
        SingleLiveEvent()
    private var updateStripeBalanceResponse: SingleLiveEvent<Resource<UpdateStripeBalanceResponse>?>? =
        null
        get() = _updateStripeBalanceResponse

    private fun updateStripeBalance(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _updateStripeBalanceResponse.value = repository.updateStripeBalance(jsonObject, token)
    }


    //initiate Payment Intent api call
    fun updateStripeBalanceApiCall() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()


        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("amount", myPrice)
        jsonObject.addProperty("transaction_id", transactionId)
        if (Objects.equals(saveCard, "yes"))
            jsonObject.addProperty("payment_method", paymentMethod)
       jsonObject.addProperty("order_dashboard_id", DASHBOARD_ID)

        MethodClass.showProgressDialog(activity)
        updateStripeBalance(jsonObject, Constants.TOKEN)

    }

    //initiate PaymentIntent Response
      
    fun updateStripeBalanceResponse() {
        updateStripeBalanceResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    CustomDialog.showOkTypeDialog(
                        activity,
                        it.value.response.raws.successMessage,
                        object : DialogOkListener {
                            override fun okOnClick(dialog: Dialog, activity: Activity) {
                                dialog.dismiss()
                                activity.setResult(Activity.RESULT_OK, activity.intent)
                                activity.finish()
                            }
                        })
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->


                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        activity, null,
                                        Constants.UPDATE_STRIPE_BALANCE,
                                        Constants.REFRESH_TOKEN
                                    )
                            else
                                it.errorString.let { it1 ->

                                    MethodClass.showErrorDialog(
                                        activity,
                                        it1,
                                        it.errorCode
                                    )
                                }
                        }


                    }
                    MethodClass.hideProgressDialog(activity)
                    _updateStripeBalanceResponse.value = null
                    updateStripeBalanceResponse = _updateStripeBalanceResponse

                }


                else -> {}
            }

        }
    }


    //click function
    fun dropDownShowClick() {

        dropDownListVisible.value = !dropDownListVisible.value!!
    }

      
    fun automaticTopUpClick(activity: Activity) {
        saveCard = "yes"
        if (cardValidation(activity))
            initiatePaymentIntentApiCall()

    }

      
    fun manualTopClick(activity: Activity) {
        saveCard = "no"
        if (cardValidation(activity))
            initiatePaymentIntentApiCall()
    }


    //set Recyclerview initially
    fun setRecyclerView(view: RecyclerView, activity: Activity) {
        recyclerView = view

        //set array list initially
        priceListInitialDataSet()

        view.apply {
            priceAdapter = PriceAdapter(priceList, this@AddNewCardViewModel)
            view.adapter = priceAdapter
            view.isFocusable = false

        }
    }


    //validation
    private fun cardValidation(activity: Activity): Boolean {

        invisibleErrorTexts()

        if (Objects.equals(myPrice, "")) {
            topUpError.value = activity.getString(R.string.please_select_top_up_amount)
            topUpErrorVisible.value = true
            return false
        }
        if (Objects.equals(cardNumber, "")) {
            cardError.value = activity.getString(R.string.please_enter_card_number)
            cardErrorVisible.value = true
            return false
        }
        if (cardInvalid) {
            cardError.value = activity.getString(R.string.please_enter_valid_card_number)
            cardErrorVisible.value = true
            return false
        }
        if (Objects.equals(expDate, "")) {
            cardError.value = activity.getString(R.string.please_enter_expiry_date)
            cardErrorVisible.value = true
            return false
        }
        if (!expDate.contains("/") && expDate.length < 4) {
            cardError.value = activity.getString(R.string.please_enter_expiry_date)
            cardErrorVisible.value = true
            return false
        }
        if (expDate.contains("/") && expDate.length < 5) {
            cardError.value = activity.getString(R.string.please_enter_expiry_date)
            cardErrorVisible.value = true
            return false
        }
        if (expDateInvalid) {
            cardError.value = activity.getString(R.string.please_enter_expiry_date)
            cardErrorVisible.value = true
            return false
        }
        if (Objects.equals(cvc, "")) {
            cardError.value = activity.getString(R.string.please_enter_cvc)
            cardErrorVisible.value = true
            return false
        }
        if (cvcInvalid) {
            cardError.value = activity.getString(R.string.please_enter_valid_cvc)
            cardErrorVisible.value = true
            return false
        }

        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        topUpErrorVisible.value = false
        cardErrorVisible.value = false
    }

    fun setValidateAllCardDetails() {
        cardInvalid = false
        expDateInvalid = false
        cvcInvalid = false
    }

    fun initialStateOfCard() {
        myPrice = ""

        //set array list initially
        priceListInitialDataSet()

        priceAdapter.priceList = priceList
        priceAdapter.notifyDataSetChanged()
        chooseAmountText.value = "Choose Top Up Amount"
        cardInputWidget.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        if (Objects.equals(tag, "priceList")) {
            val priceList: List<PriceItem> =
                arrayList as List<PriceItem>

            myPrice = priceList[position].price
            chooseAmountText.value = "Top up amount - €$myPrice"
            invisibleErrorTexts()
            dropDownListVisible.value = false

            //set array list initially
            priceListInitialDataSet()

            this.priceList[position].isChecked = true
            priceAdapter.priceList = this.priceList
            priceAdapter.notifyDataSetChanged()
        }
    }

    private fun priceListInitialDataSet() {
        priceList = ArrayList()
        priceList.add(PriceItem("10", false))
        priceList.add(PriceItem("15", false))
        priceList.add(PriceItem("25", false))
        priceList.add(PriceItem("50", false))
        priceList.add(PriceItem("100", false))
    }
}