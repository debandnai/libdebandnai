package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.NotificationListAdapter
import com.salonsolution.app.data.model.NotificationList
import com.salonsolution.app.data.model.NotificationListModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.NotificationListViewModel
import com.salonsolution.app.databinding.FragmentNotificationListBinding
import com.salonsolution.app.interfaces.NotificationListClickListener
import com.salonsolution.app.interfaces.PositiveNegativeCallBack
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationListFragment : BaseFragment(), NotificationListClickListener {
    private lateinit var binding: FragmentNotificationListBinding
    private lateinit var notificationListViewModel: NotificationListViewModel
    private lateinit var notificationListAdapter: NotificationListAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification_list, container, false)
        notificationListViewModel = ViewModelProvider(this)[NotificationListViewModel::class.java]
        binding.viewModel = notificationListViewModel
        binding.lifecycleOwner = this
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = false,
            isShowBottomNav = false,
            isShowToolbar = false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)
        with(binding) {
            binding.rvNotificationList.visibility = View.GONE
            binding.tvNoNotification.visibility = View.GONE
            binding.tvClearAll.visibility = View.GONE

            notificationListAdapter = NotificationListAdapter(this@NotificationListFragment)
            rvNotificationList.run {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = notificationListAdapter
            }
            binding.tvClearAll.setOnClickListener {
               // Toast.makeText(binding.root.context,"Clear All",Toast.LENGTH_SHORT).show()
                showOrderCancelPopup(null,binding.root.context.getString(R.string.do_you_want_to_delete_all_items),true)
            }
            binding.ivBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        loadData()
    }

    private fun loadData() {
        notificationListViewModel.notificationList(requestBodyHelper.getNotificationListRequest())
    }

    private fun setObserver() {
        notificationListViewModel.notificationListResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    manageResponse(it.data?.response?.data)
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        notificationListViewModel.notificationDeleteResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                   // loadingPopup?.dismiss()
                    loadData()
                    Log.d(mTag, it.toString())
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode,binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

    }

    private fun manageResponse(data: NotificationListModel?) {
        if(data!=null && data.notificationList.size>0){
            binding.rvNotificationList.visibility = View.VISIBLE
            binding.tvNoNotification.visibility = View.GONE
            binding.tvClearAll.visibility = View.VISIBLE
            notificationListAdapter.submitList(data.notificationList.map {
                it.copy()
            })

        }else{
            binding.rvNotificationList.visibility = View.GONE
            binding.tvNoNotification.visibility = View.VISIBLE
            binding.tvClearAll.visibility = View.GONE
        }
    }

    override fun onDeleteClick(position: Int, item: NotificationList) {
       showOrderCancelPopup(item.notificationId,binding.root.context.getString(R.string.do_you_want_to_delete_this_item),false)
    }

    override fun onItemClick(position: Int, item: NotificationList) {
       // Toast.makeText(binding.root.context,"Item click: ${item.notificationId}",Toast.LENGTH_SHORT).show()
    }
    private fun showOrderCancelPopup(id: Int?,msg:String, isAllDelete:Boolean) {
        CustomPopup.showMessageDialogYesNo(
            mContext = binding.root.context,
            description = msg,
            positiveNegativeCallBack = object :
                PositiveNegativeCallBack {
                override fun onPositiveButtonClick() {
                    notificationDelete(id,isAllDelete)
                }

                override fun onNegativeButtonClick() {

                }
            })
    }

    private fun notificationDelete(notificationId: Int?, isAllDelete: Boolean){
        notificationListViewModel.notificationDelete(requestBodyHelper.getNotificationDeleteRequest(notificationId,isAllDelete))
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if(this::notificationListViewModel.isInitialized)
            loadData()
    }

}