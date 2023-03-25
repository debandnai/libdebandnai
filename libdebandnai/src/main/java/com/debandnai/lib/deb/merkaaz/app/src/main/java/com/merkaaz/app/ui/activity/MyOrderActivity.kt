package com.merkaaz.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import com.merkaaz.app.R
import com.merkaaz.app.adapter.OrderAdapter
import com.merkaaz.app.adapter.PagingFooterAdapter
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.model.OrderList
import com.merkaaz.app.data.viewModel.MyOrderViewModel
import com.merkaaz.app.databinding.ActivityMyOrderBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.MANAGE_P_L
import com.merkaaz.app.utils.Constants.ORDER_ID
import com.merkaaz.app.utils.Constants.VIEW_DETAILS
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyOrderActivity : BaseActivity(), AdapterItemClickListener, DialogCallback {
    private lateinit var binding: ActivityMyOrderBinding
    private lateinit var myOrderViewModel: MyOrderViewModel
    private var loader: Dialog? = null
    private var orderAdapter: OrderAdapter? = null
    private var isSwipeRefreshLoader: Boolean = false

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order)

        myOrderViewModel = ViewModelProvider(this)[MyOrderViewModel::class.java]
        binding.viewModel = myOrderViewModel
        binding.activity = this
        binding.lifecycleOwner = this
        initialise()
        onViewClick()
        observeData()

    }


    private fun initialise() {

        val c2 = ContextCompat.getColor(this, R.color.teal_700)
        binding.swipeRefreshLayout.setColorSchemeColors(c2)

        loader = MethodClass.custom_loader(this, resources.getString(R.string.loading_msg))
        val json = commonFunctions.commonJsonData()
        json.addProperty("rows", Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        myOrderViewModel.getData(json)


        binding.swipeRefreshLayout.setOnRefreshListener {
            isSwipeRefreshLoader = true
            orderAdapter.let { orderAdapter?.refresh() }

        }
    }

    private fun onViewClick() {
        binding.imgHelp.setOnClickListener {
            startActivity(Intent(this@MyOrderActivity, CustomerServiceActivity::class.java))
        }
    }

    private fun observeData() {
        orderAdapter = OrderAdapter(this)
        binding.rvOrder.adapter = orderAdapter?.withLoadStateFooter(
            footer = PagingFooterAdapter { orderAdapter?.retry() }
        )
        myOrderViewModel.data_list.observe(this) {
            try {
                orderAdapter?.submitData(lifecycle, it)
            } catch (_: Exception) {
            }
        }

        orderAdapter?.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    showLoader()
                }
                is LoadState.NotLoading -> {
                    hideLoader()
                }
                is LoadState.Error -> {
                    hideLoader()
                    val loadStateError = loadState.refresh as LoadState.Error
                    if (loadStateError.error is HttpException) {
                        MethodClass.custom_msg_dialog(
                            binding.root.context,
                            resources.getString(R.string.network_error_msg)
                        )
                    } else {
                        MethodClass.custom_msg_dialog(
                            binding.root.context,
                            resources.getString(R.string.server_error_msg)
                        )
                    }
                }
                else -> {
                    hideLoader()
                }
            }

            val displayEmptyMessage =
                (loadState.append.endOfPaginationReached && orderAdapter?.itemCount == 0)
            if (displayEmptyMessage) {
                MethodClass.custom_msg_no_order_dialog_callback(
                    this,
                    getString(R.string.no_orders_yet),
                    getString(R.string.add_something_from_the_menu),
                    getString(R.string.start_shopping),
                    this@MyOrderActivity
                )?.show()
            }


        }

    }

    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {

        isSwipeRefreshLoader = false
        val orderList: List<OrderList> = arrayList as List<OrderList>
        if (Objects.equals(tag, VIEW_DETAILS)) {
            Log.d(TAG, "onAdapterItemClick:order id  " + orderList[position].orderId)
            startActivity(
                Intent(this@MyOrderActivity, OrderDetailsActivity::class.java)
                    .putExtra(ORDER_ID, orderList[position])
            )
        } else if (Objects.equals(tag, MANAGE_P_L)) {
            //For Manage Profit Loss
            startActivity(
                Intent(this@MyOrderActivity, ManageProfitLossActivity::class.java)
                    .putExtra(ORDER_ID, orderList[position])

            )
        }
    }

    private fun showLoader() {

        if (isSwipeRefreshLoader) {
            loader?.dismiss()
        } else {
            loader?.show()
        }

    }

    private fun hideLoader() {
        with(binding) {
            loader?.dismiss()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    override fun onBackPressed() {

        //isTaskRoot which return true if current activity is only activity in your stack
        if (isTaskRoot) {
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        } else
            super.onBackPressed()
    }

    override fun dialog_clickstate() {
        startActivity(
            Intent(this, DashBoardActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        )
    }
}