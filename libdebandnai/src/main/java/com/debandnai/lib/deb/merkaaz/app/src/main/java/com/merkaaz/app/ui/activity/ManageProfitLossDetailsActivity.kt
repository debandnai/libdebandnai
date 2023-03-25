package com.merkaaz.app.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.ProfitLossDetailsProductAdapter
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.model.ProfitLossDetailsListModel
import com.merkaaz.app.data.model.ProfitlossProduct
import com.merkaaz.app.data.viewModel.ManageProfitLossDetailsViewModel
import com.merkaaz.app.databinding.ActivityManageProfitLossdetailsBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.CalculateClickListener
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.ui.dialogs.ManageProfitLossBottomSheet
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants.CALCULATE
import com.merkaaz.app.utils.Constants.GENERATED_ORDER_ID
import com.merkaaz.app.utils.Constants.ORDER_ID
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ManageProfitLossDetailsActivity : BaseActivity(), AdapterItemClickListener,
    CalculateClickListener {
    private lateinit var binding: ActivityManageProfitLossdetailsBinding
    private var profitLossDetailsProductAdapter: ProfitLossDetailsProductAdapter? = null
    private lateinit var manageProfitLossDetailsViewModel: ManageProfitLossDetailsViewModel
    private var loader: Dialog? = null
    private var isSwipeRefreshLoader: Boolean = false
    var orderId = ""
    var generatedOrderId = ""

    // private var calculateBottomSheet: CalculateBottomSheet? = null
    private var manageProfitLossBottomSheet: ManageProfitLossBottomSheet? = null
    private var resolutionForResult: ActivityResultLauncher<IntentSenderRequest>? = null

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_profit_lossdetails)
        manageProfitLossDetailsViewModel =
            ViewModelProvider(this)[ManageProfitLossDetailsViewModel::class.java]
        binding.viewModel = manageProfitLossDetailsViewModel
        binding.activity = this
        binding.lifecycleOwner = this

        initialise()
        onViewClick()
        observeData()

    }


    private fun initialise() {
        val c2 = ContextCompat.getColor(this, R.color.teal_700)
        binding.swipeRefreshLayout.setColorSchemeColors(c2)

        profitLossDetailsProductAdapter = ProfitLossDetailsProductAdapter(this, ArrayList())
        binding.rvProfitLossProduct.adapter = profitLossDetailsProductAdapter
        binding.rvProfitLossProduct.isFocusable = false

        loader = MethodClass.custom_loader(this, resources.getString(R.string.loading_msg))
        if (intent.extras != null && intent.hasExtra(ORDER_ID)) {
            orderId = intent.getStringExtra(ORDER_ID).toString()
            generatedOrderId = intent.getStringExtra(GENERATED_ORDER_ID).toString()
            orderId.let { manageProfitLossDetailsViewModel.getProfitLossDetails(it) }
            manageProfitLossDetailsViewModel.orderId.value =
                getString(R.string.order_id) + " # " + generatedOrderId
            binding.swipeRefreshLayout.setOnRefreshListener {
                isSwipeRefreshLoader = true
                orderId.let { manageProfitLossDetailsViewModel.getProfitLossDetails(it) }
            }
        }

    }

    private fun onViewClick() {
        binding.imgHelp.setOnClickListener {
            startActivity(
                Intent(
                    this@ManageProfitLossDetailsActivity,
                    CustomerServiceActivity::class.java
                )
            )
        }
    }

    private fun observeData() {
        //For Manage Profit Loss Details
        manageProfitLossDetailsViewModel.profitLossDetailsListLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    // loader?.show()
                    showLoader()
                }
                is Response.Success -> {
                    it.data?.let { data ->
                        hideLoader()
                        // loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val profitLossListDetailsList = data.response?.data
                            setResponseData(profitLossListDetailsList)
                        }

                    }
                }
                is Response.Error -> {
                    hideLoader()
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(
                        binding.root.context,
                        it.errorCode,
                        it.errorMessage
                    )
                }
            }
        }


        //For Manage Profit Loss Save Data
        manageProfitLossDetailsViewModel.profitLossSaveLiveData.observe(this) { jsonResponse ->
            when (jsonResponse) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    jsonResponse.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            orderId.let { manageProfitLossDetailsViewModel.getProfitLossDetails(it) }
                        }

                    }
                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(
                        binding.root.context,
                        jsonResponse.errorCode,
                        jsonResponse.errorMessage
                    )
                }
            }
        }
    }

    private fun setResponseData(profitLossListDetailsList: JsonElement?) {
        val gson = Gson()
        val profitLossDetailsType =
            object : TypeToken<ProfitLossDetailsListModel>() {}.type
        val profitLossDetailsListModel: ProfitLossDetailsListModel =
            gson.fromJson(profitLossListDetailsList, profitLossDetailsType)
        manageProfitLossDetailsViewModel.orderDate.value =
            profitLossDetailsListModel.orderDate
        manageProfitLossDetailsViewModel.items.value =
            profitLossDetailsListModel.numOfItems.toString()
        manageProfitLossDetailsViewModel.orderCostPrice.value =
            profitLossDetailsListModel.orderCostPrice.toString()
        manageProfitLossDetailsViewModel.profit.value =
            profitLossDetailsListModel.orderProfit.toString()
        manageProfitLossDetailsViewModel.grossProfit.value =
            profitLossDetailsListModel.orderGrossPrice.toString()
        profitLossDetailsProductAdapter?.profitLossProduct =
            profitLossDetailsListModel.profitLossProductList
        profitLossDetailsProductAdapter?.notifyDataSetChanged()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {
        if (Objects.equals(tag, CALCULATE)) {
            val profitLossProduct: List<ProfitlossProduct> = arrayList as List<ProfitlossProduct>
            Log.d(TAG, "onAdapterItemClick: sellPrice " + profitLossProduct[position].sellPrice)
            /*calculateBottomSheet = CalculateBottomSheet(profitLossProduct[position], this)
            calculateBottomSheet?.show(supportFragmentManager, "")*/


            manageProfitLossBottomSheet =
                ManageProfitLossBottomSheet(profitLossProduct[position], this)
            manageProfitLossBottomSheet?.show(supportFragmentManager, "")


        }
    }

    override fun calculateAndUpdate(
        id: String?,
        sellPrice: String?,
        profitPercentage: String?,
        tag: String
    ) {
        manageProfitLossBottomSheet?.dismiss()
        hideKeyBoard()
        if (Objects.equals(tag, CALCULATE)) {
            val json = commonFunctions.commonJsonData()
            json.addProperty("id", id)
            json.addProperty("sell_price", sellPrice)
            json.addProperty("profit_percentage", profitPercentage)
            manageProfitLossDetailsViewModel.getProfitLossSaveDetails(json)
            Log.d(TAG, "calculateAndUpdate: $json")
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

    private fun hideKeyBoard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.swipeRefreshLayout.windowToken, 0)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}




