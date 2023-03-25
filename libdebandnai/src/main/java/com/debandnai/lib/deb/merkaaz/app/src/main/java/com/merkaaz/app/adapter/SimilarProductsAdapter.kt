package com.merkaaz.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.R
import com.merkaaz.app.data.model.FeaturedData
import com.merkaaz.app.data.model.RelatedProductItem
import com.merkaaz.app.databinding.SimilarProductsItemBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.ui.activity.ProductDetailsActivity
import com.merkaaz.app.ui.dialogs.ProductQtyBottomSheet
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass

class SimilarProductsAdapter(
    val listener: AdapterItemClickListener,
    val addRemoveListner: ProductAddRemoveListner,
    var rltdPrdctList: ArrayList<RelatedProductItem?>?,
    private val act: Activity?,
    private val isActiveUser: Int?
) :
    RecyclerView.Adapter<SimilarProductsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = SimilarProductsItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (rltdPrdctList?.get(position)?.selectedVariationDataItem?.id == null)
            rltdPrdctList?.get(position)?.selectedVariationDataItem = rltdPrdctList?.get(position)?.variationDataList?.get(0)

        rltdPrdctList?.get(position)?.let { holder.bind(it) }

        holder.binding.lnrRltdProduct.setOnClickListener {
            listener.onAdapterItemClick(rltdPrdctList,position,it,Constants.SUB_CATEGORY)
        }


        holder.binding.tvQuantity.text =
            (rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty?:0).toString()
        if (rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty!! > 0) {
            holder.binding.btnAdd.visibility = View.GONE
            holder.binding.qntLay.visibility = View.VISIBLE
            holder.binding.tvQuantity.text = rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty.toString()
        }else if (rltdPrdctList?.get(position)?.selectedVariationDataItem?.presentInCart.equals(Constants.YES,true)){
            holder.binding.btnAdd.visibility = View.GONE
            holder.binding.qntLay.visibility = View.VISIBLE
            rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty = rltdPrdctList?.get(position)?.selectedVariationDataItem?.cartQuantity!!.toInt()
            holder.binding.tvQuantity.text = rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty.toString()
        } else {
            holder.binding.btnAdd.visibility = View.VISIBLE
            holder.binding.qntLay.visibility = View.GONE
            rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty = 0
        }
        holder.binding.tvSize.setOnClickListener {
            act?.let {
                val featuredData = FeaturedData()
                featuredData.product_name = rltdPrdctList?.get(position)?.productName
                featuredData.brand_name = rltdPrdctList?.get(position)?.subCategoryName
                featuredData.productId = rltdPrdctList?.get(position)?.productId
                featuredData.image = rltdPrdctList?.get(position)?.image
                featuredData.variationDataList = rltdPrdctList?.get(position)!!.variationDataList
                val modalBottomSheet = ProductQtyBottomSheet(featuredData,rltdPrdctList?.get(position))
                modalBottomSheet.show((act as AppCompatActivity).supportFragmentManager, "")
            }

        }
        holder.binding.btnAdd.setOnClickListener{
            if (isActiveUser==1) {
                addRemoveListner.onAdditem(
                    rltdPrdctList?.get(position)?.selectedVariationDataItem,
                    rltdPrdctList?.get(position)?.productId, position
                )
            }
            else {
                act?.let {
                    MethodClass.custom_msg_dialog(it, act.resources.getString(R.string.you_are_not_an_approved_user))?.show()
                }
            }
        }
        holder.binding.ivAdd.setOnClickListener{
            addRemoveListner.onUpdateitem(rltdPrdctList?.get(position)?.selectedVariationDataItem,
                rltdPrdctList?.get(position)?.productId,position,
                (rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty)?.plus(1 )
            )
        }
        holder.binding.ivRemove.setOnClickListener{

            if ((rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty)!! >1) {
                addRemoveListner.onUpdateitem(
                    rltdPrdctList?.get(position)?.selectedVariationDataItem,
                    rltdPrdctList?.get(position)?.productId, position,
                    (rltdPrdctList?.get(position)?.selectedVariationDataItem?.variationData_qty)?.minus(1)
                )
            }else{
                    addRemoveListner.onRemoveitem(rltdPrdctList?.get(position)?.selectedVariationDataItem,position)
            }
        }
        (act as ProductDetailsActivity).observeRecyclerviewData(rltdPrdctList?.get(position))
    }

    class ViewHolder(val binding: SimilarProductsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RelatedProductItem) {
            binding.dataItem = item
            binding.executePendingBindings()
        }

    }

    override fun getItemCount() = rltdPrdctList?.size ?: 0

}