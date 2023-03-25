package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.allergenListModel.AllergenListResponse
import ie.healthylunch.app.data.model.allergenListModel.DataItem
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.saveAllergenModel.SaveAllergensResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository
import ie.healthylunch.app.fragment.students.EditStudentAllergenFragment
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.SAVE_ALLERGEN
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import kotlinx.coroutines.launch

class EditStudentAllergenViewModel(private val repository: StudentRepository) : ViewModel()
     {
    var studentId: MutableLiveData<Int> = MutableLiveData()
    var userType: MutableLiveData<String> = MutableLiveData("student")
    var message: MutableLiveData<String> = MutableLiveData("")
    var studentName: MutableLiveData<String> = MutableLiveData("")
    lateinit var editStudentAllergenFragment: EditStudentAllergenFragment
    var allergenList: MutableList<DataItem> = ArrayList()
    var selectedAllergenList: JsonArray = JsonArray()

    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

     var selectedAlergenList: ArrayList<DataItem> = ArrayList()
     var nutritionalArrayList: ArrayList<DataItem> = ArrayList()
     var culturalAllergenList: ArrayList<DataItem> = ArrayList()

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
    val _saveAllergenListResponse: SingleLiveEvent<Resource<SaveAllergensResponse>?> =
        SingleLiveEvent()
    var saveAllergenListResponse: LiveData<Resource<SaveAllergensResponse>?>? = null
        get() = _saveAllergenListResponse

    fun saveAllergenList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _saveAllergenListResponse.value = repository.saveAllergensApi(jsonObject, token)
    }

    fun back(v: View) {

        val navOptions: NavOptions =
            NavOptions.Builder().setPopUpTo(R.id.editStudentAllergenFragment, true).build()

        Navigation.findNavController(v)
            .navigate(
                R.id.action_editStudentAllergenFragment_to_viewaddedstudentprofilelistFragment,
                null,
                navOptions
            )
    }




    @SuppressLint("NotifyDataSetChanged")
    fun allergenListApiCall(activity: Activity) {
        activity as DashBoardActivity
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(activity)

        //jsonObject.addProperty("student_id", StudentId.value)

        jsonObject.addProperty("student_id", Constants.STUDENT_ID)
        jsonObject.addProperty("user_type", userType.value)
        MethodClass.showProgressDialog(activity)

        allergenList(jsonObject, Constants.TOKEN)

    }









         fun setAllergensJsonArray(dataItem: MutableList<DataItem>) {


             for (i in dataItem.indices) {

                 if (dataItem[i].isMapped == Constants.STATUS_ONE && dataItem[i].id!= STATUS_ZERO) {
                     selectedAllergenList.add(dataItem[i].id.toString())
                 }

                /* if (this.allergenList[Constants.STATUS_ZERO].isMapped == Constants.STATUS_ONE) {
                     selectedAllergenList = JsonArray()
                     break
                 }*/
             }

         }

}