package com.salonsolution.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CartFoodList
import com.salonsolution.app.data.model.CartProductList
import com.salonsolution.app.data.model.CartServiceList
import com.salonsolution.app.databinding.CartFoodListItemBinding
import com.salonsolution.app.databinding.CartListItemBinding
import com.salonsolution.app.databinding.CartProductListItemBinding
import com.salonsolution.app.interfaces.CartItemClickListener
import com.salonsolution.app.utils.AvatarGenerator

class CartListAdapter(val listener: CartItemClickListener) :
    ListAdapter<CartServiceList, CartListAdapter.CartListViewHolder>(CartListDiffUtil()) {

    inner class CartListViewHolder(val itemBinding: CartListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        val binding: CartListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cart_list_item,
            parent,
            false
        )
        return CartListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.itemBinding) {
            tvCategoryName.text = item.categoryName
            tvServiceName.text = item.serviceName
            tvDate.text = item.bookingDate
            tvStaff.text = item.staffName
            tvTime.text = item.serviceTime
            tvPrice.text = item.serviceCost

            val imgAvatar = AvatarGenerator.avatarImage(
                context = holder.itemBinding.root.context,
                width = 500,
                height = 500,
                avatarTextSizeRatio = 0.33f,
                name = item.serviceName,
                avatarTextStyle = Typeface.NORMAL,
                backgroundColor = ContextCompat.getColor(
                    holder.itemBinding.root.context,
                    R.color.avatar_background_color
                ),
                avatarTextColor = ContextCompat.getColor(
                    holder.itemBinding.root.context,
                    R.color.avatar_text_color
                )
            )
            Glide.with(root.context)
                .load(item.serviceImage)
                .placeholder(imgAvatar)
                .centerInside()
                .into(ivService)

            ivFoodAdd.setOnClickListener {
                listener.onFoodAddClick(holder.absoluteAdapterPosition, item)
            }

            ivProductAdd.setOnClickListener {
                listener.onProductAddClick(holder.absoluteAdapterPosition, item)
            }
            ivDeleteService.setOnClickListener {
                listener.onServiceDeleteClick(holder.absoluteAdapterPosition,item)
            }

            addProductList(
                this@with,
                item.productList,
                item,
                holder.absoluteAdapterPosition,
                listener
            )
            addFoodList(this@with, item.foodList, item, holder.absoluteAdapterPosition, listener)


        }

    }

    private fun addFoodList(
        cartListItemBinding: CartListItemBinding,
        foodList: ArrayList<CartFoodList>,
        serviceList: CartServiceList,
        absoluteAdapterPosition: Int,
        listener: CartItemClickListener
    ) {
        if (foodList.isEmpty()) {
            cartListItemBinding.llFoodList.visibility = View.GONE
        } else {
            //remove previous views
            cartListItemBinding.llFoodList.removeAllViews()

            // add product item into LinearLayout
            for (i in 0 until foodList.size) {
                val item = foodList[i]
                val inflater: LayoutInflater =
                    LayoutInflater.from(cartListItemBinding.llFoodList.context).context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                    ) as LayoutInflater

                val listItemBinding: CartFoodListItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.cart_food_list_item,
                        null,
                        false
                    )

                with(listItemBinding) {
                    val imgAvatar = AvatarGenerator.avatarImage(
                        context = root.context,
                        width = 500,
                        height = 500,
                        avatarTextSizeRatio = 0.33f,
                        name = item.foodName,
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
                        .load(item.foodImage)
                        .placeholder(imgAvatar)
                        .centerInside()
                        .into(ivFood)

                    tvName.text = item.foodName
                    tvPrice.text = item.foodCost
                    tvQuantity.text = item.foodQty.toString()


                    ivFoodDelete.setOnClickListener {
                        listener.onFoodDeleteClick(absoluteAdapterPosition, i, item, serviceList)
                    }

                    ivAdd.setOnClickListener {
                        listener.onFoodUpdateClick(absoluteAdapterPosition, i, item, serviceList)
                    }

                    ivRemove.setOnClickListener {
                        listener.onFoodRemovedClick(absoluteAdapterPosition, i, item, serviceList)
                    }

                }

                //add child view to the linear layout
                cartListItemBinding.llFoodList.addView(listItemBinding.root)

            }

        }

    }

    private fun addProductList(
        cartListItemBinding: CartListItemBinding,
        productList: ArrayList<CartProductList>,
        serviceList: CartServiceList,
        absoluteAdapterPosition: Int,
        listener: CartItemClickListener
    ) {
        if (productList.isEmpty()) {
            cartListItemBinding.llProductList.visibility = View.GONE
        } else {
            //remove previous views
            cartListItemBinding.llProductList.removeAllViews()

            // add product item into LinearLayout
            for (i in 0 until productList.size) {
                val item = productList[i]
                val inflater: LayoutInflater =
                    LayoutInflater.from(cartListItemBinding.llProductList.context).context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                    ) as LayoutInflater

                val listItemBinding: CartProductListItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.cart_product_list_item,
                        null,
                        false
                    )

                with(listItemBinding) {
                    val imgAvatar = AvatarGenerator.avatarImage(
                        context = root.context,
                        width = 500,
                        height = 500,
                        avatarTextSizeRatio = 0.33f,
                        name = item.productName,
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
                        .load(item.productImage)
                        .placeholder(imgAvatar)
                        .centerInside()
                        .into(ivProduct)

                    tvName.text = item.productName
                    tvPrice.text = item.productCost

                    ivProductDelete.setOnClickListener {
                        listener.onProductDeleteClick(absoluteAdapterPosition, i, item, serviceList)
                    }

                }

                //add child view to the linear layout
                cartListItemBinding.llProductList.addView(listItemBinding.root)

            }

        }

    }

    class CartListDiffUtil : DiffUtil.ItemCallback<CartServiceList>() {
        override fun areItemsTheSame(
            oldItem: CartServiceList,
            newItem: CartServiceList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CartServiceList,
            newItem: CartServiceList
        ): Boolean {
            return oldItem == newItem
        }


    }
}