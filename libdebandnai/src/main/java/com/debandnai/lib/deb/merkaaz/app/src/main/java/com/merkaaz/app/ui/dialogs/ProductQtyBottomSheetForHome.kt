package com.merkaaz.app.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.merkaaz.app.R
import com.merkaaz.app.adapter.BottomSheetChoosePackSizeAdapter
import com.merkaaz.app.data.ProductBottomSheetViewModel
import com.merkaaz.app.data.model.FeaturedData
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.data.model.VariationDataItem
import com.merkaaz.app.databinding.ProductQtyPopupBinding
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.fragments.HomeFragment
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class ProductQtyBottomSheetForHome(
    private val featureData: FeaturedData,
    private val homeFragment: HomeFragment
) : BottomSheetDialogFragment(),
    ProductAddRemoveListner {
    lateinit var binding: ProductQtyPopupBinding
    private lateinit var bottomSheetAdapter: BottomSheetChoosePackSizeAdapter
    private var loader: Dialog? = null
    private var position : Int? =-1
    private var qty : Int? =-1
    private lateinit var productBottomSheetViewModel: ProductBottomSheetViewModel
    private lateinit var loginModel: LoginModel

    @Inject
    lateinit var sharedPreff: SharedPreff

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.product_qty_popup, container, false)
        productBottomSheetViewModel =
            ViewModelProvider(this)[ProductBottomSheetViewModel::class.java]
        binding.viewmodel = productBottomSheetViewModel
        binding.lifecycleOwner = this

        initialise()
        updateObserveAdapter()
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.SheetDialog
    }

    private fun updateObserveAdapter() {
        context?.let {
            bottomSheetAdapter =
                BottomSheetChoosePackSizeAdapter(it, featureData, this, loginModel.isActive)
            binding.rvPackSize.adapter = bottomSheetAdapter
        }

        productBottomSheetViewModel.addUpdateLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            homeFragment.homeViewModel?.dashBoardAllProductList()
                            if (qty == 0){
                                featureData.variationDataList[position!!].presentInCart = Constants.NO
                                featureData.variationDataList[position!!].cartId = "0"
                            }else{
                                val jsonobj  = JSONObject(data.response!!.data.toString())
                                val cart_id = jsonobj.getString("id")
                                featureData.variationDataList[position!!].cartId = cart_id
                            }

                            featureData.variationDataList[position!!].variationData_qty = qty!!
                            featureData.variationData = featureData.variationDataList[position!!]
                            featureData.change_data.value = featureData.variationDataList[position!!]


                            bottomSheetAdapter.let { adapter ->
                                if (!binding.rvPackSize.isComputingLayout){
                                    adapter.notifyItemChanged(position!!)
                                }
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

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {
//        list=item.variationData
        activity?.let {
            loader = MethodClass.custom_loader(it, getString(R.string.please_wait))
            loginModel = sharedPreff.get_logindata()
        }

        binding.tvProductName.text = featureData.product_name
        binding.tvCompanyName.text = featureData.brand_name
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.ivProductImg.setImageURI(featureData.image)

        //add setOn touch listener
        binding.rvPackSize.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            v.onTouchEvent(event)
            true
        })

    }

    override fun onAdditem(item: VariationDataItem?, productID: String?, position: Int?) {
        context?.let {
            this.position = position
            this.qty =  1
            productBottomSheetViewModel.add_update_Product(
                commonFunctions.getaddupdateJsondata(
                    it, featureData.productId, featureData.variationDataList[position!!], 1
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
            productBottomSheetViewModel.add_update_Product(
                commonFunctions.getaddupdateJsondata(
                    it, featureData.productId, featureData.variationDataList[position!!], qty
                )
            )
        }
    }

    override fun onRemoveitem(item: VariationDataItem?, position: Int?) {
        context?.let {
            this.position = position
            this.qty = 0
            productBottomSheetViewModel.remove_Product(
                commonFunctions.getremoveJsondata(featureData.variationDataList[position!!])
            )
        }
    }

}