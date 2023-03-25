package ie.healthylunch.app.data.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.parentDetailsModel.ParentDetailsResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.DashBoardRepository
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ParentProfileViewViewModel(private val repository: DashBoardRepository) : ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData("")
    var phone: MutableLiveData<String> = MutableLiveData("")
    var name: MutableLiveData<String> = MutableLiveData("")
    var firstName: MutableLiveData<String> = MutableLiveData("")
    var lastName: MutableLiveData<String> = MutableLiveData("")
    var kitchenName: MutableLiveData<String> = MutableLiveData("")
    var parentDetails: ParentDetailsResponse? = null


    //Parent Details
    val _parentDetailsResponse: SingleLiveEvent<Resource<ParentDetailsResponse>?> =
        SingleLiveEvent()
    var parentDetailsResponse: SingleLiveEvent<Resource<ParentDetailsResponse>?>? = null
        get() = _parentDetailsResponse

    fun parentDetails(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _parentDetailsResponse.value = repository.parentDetails(jsonObject, token)
    }





}