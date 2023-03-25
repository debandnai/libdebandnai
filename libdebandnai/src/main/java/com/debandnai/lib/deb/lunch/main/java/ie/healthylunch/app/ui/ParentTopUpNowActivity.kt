package ie.healthylunch.app.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.ParentTopUpRepository
import ie.healthylunch.app.data.viewModel.ParentTopUpNowViewModel
import ie.healthylunch.app.databinding.ActivityParentTopUpNowBinding
import ie.healthylunch.app.ui.base.BaseActivity

class ParentTopUpNowActivity : BaseActivity<ParentTopUpNowViewModel, ParentTopUpRepository>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        val binding: ActivityParentTopUpNowBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_parent_top_up_now)
        binding.viewModel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
        viewModel.activity = this


        //get data from intent
        if (intent.extras != null) {
            viewModel.cardNumber.value = intent.getStringExtra("card_number").toString()
            viewModel.brandName.value = intent.getStringExtra("brand").toString()
        }
    }

    override fun getViewModel() = ParentTopUpNowViewModel::class.java

    override fun getRepository() =
        ParentTopUpRepository(remoteDataSource.buildApi(ApiInterface::class.java))
}