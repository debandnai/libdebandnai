package ie.healthylunch.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.PrivacyPolicyViewRepository
import ie.healthylunch.app.data.viewModel.TermsOfUseViewModel
import ie.healthylunch.app.databinding.ActivityTermsOfUseBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences

class TermsOfUseActivity :
    BaseActivity<TermsOfUseViewModel, PrivacyPolicyViewRepository>() {
    lateinit var binding: ActivityTermsOfUseBinding
    private lateinit var loader: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_terms_of_use)
        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        init()
        onViewClick()
    }

    override fun getViewModel() = TermsOfUseViewModel::class.java
    override fun getRepository() =
        PrivacyPolicyViewRepository(remoteDataSource.buildApi(ApiInterface::class.java))

    private fun init(){
        loader = MethodClass.loaderDialog(binding.root.context)
    }

    private fun onViewClick(){
        binding.ivArrow.setOnClickListener {
            back()
        }
    }

    //PrivacyPolicyViewRepository
    @SuppressLint("SetJavaScriptEnabled")
    fun setTextView() {
        loader.show()
        val jsonObject = MethodClass.getCommonJsonObject(this@TermsOfUseActivity)
        jsonObject.addProperty("pagename", "term-condition")
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this@TermsOfUseActivity,
                Constants.USER_DETAILS
            )
        Constants.TOKEN = loginResponse?.response?.raws?.data?.token.toString()
        viewModel.privacyPolicy(jsonObject, Constants.TOKEN)
        viewModel.bool = true
        privacyPolicyResponse()
    }

    private fun privacyPolicyResponse(){
        with(this@TermsOfUseActivity){
            viewModel.privacyPolicyResponse.observe(this) {

                when (it) {
                    is Resource.Success -> {
                        binding.noteTermsOfUse.text = Html.fromHtml(
                            it.value.response.raws.data.footercontent.content,
                            Html.FROM_HTML_MODE_COMPACT
                        )

                        //MethodClass.hideProgressDialog(this)
                        //viewModel.privacyPolicyResponse.removeObservers(this)
                        loader.dismiss()
                    }
                    is Resource.Failure -> {
                        if (it.errorBody != null) {
                            MethodClass.hideProgressDialog(this)
                            it.errorString?.let { _ ->

                                it.errorString.let { it1 ->
                                    if (it.errorCode == 401)
                                        AppController.getInstance()
                                            .refreshTokenResponse(
                                                this,
                                                null,
                                                Constants.TERMS_OF_USE,
                                                Constants.REFRESH_TOKEN
                                            )
                                    else {
                                        if (viewModel.bool)
                                            MethodClass.showErrorDialog(
                                                this,
                                                it1,
                                                it.errorCode
                                            )
                                        viewModel.bool = false
                                    }
                                }


                            }


                        }
                        //viewModel.privacyPolicyResponse.removeObservers(this)
                        //MethodClass.hideProgressDialog(this)
                        loader.dismiss()
                    }


                }

            }
        }
    }

    fun back() {
        finish()
    }

}