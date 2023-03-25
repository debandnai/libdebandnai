package ie.healthylunch.app.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonArray
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.DashBoardCalenderAdapter
import ie.healthylunch.app.adapter.OrderPagerAdapter
import ie.healthylunch.app.adapter.StudentListPagerAdapter
import ie.healthylunch.app.data.model.dashBoardViewResponseModel.DashboardListItem
import ie.healthylunch.app.data.model.dashBoardViewResponseModel.Data
import ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel.CalendarItem
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.DashBoardRepository
import ie.healthylunch.app.data.viewModel.DashBoardViewModel
import ie.healthylunch.app.databinding.*
import ie.healthylunch.app.ui.*
import ie.healthylunch.app.ui.DashBoardActivity.Companion.dashBoardHelpLayout
import ie.healthylunch.app.ui.DashBoardActivity.Companion.dashBoardNotificationLayout
import ie.healthylunch.app.ui.DashBoardActivity.Companion.dashBoardToolbar
import ie.healthylunch.app.ui.DashBoardActivity.Companion.dashBoardWalletLayout
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.ADD_FAVORITES
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_END_DATE_TAG
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_OFF
import ie.healthylunch.app.utils.Constants.Companion.CURRENT_DAY
import ie.healthylunch.app.utils.Constants.Companion.CURRENT_ORDER_DAY
import ie.healthylunch.app.utils.Constants.Companion.DASHBOARD_ID
import ie.healthylunch.app.utils.Constants.Companion.DASHBOARD_ORDER
import ie.healthylunch.app.utils.Constants.Companion.DAY_NAME
import ie.healthylunch.app.utils.Constants.Companion.FAVORITES_ACTIVITY
import ie.healthylunch.app.utils.Constants.Companion.FOOD_ITEMS
import ie.healthylunch.app.utils.Constants.Companion.FROM_TAG
import ie.healthylunch.app.utils.Constants.Companion.NO
import ie.healthylunch.app.utils.Constants.Companion.ONE
import ie.healthylunch.app.utils.Constants.Companion.ORDER_CLEAR
import ie.healthylunch.app.utils.Constants.Companion.ORDER_DATE
import ie.healthylunch.app.utils.Constants.Companion.ORDER_VALUE
import ie.healthylunch.app.utils.Constants.Companion.PARENT_ID
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.REMOVE_FAVORITES
import ie.healthylunch.app.utils.Constants.Companion.REPEAT
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_CALENDAR_POSITION
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_ORDER_POSITION
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_STUDENT_ID
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_LIST
import ie.healthylunch.app.utils.Constants.Companion.USER_DETAILS
import ie.healthylunch.app.utils.Constants.Companion.USER_NAME
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE
import ie.healthylunch.app.utils.Constants.Companion.WEEK_NAME
import ie.healthylunch.app.utils.Constants.Companion.YES
import ie.healthylunch.app.utils.Constants.Companion.dayFormat
import ie.healthylunch.app.utils.Constants.Companion.dayFormatDay
import ie.healthylunch.app.utils.Constants.Companion.dayFormatMonth
import ie.healthylunch.app.utils.Constants.Companion.dayFormatYearMonthDay
import ie.healthylunch.app.utils.Constants.Companion.dayFormatyear
import ie.healthylunch.app.utils.Constants.Companion.formatDate
import ie.healthylunch.app.utils.Constants.Companion.formatDay
import ie.healthylunch.app.utils.MethodClass.calenderOffDialog
import ie.healthylunch.app.utils.MethodClass.getShortFormOfDay
import ie.healthylunch.app.utils.UserPreferences.Companion.saveAsObject
import java.util.*


