package ie.healthylunch.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.notificationSettingsModel.DataItem
import ie.healthylunch.app.data.model.notificationSettingsModel.NotiFicationSettingsResponse
import ie.healthylunch.app.data.model.updateSettingsModel.NotificationSettingsUpdateResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.NotificationRepository
import ie.healthylunch.app.ui.NotificationOnOffActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import kotlinx.coroutines.launch

class NotificationOnOffViewModel(private val repository: NotificationRepository) : ViewModel()
     {


    var notificationSettingsList: List<DataItem>? =null

    //Notification settings list
    val notificationSettingsLiveData: LiveData<Resource<NotiFicationSettingsResponse>?>
        get() = repository.notificationSettingsListResponse

         fun notificationSettingsList(
             jsonObject: JsonObject,
             token: String
         ) = viewModelScope.launch {
             repository._notificationSettingsListResponse.value =
                 repository.notificationSettingsRepository(jsonObject, token)
         }



    //Notification Update
    val notificationSettingUpdateLiveData: LiveData<Resource<NotificationSettingsUpdateResponse>?>
        get() = repository.notificationSettingUpdateResponse

         fun notificationSettingsUpdate(
             jsonObject: JsonObject,
             token: String
         ) = viewModelScope.launch {
             repository._notificationSettingUpdateResponse.value =
                 repository.notificationSettingsUpdateRepository(jsonObject, token)
         }

}
