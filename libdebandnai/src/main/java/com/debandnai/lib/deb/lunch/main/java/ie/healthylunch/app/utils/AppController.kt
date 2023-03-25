package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.RemoteDataSource
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.RefreshTokenRepository
import ie.healthylunch.app.data.viewModel.AppControllerRefreshTokenViewModel
import ie.healthylunch.app.fragment.AboutUsFragment
import ie.healthylunch.app.fragment.DashBoardFragment
import ie.healthylunch.app.fragment.forgot_password.ForgotPasswordStepOneFragment
import ie.healthylunch.app.fragment.forgot_password.ForgotPasswordStepTwoFragment
import ie.healthylunch.app.fragment.forgot_password.ResetPasswordFragment
import ie.healthylunch.app.fragment.registration.*
import ie.healthylunch.app.fragment.students.EditStudentAllergenFragment
import ie.healthylunch.app.fragment.students.EditStudentProfileFragment
import ie.healthylunch.app.fragment.students.ViewAddedStudentProfileListFragment
import ie.healthylunch.app.ui.*
import ie.healthylunch.app.ui.base.ViewModelFactory
import ie.healthylunch.app.utils.Constants.Companion.LOGIN_CHECK
import ie.healthylunch.app.utils.Constants.Companion.NOTIFICATION_SETTINGS_LIST
import ie.healthylunch.app.utils.Constants.Companion.NOTIFICATION_SETTINGS_UPDATE
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.Constants.Companion.USER_DETAILS
import java.util.*

class AppController : Application(), Application.ActivityLifecycleCallbacks, LifecycleOwner {
    private var loginResponse: LoginResponse? = null
    var isDeActivatedAllStudents: Boolean = false
    var email: String = ""
    private var isFirstTimeForLogout: Boolean = true
    private var isFirstTimeForRefreshToken: Boolean = true
    private var isFirstTimeForDeleteAccount: Boolean = true
    private var showProgress: Boolean = false


    companion object {
        private val remoteDataSource = RemoteDataSource()
        private lateinit var viewModelAppController: AppControllerRefreshTokenViewModel

        private lateinit var refreshTokenRepository: RefreshTokenRepository

        private var appController: AppController? = null
        fun getInstance(): AppController {
            if (appController == null)
                appController = AppController()
            return appController as AppController

        }

    }


    fun setUpViewModel() {
        refreshTokenRepository =
            RefreshTokenRepository(remoteDataSource.buildApi(ApiInterface::class.java))
        val factory = ViewModelFactory(refreshTokenRepository)

        viewModelAppController =
            ViewModelProvider(
                ViewModelStore(),
                factory
            )[AppControllerRefreshTokenViewModel::class.java]
    }


    fun logoutAppController(activity: Activity?, showProgress: Boolean) {
        this.showProgress = showProgress
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                USER_DETAILS
            )

        TOKEN = ""
        loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        email = loginResponse?.response?.raws?.data?.username.toString()

        if (isDeActivatedAllStudents) {
            UserPreferences.saveAsObject(activity, IsLogin(0), LOGIN_CHECK)
            activity?.startActivity(
                Intent(activity, RegistrationActivity::class.java)
                    .putExtra("for", "add_student")
                    .putExtra("email", email)
            )
            return
        }

