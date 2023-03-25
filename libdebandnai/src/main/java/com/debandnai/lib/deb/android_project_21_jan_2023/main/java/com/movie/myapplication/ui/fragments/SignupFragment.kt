package com.movie.myapplication.ui.fragments



import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.movie.myapplication.R
import com.movie.myapplication.data.viewModel.AuthViewModel
import com.movie.myapplication.databinding.FragmentSignupBinding
import com.movie.myapplication.db.entity.Auth
import com.movie.myapplication.ui.base.BaseFragment


class SignupFragment : BaseFragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var binding: FragmentSignupBinding
  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
      binding.viewModel=authViewModel
      binding.lifecycleOwner =this
      init()
      viewOnClick()
      return binding.root

    }

    private fun init() {
        context?.let {
            binding.composeView.apply {
                //setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setContent {
                    setViewTesting()
                }
               }
            authViewModel.titleTag.value=getString(R.string.create_account)
            val genderArray = resources.getStringArray(R.array.gender)
            val langAdapter = ArrayAdapter<CharSequence>(it, R.layout.spinner_text, genderArray)
            langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
            binding.atGender.adapter = langAdapter

            binding.atGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    authViewModel.gender.value=binding.atGender.selectedItem.toString()
                }

            }

        }

    }




    private fun viewOnClick() {
        binding.tvLogin.setOnClickListener {
            goToLoginPage()
        }
        binding.signUpBtn.setOnClickListener {
            if(validate()) {
                var auth = Auth(
                    null,
                    authViewModel.first_name.value,
                    authViewModel.last_name.value,
                    authViewModel.age.value,
                    authViewModel.gender.value,
                    authViewModel.email.value,
                    authViewModel.phone_number.value,
                    authViewModel.password_sign_up.value
                )
                authViewModel.insertAuthDetails(auth)
                goToLoginPage()
            }
        }


        binding.tvShow.setOnClickListener {
            if (binding.etPassword.inputType==1){
                binding.etPassword.inputType =225
                authViewModel.showHideText.value=getString(R.string.show)
            }
            else{
                binding.etPassword.inputType =1
                authViewModel.showHideText.value=getString(R.string.hide)
            }
        }

    }
    private fun validate():Boolean{
        activity?.let { act->
            if (authViewModel.first_name.value?.isBlank() == true) {
                Toast.makeText(act, getString(R.string.your_first_name), Toast.LENGTH_SHORT).show()
                return false
            }
            else  if (authViewModel.last_name.value?.isBlank() == true) {
                Toast.makeText(act, getString(R.string.your_last_name), Toast.LENGTH_SHORT).show()
                return false
            }
            if (authViewModel.age.value?.isBlank() == true) {
                Toast.makeText(act, getString(R.string.your_age), Toast.LENGTH_SHORT).show()
                return false
            }
            if (authViewModel.gender.value?.isBlank() == true) {
                Toast.makeText(act, getString(R.string.your_gender), Toast.LENGTH_SHORT).show()
                return false
            }
            if (!((authViewModel.gender.value?.equals("Male",false)==true) ||  (authViewModel.gender.value?.equals("Female",false)==true))){
                Toast.makeText(act, getString(R.string.valid_gender_type), Toast.LENGTH_SHORT).show()
                return false
            }

            if ((authViewModel.email.value?.isBlank() == true) || !Patterns.EMAIL_ADDRESS.matcher(authViewModel?.email?.value).matches()) {
                Toast.makeText(act, getString(R.string.your_email), Toast.LENGTH_SHORT).show()
                return false
            }
            if (authViewModel.phone_number.value?.isBlank() == true || !Patterns.PHONE.matcher(authViewModel.phone_number.value).matches()) {
                Toast.makeText(act, getString(R.string.phone_number), Toast.LENGTH_SHORT).show()
                return false
            }
            if (authViewModel.password_sign_up.value?.isBlank() == true) {
                Toast.makeText(act, getString(R.string.password), Toast.LENGTH_SHORT).show()
                return false
            }

        }
        return true
    }
    private fun goToLoginPage(){
        Navigation.findNavController(binding.tvLogin)
            .navigate(
                R.id.action_signupFragment_to_loginFragment,
                null
            )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun setViewTesting() {
    Text(text = stringResource(id = R.string.app_name),color= colorResource(id = R.color.teal_200), fontSize = 18.sp)
}


