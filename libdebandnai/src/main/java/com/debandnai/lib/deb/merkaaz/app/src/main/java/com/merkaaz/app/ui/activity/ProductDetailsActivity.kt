package com.merkaaz.app.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.*
import com.merkaaz.app.data.model.*
import com.merkaaz.app.network.Response
import com.merkaaz.app.data.viewModel.ProductDetailsViewModel
import com.merkaaz.app.databinding.ActivityProductDetailsBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.FROM
import com.merkaaz.app.utils.Constants.PRODUCT_DETAILS
import com.merkaaz.app.utils.Constants.PRODUCT_DETAILS_VW_MORE
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.MethodClass.isVIewVisible
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsActivity : BaseActivity(), AdapterItemClickListener, ProductAddRemoveListner,
    SwipeRefreshLayout.OnRefreshListener {
    private val productImageAdapter = ProductImageAdapter(this@ProductDetailsActivity)
    private val packSizeAdapter = PackSizeAdapter(this@ProductDetailsActivity, ArrayList())
    private val productSpecificationAdapter = ProductSpecificationAdapter(ArrayList())
    private lateinit var similarProductsAdapter: SimilarProductsAdapter
    private var loader: Dialog? = null

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var productDetailsViewModel: ProductDetailsViewModel
    private var isShowLoader: Boolean = true
    private var position: Int? = -1
    private var qty: Int? = -1
    private lateinit var items: VariationDataItem
    private var mainLayoutClick: Boolean = false
    private var getRelatedProductList: ArrayList<RelatedProductItem?> = ArrayList()
    private var productDetailsModel: ProductDetailsModel? = null

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details)
        productDetailsViewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]

        binding.viewModel = productDetailsViewModel
        binding.activity = this
        binding.lifecycleOwner = this

        init()
        setView()
        observeProductDetailsData()
    }

    override fun onResume() {
        super.onResume()
        productDetailsViewModel.getCartCountResponse()

    }

    private fun setView() {
        binding.clStickyBtns.isVisible =
            binding.nstdScrollview.isVIewVisible(binding.clBtns) == false
        binding.clBtns.isVisible = !binding.clStickyBtns.isVisible
        binding.nstdScrollview.setOnScrollChangeListener { _: View, _: Int, _: Int, _: Int, _: Int ->
            binding.clStickyBtns.isVisible =
                binding.nstdScrollview.isVIewVisible(binding.clBtns) == false
            binding.clBtns.isVisible = !binding.clStickyBtns.isVisible
        }
    }


    private fun init() {
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))

        //set Product image adapter
        binding.rvProductImg.adapter = productImageAdapter
        binding.rvProductImg.isFocusable = false

        //set Pack size adapter
        binding.rvPackSize.adapter = packSizeAdapter
        binding.rvPackSize.isFocusable = false

        //set Product image adapter
        binding.rvProductSpecification.adapter = productSpecificationAdapter
        binding.rvProductSpecification.isFocusable = false

        //set Product image adapter
        similarProductsAdapter = SimilarProductsAdapter(this, this, getRelatedProductList, this,loginModel.isActive)
        binding.rvSimilarProducts.adapter = similarProductsAdapter
        binding.rvSimilarProducts.isFocusable = false

        if (intent.extras != null) {
            productDetailsViewModel.productId = intent.getStringExtra(Constants.CATEGORY_ID_TAG)
            productDetailsViewModel.productDetails(productDetailsViewModel.productId)
        }

        //set setOnRefreshListener to swipe_refresh_layout
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        binding.lnrCart.setOnClickListener {
            //dashBoardActivity?.goToCart()
            if (loginModel.isActive == 1) {
                val intent = Intent(this, DashBoardActivity::class.java)
                intent.putExtra(FROM, PRODUCT_DETAILS)
                intent.putExtra(Constants.CATEGORY_ID_TAG, productDetailsViewModel.productId)
                startActivity(intent)
            }else{
                MethodClass.custom_msg_dialog(this, resources.getString(R.string.you_are_not_an_approved_user))?.show()
            }
        }
        binding.lnrCartSticky.setOnClickListener {
            binding.lnrCart.performClick()
        }

        binding.productLayout.rlNotification.setOnClickListener{
            binding.lnrCart.performClick()
        }
        binding.btnAddToCart.setOnClickListener {
            if (loginModel.isActive == 1) {
                qty = 1
                if (position == -1)
                    position = 0
                items = productDetailsModel?.variationList!![position!!]!!
                mainLayoutClick = true
                productDetailsViewModel.add_update_Product(
                    commonFunctions.getaddupdateJsondata(
                        this,
                        productDetailsModel?.productId,
                        productDetailsModel?.variationList!![position!!]!!,
                        1
                    )
                )
            } else {
                MethodClass.custom_msg_dialog(this, resources.getString(R.string.you_are_not_an_approved_user))?.show()
            }

        }
        binding.btnAddToCartSticky.setOnClickListener {
            binding.btnAddToCart.performClick()
        }

        binding.inclAddSubtractLayout.ivAdd.setOnClickListener {
            if (loginModel.isActive == 1) {
                if (position == -1)
                    position = 0
                qty = productDetailsModel?.variationList!![position!!]?.variationData_qty?.plus(1)
                items = productDetailsModel?.variationList!![position!!]!!
                mainLayoutClick = true
                productDetailsViewModel.add_update_Product(
                    commonFunctions.getaddupdateJsondata(
                        this,
                        productDetailsModel?.productId,
                        productDetailsModel?.variationList!![position!!]!!,
                        qty
                    )
                )
            } else{
                MethodClass.custom_msg_dialog(this, resources.getString(R.string.you_are_not_an_approved_user))?.show()
            }


        }
        binding.inclAddSubtractLayoutSticky.ivAdd.setOnClickListener {
            binding.inclAddSubtractLayout.ivAdd.performClick()
        }

        binding.inclAddSubtractLayout.ivRemove.setOnClickListener {
            if (loginModel.isActive == 1) {
                mainLayoutClick = true
                if (position == -1)
                    position = 0

                items = productDetailsModel?.variationList!![position!!]!!
                if (productDetailsModel?.variationList!![position!!]?.variationData_qty!! > 1) {
                    qty =
                        productDetailsModel?.variationList!![position!!]?.variationData_qty?.minus(1)


                    productDetailsViewModel.add_update_Product(
                        commonFunctions.getaddupdateJsondata(
                            this,
                            productDetailsModel?.productId,
                            productDetailsModel?.variationList!![position!!]!!,
                            qty
                        )
                    )
                } else {
                    qty = 0
                    productDetailsViewModel.remove_Product(
                        commonFunctions.getaddupdateJsondata(
                            this,
                            productDetailsModel?.productId,
                            productDetailsModel?.variationList!![position!!]!!,
                            0
                        )
                    )
                }
            } else {
                MethodClass.custom_msg_dialog(this, resources.getString(R.string.you_are_not_an_approved_user))?.show()
            }
        }
        binding.inclAddSubtractLayoutSticky.ivRemove.setOnClickListener {
            binding.inclAddSubtractLayout.ivRemove.performClick()
        }

        binding.txtVwMore.setOnClickListener{
            startActivity(Intent(this, DashBoardActivity::class.java).apply {
                putExtra(FROM, PRODUCT_DETAILS_VW_MORE)
                putExtra(Constants.PRODUCTID,productDetailsModel?.parentCatId)
//                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
//            finish()
        }

    }

    private fun observeProductDetailsData() {
        productDetailsViewModel.productDetailsLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    if (isShowLoader)
                        loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->
                        if (isShowLoader)
                            loader?.dismiss()
                        else
                            binding.swipeRefreshLayout.isRefreshing = false

                        if (data.response?.status?.actionStatus == true) {
                            val productDetailsData = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<ProductDetailsModel>() {}.type
                            productDetailsModel = gson.fromJson(productDetailsData, dataType)

                            //set data
                            productDetailsViewModel.productName.value =
                                productDetailsModel?.productName

                            productDetailsViewModel.subCatName.value =
                                productDetailsModel?.subCatName

                            productDetailsViewModel.brandName.value = productDetailsModel?.brandName

                            productDetailsViewModel.shortDesc.value = productDetailsModel?.shortDesc
                            productDetailsViewModel.longDesc.value = productDetailsModel?.longDesc

                            setDetailsData(productDetailsModel, 0)
                            //method for showing or hiding the out of stock text and discounted texts
                            showDiscountOutOfStock(productDetailsModel?.variationList, 0)


                            //set product image adapter
                            productImageAdapter.submitList(productDetailsModel?.imagesList)

                            //set product variation
                            //by default the 1st item will be selected
                            productDetailsModel?.variationList?.get(0)?.isSelected = true
                            val packSizeAdapter1: PackSizeAdapter =
                                binding.rvPackSize.adapter as PackSizeAdapter
                            packSizeAdapter1.variationList = productDetailsModel?.variationList
                            productDetailsModel?.variationList?.let {
                                packSizeAdapter1.notifyDataSetChanged()
                            }
                            binding.rvPackSize.isFocusable = false

                            //set product attributes
                            if (productDetailsModel?.productAttrList?.isNotEmpty() == true) {
                                productSpecificationAdapter.productAttrList =
                                    productDetailsModel?.productAttrList
                                productSpecificationAdapter.notifyDataSetChanged()
                                binding.rvProductSpecification.isVisible = true
                            } else
                                binding.rvProductSpecification.isVisible = false


                            similarProductsAdapter.rltdPrdctList =
                                productDetailsModel?.relatedProductList
                            similarProductsAdapter.notifyDataSetChanged()
                            binding.rvSimilarProducts.isFocusable = false

                            productDetailsViewModel.isRelatedLayoutVisible.value =
                                productDetailsModel?.relatedProductList?.isNotEmpty()


                            //set the 1st item image in the product details page
                            productDetailsModel?.imagesList?.let { it1 -> setProductImage(it1) }

                            //visible main layout
                            productDetailsViewModel.isMainLayoutVisible.value = true
                        }

                    }

                }
                is Response.Error -> {
                    //hide main layout
                    productDetailsViewModel.isMainLayoutVisible.value = true
                    if (isShowLoader)
                        loader?.dismiss()
                    else
                        binding.swipeRefreshLayout.isRefreshing = false

                    MethodClass.custom_msg_dialog_callback(
                        this, getString(R.string.product_not_found),
                        object : DialogCallback {
                            override fun dialog_clickstate() {

                               startActivity(
                                    Intent(this@ProductDetailsActivity, DashBoardActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                                )
                            }
                        },
                    )

                }
                else -> {}
            }
        }

        //For cart count
        productDetailsViewModel.cartCountLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
