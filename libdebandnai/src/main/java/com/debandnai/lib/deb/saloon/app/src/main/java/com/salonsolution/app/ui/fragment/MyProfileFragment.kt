package com.salonsolution.app.ui.fragment

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CountryListModel
import com.salonsolution.app.data.network.ResponseMessageConverter
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.data.viewModel.MyProfileViewModel
import com.salonsolution.app.databinding.FragmentMyProfileBinding
import com.salonsolution.app.interfaces.DialogButtonClick
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.fragment.UploadPhotoDialogFragment.Companion.CAMERA_URI
import com.salonsolution.app.ui.fragment.UploadPhotoDialogFragment.Companion.GALLERY_URI
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var myProfileViewModel: MyProfileViewModel
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private var adapter: ArrayAdapter<String>? = null

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    @Inject
    lateinit var userSharedPref: UserSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)
        myProfileViewModel = ViewModelProvider(this)[MyProfileViewModel::class.java]
        binding.viewModel = myProfileViewModel
        binding.lifecycleOwner = this
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = false,
            isShowBottomNav = false,
            isShowToolbar = true
        )


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDestinationResult()
        initView()
        setObserver()
    }

    private fun initView() {
        //KeyboardUtil(requireActivity(),binding.scrollViewMain).enable()
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)
        myProfileViewModel.firstName.value = userSharedPref.getLoginData().customerFName
        myProfileViewModel.lastName.value = userSharedPref.getLoginData().customerLName
        loadProfileData()
        with(binding) {
            ibUploadImage.setOnClickListener {
                val action =
                    MyProfileFragmentDirections.actionMyProfileFragmentToUploadPhotoDialogFragment()
                findNavController().navigate(action)
            }
            spCountryCode.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = parent?.getItemAtPosition(position) as? String
                    myProfileViewModel.selectedCountryCode.value = item
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

            ivProfile.setOnClickListener {
                viewPhoto()
            }
        }
    }

    private fun loadProfileData() {
        myProfileViewModel.getProfileDetails(requestBodyHelper.getProfileDetailsRequest())
//        myProfileViewModel.initProfileData(userSharedPref.getLoginData())
//        Log.d("tag","${userSharedPref.getLoginData().profileImage}")
    }

    private fun setObserver() {
        myProfileViewModel.errorResetObserver.observe(viewLifecycleOwner) {
            Log.d("tag", "changed.")
            //This blank observer is required for MediatorLiveData
            //MediatorLiveData observe other LiveData objects
        }

        myProfileViewModel.updateProfileLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    // loadingPopup?.show()
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    myProfileViewModel.isShowLoader.value = false
                    showSuccessfullyUpdatePopup()
                }
                is ResponseState.Error -> {
                    Log.d(mTag, it.errorMessage.toString())
                    loadingPopup?.dismiss()
                    myProfileViewModel.isShowLoader.value = false
                    val msg = if(it.errorCode==400)
                        ResponseMessageConverter.getUpdateProfileErrorMessage(binding.root.context,it.errorMessage)
                    else
                        it.errorMessage
                    handleApiError(it.isNetworkError,it.errorCode, msg)
                }
            }
        }

        myProfileViewModel.profileDetailsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    // loadingPopup?.show()
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    userSharedPref.setProfileDetails(it.data?.response?.data)
                    myProfileViewModel.initProfileData(it.data?.response?.data)
                    updateAdapter()
                }
                is ResponseState.Error -> {
                    Log.d(mTag, it.toString())
                    loadingPopup?.dismiss()

                }
            }
        }

        myProfileViewModel.isShowLoader.observe(viewLifecycleOwner) {
            if (it)
                loadingPopup?.show()
        }

        myProfileViewModel.countriesDetailsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    // loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    // loadingPopup?.dismiss()
                    updateCountryCode(it.data?.response?.data)
                    Log.d(mTag, it.toString())

                }
                is ResponseState.Error -> {
                    //  loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

    }

    private fun updateAdapter() {
        val spinnerPosition = adapter?.getPosition( myProfileViewModel.selectedCountryCode.value) ?: 0
        binding.spCountryCode.setSelection(spinnerPosition)
    }

    private fun updateCountryCode(data: CountryListModel?) {
        data?.let {
            // val code = arrayOf("+91","+344","+677")
            //  val customArrayAdapter = CustomArrayAdapter(binding.root.context,R.layout.spinner_layout,it.countryList)
            val codeList = it.countryList.map { item ->
                item.countryCode
            }
            adapter = ArrayAdapter(binding.root.context, R.layout.spinner_layout, codeList)
            binding.spCountryCode.adapter = adapter
            updateAdapter()
        }
    }

    private fun getDestinationResult() {
        val currentBackStackEntry = findNavController().currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Uri>(GALLERY_URI)?.observe(currentBackStackEntry) {
            manageUri(it, false)
        }
        savedStateHandle?.getLiveData<Uri>(CAMERA_URI)?.observe(currentBackStackEntry) {
            manageUri(it, true)
        }
    }

    private fun manageUri(uri: Uri?, isFromCamera: Boolean) {
        uri?.let {

            if (isFromCamera) {
                myProfileViewModel.path.value = it.path
                myProfileViewModel.profileImageUri.value = it
            } else {
                fileCopy(it)
            }
        }
    }

    private fun fileCopy(data: Uri) {
        // loadingPopup?.show()
        lifecycleScope.launch {
            loadingPopup?.show()

            val mPath = FileUtils.createCopyAndReturnRealPath(
                binding.root.context,
                data,
                true
            )
            if (!mPath.isNullOrEmpty()) {
                // filename = path!!.substring(path!!.lastIndexOf("/") + 1)
                // FileUtils.deleteFileFrom(myProfileViewModel.path.value ?: "")
                myProfileViewModel.path.value = mPath
                myProfileViewModel.profileImageUri.value = data
            } else {
                Toast.makeText(
                    binding.root.context,
                    binding.root.context.getText(R.string.please_choose_from_a_different_folder),
                    Toast.LENGTH_LONG
                ).show()
            }
            loadingPopup?.dismiss()
        }

    }

    private fun showSuccessfullyUpdatePopup() {
        context?.let {
            CustomPopup.showPopupMessageButtonClickDialog(it,
                null,
                it.getString(R.string.updated_successfully),
                it.getString(R.string.ok),
                false,
                object : DialogButtonClick {
                    override fun dialogButtonCallBack(dialog: Dialog) {
                        (activity as? DashboardActivity)?.loadData()
                        findNavController().popBackStack()
                    }

                }
            )
        }

    }

    private fun viewPhoto() {
        if(myProfileViewModel.profileImageUri.value==null) {
            val action = ServiceDetailsFragmentDirections.actionGlobalPhotoViewerFragment(
                images = arrayOf(myProfileViewModel.profileImageUrl.value ?: ""),
                position = 0
            )
            findNavController().navigate(action)
        }
    }


    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if (this::myProfileViewModel.isInitialized)
            loadProfileData()
    }


}
