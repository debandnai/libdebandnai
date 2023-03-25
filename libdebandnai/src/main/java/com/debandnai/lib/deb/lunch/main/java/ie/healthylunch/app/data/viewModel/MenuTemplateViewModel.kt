package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.widget.RelativeLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.menuTemplate.MenuTemplateResponse
import ie.healthylunch.app.data.model.studentListModel.StudenListResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.MenuTemplateRepository
import ie.healthylunch.app.utils.Constants.Companion.STUDENT
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.coverflow.core.FinitePagerContainer
import kotlinx.coroutines.launch


@SuppressLint("StaticFieldLeak")
class MenuTemplateViewModel(private val repository: MenuTemplateRepository) :
    ViewModel() {
    var menuId: Int? = 0
    var userType: MutableLiveData<String> = MutableLiveData(STUDENT)
    var studentName: MutableLiveData<String> = MutableLiveData("")
    lateinit var rlMainLayout: RelativeLayout
    var infoVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    //var selectedMenuTemplateId: MutableLiveData<Int> = MutableLiveData(0)
    var tempName: MutableLiveData<String> = MutableLiveData("")
    var tempPrice: MutableLiveData<String> = MutableLiveData("")
    var title: MutableLiveData<String> = MutableLiveData("")
    var selectedTempId: Int? = 0
    var isShowNextButton: MutableLiveData<Boolean> = MutableLiveData(false)

    var isXpLayoutVisible: Boolean = false

    var from = ""
    var isCalendarSession = false
    var isDataVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    var studentList: List<ie.healthylunch.app.data.model.studentListModel.DataItem> = ArrayList()


    // Student  list
    val _studentListResponse: MutableLiveData<Resource<StudenListResponse>> =
        MutableLiveData()
    val studentListResponse: LiveData<Resource<StudenListResponse>>
        get() = _studentListResponse

    fun studentList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _studentListResponse.value = repository.studentListRepository(jsonObject, token)
    }

    // menu templateList
    private val _menuTemplateListResponse: MutableLiveData<Resource<MenuTemplateResponse>> =
        MutableLiveData()
    val menuTemplateListResponse: LiveData<Resource<MenuTemplateResponse>>
        get() = _menuTemplateListResponse

    fun menuTemplateList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _menuTemplateListResponse.value = repository.menuTemplateListRepository(jsonObject, token)
    }


    fun setStudentFinitePagerContainer(view: FinitePagerContainer) {
        MethodClass.setUpStudentFinitePagerContainer(view)
    }


}

