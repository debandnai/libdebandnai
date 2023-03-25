package ie.healthylunch.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.QuickViewAdapter
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.QuickViewForStudentRepository
import ie.healthylunch.app.data.viewModel.QuickViewForStudentViewModel
import ie.healthylunch.app.databinding.ActivityQuickViewForStudentBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_DISABLE_TAG
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_DISABLE_TIME
import ie.healthylunch.app.utils.Constants.Companion.ERROR_STATUS_307
import ie.healthylunch.app.utils.Constants.Companion.ERROR_STATUS_401
import ie.healthylunch.app.utils.Constants.Companion.ERROR_STATUS_426
import ie.healthylunch.app.utils.Constants.Companion.HOUR_TAG_16
import ie.healthylunch.app.utils.Constants.Companion.HOUR_TAG_30
import ie.healthylunch.app.utils.Constants.Companion.LOGIN_CHECK
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FIVE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.dayFormat
import ie.healthylunch.app.utils.Constants.Companion.hourDateFormat
import java.util.*

class QuickViewForStudentActivity :
    BaseActivity<QuickViewForStudentViewModel, QuickViewForStudentRepository>(),
    DialogYesNoListener, AdapterItemOnclickListener {
    private lateinit var binding: ActivityQuickViewForStudentBinding

    private var lastClickTime1: Long = 0
    private var lastClickTime2: Long = 0

      
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_quick_view_for_student
        )

        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        init()
        onViewClick()

    }

    override fun getViewModel() = QuickViewForStudentViewModel::class.java
    override fun getRepository() =
        QuickViewForStudentRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    @SuppressLint("SimpleDateFormat")
    fun setCurrentDate() {

        viewModel.currentDay.value =
            MethodClass.getIntegerValueOfDay(
                dayFormat.format(Date()).lowercase(Locale.getDefault())
            )
//        val calendar = Calendar.getInstance()
//        calendar.time = Date()
//        if (calendar.get(Calendar.HOUR_OF_DAY)>HOUR_TAG_16 && calendar.get(Calendar.MINUTE)>HOUR_TAG_30){
//            viewModel.currentDay.value = viewModel.currentDay.value?.plus(STATUS_ONE)
//        }

        viewModel.currentDay.value?.let { currentDay->
        if (currentDay > STATUS_FIVE)
            viewModel.currentDay.value = STATUS_ONE
        }
    }

      
    private fun init() {
        //after coming this page means the user is logged in
        UserPreferences.saveAsObject(
            this, IsLogin(1), LOGIN_CHECK
        )

        val quickViewAdapter = QuickViewAdapter(ArrayList(), this, this)
        binding.studentListRv.adapter = quickViewAdapter
        binding.studentListRv.isFocusable = false

        //add call back for back pressed
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        //get Quick view response
        getQuickViewResponse()




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        checkNotificationPermissionForAndroid13()
                    }
    }

      
    override fun onResume() {
        super.onResume()
        setCurrentDate()

        //call quick view api
        //Quick View Api need to call from onResume. Because if the day is friday and after 12 pm then day will change.
        //Otherwise after navigating from the dashboard page quickView page will refresh.

        quickViewOrderDay()
    }


    private fun checkNotificationPermissionForAndroid13() {
        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { }


    fun quickViewApiCall() {
        MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )
        loginResponse?.response?.raws?.data?.token?.let { token->
            val jsonObject = MethodClass.getCommonJsonObject(this)
            jsonObject.addProperty("order_day", viewModel.currentDay.value)
            viewModel.quickViewDay(jsonObject, token)
        }
    }
         fun quickViewOrderDay() {
                MethodClass.showProgressDialog(this)
                val loginResponse: LoginResponse? =
                    UserPreferences.getAsObject<LoginResponse>(
                        this,
                        Constants.USER_DETAILS
                    )
                loginResponse?.response?.raws?.data?.token?.let { token->
                    val jsonObject = MethodClass.getCommonJsonObject(this@QuickViewForStudentActivity)
                    viewModel.quickViewOrderDay(jsonObject, token)
                }
            }

      
    @SuppressLint("NotifyDataSetChanged")
    fun getQuickViewResponse() {
        viewModel.quickViewDayResponse?.observe(this) {  response->

            when (response) {

                is Resource.Success -> {

                    response.value.response.raws.data.studentList?.let { studentList->

                        val adapter: QuickViewAdapter =
                            binding.studentListRv.adapter as QuickViewAdapter
                        adapter.studentList = studentList
                        binding.studentListRv.smoothScrollToPosition(Constants.SELECTED_STUDENT_POSITION)
                        adapter.notifyDataSetChanged()
                    }

                    response.value.response.raws.data.dayDetails?.let { dayDetails ->
                        viewModel.currentDay.value =dayDetails.dayNumber
                        if (viewModel.currentDay.value == STATUS_ONE)
                            binding.ivArrowLeft.visibility = View.INVISIBLE
                        if (viewModel.currentDay.value == STATUS_FIVE)
                            binding.ivArrowRight.visibility = View.INVISIBLE

                        viewModel.dayName.value = "${dayDetails.dayName} ${dayDetails.dayDate}"
                        MethodClass.hideProgressDialog(this@QuickViewForStudentActivity)
                    }
                }
                is Resource.Failure -> {

                    when (response.errorCode) {
                        ERROR_STATUS_401 -> {
                            AppController.getInstance()
                                .refreshTokenResponse(
                                    this,
                                    null,
                                    Constants.QUICK_VIEW_DAY,
                                    Constants.REFRESH_TOKEN
                                )

                        }
                        ERROR_STATUS_307, ERROR_STATUS_426 -> response.errorString?.let { error ->
                            MethodClass.showErrorDialog(
                                this,
                                error, response.errorCode
                            )
                        }
                        else -> {
                            viewModel.dayName.value = ""
                            val adapter: QuickViewAdapter =
                                binding.studentListRv.adapter as QuickViewAdapter
                            adapter.studentList = ArrayList()
                            binding.studentListRv.smoothScrollToPosition(Constants.SELECTED_STUDENT_POSITION)
                            adapter.notifyDataSetChanged()
                            MethodClass.hideProgressDialog(this)

                        }
                    }
                }
                else -> {}
            }

        }

        viewModel.quickViewOrderDayLiveData.observe(this@QuickViewForStudentActivity){ response->
            when (response) {
                is Resource.Success -> {
                    response.value.response.raws.data?.let { data ->
                        viewModel.currentDay.value = data.dayDetails.dayNumber
                        UserPreferences.saveAsObject(this@QuickViewForStudentActivity, data.calendar_disable_time, CALENDAR_DISABLE_TIME)
                        quickViewApiCall()
                    }
                    MethodClass.hideProgressDialog(this@QuickViewForStudentActivity)

                }
                is Resource.Failure -> {
                    if (response.errorBody != null) {
                        MethodClass.hideProgressDialog(this@QuickViewForStudentActivity)
                        if (response.errorCode == ERROR_STATUS_401)
                            AppController.getInstance()
                                .refreshTokenResponse(
                                    this@QuickViewForStudentActivity,
                                    null,
                                    Constants.NOTIFICATION_SETTINGS_UPDATE,
                                    Constants.REFRESH_TOKEN
                                )
                        else if (response.errorCode == ERROR_STATUS_307 || response.errorCode == ERROR_STATUS_426)
                            response.errorString?.let { error ->
                                MethodClass.showErrorDialog(
                                    this,
                                    error, response.errorCode
                                )
                            }

                    }

                }
                else -> {}
            }

        }

    }

      
    private fun onViewClick() {

        binding.ivArrowRight.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime1 < 1000) {
                return@setOnClickListener
            }
            viewModel.currentDay.value = viewModel.currentDay.value?.plus(STATUS_ONE)
            if (viewModel.currentDay.value!! > STATUS_FIVE) {
                binding.ivArrowRight.visibility = View.INVISIBLE
            } else
                binding.ivArrowLeft.visibility = View.VISIBLE

            MethodClass.showProgressDialog(this)
            quickViewApiCall()
            lastClickTime1 = SystemClock.elapsedRealtime()
        }

        binding.ivArrowLeft.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime2 < 1000) {
                return@setOnClickListener
            }
            viewModel.currentDay.value = viewModel.currentDay.value?.minus(STATUS_ONE)
            if (viewModel.currentDay.value!! < STATUS_TWO) {
                binding.ivArrowLeft.visibility = View.INVISIBLE
            } else
                binding.ivArrowRight.visibility = View.VISIBLE

            quickViewApiCall()
            lastClickTime2 = SystemClock.elapsedRealtime()
        }

        binding.dashboard.setOnClickListener {
            //Dashboard will be at start position
            Constants.SELECTED_STUDENT_POSITION = STATUS_ZERO

            //Default position 1 change -1
            Constants.SELECTED_ORDER_POSITION = STATUS_ONE

            var intent=Intent(this@QuickViewForStudentActivity, DashBoardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(CALENDAR_DISABLE_TAG,viewModel.calendarDisableTime.value)
            startActivity(intent)
        }

    }




    override fun yesOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
        this.finishAffinity()
    }

    override fun noOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
    }

    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        //Dashboard will be at start position
        Constants.SELECTED_STUDENT_POSITION = position

        //Default position 1 change -1
        Constants.SELECTED_ORDER_POSITION = STATUS_ONE


        startActivity(Intent(this, DashBoardActivity::class.java))
        finish()

    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
//            CustomDialog.showYesNoTypeDialog(
//                this@QuickViewForStudentActivity,
//                resources.getString(R.string.do_you_want_to_exit_this_page_),
//                this@QuickViewForStudentActivity
//            )
            CustomDialog.exitAppDialog(this@QuickViewForStudentActivity,
                object : DialogYesNoListener {
                    override fun yesOnClick(dialog: Dialog, activity: Activity) {
                        dialog.dismiss()
                        finishAffinity()
                    }

                    override fun noOnClick(dialog: Dialog, activity: Activity) {
                        dialog.dismiss()
                    }

                })
        }
    }

}