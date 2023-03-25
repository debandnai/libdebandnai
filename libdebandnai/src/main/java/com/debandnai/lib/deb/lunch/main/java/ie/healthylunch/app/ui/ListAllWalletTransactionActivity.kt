package ie.healthylunch.app.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.PagingFooterAdapter
import ie.healthylunch.app.adapter.TransactionPagingAdapter
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionList
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.TransactionListRepository
import ie.healthylunch.app.data.viewModel.TransactionListViewModel
import ie.healthylunch.app.databinding.ActivityListAllWalletTransactionBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.ROWS_TAG
import ie.healthylunch.app.utils.Constants.Companion.TOTAL_NO_OF_ITEMS_PER_PAGE
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences
import ie.healthylunch.app.utils.quickActionPopup.ActionItem
import ie.healthylunch.app.utils.quickActionPopup.QuickAction
import ie.healthylunch.app.utils.quickActionPopup.QuickActionMenu
import java.util.*

class ListAllWalletTransactionActivity :
    BaseActivity<TransactionListViewModel, TransactionListRepository>(), AdapterItemOnclickListener {
    lateinit var activityListAllWalletTransactionBinding:ActivityListAllWalletTransactionBinding
    lateinit var transaactionPagingAdapter: TransactionPagingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()
        activityListAllWalletTransactionBinding=
            DataBindingUtil.setContentView(this, R.layout.activity_list_all_wallet_transaction)
        activityListAllWalletTransactionBinding.viewModel = viewModel
        activityListAllWalletTransactionBinding.activity = this
        activityListAllWalletTransactionBinding.lifecycleOwner = this


        init()
        observeData()
    }
    fun init(){
        transaactionPagingAdapter = TransactionPagingAdapter(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )
        val jsonObject1 = MethodClass.getCommonJsonObject(this)
        jsonObject1.addProperty(ROWS_TAG, TOTAL_NO_OF_ITEMS_PER_PAGE)
        loginResponse?.response?.raws?.data?.token?.let { token->
            viewModel.getData(jsonObject1,token)
        }

    }
    override fun getViewModel() = TransactionListViewModel::class.java

    override fun getRepository() =
        TransactionListRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    private fun observeData() {

        activityListAllWalletTransactionBinding.transactionListRv.adapter =
            transaactionPagingAdapter.withLoadStateFooter(
                footer = PagingFooterAdapter { transaactionPagingAdapter.retry() }
            )
        viewModel.data_list.observe(this) {
            try {
                transaactionPagingAdapter.submitData(lifecycle, it)
            } catch (_: Exception) {
            }
        }

        transaactionPagingAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    MethodClass.showProgressDialog(this)
                }
                is LoadState.NotLoading -> {
                    MethodClass.hideProgressDialog(this)
                }
                is LoadState.Error -> {
                    MethodClass.hideProgressDialog(this)

                }
                else -> {
                    MethodClass.hideProgressDialog(this)
                }
            }
            with((loadState.append.endOfPaginationReached && transaactionPagingAdapter.itemCount == 0)){
                viewModel.recyclerViewVisible.value =!this
                viewModel.noDataTextVisible.value =this
            }

        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        if (Objects.equals(tag, "transactionInfo")) {
            val transactionList: List<TransactionList> =
                arrayList as List<TransactionList>

            val quickAction = QuickAction(this, QuickActionMenu.VERTICAL)
            transaactionPagingAdapter?.ivInfo?.let {transaactionPagingAdapter-> quickAction.show(transaactionPagingAdapter) }
            quickAction.setAnimStyle(QuickActionMenu.ANIM_REFLECT)

            if (transactionList.isNotEmpty()
            ) {
                val templateName: String? =
                    transactionList[position].templateName
                var productsName: String? = null
                if (transactionList[position].products != null &&
                    transactionList[position].products is String
                ) {
                    if (transactionList[position].products.toString().isNotEmpty()) {
                        productsName =
                            transactionList[position].products.toString()
                    }
                } else {
                    if (transactionList[position].products is List<*>) {
                        val productList = transactionList[position].products as List<String>
                        if (productList.isNotEmpty())
                            productsName = TextUtils.join(", ", productList)

                    }

                }

                val noProduct = getString(R.string.no_product_found)

                val accAction = ActionItem()
                accAction.setTitle(templateName)
                accAction.setProduct(productsName)

                if (templateName.isNullOrEmpty() || productsName.isNullOrEmpty()) {
                    accAction.setTitle(noProduct)
                    quickAction.addActionItem(accAction)
                } else
                    quickAction.addActionItem(accAction)

            }

        }

    }


}