package ie.healthylunch.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.favorites.FavoritesResponse
import ie.healthylunch.app.data.model.favoritesRemoveModel.FavouriteRemoveResponse
import ie.healthylunch.app.data.model.studentListModel.StudenListResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.FavoritesRepository
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.SingleLiveEvent
import ie.healthylunch.app.utils.coverflow.core.FinitePagerContainer
import kotlinx.coroutines.launch

class FavoritesViewModel (private val repository: FavoritesRepository) :
    ViewModel() {
    var studentList: List<ie.healthylunch.app.data.model.studentListModel.DataItem> = ArrayList()
    // Student  list
    val _studentListResponse: MutableLiveData<Resource<StudenListResponse>> =
        MutableLiveData()
    val studentListResponse: LiveData<Resource<StudenListResponse>>
        get() = _studentListResponse
    var isXpLayoutVisible: Boolean = false
    var userType: MutableLiveData<String> = MutableLiveData(Constants.STUDENT)
    var studentName: MutableLiveData<String> = MutableLiveData("")
    var schoolType: Int? = Constants.STATUS_ZERO
    fun studentList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _studentListResponse.value = repository.studentListRepository(jsonObject, token)
    }


    // Favorites  list
    val _favoritesListResponse: MutableLiveData<Resource<FavoritesResponse>> =
        MutableLiveData()
    val favoritesListResponse: LiveData<Resource<FavoritesResponse>>
        get() = _favoritesListResponse


    fun favoritesList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _favoritesListResponse.value = repository.favoritesListRepository(jsonObject, token)
    }

    // Favourite Orders remove
    val _favouriteOrdersRemoveResponse: SingleLiveEvent<Resource<FavouriteRemoveResponse>?> =
        SingleLiveEvent()
    var favouriteOrdersRemoveResponse: LiveData<Resource<FavouriteRemoveResponse>?>? = null
        get() = _favouriteOrdersRemoveResponse

    fun favouriteOrdersRemove(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _favouriteOrdersRemoveResponse.value = repository.favouriteOrdersRemove(jsonObject, token)
    }


    fun setStudentFinitePagerContainer(view: FinitePagerContainer) {
        MethodClass.setUpStudentFinitePagerContainer(view)
    }
}