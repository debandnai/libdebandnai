package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.sendFeedBackModel.SendFeedBackResponse
import androidx.recyclerview.widget.RecyclerView

import ie.healthylunch.app.data.model.feedBackListModel.FeedBackListResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.FeedBackRepository
import ie.healthylunch.app.ui.FeedbackActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.FEEDBACK_SUBMIT
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.SingleLiveEvent
import ie.healthylunch.app.utils.UserPreferences
import kotlinx.coroutines.launch

class FeedBackViewModel(
    val repository: FeedBackRepository


) : ViewModel(), AdapterItemOnclickListener {
    var feedBack: MutableLiveData<String> = MutableLiveData()

    @SuppressLint("StaticFieldLeak")
    lateinit var recyclerView: RecyclerView

    var feedbackListVisible: MutableLiveData<Boolean> = MutableLiveData(false)


    //Send FeedBack
    private var _feedBackSubmitResponse: SingleLiveEvent<Resource<SendFeedBackResponse>?> =
        SingleLiveEvent()
    private var feedBackSubmitResponse: LiveData<Resource<SendFeedBackResponse>?>? = null
        get() = _feedBackSubmitResponse

    private fun sendFeedBack(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _feedBackSubmitResponse.value = repository.sendFeedBack(jsonObject, token)
    }


    //Get FeedBack
    private var _feedBackListResponse: MutableLiveData<Resource<FeedBackListResponse>> =
        MutableLiveData()
    private val feedBackListResponse: LiveData<Resource<FeedBackListResponse>>
        get() = _feedBackListResponse

    private fun getFeedback(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _feedBackListResponse.value = repository.feedBackList(jsonObject, token)
    }


      
    fun submitFeedBackOnclick(activity: Activity) {
        if (feedBack.value.isNullOrEmpty()) {
            Toast.makeText(
                activity,
                activity.getString(R.string.please_write_down_your_feedback),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        submitFeedBack(activity)

    }


    fun submitFeedBack(activity: Activity) {

        activity as FeedbackActivity
        MethodClass.showProgressDialog(activity)

        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("message", feedBack.value)
        loginResponse?.response?.raws?.data?.token?.let { sendFeedBack(jsonObject, it) }
    }

      
    fun getSubmitFeedbackResponse(activity: Activity) {
        activity as FeedbackActivity
        feedBackSubmitResponse?.observe(activity) {

            when (it) {

                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    feedBack.value = ""
                    Toast.makeText(activity, "Feedback added successfully", Toast.LENGTH_SHORT)
                        .show()
                    //getFeedBackList(activity)
                    _feedBackSubmitResponse.value = null
                    feedBackSubmitResponse = _feedBackSubmitResponse

                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(activity)
                        it.errorString?.let { _ ->
                            it.errorString.let { it1 ->
                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            activity,
                                            null,
                                            FEEDBACK_SUBMIT,
                                            REFRESH_TOKEN
                                        )
                                else {
                                    MethodClass.showErrorDialog(
                                        activity,
                                        it1,
                                        it.errorCode
                                    )
                                }
                            }
                        }


                    }
                    _feedBackSubmitResponse.value = null
                    feedBackSubmitResponse = _feedBackSubmitResponse
                }
                else -> {}
            }

        }

    }

    /*  
    fun getFeedBackList(activity: FeedbackActivity) {
        MethodClass.showProgressDialog(activity)

        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("message", feedBack.value)


        loginResponse?.response?.raws?.data?.let { getFeedback(jsonObject, it.token) }
        feedBackListResponse.observe(activity, {

            when (it) {

                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)

                    if (it.value.response.raws.data!!.isNotEmpty()) {

                        recyclerView.apply {
                            val feedbackAdapter =
                                FeedbackAdapter(
                                    activity,
                                    it.value.response.raws.data,
                                    this@FeedBackViewModel
                                )
                            recyclerView.adapter = feedbackAdapter
                            recyclerView.isFocusable = false

                            feedbackListVisible.value = true
                        }
                    } else {
                        feedbackListVisible.value = false
                    }

                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(activity)
                        it.errorString?.let { it1 ->
                            it.errorString.let { it1 ->
                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            activity,
                                            null,
                                            FEEDBACK_LIST,
                                            REFRESH_TOKEN
                                        )
                                else {
                                    MethodClass.showErrorDialog(
                                        activity,
                                        it1,
                                        it.errorCode
                                    )
                                }
                            }
                        }


                    }
                }
            }

        })
    }

      
    fun getFeedBack(activity: Activity) {

        getFeedBackList(activity as FeedbackActivity)
    }*/


    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {

    }

}