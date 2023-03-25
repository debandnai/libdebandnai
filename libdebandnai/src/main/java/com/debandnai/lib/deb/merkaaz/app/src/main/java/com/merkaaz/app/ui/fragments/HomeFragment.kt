package com.merkaaz.app.ui.fragments

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.HomeBestSellerAdapter
import com.merkaaz.app.adapter.HomeCategoryAdapter
import com.merkaaz.app.adapter.HomeFeaturedAdapter
import com.merkaaz.app.adapter.HomeSnacksAdapter
import com.merkaaz.app.data.model.*
import com.merkaaz.app.data.viewModel.DashboardViewModel
import com.merkaaz.app.data.viewModel.HomeViewModel
import com.merkaaz.app.databinding.FragmentHomeBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.ui.activity.ProductDetailsActivity
import com.merkaaz.app.ui.base.BaseFragment
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.HOME_BEST_SELLER
import com.merkaaz.app.utils.Constants.HOME_CATEGORY
import com.merkaaz.app.utils.Constants.HOME_FEATURED_CATEGORY
import com.merkaaz.app.utils.Constants.HOME_SNACKS
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment(), AdapterItemClickListener, ProductAddRemoveListner {
    private var homeCategoryAdapter = HomeCategoryAdapter(this)
    private var homeFeaturedAdapter: HomeFeaturedAdapter? = null
    private var homeBestSellerAdapter: HomeBestSellerAdapter? = null
    private var homeSnacksAdapter: HomeSnacksAdapter? = null

    private lateinit var binding: FragmentHomeBinding
    var homeViewModel: HomeViewModel? = null
    private var loader: Dialog? = null

    private var ctx: Context? = null
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onDetach() {
        super.onDetach()
        ctx = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.viewModel = homeViewModel
        binding.context = this
        binding.lifecycleOwner = this

        init()
        onViewClick()
        observeData()

        return binding.root
    }


    private fun observeData() {
        //For Category List
        homeViewModel?.categoryListLiveData?.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                    if (loader?.isShowing == false)
                        loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val categoryData = data.response?.data
                            val gson = Gson()
                            val categoryType = object : TypeToken<CategoryListModel>() {}.type
                            val categoryListModel: CategoryListModel =
                                gson.fromJson(categoryData, categoryType)
                            binding.rvCategory.adapter = homeCategoryAdapter
                            homeCategoryAdapter.submitList(categoryListModel.categoryList)
                            binding.rvCategory.isFocusable = false
                        }

                    }
                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
            }
        }

        //For Featured List
        homeViewModel?.featuredListLiveData?.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
//                    if (loader?.isShowing == false)
//                        loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        //loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val featuredData = data.response?.data
                            val gson = Gson()
                            val featuredType = object : TypeToken<FeatureProductListModel>() {}.type
                            val featureProductListModel: FeatureProductListModel =
                                gson.fromJson(featuredData, featuredType)
                            if (featureProductListModel.featureList.size>=4)
                                binding.txtFeaturedVwMore.visibility=View.VISIBLE
                            else
                                binding.txtFeaturedVwMore.visibility=View.INVISIBLE
                            homeFeaturedAdapter?.productList = featureProductListModel.featureList
                            homeFeaturedAdapter?.notifyDataSetChanged()
                        }

                    }
                }
                is Response.Error -> {
                    //loader?.dismiss()
//                    commonFunctions.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
            }
        }

        //For Best Seller List
        homeViewModel?.bestSellerListLiveData?.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
//                    if (loader?.isShowing == false)
//                        loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        //loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val bestSellerData = data.response?.data
                            val gson = Gson()
                            val bestSellerType =
                                object : TypeToken<FeatureProductListModel>() {}.type
                            val featureProductListModel: FeatureProductListModel =
                                gson.fromJson(bestSellerData, bestSellerType)
                            homeBestSellerAdapter?.productList =
                                featureProductListModel.bestSellerList
                            homeBestSellerAdapter?.notifyDataSetChanged()
                        }

                    }
                }
                is Response.Error -> {
                    //loader?.dismiss()
//                    MethodClass.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }


            }
        }

        //For Snacks and branded List
        homeViewModel?.snacksAndBrandedLiveData?.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