        if (TOKEN.isNotEmpty()) {
            activity?.let {
                if (showProgress)
                    MethodClass.showProgressDialog(activity)
                if (isFirstTimeForLogout) {
                    isFirstTimeForLogout = false
                    getLogoutResponse(activity)
                }


                val jsonObject = MethodClass.getCommonJsonObject(activity)
                viewModelAppController.logout(jsonObject, TOKEN)
            }
        }


    }


    fun userDeleteAccountApi(activity: Activity?) {
        activity?.let {
            MethodClass.showProgressDialog(activity)
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    activity,
                    USER_DETAILS
                )

            TOKEN = ""
            loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
            REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()

            if (isFirstTimeForDeleteAccount) {
                isFirstTimeForDeleteAccount = false
                getUserDeleteAccountResponse(activity)
            }
            val jsonObject = MethodClass.getCommonJsonObject(activity)
            viewModelAppController.userDeleteAccount(jsonObject, TOKEN)
        }
    }

    private fun getLogoutResponse(activity: Activity) {
        viewModelAppController.logoutResponse?.observe(this) {
            when (it) {
                is Resource.Success -> navigateToLoginActivity(activity)
                is Resource.Failure -> navigateToLoginActivity(activity)
                else -> {}
            }
        }
    }

    private fun getUserDeleteAccountResponse(activity: Activity) {
        viewModelAppController.deleteAccountResponse?.observe(this) {
            when (it) {
                is Resource.Success -> navigateToLoginAfterDeleteAccount(activity)

                is Resource.Failure -> navigateToLoginAfterDeleteAccount(activity)
                else -> {}
            }
        }
    }

    private fun refreshTokenApiCall(activity: Activity, refreshToken: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("refreshToken", refreshToken)
        loginResponse =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                USER_DETAILS
            )
        loginResponse?.response?.raws?.data?.token?.let {
            TOKEN = it
            viewModelAppController.refreshToken(jsonObject, TOKEN)
            MethodClass.showProgressDialog(activity)
        }


    }




    fun refreshTokenResponse(
        activity: Activity,
        fragment: Fragment?,
        apiName: String,
        refreshToken: String?
    ) {

        if (isFirstTimeForRefreshToken) {
            isFirstTimeForRefreshToken = false
            getRefreshTokenResponse(activity, fragment, apiName)
        }

        refreshToken?.let { refreshTokenApiCall(activity, it) }
    }



    private fun getRefreshTokenResponse(
        activity: Activity,
        fragment: Fragment?,
        apiName: String,
    ) {
        viewModelAppController.refreshTokenResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    Log.e("TAG", "refreshTokenResponse: ")


                    //update the token and refresh token in user details preference
                    loginResponse?.response?.raws?.data.let { it1 ->
                        it1?.token = it.value.response.raws.data.token
                        TOKEN = it.value.response.raws.data.token
                    }

                    loginResponse?.response?.raws?.data.let { it1 ->
                        it1?.refreshToken = it.value.response.raws.data.refreshToken
                        REFRESH_TOKEN = it.value.response.raws.data.refreshToken
                    }


                    //save the updated data of user details
                    UserPreferences.saveAsObject(activity, loginResponse, USER_DETAILS)

                    checkActivityFragmentApiName(activity, fragment, apiName)
                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(activity)
                    Toast.makeText(
                        activity,
                        "Your session has expired",
                        Toast.LENGTH_LONG
                    ).show()

                    UserPreferences.saveAsObject(this, null, USER_DETAILS)

                    val intent =
                        Intent(Intent(activity, LoginActivity::class.java))
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    activity.startActivity(intent)
                }
            }
        }

    }



    private fun checkActivityFragmentApiName(
        activity: Activity,
        fragment: Fragment?,
        apiName: String
    ) {
        if (!Objects.equals(fragment, null)) {
            when (fragment) {

                is DashBoardFragment -> {
                    if (Objects.equals(apiName, Constants.STUDENT_LIST))
                        fragment.getStudentList()

                    if (Objects.equals(apiName, Constants.LOGOUT_RESPONSE))
                        logoutAppController(activity as DashBoardActivity, true)





                    if (Objects.equals(apiName, Constants.NEXT_DAY_ORDER))
                        fragment.replacedOrderForNextWeekSameDayApiCall()

                    if (Objects.equals(apiName, Constants.NOTIFICATION_COUNT_API) or
                        Objects.equals(apiName, Constants.PARENT_DETAILS) or
                        Objects.equals(apiName, Constants.SIX_DAY_MENU_LIST) or
                        Objects.equals(apiName, Constants.DASHBOARD_BOTTOM_CALENDER)
                    )
                        fragment.dashboardList()

                }
                is ForgotPasswordStepOneFragment -> {
                    if (Objects.equals(apiName, Constants.SEND_OTP))
                        fragment.sendOtp()
                }
                is ForgotPasswordStepTwoFragment -> {
                    if (Objects.equals(apiName, Constants.PARENT_EMAIL_VERIFICATION))
                        fragment.viewModel.verifyOtpApiCall(
                            activity as ForgotPasswordActivity
                        )
                    if (Objects.equals(apiName, Constants.SEND_OTP))
                        fragment.viewModel.reSendOtp(activity)
                }
                is ResetPasswordFragment -> {
                    if (Objects.equals(apiName, Constants.RESET_PASSWORD))
                        fragment.viewModel.resetPasswordApiCall(activity as ForgotPasswordActivity)

                }
                is AddStudentNewStepTwoFragment -> {
                    if (Objects.equals(apiName, Constants.STUDENT_LIST)) {
                        activity as RegistrationActivity
                        fragment.getStudentList()
                    }


                    if (Objects.equals(apiName, Constants.SCHOOL_CLASS_LIST))
                        fragment.getClassList()

                }

                is AddStudentNewStepOneFragment -> {
                    if (Objects.equals(apiName, Constants.SCHOOL_LIST))
                        fragment.getSchoolList()
                    if (Objects.equals(apiName, Constants.COUNTRY_LIST))
                        fragment.getCountyList()

                }
                is AllergenFragment -> {
                    if (Objects.equals(apiName, Constants.ALLERGEN_LIST))
                        fragment.allergenListApiCall()
                    if (Objects.equals(apiName, Constants.SAVE_ALLERGENS_LIST))
                        fragment.saveAllergenApiCall()


                }
                is RegistrationOtpFragment -> {
                    if (Objects.equals(apiName, Constants.NEW_PARENT_EMAIL))
                        fragment.getParentEmailResponseFromAppController()
                }
                is AllergenProductRemoveConfirmationFragment -> {
                    if (Objects.equals(apiName, Constants.REMOVE_PRODUCTS_HAVING_MAY_ALLERGEN))
                        fragment.removeProductMayAllergenApiCall()
                }

                /*is QuickViewForStudentFragment -> {
                    if (Objects.equals(apiName, Constants.QUICK_VIEW_DAY))
                        fragment.viewModel.quickViewResponse()

                }*/

                is EditStudentAllergenFragment -> {
                    if (Objects.equals(apiName, Constants.ALLERGEN_LIST))
                        fragment.viewModel.allergenListApiCall(activity as DashBoardActivity)
                    if (Objects.equals(apiName, Constants.SAVE_ALLERGEN))
                        fragment.save()
                }
                is ViewAddedStudentProfileListFragment -> {
                    if (Objects.equals(apiName, Constants.STUDENT_LIST))
                        fragment.studentListApiCall()
                    if (Objects.equals(apiName, Constants.LOGOUT_RESPONSE))
                        logoutAppController(activity as DashBoardActivity, true)
                    if (Objects.equals(apiName, Constants.DELETE_STUDENT))
                        fragment.viewModel.getDeleteStudent(
                            activity as DashBoardActivity
                        )
                    if (Objects.equals(apiName, Constants.ALLERGEN_COUNT))
                        fragment.studentAllergenCountCall()

                }
                is EditStudentProfileFragment -> {
                    if (Objects.equals(apiName, Constants.STUDENT_DETAILS))
                        fragment.viewModel.studentDetailsApiCall(
                            activity as DashBoardActivity
                        )
                    if (Objects.equals(apiName, Constants.SCHOOL_CLASS_LIST))
                        fragment.viewModel.studentClassListApiCall(activity as DashBoardActivity)
                    if (Objects.equals(apiName, Constants.EDIT_STUDENT))
                        fragment.viewModel.saveStudentDetailsApiCall(
                            activity as DashBoardActivity
                        )
                }
                is DeisStudentUniqueCodeFragment -> {
                    if (Objects.equals(apiName, Constants.ADD_NEW_STUDENT_DEIS))
                        fragment.deisStudentAdd()
                }
                is DeisStudentSchoolDetailsFragment -> {
                    if (Objects.equals(apiName, Constants.EDIT_STUDENT))
                        fragment.saveStudentDetailsApiCall()
                }
                is AboutUsFragment -> {
                    if (Objects.equals(apiName, Constants.ABOUT_US))
                        fragment.getPrivacyPolicy()

                }


            }

        } else {
            when (activity) {

                is PrivacyPolicyActivity -> {
                    if (Objects.equals(apiName, Constants.PRIVACY_POLICY))
                        activity.setTextView()
                }
                is TermsOfUseActivity -> {
                    if (Objects.equals(apiName, Constants.TERMS_OF_USE))
                        activity.setTextView()
                }

                is ParentProfileViewActivity -> {
                    if (Objects.equals(apiName, Constants.PARENT_DETAILS))
                        activity.setParentDetails()
                }

                is NotificationActivity -> {
                    if (Objects.equals(apiName, Constants.NOTIFICATION_LIST)) {
                        activity.notificationListApiCall()
                    }
                    if (Objects.equals(apiName, Constants.DELETE_NOTIFICATION)) {
                        activity.deleteNotification()
                    }
                    if (Objects.equals(apiName, Constants.NOTIFICATION_COUNT_INCREASE)) {
                        activity.notificationCountIncreaseApi()
                    }


                }
                is ProductActivity -> {
                    if (Objects.equals(apiName, Constants.PRODUCT_LIST_BY_MENU_TEMPLATE))
                        activity.getProductLitByMenuTemplate()
                    if (Objects.equals(apiName, Constants.CALORIE_METER)) {
                        activity.isOrderedGroupPosition = 0
                        ///activity.getCalorieMeter()
                    }
                    if (Objects.equals(apiName, Constants.LOGOUT_RESPONSE))
                        logoutAppController(activity as DashBoardActivity, true)

                    if (Objects.equals(apiName, Constants.SAVE_MENU_FOR_SINGLE_DAY))
                        activity.saveMenu()
                    if (Objects.equals(apiName, Constants.SINGLE_DAY_ORDER_DATE_DISPLAY))
                        activity.singleDayOrderDateDisplay()
                    /*if (Objects.equals(apiName, Constants.STUDENT_ALLOWED_ORDER))
                        activity.getStudentAllowedOrder()*/
                }

                is NotificationOnOffActivity -> {

                    if (Objects.equals(
                            apiName,
                            NOTIFICATION_SETTINGS_LIST
                        ) or Objects.equals(apiName, NOTIFICATION_SETTINGS_UPDATE)
                    )
                        activity.notificationSettingsListApiCall()

                }
                is FeedbackActivity -> {
                    if (Objects.equals(apiName, Constants.FEEDBACK_SUBMIT))
                        activity.viewModel.submitFeedBack(activity)
                    /*if (Objects.equals(apiName, Constants.FEEDBACK_LIST))
                        activity.viewModel.getFeedBackList(activity)*/

                }

                is QuickViewForStudentActivity -> {
                    if (Objects.equals(apiName, Constants.QUICK_VIEW_DAY))
                        activity.quickViewApiCall()

                }

                is ChangePasswordActivity -> {
                    if (Objects.equals(apiName, Constants.PARENT_CHANGE_PASSWORD))
                        activity.viewModel.changePassWord(activity)
                    if (Objects.equals(apiName, Constants.LOGOUT_RESPONSE))
                        activity.viewModel.logoutUser(activity)

                }

                is CalendarActivity -> {
                    if (Objects.equals(apiName, Constants.STUDENT_LIST))
                        activity.viewModel.setViewPagerFromAppController()
                    if (Objects.equals(apiName, Constants.HOLIDAY_LIST))
                        activity.viewModel.holidayListApi()
                    if (Objects.equals(apiName, Constants.LOGOUT_RESPONSE))
                        logoutAppController(activity as DashBoardActivity, true)

                    if (Objects.equals(apiName, Constants.DELETE_HOLIDAY))
                        activity.viewModel.deleteHolidayApi()

                    if (Objects.equals(apiName, Constants.SAVE_HOLIDAY))
                        activity.viewModel.saveHolidayApi()

                    if (Objects.equals(apiName, Constants.SAVE_HOLIDAY_SESSION))
                        activity.viewModel.allDayAsHolidayForAppController()
                }

                is MenuTemplateActivity -> {
                    if (Objects.equals(apiName, Constants.MENU_TEMPLATE_LIST))
                        activity.getMenuTemplate()
                    if (Objects.equals(apiName, Constants.STUDENT_LIST))
                        activity.getStudentListResponse()
                    if (Objects.equals(apiName, Constants.LOGOUT_RESPONSE))
                        logoutAppController(activity as DashBoardActivity, true)

                }
                is ProductActivity -> {
                    if (Objects.equals(apiName, Constants.FAVORITES_LIST))
                        activity.getFavoritesList()


                }

                is EditParentProfileActivity -> {
                    if (Objects.equals(apiName, Constants.EDIT_PROFILE))
                        activity.save()
                }
                is WalletViewActivity -> {
                    if (Objects.equals(apiName, Constants.DELETE_CARD))
                        activity.deleteCardApiCall()
                    if (Objects.equals(apiName, Constants.CARD_DETAILS))
                        activity.cardListApiCall()

                }
                is ParentTopUpNowActivity -> {
                    if (Objects.equals(apiName, Constants.DELETE_CARD))
                        activity.viewModel.deleteCardApiCall()
                    if (Objects.equals(apiName, Constants.WALLET_MANUAL_TOP_UP))
                        activity.viewModel.walletManualTopUpApiCall()

                }
                is AddNewCardActivity -> {
                    if (Objects.equals(apiName, Constants.INITIATE_PAYMENT))
                        activity.viewModel.initiatePaymentIntentApiCall()
                    if (Objects.equals(apiName, Constants.UPDATE_STRIPE_BALANCE))
                        activity.viewModel.updateStripeBalanceApiCall()

                }
                /*is ListAllWalletTransactionActivity -> {
                    if (Objects.equals(apiName, Constants.TRANSACTION_LIST))
                        activity.viewModel.adapterNotifyDataSetChanged()
                }*/
                is ParentTopUpByVoucherActivity -> {
                    if (Objects.equals(apiName, Constants.CARD_DETAILS))
                        activity.viewModel.cardListApiCall()
                    if (Objects.equals(apiName, Constants.COUPON_VALIDATION))
                        activity.viewModel.couponCodeValidationApiCall()
                }


            }
        }

    }


    override fun onCreate() {
        super.onCreate()
        setUpViewModel()
        //initialize the loginResponse object
        loginResponse =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                USER_DETAILS
            )
        Log.e("setUpViewModel", "onCreate: ")
        NotificationUtils.createNotificationChannel(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }


    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun getLifecycle(): Lifecycle {
        return ProcessLifecycleOwner.get().lifecycle
    }


    private fun navigateToLoginActivity(activity: Activity) {
        if (showProgress)
            MethodClass.hideProgressDialog(activity)
        UserPreferences.saveAsObject(activity, IsLogin(0), LOGIN_CHECK)
        UserPreferences.saveAsObject(activity, null, USER_DETAILS)

        viewModelAppController._logoutResponse.value = null
        viewModelAppController.logoutResponse = viewModelAppController._logoutResponse

        if (showProgress) {
            val intent =
                Intent(Intent(activity, LoginActivity::class.java))
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        }
    }

    private fun navigateToLoginAfterDeleteAccount(activity: Activity) {
        MethodClass.hideProgressDialog(activity)
        UserPreferences.saveAsObject(activity, IsLogin(0), LOGIN_CHECK)
        UserPreferences.saveAsObject(activity, null, USER_DETAILS)

        viewModelAppController._deleteAccountResponse.value = null
        viewModelAppController.deleteAccountResponse =
            viewModelAppController._deleteAccountResponse


        val intent =
            Intent(Intent(activity, LoginActivity::class.java))
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
    }
}