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
import com.salonsolution.app.data.model.ProductList
import com.salonsolution.app.databinding.ProductListItemBinding
import com.salonsolution.app.interfaces.ProductListClickListener
import com.salonsolution.app.utils.AvatarGenerator

class ProductListPagingAdapter( val listener: ProductListClickListener) :
    PagingDataAdapter<ProductList, ProductListPagingAdapter.ProductListViewHolder>(
        ProductListDiffUtil()
    ) {

    inner class ProductListViewHolder(val itemBinding: ProductListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding: ProductListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.product_list_item,
            parent,
            false
        )
        return ProductListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {

        val item = getItem(position)
        item?.let { list ->
            with(holder.itemBinding){
                val imgAvatar = AvatarGenerator.avatarImage(
                    context = root.context,
                    width = 500,
                    height = 500,
                    avatarTextSizeRatio = 0.33f,
                    name = list.productName,
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
                tvTitle.text = list.productName
                tvPrice.text = list.price
                tvDetails.text = list.description
                if(item.inCart==1)
                {
                    //Already in cart
                    btAdd.visibility = View.GONE
                    btGoToCart.visibility = View.VISIBLE


                }else{
                    //not added in cart
                    btAdd.visibility = View.VISIBLE
                    btGoToCart.visibility = View.GONE
                }

                btAdd.setOnClickListener {
                    listener.onAddClick(holder.absoluteAdapterPosition,list)
                }
                btGoToCart.setOnClickListener {
                    listener.goToCartClick(holder.absoluteAdapterPosition,list)
                }

                root.setOnClickListener {
                    listener.onItemClick(holder.absoluteAdapterPosition,list)
                }
            }
        }

    }


    class ProductListDiffUtil : DiffUtil.ItemCallback<ProductList>() {
        override fun areItemsTheSame(oldItem: ProductList, newItem: ProductList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductList, newItem: ProductList): Boolean {
            return oldItem == newItem
        }

    }

}