package ie.healthylunch.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.*
import ie.healthylunch.app.data.model.favorites.FavouriteOrdersItem
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.productInfoDetailsModel.Data
import ie.healthylunch.app.data.model.productListByMenuTemplateModel.ProductGroupItem
import ie.healthylunch.app.data.model.productListByMenuTemplateModel.ProductItem
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ProductRepository
import ie.healthylunch.app.data.viewModel.DashBoardViewModel
import ie.healthylunch.app.data.viewModel.ProductViewModel
import ie.healthylunch.app.databinding.ActivityProductBinding
import ie.healthylunch.app.databinding.MenuOrderReviewBinding
import ie.healthylunch.app.databinding.OrderStatusLayoutWithMsgBinding
import ie.healthylunch.app.databinding.ProductInfoPopupBinding
import ie.healthylunch.app.databinding.SelectMinimumOneProductDialogBinding
import ie.healthylunch.app.databinding.SelectNextButtonDialogBinding
import ie.healthylunch.app.fragment.OrderProcessFragmentDialog
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.COMMA_TAG
import ie.healthylunch.app.utils.Constants.Companion.CURRENT_DAY
import ie.healthylunch.app.utils.Constants.Companion.DASHBOARD_ID
import ie.healthylunch.app.utils.Constants.Companion.FAVORITES_ACTIVITY
import ie.healthylunch.app.utils.Constants.Companion.FROM_TAG
import ie.healthylunch.app.utils.Constants.Companion.ORDER_DATE
import ie.healthylunch.app.utils.Constants.Companion.ORDER_PROCESS_DIALOG
import ie.healthylunch.app.utils.Constants.Companion.REPEAT
import ie.healthylunch.app.utils.Constants.Companion.SINGLE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FIVE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FOUR
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_SIX
import ie.healthylunch.app.utils.Constants.Companion.STATUS_THREE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT
import ie.healthylunch.app.utils.Constants.Companion.TEACHER
import ie.healthylunch.app.utils.MethodClass.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class ProductActivity :
    BaseActivity<ProductViewModel, ProductRepository>(), AdapterItemOnclickListener,
    DialogYesNoListener, DialogOnceListener {
    lateinit var binding: ActivityProductBinding
    private var orderWeekDay = ""
    var from = ""
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var orderdate: Date? = null
    var noFavoritesInfoDialog:Dialog?=null
    private var selectedFavouriteOrdersList:FavouriteOrdersItem?=null
    private var repeatRl: RelativeLayout? = null
    private var rlEveryDay: RelativeLayout? = null
    private var firstOrder: Int = STATUS_ZERO
    private var hasOrder = ""
    private var orderType = ""
    private var schoolType: Int? = STATUS_ZERO
    var isOrderedGroupPosition = STATUS_ZERO
    private var selectedProductList: JsonArray = JsonArray()
    private var selectedProductGroupList: JsonArray = JsonArray()
    var productGroupList: List<ProductGroupItem?>? = ArrayList()
    private var productGroupAdapter: ProductGroupAdapter? = null
    private var allPriceTotal: Float = 0f
    private var allXpPoints: Int = STATUS_ZERO
    private var paymentProcessingDialog: Dialog? = null
    private var minimumProductAddDialog: Dialog? = null
    private var selectNextButtonDialog: Dialog? = null
    var favouriteOrdersItem:FavouriteOrdersItem? = null
    private var weekDay: Int = STATUS_ZERO
    private var templatePrice: Float = 0f
    private var templateXpPoint: Int = STATUS_ZERO
    private val favoritesAdapter = FavoritesAdapter(ArrayList(),null, this)
    private var selectedFavoritesId= 0
    private var selectedFavoriteProductName= ""
    private var orderDialog: Dialog? = null
    var orderNextDate: String = ""
    var orderNextDay: String = ""
    var orderNextWeekDay: String = ""
    var orderProcessFragmentDialog:OrderProcessFragmentDialog?=null
    lateinit var loader : Dialog


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_product)
        selectedGroupPosition = STATUS_ZERO
        weekDay = CURRENT_DAY

        viewModel.userType.value = intent.getStringExtra("userType")
        viewModel.menuTemplateId.value = intent.getIntExtra("menuTemplateId", STATUS_MINUS_ONE)
        viewModel.studentName.value = intent.getStringExtra("studentName")
        viewModel.menuId.value = intent.getIntExtra("menuId", STATUS_MINUS_ONE)
        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        init()
        onViewClick()


    }


    private fun init() {

        loader = MethodClass.loaderDialog(binding.root.context)

        orderProcessFragmentDialog=OrderProcessFragmentDialog()
        if (intent.extras !== null) {
            from = intent.getStringExtra(FROM_TAG).toString()
        }
        noFavoritesInfoDialog=Dialog(this)
        binding.productGroupRv.apply {
            productGroupAdapter = ProductGroupAdapter(
                this@ProductActivity,
                ArrayList(),
                viewModel,
                this@ProductActivity
            )
            this.adapter = productGroupAdapter
            this.isFocusable = false
        }


        //register OnPageChangeCallback On StudentViewPager
        registerOnPageChangeCallbackOnStudentViewPager()

        //Get Single Day Order Date Display Response
        getProductListByMenuTemplateResponse()

        //Get Single Day Order Date Display Response
        getSingleDayOrderDateDisplayResponse()

        //Get SaveMenu For SingleDay Response
        getSaveMenuForSingleDayResponse()

        //Get student AllowedOrder Response
        studentAllowedOrderResponse()

        //Get Calorie Meter Response
        //getCalorieMeterResponse()

        //Get student list Response
        getStudentListResponse()

        //Get Product Info Details Response
        getProductInfoDetailsResponse()

        //Get Favorites Response
        getFavoritesListResponse()

        if (from!=null && from.equals(FAVORITES_ACTIVITY,true)){
            binding.tvOrderDate.isVisible=true
            binding.tvWeekDayName.isVisible=true
            binding.llCost.isVisible=false
            viewModel.isXpLayoutVisible.value=false
            binding.productGroupRv.isVisible=false
            binding.tvMaxNumberProduct.isVisible=false
            viewModel.isFavorites.value=true
            viewModel.menuTemplateName.value=getString(R.string.my_favourites)

            //favorites
            binding.productChildRv.adapter = favoritesAdapter
            binding.productChildRv.isFocusable = false

            if (ORDER_DATE.isNotEmpty()){
                orderdate = simpleDateFormat.parse(ORDER_DATE)
                val calendar = Calendar.getInstance()
                calendar.time=orderdate

                val dayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)
                val weekday: String = DateFormatSymbols().shortWeekdays[dayOfWeek]

                val day:String= when (calendar.get(Calendar.DAY_OF_MONTH) % 10) {
                    STATUS_ONE -> Constants.ST
                    STATUS_TWO -> Constants.ND
                    STATUS_THREE -> Constants.RD
                    else -> Constants.TH
                }

                viewModel.dayAndWeekDay.value= "$weekday ${calendar.get(Calendar.DAY_OF_MONTH)}$day"

            }
        }

        /*if (from!=null && from.equals(FAVORITES_ACTIVITY,true)){

            favouriteOrdersItem= if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(FAVORITES_LIST,FavouriteOrdersItem::class.java)
            } else {
                intent.getParcelableExtra(FAVORITES_LIST)
            }
            selectedProductGroupList = JsonParser.parseString(Gson().toJson(favouriteOrdersItem?.allId)).asJsonArray
            viewModel.menuTemplateId.value = favouriteOrdersItem?.fkSublunchMenuId
            viewModel.menuId.value = favouriteOrdersItem?.fkMenuId
            schoolType=intent.getIntExtra(SCHOOL_TYPE, STATUS_ZERO)
            singleDayOrderDateDisplay()
        }
        else {*/
            //student list api call
            getStudentList()
      //  }

    }

      
    private fun onViewClick() {
        binding.nextBtn.setOnClickListener {
            if (from.equals(FAVORITES_ACTIVITY,true)) {
                if (selectedFavouriteOrdersList != null) {
                    selectedProductGroupList =
                        JsonParser.parseString(Gson().toJson(selectedFavouriteOrdersList?.allId)).asJsonArray
                    viewModel.menuTemplateId.value = selectedFavouriteOrdersList?.fkSublunchMenuId
                    viewModel.menuId.value = selectedFavouriteOrdersList?.fkMenuId
                    selectedFavouriteOrdersList?.id?.let { favId-> selectedFavoritesId=favId}
                    selectedFavouriteOrdersList?.productName?.let { productName-> selectedFavoriteProductName=productName}
                    singleDayOrderDateDisplay()
                } else {
                    showToast(this, getString(R.string.please_select_one_order))
                }
            }
            else {
                next()
            }
        }
    }

    companion object {

        var selectedGroupPosition = 0
    }

    override fun getViewModel() = ProductViewModel::class.java
    override fun getRepository() =
        ProductRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    private fun getStudentList() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        //MethodClass.showProgressDialog(this)
        loader.show()
        val jsonObject = MethodClass.getCommonJsonObject(this)
        viewModel.studentList(jsonObject, Constants.TOKEN)
    }

      
    fun getStudentListResponse() {
        binding.studentViewPager.offscreenPageLimit = 4
        viewModel.studentListResponse.observe(this) {

            when (it) {
                is Resource.Success -> {
                    //MethodClass.hideProgressDialog(this)
                    if (it.value.response.raws.data.isNotEmpty()) {
                        viewModel.studentList = it.value.response.raws.data
                        viewModel.studentName.value =
                            viewModel.studentList[Constants.SELECTED_STUDENT_POSITION].fName
                        viewModel.userType.value= viewModel.studentList[Constants.SELECTED_STUDENT_POSITION].user_type
                        //check if school type deis or private. schoolType == 1 is for private and schoolType == 2 is for deis
                        //xp points only be visible for private school i.e. for schoolType == 1
                        viewModel.isXpLayoutVisible.value =
                            viewModel.studentList[Constants.SELECTED_STUDENT_POSITION].schoolType == STATUS_ONE
                                    && viewModel.userType.value.equals(STUDENT, true)

                        val studentPagerAdapter =
                            StudentListPagerAdapter(this, viewModel.studentList)
                        binding.studentViewPager.adapter = studentPagerAdapter
                        binding.studentViewPager.isFocusable = false

                        binding.studentViewPager.currentItem =
                            Constants.SELECTED_STUDENT_POSITION

                        //call product list api
                        getProductLitByMenuTemplate()


                    } else {
                        viewModel.studentList = ArrayList()
                        AppController.getInstance().isDeActivatedAllStudents = true
                        AppController.getInstance().logoutAppController(this, true)
                    }

                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(this)
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                MethodClass.hideProgressDialog(this)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.STUDENT_LIST,
                                        Constants.REFRESH_TOKEN
                                    )
                            } else {
                                it.errorString.let { it1 ->
                                    MethodClass.hideProgressDialog(this)
                                    MethodClass.showErrorDialog(
                                        this,
                                        it1,
                                        it.errorCode
                                    )

                                }
                            }


                        }


                    }
                    //MethodClass.hideProgressDialog(this)
                    loader.dismiss()
                }

            }


        }
    }


    /*  
    fun getCalorieMeter() {
        //MethodClass.showProgressDialog(productActivity)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)
        jsonObject.add("productid", selectedProductList)
        viewModel.calorieMeter(jsonObject, Constants.TOKEN)

    }

      
    fun getCalorieMeterResponse() {

        viewModel.calorieMeterResponse?.observe(this) {

            when (it) {
                is Resource.Success -> {
                    var respectiveCalorieStr = ""
                    var respectiveSugarStr = ""

                    for (i in it.value.response.raws.data.nutrient!!.indices) {

                        //getting respective calorie
                        if (Objects.equals(
                                it.value.response.raws.data.nutrient[i].nutrientName,
                                "Energy(Kcal)"
                            )
                        ) {
                            respectiveCalorieStr =
                                it.value.response.raws.data.nutrient[i].nutrientValue
                        }

                        //getting respective sugar
                        if (Objects.equals(
                                it.value.response.raws.data.nutrient[i].nutrientName,
                                "Carbs Sugars(g)"
                            )
                        ) {
                            respectiveSugarStr =
                                it.value.response.raws.data.nutrient[i].nutrientValue
                        }


                    }


                    //set Calorie data
                    var respectiveCalorieFloat = 0F
                    var totalCalorieFloat = 0F
                    try {
                        respectiveCalorieFloat = respectiveCalorieStr.toFloat()
                        totalCalorieFloat = viewModel.totalCalorieStr.value?.toFloat()!!
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    viewModel.calorieStr.value = "$respectiveCalorieStr/"
                    progressbarOne.progress =
                        ((respectiveCalorieFloat / totalCalorieFloat) * 100).toInt()


                    //set Sugar data
                    var respectiveSugarFloat = 0F
                    var totalSugarFloat = 0F
                    try {
                        respectiveSugarFloat = respectiveSugarStr.toFloat()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {

                        totalSugarFloat = viewModel.totalSugarStr.value?.toFloat()!!
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    viewModel.sugarStr.value = "$respectiveSugarStr/"
                    progressbarTwo.progress =
                        ((respectiveSugarFloat / totalSugarFloat) * 100).toInt()

                    //MethodClass.hideProgressDialog(productActivity)

                }
                is Resource.Failure -> {
                    //MethodClass.hideProgressDialog(productActivity)
                    if (it.errorBody != null) {
                        it.errorString?.let { it1 ->
                            if (it.errorCode == 401) {
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.CALORIE_METER,
                                        Constants.REFRESH_TOKEN
                                    )
                            }
                            else if (it.errorCode == 307 || it.errorCode == 426)
                                MethodClass.showErrorDialog(this, it1, it.errorCode)
                            it.errorString.let {
                                *//*MethodClass.showErrorDialog(
                                    productActivity,
                                    it1,
                                    it.errorCode
                                )*//*

                            }


                        }


                    }
                }

                else -> {}
            }


        }
    }*/



    fun getProductLitByMenuTemplate() {
        //MethodClass.showProgressDialog(this)
        if (!loader.isShowing)
            loader.show()
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)
        jsonObject.addProperty("user_type", viewModel.userType.value)
        jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
        jsonObject.addProperty("menu_template_id", viewModel.menuTemplateId.value)
        jsonObject.addProperty("order_date", Constants.ORDER_DATE)
        viewModel.productListByMenuTemplate(jsonObject, Constants.TOKEN)
    }

      
    fun getProductListByMenuTemplateResponse() {
        viewModel.productListByMenuTemplateResponse?.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.response.raws.data.menutemplateDetails.isNullOrEmpty()) {
                        viewModel.menuTitle.value =
                            "Menu for ${viewModel.studentName.value}"
                        productGroupList =
                            if (it.value.response.raws.data.menutemplateDetails.isNotEmpty()) {

                                viewModel.menuTemplateName.value =
                                    it.value.response.raws.data.menutemplateDetails[0].menuTemplateName

                                val items =
                                    getFilteredProductGroupListDayWise(it.value.response.raws.data.menutemplateDetails[0].productGroup)

                                items?.forEach { productGroupItem ->
                                    productGroupItem?.product?.forEach { productItem ->
                                        productItem?.isOrdered = STATUS_ZERO

                                        if (weekDay == STATUS_ONE && productItem?.mondayOrder == STATUS_ONE)
                                            productItem.isOrdered = STATUS_ONE
                                        else if (weekDay == STATUS_TWO && productItem?.tuesdayOrder == STATUS_ONE)
                                            productItem.isOrdered = STATUS_ONE
                                        else if (weekDay == STATUS_THREE && productItem?.wednesdayOrder == STATUS_ONE)
                                            productItem.isOrdered = STATUS_ONE
                                        else if (weekDay == STATUS_FOUR && productItem?.thursdayOrder == STATUS_ONE)
                                            productItem.isOrdered = STATUS_ONE
                                        else if (weekDay == STATUS_FIVE && productItem?.fridayOrder == STATUS_ONE)
                                            productItem.isOrdered = STATUS_ONE

                                    }
                                    productGroupItem?.selectedChildItemNumber =
                                        productGroupItem?.product?.filter { product -> product?.isOrdered == 1 }?.size
                                            ?: 0
                                }

                                items
                            } else
                                ArrayList()


                        viewModel.dayName.value = MethodClass.getShortFormOfDay(Constants.DAY_NAME)


                        //get and set total calorie and total sugar qty
                        viewModel.totalCalorieStr.value =
                            it.value.response.raws.data.menutemplateDetails[0].totalCalories.toString()
                        viewModel.totalSugarStr.value =
                            it.value.response.raws.data.menutemplateDetails[0].totalSugar.toString()
                        templatePrice =
                            it.value.response.raws.data.menutemplateDetails[0].menuTemplatePrice?.toFloat()
                                ?: 0F
                        templateXpPoint =
                            it.value.response.raws.data.menutemplateDetails[0].menuTemplateXpPoints
                                ?: 0

                        //set total amount with 2 decimal places format
                        // viewModel.totalAmt.value = "€${String.format("%.2f", templatePrice)}"
                        adapterNotifyDataSetChanged()
                    }
                    //MethodClass.hideProgressDialog(this)
                    loader.dismiss()
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {

                        MethodClass.hideProgressDialog(this)
                        it.errorString?.let { it1 ->
                            if (it.errorCode == 401) {
                                MethodClass.hideProgressDialog(this)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.PRODUCT_LIST_BY_MENU_TEMPLATE,
                                        Constants.REFRESH_TOKEN
                                    )
                            } else if (it.errorCode == 307 || it.errorCode == 426)
                                MethodClass.showErrorDialog(this, it1, it.errorCode)

                        }


                    }
                    //MethodClass.hideProgressDialog(this)
                    loader.dismiss()
                }

                else -> {}
            }


        }
    }

      
    @SuppressLint("UseCompatLoadingForDrawables")
    fun next() {
        try {
            //check if any item selected or not from food list of a particular ProductGroup
            //Here selectedItemNumber is the number of selection of food item of a particular ProductGroup
            if ((productGroupList?.get(selectedGroupPosition)?.selectedChildItemNumber
                    ?: 0) > 0
            ) {

                //check last Product group
                if (selectedGroupPosition == (productGroupList?.size?.minus(1))) {
                    //      showSaveOrderDialog()

                    orderWeekDay = MethodClass.getDayNameOfIntegerDayValue(weekDay)
                    //this popup will open when click on next button one api will be call in this api success part this popup will open
                    singleDayOrderDateDisplay()
                } else {
                    //increasing the selected group position
                    selectedGroupPosition += 1
                    //Refreshing the ProductGroup adapter
                    productGroupAdapter?.itemCount?.let {
                        productGroupAdapter?.notifyItemRangeChanged(
                            0,
                            it
                        )
                    }

                    binding.productGroupRv.smoothScrollToPosition(selectedGroupPosition)

                    //set next button
                    setNextButtonText(selectedGroupPosition, productGroupList)

                    //set product child adapter
                    setChildProductAdapter()

                    /*previousRelativeLayout?.visibility = View.VISIBLE
                    //change the next layout background
                    if ((productGroupList?.get(selectedGroupPosition)?.selectedChildItemNumber
                            ?: 0) > 0
                    )
                        nextRelativeLayout?.background =
                            productActivity.resources.getDrawable(
                                R.drawable.capsule_shaped_add_children_edittext_bg_green,
                                null
                            )
                    else
                        nextRelativeLayout?.background =
                            productActivity.resources.getDrawable(
                                R.drawable.capsule_shaped_add_children_edittext_bg_light_green,
                                null
                            )*/


                }


            } else {
                showMinimumOneProductAddDialog(productGroupList?.get(selectedGroupPosition)?.productGroupName)
            }
        } catch (_: Exception) {
        }

    }

    /* @SuppressLint("UseCompatLoadingForDrawables")
     fun previous() {

         //decreasing the selected group position
         selectedGroupPosition -= 1
         //Refreshing the ProductGroup adapter
         productGroupAdapter?.itemCount?.let { productGroupAdapter?.notifyItemRangeChanged(0, it) }

         //If first productGroup is visible then the back layout will be hidden
         if (selectedGroupPosition == 0)
             previousRelativeLayout?.visibility = View.GONE

         //check if any item selected or not from food list of a particular ProductGroup
         //Here selectedItemNumber is the number of selection of food item of a particular ProductGroup
         if ((productGroupList?.get(selectedGroupPosition)?.selectedChildItemNumber
                 ?: 0) > 0
         )
             nextRelativeLayout?.background = .resources.getDrawable(
                 R.drawable.capsule_shaped_add_children_edittext_bg_green,
                 null
             )
         else
             nextRelativeLayout?.background = .resources.getDrawable(
                 R.drawable.capsule_shaped_add_children_edittext_bg_light_green,
                 null
             )

     }*/

    private fun groupItemClick(groupPosition: Int) {

        if (groupPosition == selectedGroupPosition) {
            return
        } else if (groupPosition > selectedGroupPosition) {//check the clicked position is greater than already selected position
            //checked if the next item's selection count is zero or not
            if ((productGroupList?.get(selectedGroupPosition)?.selectedChildItemNumber
                    ?: 0) > 0
            ) {

                //check the current  clicked group's selected child products count
                if ((productGroupList?.get(groupPosition)?.selectedChildItemNumber
                        ?: 0) > 0
                ) {
                    //set the selected group position after clicking on the group item
                    selectedGroupPosition = groupPosition

                    //Refreshing the ProductGroup adapter
                    productGroupAdapter?.itemCount?.let {
                        productGroupAdapter?.notifyItemRangeChanged(
                            0,
                            it
                        )
                    }

                    binding.productGroupRv.smoothScrollToPosition(selectedGroupPosition)

                    //set next button
                    setNextButtonText(selectedGroupPosition, productGroupList)

                    //set product child adapter
                    setChildProductAdapter()
                } else {
                    //showSelectNextButtonDialog(productGroupList?.get(groupPosition)?.productGroupName)
                    showSelectNextButtonDialog(productGroupList?.get(selectedGroupPosition)?.productGroupName)
                }
            } else {
                showMinimumOneProductAddDialog(productGroupList?.get(selectedGroupPosition)?.productGroupName)
            }


        } else {
            //set the selected group position after clicking on the group item
            selectedGroupPosition = groupPosition

            //Refreshing the ProductGroup adapter
            productGroupAdapter?.itemCount?.let {
                productGroupAdapter?.notifyItemRangeChanged(
                    0,
                    it
                )
            }
            binding.productGroupRv.smoothScrollToPosition(selectedGroupPosition)

            //set next button
            setNextButtonText(selectedGroupPosition, productGroupList)

            //set product child adapter
            setChildProductAdapter()
        }
    }


    fun singleDayOrderDateDisplay() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        MethodClass.showProgressDialog(this)
        val jsonObject = MethodClass.getCommonJsonObject(this)
        jsonObject.addProperty("user_type", viewModel.userType.value)
        jsonObject.addProperty("week_day", orderWeekDay)
        jsonObject.addProperty("order_date", ORDER_DATE)
        viewModel.singleDayOrderDateDisplay(jsonObject, Constants.TOKEN)
        MethodClass.showProgressDialog(this)
    }

      
    fun getSingleDayOrderDateDisplayResponse() {
        viewModel.singleDayOrderDateDisplayResponse.observe(this) {

            when (it) {
                is Resource.Success -> {
                    orderNextDate = it.value.response.raws.data.orderNextDate
                    orderNextDay = it.value.response.raws.data.orderNextDay
                    orderNextWeekDay = it.value.response.raws.data.orderNextWeekDay
                    firstOrder = it.value.response.raws.data.firstOrder

                    if (schoolType == STATUS_ONE || (schoolType == STATUS_TWO && viewModel.userType.value.equals(
                            TEACHER,
                            true
                        ))
                    ) {
                        var productName=""
                        if (from.equals(FAVORITES_ACTIVITY,true)) {
                            productName=selectedFavoriteProductName
                        }
                        else{
                            productGroupList?.forEach{ productGroup->
                                productGroup?.product?.forEach{ productItem->
                                    if (productItem?.isOrdered== STATUS_ONE) {
                                        productName += "${productItem.productName}, "
                                    }
                                }
                            }
                            productName= productName.substring(STATUS_ZERO, productName.lastIndexOf(COMMA_TAG))
                        }

                        orderReviewPopup(orderNextDate,
                            orderNextDay,
                            orderNextWeekDay,
                            productName)
                        MethodClass.hideProgressDialog(this)
                    } else {
                        orderType = REPEAT
                        saveMenu()
                    }


                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(this)
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                MethodClass.hideProgressDialog(this)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.SINGLE_DAY_ORDER_DATE_DISPLAY,
                                        Constants.REFRESH_TOKEN
                                    )
                            }
                            it.errorString.let { it1 ->
                                MethodClass.hideProgressDialog(this)
                                MethodClass.showErrorDialog(
                                    this,
                                    it1,
                                    it.errorCode
                                )

                            }


                        }


                    }
                    MethodClass.hideProgressDialog(this)
                }


            }
        }
    }


      
    fun orderReviewPopup(
        orderNextDate: String,
        order_next_date: String,
        order_next_week_date: String,
        productName:String
    ) {
        binding.root.context?.let { ctx->
        orderDialog = Dialog(ctx)
        orderDialog?.window?.setBackgroundDrawableResource(R.color.white)
        val binding: MenuOrderReviewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(ctx),
            R.layout.menu_order_review,
            null,
            false
        )
        orderDialog?.setContentView(binding.root)
        val width = (resources.displayMetrics.widthPixels)
        val height = (resources.displayMetrics.heightPixels)
        orderDialog?.window?.setLayout(width, height)
        orderDialog?.setCancelable(false)
        binding.ivBack.setOnClickListener {
            orderDialog?.dismiss()
//            val intent =
//                Intent(this@ProductActivity, DashBoardActivity::class.java)
//            intent.flags =
//                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
        }
        binding.tvProductName.text = productName
        binding.tvDayName
        binding.tvDayName.text =getString(R.string.repeat_order_every, orderNextDate)

        binding.tvCurrentDayName.text =getString(R.string.order_for, orderNextDate ,order_next_date)
            binding.tvDayDetails.text = getString(R.string.we_will_remember_to_deliver_lunches_each_thursday_until_you_edit_canel_them, orderNextDate)
        binding.tvCurrentDayDetails.text =getString(R.string.one_time_order_you_will_remember_to_re_order_lunches, orderNextDate)
        binding.tvDayName.setOnClickListener {
            orderType = REPEAT
            saveMenu()
        }
        binding.tvCurrentDayName.setOnClickListener {
            orderType = SINGLE
            saveMenu()
        }
        orderDialog?.show()
    }
    }

      
    @SuppressLint("UseCompatLoadingForDrawables", "LongLogTag", "NotifyDataSetChanged")
    fun adapterNotifyDataSetChanged() {
        //get previous whole list
        val getPreviousTotalList = productGroupAdapter?.productGroupList

        // if count is zero then it means the list set for first time
        if (getPreviousTotalList?.isNotEmpty() == true)
            productGroupList = getPreviousTotalList


        productGroupAdapter?.productGroupList =
            productGroupList
        productGroupAdapter?.notifyDataSetChanged()

        //set next button
        setNextButtonText(selectedGroupPosition, productGroupList)

        //set product child adapter
        setChildProductAdapter()



        Handler(Looper.getMainLooper()).postDelayed({
            /*try {
                //change the next layout background
                if (productGroupList?.get(selectedGroupPosition).selectedChildItemNumber > 0)
                    nextRelativeLayout?.background = resources.getDrawable(
                        R.drawable.capsule_shaped_add_children_edittext_bg_green,
                        null
                    )
                else
                    nextRelativeLayout?.background = resources.getDrawable(
                        R.drawable.capsule_shaped_add_children_edittext_bg_light_green,
                        null
                    )
            } catch (e: Exception) {
            }*/
            allPriceTotal = 0f
            allXpPoints = 0

            //calculate TotalPrice
            calculateTotalPriceAndXpPoints(productGroupList)


            val totalPrice = templatePrice + allPriceTotal

            viewModel.totalAmt.value = "€${String.format("%.2f", totalPrice)}"
            viewModel.totalXpPointsTxt.value = (templateXpPoint + allXpPoints).toString()


            //get selected all group product id list
            selectedProductList =
                getSelectedProductIdList(productGroupList)

            Log.e("selectedProductList", selectedProductList.toString())

            /*if (selectedProductList.size() > 0) {
                //call calorieMeter api
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    getCalorieMeter()
                }
            }*/

            //get selected all product group list
            selectedProductGroupList =
                getSelectedProductGroupIdWithProductIdList(productGroupList)
        }, 300)


    }

    private fun calculateTotalPriceAndXpPoints(productGroupList: List<ProductGroupItem?>?) {

        productGroupList?.forEach { productGroupItem ->
            productGroupItem?.product?.forEach { productItem ->
                if (productItem?.isOrdered == 1) {
                    var additionalPriceFloat = 0f
                    var xpPoint = STATUS_ZERO
                    try {
                        additionalPriceFloat = productItem.additionalPrice?.toFloat() ?: 0F
                    } catch (_: Exception) {
                    }
                    productItem.xpPoints?.let {
                        xpPoint = it
                    }

                    allPriceTotal += additionalPriceFloat
                    allXpPoints += xpPoint

                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMinimumOneProductAddDialog(productGroupName: String?) {
        if (minimumProductAddDialog != null) {
            minimumProductAddDialog?.dismiss()
        }

        minimumProductAddDialog = Dialog(this@ProductActivity)
        minimumProductAddDialog?.window?.let { window ->
            window.setBackgroundDrawableResource(R.color.transparent)
            window.decorView.setBackgroundResource(android.R.color.transparent)
            window.setDimAmount(0.0f)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val dialogBinding =
            SelectMinimumOneProductDialogBinding.inflate(LayoutInflater.from(this@ProductActivity))
        minimumProductAddDialog?.setContentView(dialogBinding.root)
        minimumProductAddDialog?.setCancelable(false)
//        contentTv?.text =
//            "${getString(R.string.please_select_at_least_one_food_msg)} $productGroupName"

        dialogBinding.btnOk.setOnClickListener {
            minimumProductAddDialog?.dismiss()

        }
        minimumProductAddDialog?.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showSelectNextButtonDialog(clickedProductGroupName: String?) {
        if (selectNextButtonDialog != null) {
            selectNextButtonDialog?.dismiss()
        }

        selectNextButtonDialog = Dialog(this)
        selectNextButtonDialog?.window?.let { window ->
            window.setBackgroundDrawableResource(R.color.transparent)
            window.decorView.setBackgroundResource(android.R.color.transparent)
            window.setDimAmount(0.0f)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val dialogBinding =
            SelectNextButtonDialogBinding.inflate(LayoutInflater.from(this@ProductActivity))
        selectNextButtonDialog?.setContentView(dialogBinding.root)
        selectNextButtonDialog?.setCancelable(false)
//        if (clickedProductGroupName.isNullOrBlank())
//            contentTv?.text =
//                getString(R.string.next_button_click_msg)
//        else
//            contentTv?.text =
//                "You can not select this product group because you have not added any product yet in $clickedProductGroupName group. Please click on next button."

        //dialogBinding.tvDescription.text = getString(R.string.next_button_click_msg, clickedProductGroupName)
        dialogBinding.tvDescription.text = getString(R.string.next_button_click_msg2)
        dialogBinding.btnOk.setOnClickListener {
            selectNextButtonDialog?.dismiss()

        }
        selectNextButtonDialog?.show()
    }


    private fun getSelectedProductIdList(productGroupList: List<ProductGroupItem?>?): JsonArray {
        val selectedProductList = JsonArray()
        productGroupList?.forEach { productGroupItem ->
            productGroupItem?.product?.forEach { productItem ->
                if (productItem?.isOrdered == STATUS_ONE)
                    selectedProductList.add(productItem.productId)
            }
        }
        return selectedProductList

    }

    private fun getSelectedProductGroupIdWithProductIdList(productGroupList: List<ProductGroupItem?>?): JsonArray {
        val selectedProductGroupList = JsonArray()
        productGroupList?.forEach { productGroupItem ->
            val selectedProductList = JsonArray()
            val jsonObject = JsonObject()
            productGroupItem?.product?.forEach { productItem ->
                if (productItem?.isOrdered == STATUS_ONE)
                    selectedProductList.add(productItem.productId)
            }
            if (selectedProductList.size() > STATUS_ZERO) {
                jsonObject.addProperty("product_group_id", productGroupItem?.productGroupId)
                jsonObject.add("product_id", selectedProductList)
                selectedProductGroupList.add(jsonObject)
            }
        }
        return selectedProductGroupList

    }


    private fun getFilteredProductGroupListDayWise(productGroupList: List<ProductGroupItem?>?): List<ProductGroupItem?>? {
        val filteredProductGroupList: List<ProductGroupItem?>? = productGroupList
        if (productGroupList?.isNotEmpty() == true) {
            for (i in productGroupList.indices) {
                val filteredProductList: MutableList<ProductItem> = ArrayList()

                val productGroupItem: ProductGroupItem? = productGroupList[i]
                for (j in productGroupItem?.product!!.indices) {
                    val productItem: ProductItem? = productGroupItem.product!![j]
                    if (weekDay == STATUS_ONE && productItem?.monday == STATUS_ONE && productItem.isAllergen == STATUS_ZERO)
                        filteredProductList.add(productItem)
                    else if (weekDay == STATUS_TWO && productItem?.tuesday == STATUS_ONE && productItem.isAllergen == STATUS_ZERO)
                        filteredProductList.add(productItem)
                    else if (weekDay == STATUS_THREE && productItem?.wednesday == STATUS_ONE && productItem.isAllergen == STATUS_ZERO)
                        filteredProductList.add(productItem)
                    else if (weekDay == STATUS_FOUR && productItem?.thursday == STATUS_ONE && productItem.isAllergen == STATUS_ZERO)
                        filteredProductList.add(productItem)
                    else if (weekDay == STATUS_FIVE && productItem?.friday == STATUS_ONE && productItem.isAllergen == STATUS_ZERO)
                        filteredProductList.add(productItem)

                }
                filteredProductGroupList?.get(i)?.product = filteredProductList
            }
        }


        return filteredProductGroupList
    }



      
    fun saveMenu() {
        //MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        /*CustomDialog.orderStatusDialog(this@ProductActivity,
            getString(R.string.order_under_process),
            getString(R.string.please_don_t_go_back_or_refresh),
            STATUS_ZERO,
            object : DialogOrderStatusListener {
                override fun okOnClick(dialog: Dialog, pageRedirection: Int) {
                    paymentProcessingDialog = dialog
                }
            })*/
        orderStatusDialog(STATUS_ZERO)
        /*CustomDialog.orderStatusDialog(this@ProductActivity,
            STATUS_ZERO,
            object : DialogOrderStatusListener {
                override fun okOnClick(dialog: Dialog, pageRedirection: Int) {
                    paymentProcessingDialog = dialog
                }
            })*/
        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)

        /*if (viewModel.userType.value.equals(STUDENT, true)) {
            jsonObject.addProperty("user_type", STUDENT_CODE)
        } else
            jsonObject.addProperty("user_type", TEACHER_CODE)*/
        jsonObject.addProperty("user_type", viewModel.userType.value)
        if (orderType.equals(REPEAT, true)) {
            jsonObject.addProperty("order_type", STATUS_ONE)
        } else
            jsonObject.addProperty("order_type", STATUS_ZERO)
        jsonObject.addProperty("dashboard_id", DASHBOARD_ID)
        jsonObject.addProperty("order_date", ORDER_DATE)

        jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
        //  jsonObject.addProperty("order_type", orderType)
        //   jsonObject.addProperty("template_id", viewModel.menuTemplateId.value)
        jsonObject.addProperty("sublunch_menu_id", viewModel.menuTemplateId.value)
        //jsonObject.add("product", selectedProductList)
        jsonObject.add("product", selectedProductGroupList)
        jsonObject.addProperty("menu_id", viewModel.menuId.value)
        jsonObject.addProperty("week_day", weekDay)
        jsonObject.addProperty("favourite_order_id", selectedFavoritesId)
        //Log.e("saveMenu: ", jsonObject.toString())
        //"product": [{"product_group_id":1,"product_id":[1,2]}, {"product_group_id":2,"product_id":[3,4]}]
        viewModel.saveOrder(jsonObject, Constants.TOKEN)
        //viewModel.saveOrderResponseApi(jsonObject, Constants.TOKEN)


    }


      
    fun getSaveMenuForSingleDayResponse() {
        viewModel.saveOrderResponse?.observe(this) {

            when (it) {
                is Resource.Success -> {
                    // MethodClass.hideProgressDialog(this)
                    paymentProcessingDialog?.dismiss()
                    repeatRl?.isEnabled = true
                    rlEveryDay?.isEnabled = true
                    //getStudentAllowedOrder()
                    DASHBOARD_ID = it.value.response.raws.data.dashboardId
                    val pageRedirection = it.value.response.raws.data.pageRedirection
                    lifecycleScope.launch{
                        delay(1000L)
                        orderStatusDialog(pageRedirection)
                    }

                    Constants.CURRENT_ORDER_DAY =ORDER_DATE
                    viewModel._saveOrderResponse.value = null
                    viewModel._saveOrderResponse = viewModel._saveOrderResponse
                }
                is Resource.Failure -> {
                    repeatRl?.isEnabled = true
                    rlEveryDay?.isEnabled = true
                    orderProcessFragmentDialog?.dismiss()
                    paymentProcessingDialog?.dismiss()
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(this)
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                MethodClass.hideProgressDialog(this)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.SAVE_MENU_FOR_SINGLE_DAY,
                                        Constants.REFRESH_TOKEN
                                    )
                            }
                            it.errorString.let { it1 ->
                                MethodClass.hideProgressDialog(this)
                                MethodClass.showErrorDialog(
                                    this,
                                    it1,
                                    it.errorCode
                                )

                            }
                        }

                    }
                    viewModel._saveOrderResponse.value = null
                    viewModel._saveOrderResponse = viewModel._saveOrderResponse
                    MethodClass.hideProgressDialog(this)
                }

                else -> {}
            }


        }
    }


      
    fun paymentProcessing() {
        // MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )
        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)

        jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
        jsonObject.addProperty("order_dashboard_id", DASHBOARD_ID)
        jsonObject.addProperty("user_type", viewModel.userType.value)
        viewModel.paymentProcessingResponse(jsonObject, Constants.TOKEN)
        paymentProcessingResponse()
    }


      
    fun paymentProcessingResponse() {
        viewModel.getPaymentProcessingResponse?.observe(this@ProductActivity) {
            when (it) {
                is Resource.Success -> {
                    //  MethodClass.hideProgressDialog(this)

                        paymentProcessingDialog?.dismiss()
                        if(it.value.response.raws.data.status== STATUS_ONE){
                            orderStatusDialog(STATUS_ONE)
                        }
                        else{
                            orderStatusDialog(STATUS_SIX)
                        }

                    viewModel._getPaymentProcessingResponse.value = null
                    viewModel.getPaymentProcessingResponse =
                        viewModel._getPaymentProcessingResponse

                }
                is Resource.Failure -> {
                    paymentProcessingDialog?.dismiss()
                    if (it.errorBody != null) {

                        MethodClass.hideProgressDialog(this)
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                MethodClass.hideProgressDialog(this)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.STUDENT_ALLOWED_ORDER,
                                        Constants.REFRESH_TOKEN
                                    )
                            }
                            it.errorString.let { it1 ->
                                MethodClass.hideProgressDialog(this)
                                MethodClass.showErrorDialog(
                                    this,
                                    it1,
                                    it.errorCode
                                )

                            }
                        }
                        viewModel._getPaymentProcessingResponse.value = null
                        viewModel.getPaymentProcessingResponse =
                            viewModel._getPaymentProcessingResponse

                    }
                    MethodClass.hideProgressDialog(this)

                }

                else -> {}
            }


        }
    }


    fun getStudentAllowedOrder() {
        MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)

        jsonObject.addProperty("week_day", weekDay)
        jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
        viewModel.studentAllowedOrder(jsonObject, Constants.TOKEN)
    }


      
    fun studentAllowedOrderResponse() {
        viewModel.getStudentAllowedOrderResponse?.observe(this) {
            when (it) {
                is Resource.Success -> {
                    hasOrder = it.value.response.raws.data.order
                    if (firstOrder == 0 &&
                        (schoolType == 1 || (schoolType == 2 && viewModel.userType.value.equals(
                            TEACHER,
                            true
                        )))
                    ) {
                        CustomDialog.popupOnce(this, this)

                    } else {
                        if (schoolType == 2 && viewModel.userType.value.equals(
                                STUDENT,
                                true
                            )
                        ) {
                            CustomDialog.showOkTypeDialog(
                                this,
                                getString(R.string.your_order_has_been_placed_successfully),
                                object : DialogOkListener {
                                    override fun okOnClick(dialog: Dialog, activity: Activity) {
                                        goToNextPageAfterSavingOrder(it.value.response.raws.data.order)
                                    }
                                })
                        } else {
                            goToNextPageAfterSavingOrder(it.value.response.raws.data.order)
                        }

                    }



                    viewModel._getStudentAllowedOrderResponse.value = null
                    viewModel.getStudentAllowedOrderResponse =
                        viewModel._getStudentAllowedOrderResponse
                    MethodClass.hideProgressDialog(this)
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {

                        MethodClass.hideProgressDialog(this)
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                MethodClass.hideProgressDialog(this)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.STUDENT_ALLOWED_ORDER,
                                        Constants.REFRESH_TOKEN
                                    )
                            }
                            it.errorString.let { it1 ->
                                MethodClass.hideProgressDialog(this)
                                MethodClass.showErrorDialog(
                                    this,
                                    it1,
                                    it.errorCode
                                )

                            }


                        }
                        viewModel._getStudentAllowedOrderResponse.value = null
                        viewModel.getStudentAllowedOrderResponse =
                            viewModel._getStudentAllowedOrderResponse

                    }
                    MethodClass.hideProgressDialog(this)
                }

                else -> {}
            }


        }
    }


      
    fun productInfoDetailsApiCall(productId: Int?) {
        MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)

        jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
        jsonObject.addProperty("product_id", productId)
        /* if (viewModel.userType.value.equals(STUDENT, true)) {
             jsonObject.addProperty("user_type", STUDENT_CODE)
         } else
             jsonObject.addProperty("user_type", TEACHER_CODE)*/

        jsonObject.addProperty("user_type", viewModel.userType.value)

        viewModel.productInfoDetailsApi(jsonObject, Constants.TOKEN)
    }

      
    fun getProductInfoDetailsResponse() {
        viewModel.productInfoDetailsResponse?.observe(this) {
            when (it) {
                is Resource.Success -> {
                    showProductInfoDialog(
                        this,
                        it.value.response.raws.data
                    )

                    viewModel._productInfoDetailsResponse.value = null
                    viewModel.productInfoDetailsResponse =
                        viewModel._productInfoDetailsResponse
                    MethodClass.hideProgressDialog(this)
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {

                        MethodClass.hideProgressDialog(this)
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                MethodClass.hideProgressDialog(this)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.STUDENT_ALLOWED_ORDER,
                                        Constants.REFRESH_TOKEN
                                    )
                            }
                            it.errorString.let { it1 ->
                                MethodClass.hideProgressDialog(this)
                                MethodClass.showErrorDialog(
                                    this,
                                    it1,
                                    it.errorCode
                                )

                            }


                        }
                        viewModel._productInfoDetailsResponse.value = null
                        viewModel.productInfoDetailsResponse =
                            viewModel._productInfoDetailsResponse

                    }
                    MethodClass.hideProgressDialog(this)
                }

                else -> {}
            }


        }
    }

      
    override fun yesOnClick(dialog: Dialog, activity: Activity) {
        repeatRl = dialog.findViewById<RelativeLayout>(R.id.rl_current_day)
        rlEveryDay = dialog.findViewById<RelativeLayout>(R.id.rl_every_day)
        orderType = REPEAT
        saveMenu()
    }

      
    override fun noOnClick(dialog: Dialog, activity: Activity) {
        //dialog.dismiss()
        orderType = SINGLE
        saveMenu()
    }

    override fun onceClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
        if (hasOrder == "Yes") {
            val intent = Intent(
                this,
                WalletViewActivity::class.java
            )
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            if (Objects.equals(from, "sideDrawer"))
                Constants.SELECTED_ORDER_POSITION =
                    Constants.SELECTED_ORDER_POSITION_FROM_SIDE_DRAWER
            else if (Objects.equals(from, "calendar")) {
                CURRENT_DAY = 1
                for (i in Constants.ORDER_LIST.indices) {
                    if (Objects.equals(
                            Constants.ORDER_LIST[i].day?.lowercase(),
                            Constants.DAY_NAME.lowercase()
                        )
                        && Objects.equals(Constants.ORDER_LIST[i].dayOff?.lowercase(), "no")
                    ) {

                        Constants.DAY_NAME = Constants.ORDER_LIST[i].day.toString()
                        Constants.SELECTED_ORDER_POSITION = i
                        break
                    }
                }

            }


            val intent =
                Intent(this, DashBoardActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
    }


    private fun registerOnPageChangeCallbackOnStudentViewPager() {
        binding.studentViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            @SuppressLint("NotifyDataSetChanged")
              
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {
                    viewModel.studentName.value =
                        viewModel.studentList[position].fName
                    viewModel.userType.value = viewModel.studentList[position].user_type

                } catch (_: Exception) {
                }
                Constants.SELECTED_STUDENT_ID =
                    viewModel.studentList[position].studentid
                Constants.SELECTED_STUDENT_POSITION = position
                schoolType = viewModel.studentList[position].schoolType
                viewModel.userType.value= viewModel.studentList[position].user_type

                viewModel.isXpLayoutVisible.value =
                    viewModel.studentList[Constants.SELECTED_STUDENT_POSITION].schoolType == STATUS_ONE
                            && viewModel.userType.value.equals(STUDENT, true)

                if (from.equals(FAVORITES_ACTIVITY,true)){
                    favoritesAdapter.favouriteOrder=ArrayList()
                    favoritesAdapter.notifyDataSetChanged()
                    favoritesAdapter.xpLayoutVisible=viewModel.isXpLayoutVisible.value

                    // When student change then 'selectedFavouriteOrdersList' variable need to initialize with 'null'
                    selectedFavouriteOrdersList=null
                    getFavoritesList()
                }
                else if (DashBoardViewModel.selectedStudentPrePosition != Constants.SELECTED_STUDENT_POSITION) {
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                }


                DashBoardViewModel.selectedStudentPrePosition =
                    position// this is because in dashboard, selectedStudentPrePosition will be same as SELECTED_STUDENT_POSITION


            }

        })

    }

    private fun setChildProductAdapter() {
        //set product child adapter
        val childAdapter =
            viewModel.isXpLayoutVisible.value?.let { isXpLayoutVisible ->
                ProductChildAdapter(
                    this,
                    productGroupList?.get(selectedGroupPosition)?.product,
                    selectedGroupPosition,
                    productGroupList?.get(selectedGroupPosition)?.maxNumberOfProduct,
                    this,
                    isXpLayoutVisible
                )
            }
        binding.productChildRv.adapter = childAdapter
        binding.productChildRv.isFocusable = false
    }

      
    @SuppressLint("UseCompatLoadingForDrawables", "LongLogTag")
    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {


        if (tag.equals(Constants.REMOVE_FAVORITES, true)) {
            val favouriteOrdersList: List<FavouriteOrdersItem> =
                arrayList as List<FavouriteOrdersItem>

            CustomDialog.removeFromFavorites(this,
//                getString(R.string.do_you_want_to_remove_this_order_from_your_s_favourite),
//                getString(R.string.confirm),
//                getString(R.string.cancel),
                object : DialogYesNoListener {
                      
                    override fun yesOnClick(dialog: Dialog, activity: Activity) {
                        dialog.dismiss()
                        val loginResponse: LoginResponse? =
                            UserPreferences.getAsObject<LoginResponse>(
                                activity,
                                Constants.USER_DETAILS
                            )

                        loginResponse?.response?.raws?.data?.token?.let {
                            Constants.TOKEN = it
                        }
                        Constants.REFRESH_TOKEN =
                            loginResponse?.response?.raws?.data?.refreshToken.toString()
                        MethodClass.showProgressDialog(activity)
                        val jsonObject = MethodClass.getCommonJsonObject(activity)
                        jsonObject.addProperty(
                            "favourite_id",
                            favouriteOrdersList[position].id
                        )
                        viewModel.favouriteOrdersRemove(jsonObject, Constants.TOKEN)
                        getFavouriteOrdersRemoveResponse()
                    }

                    override fun noOnClick(dialog: Dialog, activity: Activity) {
                        dialog.dismiss()
                    }

                })
        }
        else if (tag.equals(Constants.FAV_ORDER_FAVORITES, true)) {
            val favouriteOrdersList: List<FavouriteOrdersItem> =
                arrayList as List<FavouriteOrdersItem>
            selectedFavouriteOrdersList=favouriteOrdersList[position]
        }


        else if (tag.equals("info", true)) {
            val productChildList: List<ProductItem> = arrayList as List<ProductItem>
            productInfoDetailsApiCall(productChildList[position].productId)
        } else {
            val productGroupList: List<ProductGroupItem> = arrayList as List<ProductGroupItem>

            if (tag.equals("child", true)) {
                //get selected all group product id list
                selectedProductList = getSelectedProductIdList(productGroupList)


                allPriceTotal = 0f
                allXpPoints = 0

                //calculate TotalPrice
                calculateTotalPriceAndXpPoints(productGroupList)

                val totalPrice = templatePrice + allPriceTotal

                viewModel.totalAmt.value = "€${String.format("%.2f", totalPrice)}"
                viewModel.totalXpPointsTxt.value = (templateXpPoint + allXpPoints).toString()
                /* //change the next layout background
         if (productGroupList[position].selectedChildItemNumber > 0)
             nextRelativeLayout?.background = productActivity.resources.getDrawable(
                 R.drawable.capsule_shaped_add_children_edittext_bg_green,
                 null
             )
         else
             nextRelativeLayout?.background = productActivity.resources.getDrawable(
                 R.drawable.capsule_shaped_add_children_edittext_bg_light_green,
                 null
             )


         if (selectedProductList.size() > 0)
         //call calorieMeter api
             getCalorieMeter()
         else {
             calorieStr.value = "0/"
             progressbarOne.progress = 0

             sugarStr.value = "0/"
             progressbarTwo.progress = 0
         }*/

                //get selected all product group list
                selectedProductGroupList =
                    getSelectedProductGroupIdWithProductIdList(productGroupList)

                Log.e("selectedProductList: ", selectedProductList.toString())
            } else {
                groupItemClick(position)
            }
        }

    }

    private fun showProductInfoDialog(
        activity: Activity,
        infoDetails: Data
    ) {
        if (!activity.isFinishing) {
            val productInfoDialog = Dialog(activity)
            productInfoDialog.window?.setBackgroundDrawableResource(R.color.transparent)

            val binding: ProductInfoPopupBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.product_info_popup,
                null,
                false
            )

            productInfoDialog.setContentView(binding.root)
            //productInfoDialog.setCancelable(false)

            productInfoDialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            binding.crossIv.setOnClickListener {
                productInfoDialog.dismiss()
            }

            infoDetails.iButtonHeading?.let { binding.productNameTv.text = it }
            infoDetails.productDesc?.let { binding.productDescTv.text = it }

            if (infoDetails.allergenList?.isNotEmpty() == true)
                binding.allergensNameTv.text = TextUtils.join(", ", infoDetails.allergenList)
            else
                binding.allergensNameTv.text = getString(R.string.no_allergen)

            Glide.with(binding.productIv.context)
                .load(infoDetails.iButtonImage)
                .placeholder(R.drawable.menu_placeholder_img)
                .error(R.drawable.menu_placeholder_img)
                .into(binding.productIv)

            infoDetails.packagingType?.let { packagingType ->
                when (packagingType) {
                    1 -> {
                        binding.packingTypeIv.visibility = View.VISIBLE
                        binding.packingTypeIv.setImageResource(R.drawable.ic_recycle)
                    }
                    2 -> {
                        binding.packingTypeIv.visibility = View.VISIBLE
                        binding.packingTypeIv.setImageResource(R.drawable.compostable_packing_icon)
                    }
                    else -> binding.packingTypeIv.visibility = View.INVISIBLE
                }

            }


            val productInfoNutritionAdapter =
                ProductInfoNutritionAdapter(infoDetails.nutritionList?.filterNot { it?.nutritionValue?.trim() == "0" || it?.nutritionValue?.trim() == "" })
            binding.nutritionRv.adapter = productInfoNutritionAdapter
            binding.nutritionRv.isFocusable = false


            productInfoDialog.show()
        }
    }

    private fun setNextButtonText(
        selectedGroupPosition: Int,
        productGroupList: List<ProductGroupItem?>?
    ) {
        if (selectedGroupPosition == (productGroupList?.size?.minus(1))
            && schoolType == 2
            && viewModel.userType.value.equals(STUDENT, true)
        )
            binding.nextBtn.text = getString(R.string.save)
        else
            binding.nextBtn.text = getString(R.string.next)

    }

    private fun goToNextPageAfterSavingOrder(isOrdered: String) {
        if (Objects.equals(
                isOrdered.trim().lowercase(),
                Constants.YES
            )
        ) {
            val intent = Intent(
                this,
                WalletViewActivity::class.java
            )
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            if (Objects.equals(from, "sideDrawer"))
                Constants.SELECTED_ORDER_POSITION =
                    Constants.SELECTED_ORDER_POSITION_FROM_SIDE_DRAWER
            else if (Objects.equals(from, "calendar")) {
                CURRENT_DAY = 1
                for (i in Constants.ORDER_LIST.indices) {
                    if (Objects.equals(
                            Constants.ORDER_LIST[i].day?.lowercase(),
                            Constants.DAY_NAME.lowercase()
                        )
                        && Objects.equals(
                            Constants.ORDER_LIST[i].dayOff?.lowercase(),
                            "no"
                        )
                    ) {
                        Constants.DAY_NAME = Constants.ORDER_LIST[i].day.toString()
                        Constants.SELECTED_ORDER_POSITION = i
                        break
                    }
                }

            }

            val intent =
                Intent(this, DashBoardActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    fun getFavouriteOrdersRemoveResponse() {
        viewModel.favouriteOrdersRemoveResponse?.observe(this) { response ->

            when (response) {
                is Resource.Success -> {

                    MethodClass.hideProgressDialog(this)
                    favoritesAdapter.favouriteOrder=ArrayList()
                    favoritesAdapter.notifyDataSetChanged()
                    getFavoritesList()
                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(this)
                    response.errorString?.let { error ->
                        MethodClass.showErrorDialog(
                            this,
                            error, response.errorCode
                        )
                    }


                    viewModel._favouriteOrdersRemoveResponse.value = null
                    viewModel.favouriteOrdersRemoveResponse =
                        viewModel._favouriteOrdersRemoveResponse
                }

                else -> {}
            }


        }


    }
    fun getFavoritesList() {
        MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)
        jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
        jsonObject.addProperty("order_date", Constants.ORDER_DATE)
        jsonObject.addProperty("user_type", viewModel.userType.value)
        viewModel.favoritesList(jsonObject, Constants.TOKEN)
        //If product page from favorites then 'Next' button will hide
        binding.nextBtn.isVisible=false
    }
      
    @SuppressLint("NotifyDataSetChanged")
    fun getFavoritesListResponse() {
        viewModel.favoritesListResponse.observe(this) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(this)
                    if (it.value.response.raws.data?.favouriteOrders?.isNotEmpty() == true) {
                        binding.tvNoFavoritesTag.isVisible=false
                        binding.productChildRv.isVisible=true
                        //If favorites list not empty then 'Next' button will show.
                        binding.nextBtn.isVisible=true
                        favoritesAdapter.xpLayoutVisible=viewModel.isXpLayoutVisible.value
                        favoritesAdapter.favouriteOrder =
                            it.value.response.raws.data.favouriteOrders
                        favoritesAdapter.notifyDataSetChanged()
                    }
                    else{
                        binding.productChildRv.isVisible=false
                        binding.tvNoFavoritesTag.isVisible=true
                        //If favorites list empty then 'Next' button will hide.
                        binding.nextBtn.isVisible=false
                        noFavoritesInfoDialog()
                    }

                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.FAVORITES_LIST,
                                        Constants.REFRESH_TOKEN
                                    )
                            } else {
                                it.errorString.let { it1 ->
                                    MethodClass.showErrorDialog(
                                        this,
                                        it1,
                                        it.errorCode
                                    )

                                }
                            }


                        }


                    }
                    MethodClass.hideProgressDialog(this)
                }

            }


        }
    }

    private fun noFavoritesInfoDialog() {
        binding.root.context?.let { ctx ->
            CustomDialog.noFavoritesInfoDialog(
                ctx,
                object : DialogOkListener {
                    override fun okOnClick(dialog: Dialog, activity: Activity) {
                        dialog.dismiss()
                        finish()
                    }
                })
        }
    }

      
    fun orderStatusDialog(status: Int) {
        viewModel.orderStatus.value=status
       // viewModel.orderStatus.value=0
        when(viewModel.orderStatus.value) {
            STATUS_ZERO -> {
                viewModel.title.value=getString(R.string.order_under_process)
                viewModel.status_message_2.value=getString(R.string.please_don_t_go_back_or_refresh)
            }
            STATUS_ONE->{
                viewModel.title.value=getString(R.string.order_placed_with_exclamatory_sign)
                viewModel.status_message_1.value=getString(R.string.transaction_successful)
                viewModel.button_text.value=getString(R.string.view_my_order)
            }
            STATUS_TWO->{
                viewModel.title.value=getString(R.string.order_placed)
                viewModel.status_message_1.value=getString(R.string.payment_under_process)
                viewModel.status_message_2.value=getString(R.string.please_don_t_go_back_or_refresh)
                lifecycleScope.launch{
                    delay(2000L)
                    paymentProcessing()
                }
            }

            STATUS_THREE->{
                viewModel.title.value=getString(R.string.order_not_complete)
                viewModel.status_message_1.value=getString(R.string.transaction_successful)
                viewModel.button_text.value=getString(R.string.go_to_wallet)
                viewModel.status_message_2.value=getString(R.string.Please_top_up_your_account_to_ensure_your_order_is_processed)
            }
           /* STATUS_FOUR->{
                viewModel.title.value=getString(R.string.order_placed)
                viewModel.status_message_1.value=getString(R.string.order_will_process_at_12_noon)
                viewModel.button_text.value=getString(R.string.view_my_order)
            }*/
            STATUS_FIVE->{
                viewModel.title.value=getString(R.string.school_calendar_switched_off)
                viewModel.status_message_1.value=getString(R.string.cannot_place_order)
                viewModel.button_text.value=getString(R.string.go_back)
            }
            STATUS_SIX->{
                viewModel.title.value=getString(R.string.order_pending)
                viewModel.status_message_1.value=getString(R.string.transaction_failed)
                viewModel.status_message_2.value=getString(R.string.please_wait_5_minutes_if_wallet_doesn_t_top_up_please_try_again)
                viewModel.button_text.value=getString(R.string.view_my_order)
            }


        }
        if(orderProcessFragmentDialog?.isVisible==false) {
            orderProcessFragmentDialog?.show(supportFragmentManager, ORDER_PROCESS_DIALOG)
        }


    }


    }