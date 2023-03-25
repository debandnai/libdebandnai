package com.merkaaz.app.ui.fragments


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.*
import com.merkaaz.app.data.model.*
import com.merkaaz.app.data.viewModel.CategoryViewModel
import com.merkaaz.app.data.viewModel.DashboardViewModel
import com.merkaaz.app.databinding.DialogFilterLayoutBinding
import com.merkaaz.app.databinding.ProductCategoryFregmentBinding
import com.merkaaz.app.databinding.ProductItemsLayoutBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.ui.activity.ProductDetailsActivity
import com.merkaaz.app.ui.base.BaseFragment
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.BEST_SELLER_ID
import com.merkaaz.app.utils.Constants.FEATURED_PRODUCT_ID
import com.merkaaz.app.utils.Constants.SORT_FIELD
import com.merkaaz.app.utils.Constants.SORT_ORDERS
import com.merkaaz.app.utils.Constants.VALUE
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ProductCategoryFragment : BaseFragment(), AdapterItemClickListener, ProductAddRemoveListner {
    var pageTitle = ""
    private lateinit var binding: ProductCategoryFregmentBinding
    private var loader: Dialog? = null
    private var noSearchResultFound: Dialog? = null
    private lateinit var categoryViewModel: CategoryViewModel
    private var productCatAdapter: ProductCatAdapter? = null
    private var productListAdapter: ProductListAdapter? = null
    private var subCatlList: ArrayList<SubCategory> = ArrayList()
    private var position: Int? = -1
    private var qty: Int? = -1
    private var items: VariationDataItem? = null
    private var isSwipeRefreshLoader: Boolean = false
    private var pageType: Int? = 0
    private var categoryId: String? = null
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    //    private var txt_count : TextView ?=null
    var productSearchAdapter: ProductSearchAdapter? = null

    //    var sub_cat_id: String? = null
    var search_txt: String? = null

    @Inject
    lateinit var commonFunctions: CommonFunctions

    //    private var resultLauncherFilter: ActivityResultLauncher<Intent>? = null
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.product_category_fregment, container, false)
        activity?.run {
            categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        }
        binding.viewModel = categoryViewModel
        binding.lifecycleOwner = this


        initialise()

        getIntent()
        set_product_data()
        onViewClick()
        setObserveAdapter()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        dashboardViewModel.isShowToolbar.value = true
        dashboardViewModel.isShowSearchbar.value = true
        dashboardViewModel.isShowToolbarOptionsWithLogo.value = false
        dashboardViewModel.isShowBottomNavigation.value = true
        dashboardViewModel.headerText.value = pageTitle
        dashboardViewModel.pendingapprovalStat.value = true
        dashboardViewModel.isShowHelpLogo.value = true
        dashboardViewModel.getCartCount()

        if (noSearchResultFound?.isShowing == true)
            noSearchResultFound?.dismiss()
        }

    private fun getIntent() {

        //val pageType = arguments?.getString(Constants.PRODUCT_CATEGORY_OR_FEATURED_PRODUCT_TYPE)
        pageType = arguments?.getInt(Constants.PRODUCT_CATEGORY)
        pageType?.let {
            categoryViewModel.product_category = it
        }
        binding.rvProdCat.visibility = View.GONE
        binding.rlFilterContainer.visibility = View.GONE

        context?.let {
            val json = commonFunctions.commonJsonData()
            json.addProperty("list_type", pageType)
            json.addProperty("rows", Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE)
            when (pageType) {
                Constants.FEATURED_PRODUCT_ID -> {
                    pageTitle = getString(R.string.featured_products)
                    categoryViewModel.getData(json, pageType, categoryId)
                }
                Constants.BEST_SELLER_ID -> {
                    pageTitle = getString(R.string.best_seller)
                    categoryViewModel.getData(json, pageType, categoryId)
                }
                Constants.PRODUCT_ID -> {
                    pageTitle = getString(R.string.product_list)
                    binding.rvProdCat.visibility = View.VISIBLE
                    binding.rlFilterContainer.visibility = View.VISIBLE
                    categoryId = arguments?.getString(Constants.CATEGORY_ID_TAG)
                    categoryViewModel.getProductSubcategory(categoryId)
                    println("category id....$categoryId")
//                    if (categoryId?.isEmpty() == true) {
                    //First time one filter tab will show
                    dashboardViewModel.num_of_tabs = 1
                    //First time filter button text will show sort by
                    binding.btnFilter.text = getString(R.string.sort_by)
//                    }
                    callProduct(
                        pageType, categoryId, Constants.PARENTCATEGORY, it,
                        SORT_FIELD, SORT_ORDERS, json_blank_filter()
                    )
                }
                Constants.Search_ID -> {
                    //binding.rvProdList.visibiy = View.GONE
                    //binding.rvSearchList.visibility = View.VISIBLE

                    loader?.dismiss()
                    if (binding.rvProdList.visibility == View.GONE)
                        binding.rvSearchList.visibility = View.VISIBLE
                    search_txt = dashboardViewModel.search_text.value
                    pageTitle = getString(R.string.search)
                    if (search_txt?.trim()?.isNotEmpty() == true)
                        categoryViewModel.getsearchData(dashboardViewModel.search_text.value)
                }
            }
        }

    }

    private fun set_product_data() {

        activity?.let { act ->
            if (pageType == Constants.Search_ID) {
                binding.rvSearchList.visibility = View.VISIBLE
                binding.rvProdList.visibility = View.GONE
                productSearchAdapter = ProductSearchAdapter(
                    act, this, this, this, loginModel.isActive, ArrayList()
                )
                binding.rvSearchList.adapter = productSearchAdapter

            } else {
                binding.rvSearchList.visibility = View.GONE
                binding.rvProdList.visibility = View.VISIBLE
                productListAdapter =
                    ProductListAdapter(
                        this, this, act, this,
                        loginModel.isActive
                    )

                binding.rvProdList.adapter = productListAdapter?.withLoadStateFooter(
                    footer = PagingFooterAdapter { productListAdapter?.retry() }
                )
                productListAdapter?.addLoadStateListener { loadState ->
                    when (loadState.refresh) {

                        is LoadState.Loading ->
                            showLoader()
                        is LoadState.NotLoading ->
                            hideLoader()
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
                    // categoryViewModel.category_item.value = productListAdapter?.itemCount
                    if (categoryViewModel.product_category != BEST_SELLER_ID && categoryViewModel.product_category != FEATURED_PRODUCT_ID) {
                       // if (binding.rvProdList.visibility==View.VISIBLE) {
                            categoryViewModel.category_item.value =
                                sharedPreff.getTotalProductCount()
                       /* }
                        else{
                            categoryViewModel.category_item.value =0
                        }*/
                    } else {
                        categoryViewModel.category_item.value =0
                    }



                    Log.d(
                        TAG,
                        "set_product_data: categoryViewModel.product_category " + categoryViewModel.product_category
                    )
                    if (loadState.append.endOfPaginationReached && productListAdapter?.itemCount == 0) {
                        println("Error data adapter"+loadState.append.endOfPaginationReached)
                        println("item count adapter.." + productListAdapter!!.itemCount)
                       // categoryViewModel._filter_livedata.value = FilterListModel()
                    }
                }
            }
        }

    }

    private fun json_blank_filter(): JsonObject {
        val jsonArray = JsonArray()
        val value = JsonObject()
        value.add("value", jsonArray)
        val filter = JsonObject()
        filter.add("brand", value)
        filter.add("price", value)
        filter.add("discount", value)
        filter.add("size", value)
        return filter
    }

    private fun callProduct(
        pageType: Int?,
        categoryId: String?,
        category: String,
        context: Context,
        sortFields: String,
        sortOrders: String,
        filter: JsonObject,
    ) {
        // binding.rvSearchList.visibility = View.GONE
        // binding.rvProdList.visibility = View.VISIBLE
        if (binding.rvSearchList.visibility == View.GONE)
            binding.rvProdList.visibility = View.VISIBLE
        val json = commonFunctions.commonJsonData()
        if (categoryId?.trim()?.isEmpty() == true) {
            json.addProperty("cat_id", "")
            json.addProperty("cat_type", "")
        } else {
            json.addProperty("cat_id", categoryId)
            json.addProperty("cat_type", category)
        }

        json.addProperty("rows", Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE)
        json.addProperty("sortField", sortFields)
        json.addProperty("sortOrder", sortOrders)
        json.add("filters", filter)
//            println("json output....${json.toString()}")
//        productListAdapter?.refresh()
        productListAdapter?.submitData(lifecycle, PagingData.empty())
        if (categoryId?.trim()?.isEmpty() == true) {
            categoryViewModel.getData(json, pageType, "")
        } else {
            categoryViewModel.getData(json, pageType, categoryId)
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setObserveAdapter() {

        categoryViewModel.data_list.observe(viewLifecycleOwner) {
            try {
                productListAdapter?.submitData(lifecycle, it)
//                loader?.dismiss()
            } catch (e: Exception) {
                println("Error data error...$e")
            }

        }

        categoryViewModel.subcategoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                    if (!loader?.isShowing!!)
                        loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                   //     loader?.dismiss()

                        if (data.response?.status?.actionStatus == true) {
                            val categorydata = data.response?.data

                            val gson = Gson()

                            if (categoryId?.isEmpty() == true) {
                                val allCategoryType =
                                    object : TypeToken<AllCategoryListModel>() {}.type
                                val allCategoryListModel: AllCategoryListModel =
                                    gson.fromJson(categorydata, allCategoryType)
                                if (allCategoryListModel.subCategoryList.size > 0) {
                                    subCatlList = allCategoryListModel.subCategoryList
                                    productCatAdapter?.subCategoryList = subCatlList
                                } else {
                                    productCatAdapter?.subCategoryList = ArrayList()
                                    loader?.dismiss()
                                }
                                productCatAdapter?.notifyDataSetChanged()
                                refresh_data()

                            } else {
                                val categoryType = object : TypeToken<CategoryListModel>() {}.type
                                val categoryListModel: CategoryListModel =
                                    gson.fromJson(categorydata, categoryType)
                                if (categoryListModel.categoryList.size > 0) {
                                    if (categoryListModel.categoryList[0].subCategory.isNotEmpty()) {
                                        binding.rvProdList.visibility=View.VISIBLE
                                        subCatlList = categoryListModel.categoryList[0].subCategory
                                        productCatAdapter?.subCategoryList = subCatlList
                                        productCatAdapter?.notifyDataSetChanged()
                                        refresh_data()
                                    } else {

                                        binding.rvProdList.visibility=View.GONE
                                        categoryViewModel.category_item.value =0
                                        productCatAdapter?.subCategoryList = ArrayList()
                                        productCatAdapter?.notifyDataSetChanged()
                                        hideLoader()
                                        context?.let {
                                            MethodClass.custom_msg_dialog_callback(it,it.getString(R.string.product_not_found),object :
                                                DialogCallback {
                                                override fun dialog_clickstate() {
                                                    val intent = Intent(context,DashBoardActivity::class.java).apply {
                                                        this.data= Uri.parse("merkaaz://com.merkaaz./ProductCategoryFragment")
                                                    }
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    startActivity(intent)
                                                    binding.rvProdList
                                                }
                                            })
                                        }



                                    }
                                }
                            }
                        }
                    }
                }
                is Response.Error -> {
                    loader?.dismiss()
                }
            }
        }

        categoryViewModel.filter_list_response.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        if (data.response?.status?.actionStatus == true) {
                            val filterdata = data.response?.data
                            val gson = Gson()
                            val filterType = object : TypeToken<FilterListModel>() {}.type
                            val categoryListModel: FilterListModel =
                                gson.fromJson(filterdata, filterType)
                            categoryViewModel._filter_livedata.value = categoryListModel
                        }
                    }
                }
                is Response.Error -> {
                }
            }
        }

        categoryViewModel.addUpdateLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is Response.Loading -> {
                    if (!loader?.isShowing!!)
                        loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val jsonobj = JSONObject(data.response!!.data.toString())
                            if (qty == 0) {
                                items?.presentInCart = Constants.NO
                                items?.cartId = "0"
                            } else {
                                items?.cartId = jsonobj.getString("id")

                            }
//                            val count =jsonobj.getInt("product_count")
                            if (data.toString().contains("product_count")) {
                                activity?.let { ctx ->
//                                Toast.makeText(ctx,"count..$count",Toast.LENGTH_SHORT).show()
                                    jsonobj.getInt("product_count").let { count ->
                                        (ctx as DashBoardActivity).showHideNotificationBatch(count)
                                    }

                                }
                            }
                            items?.variationData_qty = qty!!
                            println("......position..$productListAdapter....$position+.....quantity....$qty......${items?.variationData_qty}")
                            productListAdapter?.let { adapter ->
                                println("position... updated")
                                adapter.notifyDataSetChanged()
                            }
                            productSearchAdapter?.let { adapter ->
                                adapter.notifyDataSetChanged()

                            }

                        } else {
                            context?.let { ctx ->
                                MethodClass.custom_msg_dialog(ctx, data.response?.status?.msg)
                            }
                        }
                    }
                }
                is Response.Error -> {
                    loader?.dismiss()
                    context?.let { ctx ->
                        MethodClass.custom_msg_dialog(ctx, it.errorMessage)?.show()
                    }
                }
            }
        }
        categoryViewModel.filter_list_live_data.observe(viewLifecycleOwner) { list ->
            get_filter_count_onclick(get_filter_data(list))
        }
        categoryViewModel.filterCountLiveData.observe(viewLifecycleOwner)
        {
            println("filter data....${it.data.toString()}")
            when (it) {
                is Response.Loading -> {
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        println("filter response...${data.response?.toString()}")
                        if (data.response?.status?.actionStatus == true) {
                            val jsonobj = JSONObject(data.response!!.data.toString())
                            val count = jsonobj.getInt("prod_count")
                            println("filter count...$count")
                            categoryViewModel.filter_count.value = count

                        }
                    }
                }
                is Response.Error -> {
                }
            }
        }

        categoryViewModel.searchResult.observe(viewLifecycleOwner)
        {
            when (it) {
                is Response.Loading -> {
                    if (!loader?.isShowing!!)
                        loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val categorydata = data.response?.data
                            val gson = Gson()
                            val featureType = object : TypeToken<FeatureProductListModel>() {}.type
                            val featureListModel: FeatureProductListModel =
                                gson.fromJson(categorydata, featureType)

                            val productList: ArrayList<FeaturedData> = featureListModel.productList
                            println("search list size..${featureListModel.productList.size}")
                            // productSearchAdapter?.searchList = featureListModel.productList
                           // categoryViewModel.category_item.value = featureListModel.total_count
                            categoryViewModel.category_item.value = 0
                            activity?.let { act ->
                                productSearchAdapter = ProductSearchAdapter(
                                    act,
                                    this,
                                    this,
                                    this,
                                    loginModel.isActive,
                                    featureListModel.productList
                                )
                                binding.rvSearchList.adapter = productSearchAdapter
                                productSearchAdapter?.notifyDataSetChanged()
                                MethodClass.hideSoftKeyboard(act)
                            }

                        } else {
                            activity?.let { activity ->
                                MethodClass.custom_msg_dialog(
                                    activity,
                                    resources.getString(R.string.no_product)
                                )
                            }
                        }

                    }
                    //  categoryViewModel.searchResult.removeObservers(this)
                }
                is Response.Error -> {
                    hideLoader()
                    /* context?.let {
                         MethodClass.custom_msg_dialog(it,resources.getString(R.string.no_product))?.show()
                     }*/

                    if (noSearchResultFound?.isShowing == true)
                        noSearchResultFound?.dismiss()
                    else noSearchResultFound?.show()
                    // categoryViewModel.searchResult.removeObservers(this)
                }
            }
        }

        categoryViewModel.filter_applicable.observe(viewLifecycleOwner)
        { status ->
            if (status == true) {
                binding.btnFilter.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_filter_white,
                    0
                )
            } else {
                binding.btnFilter.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_filter_black,
                    0
                )
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun refresh_data() {
        if (isSwipeRefreshLoader) {
            Log.d(
                TAG,
                "setObserveAdapter:testt categoryId $categoryId  ${hasSubcategoryId(subCatlList)} "
            )
            if (hasSubcategoryId(subCatlList) == true) {
                //categoryViewModel.sub_cat_id.value = categoryViewModel.sub_cat_id.value
                productListAdapter?.refresh()
            } else {
                categoryViewModel.sub_cat_id.value=""
                if (categoryViewModel.filter_applicable.value == true) {
                    set_data_to_post()
                } else {
                    context?.let { context ->
                        callProduct(
                            pageType,
                            categoryId,
                            Constants.PARENTCATEGORY,
                            context,
                            SORT_FIELD,
                            SORT_ORDERS,
                            json_blank_filter()
                        )
                    }
                }
            }
            productCatAdapter?.notifyDataSetChanged()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun hasSubcategoryId(list: List<SubCategory>): Boolean {
        var position = -1
        return list.stream().filter { o: SubCategory ->
            position++
            val hasSubCatId: Boolean = o.subcategoryId.equals(categoryViewModel.sub_cat_id.value)
            if (hasSubCatId) {
                list[position].isSelected = true
            }
            hasSubCatId
        }.findFirst().isPresent
    }

    private fun initialise() {
        productCatAdapter = ProductCatAdapter(this@ProductCategoryFragment, ArrayList())
        binding.rvProdCat.adapter = productCatAdapter
        binding.rvProdCat.isFocusable = false

//        loginModel= SharedPreff.get_logindata(context)
//        sub_cat_id = null
        context?.let {
            val c2 = ContextCompat.getColor(it, R.color.teal_700)

            binding.swipeRefreshLayout.setColorSchemeColors(c2)
            loader = MethodClass.custom_loader(context, getString(R.string.please_wait))
            noSearchResultFound =
                MethodClass.custom_msg_dialog(it, resources.getString(R.string.no_product))
        }

        categoryViewModel.sort_field.value = ""
        categoryViewModel.sort_order.value = null
        categoryViewModel.sub_cat_id.value = ""
        categoryViewModel.filter_applicable.value = false

        binding.swipeRefreshLayout.setOnRefreshListener {
            isSwipeRefreshLoader = true
            if (pageType == Constants.Search_ID) {
                categoryViewModel.getsearchData(search_txt)
            } else {
                if (pageType == Constants.PRODUCT_ID) {

                    categoryViewModel.getProductSubcategory(categoryId)
                } else {
                    productListAdapter?.refresh()
                }
            }
        }
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    productListAdapter?.refresh()
                }
            }


    }


    fun observeRecyclerviewData(
        item: FeaturedData,
        binding_inflator: ProductItemsLayoutBinding,
        position: Int
    ) {
        item.change_data.observe(viewLifecycleOwner) { _ ->
            productListAdapter?.let {
                if (!binding.rvProdList.isComputingLayout) {
                    it.notifyItemChanged(position)
                }
            }
            productSearchAdapter?.let { search ->
                if (!binding.rvSearchList.isComputingLayout) {
                    search.notifyItemChanged(position)
                }
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {

        if (tag.equals(Constants.SUB_CATEGORY, true)) {


            isSwipeRefreshLoader = false
            val catList: List<SubCategory> = arrayList as List<SubCategory>

            var parentOrSubCategoryProductId = ""
            var categoryType = ""
            if (catList[position].isSelected) {
                setAfterClickFilterButtonText(1)
                for (i in catList.indices) {
                    catList[i].isSelected = false
                }
                categoryViewModel.sub_cat_id.value = ""
                if (categoryId?.trim()?.isEmpty() == false) {
                    categoryType = Constants.PARENTCATEGORY
                    parentOrSubCategoryProductId = categoryId.toString()
                } else {
                    categoryType = ""
                    parentOrSubCategoryProductId = ""
                }

            } else {

                setAfterClickFilterButtonText(2)
                for (i in catList.indices) {
                    catList[i].isSelected = false
                }
                catList[position].isSelected = true
                categoryViewModel.sub_cat_id.value = catList[position].subcategoryId.toString()
                parentOrSubCategoryProductId = catList[position].subcategoryId.toString()
                categoryType = Constants.SUBCATEGORY
            }

            productCatAdapter?.subCategoryList = catList as ArrayList<SubCategory>


            categoryViewModel.getFilterList(catList[position].subcategoryId.toString())

//                sub_cat_id = catList[position].subcategoryId.toString()
//                categoryViewModel.getFilterCount(json_blank_filter())
            get_filter_count_onclick(json_blank_filter())

            categoryViewModel.filter_applicable.value = false

            productCatAdapter?.notifyDataSetChanged()



            context?.let {
                val jsonArray = JsonArray()
                Log.d(
                    TAG,
                    "onAdapterItemClick:subcategoryId " + catList[position].subcategoryId.toString()
                )
                callProduct(
                    Constants.PRODUCT_ID, parentOrSubCategoryProductId,
                    categoryType, it, "", "", json_blank_filter()
                )
            }

        } else if (tag.equals(Constants.PAGING_ADAPTER_ITEM_CLICK, true)) {
            val prodList: List<FeaturedData> = arrayList as List<FeaturedData>
            resultLauncher?.launch(
                Intent(binding.root.context, ProductDetailsActivity::class.java).apply {
                    this.putExtra(Constants.CATEGORY_ID_TAG, prodList[position].productId)
                }
            )
        }


    }

    private fun setAfterClickFilterButtonText(filterTabItems: Int) {
        //After click subcategory or product two filter tab will show
        dashboardViewModel.num_of_tabs = filterTabItems
        //After click subcategory or product filter button text will show filter by
        if (filterTabItems == 2)
            binding.btnFilter.text = getString(R.string.filter)
        else
            binding.btnFilter.text = getString(R.string.sort_by)

    }

    override fun onAdditem(item: VariationDataItem?, productID: String?, position: Int?) {
        context?.let {
            this.position = position
            this.qty = 1
            if (item != null) {
                items = item
            }
            categoryViewModel.add_update_Product(
                commonFunctions.getaddupdateJsondata(
                    it,
                    productID, item!!, 1
                )
            )
        }

    }

    override fun onUpdateitem(
        item: VariationDataItem?,
        productID: String?,
        position: Int?,
        qty: Int?
    ) {
        context?.let {
            this.position = position
            this.qty = qty
            println("quantity....$qty......")
            if (item != null) {
                items = item
            }
            categoryViewModel.add_update_Product(
                commonFunctions.getaddupdateJsondata(
                    it,
                    productID, item!!, qty
                )
            )
        }
    }

    override fun onRemoveitem(item: VariationDataItem?, position: Int?) {
        context?.let {
            this.position = position
            this.qty = 0
            println("quantity....$qty......")
            if (item != null) {
                items = item
            }
            categoryViewModel.remove_Product(commonFunctions.getremoveJsondata(item!!))
        }
    }

    private fun showLoader() {
        if (isSwipeRefreshLoader) {
            loader?.dismiss()
        } else {
            if (!loader?.isShowing!!) {
                loader?.show()
            }
        }

    }

    private fun hideLoader() {
        with(binding) {
            loader?.dismiss()
            swipeRefreshLayout.isRefreshing = false
        }

    }


    private fun onViewClick() {
        binding.btnFilter.setOnClickListener {
            activity?.let {
                showDialog()
            }
        }

    }

    private fun showDialog() {
        binding.root.context?.let { ctx ->
            val dialog = Dialog(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            val dialogbinding: DialogFilterLayoutBinding =
                DialogFilterLayoutBinding.inflate(LayoutInflater.from(context))
            dialogbinding.viewModel = categoryViewModel
            dialogbinding.lifecycleOwner = this
            dialog.setContentView(dialogbinding.root)

            val viewPagerAdapter =
                ViewPagerAdapter(parentFragmentManager, lifecycle, dashboardViewModel.num_of_tabs)
            dialogbinding.vwPager.adapter = viewPagerAdapter

            val lp = dialogbinding.tabLayout.layoutParams
            if (dashboardViewModel.num_of_tabs == 1) {
                val width: Int = Resources.getSystem().displayMetrics.widthPixels / 2
                lp.width = width
//                dialogbinding.txtProductsAvialable.visibility = View.GONE
            } else {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
//                dialogbinding.txtProductsAvialable.visibility = View.VISIBLE
            }
            dialogbinding.tabLayout.layoutParams = lp
            TabLayoutMediator(dialogbinding.tabLayout, dialogbinding.vwPager) { tab, position ->
                if (dashboardViewModel.num_of_tabs == 1)
                    tab.text = resources.getString(R.string.sort_by)
                else {
                    tab.text = when (position) {
                        0 -> resources.getString(R.string.filter_by)
                        else -> resources.getString(R.string.sort_by)
                    }
                }
            }.attach()

            dialogbinding.imgClose.setOnClickListener {
                dialog.dismiss()
            }
            dialogbinding.btnReset.setOnClickListener {
                if (dashboardViewModel.num_of_tabs != 1) {
                    (parentFragmentManager.findFragmentByTag("f" + viewPagerAdapter.getItemId(0)) as FilterByTabFragment?)?.reset()
                    (parentFragmentManager.findFragmentByTag("f" + viewPagerAdapter.getItemId(1)) as SortByTabFragment?)?.reset()
                } else {
                    (parentFragmentManager.findFragmentByTag("f" + viewPagerAdapter.getItemId(0)) as SortByTabFragment?)?.reset()
                }
                categoryViewModel.filter_applicable.value = false
                get_filter_count_onclick(json_blank_filter())

            }
            dialogbinding.btnDone.setOnClickListener {
                if (categoryViewModel.filter_count.value!! > 0) {
                    /*if (categoryViewModel.sub_cat_id.value?.isNotEmpty() == true)
                    categoryViewModel.getFilterList(categoryViewModel.sub_cat_id.value)*/
                    /*set_data_to_post()
                    dialog.dismiss()*/

                    if (categoryViewModel.sort_order.value==null && categoryViewModel.sort_field.value.isNullOrEmpty()){
                        set_data_to_post()
                        dialog.dismiss()

                    }
                    else if(categoryViewModel.sort_order.value==null || categoryViewModel.sort_field.value.isNullOrEmpty()){
                        binding.root.context?.let { ctx ->
                            MethodClass.custom_msg_dialog(
                                ctx,
                                ctx.resources.getString(R.string.please_select_sort_by_and_sort_order)
                            )?.show()
                        }
                    }
                    else{
                        set_data_to_post()
                        dialog.dismiss()
                    }


                } else {
                    binding.root.context?.let { ctx ->
                        MethodClass.custom_msg_dialog(
                            ctx,
                            ctx.resources.getString(R.string.no_product_to_show)
                        )?.show()
                    }

                }
            }

            dialog.show()
//            get_filter_count_onclick(json_blank_filter())
            get_filter_count_onclick(get_filter_data(categoryViewModel.filter_list_live_data.value))
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun get_filter_count_onclick(filter: JsonObject) {
        if (hasSubcategoryId(subCatlList) == true) {
            categoryViewModel.getFilterCount(filter, "", Constants.SUBCATEGORY)
        } else if (categoryId?.trim()?.isNotEmpty() == true) {
            categoryViewModel.getFilterCount(filter, categoryId!!, Constants.PARENTCATEGORY)
        } else {
            categoryViewModel.getFilterCount(filter, "", "")
        }
    }

    // For filter data
    private fun set_data_to_post() {
        var sort_order = ""
        var sort_field = ""
        categoryViewModel.sort_order.value?.let { order ->
            sort_order = order.toString()
        }

        categoryViewModel.sort_field.value?.let { field ->
            sort_field = field.toString()
        }
        context?.let {
            if (categoryViewModel.sub_cat_id.value?.isNotEmpty() == true) {
                callProduct(
                    0, categoryViewModel.sub_cat_id.value, Constants.SUBCATEGORY, it,
                    sort_field, sort_order, get_filter_data(categoryViewModel.filterList)
                )
            } else {
                callProduct(
                    pageType, categoryId, Constants.PARENTCATEGORY, it,
                    sort_field, sort_order, get_filter_data(categoryViewModel.filterList)
                )
            }
        }

    }

    private fun get_filter_data(list: java.util.ArrayList<FilterCategoryModel>?): JsonObject {
        var filter = JsonObject()
        val brand_value = JsonObject()
        val price_value = JsonObject()
        val discount_value = JsonObject()
        val size_value = JsonObject()
        list?.let { list ->
            for (j in 0 until list.size) {
                var jsonArray = JsonArray()
                var main_list = list[j]


                if (list[j].parentTitle != null) {
//                    println("values parent....$j....${main_list}")
                    for (i in 0 until list[j].childList.size) {
                        var child_list = list[j].childList[i]
                        println("Filter list${main_list}.........$child_list")
                        if (list[j].childList[i].isChecked) {
                            jsonArray.add(list[j].childList[i].id)
                        }
                    }

                    when (list[j].parentTitle) {
                        getString(R.string.brand) -> brand_value.add(VALUE, jsonArray)
                        getString(R.string.price) -> price_value.add(VALUE, jsonArray)
                        getString(R.string.discount) -> discount_value.add(VALUE, jsonArray)
                        getString(R.string.pack_sizes) -> size_value.add(VALUE, jsonArray)
                    }

                    jsonArray = JsonArray()
                }
            }
            if (brand_value.size() == 0)
                brand_value.add(VALUE, JsonArray())
            else if (price_value.size() == 0)
                price_value.add(VALUE, JsonArray())
            else if (discount_value.size() == 0)
                discount_value.add(VALUE, JsonArray())
            else if (size_value.size() == 0)
                size_value.add(VALUE, JsonArray())

            filter.add("brand", brand_value)
            filter.add("price", price_value)
            filter.add("discount", discount_value)
            filter.add("size", size_value)
            println("filter....$filter")
        }

        if (categoryViewModel.filterList == null) {
            filter = json_blank_filter()
//            categoryViewModel.filter_applicable.value = false
        } else {
            if (brand_value.size() == 0)
                brand_value.add(VALUE, JsonArray())
            else if (price_value.size() == 0)
                price_value.add(VALUE, JsonArray())
            else if (discount_value.size() == 0)
                discount_value.add(VALUE, JsonArray())
            else if (size_value.size() == 0)
                size_value.add(VALUE, JsonArray())

            categoryViewModel.filter_applicable.value =
                !(brand_value.getAsJsonArray(VALUE).size() == 0 &&
                        price_value.getAsJsonArray(VALUE).size() == 0 &&
                        discount_value.getAsJsonArray(VALUE).size() == 0 &&
                        size_value.getAsJsonArray(VALUE).size() == 0)

        }
        return filter
    }

    /*override fun onStop() {
        super.onStop()
        categoryViewModel.searchResult.removeObservers(this)
    }*/
}