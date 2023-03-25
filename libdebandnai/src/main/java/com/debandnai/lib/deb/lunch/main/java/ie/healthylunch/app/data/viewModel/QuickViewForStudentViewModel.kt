package ie.healthylunch.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.quickViewOrderDayModel.QuickViewOrderDayResponse
import ie.healthylunch.app.data.model.updateSettingsModel.NotificationSettingsUpdateResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.QuickViewForStudentRepository
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class QuickViewForStudentViewModel(private val repository: QuickViewForStudentRepository) :
    ViewModel() {
    var currentDay: MutableLiveData<Int> = MutableLiveData(STATUS_ONE)
    var calendarDisableTime: MutableLiveData<String> = MutableLiveData("")
    var dayName: MutableLiveData<String> = MutableLiveData("")

    // Quick View Day
    private val _quickViewDayResponse: SingleLiveEvent<Resource<QuickViewOrderDayResponse>?> =
        SingleLiveEvent()
    var quickViewDayResponse: SingleLiveEvent<Resource<QuickViewOrderDayResponse>?>? = null
        get() = _quickViewDayResponse

    fun quickViewDay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _quickViewDayResponse.value = repository.quickViewDayRepository(jsonObject, token)
    }


    val quickViewOrderDayLiveData: LiveData<Resource<QuickViewOrderDayResponse>?>
        get() = repository.quickViewOrderResponse

    fun quickViewOrderDay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        repository._quickViewOrderResponse.value =
            repository.quickViewOrderRepository(jsonObject, token)
    }
    }