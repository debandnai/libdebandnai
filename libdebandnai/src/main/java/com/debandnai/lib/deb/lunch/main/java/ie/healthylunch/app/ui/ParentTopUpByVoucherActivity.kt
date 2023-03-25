package ie.healthylunch.app.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.AddVoucherCodeRepository
import ie.healthylunch.app.data.viewModel.AddVoucherCodeViewModel
import ie.healthylunch.app.databinding.ActivityParentTopUpByVoucherBinding
import ie.healthylunch.app.ui.base.BaseActivity

class ParentTopUpByVoucherActivity :
    BaseActivity<AddVoucherCodeViewModel, AddVoucherCodeRepository>() {
      
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        val binding: ActivityParentTopUpByVoucherBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_parent_top_up_by_voucher)
        binding.viewModel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
        viewModel.activity = this

        viewModel.cardListApiCall()


    }

    override fun getViewModel() = AddVoucherCodeViewModel::class.java

    override fun getRepository() =
        AddVoucherCodeRepository(remoteDataSource.buildApi(ApiInterface::class.java))

}