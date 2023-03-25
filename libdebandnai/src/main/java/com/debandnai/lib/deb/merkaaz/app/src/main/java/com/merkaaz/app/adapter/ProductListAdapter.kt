package com.merkaaz.app.adapter


import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.R
import com.merkaaz.app.data.model.FeaturedData
import com.merkaaz.app.databinding.ProductItemsLayoutBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.ui.activity.dashBoardActivity
import com.merkaaz.app.ui.dialogs.ProductQtyBottomSheet
import com.merkaaz.app.ui.fragments.ProductCategoryFragment
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass


class ProductListAdapter(
    private val listener: AdapterItemClickListener,
    private val addRemoveListener: ProductAddRemoveListner,
    private val activity: Activity?,
    private val fragment: ProductCategoryFragment,
    private val isActive: Int?
) : PagingDataAdapter<FeaturedData, ProductListAdapter.ViewHolder>(Comparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = ProductItemsLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    inner class ViewHolder(val binding: ProductItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
            fun bind(listItem: FeaturedData?, position: Int) = with(itemView) {
            binding.featuredData = listItem
            if (listItem!!.variationData.id == null) {
                listItem.variationData = listItem.variationDataList[0]
            }

            binding.btnAdd.visibility = View.GONE
            binding.qntLay.visibility = View.VISIBLE
                if (listItem.variationData.total_qty != null) {
                    dashBoardActivity?.showHideNotificationBatch(listItem.variationData.total_qty!!)
                    listItem.variationData.total_qty=null
                }


            Log.d(TAG, "bind: qty total_qty "+listItem.variationData.total_qty)

            if (listItem.variationData.isDiscounted.equals(Constants.YES, ignoreCase = true)) {
                binding.llOffer.visibility = View.VISIBLE
                binding.tvOffer.text = listItem.variationData.discountPercentage + " \n"+binding.tvOffer.context.getString(R.string.off)
            } else {
                binding.llOffer.visibility = View.GONE
            }
            if (listItem.variationData.variationData_qty > 0) {
                binding.btnAdd.visibility = View.GONE
                binding.qntLay.visibility = View.VISIBLE
                binding.tvOutOfStock.visibility = View.GONE
                binding.tvQuantity.text = listItem.variationData.variationData_qty.toString()
            }else if (listItem.variationData.presentInCart.equals(Constants.YES,true)){
                binding.btnAdd.visibility = View.GONE
                binding.qntLay.visibility = View.VISIBLE
                binding.tvOutOfStock.visibility = View.GONE
                listItem.variationData.variationData_qty = listItem.variationData.cartQuantity!!.toInt()
                binding.tvQuantity.text = listItem.variationData.variationData_qty.toString()
            }else if(listItem.variationData.quantity == 0){
                binding.btnAdd.visibility = View.VISIBLE
                binding.qntLay.visibility = View.GONE
                binding.tvOutOfStock.visibility = View.VISIBLE
                binding.btnAdd.setBackgroundResource(R.drawable.button_ash)
                binding.btnAdd.isEnabled = false
            } else {
                binding.btnAdd.visibility = View.VISIBLE
                binding.btnAdd.isEnabled = true
                binding.btnAdd.setBackgroundResource(R.drawable.view_btn_bg)
                binding.qntLay.visibility = View.GONE
                binding.tvOutOfStock.visibility = View.GONE
                listItem.variationData.variationData_qty = 0
            }
            binding.tvUnit.setOnClickListener {
                    val modalBottomSheet = ProductQtyBottomSheet(
                        listItem,
                        null
                    )
                    modalBottomSheet.show((activity as AppCompatActivity).supportFragmentManager, "")


            }

            binding.btnAdd.setOnClickListener {
                Log.d(TAG, "bind: btnadd data $isActive")
                if (isActive==1){
                    val totalQty : Int? =  listItem.variationData.quantity
                    if (totalQty != null) {

                        if (listItem.variationData.variationData_qty <= totalQty) {
                            addRemoveListener.onAdditem(listItem.variationData,listItem.productId,position)
                        } else {
                            val msg = binding.root.context.resources.getString(R.string.only)+" " + totalQty + binding.root.context.resources.getString(R.string.left_stock)
                            MethodClass.custom_msg_dialog(binding.root.context, msg)
                        }
                    }
                }
                else{
                    activity?.let {
                        MethodClass.custom_msg_dialog(it, resources.getString(R.string.you_are_not_an_approved_user))?.show()
                    }
                }

            }
            binding.ivAdd.setOnClickListener {
                if (isActive==1){
                    val totalQty : Int? =  listItem.variationData.quantity
                    if (totalQty != null)
                        if (listItem.variationData.variationData_qty < totalQty) {
                            addRemoveListener.onUpdateitem(listItem.variationData,listItem.productId,position,(listItem.variationData.variationData_qty)+1)
                        }else{
                            val msg = binding.root.context.resources.getString(R.string.only)+" "+totalQty+binding.root.context.resources.getString(R.string.left_stock)
                            MethodClass.custom_msg_dialog(binding.root.context,msg)?.show()
                        }
                }
                else{
                    activity?.let {
                        MethodClass.custom_msg_dialog(it, resources.getString(R.string.you_are_not_an_approved_user))?.show()
                    }
                }

            }
            binding.ivRemove.setOnClickListener {
                if (isActive == 1){
                    if (listItem.variationData.variationData_qty >1) {
                        addRemoveListener.onUpdateitem(listItem.variationData,listItem.productId,position,(listItem.variationData.variationData_qty) -1)
                    }else
                        addRemoveListener.onRemoveitem(listItem.variationData,position)
                    }
                else{
                    activity?.let {
                        MethodClass.custom_msg_dialog(it, resources.getString(R.string.you_are_not_an_approved_user))?.show()
                    }
                }
            }

            fragment.observeRecyclerviewData(listItem, binding, position)
            binding.clItemLayout.setOnClickListener {
                listener.onAdapterItemClick(snapshot().items,position,it, Constants.PAGING_ADAPTER_ITEM_CLICK)
            }
            binding.executePendingBindings()

        }
    }

    class Comparator : DiffUtil.ItemCallback<FeaturedData>() {
        override fun areItemsTheSame(oldItem: FeaturedData, newItem: FeaturedData): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: FeaturedData, newItem: FeaturedData): Boolean {
            return oldItem == newItem
        }

    }
}