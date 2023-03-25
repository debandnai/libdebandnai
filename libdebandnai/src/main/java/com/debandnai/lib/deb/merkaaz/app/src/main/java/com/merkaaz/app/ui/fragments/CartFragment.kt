package com.merkaaz.app.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.CartAdapter
import com.merkaaz.app.data.model.*
import com.merkaaz.app.data.viewModel.CartViewModel
import com.merkaaz.app.data.viewModel.DashboardViewModel
import com.merkaaz.app.databinding.CustomDialogWithTwoButtonsBinding
import com.merkaaz.app.databinding.FragmentMyCartBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.ui.activity.DeliveryOptionsActivity
import com.merkaaz.app.ui.activity.ProductDetailsActivity
import com.merkaaz.app.ui.base.BaseFragment
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment(), ProductAddRemoveListner, AdapterItemClickListener {
    //get Login Model
    private var cartAdapter: CartAdapter? = null
    private lateinit var binding: FragmentMyCartBinding
    private lateinit var cartViewModel: CartViewModel
    private var loader: Dialog? = null
    var intent=Intent()
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_cart, container, false)
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        binding.viewModel = cartViewModel
        binding.lifecycleOwner = this

        initialise()
        observeData()
        viewOnClick()
        return binding.root
    }

    private fun viewOnClick() {
        binding.btnReset.setOnClickListener {
            startActivity(
                Intent(binding.btnReset.context, DashBoardActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        binding.btnCheckout.setOnClickListener {
            if (loginModel.isActive == 1) {
                //call cart product available check api
                cartViewModel.cartProductAvailabilityCheck()

            } else {
                activity?.let {
                    MethodClass.custom_msg_dialog(
                        it,
                        resources.getString(R.string.you_are_not_an_approved_user)
                    )?.show()
                }
            }
        }


    }


    override fun onResume() {
        super.onResume()
        dashboardViewModel.isShowToolbar.value = true
        dashboardViewModel.isShowSearchbar.value = false
        dashboardViewModel.isShowBottomNavigation.value = false
        dashboardViewModel.isSetCollapsingToolbar.value = false
        dashboardViewModel.isShowToolbarOptionsWithLogo.value = false
        dashboardViewModel.headerText.value = getString(R.string.review_basket)
        dashboardViewModel.pendingapprovalStat.value = true
        dashboardViewModel.isShowHelpLogo.value = true
        //call cart list api
        cartViewModel.getCartList()
        dashboardViewModel.getCartCount()

    }

    private fun initialise() {
        cartAdapter = CartAdapter(this, this, ArrayList())

        loginModel = sharedPreff.get_logindata()

        //initialize loader
        binding.root.context?.let {
            loader = MethodClass.custom_loader(it, getString(R.string.please_wait))
        }

        binding.rvCart.adapter = cartAdapter
        binding.rvCart.isFocusable = false

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    cartViewModel.getCartList()
                }
            }

        if (loginModel.isActive == 1)
            binding.btnCheckout.setBackgroundResource(R.drawable.teal_button_bg)
        else
            binding.btnCheckout.setBackgroundResource(R.drawable.botton_shape_gray_color)
    }

    private fun observeData() {
        //for cart  list
        cartViewModel.cartListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                    if (loader?.isShowing == false)
                        loader?.show()
                }
                is Response.Success -> {
                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            val cartListResponse = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<CartListModel>() {}.type
                            val cartListModel: CartListModel? =
                                gson.fromJson(cartListResponse, dataType)

                            cartViewModel.cartTotal.value = cartListModel?.cartTotal
                            cartViewModel.minOrderAmount.value = cartListModel?.minimumOrderAmount


                            cartAdapter?.cartList = cartListModel?.categoryList
                            cartAdapter?.notifyDataSetChanged()


                            cartViewModel.isShowNoDataFound.value =
                                cartListModel?.categoryList?.isEmpty()
                            cartViewModel.isShowMainLayout.value =
                                cartListModel?.categoryList?.isNotEmpty()

                            //check min order to show or hide the min order layout
                            checkMinOrderLayoutShowing(
                                cartViewModel.cartTotal.value,
                                cartViewModel.minOrderAmount.value
                            )

                            if (cartViewModel.isShowMinOrder.value == true) {
                                cartViewModel.isEnableCheckout.value = false
                            } else {
                                //checkout button enable disable logic if any product available quantity is less or more than cart quantity
                                cartListModel?.categoryList?.forEach outer@{ categoryListItem ->
                                    categoryListItem?.productList?.let { productList ->
                                        productList.forEach { productListItem ->
                                            //set Selected variation item from each variationList's first item (by default)
                                            if (productListItem?.variationList?.isNotEmpty() == true){
                                                productListItem.selectedVariationDataItem =
                                                    productListItem.variationList.get(0)
                                            }


                                            cartViewModel.isEnableCheckout.value =
                                                (productListItem?.selectedVariationDataItem?.quantity
                                                    ?: 0) >= (productListItem?.cartQuantity ?: 0)

                                            if (cartViewModel.isEnableCheckout.value == false)
                                                return@outer
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
                else -> {}
            }

        }


        //for add update product
        cartViewModel.addUpdateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        if (data.response?.status?.actionStatus == true) {
                            cartViewModel.getCartList()

                        } else {
                            context?.let { ctx ->
                                //MethodClass.custom_msg_dialog(ctx,data.response?.status?.msg)
                                MethodClass.custom_msg_dialog(ctx, ctx.getString(R.string.stock_not_available))?.show()
                            }
                        }
                    }
                }
                is Response.Error -> {
                    loader?.dismiss()
                    context?.let { ctx ->
                       // MethodClass.custom_msg_dialog(ctx, it.errorMessage)?.show()
                        MethodClass.custom_msg_dialog(ctx, ctx.getString(R.string.stock_not_available))?.show()
                    }
                }
            }
        }

        cartViewModel.cartProductAvailabilityCheckLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            val cartProductAvailabilityCheckResponse = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<CartProductAvailabilityCheckModel>() {}.type
                            val cartProductAvailabilityCheckModel: CartProductAvailabilityCheckModel? =
                                gson.fromJson(cartProductAvailabilityCheckResponse, dataType)

                            if (cartProductAvailabilityCheckModel?.placeStatus == 4)
                                binding.btnCheckout.context.startActivity(
                                    Intent(
                                        binding.btnCheckout.context,
                                        DeliveryOptionsActivity::class.java
                                    )
                                )
                            else {

                                    other_status_cart(cartProductAvailabilityCheckModel)
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


        cartViewModel.moveOnPriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {
                    if (!loader?.isShowing!!)
                        loader?.show()
                }
                is Response.Success -> {
//                    loader?.dismiss()
                    it.data?.let { data ->
                        if (data.response?.status?.actionStatus == true) {
                            context?.let { startActivity(Intent(it,DeliveryOptionsActivity::class.java)) }
                         }
                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
                else -> {}
            }

        }


    }

    private fun other_status_cart(chk_prod_stat: CartProductAvailabilityCheckModel?) {
        binding.root.context?.let {ctx->

            when (chk_prod_stat?.placeStatus) {
                1 -> {
                    val msg = chk_prod_stat?.unavailableProducts+resources.getString(R.string.products_unavialable)
                    MethodClass.custom_msg_dialog_callback(ctx,msg,object : DialogCallback {
                        override fun dialog_clickstate() {
                            cartViewModel.getCartList()
                        }
                    })
                }
                2 -> {
                        show_dialog_for_payment_increase(chk_prod_stat)
                }
                3 -> {
                    val msg = resources.getString(R.string.min_order_msg)+chk_prod_stat?.minOrderValue+resources.getString(R.string.add_more_msg)
                    MethodClass.custom_msg_dialog_callback(ctx,msg,object : DialogCallback{
                        override fun dialog_clickstate() {
                            cartViewModel.getCartList()
                        }
                    })
                }
                else -> {
                    cartViewModel.getCartList()
                }
            }
        }

    }

    private fun show_dialog_for_payment_increase(prod_stat: CartProductAvailabilityCheckModel) {
        var dialog: Dialog? = null
        context?.let { ctx ->
            dialog = Dialog(ctx)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: CustomDialogWithTwoButtonsBinding = DataBindingUtil.inflate(
                LayoutInflater.from(ctx),
                R.layout.custom_dialog_with_two_buttons,
                null,
                false
            )
            dialog?.setContentView(binding.root)

            var msg =resources.getString(R.string.cart_amount_chng_msg)+" "+prod_stat?.oldCartTotal+" "+resources.getString(R.string.to)+" "+
            prod_stat?.newCartTotal+" "+resources.getString(R.string.proceed_msg)

            binding.tvTitle.text = resources.getString(R.string.amount_changed)

            binding.tvMsg.text = msg
            binding.btnYes.text = resources.getString(R.string.proceed)
            binding.btnNo.text = resources.getString(R.string.check_cart)

            binding.btnYes.setOnClickListener {
                cartViewModel.getMoveonPrice(2)
              //  startActivity(Intent(ctx,DeliveryOptionsActivity::class.java))
               dialog?.dismiss()
            }
            binding.btnNo.setOnClickListener {
                dialog?.dismiss()
                cartViewModel.getCartList()
            }
            dialog?.show()
        }
    }

    override fun onAdditem(item: VariationDataItem?, productID: String?, position: Int?) {
        if (loginModel.isActive != 1) {
            activity?.let {
                MethodClass.custom_msg_dialog(
                    it,
                    resources.getString(R.string.you_are_not_an_approved_user)
                )?.show()
            }
        }
    }

    override fun onUpdateitem(
        item: VariationDataItem?,
        productID: String?,
        position: Int?,
        qty: Int?
    ) {
        activity?.let { activity ->
            if (loginModel.isActive == 1) {
                cartViewModel.addUpdateProduct(
                    commonFunctions.getaddupdateJsondata(
                        activity,
                        productID, item!!, qty
                    )
                )

                //intent.putExtra(Constants.FROM,"cart_page")
                activity.setResult(Activity.RESULT_OK,intent)
            } else {
             //   activity?.let {
                    MethodClass.custom_msg_dialog(
                        activity,
                        resources.getString(R.string.you_are_not_an_approved_user)
                    )?.show()
               // }

            }
        }

    }

    override fun onRemoveitem(item: VariationDataItem?, position: Int?) {
        if (loginModel.isActive == 1) {
            activity?.let {
                cartViewModel.removeProduct(commonFunctions.getremoveJsondata(item!!))
                it.setResult(Activity.RESULT_OK)
            }
        } else {
            activity?.let {
                MethodClass.custom_msg_dialog(
                    it,
                    resources.getString(R.string.you_are_not_an_approved_user)
                )?.show()
            }
        }
    }

    private fun checkMinOrderLayoutShowing(totalAmountStr: String?, minOrderAmountStr: String?) {
        totalAmountStr?.let {
            minOrderAmountStr?.let {
                val getTotalAmount = totalAmountStr.replace("AOA", "").replace(",", "").trim()
                val getMinOrderAmount = minOrderAmountStr.replace("AOA", "").replace(",", "").trim()
                if (getTotalAmount.isNotBlank() && getMinOrderAmount.isNotBlank()) {
                    cartViewModel.isShowMinOrder.value =
                        getTotalAmount.toDouble() < getMinOrderAmount.toDouble()

                    Log.e(
                        "checkMinOrderLayoutShowing: ",
                        cartViewModel.isShowMinOrder.value.toString()
                    )

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
        if (tag.equals(Constants.CART_ADAPTER_ITEM_CLICK, true)) {

            val prodList: List<ProductListItem> = arrayList as List<ProductListItem>
            Log.d("CART", "onAdapterItemClick: position $position productId "+prodList[position].productId)
            resultLauncher?.launch(
                Intent(binding.root.context, ProductDetailsActivity::class.java).apply {
                    this.putExtra(Constants.CATEGORY_ID_TAG, prodList[position].productId)
                }
            )
        }
    }
}