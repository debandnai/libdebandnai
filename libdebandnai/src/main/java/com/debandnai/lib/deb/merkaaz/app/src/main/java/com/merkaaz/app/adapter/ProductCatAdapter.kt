package com.merkaaz.app.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.SubCategory
import com.merkaaz.app.databinding.ProductCatRowBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants

val TAG = "testingg"

class ProductCatAdapter(
    val listener: AdapterItemClickListener,
    var subCategoryList: ArrayList<SubCategory>
    
) :
    RecyclerView.Adapter<ProductCatAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = ProductCatRowBinding.inflate(inflater, parent, false)
        return ItemViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ProductCatAdapter.ItemViewHolder, position: Int) {
        holder.bind(subCategoryList[position],position)
    }

   inner class ItemViewHolder(val binding: ProductCatRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubCategory, position: Int) = with(itemView) {
            Log.d(TAG, "bind: ssss "+subCategoryList.size)
            //item.isSelected = item.isSelectedPosition==position
            binding.subCategory = item
            println("status check..${item.isSelected}")
            binding.executePendingBindings()
            setOnClickListener {
                listener.onAdapterItemClick(subCategoryList, position, binding.tvProdCat, Constants.SUB_CATEGORY)
            }
        }
    }

    override fun getItemCount()=subCategoryList.size
}









