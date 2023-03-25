package com.merkaaz.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.R
import com.merkaaz.app.data.model.CategoryListItem
import com.merkaaz.app.data.model.ProductListItem
import com.merkaaz.app.databinding.CartChildItemBinding
import com.merkaaz.app.databinding.CartItemBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.utils.Constants

class CartAdapter(
    val addRemoveListener: ProductAddRemoveListner,
    val listener: AdapterItemClickListener,
    var cartList: List<CategoryListItem?>?,
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = CartItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cartList?.get(position)?.let { item ->
            holder.bind(item)

        }

    }

    override fun getItemCount() = cartList?.size ?: 0

    inner class ViewHolder(val binding: CartItemBinding, private val cartAdapter: CartAdapter) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryListItem) {
            binding.dataItem = item
            binding.llSubItemContainer.removeAllViews()
            item.productList?.let { productList ->
                for (i in productList.indices) {
                    val productListItem = productList[i]
                    //set Selected variation item from each variationList's first item (by default)
                    if (productListItem?.variationList?.isNotEmpty() == true){
                        productListItem.selectedVariationDataItem =
                            productListItem.variationList[0]
                    }


//                    productListItem?.selectedVariationDataItem =
//                        productListItem?.variationList?.get(0)

                    productListItem?.selectedVariationDataItem?.cartId = productListItem?.cartId

                    setSubItem(
                        binding.llSubItemContainer,
                        productListItem,
                        cartAdapter,
                        i,
                        productList
                    )
                }
            }

            binding.executePendingBindings()
        }

        private fun setSubItem(
            layout: LinearLayout?,
            productListItem: ProductListItem?,
            cartAdapter: CartAdapter,
            itemPosition: Int?,
            prodList: List<ProductListItem?>
        ) {

            Log.d(TAG, "setSubItem:itemPosition $itemPosition")
            val inflater: LayoutInflater =
                LayoutInflater.from(layout?.context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val listItemBinding: CartChildItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.cart_child_item, null, false)
            listItemBinding.listItem = productListItem


            //set click listener on minus icon
            listItemBinding.ivRemove.setOnClickListener {
                if ((productListItem?.cartQuantity ?: 0) > 1) {
                    if ((productListItem?.selectedVariationDataItem?.quantity ?: 0)
                        < (productListItem?.cartQuantity ?: 0)
                    ) {
                        cartAdapter.addRemoveListener.onUpdateitem(
                            productListItem?.selectedVariationDataItem,
                            productListItem?.productId,
                            itemPosition,
                            (productListItem?.selectedVariationDataItem?.quantity ?: 0)
                        )
                    } else
                        cartAdapter.addRemoveListener.onUpdateitem(
                            productListItem?.selectedVariationDataItem,
                            productListItem?.productId,
                            itemPosition,
                            productListItem?.cartQuantity?.minus(1)
                        )
                } else
                    cartAdapter.addRemoveListener.onRemoveitem(
                        productListItem?.selectedVariationDataItem,
                        itemPosition
                    )
            }
            listItemBinding.ivAdd.setOnClickListener {
                cartAdapter.addRemoveListener.onUpdateitem(
                    productListItem?.selectedVariationDataItem,
                    productListItem?.productId,
                    itemPosition,
                    productListItem?.cartQuantity?.plus(1)
                )
            }
            listItemBinding.tvDelete.setOnClickListener {
                Log.d(TAG, "setSubItem: delete  itemPosition "+itemPosition)
                cartAdapter.addRemoveListener.onRemoveitem(
                    productListItem?.selectedVariationDataItem,
                    itemPosition
                )
            }
            listItemBinding.llProduct.setOnClickListener {
                Log.d(TAG, "setSubItem: cart adapter itemPosition $itemPosition ${prodList[itemPosition!!]?.productId} ")
                cartAdapter.listener.onAdapterItemClick(
                    prodList,
                    itemPosition!!,
                    it,
                    Constants.CART_ADAPTER_ITEM_CLICK
                )
            }


            listItemBinding.executePendingBindings()

            //add child view to the linear layout
            layout?.addView(listItemBinding.root)
        }
    }


}