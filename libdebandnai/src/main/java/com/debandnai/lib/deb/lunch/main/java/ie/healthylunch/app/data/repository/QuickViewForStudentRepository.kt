package ie.healthylunch.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.quickViewOrderDayModel.QuickViewOrderDayResponse
import ie.healthylunch.app.data.model.updateSettingsModel.NotificationSettingsUpdateResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class QuickViewForStudentRepository (private val apiInterface: ApiInterface) : BaseRepository()  {
    suspend fun quickViewDayRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.quickViewResponseApi(jsonObject, token, "application/json")
    }

    suspend fun quickViewOrderRepository(
        jsonObject: JsonObject,
        token: String

    ) = safeApiCall {
        apiInterface.quickViewOrderResponseApi(jsonObject, token )
    }

    val _quickViewOrderResponse = SingleLiveEvent<Resource<QuickViewOrderDayResponse>?>()
    val quickViewOrderResponse : LiveData<Resource<QuickViewOrderDayResponse>?>
        get() = _quickViewOrderResponse
}