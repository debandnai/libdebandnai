package com.merkaaz.app.ui.fragments

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.merkaaz.app.R
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.data.viewModel.DashboardViewModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.databinding.FragmentMyAccountBinding
import com.merkaaz.app.interfaces.CustomDialogYesNoClickListener
import com.merkaaz.app.ui.activity.*
import com.merkaaz.app.ui.base.BaseFragment
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.MY_DELIVERY_ADDRESS_FROM_MY_ACCOUNT
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class MyAccountFragment : BaseFragment() {
    lateinit var binding: FragmentMyAccountBinding
    private var loader: Dialog? = null
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false)

//        myAccountViewModel = ViewModelProvider(this)[MyAccountViewModel::class.java]
        binding.viewModel = dashboardViewModel
        binding.lifecycleOwner = this

        initialise()
        onViewClick()
        observeData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.isShowToolbar.value = false
        dashboardViewModel.isShowBottomNavigation.value = true
        dashboardViewModel.isSetCollapsingToolbar.value = false
        dashboardViewModel.pendingapprovalStat.value = true
        dashboardViewModel.isShowHelpLogo.value = false
//        binding.root.context?.let {
//            (it as DashBoardActivity).setUserProfileData()
//        }
        activity?.let {
            (it as? DashBoardActivity)?.setUserProfileData()
        }
        dashboardViewModel.getCartCount()
        Log.d(TAG, "onResume:myfrag $loginModel.isActive")
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onResume:myfragPP $loginModel.isActive")
    }

    private fun observeData() {
        dashboardViewModel.logOutLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            binding.root.context?.let { context ->
                                MethodClass.logOut(context, sharedPreff)
                            }
                        }

                    }
                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
            }
        }
    }


    private fun initialise() {

        //initialize the loader
        binding.root.context?.let {
            loader = MethodClass.custom_loader(it, getString(R.string.please_wait))
        }

    }


    private fun onViewClick() {
        binding.tvProfileEdit.setOnClickListener {
            binding.root.context?.let {
                startActivity(
                    Intent(
                        it,
                        UserUpdateProfileActivity::class.java
                    )
                )
            }
        }
        binding.rbMyOrder.setOnClickListener {
            activity?.let {
                if (loginModel.isActive == 1) {
                    startActivity(
                        Intent(
                            requireContext(),
                            MyOrderActivity::class.java
                        )
                    )
                } else {
                    MethodClass.custom_msg_dialog(
                        it,
                        resources.getString(R.string.you_are_not_an_approved_user)
                    )?.show()
                }
            }
        }

        binding.rbNotification.setOnClickListener {
            binding.root.context?.let {
                startActivity(
                    Intent(
                        it,
                        NotificationsActivity::class.java
                    )
                )
            }
        }

        binding.rbNotification.setOnClickListener {
            binding.root.context?.let {
                startActivity(
                    Intent(
                       it,
                        NotificationsActivity::class.java
                    )
                )
            }
        }
        binding.rbCustomerService.setOnClickListener {
            binding.root.context?.let {
                startActivity(
                    Intent(
                        it,
                        CustomerServiceActivity::class.java
                    )
                )
            }
        }

        binding.rbMyDeliveryAddress.setOnClickListener {
            binding.root.context?.let { context ->
                val loginModel: LoginModel = sharedPreff.get_logindata()

                val intent = Intent(
                    context,
                    MapsActivity::class.java
                )
                intent.putExtra(Constants.ADDRESS1, loginModel.address1)
                intent.putExtra(Constants.ADDRESS2, loginModel.address2)
                intent.putExtra(Constants.REFERENCE_POINT, loginModel.referencePoint)
                intent.putExtra(Constants.MY_DELIVERY_ADDRESS, MY_DELIVERY_ADDRESS_FROM_MY_ACCOUNT)
                startActivity(intent)
            }
        }
        binding.rbLogout.setOnClickListener {
            binding.root.context?.let { context ->
                MethodClass.customDialogWithTwoButtons(
                    context,
                    context.resources.getString(R.string.logout),
                    context.resources.getString(R.string.are_you_sure_want_to_log_out_),
                    context.resources.getString(R.string.logout),
                    context.resources.getString(R.string.cancel),
                    object : CustomDialogYesNoClickListener {
                        override fun yesClickListener(dialog: Dialog?) {
                            dialog?.dismiss()
                            dashboardViewModel.logOut(context)
                        }

                        override fun noClickListener(dialog: Dialog?) {
                            dialog?.dismiss()
                        }

                    }

                )

            }

        }

        binding.tvLocationEdit.setOnClickListener {
            binding.root.context?.let { context ->
                val loginModel: LoginModel = sharedPreff.get_logindata()

                val intent = Intent(
                    context,
                    MapsActivity::class.java
                )
                intent.putExtra(Constants.ADDRESS1, loginModel.address1)
                intent.putExtra(Constants.ADDRESS2, loginModel.address2)
                intent.putExtra(Constants.REFERENCE_POINT, loginModel.referencePoint)
                startActivity(intent)
            }

        }


    }

//    private fun setUserProfileData() {
//        binding.root.context?.let { context ->
//            //set ph number
//            myAccountViewModel.mobileNo.value =
//                "${Constants.ANGOLA_COUNTRY_CODE} ${SharedPreff.getPhone(context)}"
//
//            //set address
//            val addressList = MethodClass.getAddressList(
//                context,
//                SharedPreff.get_logindata(context).address1,
//                SharedPreff.get_logindata(context).address2,
//                SharedPreff.get_logindata(context).referencePoint
//            )
//            myAccountViewModel.userLocation.value = TextUtils.join(", ", addressList)
//
//            //set user name
//            myAccountViewModel.userName.value =
//                SharedPreff.get_logindata(context).vendorName
//
//            //set user image
//            myAccountViewModel.imgUrl.value =
//                SharedPreff.get_logindata(context).profileImage
//        }
//    }
}