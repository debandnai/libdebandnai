package ie.healthylunch.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.PrivacyPolicyViewRepository
import ie.healthylunch.app.data.viewModel.PrivacyPolicyViewModel
import ie.healthylunch.app.databinding.ActivityPrivacyPolicyBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences

class PrivacyPolicyActivity :
    BaseActivity<PrivacyPolicyViewModel, PrivacyPolicyViewRepository>() {
    lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy)
        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.activity = this

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var privacyActivity: Activity
    }

    override fun getViewModel() = PrivacyPolicyViewModel::class.java
    override fun getRepository() =
        PrivacyPolicyViewRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    //PrivacyPolicyViewRepository
    fun setTextView() {
        val jsonObject = MethodClass.getCommonJsonObject(this@PrivacyPolicyActivity)
        jsonObject.addProperty("pagename", "privacy-policy")
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this@PrivacyPolicyActivity,
                Constants.USER_DETAILS
            )
        Constants.TOKEN = loginResponse?.response?.raws?.data?.token.toString()
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        viewModel.privacyPolicy(jsonObject, Constants.TOKEN)

        privacyPolicyResponse()
    }

    private fun privacyPolicyResponse(){
        with(this@PrivacyPolicyActivity){
            viewModel.privacyPolicyResponse?.observe(this) {

                when (it) {
                    is Resource.Success -> {
                        binding.notePrivacy.text = Html.fromHtml(
                            it.value.response.raws.data.footercontent.content,
                            Html.FROM_HTML_MODE_COMPACT
                        )


                        MethodClass.hideProgressDialog(this)

                        viewModel._privacyPolicyResponse.value = null
                        viewModel.privacyPolicyResponse = viewModel._privacyPolicyResponse
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
                                            Constants.PRIVACY_POLICY,
                                            Constants.REFRESH_TOKEN
                                        )
                                else
                                    MethodClass.showErrorDialog(
                                        this,
                                        it1,
                                        it.errorCode
                                    )


                            }


                        }
                        MethodClass.hideProgressDialog(this)
                        viewModel._privacyPolicyResponse.value = null
                        viewModel.privacyPolicyResponse = viewModel._privacyPolicyResponse
                    }


                    else -> {}
                }

            }
        }
    }
}