package com.merkaaz.app.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.ShopByCategoryList
import com.merkaaz.app.databinding.ShopByCategoryItemsLayoutBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants


class ShopByCategoryCartListAdapter(
    private val listener: AdapterItemClickListener
) : PagingDataAdapter<ShopByCategoryList, ShopByCategoryCartListAdapter.ViewHolder>(Comparator()){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = ShopByCategoryItemsLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),position)
        }


    inner class ViewHolder(val binding: ShopByCategoryItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")

        fun bind(listItem: ShopByCategoryList?, position: Int) = with(itemView) {
            binding.shopByCategoryList=listItem
            binding.clItemLayout.setOnClickListener{
                listener.onAdapterItemClick(snapshot(),position,binding.tvProductName, Constants.SHOP_BY_CATEGORY)
            }
            binding.executePendingBindings()
        }

    }



    class Comparator : DiffUtil.ItemCallback<ShopByCategoryList>() {
        override fun areItemsTheSame(oldItem: ShopByCategoryList, newItem: ShopByCategoryList): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: ShopByCategoryList, newItem: ShopByCategoryList): Boolean {
            return oldItem == newItem
        }

    }
}