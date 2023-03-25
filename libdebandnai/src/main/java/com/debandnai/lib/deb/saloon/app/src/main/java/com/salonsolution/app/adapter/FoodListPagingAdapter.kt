package com.salonsolution.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.FoodList
import com.salonsolution.app.databinding.FoodListItemBinding
import com.salonsolution.app.interfaces.FoodListClickListener
import com.salonsolution.app.utils.AvatarGenerator

class FoodListPagingAdapter( val listener: FoodListClickListener) :
    PagingDataAdapter<FoodList, FoodListPagingAdapter.FoodListViewHolder>(FoodListDiffUtil()) {

    inner class FoodListViewHolder(val itemBinding: FoodListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        val binding: FoodListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.food_list_item,
            parent,
            false
        )
        return FoodListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) {

        val item = getItem(position)
        item?.let { list ->
            with(holder.itemBinding){
                val imgAvatar = AvatarGenerator.avatarImage(
                    context = root.context,
                    width = 500,
                    height = 500,
                    avatarTextSizeRatio = 0.33f,
                    name = list.foodName,
                    avatarTextStyle = Typeface.NORMAL,
                    backgroundColor = ContextCompat.getColor(
                        root.context,
                        R.color.avatar_background_color
                    ),
                    avatarTextColor = ContextCompat.getColor(
                        root.context,
                        R.color.avatar_text_color
                    )
                )
                Glide.with(root.context)
                    .load(item.image)
                    .placeholder(imgAvatar)
                    .centerInside()
                    .into(ivService)
                tvTitle.text = list.foodName
                tvPrice.text = list.price
                tvDesc.text = list.description
                tvQuantity.text = list.qty.toString()

                if(item.inCart==1 || item.qty==0){
                    cartQuantity.visibility =View.VISIBLE
                    btAdd.visibility = View.GONE
                }else{
                    cartQuantity.visibility =View.GONE
                    btAdd.visibility = View.VISIBLE
                }

                btAdd.setOnClickListener {
                    listener.onAddClick(holder.absoluteAdapterPosition,list)
                }

                ivAdd.setOnClickListener {
                    listener.onUpdateClick(holder.absoluteAdapterPosition,list)
                }
                ivRemove.setOnClickListener {
                    listener.onRemovedClick(holder.absoluteAdapterPosition,list)
                }

                root.setOnClickListener {
                    listener.onItemClick(holder.absoluteAdapterPosition,list)
                }
            }
        }

    }


    class FoodListDiffUtil : DiffUtil.ItemCallback<FoodList>() {
        override fun areItemsTheSame(oldItem: FoodList, newItem: FoodList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodList, newItem: FoodList): Boolean {
            return oldItem == newItem
        }

    }

}