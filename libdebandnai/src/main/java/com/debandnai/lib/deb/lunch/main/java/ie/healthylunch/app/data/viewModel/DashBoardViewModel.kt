package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.clearOrderModel.CleareOrderResponse
import ie.healthylunch.app.data.model.dashBoardViewResponseModel.DashBoardViewResponse
import ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel.DashboardBottomCalendarNewResponse
import ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel.DataItem
import ie.healthylunch.app.data.model.dashboardCaloremeterSixDayNewModel.DasboardCalorieMeterSixDayNewResponse
import ie.healthylunch.app.data.model.favoriteOrdersAdd.FavoriteOrdersAddResponse
import ie.healthylunch.app.data.model.favorites.FavoritesResponse
import ie.healthylunch.app.data.model.favoritesRemoveModel.FavouriteRemoveResponse
import ie.healthylunch.app.data.model.holidaySavedModel.holidaysavedResponse
import ie.healthylunch.app.data.model.notificationCountModel.NotificationCountResponse
import ie.healthylunch.app.data.model.parentDetailsModel.ParentDetailsResponse
import ie.healthylunch.app.data.model.replacedOrderForNextWeekSamedayModel.ReplacedOrderForNextWeekSamedayResponse
import ie.healthylunch.app.data.model.sixDayMenuListModel.SixDayMenuListResponse
import ie.healthylunch.app.data.model.studentListModel.StudenListResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.DashBoardRepository
import ie.healthylunch.app.ui.CalendarActivity
import ie.healthylunch.app.ui.FeedbackActivity
import ie.healthylunch.app.ui.NotificationOnOffActivity
import ie.healthylunch.app.ui.QuickViewForStudentActivity
import ie.healthylunch.app.utils.Constants.Companion.CURRENT_ORDER_DAY
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.SingleLiveEvent
import ie.healthylunch.app.utils.coverflow.core.FinitePagerContainer
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@SuppressLint("StaticFieldLeak")
class DashBoardViewModel(private val repository: DashBoardRepository) : ViewModel() {
    var calendarDisableTime: MutableLiveData<String> = MutableLiveData("")
    var studentXpPoints: MutableLiveData<String> = MutableLiveData("")
    var clearOrderPopupMessage: MutableLiveData<String> = MutableLiveData("")
    //School tyoe 1 for private and 2 for DEIS student
    var schoolType: Int? = 0
    var isRepeatOrder: MutableLiveData<Boolean> = MutableLiveData(false)
    var orderStatus: MutableLiveData<String> = MutableLiveData("")
    var isXpLayoutVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isBottomCalendarVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isDeisBottomLayoutVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    var bottomCalendarList:List<DataItem>?=null
    var endDateStr: String? = null
    var endDate30Str: String? = null
    var favoritesIConIsClickable=true

    var userEmail: MutableLiveData<String> = MutableLiveData("")


    companion object {
        var selectedStudentPrePosition = 0

    }

    // Student  list
    val _studentListResponse: SingleLiveEvent<Resource<StudenListResponse>?> =
        SingleLiveEvent()
    var studentListResponse: LiveData<Resource<StudenListResponse>?>? = null
        get() = _studentListResponse

    fun studentList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _studentListResponse.value = repository.studentList(jsonObject, token)
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

    // Save one day Holiday
    val _saveHolyDayResponse: SingleLiveEvent<Resource<holidaysavedResponse>?> =
        SingleLiveEvent()
    val saveHolyDayResponse: SingleLiveEvent<Resource<holidaysavedResponse>?>
        get() = _saveHolyDayResponse

