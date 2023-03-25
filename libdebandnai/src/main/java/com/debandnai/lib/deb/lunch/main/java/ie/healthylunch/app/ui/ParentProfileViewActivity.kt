package ie.healthylunch.app.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.parentDetailsModel.ParentDetailsResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.DashBoardRepository
import ie.healthylunch.app.data.viewModel.ParentProfileViewViewModel
import ie.healthylunch.app.databinding.ActivityParentProfileViewBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.PARENT_DETAILS_EDIT_PROFILE
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences

class ParentProfileViewActivity : BaseActivity<ParentProfileViewViewModel, DashBoardRepository>() {
    private lateinit var binding: ActivityParentProfileViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        window.statusBarColor = ContextCompat.getColor(this@ParentProfileViewActivity, R.color.sky_bg_2)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_parent_profile_view)

        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        onViewClick()
    }

      
    override fun onResume() {
        super.onResume()
        setParentDetails()
    }


    override fun getViewModel() = ParentProfileViewViewModel::class.java
    override fun getRepository() =
        DashBoardRepository(remoteDataSource.buildApi(ApiInterface::class.java))

    private fun onViewClick(){
        binding.editProfile.setOnClickListener {
            editClick()
        }
    }

    fun editClick() {
        startActivity(
            Intent(this@ParentProfileViewActivity, EditParentProfileActivity::class.java)
                .putExtra(PARENT_DETAILS_EDIT_PROFILE, viewModel.parentDetails)
        )
    }

    fun setParentDetails() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this@ParentProfileViewActivity,
                Constants.USER_DETAILS
            )
        Constants.TOKEN = loginResponse?.response?.raws?.data?.token.toString()
        val parentId = loginResponse?.response?.raws?.data?.id
        val parentDetailsJsonObject = MethodClass.getCommonJsonObject(this@ParentProfileViewActivity)
        parentDetailsJsonObject.addProperty("parent_id", parentId)

        MethodClass.showProgressDialog(this@ParentProfileViewActivity)
        viewModel.parentDetails(parentDetailsJsonObject, TOKEN)

        parentDetailsResponse()
    }

    private fun parentDetailsResponse(){
        with(this@ParentProfileViewActivity){
            viewModel.parentDetailsResponse?.observe(this) {

                when (it) {
                    is Resource.Success -> {
                        viewModel.email.value = it.value.response.raws.data.email
                        viewModel.phone.value = it.value.response.raws.data.phone
                        viewModel.name.value = it.value.response.raws.data.displayName
                        viewModel.firstName.value = it.value.response.raws.data.fName
                        viewModel.lastName.value = it.value.response.raws.data.lName
                        viewModel.kitchenName.value = it.value.response.raws.data.kitchenName
//                        UserPreferences.saveAsObject(this, it.value,
//                            Constants.PARENT_DETAILS_EDIT_PROFILE
//                        )
                        viewModel.parentDetails = it.value
                        MethodClass.hideProgressDialog(this)

                    }
                    is Resource.Failure -> {
                        if (it.errorBody != null) {
                            MethodClass.hideProgressDialog(this)
                            it.errorString?.let { it1 ->
                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            this,
                                            null,
                                            Constants.PARENT_DETAILS,
                                            Constants.REFRESH_TOKEN
                                        )
                                else if (it.errorCode == 307 || it.errorCode == 426)
                                    MethodClass.showErrorDialog(this, it1, it.errorCode)
                                else {
                                    MethodClass.showErrorDialog(
                                        this,
                                        it1,
                                        it.errorCode
                                    )

                                }


                            }

                            viewModel._parentDetailsResponse.value = null
                            viewModel.parentDetailsResponse = viewModel._parentDetailsResponse

                        }
                        MethodClass.hideProgressDialog(this)
                    }


                    else -> {}
                }

            }
        }
    }
}