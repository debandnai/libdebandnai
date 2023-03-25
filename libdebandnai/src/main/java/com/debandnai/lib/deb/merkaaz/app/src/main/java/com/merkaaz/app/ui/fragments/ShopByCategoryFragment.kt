package com.merkaaz.app.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.paging.LoadState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.PagingFooterAdapter
import com.merkaaz.app.adapter.ProfitLossAdapter
import com.merkaaz.app.adapter.ShopByCategoryAdapter
import com.merkaaz.app.adapter.ShopByCategoryCartListAdapter
import com.merkaaz.app.network.Response
import com.merkaaz.app.data.model.CategoryData
import com.merkaaz.app.data.model.CategoryListModel
import com.merkaaz.app.data.model.ShopByCategoryList
import com.merkaaz.app.data.viewModel.DashboardViewModel
import com.merkaaz.app.data.viewModel.ShopByCategoryViewModel
import com.merkaaz.app.databinding.FragmentShopByCategoryBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.ui.base.BaseFragment
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.SHOP_BY_CATEGORY
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ShopByCategoryFragment : BaseFragment(), AdapterItemClickListener {
    private var shopByCategoryCartListAdapter: ShopByCategoryCartListAdapter? = null
    private lateinit var binding: FragmentShopByCategoryBinding
    private  var loader: Dialog ?=null
    private lateinit var shopByCategoryViewModel: ShopByCategoryViewModel
    private var isSwipeRefreshLoader: Boolean = false
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_shop_by_category, container, false)
        shopByCategoryViewModel = ViewModelProvider(this)[ShopByCategoryViewModel::class.java]
        binding.viewModel = shopByCategoryViewModel
        binding.lifecycleOwner = this
        initialise()
        observeData()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        dashboardViewModel.isShowToolbar.value = true
        dashboardViewModel.isShowSearchbar.value = false
        dashboardViewModel.isShowBottomNavigation.value = false
        dashboardViewModel.isSetCollapsingToolbar.value = true
        dashboardViewModel.isShowToolbarOptionsWithLogo.value = false
        dashboardViewModel.isShowBottomNavigation.value = true
        dashboardViewModel.pendingapprovalStat.value = true
        dashboardViewModel.isShowHelpLogo.value = true
        dashboardViewModel.headerText.value = getString(R.string.shop_by_category)
        dashboardViewModel.getCartCount()

    }

    private fun initialise() {
        activity.let { loader = MethodClass.custom_loader(it, getString(R.string.please_wait)) }


        //shopByCategoryViewModel.categoryList()
        context?.let {
            val json = commonFunctions.commonJsonData()
            json.addProperty("rows", Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE)
            json.addProperty("parent_cat_id", "0")
            shopByCategoryViewModel.getData(json)
            val c2 = ContextCompat.getColor(it, R.color.teal_700)

            binding.swipeRefreshLayout.setColorSchemeColors(c2)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
           // shopByCategoryViewModel.categoryList()
            isSwipeRefreshLoader = true
            shopByCategoryCartListAdapter.let {  shopByCategoryCartListAdapter?.refresh()}
        }


    }
    private fun observeData() {
        shopByCategoryCartListAdapter = ShopByCategoryCartListAdapter(this)
        binding.rvCategory.adapter = shopByCategoryCartListAdapter?.withLoadStateFooter(
            footer = PagingFooterAdapter { shopByCategoryCartListAdapter?.retry() }
        )
                shopByCategoryViewModel.data_list.observe(viewLifecycleOwner) {
            try {
                shopByCategoryCartListAdapter?.submitData(lifecycle, it)
            } catch (e: Exception) {
            }
        }

        shopByCategoryCartListAdapter?.addLoadStateListener { loadState ->
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
                (loadState.append.endOfPaginationReached && shopByCategoryCartListAdapter?.itemCount == 0)
            if (displayEmptyMessage) {
               context?.let {  MethodClass.custom_msg_dialog(it, getString(R.string.no_data_found))?.show()}
            }


        }
    }


    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {
        if (Objects.equals(tag, SHOP_BY_CATEGORY)) {
            dashboardViewModel.num_of_tabs = 1
            val shopByCategoryList: List<ShopByCategoryList> = arrayList as List<ShopByCategoryList>
            val bundle = Bundle()
            bundle.putInt(Constants.PRODUCT_CATEGORY, Constants.PRODUCT_ID)
            bundle.putString(Constants.CATEGORY_ID_TAG,
                shopByCategoryList[position].categoryId.toString()
            )
            clickView?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_shopByCategoryFragment_to_productsFragment, bundle)
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

}