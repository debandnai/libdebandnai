package com.merkaaz.app.ui.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.merkaaz.app.R
import com.merkaaz.app.databinding.ActivityCustomerServiceBinding
import com.merkaaz.app.ui.base.BaseActivity

class CustomerServiceActivity : BaseActivity() {
    lateinit var binding: ActivityCustomerServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_service)

        binding.activity = this
        binding.lifecycleOwner = this

    }
}