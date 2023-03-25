package ie.healthylunch.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.NotificationPagingAdapter
import ie.healthylunch.app.adapter.PagingFooterAdapter
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.notificationListPagingModel.NotificationList
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.NotificationRepository
import ie.healthylunch.app.data.viewModel.NotificationViewModel
import ie.healthylunch.app.databinding.ActivityNotificationBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.ALL_TAG
import ie.healthylunch.app.utils.Constants.Companion.CAMPAIGN_ID
import ie.healthylunch.app.utils.Constants.Companion.DELETE_NOTIFICATION_TAG
import ie.healthylunch.app.utils.Constants.Companion.NOTIFICATION_ID_TAG
import ie.healthylunch.app.utils.Constants.Companion.ROWS_TAG
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.TOTAL_NO_OF_ITEMS_PER_PAGE
import java.util.*

class NotificationActivity : BaseActivity<NotificationViewModel, NotificationRepository>(),
    DialogYesNoListener,
    AdapterItemOnclickListener {
    lateinit var notificationPagingAdapter: NotificationPagingAdapter
    lateinit var binding: ActivityNotificationBinding
    private var campaignId: String? = null
    private lateinit var loader: Dialog


    private var deleteNotificationJsonObject = JsonObject()
    var selectedNotificationItemsPosition=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_notification)

        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        init()
        onViewClick()
    }


    private fun init() {
        notificationPagingAdapter = NotificationPagingAdapter(this)
        //get data from intent
        if (intent.extras != null) {
            campaignId = intent.getStringExtra(CAMPAIGN_ID).toString()
            if (!campaignId.isNullOrBlank())
                notificationCountIncreaseApi()
        }

        loader = MethodClass.loaderDialog(binding.root.context)
        /*//adapter set initially
        binding.notificationRv.apply {
            notificationAdapter = NotificationAdapter(ArrayList(), this@NotificationActivity)
            this.adapter = notificationAdapter
            this.isFocusable = false

        }*/








        //get Notification List Response
        //getNotificationListResponse()

        //get Notification Delete Response
        getNotificationDeleteResponse()

        //get Notification Count Increase Response
        getNotificationCountIncreaseResponse()


        //notification ListApi Call
        notificationListApiCall()
    }

    private fun onViewClick() {
        binding.clearAllTv.setOnClickListener {
            deleteNotificationJsonObject = JsonObject()
            deleteNotificationJsonObject = MethodClass.getCommonJsonObject(this)
            deleteNotificationJsonObject.addProperty(NOTIFICATION_ID_TAG, ALL_TAG)

            CustomDialog.showYesNoTypeDialog(
                this,
                resources.getString(R.string.do_you_want_to_delete_all_notifications_),
                this
            )
        }
    }


    override fun getViewModel() = NotificationViewModel::class.java
    override fun getRepository() =
        NotificationRepository(remoteDataSource.buildApi(ApiInterface::class.java))

    override fun onResume() {
        super.onResume()
        //clear all push notifications
        NotificationManagerCompat.from(this).cancelAll()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter("Notification"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }





    @SuppressLint("NotifyDataSetChanged")
    fun notificationListApiCall() {
        /*MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)

        viewModel.notificationList(jsonObject, Constants.TOKEN)*/


        loader.show()
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )
        val jsonObject1 = MethodClass.getCommonJsonObject(this)
        jsonObject1.addProperty(ROWS_TAG, TOTAL_NO_OF_ITEMS_PER_PAGE)
        loginResponse?.response?.raws?.data?.token?.let { token->
            viewModel.getData(jsonObject1,token)
        }
        observeData()
    }


    fun deleteNotification() {
        MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        viewModel.notificationDelete(deleteNotificationJsonObject, Constants.TOKEN)
    }


    fun notificationCountIncreaseApi() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)
        jsonObject.addProperty("campaign_id", campaignId)
        viewModel.notificationCountIncrease(jsonObject, Constants.TOKEN)
    }

    /*@SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun getNotificationListResponse() {
        viewModel.notificationListResponse?.observe(this) {

            when (it) {

                is Resource.Success -> {
                    MethodClass.hideProgressDialog(this)

                    if (it.value.response.raws.data?.isNotEmpty() == true) {
                        viewModel.noDataTextVisible.value = false
                        viewModel.clearAllTextViewVisible.value = true
                        viewModel.recyclerViewVisible.value = true
                    } else {
                        viewModel.noDataTextVisible.value = true
                        viewModel.clearAllTextViewVisible.value = false
                        viewModel.recyclerViewVisible.value = false
                    }

                    notificationAdapter.notificationList = it.value.response.raws.data
                    notificationAdapter.notifyDataSetChanged()

                    viewModel._notificationListResponse.value = null
                    viewModel.notificationListResponse = viewModel._notificationListResponse
                }
                is Resource.Failure -> {
                    if (!Objects.equals(it.errorBody, null)) {
                        it.errorString?.let { errorString ->
                            when (it.errorCode) {
                                401 -> AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.NOTIFICATION_LIST,
                                        Constants.REFRESH_TOKEN
                                    )
                                307, 426 -> MethodClass.showErrorDialog(
                                    this,
                                    errorString,
                                    it.errorCode
                                )
                                else -> {
                                    MethodClass.showErrorDialog(
                                        this,
                                        errorString,
                                        it.errorCode
                                    )
                                }
                            }
                        }
                    }

                    MethodClass.hideProgressDialog(this)
                    viewModel._notificationListResponse.value = null
                    viewModel.notificationListResponse = viewModel._notificationListResponse

                }
                else -> {}
            }

        }

    }*/


    private fun getNotificationDeleteResponse() {
        viewModel.notificationDeleteResponse?.observe(this) {

            when (it) {

                is Resource.Success -> {
                    MethodClass.hideProgressDialog(this)
                    //notificationListApiCall()
                   // notificationPagingAdapter?.notifyItemRemoved(selectedNotificationItemsPosition)
                    notificationPagingAdapter.refresh()
                    viewModel._notificationDeleteResponse.value = null
                    viewModel.notificationDeleteResponse = viewModel._notificationDeleteResponse

                }
                is Resource.Failure -> {
                    if (!Objects.equals(it.errorBody, null)) {

                        it.errorString?.let { errorString ->
                            when (it.errorCode) {
                                401 -> AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.DELETE_NOTIFICATION,
                                        Constants.REFRESH_TOKEN
                                    )
                                307, 426 -> MethodClass.showErrorDialog(
                                    this,
                                    errorString,
                                    it.errorCode
                                )
                                else -> {
                                    MethodClass.showErrorDialog(
                                        this,
                                        errorString,
                                        it.errorCode
                                    )
                                }
                            }
                        }

                    }

                    MethodClass.hideProgressDialog(this)
                    viewModel._notificationDeleteResponse.value = null
                    viewModel.notificationDeleteResponse = viewModel._notificationDeleteResponse
                }
                else -> {}
            }

        }

    }


    private fun getNotificationCountIncreaseResponse() {
        viewModel.notificationCountIncreaseResponse?.observe(this) {

            when (it) {
                is Resource.Success -> {

                    viewModel._notificationCountIncreaseResponse.value = null
                    viewModel.notificationCountIncreaseResponse =
                        viewModel._notificationCountIncreaseResponse
                }
                is Resource.Failure -> {
                    if (!Objects.equals(it.errorBody, null)) {

                        it.errorString?.let { errorString ->
                            when (it.errorCode) {
                                401 -> AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.NOTIFICATION_COUNT_INCREASE,
                                        Constants.REFRESH_TOKEN
                                    )
                                307, 426 -> MethodClass.showErrorDialog(
                                    this,
                                    errorString,
                                    it.errorCode
                                )
                                else -> {
                                    MethodClass.showErrorDialog(
                                        this,
                                        errorString,
                                        it.errorCode
                                    )
                                }
                            }
                        }


                    }

                    viewModel._notificationCountIncreaseResponse.value = null
                    viewModel.notificationCountIncreaseResponse =
                        viewModel._notificationCountIncreaseResponse
                }
                else -> {}
            }

        }

    }


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun onReceive(context: Context, intent: Intent) {
           // notificationListApiCall()
            notificationPagingAdapter.refresh()
            if (intent.extras != null) {
                campaignId = intent.getStringExtra(CAMPAIGN_ID).toString()
                if (!campaignId.isNullOrBlank())
                    notificationCountIncreaseApi()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun yesOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
        deleteNotification()
    }

    override fun noOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        if (tag == DELETE_NOTIFICATION_TAG) {
            val notificationList: List<NotificationList>? =
                arrayList as? List<NotificationList>
            if (!notificationList.isNullOrEmpty() && position > STATUS_MINUS_ONE && position < notificationList.size) {
                deleteNotificationJsonObject = JsonObject()
                deleteNotificationJsonObject = MethodClass.getCommonJsonObject(this)
                deleteNotificationJsonObject.addProperty(
                    NOTIFICATION_ID_TAG,
                    notificationList[position].notificationid.toString()
                )
                selectedNotificationItemsPosition=position

                CustomDialog.showYesNoTypeDialog(
                    this,
                    this.resources.getString(R.string.do_you_want_to_delete_this_notification_),
                    this
                )
            }
        }
    }


    private fun observeData() {

        binding.notificationRv.run{
            layoutManager= LinearLayoutManager(this@NotificationActivity,LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            adapter = notificationPagingAdapter.withLoadStateFooter(
                footer = PagingFooterAdapter { notificationPagingAdapter.retry() }
            )
        }

            viewModel.data_list.observe(this) {
                notificationPagingAdapter.submitData(lifecycle, it)
            }

        notificationPagingAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    MethodClass.showProgressDialog(this)
                }
                is LoadState.NotLoading -> {
                    loader.dismiss()
                    MethodClass.hideProgressDialog(this)
                }
                is LoadState.Error -> {
                    MethodClass.hideProgressDialog(this)

                }
                else -> {
                    MethodClass.hideProgressDialog(this)
                }
            }
            Log.d("TAG_count", "observeData: ${notificationPagingAdapter.itemCount}")
            with((loadState.append.endOfPaginationReached && notificationPagingAdapter.itemCount == STATUS_ZERO)){
                viewModel.recyclerViewVisible.value =!this
                viewModel.clearAllTextViewVisible.value = !this
                viewModel.noDataTextVisible.value =this
            }

        }
        }


    private val onBackPressedCallback=object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
           startActivity(Intent(this@NotificationActivity, DashBoardActivity::class.java))
           finish()
        }
    }
    }