//                    if (loader?.isShowing == false)
//                        loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val snacksAndBrandedListData = data.response?.data
                            val gson = Gson()
                            val snacksAndBrandedListType =
                                object : TypeToken<FeatureProductListModel>() {}.type
                            val featureProductListModel: FeatureProductListModel =
                                gson.fromJson(snacksAndBrandedListData, snacksAndBrandedListType)
                            binding.txtSnackBrand.text = featureProductListModel.categoryName
                            if (featureProductListModel.productList.size>=4)
                                binding.txtSnacksBrandedVwMore.visibility=View.VISIBLE
                            else
                                binding.txtSnacksBrandedVwMore.visibility=View.INVISIBLE
                            homeSnacksAdapter?.productList = featureProductListModel.productList
                            Log.e("observeData: ", featureProductListModel.productList.toString())
                            homeSnacksAdapter?.notifyDataSetChanged()
                            dashboardViewModel.parentCat_ID = featureProductListModel.parentCatId
                        }

                    }
                }
                is Response.Error -> {
                    loader?.dismiss()
//                    MethodClass.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
                else -> loader?.dismiss()

            }
        }

        //add update
        homeViewModel?.addUpdateLiveData?.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        //loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            if (data.toString().contains("product_count")) {
                                val jsonobj  = JSONObject(data.response!!.data.toString())
                                activity?.let { ctx ->
                                    jsonobj.getInt("product_count").let { count ->
                                        (ctx as DashBoardActivity).showHideNotificationBatch(count)
                                    }

                                }
                            }
                            homeViewModel?.dashBoardAllProductList()

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


    }


    private fun init() {

        //initialize loader
        binding.root.context?.let {
            loader = MethodClass.custom_loader(it, getString(R.string.please_wait))
        }


        activity?.let { activity ->
            //featured
            homeFeaturedAdapter = HomeFeaturedAdapter(ArrayList(), this, this, this, activity, loginModel.isActive)
            binding.rvFeature.adapter = homeFeaturedAdapter
            binding.rvCategory.isFocusable = false

            //best seller
            homeBestSellerAdapter =
                HomeBestSellerAdapter(ArrayList(), this, this, this, activity, loginModel.isActive)
            binding.rvBestSeller.adapter = homeBestSellerAdapter
            binding.rvBestSeller.isFocusable = false

            //best seller
            homeSnacksAdapter =
                HomeSnacksAdapter(ArrayList(), this, this, this, activity, loginModel.isActive)
            binding.rvSnacks.adapter = homeSnacksAdapter
            binding.rvSnacks.isFocusable = false

            dashboardViewModel.search_text.value = ""
        }


    }

    private fun onViewClick() {
        binding.txtFeaturedVwMore.setOnClickListener{
            activity?.let {act->
                (act as DashBoardActivity).featured_product()
            }
        }

        binding.txtSnacksBrandedVwMore.setOnClickListener{
            activity?.let {act->
                (act as DashBoardActivity).produc_list()
            }
        }

        binding.btnShopNow.setOnClickListener {
            dashboardViewModel.parentCat_ID=""
            val bundle = Bundle()
            bundle.putInt(Constants.PRODUCT_CATEGORY, Constants.PRODUCT_ID)
            bundle.putString(Constants.CATEGORY_ID_TAG, dashboardViewModel.parentCat_ID)
            Navigation.findNavController(binding.btnShopNow)
                .navigate(
                    R.id.action_homeFragment_to_productsFragment,
                    bundle
                )
        }
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.isShowToolbar.value = true
        dashboardViewModel.isShowSearchbar.value = true
        dashboardViewModel.isShowSearchbar.value = true
        dashboardViewModel.isSetCollapsingToolbar.value = true
        dashboardViewModel.isShowToolbarOptionsWithLogo.value = true
        dashboardViewModel.isShowBottomNavigation.value = true
        dashboardViewModel.isShowHelpLogo.value = false
        //activity?.let { (it as DashBoardActivity).deselectAllBottomNavItems(false) }
        ctx?.let {ctx->
            (ctx as DashBoardActivity).check_user_active(loginModel)
        }
        dashboardViewModel.getCartCount()

        Log.d(ContentValues.TAG, "onResume:home $loginModel.isActive")
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {
        if (tag.equals(HOME_CATEGORY, true)) {
            dashboardViewModel.num_of_tabs = 1
            val catList: List<CategoryData> = arrayList as List<CategoryData>
            val bundle = Bundle()
            bundle.putInt(Constants.PRODUCT_CATEGORY, Constants.PRODUCT_ID)
            bundle.putString(Constants.CATEGORY_ID_TAG, catList[position].categoryId)
            clickView?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_homeFragment_to_productsFragment, bundle)
            }
        }else if (tag.equals(HOME_FEATURED_CATEGORY, true)) {
            val catList: List<FeaturedData> = arrayList as List<FeaturedData>
            startActivity(Intent(binding.root.context, ProductDetailsActivity::class.java).apply {
                this.putExtra(Constants.CATEGORY_ID_TAG, catList[position].productId)
            })
        }else if (tag.equals(HOME_BEST_SELLER, true)) {
            val catList: List<FeaturedData> = arrayList as List<FeaturedData>
            startActivity(Intent(binding.root.context, ProductDetailsActivity::class.java).apply {
                this.putExtra(Constants.CATEGORY_ID_TAG, catList[position].productId)
            })
        }else if (tag.equals(HOME_SNACKS, true)) {
            val catList: List<FeaturedData> = arrayList as List<FeaturedData>
            startActivity(Intent(binding.root.context, ProductDetailsActivity::class.java).apply {
                this.putExtra(Constants.CATEGORY_ID_TAG, catList[position].productId)
            })
        }
    }



    override fun onAdditem(item: VariationDataItem?, productID: String?, position: Int?) {
        context?.let {
            homeViewModel?.addUpdateProduct(
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
            homeViewModel?.addUpdateProduct(
                commonFunctions.getaddupdateJsondata(
                    it,
                    productID, item!!, qty
                )
            )
        }
    }

    override fun onRemoveitem(item: VariationDataItem?, position: Int?) {
        context?.let {
            homeViewModel?.removeProduct(commonFunctions.getremoveJsondata(item!!))
        }
    }

    fun observeRecyclerviewData_homefeaturedata(listItem: FeaturedData?) {
        listItem?.change_data?.observe(viewLifecycleOwner){ _ ->
            dashboardViewModel.getCartCount()
        }
    }

    fun observeRecyclerviewData_homebestseller(listItem: FeaturedData?) {
        listItem?.change_data?.observe(viewLifecycleOwner){ _ ->
            dashboardViewModel.getCartCount()
        }
    }

    fun observeRecyclerviewData_snackbar(listItem: FeaturedData?) {
        listItem?.change_data?.observe(viewLifecycleOwner){ _ ->
            dashboardViewModel.getCartCount()
        }
    }
    fun refreshAdapter(){
        loginModel.isActive.apply {
            homeSnacksAdapter?.isActive=this
            homeBestSellerAdapter?.isActive=this
            homeFeaturedAdapter?.isActive=this
        }

        homeSnacksAdapter?.notifyDataSetChanged()
        homeBestSellerAdapter?.notifyDataSetChanged()
        homeFeaturedAdapter?.notifyDataSetChanged()

    }

}