class DashBoardFragment :
    BaseFragment<DashBoardViewModel, DashBoardRepository>(),
    AdapterItemOnclickListener {
    lateinit var binding: FragmentDashBoardBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var shouldCallPaging: Boolean = true
    private var finalOrderList: MutableList<DashboardListItem?>? = ArrayList()
    private var selectedDate: String? = ""
    private lateinit var orderPagerAdapter: OrderPagerAdapter

    private var handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var isOrderReplaced = false
    private var orderReplacedMsg = ""
    var currentSelectedDate = ""
    private var shouldShowLoader: Boolean = true
    private var nextOrderSelectedPosition = STATUS_MINUS_ONE
    var studentList: List<ie.healthylunch.app.data.model.studentListModel.DataItem>? = ArrayList()
    var selectedFavoritesItemsPosition = STATUS_MINUS_ONE
    private lateinit var loader: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setUpViewModel()

        dashBoardFragment = this
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_dash_board,
                container,
                false
            )
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.dashboard_background_color)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        binding.context = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        init()
        onViewClick()
        allMethodCall()

        return binding.root

    }


    override fun onResume() {
        super.onResume()
        if (CURRENT_ORDER_DAY.isEmpty()) {
            //Default position 1 change -1
            SELECTED_ORDER_POSITION = STATUS_ONE
        }

        getStudentList()
        dashBoardToolbar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dashboard_toolbar_color
            )
        )
        dashBoardNotificationLayout.visibility = View.VISIBLE
        dashBoardHelpLayout.visibility = View.VISIBLE
        dashBoardWalletLayout.visibility = View.VISIBLE
        dashBoardToolbar.visibility = View.VISIBLE

    }

    private fun init() {
        MethodClass.setupCarousel(binding.orderViewPager, 0.32f, 0.45f)
        loader = MethodClass.loaderDialog(binding.root.context)
    }

    private fun onViewClick() {
        binding.calendarHelpIv.setOnClickListener {
            activity?.let { activity -> dashBoardBottomCalenderHelpDialog(activity) }
        }

        binding.layoutXpPoint.setOnClickListener{
            aboutXpDialog()
        }
    }

    override fun getViewModel() = DashBoardViewModel::class.java
    override fun getRepository() =
        DashBoardRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    private fun allMethodCall() {
        //register OnPageChangeCallback on Student ViewPager
        registerOnPageChangeCallbackOnStudentViewPager()

        //call registerOnPageChangeCallback function of orderViewPager
        registerOnPageChangeCallbackOnOrderViewPager()

        //call registerOnPageChangeCallback function of calendar ViewPager
        registerOnPageChangeCallbackOnCalendarViewPager()

        //get Student list response
        getStudentListResponse()

        ////get dashboard view
        getDashBoardView()

        ////Get Dashboard Current Order View
        getDashboardCurrentOrderView()

        ////get dashboard notification count response
        getNotificationCountResponse()

        //get dashboard bottom calendar response
        getDashboardBottomCalenderResponse()

        //get parent details response
        getParentDetailsResponse()

        //get Replaced Order For Next Week Same Day Response
        getReplacedOrderForNextWeekSameDayResponse()

        //Get Favorites List Response
        getFavoritesListResponse()
    }

    private fun aboutXpDialog(){
        binding.root.context.let { ctx ->

            val dialog = Dialog(ctx)
            dialog.window?.let { window ->
                window.setBackgroundDrawableResource(R.color.transparent)
                window.decorView.setBackgroundResource(R.color.transparent)
                window.setDimAmount(0.0f)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val dialogBinding = AboutXpDialogBinding.inflate(LayoutInflater.from(ctx))
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)

            dialogBinding.btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    fun getStudentList() {

        binding.root.context?.let { context ->
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    context,
                    Constants.USER_DETAILS
                )

            val userdata = loginResponse?.response?.raws?.data
            userdata?.username?.let { usernameAndEmail ->
                USER_NAME = usernameAndEmail
                viewModel.userEmail.value = usernameAndEmail
            }
            userdata?.id?.let { parentId -> PARENT_ID = parentId }
            //showLoader()
            loader.show()
            userdata?.token?.let { token ->
                val jsonObject = MethodClass.getCommonJsonObject(context)
                viewModel.studentList(jsonObject, token)
            }

        }


    }


    fun replacedOrderForNextWeekSameDayApiCall() {
        binding.root.context?.let { context ->
            showLoader()
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    context,
                    Constants.USER_DETAILS
                )

            Constants.TOKEN = loginResponse?.response?.raws?.data?.token.toString()
            Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
            val jsonObject = MethodClass.getCommonJsonObject(context)
            jsonObject.addProperty("week_day", Constants.CURRENT_DAY)
            jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
            jsonObject.addProperty("replace_order", "yes")
            jsonObject.addProperty("user_type", Constants.USER_TYPE)
            viewModel.replacedOrderForNextWeekSameDay(jsonObject, Constants.TOKEN)
        }

    }

    //Dashboard list api call method

    fun dashboardList() {
        binding.root.context?.let { context ->
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    context,
                    Constants.USER_DETAILS
                )
            Constants.TOKEN = loginResponse?.response?.raws?.data?.token.toString()

            viewModel.endDateStr = ""
            finalOrderList = ArrayList()

            // for parent details
            val parentDetailsJsonObject = MethodClass.getCommonJsonObject(context)
            parentDetailsJsonObject.addProperty("parent_id", Constants.PARENT_ID)
            //for notificationCount jsonObject
            val notificationJsonObject = MethodClass.getCommonJsonObject(context)

            //For Dashboard bottom calender
            val dashboardBottomCalender = MethodClass.getCommonJsonObject(context)
            dashboardBottomCalender.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
            dashboardBottomCalender.addProperty("user_type", Constants.USER_TYPE)

            //For Dashboard view
            val dashBoardViewJsonObject = MethodClass.getCommonJsonObject(context)
            dashBoardViewJsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
            dashBoardViewJsonObject.addProperty("user_type", Constants.USER_TYPE)
            if (CURRENT_ORDER_DAY.isEmpty()) {
                dashBoardViewJsonObject.addProperty("end_date", viewModel.endDateStr)
            } else {
                dashBoardViewJsonObject.addProperty("end_date", CURRENT_ORDER_DAY)
            }
            shouldShowLoader = true
            //showLoader()
            if (!loader.isShowing)
                loader.show()

            viewModel.dashboardList(
                parentDetailsJsonObject,
                notificationJsonObject,
                dashboardBottomCalender,
                dashBoardViewJsonObject,
                Constants.TOKEN
            )
        }
    }


    //registerOnPageChangeCallback function
    private fun registerOnPageChangeCallbackOnOrderViewPager() {
        binding.orderViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                @SuppressLint("SimpleDateFormat")
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    SELECTED_ORDER_POSITION = position

                    finalOrderList?.let { finalOrderList ->
                        if (finalOrderList.isNotEmpty()) {
                            val endDate =
                                viewModel.endDateStr?.let { dayFormatYearMonthDay.parse(it) }
                            val endDate30 =
                                viewModel.endDate30Str?.let { dayFormatYearMonthDay.parse(it) }

                            if ((finalOrderList.size - position) < 4 &&
                                !viewModel.endDateStr.isNullOrBlank() &&
                                !viewModel.endDate30Str.isNullOrBlank() &&
                                (endDate?.before(endDate30) == true) &&
                                shouldCallPaging
                            ) {
                                shouldCallPaging = false
                                dashBoardViewWithoutLoader()
                            }
                            selectedDate = finalOrderList[SELECTED_ORDER_POSITION]?.endDate
                            setBottomCalendarPosition()
                            (activity as? DashBoardActivity)?.setEndDateForPullToRefresh(
                                finalOrderList[position]?.endDate
                            )
                        }
                    }


                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    (activity as? DashBoardActivity)?.enableDisableSwiperefresh(state == ViewPager2.SCROLL_STATE_IDLE)
                }
            })
    }


    fun dashBoardViewWithoutLoader() {
        binding.root.context?.let { context ->
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    context,
                    Constants.USER_DETAILS
                )

            Constants.TOKEN = loginResponse?.response?.raws?.data?.token.toString()
            Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()

            val dashBoardViewJsonObject = MethodClass.getCommonJsonObject(context)
            dashBoardViewJsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
            dashBoardViewJsonObject.addProperty("user_type", Constants.USER_TYPE)
            dashBoardViewJsonObject.addProperty("end_date", viewModel.endDateStr)
            viewModel.dashBoardView(dashBoardViewJsonObject, Constants.TOKEN)
        }
    }

    fun notificationListApiCallWithoutLoader() {
        binding.root.context?.let { context ->
            shouldShowLoader = false
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    context,
                    Constants.USER_DETAILS
                )
            Constants.TOKEN = loginResponse?.response?.raws?.data?.token.toString()
            val notificationJsonObject = MethodClass.getCommonJsonObject(context)
            viewModel.notificationListWithoutLoader(notificationJsonObject, Constants.TOKEN)
        }

    }


    fun getStudentListResponse() {
        activity?.let { activity ->
            binding.studentViewPager.offscreenPageLimit = 4

            viewModel.studentListResponse?.observe(viewLifecycleOwner) { studentResponse ->

                when (studentResponse) {
                    is Resource.Success -> {
                        studentList = ArrayList()
                        //hideLoader()

                        studentList = studentResponse.value.response.raws.data
                        if (studentList?.isNotEmpty() == true) {
                            val studentPagerAdapter =
                                StudentListPagerAdapter(activity, studentList)
                            binding.studentViewPager.adapter = studentPagerAdapter

                            //end fake dragging
                            if (binding.studentViewPager.isFakeDragging)
                                binding.studentViewPager.endFakeDrag()

                            binding.studentViewPager.currentItem =
                                Constants.SELECTED_STUDENT_POSITION
                            binding.studentViewPager.isFocusable = false
                        } else {
                            AppController.getInstance().isDeActivatedAllStudents = true
                            AppController.getInstance().logoutAppController(activity, true)
                        }


                    }
                    is Resource.Failure -> {
                        //hideLoader()
                        if (studentResponse.errorBody != null) {
                            studentResponse.errorString?.let { error ->
                                if (studentResponse.errorCode == 401)

                                    AppController.getInstance().refreshTokenResponse(
                                        activity,
                                        dashBoardFragment,
                                        STUDENT_LIST,
                                        REFRESH_TOKEN
                                    )
                                else if (studentResponse.errorCode == 307 || studentResponse.errorCode == 426)
                                    MethodClass.showErrorDialog(
                                        activity,
                                        error,
                                        studentResponse.errorCode
                                    )

                            }


                        }
                        viewModel._studentListResponse.value = null
                        viewModel.studentListResponse = viewModel._studentListResponse
                        loader.dismiss()
                    }

                    else -> {}
                }

            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")

    private fun getDashBoardView() {
        viewModel.dashBoardViewResponse?.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    viewDashboard(it.value.response.raws.data)
                    /*if (it.value.response.raws.data?.dashboardList?.isNotEmpty() == true) {
                        finalOrderList?.addAll(it.value.response.raws.data.dashboardList)


                        if (viewModel.endDateStr.isNullOrBlank()) {
                            orderPagerAdapter = OrderPagerAdapter(
                                requireActivity(),
                                finalOrderList,
                                viewModel.schoolType,
                                this
                            )
                            binding.orderViewPager.adapter = orderPagerAdapter
                            binding.orderViewPager.isFocusable = false
                        } else {
                            orderPagerAdapter.orderList = finalOrderList
                            orderPagerAdapter.notifyDataSetChanged()
                        }



                        viewModel.endDate30Str = it.value.response.raws.data.date_30?.endDate_30
                        viewModel.endDateStr = finalOrderList?.last()?.endDate

                        viewModel.endDate30Str?.let { it1 -> Log.e("endDate30", it1) }
                        viewModel.endDateStr?.let { it1 -> Log.e("endDate", it1) }

                        requireActivity().runOnUiThread {
                            //set selected position
                            if (binding.orderViewPager.isFakeDragging)
                                binding.orderViewPager.endFakeDrag()
                            if (SELECTED_ORDER_POSITION != STATUS_MINUS_ONE) {
                                //binding.orderViewPager.currentItem = Constants.SELECTED_ORDER_POSITION

                            } else {
                                lifecycleScope.launch {
                                    delay(300L)
                                    binding.orderViewPager.currentItem = STATUS_ONE
                                }

                            }
                            shouldCallPaging = true
                        }
                    }*/
                    //hideLoader()
                    loader.dismiss()
                }
                is Resource.Failure -> {
                    //hideLoader()
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                requireActivity(),
                                dashBoardFragment,
                                Constants.DASHBOARD_BOTTOM_CALENDER,
                                Constants.REFRESH_TOKEN
                            )
                    else if (it.errorCode == 307 || it.errorCode == 426)
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                requireActivity(),
                                it1, it.errorCode
                            )
                        }
                    viewModel._dashBoardViewResponse.value = null
                    viewModel.dashBoardViewResponse = viewModel._dashBoardViewResponse

                    loader.dismiss()
                }
                else -> {}
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")

    private fun getDashboardCurrentOrderView() {
        viewModel.viewDashboardCurrentOrderResponse?.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (CURRENT_ORDER_DAY.isNotEmpty()) {
                        SELECTED_ORDER_POSITION =
                            it.value.response.raws.data?.dashboardList?.indexOfFirst { dashboardListItem ->
                                dashboardListItem?.endDate == CURRENT_ORDER_DAY
                            } ?: STATUS_ONE
                    }

                    CURRENT_ORDER_DAY = ""
                    viewDashboard(it.value.response.raws.data)

                }
                is Resource.Failure -> {
                    //hideLoader()
                    CURRENT_ORDER_DAY = ""
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                requireActivity(),
                                dashBoardFragment,
                                Constants.DASHBOARD_BOTTOM_CALENDER,
                                Constants.REFRESH_TOKEN
                            )
                    else if (it.errorCode == 307 || it.errorCode == 426)
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                requireActivity(),
                                it1, it.errorCode
                            )
                        }
                    viewModel._viewDashboardCurrentOrderResponse.value = null
                    viewModel.viewDashboardCurrentOrderResponse =
                        viewModel._viewDashboardCurrentOrderResponse

                    loader.dismiss()
                }
                else -> {}
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun viewDashboard(data: Data?) {
        binding.root.context?.let { ctx ->
        if (data?.dashboardList?.isNotEmpty() == true) {
            saveAsObject(ctx, data.date_30?.endDate_30, CALENDAR_END_DATE_TAG)
            finalOrderList?.addAll(data.dashboardList)
            data.holidaysList?.let { holidays ->
                finalOrderList?.forEach { orderItem ->
                    if (orderItem?.dayOff?.equals(NO, true) == true) {
                        orderItem.isHoliday = holidays.contains(orderItem.endDate) == true
                    }
                }
            }

            if (viewModel.endDateStr.isNullOrBlank()) {
                orderPagerAdapter = OrderPagerAdapter(
                    finalOrderList,
                    viewModel.schoolType,
                    this
                )
                binding.orderViewPager.adapter = orderPagerAdapter
                binding.orderViewPager.isFocusable = false
            } else {
                orderPagerAdapter.orderList = finalOrderList
                orderPagerAdapter.notifyDataSetChanged()
            }



            viewModel.endDate30Str = data.date_30?.endDate_30
            viewModel.endDateStr = finalOrderList?.last()?.endDate

            viewModel.endDate30Str?.let { it1 -> Log.e("endDate30", it1) }
            viewModel.endDateStr?.let { it1 -> Log.e("endDate", it1) }

            requireActivity().runOnUiThread {
                //set selected position
                if (binding.orderViewPager.isFakeDragging)
                    binding.orderViewPager.endFakeDrag()
                // if (SELECTED_ORDER_POSITION != STATUS_MINUS_ONE) {
                if (CURRENT_ORDER_DAY.isEmpty() || !shouldCallPaging) {
                    binding.orderViewPager.currentItem = SELECTED_ORDER_POSITION
                }
                /*} else {
                    lifecycleScope.launch {
                        delay(300L)
                        binding.orderViewPager.currentItem = STATUS_ONE
                    }

                }*/

                shouldCallPaging = true
            }
        }
    }
    }



    @SuppressLint("SetTextI18n")
    fun getParentDetailsResponse() {
        activity?.let { activity ->
            viewModel.parentDetailsResponse?.observe(viewLifecycleOwner) { response ->

                when (response) {
                    is Resource.Success -> {
                        var walletBalanceFloat = 0.00f
                        try {
                            walletBalanceFloat =
                                response.value.response.raws.data.walletBalance.toFloat()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        val walletBalance = "€ ${
                            String.format(
                                "%.2f",
                                walletBalanceFloat
                            )
                        }"

                        //get login response object from shared preference
                        val loginResponse: LoginResponse? =
                            UserPreferences.getAsObject<LoginResponse>(
                                context,
                                Constants.USER_DETAILS
                            )
                        loginResponse?.response?.raws?.data?.displayName =
                            response.value.response.raws.data.displayName

                        //save in shared preference
                        UserPreferences.saveAsObject(
                            requireContext(), loginResponse,
                            Constants.USER_DETAILS
                        )

                        (activity as? DashBoardActivity)?.updateWalletBalance(walletBalance)

                        delayDashBoardListDataLoader()
                    }
                    is Resource.Failure -> {
                        //hideLoader()
                        if (response.errorBody != null) {
                            response.errorString?.let { _ ->

                                response.errorString.let { error ->
                                    if (response.errorCode == 401)
                                        AppController.getInstance()
                                            .refreshTokenResponse(
                                                activity,
                                                dashBoardFragment,
                                                Constants.PARENT_DETAILS,
                                                Constants.REFRESH_TOKEN
                                            )
                                    else if (response.errorCode == 307 || response.errorCode == 426)
                                        MethodClass.showErrorDialog(
                                            activity,
                                            error,
                                            response.errorCode
                                        )
                                }


                            }


                        }
                        viewModel._parentDetailsResponse.value = null
                        viewModel.parentDetailsResponse = viewModel._parentDetailsResponse

                        loader.dismiss()
                    }


                    else -> {}
                }

            }

        }
    }



    fun getReplacedOrderForNextWeekSameDayResponse() {
        activity?.let { activity ->
            viewModel.replacedOrderForNextWeekSameDayResponse?.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        for (i in Constants.ORDER_LIST.indices) {
                            if (Objects.equals(
                                    Constants.ORDER_LIST[i].day?.lowercase(),
                                    Constants.ORDER_LIST[nextOrderSelectedPosition].day?.lowercase()
                                )
                                && Objects.equals(Constants.ORDER_LIST[i].dayOff?.lowercase(), NO)
                            ) {
                                SELECTED_ORDER_POSITION = i
                                Constants.DAY_NAME = Constants.ORDER_LIST[i].day.toString()
                                break
                            }
                        }
                        isOrderReplaced = true
                        orderReplacedMsg = it.value.response.raws.successMessage
                        hideLoader()

                        //Default position 1 change -1
                        SELECTED_ORDER_POSITION = STATUS_ONE
                        //call dashboard list
                        dashboardList()

                    }
                    is Resource.Failure -> {
                        isOrderReplaced = false
                        hideLoader()
                        if (it.errorCode == 401)
                            AppController.getInstance()
                                .refreshTokenResponse(
                                    activity,
                                    dashBoardFragment,
                                    Constants.NEXT_DAY_ORDER,
                                    Constants.REFRESH_TOKEN
                                )
                        else if (it.errorCode == 307 || it.errorCode == 426)
                            it.errorString?.let { it1 ->
                                MethodClass.showErrorDialog(
                                    activity,
                                    it1, it.errorCode
                                )
                            }

                        viewModel._replacedOrderForNextWeekSameDayResponse.value = null
                        viewModel.replacedOrderForNextWeekSameDayResponse =
                            viewModel._replacedOrderForNextWeekSameDayResponse
                    }

                    else -> {
                        hideLoader()
                    }
                }


            }

        }
    }

    private fun registerOnPageChangeCallbackOnStudentViewPager() {
        binding.studentViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {


                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if ((studentList?.size ?: 0) > 0) {
                        try {
                            Constants.SELECTED_STUDENT_POSITION = position


                            Constants.USER_TYPE = studentList?.get(position)?.user_type.toString()

                            viewModel.studentXpPoints.value =
                                studentList?.get(position)?.xpPoints.toString()

                            //getting school type
                            viewModel.schoolType = studentList?.get(position)?.schoolType

                            //check if school type deis or private. schoolType == 1 is for private and schoolType == 2 is for deis
                            //xp points only be visible for private school i.e. for schoolType == 1
                            viewModel.isXpLayoutVisible.value =
                                (viewModel.schoolType == 1 && Constants.USER_TYPE.equals(
                                    Constants.STUDENT,
                                    true
                                ))
                                        && (studentList?.get(position)?.xpPoints ?: 0) > 0


                            viewModel.schoolType?.let {
                                viewModel.isBottomCalendarVisible.value =
                                    (viewModel.schoolType == 1 || (viewModel.schoolType == 2 && Constants.USER_TYPE.equals(
                                        Constants.TEACHER,
                                        true
                                    )))

                                viewModel.isDeisBottomLayoutVisible.value =
                                    viewModel.schoolType == 2 &&
                                            Constants.USER_TYPE.equals(Constants.STUDENT, true)
                            }

                            val studentPagerAdapter: StudentListPagerAdapter =
                                binding.studentViewPager.adapter as StudentListPagerAdapter


                            //Log.e("onPageSelected: ", student_id.toString())
                            if (DashBoardViewModel.selectedStudentPrePosition != Constants.SELECTED_STUDENT_POSITION) {
                                //Default SELECTED_ORDER_POSITION is 1 change -1
                                SELECTED_ORDER_POSITION =
                                    STATUS_ONE //when new student selected then position will be unselected
                                Constants.SELECTED_CALENDAR_POSITION =
                                    STATUS_ONE //when new student selected then position will be 1
                            }

                            //store the position as previous position
                            DashBoardViewModel.selectedStudentPrePosition =
                                Constants.SELECTED_STUDENT_POSITION

                            Constants.SELECTED_STUDENT_ID =
                                studentPagerAdapter.studentList?.get(position)?.studentid!!


                            //call dashboard list
                            dashboardList()
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    (activity as? DashBoardActivity)?.enableDisableSwiperefresh(state == ViewPager2.SCROLL_STATE_IDLE)
                }
            })

    }


    //registerOnPageChangeCallback function
    private fun registerOnPageChangeCallbackOnCalendarViewPager() {
        binding.calenderViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    Constants.SELECTED_CALENDAR_POSITION = position

                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    (activity as? DashBoardActivity)?.enableDisableSwiperefresh(state == ViewPager2.SCROLL_STATE_IDLE)
                }
            })
    }



    fun getNotificationCountResponse() {
        activity?.let { activity ->
            viewModel.notificationCountResponse?.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {

                        (activity as? DashBoardActivity)?.let { act ->
                            act.updateNotificationCount(response.value.response.raws.data.notificationcount)
                            act.isSwipeRefreshLoader = false
                        }


                        delayDashBoardListDataLoader()
                    }
                    is Resource.Failure -> {
                        if (shouldShowLoader)
                            //hideLoader()
                            loader.dismiss()
                        if (response.errorBody != null) {

                            response.errorString?.let { error ->
                                if (response.errorCode == 401)

                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            activity,
                                            dashBoardFragment,
                                            Constants.NOTIFICATION_COUNT_API,
                                            Constants.REFRESH_TOKEN
                                        )
                                else if (response.errorCode == 307 || response.errorCode == 426)
                                    MethodClass.showErrorDialog(
                                        activity,
                                        error,
                                        response.errorCode
                                    )

                            }

                        }
                        //set notification count textView visibility
                        (activity as DashBoardActivity).notificationCountVisibility(View.INVISIBLE)
                        viewModel._notificationCountResponse.value = null
                        viewModel.notificationCountResponse = viewModel._notificationCountResponse

                    }


                    else -> {}
                }

            }

        }
    }


    @SuppressLint("SimpleDateFormat")

    fun getDashboardBottomCalenderResponse() {
        activity?.let { activity ->
            binding.calenderViewPager.offscreenPageLimit = 4
            viewModel.dashBoardBottomCalenderResponse?.observe(viewLifecycleOwner) { response ->

                when (response) {
                    is Resource.Success -> {
                        viewModel.bottomCalendarList = response.value.response.raws.data
                        viewModel.bottomCalendarList?.let { bottomCalendarList ->
//                            Log.e("bottomCalendarList", "$bottomCalendarList")
                            if (bottomCalendarList?.isNotEmpty() == true) {
                                for (i in bottomCalendarList.indices) {
                                    val subItemList: MutableList<CalendarItem> = ArrayList()
                                    var totalDateList: List<Date>? = null

                                    bottomCalendarList[i]?.let { bottomCalendar ->
                                        //get all date list between two date range
//                                        Log.e("startDate", "${bottomCalendar.startDate}")
//                                        Log.e("endDate", "${bottomCalendar.endDate}")
                                        totalDateList = bottomCalendar.startDate?.let { startDate ->
                                            bottomCalendar.endDate?.let { endDate ->
                                                MethodClass.getDatesBetween(
                                                        startDate,
                                                        endDate
                                                )
                                            }
                                        }
//                                        Log.e("totalDateList", "$totalDateList")
                                    }
//                                    Log.d("TAG_totalDateList", "getDashboardBottomCalenderResponse: $totalDateList")

                                    totalDateList?.forEach { localDate ->

                                        //getting day name
                                        val dayName: String = dayFormat.format(localDate)

                                        // Check if the day is not saturday or sunday

                                        if (!(dayName.equals(Constants.SATURDAY, true) ||
                                                    dayName.equals(Constants.SUNDAY, true))
                                        ) {

                                            //getting day integer
                                            val dayTextStr: String = dayFormatDay.format(localDate)


                                            //primary sates of these field
                                            var isFree = false
                                            var isPending = false
                                            var isDelivered = false
                                            var isFutureDate: Boolean

                                            //check if the relevant date is after current date
                                            val relevantDateStr =
                                                "${dayFormatyear.format(localDate)}-${
                                                    dayFormatMonth.format(localDate)
                                                }-${dayFormatDay.format(localDate)}"

                                            if (MethodClass.isAfterLastOrderDate_yyyy_MM_dd_format(
                                                    relevantDateStr,
                                                    bottomCalendarList[i].lastOrderDate
                                                )
                                            ) {
                                                //date will be marked as pending

                                                isFutureDate = true
                                                if (bottomCalendarList[i].pendingOrderDate.toString()
                                                        .contains(relevantDateStr)
                                                ) {
                                                    isPending = true
                                                } else if (bottomCalendarList[i].orderDate.toString()
                                                        .contains(relevantDateStr)
                                                ) {
                                                    isDelivered = true
                                                }

                                            } else {
                                                isFutureDate = false
                                                if (
                                                    bottomCalendarList[i].orderDate.toString()
                                                        .contains(relevantDateStr) &&
                                                    bottomCalendarList[i].freeOrderDate?.contains(
                                                        relevantDateStr
                                                    ) == true
                                                ) {
                                                    //date will be marked as Free
                                                    isFree = true

                                                } else if (bottomCalendarList[i].pendingOrderDate.toString()
                                                        .contains(relevantDateStr)
                                                ) {
                                                    isPending = true
                                                } else if (bottomCalendarList[i].orderDate.toString()
                                                        .contains(relevantDateStr)
                                                ) {
                                                    //date will be marked as delivered
                                                    isDelivered = true

                                                } else {
                                                    isFree = false
                                                    isPending = false
                                                    isDelivered = false
                                                    isFutureDate = false
                                                }
                                            }

                                            //set subItem list
                                            subItemList.add(
                                                CalendarItem(
                                                    dayTextStr,
                                                    isFree,
                                                    isPending,
                                                    isDelivered,
                                                    isFutureDate
                                                )
                                            )
                                        }


                                    }

                                    //added the modified sub item list to the main list
                                    bottomCalendarList[i].calendarItemList = subItemList


                                }


                                val dashBoardCalenderAdapter =
                                    DashBoardCalenderAdapter(bottomCalendarList, this)
                                binding.calenderViewPager.adapter = dashBoardCalenderAdapter
                                binding.calenderViewPager.isFocusable = false

                                //end fake dragging
                                if (binding.calenderViewPager.isFakeDragging)
                                    binding.calenderViewPager.endFakeDrag()
                                setBottomCalendarPosition()


                            }
                        }
                        delayDashBoardListDataLoader()

                    }
                    is Resource.Failure -> {
                        //hideLoader()
                        if (response.errorCode == 401)
                            AppController.getInstance()
                                .refreshTokenResponse(
                                    activity,
                                    dashBoardFragment,
                                    Constants.DASHBOARD_BOTTOM_CALENDER,
                                    Constants.REFRESH_TOKEN
                                )
                        else if (response.errorCode == 307 || response.errorCode == 426)
                            response.errorString?.let { error ->
                                MethodClass.showErrorDialog(
                                    activity,
                                    error, response.errorCode
                                )
                            }
                        viewModel._dashBoardBottomCalenderResponse.value = null
                        viewModel.dashBoardBottomCalenderResponse =
                            viewModel._dashBoardBottomCalenderResponse

                        loader.dismiss()
                    }
                    else -> {}
                }

            }

        }
    }

    private fun setBottomCalendarPosition() {
        viewModel.bottomCalendarList?.let { bottomCalendarList ->
            for (i in bottomCalendarList.indices) {
                var startDate = dayFormatYearMonthDay.parse(bottomCalendarList[i].startDate)
                var endDate = dayFormatYearMonthDay.parse(bottomCalendarList[i].endDate)
                var selectedDate = dayFormatYearMonthDay.parse(selectedDate)
                if (selectedDate != null) {
                    if (!selectedDate.before(startDate) && !selectedDate.after(endDate)) {
                        SELECTED_CALENDAR_POSITION = i
                    }
                }
            }
        }
        binding.calenderViewPager.currentItem = SELECTED_CALENDAR_POSITION
    }



    fun getClearOrderResponse() {
        activity?.let { activity ->
            viewModel.clearOrderResponse?.observe(viewLifecycleOwner) {

                when (it) {
                    is Resource.Success -> {

                        hideLoader()
                        CURRENT_ORDER_DAY = ORDER_DATE
                        //Log.e("Success",CURRENT_ORDER_DAY)
                        //  SELECTED_ORDER_POSITION = STATUS_MINUS_ONE

                        Log.e("response", it.toString())
                        //success dialog
                        clearOrderSuccessDialog(CURRENT_ORDER_DAY)

                    }
                    is Resource.Failure -> {
                        hideLoader()
                        if (it.errorCode == 401)
                            AppController.getInstance()
                                .refreshTokenResponse(
                                    activity,
                                    dashBoardFragment,
                                    Constants.CLEAR_ORDER,
                                    Constants.REFRESH_TOKEN
                                )
                        else
                            it.errorString?.let { it1 ->
                                MethodClass.showErrorDialog(
                                    activity,
                                    it1, it.errorCode
                                )
                            }


                        viewModel._clearOrderResponse.value = null
                        viewModel.clearOrderResponse = viewModel._clearOrderResponse
                    }

                    else -> {}
                }


            }

        }
    }


    private fun clearOrderSuccessDialog(date: String) {

        val day = dayFromDate(date)
        //Log.e("Success",day)
        binding.root.context.let { ctx ->
            val clearOrderItemSuccessPopup = Dialog(ctx)

            with(clearOrderItemSuccessPopup) {
                window?.let { window ->
                    window.setBackgroundDrawableResource(R.color.transparent)
                    window.decorView.setBackgroundResource(android.R.color.transparent)
                    window.setDimAmount(0.0f)
                    window.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val dialogBinding =
                    ClearOrderItemSuccessDialogBinding.inflate(LayoutInflater.from(ctx))
                setContentView(dialogBinding.root)
                setCancelable(false)
                dialogBinding.dashBoardViewModel = viewModel
                viewModel.clearOrderPopupMessage.value =
                    getString(R.string.you_are_about_to_turn_off, day)
                dialogBinding.okBTN.setOnClickListener {
                    dismiss()
                    //call dashboard list
                    dashboardList()
                }
                show()
            }
        }
    }

    private fun dayFromDate(date: String): String {
        val dt: Date = formatDate.parse(date)!!
        return formatDay.format(dt)
    }

//    private fun getCurrentOrderDayList() {
//
//        binding.root.context?.let { context ->
//
//            val dashBoardViewJsonObject = MethodClass.getCommonJsonObject(context)
//            dashBoardViewJsonObject.addProperty("student_id", SELECTED_STUDENT_ID)
//            dashBoardViewJsonObject.addProperty("user_type", USER_TYPE)
//            dashBoardViewJsonObject.addProperty("end_date", CURRENT_ORDER_DAY)
//            viewModel.viewDashBoardCurrentOrder(dashBoardViewJsonObject, Constants.TOKEN)
//        }
//    }


    fun getFavouriteOrdersAddResponse() {
        activity?.let { activity ->
            viewModel.favouriteOrdersAddResponse?.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        viewModel.favoritesIConIsClickable = true
                        hideLoader()
                        response.value.response.raws.data.favouriteOrderId?.let { favouriteOrderId ->
                            setFavorites(
                                favouriteOrderId
                            )
                        }
//                        binding.root.context?.let { ctx->Toast.makeText(ctx, ctx.getString(R.string.order_added_to_your_favourite_list), Toast.LENGTH_SHORT).show()}
                        //for order add to Favourite list
                        orderAddToFavouriteDialog()
                    }
                    is Resource.Failure -> {
                        setRemoveFavorites()
                        viewModel.favoritesIConIsClickable = true
                        hideLoader()
                        response.errorString?.let { error ->
                            MethodClass.showErrorDialog(
                                activity,
                                error, response.errorCode
                            )
                        }
                        viewModel._favouriteOrdersAddResponse.value = null
                        viewModel.favouriteOrdersAddResponse = viewModel._favouriteOrdersAddResponse
                    }
                    else -> {}
                }
            }
        }
    }

    private fun orderAddToFavouriteDialog() {

        binding.root.context?.let { ctx ->

            if (!activity?.isFinishing!!) {
                val orderAddedFavouriteDialog = Dialog(ctx)

                with(orderAddedFavouriteDialog) {
                    window?.let { window ->
                        window.setBackgroundDrawableResource(R.color.transparent)
                        window.decorView.setBackgroundResource(
                            android.R.color.transparent
                        )
                        window.setDimAmount(0.0f)
                        window.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    val dialogBinding =
                        OrderAddedFavouriteDialogBinding.inflate(LayoutInflater.from(ctx))

                    setContentView(dialogBinding.root)
                    setCancelable(false)

                    dialogBinding.btnOk.setOnClickListener {
                        dismiss()
                    }
                    show()
                }

            }
        }

    }

    private fun setFavorites(favouriteOrderId: Int) {
        //If dashboardId != 0 and orderType is REPEAT then this condition will be executed.
        if (Objects.equals(
                finalOrderList?.get(selectedFavoritesItemsPosition)?.orderType?.lowercase(),
                REPEAT
            ) && finalOrderList?.get(SELECTED_ORDER_POSITION)?.dashboardId != Constants.DASHBOARD_ID_VALUE /*&& finalOrderList?.get(SELECTED_ORDER_POSITION)?.holidayStatus!= Constants.HOLIDAY_STATUS*/) {
            finalOrderList?.forEachIndexed { index, finalOrderItem ->
                // if(finalOrderList?.get(selectedFavoritesItemsPosition)?.weekName==finalOrderItem?.weekName && Objects.equals(finalOrderList?.get(index)?.orderType?.lowercase(), REPEAT)) {
                if (finalOrderList?.get(selectedFavoritesItemsPosition)?.unique_order_id?.equals(
                        finalOrderItem?.unique_order_id,
                        true
                    ) == true
                ) {
                    finalOrderList?.get(index)?.favourite_order_id = favouriteOrderId
                    orderPagerAdapter.notifyItemChanged(index)
                }
            }

        } else {
            finalOrderList?.get(selectedFavoritesItemsPosition)?.favourite_order_id =
                favouriteOrderId

            orderPagerAdapter.notifyItemChanged(selectedFavoritesItemsPosition)
        }
    }



    fun getFavouriteOrdersRemoveResponse(favouriteOrderId: Int?) {
        activity?.let { activity ->
            viewModel.favouriteOrdersRemoveResponse?.observe(viewLifecycleOwner) { response ->

                when (response) {
                    is Resource.Success -> {

                        hideLoader()
                        setRemoveFavorites()
                        binding.root.context?.let { ctx ->
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.order_removed_to_your_favourite_list),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    is Resource.Failure -> {
                        favouriteOrderId?.let { setFavorites(favouriteOrderId) }
                        hideLoader()
                        response.errorString?.let { error ->
                            MethodClass.showErrorDialog(
                                activity,
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
    }

    private fun setRemoveFavorites() {
        var selectedFavouriteOrderId =
            finalOrderList?.get(selectedFavoritesItemsPosition)?.favourite_order_id
        finalOrderList?.forEachIndexed { index, finalOrderItem ->
            if (Objects.equals(
                    finalOrderList?.get(selectedFavoritesItemsPosition)?.orderType?.lowercase(),
                    REPEAT
                ) && finalOrderList?.get(selectedFavoritesItemsPosition)?.unique_order_id?.equals(
                    finalOrderItem?.unique_order_id,
                    true
                ) == true
            ) {
                finalOrderList?.get(index)?.favourite_order_id = STATUS_ZERO
                orderPagerAdapter.notifyItemChanged(index)
            } else if (selectedFavouriteOrderId == finalOrderItem?.favourite_order_id && selectedFavouriteOrderId != STATUS_ZERO && finalOrderItem?.favourite_order_id != STATUS_ZERO) {
                finalOrderList?.get(index)?.favourite_order_id = STATUS_ZERO
                orderPagerAdapter.notifyItemChanged(index)
            }
        }
    }


    //order adapter item click
    @SuppressLint("SetTextI18n")

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        activity?.let { activity ->
            if (tag.equals("bottom_calendar", true))
                viewModel.calenderClick(activity)
            else {
                selectedFavoritesItemsPosition = SELECTED_ORDER_POSITION
                val orderList: List<DashboardListItem> =
                    arrayList as List<DashboardListItem>
                Constants.CURRENT_DAY =
                    MethodClass.getIntegerValueOfDay(orderList[position].weekName.toString())
                Log.d("CURRENT_DAY", "$CURRENT_DAY ")
                DAY_NAME = orderList[position].weekName.toString()
                WEEK_NAME = orderList[position].weekName.toString()
                Constants.ORDER_DATE = orderList[position].endDate.toString()
                Constants.DASHBOARD_ID = orderList[position].dashboardId

                if (tag.equals(ORDER_VALUE, true)) {
                    if (orderList[position].noonSnooze == STATUS_ONE) {//noon snooze pop up
                        orderList[position].dayOffMsg?.let { dayOffMsg ->
                            dashBoardNoonSnoozeDialog(activity, dayOffMsg)
                        }

                    } else if (Objects.equals(orderList[position].dayOff, YES)) {
                        orderList[position].dayOffMsg?.let { dayOffMsg ->
                            CustomDialog.showWarningTypeDialog(
                                activity,
                                dayOffMsg,
                                object : DialogOkListener {
                                    override fun okOnClick(dialog: Dialog, activity: Activity) {
                                        dialog.dismiss()
                                        SELECTED_ORDER_POSITION += STATUS_ONE
                                        binding.orderViewPager.currentItem =
                                            SELECTED_ORDER_POSITION
                                    }
                                })
                        }

                    } else {
                        CURRENT_ORDER_DAY = ORDER_DATE
                        val intent = Intent(activity, MenuTemplateActivity::class.java)
                        intent.putExtra(FROM_TAG, DASHBOARD_ORDER)
                        startActivity(intent)
                    }


                } else if (tag.equals(ORDER_CLEAR, true)) {
                    var isSingleOrderForPrivateStudent = false
                    var contentMsg = ""
                    var yesButtonText = ""
                    var noButtonText = ""
                    if (viewModel.schoolType == STATUS_TWO
                        && Constants.USER_TYPE.equals(Constants.STUDENT, true)
                    ) { //school type 2 is for deis market
                        contentMsg =
                            "${getString(R.string.please_note_you_are_clearing_only_additional_snacks_for_all)} ${orderList[position].weekName}."
                        yesButtonText = getString(R.string.confirm)
                        noButtonText = getString(R.string.cancel)
                        isSingleOrderForPrivateStudent = true
                    } else if (Objects.equals(
                            orderList[position].orderType?.lowercase(),
                            REPEAT
                        ) && orderList[position].orderStatus.equals(STATUS_ONE.toString(), true)
                    ) else if (Objects.equals(
                            orderList[position].orderType?.lowercase(),
                            REPEAT
                        ) && !orderList[position].orderStatus.equals(STATUS_ONE.toString(), true)
                    ) {
                        /*contentMsg =
                            "${getString(R.string.please_note_you_are_clearing_orders_for_all)} ${orderList[position].weekName} ${
                                getString(
                                    R.string.to_turn_off_just_one
                                )
                            } ${orderList[position].weekName} ${getString(R.string.go_to_calendar)}"
                        yesButtonText = getString(R.string.confirm)
                        noButtonText = getString(R.string.calendar)*/
                        isSingleOrderForPrivateStudent = false

                    } else {
                        contentMsg =
                            getString(R.string.are_you_sure_you_want_to_delete_order_)
                        yesButtonText = getString(R.string.confirm)
                        noButtonText = getString(R.string.cancel)
                        isSingleOrderForPrivateStudent = true
                    }



                    if (isSingleOrderForPrivateStudent) {
                        viewModel.isRepeatOrder.value = false

                        clearOrderDialog(orderList, position)

//                    CustomDialog.clearFoodItemPopup(activity,
//                        contentMsg,
//                        yesButtonText,
//                        noButtonText,
//                        object : DialogYesNoListener {
//                            override fun yesOnClick(dialog: Dialog, activity: Activity) {
//                                dialog.dismiss()
//                                clearOrder()
//                                viewModel.orderStatus.value = orderList[position].orderStatus
//                            }
//
//                            override fun noOnClick(dialog: Dialog, activity: Activity) {
//                                dialog.dismiss()
//                                if (Objects.equals(
//                                        orderList[position].orderType?.lowercase(),
//                                        REPEAT
//                                    ) && !orderList[position].orderStatus.equals(
//                                        STATUS_ONE.toString(),
//                                        true
//                                    ) && (viewModel.schoolType == STATUS_ONE ||
//                                            (viewModel.schoolType == STATUS_TWO && Constants.USER_TYPE.equals(
//                                                Constants.TEACHER,
//                                                true
//                                            )))
//                                //school type 1 is for private market and 2 is for deis market
//                                )
//                                    activity.startActivity(
//                                        Intent(
//                                            activity,
//                                            CalendarActivity::class.java
//                                        )
//                                    )
//
//                            }
//
//                        })
                    } else {
                        viewModel.isRepeatOrder.value = true
                        clearOrderDialog(orderList, position)
                    }

                } else if (tag.equals(FOOD_ITEMS, true)) {
                    CustomDialog.showOkTypeDialog(
                        activity,
                        getString(R.string.your_order),
                        orderList[position].productName,
                        object : DialogOkListener {
                            override fun okOnClick(dialog: Dialog, activity: Activity) {
                                dialog.dismiss()
                            }

                        })
                } else if (tag.equals(ADD_FAVORITES, true)) {
                    setFavorites(STATUS_MINUS_ONE)
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
                    //     MethodClass.showProgressDialog(activity)
                    val jsonObject = MethodClass.getCommonJsonObject(activity)
                    jsonObject.addProperty("order_date", Constants.ORDER_DATE)
                    jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
                    jsonObject.addProperty("user_type", Constants.USER_TYPE)
                    jsonObject.addProperty("order_dashboard_id", Constants.DASHBOARD_ID)
                    if (viewModel.favoritesIConIsClickable) {
                        viewModel.favouriteOrdersAdd(jsonObject, Constants.TOKEN)
                        viewModel.favoritesIConIsClickable = false
                    }
                    getFavouriteOrdersAddResponse()
                } else if (tag.equals(REMOVE_FAVORITES, true)) {
                    CustomDialog.removeFromFavorites(binding.root.context,
//                        getString(R.string.do_you_want_to_remove_this_order_from_your_s_favourite),
//                        getString(R.string.confirm),
//                        getString(R.string.cancel),
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
                                //         MethodClass.showProgressDialog(activity)
                                val jsonObject = MethodClass.getCommonJsonObject(activity)
                                jsonObject.addProperty(
                                    "favourite_id",
                                    orderList[position].favourite_order_id
                                )
                                viewModel.favouriteOrdersRemove(jsonObject, Constants.TOKEN)
                                getFavouriteOrdersRemoveResponse(orderList[position].favourite_order_id)
                                setRemoveFavorites()
                            }

                            override fun noOnClick(dialog: Dialog, activity: Activity) {
                                dialog.dismiss()
                            }

                        })
                } else if (tag.equals(CALENDAR_OFF, true)) {

                    binding.root.context.let { ctx ->
                        calenderOffDialog(ctx)
                    }


                } else /*if (tag.equals(FAVORITES_LIST, true)) */ {
                    orderList[position].orderDate?.let { orderDate ->
                        currentSelectedDate = orderDate
                    }
                    getFavoritesList()

                }
            }
        }
    }


    private fun clearOrderDialog(orderList: List<DashboardListItem>, position: Int) {

        binding.root.context.let { ctx ->
            viewModel.orderStatus.value = orderList[position].orderStatus
            val clearOrderItemPopup = Dialog(ctx)

            clearOrderItemPopup.window?.let { window ->
                window.setBackgroundDrawableResource(R.color.transparent)
                window.decorView.setBackgroundResource(android.R.color.transparent)
                window.setDimAmount(0.0f)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val dialogBinding =
                ClearOrderItemDialogBinding.inflate(LayoutInflater.from(ctx))
//                    val clearOrderItemPopupBinding: ClearOrderItemYesNoBinding = DataBindingUtil.inflate(
//                        LayoutInflater.from(activity),
//                        R.layout.clear_order_item_yes_no,
//                        null,
//                        false,
//                        )
            clearOrderItemPopup.setContentView(dialogBinding.root)
            clearOrderItemPopup.setCancelable(false)

            with(dialogBinding) {
                if (viewModel.schoolType == STATUS_TWO
                    && USER_TYPE.equals(Constants.STUDENT, true)
                ) {
                    (orderList[position].weekName).let { wknm ->
                        contentTv.text = getString(R.string.please_note_you_are_clearing, wknm)
                        clearThisDayBTN.text = getString(R.string.confirm)
                        clearRepeatDayBTN.text = getString(R.string.cancel)
                        clearRepeatDayBTN.setBackgroundResource(R.drawable.red_button_bg)
                    }

                    clearRepeatDayBTN.setOnClickListener {
                        clearOrderItemPopup.dismiss()
                        if(viewModel.schoolType!= STATUS_TWO) {
                            clearOrder()
                        }
                    }
                    clearThisDayBTN.setOnClickListener {
                        clearOrderItemPopup.dismiss()
                        if(viewModel.schoolType== STATUS_TWO) {
                            clearOrder()
                        }
                    }
                   } else if (viewModel.isRepeatOrder.value == true && orderList[position].holidayStatus == 0) {

                    (orderList[position].weekName).let { wknm ->
                        contentTv.text = getString(R.string.you_are_about_to_turn_off, wknm)
                        clearThisDayBTN.text = getString(R.string.turn_off_just_for_this, wknm)
                        clearRepeatDayBTN.text = getString(R.string.clear_all_repeat_orders, wknm)
                    }

                    clearRepeatDayBTN.setOnClickListener {
                        clearOrderItemPopup.dismiss()
                        clearOrder()
                    }
                    clearThisDayBTN.setOnClickListener {
                        clearOrderItemPopup.dismiss()
//                    if (Objects.equals(
//                            orderList[position].orderType?.lowercase(),
//                            REPEAT
//                        ) && !orderList[position].orderStatus.equals(
//                            STATUS_ONE.toString(),
//                            true
//                        ) && (viewModel.schoolType == STATUS_ONE ||
//                                (viewModel.schoolType == STATUS_TWO && Constants.USER_TYPE.equals(
//                                    Constants.TEACHER,
//                                    true
//                                )))
//                    //school type 1 is for private market and 2 is for deis market
//                    )

                        saveHolidayApi()
//                        ctx.startActivity(
//                            Intent(
//                                ctx,
//                                CalendarActivity::class.java
//                            )
//                        )
                    }
//                } else if(viewModel.isRepeatOrder.value == true && orderList[position].holidayStatus == 1){
//                    (orderList[position].weekName).let { wknm ->
//                        contentTv.text = getString(R.string.you_are_about_to_turn_off, wknm)
//                        clearThisDayBTN.text = getString(R.string.confirm)
//                        clearRepeatDayBTN.text = getString(R.string.cancel)
//                        clearRepeatDayBTN.setBackgroundResource(R.drawable.clear_order_item_2nd_button_bg2)
//                    }
//
//                    clearRepeatDayBTN.setOnClickListener {
//                        clearOrderItemPopup.dismiss()
//                    }
//                    clearThisDayBTN.setOnClickListener {
//                        clearOrderItemPopup.dismiss()
//                        clearOrder()
//                    }
//                }

                } else if (viewModel.isRepeatOrder.value == true && orderList[position].holidayStatus == ONE){
                    (orderList[position].weekName).let{wknm ->
                        contentTv.text = getString(R.string.please_note_you_are_clearing_orders, wknm)
                    }
                    clearThisDayBTN.text = getString(R.string.confirm)
                    clearRepeatDayBTN.text = getString(R.string.cancel)
                    clearRepeatDayBTN.setBackgroundResource(R.drawable.red_button_bg)

                    clearRepeatDayBTN.setOnClickListener {
                        clearOrderItemPopup.dismiss()
                    }
                    clearThisDayBTN.setOnClickListener {
                        clearOrderItemPopup.dismiss()
                        clearOrder()
                    }

                } else {
                    contentTv.text = getString(R.string.are_you_sure_you)
                    clearThisDayBTN.text = getString(R.string.confirm)
                    clearRepeatDayBTN.text = getString(R.string.cancel)
                    clearRepeatDayBTN.setBackgroundResource(R.drawable.red_button_bg)

                    clearRepeatDayBTN.setOnClickListener {
                        clearOrderItemPopup.dismiss()
                    }
                    clearThisDayBTN.setOnClickListener {
                        clearOrderItemPopup.dismiss()
                        clearOrder()
                    }
                }

                closeDialogBTN.setOnClickListener {
                    clearOrderItemPopup.dismiss()
                }
            }
            clearOrderItemPopup.show()
        }

    }

    private fun getToken() : String? {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                binding.root.context,
                USER_DETAILS
            )
        return loginResponse?.response?.raws?.data?.token
    }


    private fun clearOrder() {
        getToken()?.let { token ->
            showLoader()
            val jsonObject = MethodClass.getCommonJsonObject(binding.root.context)
            jsonObject.addProperty("weekday", CURRENT_DAY)
            jsonObject.addProperty("student_id", SELECTED_STUDENT_ID)
            jsonObject.addProperty("user_type", USER_TYPE)
            jsonObject.addProperty("dashboard_id", DASHBOARD_ID)
            viewModel.clearOrder(jsonObject, token)
            getClearOrderResponse()
        }

    }

    private fun getFavoritesList() {
        getToken()?.let { token ->
            showLoader()
            val jsonObject = MethodClass.getCommonJsonObject(binding.root.context)
            jsonObject.addProperty("student_id", SELECTED_STUDENT_ID)
            jsonObject.addProperty("order_date", ORDER_DATE)
            jsonObject.addProperty("user_type", USER_TYPE)
            viewModel.favoritesList(jsonObject, token)
        }
    }


    fun saveHolidayApi(){
        getToken()?.let { token ->
            showLoader()
            val jsonObject = MethodClass.getCommonJsonObject(binding.root.context)
            val deleteDate = JsonArray()
            deleteDate.add(ORDER_DATE)
            jsonObject.addProperty("holidayfor", "1")
            jsonObject.add("holidayon", deleteDate)
            jsonObject.addProperty("user_type", USER_TYPE)
            jsonObject.addProperty("refuserid", SELECTED_STUDENT_ID.toString())
            viewModel.saveHolyDay(jsonObject, token)
            getSaveHolidayResponse()
        }
    }

    @SuppressLint("NotifyDataSetChanged")

    fun getFavoritesListResponse() {
        activity?.let { activity ->
            viewModel.favoritesListResponse.observe(viewLifecycleOwner) {

                when (it) {
                    is Resource.Success -> {
                        hideLoader()
                        if (it.value.response.raws.data?.favouriteOrders?.isNotEmpty() == true) {

                            CURRENT_ORDER_DAY = ORDER_DATE
                            val intent = Intent(activity, ProductActivity::class.java)
                            intent.putExtra(FROM_TAG, FAVORITES_ACTIVITY)
                            startActivity(intent)
                        } else {
                            noFavoritesInfoDialog()
                        }

                    }
                    is Resource.Failure -> {
                        if (it.errorBody != null) {
                            it.errorString?.let { _ ->
                                if (it.errorCode == 401) {
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            activity,
                                            this,
                                            Constants.FAVORITES_LIST,
                                            Constants.REFRESH_TOKEN
                                        )
                                } else {
                                    it.errorString.let { it1 ->
                                        MethodClass.showErrorDialog(
                                            activity,
                                            it1,
                                            it.errorCode
                                        )

                                    }
                                }


                            }


                        }
                        hideLoader()
                    }

                }


            }

        }
    }


    private fun getSaveHolidayResponse(){
        activity?.let { activity ->

            viewModel.saveHolyDayResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        hideLoader()
                        CURRENT_ORDER_DAY = ORDER_DATE
                        dashboardList()
                        viewModel._saveHolyDayResponse.value = null
                        viewModel.saveHolyDayResponse.value = viewModel._saveHolyDayResponse.value
                    }
                    is Resource.Failure -> {
                        CURRENT_ORDER_DAY =""
                        if (it.errorBody != null) {
                            it.errorString?.let { _ ->
                                if (it.errorCode == 401) {
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            activity,
                                            this,
                                            Constants.SAVE_HOLIDAY,
                                            Constants.REFRESH_TOKEN
                                        )
                                } else {
                                    it.errorString.let { it1 ->
                                        MethodClass.showErrorDialog(
                                            activity,
                                            it1,
                                            it.errorCode
                                        )
                                    }
                                }
                            }
                        }
                        hideLoader()
                    }
                    else -> {
                        CURRENT_ORDER_DAY =""
                        hideLoader()
                    }
                }
            }
        }
    }

    //DashBoard Bottom Calender Help Dialog
    private fun dashBoardBottomCalenderHelpDialog(
        activity: Activity

    ) {
        if (!activity.isFinishing) {
            val dialog = Dialog(activity)
            //dialog.window?.setBackgroundDrawableResource(R.color.transparent)
            dialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)
            dialog.window?.setDimAmount(0.0f)
            dialog.window?.setLayout(
                activity.resources.getDimensionPixelSize(R.dimen._215sdp),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val params: WindowManager.LayoutParams = WindowManager.LayoutParams()
            params.copyFrom(dialog.window?.attributes)
            params.gravity = Gravity.BOTTOM
            params.y = binding.calenderPagerContainer.height
            dialog.window?.attributes = params

            val binding: DashBoardBottomCalenderHelpDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.dash_board_bottom_calender_help_dialog,
                null,
                false
            )

            dialog.setContentView(binding.root)
            dialog.setCancelable(true)

            binding.crossIv.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    //DashBoard noon snooze Dialog
    private fun dashBoardNoonSnoozeDialog(activity: Activity, contentMsg: String) {
        if (!activity.isFinishing) {
            val dialog = Dialog(activity)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
            dialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)
            dialog.window?.setDimAmount(0.0f)

            val binding: NoonSnoozePopUpBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.noon_snooze_pop_up,
                null,
                false
            )
            dialog.setContentView(binding.root)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


            binding.contentTv.text = contentMsg
            binding.crossIv.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun delayDashBoardListDataLoader() {
        activity?.let { activity ->
            if (shouldShowLoader) {
                runnable = Runnable { MethodClass.hideProgressDialog(activity) }
                handler.postDelayed(runnable, 700)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun noFavoritesInfoDialog() {
        binding.root.context?.let { ctx ->
            CustomDialog.noFavoritesInfoDialog(
                ctx,
                object : DialogOkListener{
                    override fun okOnClick(dialog: Dialog, activity: Activity) {
                        dialog.dismiss()
                    }
                })
//                binding.contentTv.text =
//                    "${context?.getString(R.string.No_favourites_yet_for)} ${
//                        getShortFormOfDay(DAY_NAME).replaceFirstChar(Char::titlecase)
//                    } ${context?.getString(R.string.once_you_have_placed_orders)} ${
//                        getShortFormOfDay(
//                            DAY_NAME
//                        ).replaceFirstChar(Char::titlecase)
//                    } ${context?.getString(R.string.heart_your_favourites_to_gather_them_here)}"
//                noFavoritesInfoDialog?.setContentView(binding.root)
//                noFavoritesInfoDialog?.setCancelable(true)
//                binding.tvCreateNewOrder.setOnClickListener {
//                    CURRENT_ORDER_DAY = ORDER_DATE
//                    val intent = Intent(activity, MenuTemplateActivity::class.java)
//                    intent.putExtra(FROM_TAG, DASHBOARD_ORDER)
//                    startActivity(intent)
//                    noFavoritesInfoDialog?.dismiss()
//                }
//                binding.crossIv.setOnClickListener {
//                    noFavoritesInfoDialog?.dismiss()
//                }
//                if (noFavoritesInfoDialog?.isShowing == true)
//                    noFavoritesInfoDialog?.dismiss()
        }
    }

    private fun showLoader() {
        activity?.let { act ->
            if ((act as DashBoardActivity).isSwipeRefreshLoader) {
                MethodClass.hideProgressDialog(act)
            } else {
                MethodClass.showProgressDialog(act)
            }
        }
    }

    private fun hideLoader() {
        (activity as? DashBoardActivity)?.let { act ->
            act.activityDashboardBinding.swipeRefreshLayout.isRefreshing = false
            MethodClass.hideProgressDialog(act)
        }
    }
}