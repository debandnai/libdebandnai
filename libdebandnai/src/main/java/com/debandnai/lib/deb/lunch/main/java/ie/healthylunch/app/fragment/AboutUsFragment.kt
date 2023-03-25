package ie.healthylunch.app.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.PrivacyPolicyViewRepository
import ie.healthylunch.app.data.viewModel.AboutViewModel
import ie.healthylunch.app.databinding.FragmentAboutUsBinding
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.PrivacyPolicyActivity
import ie.healthylunch.app.ui.TermsOfUseActivity
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.ABOUT_US
import ie.healthylunch.app.utils.Constants.Companion.ABOUT_US_TAG
import ie.healthylunch.app.utils.Constants.Companion.PAGE_NAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.USER_DETAILS
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences


class AboutUsFragment : BaseFragment<AboutViewModel, PrivacyPolicyViewRepository>() {
    lateinit var fragmentAboutUsBinding: FragmentAboutUsBinding

      
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        setUpViewModel()
        fragmentAboutUsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_about_us,
            container,
            false
        )
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.sky_bg_2)

        fragmentAboutUsBinding.viewModel = viewModel
        viewModel.aboutUsFragment=this
        fragmentAboutUsBinding.lifecycleOwner = this
        onViewClick()
        getPrivacyPolicy()
        getPrivacyPolicyResponse()

        return fragmentAboutUsBinding.root
    }

    private fun onViewClick() {
        fragmentAboutUsBinding.rlTopNew.setOnClickListener { view ->
            Navigation.findNavController(view).popBackStack()
        }
        activity?.let { act ->
            fragmentAboutUsBinding.tvTermsOfUse.setOnClickListener {
                    startActivity(Intent(act, TermsOfUseActivity::class.java))
            }
            fragmentAboutUsBinding.tvPrivacyPolicy.setOnClickListener {
                startActivity(Intent(act, PrivacyPolicyActivity::class.java))
            }

        }
    }

    override fun onResume() {
        super.onResume()
        DashBoardActivity.dashBoardToolbar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dashboard_toolbar_color2
            )
        )

        DashBoardActivity.dashBoardNotificationLayout.visibility = View.GONE
        DashBoardActivity.dashBoardHelpLayout.visibility = View.GONE
        DashBoardActivity.dashBoardWalletLayout.visibility = View.GONE
        DashBoardActivity.dashBoardToolbar.visibility = View.VISIBLE

    }

    override fun getViewModel() = AboutViewModel::class.java
    override fun getRepository() =
        PrivacyPolicyViewRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    //PrivacyPolicyViewRepository
    @SuppressLint("SetJavaScriptEnabled")
    fun getPrivacyPolicy() {
        fragmentAboutUsBinding.root.context?.let { ctx->
            val jsonObject = MethodClass.getCommonJsonObject(ctx)
            jsonObject.addProperty(PAGE_NAME_TAG, ABOUT_US_TAG)
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    ctx,
                    USER_DETAILS
                )
            loginResponse?.response?.raws?.data?.token?.let {token->
                viewModel.privacyPolicy(jsonObject, token)
            }

        }




    }
      
    private fun getPrivacyPolicyResponse(){
        activity?.let { act ->
            viewModel.privacyPolicyResponse?.observe(viewLifecycleOwner) { response->

                when (response) {
                    is Resource.Success -> {
                        response.value.response.raws.data.footercontent.content?.let { content->
                            fragmentAboutUsBinding.notePrivacy.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                Html.fromHtml(content,Html.FROM_HTML_MODE_COMPACT)
                            else
                                Html.fromHtml(content)
                        }


                        if (!viewModel.isFirstTime) {
                            viewModel.isFirstTime = true
                        }

                        MethodClass.hideProgressDialog(act)
                    }
                    is Resource.Failure -> {
                        if (response.errorBody != null) {
                            MethodClass.hideProgressDialog(act)
                            response.errorString?.let { error ->
                                if (response.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            act,
                                            this,
                                            ABOUT_US,
                                            REFRESH_TOKEN
                                        )
                                else
                                    MethodClass.showErrorDialog(
                                        act,
                                        error,
                                        response.errorCode
                                    )


                            }


                        }
                        MethodClass.hideProgressDialog(act)

                        viewModel._privacyPolicyResponse.value = null
                        viewModel.privacyPolicyResponse = viewModel._privacyPolicyResponse
                    }


                    else -> {}
                }

            }
        }
    }
}