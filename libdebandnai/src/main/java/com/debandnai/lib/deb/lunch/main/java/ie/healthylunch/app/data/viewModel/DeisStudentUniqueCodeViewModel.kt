package ie.healthylunch.app.data.viewModel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.deisStudentUniqueCodeModel.DeisStudentUniqueCodeResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.DeisStudentUniqueCodeRepository
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class DeisStudentUniqueCodeViewModel(private val repository: DeisStudentUniqueCodeRepository) :
    ViewModel() {
    var schoolName: String? = null
    var schoolId: Int? = 0
    var uniqueCode: MutableLiveData<String> = MutableLiveData()
    var uniqueCodeError: MutableLiveData<String> = MutableLiveData()
    var uniqueCodeErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    // Student  list
    val _deisStudentAddResponse: SingleLiveEvent<Resource<DeisStudentUniqueCodeResponse>?> =
        SingleLiveEvent()
    var deisStudentAddResponse: LiveData<Resource<DeisStudentUniqueCodeResponse>?>? = null
        get() = _deisStudentAddResponse

    fun deisStudentAdd(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _deisStudentAddResponse.value = repository.deisStudentAdd(jsonObject, token)
    }

    //validation
    fun uniqueCodeValidation(view: View): Boolean {
        invisibleErrorTexts()

        if (uniqueCode.value.isNullOrBlank()) {
            uniqueCodeError.value = view.context.getString(R.string.please_enter_unique_passcode)
            uniqueCodeErrorVisible.value = true
            return false
        }
        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        uniqueCodeErrorVisible.value = false
    }
}

