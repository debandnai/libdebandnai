package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.favorites.FavoritesResponse
import ie.healthylunch.app.data.model.favoritesRemoveModel.FavouriteRemoveResponse
import ie.healthylunch.app.data.model.getStudentAllowedOrderModel.StudentAllowedOrderResponse
import ie.healthylunch.app.data.model.paymentProcessing.PaymentProcessingResponse
import ie.healthylunch.app.data.model.productInfoDetailsModel.ProductInfoDetailsResponse
import ie.healthylunch.app.data.model.productListByMenuTemplateModel.ProductListByMenuTemplateResponse
import ie.healthylunch.app.data.model.saveOrder.SaveOrderResponse
import ie.healthylunch.app.data.model.singleDayOrderDateDisplay.SingleDayOrderDateDisplayResponse
import ie.healthylunch.app.data.model.studentListModel.StudenListResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ProductRepository
import ie.healthylunch.app.ui.ProductActivity
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.SingleLiveEvent
import ie.healthylunch.app.utils.coverflow.core.FinitePagerContainer
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) :
    ViewModel() {
    var title: MutableLiveData<String> = MutableLiveData("")
    var status_message_1: MutableLiveData<String> = MutableLiveData("")
    var status_message_2: MutableLiveData<String> = MutableLiveData("")
    var button_text: MutableLiveData<String> = MutableLiveData("")
    var orderStatus: MutableLiveData<Int> = MutableLiveData(STATUS_ZERO)
    var userType: MutableLiveData<String> = MutableLiveData(STUDENT)
    var studentName: MutableLiveData<String> = MutableLiveData("")
    var menuTemplateId: MutableLiveData<Int> = MutableLiveData(-1)
    var menuTemplateName: MutableLiveData<String> = MutableLiveData()
    var menuId: MutableLiveData<Int> = MutableLiveData(-1)
    var totalSugarStr: MutableLiveData<String> = MutableLiveData("")
    var sugarStr: MutableLiveData<String> = MutableLiveData("0/")
    var calorieStr: MutableLiveData<String> = MutableLiveData("0/")
    var totalAmt: MutableLiveData<String> = MutableLiveData("€0.00")
    var totalXpPointsTxt: MutableLiveData<String> = MutableLiveData("0")
    var totalCalorieStr: MutableLiveData<String> = MutableLiveData("")
    var dayName: MutableLiveData<String> = MutableLiveData("")
    var maxNumberProduct: MutableLiveData<Int> = MutableLiveData(0)
    var isXpLayoutVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isFavorites: MutableLiveData<Boolean> = MutableLiveData(false)
    var dayAndWeekDay: MutableLiveData<String> = MutableLiveData("")
    /*@SuppressLint("StaticFieldLeak")
    lateinit var progressbarOne: ProgressBar

    @SuppressLint("StaticFieldLeak")
    lateinit var progressbarTwo: ProgressBar


    @SuppressLint("StaticFieldLeak")
    var previousRelativeLayout: RelativeLayout? = null

    @SuppressLint("StaticFieldLeak")
    var nextRelativeLayout: RelativeLayout? = null
    private var minimumProductAddDialog: Dialog? = null*/


    @SuppressLint("StaticFieldLeak")
    lateinit var productActivity: ProductActivity

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
    // Product List By Menu Template
    private val _productListByMenuTemplateResponse: SingleLiveEvent<Resource<ProductListByMenuTemplateResponse>?> =
        SingleLiveEvent()
    var productListByMenuTemplateResponse: LiveData<Resource<ProductListByMenuTemplateResponse>?>? = null
        get() = _productListByMenuTemplateResponse

    fun productListByMenuTemplate(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _productListByMenuTemplateResponse.value =
            repository.productListByMenuTemplateRepository(jsonObject, token)
    }
    // Favorites  list
    private val _favoritesListResponse: MutableLiveData<Resource<FavoritesResponse>> =
        MutableLiveData()
    val favoritesListResponse: LiveData<Resource<FavoritesResponse>>
        get() = _favoritesListResponse


    fun favoritesList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _favoritesListResponse.value = repository.favoritesListRepository(jsonObject, token)
    }
   /* // Calorie Meter
    private val _calorieMeterResponse: SingleLiveEvent<Resource<CalorieMeterResponse>?> =
        SingleLiveEvent()
    var calorieMeterResponse: LiveData<Resource<CalorieMeterResponse>?>? = null
        get() = _calorieMeterResponse

    fun calorieMeter(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _calorieMeterResponse.value = repository.calorieMeterRepository(jsonObject, token)
    }*/

    // Save Menu For Single Day
    var _saveOrderResponse: SingleLiveEvent<Resource<SaveOrderResponse>?> =
        SingleLiveEvent()
    var saveOrderResponse: LiveData<Resource<SaveOrderResponse>?>? =
        null
        get() = _saveOrderResponse

    fun saveOrder(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _saveOrderResponse.value =
            repository.saveOrderRepository(jsonObject, token)
    }
    /*// Save Menu For Single Day
    var _saveMenuForSingleDayResponse: SingleLiveEvent<Resource<SaveMenuForSingleDayResponse>?> =
        SingleLiveEvent()
    var saveMenuForSingleDayResponse: LiveData<Resource<SaveMenuForSingleDayResponse>?>? =
        null
        get() = _saveMenuForSingleDayResponse

    fun saveMenuForSingleDay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _saveMenuForSingleDayResponse.value =
            repository.saveMenuForSingleDayRepository(jsonObject, token)
    }
    */

    // single Day Order Date Display
    private val _singleDayOrderDateDisplayResponse: MutableLiveData<Resource<SingleDayOrderDateDisplayResponse>> =
        MutableLiveData()
    val singleDayOrderDateDisplayResponse: LiveData<Resource<SingleDayOrderDateDisplayResponse>>
        get() = _singleDayOrderDateDisplayResponse

    fun singleDayOrderDateDisplay(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _singleDayOrderDateDisplayResponse.value =
            repository.singleDayOrderDateDisplayRepository(jsonObject, token)
    }

    // Student Allowed Order
    var _getStudentAllowedOrderResponse: SingleLiveEvent<Resource<StudentAllowedOrderResponse>?> =
        SingleLiveEvent()
    var getStudentAllowedOrderResponse: LiveData<Resource<StudentAllowedOrderResponse>?>? =
        null
        get() = _getStudentAllowedOrderResponse

    fun studentAllowedOrder(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _getStudentAllowedOrderResponse.value =
            repository.studentAllowedOrderRepository(jsonObject, token)
    }

    // Payment Processing
    var _getPaymentProcessingResponse: SingleLiveEvent<Resource<PaymentProcessingResponse>?> =
        SingleLiveEvent()
    var getPaymentProcessingResponse: LiveData<Resource<PaymentProcessingResponse>?>? =
        null
        get() = _getPaymentProcessingResponse

    fun paymentProcessingResponse(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _getPaymentProcessingResponse.value =
            repository.paymentProcessingRepository(jsonObject, token)
    }




    // product info details
    val _productInfoDetailsResponse: MutableLiveData<Resource<ProductInfoDetailsResponse>?> =
        MutableLiveData()
    var productInfoDetailsResponse: LiveData<Resource<ProductInfoDetailsResponse>?>? = null
        get() = _productInfoDetailsResponse

    fun productInfoDetailsApi(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _productInfoDetailsResponse.value = repository.productInfoDetails(jsonObject, token)
    }


    var menuTitle: MutableLiveData<String> = MutableLiveData()


    fun setStudentFinitePagerContainer(view: FinitePagerContainer) {
        MethodClass.setUpStudentFinitePagerContainer(view)
    }
}