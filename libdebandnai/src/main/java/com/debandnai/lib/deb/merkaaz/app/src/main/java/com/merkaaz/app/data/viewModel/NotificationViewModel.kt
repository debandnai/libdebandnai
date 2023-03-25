package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.NotificationRepository
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository,
    private val sharedPreff: SharedPreff,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {

    var isShowNoDataFound: MutableLiveData<Boolean> = MutableLiveData(false)
    var isShowMainLayout: MutableLiveData<Boolean> = MutableLiveData(false)

    //notification list
    val notificationListLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.notificationListResponse

    ////notification delete
    val notificationDeleteLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.notificationDeleteResponse

    //notification list
    fun getNotificationList() {
        viewModelScope.launch {
            context.let {
                val jsonObject = commonFunctions.commonJsonData()
                jsonObject.addProperty("device_token", sharedPreff.getfirebase_token())
                repository.notificationList(
                    jsonObject,
                    commonFunctions.commonHeader()
                )
            }

        }
    }

    //notification delete
    fun notificationDelete(deleteType: String, notificationId: String) {
        viewModelScope.launch {
            val jsonObject = commonFunctions.commonJsonData()
            jsonObject.addProperty("delete_type", deleteType)
            jsonObject.addProperty("notification_id", notificationId)
            repository.notificationDelete(
                jsonObject,
                commonFunctions.commonHeader()
            )
        }
    }

    //view click
    fun goToDashBoardClick(view: View) {
        view.context.startActivity(
            Intent(view.context, DashBoardActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        )
    }
}