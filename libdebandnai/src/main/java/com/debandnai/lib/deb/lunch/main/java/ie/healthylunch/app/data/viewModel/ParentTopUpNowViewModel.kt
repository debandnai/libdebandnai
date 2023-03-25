package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.deleteCardModel.DeleteCardResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.walletManualTopUpModel.WalletManualTopUpResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ParentTopUpRepository
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.ParentTopUpNowActivity
import ie.healthylunch.app.utils.*
import kotlinx.coroutines.launch

class ParentTopUpNowViewModel(
    private val repository: ParentTopUpRepository
) : ViewModel(), DialogYesNoListener {
    lateinit var activity: ParentTopUpNowActivity
    var cardNumber: MutableLiveData<String> = MutableLiveData("")
    var brandName: MutableLiveData<String> = MutableLiveData("")
    var okButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(true)


    //Delete card
    private val _deleteCardResponse: SingleLiveEvent<Resource<DeleteCardResponse>?> =
        SingleLiveEvent()
    private var deleteCardResponse: SingleLiveEvent<Resource<DeleteCardResponse>?>? = null
        get() = _deleteCardResponse

    private fun deleteCard(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _deleteCardResponse.value = repository.deleteCard(jsonObject, token)
    }

    //Wallet Manual TopUp
    private val _walletManualTopResponse: SingleLiveEvent<Resource<WalletManualTopUpResponse>?> =
        SingleLiveEvent()
    private var walletManualTopUpResponse: SingleLiveEvent<Resource<WalletManualTopUpResponse>?>? =
        null
        get() = _walletManualTopResponse

    private fun walletManualTopUp(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _walletManualTopResponse.value = repository.walletManualTopUp(jsonObject, token)
    }


    //////////////////click functions///////////
      
    fun okClick() {
        walletManualTopUpApiCall()
    }

    fun cancelClick(activity: Activity) {
        val intent = Intent(
            activity,
            DashBoardActivity::class.java
        )
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
    }

    fun deleteCardClick(activity: Activity) {
        CustomDialog.showYesNoTypeDialog(
            activity,
            activity.getString(R.string.do_you_want_to_delete_this_card_),
            this
        )
    }

    ////////////////////////////////////////////////////

    //delete card api call
    fun deleteCardApiCall() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        deleteCard(jsonObject, Constants.TOKEN)
        deleteCardResponse()
    }

    //delete card response
    private fun deleteCardResponse() {
        MethodClass.showProgressDialog(activity)
        deleteCardResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    Toast.makeText(
                        activity,
                        it.value.response.raws.successMessage,
                        Toast.LENGTH_SHORT
                    ).show()

                    _deleteCardResponse.value = null
                    deleteCardResponse = _deleteCardResponse
                    activity.finish()


                }
                is Resource.Failure -> {
                    Log.d("CardDetails", "Not Ok")
                    if (it.errorBody != null) {
                        it.errorString?.let { it1 ->


                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        activity, null,
                                        Constants.DELETE_CARD,
                                        Constants.REFRESH_TOKEN
                                    )
                            else if (it.errorCode == 307 || it.errorCode == 426)
                                MethodClass.showErrorDialog(activity, it1, it.errorCode)
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
                    _deleteCardResponse.value = null
                    deleteCardResponse = _deleteCardResponse

                }


                else -> {}
            }

        }
    }


    //wallet manual top up api call
    fun walletManualTopUpApiCall() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        okButtonEnabled.value = false
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        walletManualTopUp(jsonObject, Constants.TOKEN)
        walletManualTopUpResponse()
    }

    //wallet manual top up response
    private fun walletManualTopUpResponse() {
        MethodClass.showProgressDialog(activity)
        walletManualTopUpResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)

                    CustomDialog.showOkTypeDialog(
                        activity,
                        activity.getString(R.string.your_top_up_added_successfully),
                        object : DialogOkListener {
                            override fun okOnClick(dialog: Dialog, activity: Activity) {
                                activity.finish()
                            }

                        }

                    )
                    okButtonEnabled.value = true
                }
                is Resource.Failure -> {
                    Log.d("CardDetails", "Not Ok")
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->


                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        activity, null,
                                        Constants.WALLET_MANUAL_TOP_UP,
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
                    _walletManualTopResponse.value = null
                    walletManualTopUpResponse = _walletManualTopResponse
                    okButtonEnabled.value = true

                }


                else -> {}
            }

        }
    }


      
    override fun yesOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
        deleteCardApiCall()
    }

    override fun noOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
    }
}