package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.allergenListModel.AllergenListResponse
import ie.healthylunch.app.data.model.allergenListModel.DataItem
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.saveAllergenModel.SaveAllergensResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository
import ie.healthylunch.app.fragment.registration.AllergenFragment
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.Constants.Companion.USER_DETAILS
import kotlinx.coroutines.launch

class AllergenViewModel(private val repository: StudentRepository) : ViewModel()
{
    var studentFirstName: MutableLiveData<String> = MutableLiveData("")
    var allergenList: MutableList<DataItem> = ArrayList()
    var studentId: MutableLiveData<Int> = MutableLiveData()
    var page: MutableLiveData<String> = MutableLiveData()
    var userType: MutableLiveData<String> = MutableLiveData()
    var selectedAllergenList: JsonArray = JsonArray()
    val bundle = Bundle()
    var continueButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    lateinit var allergenFragment: AllergenFragment
    var isFromEditAllergenPage: MutableLiveData<Boolean> = MutableLiveData(false)
    var selectedAlergenList: ArrayList<DataItem> = ArrayList()
    var nutritionalArrayList: ArrayList<DataItem> = ArrayList()
    var culturalAllergenList: ArrayList<DataItem> = ArrayList()
    /*@SuppressLint("StaticFieldLeak")
    lateinit var recyclerView: RecyclerView*/

    //Allergen List
    val _allergenListResponse: SingleLiveEvent<Resource<AllergenListResponse>?> =
            SingleLiveEvent()
    var allergenListResponse: LiveData<Resource<AllergenListResponse>?>? = null
        get() = _allergenListResponse

    fun allergenList(
            jsonObject: JsonObject,
            token: String
    ) = viewModelScope.launch {
        _allergenListResponse.value = repository.allergenList(jsonObject, token)
    }

    //Save Allergen List
    //Notification Update
    val saveAllergenListLiveData: LiveData<Resource<SaveAllergensResponse>?>
        get() = repository.saveAllergenListResponse

    fun saveAllergensList(
            jsonObject: JsonObject,
            token: String
    ) = viewModelScope.launch {
        repository._saveAllergenListResponse.value =
                repository.saveAllergensApi(jsonObject, token)
    }


    /*private val _saveAllergenListResponse: SingleLiveEvent<Resource<SaveAllergensResponse>?> =
        SingleLiveEvent()
    var saveAllergenListResponse: LiveData<Resource<SaveAllergensResponse>?>? = null
        get() = _saveAllergenListResponse

    fun saveAllergensList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _saveAllergenListResponse.value = repository.saveAllergensApi(jsonObject, token)
    }*/


    /*fun setRecyclerView(view: RecyclerView, activity: Activity) {
        recyclerView = view

        view.apply {
            val allergenListAdapter =
                AllergenListAdapter(activity, ArrayList(), this@AllergenViewModel)
            view.adapter = allergenListAdapter
            view.isFocusable = false
        }
    }*/





}