//                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->
//                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val cartCountData = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<CartCountModel>() {}.type
                            val cartCount: CartCountModel =
                                gson.fromJson(cartCountData, dataType)
                            Log.d(TAG, "observeProductDetailsData:prodCount "+cartCount.prodCount)
                            productDetailsViewModel.cartCount.value =
                                (cartCount.prodCount ?: 0).toString()
                            productDetailsViewModel.isCartCountVisible.value =
                                (cartCount.prodCount ?: 0) != 0

                        }

                    }

                }
                is Response.Error -> {
//                    loader?.dismiss()
                    commonFunctions.showErrorMsg(
                        binding.root.context,
                        it.errorCode,
                        it.errorMessage
                    )
                }
            }
        }


        productDetailsViewModel.addUpdateLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val jsonObject = JSONObject(data.response!!.data.toString())
                            if (qty == 0) {
                                items.presentInCart = Constants.NO
                                items.cartId = "0"
                            } else {
                                items.cartId = jsonObject.getString("id")

                            }

                            val cartCount = jsonObject.getInt("product_count")

                            productDetailsViewModel.cartCount.value =
                                (cartCount ?: 0).toString()
                            productDetailsViewModel.isCartCountVisible.value =
                                (cartCount ?: 0) != 0

//                            productDetailsViewModel.cartCount.value =
//                                jsonObject.getString("product_count")

                            items.variationData_qty = qty!!
                            if (mainLayoutClick)
                                setDetailsData(productDetailsModel, position!!)
                            else {
                                similarProductsAdapter.let { adapter ->
                                    if (!binding.rvSimilarProducts.isComputingLayout) {
                                        adapter.notifyItemChanged(position!!)
                                    }
                                }
                            }
                            setResult(Activity.RESULT_OK, intent)

                        } else {
                            MethodClass.custom_msg_dialog(this, data.response?.status?.msg)

                        }

                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    MethodClass.custom_msg_dialog(this, it.errorMessage)?.show()
                }
            }
        }

    }

    private fun setDetailsData(productDetailsModel: ProductDetailsModel?, pos: Int) {

        if (productDetailsModel?.variationList?.get(pos)?.variationData_qty!! > 0) {
            btnAddShow(true)
            binding.inclAddSubtractLayout.tvQuantity.text =
                productDetailsModel.variationList[pos]?.variationData_qty.toString()
            binding.inclAddSubtractLayoutSticky.tvQuantity.text =
                productDetailsModel.variationList[pos]?.variationData_qty.toString()
        } else if (productDetailsModel.variationList[pos]?.presentInCart.equals(
                Constants.YES,
                true
            )
        ) {
            btnAddShow(true)
            productDetailsModel.variationList[pos]?.variationData_qty =
                productDetailsModel.variationList[pos]?.cartQuantity!!
            binding.inclAddSubtractLayout.tvQuantity.text =
                productDetailsModel.variationList[pos]?.variationData_qty.toString()
            binding.inclAddSubtractLayoutSticky.tvQuantity.text =
                productDetailsModel.variationList[pos]?.variationData_qty.toString()
        } else {
            btnAddShow(false)
            productDetailsModel.variationList[pos]?.variationData_qty = 0
        }
    }

    private fun btnAddShow(bool: Boolean) {
        if (bool) {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnAddToCartSticky.visibility = View.GONE
            binding.inclAddSubtractLayout.qntLay.visibility = View.VISIBLE
            binding.inclAddSubtractLayoutSticky.qntLay.visibility = View.VISIBLE
        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
            binding.btnAddToCartSticky.visibility = View.VISIBLE
            binding.inclAddSubtractLayout.qntLay.visibility = View.GONE
            binding.inclAddSubtractLayoutSticky.qntLay.visibility = View.GONE
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {
        if (tag.equals(Constants.PRODUCT_IMAGE, true)) {
            val imageList: List<ImagesItem?> = arrayList as List<ImagesItem?>
            productDetailsViewModel.productImageLink.value = imageList[position]?.imageLink
        } else if (tag.equals(Constants.SUB_CATEGORY, true)) {
            val rltd_product: List<RelatedProductItem?> = arrayList as List<RelatedProductItem?>
            startActivity(Intent(this, ProductDetailsActivity::class.java).apply {
                this.putExtra(Constants.CATEGORY_ID_TAG, rltd_product[position]?.productId)
            })

        } else if (tag.equals(Constants.VARIATION, true)) {
            val variationList: List<VariationDataItem?> = arrayList as List<VariationDataItem?>
            showDiscountOutOfStock(variationList, position)
            this.position = position

            binding.inclAddSubtractLayout.tvQuantity.text =
                productDetailsModel?.variationList!![position]?.variationData_qty.toString()
            binding.inclAddSubtractLayoutSticky.tvQuantity.text =
                productDetailsModel?.variationList!![position]?.variationData_qty.toString()
//            if (productDetailsModel?.variationList!![position]?.variationData_qty!! > 0)
//                btn_add_show(true)
//            else
//                btn_add_show(false)
            setDetailsData(productDetailsModel, position)
            //set selected variation
            for (i in variationList.indices) {
                variationList[i]?.isSelected = false
            }
            variationList[position]?.isSelected = true

            val packSizeAdapter: PackSizeAdapter = binding.rvPackSize.adapter as PackSizeAdapter
            packSizeAdapter.variationList = variationList
            packSizeAdapter.notifyDataSetChanged()
            Log.e("onAdapterItemClick: ", packSizeAdapter.variationList.toString())

        }
    }

    private fun setProductImage(imageList: List<ImagesItem?>) {
        if (imageList.isNotEmpty()) {
            imageList.forEach { imageItem ->
                if (imageItem?.isDefault.equals(Constants.YES, true)) {
                    productDetailsViewModel.productImageLink.value = imageItem?.imageLink
                    imageItem?.imageLink?.let { Log.e("setProductImage:", it) }
                    return@forEach
                }
            }
        }
    }

    private fun showDiscountOutOfStock(variationList: List<VariationDataItem?>?, position: Int) {
        if (variationList?.isNotEmpty() == true) {
            if ((variationList[position]?.quantity ?: 0) > 0) {

                productDetailsViewModel.isOutOfStock.value = false

                productDetailsViewModel.isDiscounted.value =
                    variationList[position]?.isDiscounted.equals(
                        Constants.YES,
                        true
                    )
            } else
                productDetailsViewModel.isOutOfStock.value = true

            //set offer price and sell price and discount percentage
            productDetailsViewModel.offPercentage.value =
                "${variationList[position]?.discountPercentage} ${resources.getString(R.string.off)}"

            if (variationList[position]?.isDiscounted.equals(Constants.YES, true))
                productDetailsViewModel.productOfferPrice.value =
                    variationList[position]?.discountPrice
            else
                productDetailsViewModel.productOfferPrice.value =
                    variationList[position]?.sellPrice

            productDetailsViewModel.productSellPrice.value =
                variationList[position]?.sellPrice

        }
    }

    override fun onRefresh() {
        isShowLoader = false
        //call product details api
        productDetailsViewModel.productDetails(productDetailsViewModel.productId)
        getRelatedProductList = ArrayList()
    }

    fun observeRecyclerviewData(item: RelatedProductItem?) {
        item?.change_data?.observe(this) { _ ->
            similarProductsAdapter.let {
                if (!binding.rvSimilarProducts.isComputingLayout) {
                    it.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onAdditem(item: VariationDataItem?, productID: String?, position: Int?) {

        mainLayoutClick = false
        this.position = position
        this.qty = 1
        if (item != null) {
            items = item
        }
        productDetailsViewModel.add_update_Product(
            commonFunctions.getaddupdateJsondata(
                this,
                productID, item!!, 1
            )
        )
    }


    override fun onUpdateitem(
        item: VariationDataItem?,
        productID: String?,
        position: Int?,
        qty: Int?
    ) {
        mainLayoutClick = false
        this.position = position
        this.qty = qty
        println("quantity....$qty......")
        if (item != null) {
            items = item
        }
        productDetailsViewModel.add_update_Product(
            commonFunctions.getaddupdateJsondata(this, productID, item!!, qty)
        )
    }

    override fun onRemoveitem(item: VariationDataItem?, position: Int?) {
        mainLayoutClick = false
        this.position = position
        this.qty = 0
        println("quantity....$qty......")
        if (item != null) {
            items = item
        }
        productDetailsViewModel.remove_Product(commonFunctions.getremoveJsondata(item!!))
    }


}