package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.app.Dialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.cardListModel.CardListResponse
import ie.healthylunch.app.data.model.couponCodeValidationModel.CouponCodeValidationResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.AddVoucherCodeRepository
import ie.healthylunch.app.ui.ParentTopUpByVoucherActivity
import ie.healthylunch.app.utils.*
import kotlinx.coroutines.launch

class AddVoucherCodeViewModel(
    private val repository: AddVoucherCodeRepository
) : ViewModel() {
    lateinit var activity: ParentTopUpByVoucherActivity
    var walletBalance: MutableLiveData<String> = MutableLiveData("€ 0.00")
    var couponCodeErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var couponCodeError: MutableLiveData<String> = MutableLiveData("")
    var couponCode: MutableLiveData<String> = MutableLiveData("")

    //card List
    private val _cardListResponse: SingleLiveEvent<Resource<CardListResponse>?> = SingleLiveEvent()

    private var cardListResponse: SingleLiveEvent<Resource<CardListResponse>?>? = null
        get() = _cardListResponse


    private fun cardList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _cardListResponse.value = repository.cardList(jsonObject, token)
    }


    fun cardListApiCall() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()

        val cardDetailsJsonObject = MethodClass.getCommonJsonObject(activity)
        cardList(cardDetailsJsonObject, Constants.TOKEN)
        cardListApiResponse()
    }


    private fun cardListApiResponse() {
        MethodClass.showProgressDialog(activity)
        cardListResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    val data = it.value.response.raws.data

                    walletBalance.value = "€ ${data.walletBalance}"



                    MethodClass.hideProgressDialog(activity)
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->


                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        activity, null,
                                        Constants.CARD_DETAILS,
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
                    _cardListResponse.value = null
                    cardListResponse = _cardListResponse

                }


                else -> {}
            }

        }
    }


    //coupon code validation
    private val _couponCodeValidationResponse: SingleLiveEvent<Resource<CouponCodeValidationResponse>?> =
        SingleLiveEvent()

    private var couponCodeValidationResponse: SingleLiveEvent<Resource<CouponCodeValidationResponse>?>? =
        null
        get() = _couponCodeValidationResponse


    private fun couponCodeValidation(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _couponCodeValidationResponse.value = repository.couponCodeValidation(jsonObject, token)
    }



    fun couponCodeValidationApiCall() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("coupon_code",couponCode.value)
        couponCodeValidation(jsonObject, Constants.TOKEN)
        couponCodeValidationApiResponse()
    }


    private fun couponCodeValidationApiResponse() {
        MethodClass.showProgressDialog(activity)
        couponCodeValidationResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    cardListApiCall()

                    CustomDialog.showOkTypeDialog(
                        activity,
                        activity.getString(R.string.voucher_added_successfully),
                        object : DialogOkListener {
                            override fun okOnClick(dialog: Dialog, activity: Activity) {
                                activity.finish()
                            }

                        }

                    )

                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->


                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        activity, null,
                                        Constants.COUPON_VALIDATION,
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
                    _couponCodeValidationResponse.value = null
                    couponCodeValidationResponse = _couponCodeValidationResponse

                }


                else -> {}
            }

        }
    }

    //click function
      
    fun continueClick(activity: Activity) {
        if (couponCodeValidation(activity))
            couponCodeValidationApiCall()
    }


    //validation
    private fun couponCodeValidation(activity: Activity): Boolean {

        invisibleErrorTexts()

        if (couponCode.value.isNullOrEmpty()) {
            couponCodeError.value = activity.getString(R.string.please_enter_coupon_code)
            couponCodeErrorVisible.value = true
            return false
        }
        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        couponCodeErrorVisible.value = false
    }
}