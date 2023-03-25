package com.merkaaz.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.NotificationsAdapter
import com.merkaaz.app.data.model.*
import com.merkaaz.app.data.viewModel.NotificationViewModel
import com.merkaaz.app.databinding.ActivityNotificationsBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.CustomDialogYesNoClickListener
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsActivity : BaseActivity(), AdapterItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var commonFunctions: CommonFunctions

    lateinit var notificationViewModel: NotificationViewModel
    lateinit var binding: ActivityNotificationsBinding
    private var loader: Dialog? = null
    private var isShowLoader: Boolean = true
    private val notificationsAdapter = NotificationsAdapter(ArrayList(), this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)
        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        binding.viewModel = notificationViewModel
        binding.activity = this
        binding.lifecycleOwner = this

        init()
        onViewClick()
        observeData()
    }

    private fun init() {
        //notification
        binding.rvNotification.adapter = notificationsAdapter
        binding.rvNotification.isFocusable = false

        //initialize loader
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))

        //set setOnRefreshListener to swipe_refresh_layout
        binding.swipeRefreshLayout.setOnRefreshListener(this)

        //call notification list
        notificationViewModel.getNotificationList()
    }

    private fun onViewClick() {
        binding.tvDeleteAll.setOnClickListener {
            MethodClass.customDialogWithTwoButtons(
                this, resources.getString(R.string.delete_all),
                getString(R.string.do_you_want_to_delete_all_notifications_),
                getString(R.string.delete),
                getString(R.string.cancel),
                object : CustomDialogYesNoClickListener {
                    override fun yesClickListener(dialog: Dialog?) {
                        dialog?.dismiss()
                        //call notification delete api for all delete
                        notificationViewModel.notificationDelete(Constants.ALL, "0")
                    }

                    override fun noClickListener(dialog: Dialog?) {
                        dialog?.dismiss()
                    }

                }
            )

        }
        binding.ivDeleteAll.setOnClickListener {
            binding.tvDeleteAll.performClick()
        }
        binding.imgHelp.setOnClickListener {
            startActivity(
                Intent(
                    this@NotificationsActivity,
                    CustomerServiceActivity::class.java
                )
            )
        }

    }

    private fun observeData() {
        //for notification  list
        notificationViewModel.notificationListLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    if (isShowLoader)
                        loader?.show()
                }
                is Response.Success -> {
                    if (isShowLoader)
                        loader?.dismiss()
                    else
                        binding.swipeRefreshLayout.isRefreshing = false
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            val notificationListResponse = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<NotificationModel>() {}.type
                            val notificationModel: NotificationModel? =
                                gson.fromJson(notificationListResponse, dataType)


                            notificationsAdapter.notificationList =
                                notificationModel?.notificationList
                            notificationsAdapter.notifyDataSetChanged()

                            notificationViewModel.isShowNoDataFound.value =
                                notificationModel?.notificationList?.isEmpty() == true
                            notificationViewModel.isShowMainLayout.value =
                                notificationModel?.notificationList?.isNotEmpty()

                        }
                    }

                }
                is Response.Error -> {
                    if (isShowLoader)
                        loader?.dismiss()
                    else
                        binding.swipeRefreshLayout.isRefreshing = false
                    commonFunctions.showErrorMsg(
                        binding.root.context,
                        it.errorCode,
                        it.errorMessage
                    )
                }
                else -> {}
            }
        }

        //notification delete
        notificationViewModel.notificationDeleteLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {

                            //call notification list
                            notificationViewModel.getNotificationList()
                        }
                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(
                        binding.root.context,
                        it.errorCode,
                        it.errorMessage
                    )
                }
                else -> {}
            }
        }


    }


    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {
        val notificationList: List<NotificationItem?>? = arrayList as List<NotificationItem?>?
        val notificationItem = notificationList?.get(position)

        MethodClass.customDialogWithTwoButtons(
            this, resources.getString(R.string.delete),
            getString(R.string.do_you_want_to_delete_this_notification_),
            getString(R.string.delete),
            getString(R.string.cancel),
            object : CustomDialogYesNoClickListener {
                override fun yesClickListener(dialog: Dialog?) {
                    dialog?.dismiss()
                    //call notification delete api for single delete
                    notificationViewModel.notificationDelete(
                        Constants.SINGLE,
                        notificationItem?.notificationId.toString()
                    )
                }

                override fun noClickListener(dialog: Dialog?) {
                    dialog?.dismiss()
                }

            }
        )


    }

    override fun onRefresh() {
        isShowLoader = false
        //call notification api
        notificationViewModel.getNotificationList()
    }


}