package ie.healthylunch.app.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.NotificationOnOffAdapter
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.notificationSettingsModel.DataItem
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.NotificationRepository
import ie.healthylunch.app.data.viewModel.NotificationOnOffViewModel
import ie.healthylunch.app.databinding.ActivityNotificationOnOffBinding
import ie.healthylunch.app.databinding.NotificationSettingsLayoutBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import java.util.*

class NotificationOnOffActivity :
    BaseActivity<NotificationOnOffViewModel, NotificationRepository>(),AdapterItemOnclickListener {
    lateinit var binding: ActivityNotificationOnOffBinding
    lateinit var notificationOnOffAdapter: NotificationOnOffAdapter
    var notificationCurrentItems= STATUS_MINUS_ONE
    var notificationSettingsDialog:Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding=
            DataBindingUtil.setContentView(this, R.layout.activity_notification_on_off)

        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        //viewModel.recyclerView = binding?.notificationRv!!


        init()
        viewOnClick()
    }

    private fun viewOnClick() {
        binding.tvTapHare?.setOnClickListener {
            goNotificationSettings()
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
        notificationSettingsListApiCall()
        if (areNotificationsEnabled()){
             binding?.tvNotificationNotEnableMessage?.visibility= View.GONE
             binding?.tvTapHare?.visibility= View.GONE
        }
        else{
            binding?.tvNotificationNotEnableMessage?.visibility= View.VISIBLE
            binding?.tvTapHare?.visibility= View.VISIBLE
        }
    }

    override fun getViewModel() = NotificationOnOffViewModel::class.java

    override fun getRepository() =
        NotificationRepository(remoteDataSource.buildApi(ApiInterface::class.java))

    fun init(){
        binding.rlNotification.visibility = View.GONE

        //Notification Settings List Response
        getNotificationSettingsListResponse()

        //Notification Settings Response
        getNotificationSettingsResponse()

        notificationOnOffAdapter = NotificationOnOffAdapter(
            ArrayList(), this,
            false
        )
        binding?.notificationRv?.adapter = notificationOnOffAdapter
        binding?.notificationRv?.isFocusable = false

        notificationSettingsDialog= Dialog(this)
    }

    fun notificationSettingsListApiCall() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )
        val jsonObject = MethodClass.getCommonJsonObject(this)
        MethodClass.showProgressDialog(this)
        loginResponse?.response?.raws?.data?.token?.let { token->
            viewModel.notificationSettingsList(jsonObject, token)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkNotificationPermission(){
        if(!areNotificationsEnabled()){

            notificationSettingsDialog?.window?.let { window ->
                window.setBackgroundDrawableResource(R.color.transparent)
                window.decorView.setBackgroundResource(android.R.color.transparent)
                window.setDimAmount(0.0f)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val notificationSettingsLayoutBinding = NotificationSettingsLayoutBinding.inflate(
                LayoutInflater.from(this@NotificationOnOffActivity))
            notificationSettingsDialog?.setContentView(notificationSettingsLayoutBinding.root)
            notificationSettingsDialog?.setCancelable(false)

            //notificationSettingsLayoutBinding.contTv.text = getString(R.string.there_are_some_service_notifications_which_mUST_be_sent_to_ensure_that_your_child_receives_their_lunches_Please_follow_the_link_below_to_turn_on_important_notifications_you_can_then_edit_your_general_notifications_choice_above)
            //binding.settingsTv.setTextColor(ContextCompat.getColor(binding.settingsTv.context,R.color.blue))
            notificationSettingsLayoutBinding.btnSettings.setOnClickListener {
                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@NotificationOnOffActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        else
                            goNotificationSettings()
                    }
                    goNotificationSettings()
                }
            }

            notificationSettingsLayoutBinding.btnCancel.setOnClickListener {
                notificationSettingsDialog?.dismiss()
            }

            notificationSettingsDialog?.show()
        }


    }

    override fun onPause() {
        super.onPause()
        if (notificationSettingsDialog?.isShowing == true){
            notificationSettingsDialog?.dismiss()
        }
    }
    fun goNotificationSettings(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val settingsIntent: Intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            //.putExtra(EXTRA_CHANNEL_ID, "general_channel_id")
            //val uri:Uri=Uri.fromParts("package", packageName, null);
            startActivity(settingsIntent)
        }
        else {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private var notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

        }






    @RequiresApi(Build.VERSION_CODES.P)
    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        if (Objects.equals(tag, "notificationSetting")) {
            val notificationSettings: List<DataItem> =
                arrayList as List<DataItem>
            notificationCurrentItems=position
            val jsonObject = MethodClass.getCommonJsonObject(this)
            jsonObject.addProperty("notification_type", notificationSettings[position].type)
            MethodClass.showProgressDialog(this)

            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    this,
                    Constants.USER_DETAILS
                )

            loginResponse?.response?.raws?.data?.token?.let {token->

                viewModel.notificationSettingsUpdate(jsonObject, token)
            }



        }
    }


    private fun areNotificationsEnabled(): Boolean {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (!manager.areNotificationsEnabled()) {
                    return false
                }
                val channels = manager.notificationChannels
                for (channel in channels) {
                    if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                        return false
                    }
                }
                true
            } else {
                NotificationManagerCompat.from(this).areNotificationsEnabled()
            }


    }


    fun getNotificationSettingsListResponse() {
        viewModel.notificationSettingsLiveData.observe(this){ response->
            when(response){

                is Resource.Success -> {
                    binding.rlNotification.visibility = View.VISIBLE

                    if (response.value.response.raws.data!!.isNotEmpty()) {


                        if(!areNotificationsEnabled()){
                            for (i in response.value.response.raws.data.indices)
                                response.value.response.raws.data[i].status=STATUS_MINUS_ONE
                        }
                        viewModel.notificationSettingsList = response.value.response.raws.data
                        notificationOnOffAdapter.notificationSettingList = viewModel.notificationSettingsList!!
                        notificationOnOffAdapter.isNotificationEnable =areNotificationsEnabled()
                        notificationOnOffAdapter.notifyItemRangeChanged(Constants.STATUS_ZERO, response.value.response.raws.data.size)
                    }
                    MethodClass.hideProgressDialog(this)



                }
                is Resource.Failure -> {
                    if (response.errorBody != null) {
                        MethodClass.hideProgressDialog(this)
                        if (response.errorCode == 401)
                            AppController.getInstance()
                                .refreshTokenResponse(
                                    this,
                                    null,
                                    Constants.NOTIFICATION_SETTINGS_LIST,
                                    Constants.REFRESH_TOKEN
                                )
                        else if (response.errorCode == 307 || response.errorCode == 426)
                            response.errorString?.let { it1 ->
                                MethodClass.showErrorDialog(
                                    this,
                                    it1, response.errorCode
                                )
                            }


                    }

                }
                else -> {}
            }

        }

    }


    fun getNotificationSettingsResponse() {
        viewModel.notificationSettingUpdateLiveData.observe(this){ response->


            when (response) {

                is Resource.Success -> {


                        MethodClass.hideProgressDialog(this)
                       // viewModel.notificationSettingsListApiCall(this)
                    viewModel.notificationSettingsList?.let {notificationSettingsList->
                        if (notificationSettingsList[notificationCurrentItems].status== STATUS_ZERO) {
                            notificationSettingsList[notificationCurrentItems].status=
                                STATUS_ONE
                        }
                        else{
                            notificationSettingsList[notificationCurrentItems].status=
                                STATUS_ZERO
                        }
                        notificationOnOffAdapter?.notificationSettingList=viewModel.notificationSettingsList!!
                        notificationOnOffAdapter?.notifyItemChanged(notificationCurrentItems)
                    }

                        CustomDialog.showOkTypeDialog(
                            this,
                            response.value.response.raws.successMessage,
                            object : DialogOkListener {
                                override fun okOnClick(dialog: Dialog, activity: Activity) {
                                    dialog.dismiss()
                                }
                            })






                }
                is Resource.Failure -> {
                    if (response.errorBody != null) {
                        MethodClass.hideProgressDialog(this)
                        if (response.errorCode == 401)
                            AppController.getInstance()
                                .refreshTokenResponse(
                                    this,
                                    null,
                                    Constants.NOTIFICATION_SETTINGS_UPDATE,
                                    Constants.REFRESH_TOKEN
                                )
                        else if (response.errorCode == 307 || response.errorCode == 426)
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


    }
