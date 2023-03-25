package ie.healthylunch.app.ui


import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.parentDetailsModel.ParentDetailsResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.EditParentProfileRepository
import ie.healthylunch.app.data.viewModel.EditParentProfileViewModel
import ie.healthylunch.app.databinding.ActivityEditParentProfileBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.EMAIL_TAG
import ie.healthylunch.app.utils.Constants.Companion.FNAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.LNAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.PARENT_DETAILS_EDIT_PROFILE
import ie.healthylunch.app.utils.Constants.Companion.PHONE_TAG
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences

class EditParentProfileActivity :
    BaseActivity<EditParentProfileViewModel, EditParentProfileRepository>() {

    lateinit var editProfileBinding:ActivityEditParentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_parent_profile)
        setUpViewModel()
        window.statusBarColor = ContextCompat.getColor(this@EditParentProfileActivity, R.color.sky_bg_2)
        editProfileBinding=DataBindingUtil.setContentView(this, R.layout.activity_edit_parent_profile)

        editProfileBinding.activity = this
        editProfileBinding.viewModel = viewModel
        editProfileBinding.lifecycleOwner = this

//        viewModel.emailTv.value = intent.getStringExtra(EMAIL_TAG).toString()
//        viewModel.phoneTv.value = intent.getStringExtra(PHONE_TAG).toString()
//        viewModel.lastNameTv.value = intent.getStringExtra(LNAME_TAG).toString()
//        viewModel.firstNameTv.value = intent.getStringExtra(FNAME_TAG).toString()

//        val parentDetails: ParentDetailsResponse? =
//            UserPreferences.getAsObject<ParentDetailsResponse>(
//                this@EditParentProfileActivity,
//                PARENT_DETAILS_EDIT_PROFILE
//            )
        val parentDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(PARENT_DETAILS_EDIT_PROFILE, ParentDetailsResponse::class.java)
        } else {
            intent.getParcelableExtra<ParentDetailsResponse>(PARENT_DETAILS_EDIT_PROFILE)
        }

        parentDetails?.let { response ->
            viewModel.parentDetails = response
        }

        viewModel.parentDetails?.response?.raws?.data?.let { data ->
            viewModel.emailTv.value = data.email
            viewModel.phoneTv.value = data.phone
            viewModel.lastNameTv.value = data.lName
            viewModel.firstNameTv.value = data.fName
        }

        //getSaveProfileResponse
        viewModel.getSaveProfileResponse(this)
        viewClick()
    }

    private fun viewClick() {
        editProfileBinding.rlSave.setOnClickListener {
            save()
        }
    }

    fun save() {
        editProfileBinding.root.context?.let {ctx->
            if (validation()) {
                val loginResponse: LoginResponse? =
                    UserPreferences.getAsObject<LoginResponse>(
                        ctx,
                        Constants.USER_DETAILS
                    )

                loginResponse?.response?.raws?.data?.token?.let {token->
                    val jsonObject = MethodClass.getCommonJsonObject(ctx)
                    jsonObject.addProperty(PHONE_TAG, viewModel.phoneTv.value)
                    jsonObject.addProperty(LNAME_TAG, viewModel.lastNameTv.value)
                    jsonObject.addProperty(FNAME_TAG, viewModel.firstNameTv.value)
                    jsonObject.addProperty(EMAIL_TAG, viewModel.emailTv.value)

                    MethodClass.showProgressDialog(ctx as Activity)
                    viewModel.editProfile(jsonObject, token)
                }
            }
        }

    }
    override fun getViewModel() = EditParentProfileViewModel::class.java
    override fun getRepository() =
        EditParentProfileRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    fun validation(): Boolean {

        viewModel.invisibleErrorTexts()

        if (viewModel.firstNameTv.value.isNullOrBlank()) {
            setErrorText(viewModel.firstNameError, viewModel.firstNameErrorVisible, getString(R.string.please_enter_first_name))
            return false
        } else if (viewModel.lastNameTv.value.isNullOrBlank()) {
            setErrorText(viewModel.lastNameError, viewModel.lastNameErrorVisible, getString(R.string.please_enter_last_name))
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(viewModel.emailTv.value).matches()) {
            setErrorText(viewModel.emailError, viewModel.emailErrorVisible, getString(R.string.please_enter_valid_email_address))
            return false
        } else if (viewModel.phoneTv.value.isNullOrBlank()) {
            setErrorText(viewModel.phoneError, viewModel.phoneErrorVisible, getString(R.string.please_enter_phone_number))
            return false
        }

        return true
    }

    private fun setErrorText(liveDataText: MutableLiveData<String>,
                             liveDataVisibility: MutableLiveData<Boolean>,
                             text: String){
        liveDataText.value = text
        liveDataVisibility.value = true
    }

    }