    fun saveHolyDay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _saveHolyDayResponse.value = repository.saveHoliday(jsonObject, token)
    }

    // dashboard  view
    val _dashBoardViewResponse: SingleLiveEvent<Resource<DashBoardViewResponse>?> =
        SingleLiveEvent()
    var dashBoardViewResponse: LiveData<Resource<DashBoardViewResponse>?>? = null
        get() = _dashBoardViewResponse

    fun dashBoardView(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _dashBoardViewResponse.value = repository.dashBoardViewApi(jsonObject, token)
    }
    // Dashboard Current Order view
        val _viewDashboardCurrentOrderResponse: SingleLiveEvent<Resource<DashBoardViewResponse>?> =
            SingleLiveEvent()
        var viewDashboardCurrentOrderResponse: LiveData<Resource<DashBoardViewResponse>?>? = null
            get() = _viewDashboardCurrentOrderResponse

        fun viewDashBoardCurrentOrder(
            jsonObject: JsonObject,
            token: String
        ) = viewModelScope.launch {
            _viewDashboardCurrentOrderResponse.value = repository.viewDashboardCurrentOrderApi(jsonObject, token)
        }

    // Dashboard calorie meter six day
    private val _dashboardCalorieMeterSixDayResponse: SingleLiveEvent<Resource<DasboardCalorieMeterSixDayNewResponse>?> =
        SingleLiveEvent()
    private var dashboardCalorieMeterSixDayResponse: LiveData<Resource<DasboardCalorieMeterSixDayNewResponse>?>? =
        null
        get() = _dashboardCalorieMeterSixDayResponse

    // Dashboard calorie meter six day
    fun dashboardCalorieMeterSixDay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _dashboardCalorieMeterSixDayResponse.value =
            repository.dashboardCalorieMeterSixDay(jsonObject, token)
    }

    // Clear Order
    val _clearOrderResponse: SingleLiveEvent<Resource<CleareOrderResponse>?> =
        SingleLiveEvent()
    var clearOrderResponse: LiveData<Resource<CleareOrderResponse>?>? = null
        get() = _clearOrderResponse

    fun clearOrder(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _clearOrderResponse.value = repository.clearOrder(jsonObject, token)
    }

    // Favourite Orders Add
    val _favouriteOrdersAddResponse: SingleLiveEvent<Resource<FavoriteOrdersAddResponse>?> =
        SingleLiveEvent()
    var favouriteOrdersAddResponse: LiveData<Resource<FavoriteOrdersAddResponse>?>? = null
        get() = _favouriteOrdersAddResponse

    fun favouriteOrdersAdd(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _favouriteOrdersAddResponse.value = repository.favouriteOrdersAdd(jsonObject, token)
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



    // replaced Order For Next Week SameDay Api
    val _replacedOrderForNextWeekSameDayResponse: SingleLiveEvent<Resource<ReplacedOrderForNextWeekSamedayResponse>?> =
        SingleLiveEvent()
    var replacedOrderForNextWeekSameDayResponse: LiveData<Resource<ReplacedOrderForNextWeekSamedayResponse>?>? =
        null
        get() = _replacedOrderForNextWeekSameDayResponse

    fun replacedOrderForNextWeekSameDay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _replacedOrderForNextWeekSameDayResponse.value =
            repository.replacedOrderForNextWeekSameDayApi(jsonObject, token)
    }


    //Notification Count
    val _notificationCountResponse: SingleLiveEvent<Resource<NotificationCountResponse>?> =
        SingleLiveEvent()
    var notificationCountResponse: LiveData<Resource<NotificationCountResponse>?>? = null
        get() = _notificationCountResponse

    fun notificationListWithoutLoader(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _notificationCountResponse.value = repository.notificationCount(jsonObject, token)
    }

    //get parent details
    val _parentDetailsResponse: SingleLiveEvent<Resource<ParentDetailsResponse>?> =
        SingleLiveEvent()
    var parentDetailsResponse: LiveData<Resource<ParentDetailsResponse>?>? = null
        get() = _parentDetailsResponse

    // Six day menu list
    val _sixDayMenuListResponse: SingleLiveEvent<Resource<SixDayMenuListResponse>?> =
        SingleLiveEvent()
    var sixDayMenuListResponse: LiveData<Resource<SixDayMenuListResponse>?>? = null
        get() = _sixDayMenuListResponse

    // DashBoard Bottom Calender
    val _dashBoardBottomCalenderResponse: SingleLiveEvent<Resource<DashboardBottomCalendarNewResponse>?> =
        SingleLiveEvent()
    var dashBoardBottomCalenderResponse: LiveData<Resource<DashboardBottomCalendarNewResponse>?>? =
        null
        get() = _dashBoardBottomCalenderResponse


    //Parallel api calling using coroutine scope(dashboard order list api + dashboard calender api + notification count api + parent details api)

    fun dashboardList(
        parentDetailsJsonObject: JsonObject,
        notificationJsonObject: JsonObject,
        dashBoardBottomCalenderJsonObject: JsonObject,
        dashBoardBottomViewJsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {

        // coroutineScope is needed, else in case of any network error, it will crash
        try {
            coroutineScope {


                if (CURRENT_ORDER_DAY.isNotEmpty()){
                    //DashBoard current order view
                    val dashBoardCurrentOrderViewFromApi =
                        async { repository.viewDashboardCurrentOrderApi(dashBoardBottomViewJsonObject, token) }
                    _viewDashboardCurrentOrderResponse.value = dashBoardCurrentOrderViewFromApi.await()
                }
                else {
                    //DashBoard view
                    val dashBoardViewFromApi =
                        async { repository.dashBoardViewApi(dashBoardBottomViewJsonObject, token) }
                    _dashBoardViewResponse.value = dashBoardViewFromApi.await()

                }

                //Bottom Calender
                val dashBoardBottomCalender =
                    async {
                        repository.dashboardBottomCalendarNewApi(
                            dashBoardBottomCalenderJsonObject,
                            token
                        )
                    }
                _dashBoardBottomCalenderResponse.value = dashBoardBottomCalender.await()

                //Parent Details
                val parentDetailsFromApi =
                    async { repository.parentDetails(parentDetailsJsonObject, token) }
                _parentDetailsResponse.value = parentDetailsFromApi.await()

                //Notification Count
                val notificationCountFromApi =
                    async { repository.notificationCount(notificationJsonObject, token) }
                _notificationCountResponse.value = notificationCountFromApi.await()


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun setFinitePagerContainer(view: FinitePagerContainer) {
        view.setOverlapSlider(
            unSelectedItemRotation = 0f,
            unSelectedItemAlpha = 0.6f,
            minScale = 0.1f,
            itemGap = 0f
        )
    }


    fun setStudentFinitePagerContainer(view: FinitePagerContainer) {
        MethodClass.setUpStudentFinitePagerContainer(view)
    }

    fun quickViewClick(activity: Activity) {
        val intent = Intent(Intent(activity, QuickViewForStudentActivity::class.java))
        activity.startActivity(intent)
    }

    fun alertClick(activity: Activity) {
        val intent = Intent(Intent(activity, NotificationOnOffActivity::class.java))
        activity.startActivity(intent)
    }

    fun feedbackClick(activity: Activity) {
        val intent = Intent(Intent(activity, FeedbackActivity::class.java))
        activity.startActivity(intent)
    }

    fun calenderClick(activity: Activity) {
        val intent = Intent(Intent(activity, CalendarActivity::class.java))
        activity.startActivity(intent)
    }
}