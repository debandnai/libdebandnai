package com.merkaaz.app.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import com.merkaaz.app.R
import com.merkaaz.app.adapter.PagingFooterAdapter
import com.merkaaz.app.adapter.ProfitLossAdapter
import com.merkaaz.app.data.model.ProfitLossList
import com.merkaaz.app.data.viewModel.ManageProfitLossViewModel
import com.merkaaz.app.databinding.ActivityManageProfitLossBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.GENERATED_ORDER_ID
import com.merkaaz.app.utils.Constants.ORDER_ID
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ManageProfitLossActivity : BaseActivity(), AdapterItemClickListener {
    private lateinit var binding: ActivityManageProfitLossBinding
    private lateinit var manageProfitLossViewModel: ManageProfitLossViewModel
    private var loader: Dialog? = null
    private var profitLossAdapter: ProfitLossAdapter? = null
    private var isSwipeRefreshLoader: Boolean = false
    var resultLauncher: ActivityResultLauncher<Intent>? = null

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_profit_loss)
        manageProfitLossViewModel = ViewModelProvider(this)[ManageProfitLossViewModel::class.java]
        binding.viewModel = manageProfitLossViewModel
        binding.activity = this
        binding.lifecycleOwner = this
        initialise()
        onViewClick()
        observeData()
        getIntentData()
    }

    private fun getIntentData() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    profitLossAdapter.let { profitLossAdapter?.refresh() }
                }
            }
    }

    private fun initialise() {
        val c2 = ContextCompat.getColor(this, R.color.teal_700)
        binding.swipeRefreshLayout.setColorSchemeColors(c2)

        loader = MethodClass.custom_loader(this, resources.getString(R.string.loading_msg))

        val json = commonFunctions.commonJsonData()
        json.addProperty("rows", Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        manageProfitLossViewModel.getData(json)
        binding.swipeRefreshLayout.setOnRefreshListener {
            isSwipeRefreshLoader = true
            profitLossAdapter.let { profitLossAdapter?.refresh() }

        }
    }

    private fun onViewClick() {
        binding.imgHelp.setOnClickListener {
            startActivity(
                Intent(
                    this@ManageProfitLossActivity,
                    CustomerServiceActivity::class.java
                )
            )
        }
    }


    private fun observeData() {
        profitLossAdapter = ProfitLossAdapter(this)
        binding.rvProfitLoss.adapter = profitLossAdapter?.withLoadStateFooter(
            footer = PagingFooterAdapter { profitLossAdapter?.retry() }
        )
        manageProfitLossViewModel.data_list.observe(this) {
            try {
                profitLossAdapter?.submitData(lifecycle, it)
            } catch (_: Exception) {
            }
        }

        profitLossAdapter?.addLoadStateListener { loadState ->
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
                (loadState.append.endOfPaginationReached && profitLossAdapter?.itemCount == 0)
            if (displayEmptyMessage) {
                MethodClass.custom_msg_dialog(this, getString(R.string.no_data_found))?.show()
                MethodClass.custom_msg_dialog_callback(this,
                    resources.getString(R.string.no_data_found),
                    object : DialogCallback {
                        override fun dialog_clickstate() {
                            startActivity(
                                Intent(
                                    this@ManageProfitLossActivity,
                                    DashBoardActivity::class.java
                                )
                            )
                            finish()
                        }
                    })
            }


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

    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {
        isSwipeRefreshLoader = false
        if (Objects.equals(tag, Constants.PROFIT_LOSS_DETAILS)) {
            val profitLossList: List<ProfitLossList> = arrayList as List<ProfitLossList>
            resultLauncher?.launch(
                Intent(this@ManageProfitLossActivity, ManageProfitLossDetailsActivity::class.java)
                    .putExtra(ORDER_ID, profitLossList[position].orderId.toString())
                    .putExtra(
                        GENERATED_ORDER_ID,
                        profitLossList[position].generatedOrderId.toString()
                    )
            )

        }
    }
}