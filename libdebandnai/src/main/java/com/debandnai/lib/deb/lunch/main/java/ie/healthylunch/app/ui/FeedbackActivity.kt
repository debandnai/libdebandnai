package ie.healthylunch.app.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.FeedBackRepository
import ie.healthylunch.app.data.viewModel.FeedBackViewModel
import ie.healthylunch.app.databinding.ActivityFeedbackBinding

import ie.healthylunch.app.ui.base.BaseActivity

class FeedbackActivity : BaseActivity<FeedBackViewModel, FeedBackRepository>() {
      
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        window.statusBarColor = ContextCompat.getColor(this@FeedbackActivity, R.color.sky_bg_1)

        val binding: ActivityFeedbackBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_feedback)
        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.recyclerView = binding.feedbackRv

        //get feedback response
        viewModel.getSubmitFeedbackResponse(this)

    }

    override fun getViewModel() = FeedBackViewModel::class.java

    override fun getRepository() =
        FeedBackRepository(remoteDataSource.buildApi(ApiInterface::class.java))
}