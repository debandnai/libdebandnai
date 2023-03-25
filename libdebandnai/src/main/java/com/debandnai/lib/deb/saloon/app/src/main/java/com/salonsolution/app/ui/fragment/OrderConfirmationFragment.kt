package com.salonsolution.app.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.salonsolution.app.R
import com.salonsolution.app.databinding.FragmentOrderConfirmationBinding
import com.salonsolution.app.ui.activity.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConfirmationFragment : Fragment() {
    private lateinit var binding: FragmentOrderConfirmationBinding
    private val navArgs: OrderConfirmationFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_confirmation, container, false)
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = false,
            isShowBottomNav = false,
            isShowToolbar = false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding){
            tvOrderId.text = navArgs.orderId
            tvTotalAmount.text = navArgs.totalPrice
            btGoToDashboard.setOnClickListener {
                findNavController().navigate(R.id.homeFragment)
            }

            btViewDetails.setOnClickListener {
                val action = OrderConfirmationFragmentDirections.actionOrderConfirmationFragmentToOrderDetailsFragment(navArgs.id)
                findNavController().navigate(action)
            }
        }
    }

}