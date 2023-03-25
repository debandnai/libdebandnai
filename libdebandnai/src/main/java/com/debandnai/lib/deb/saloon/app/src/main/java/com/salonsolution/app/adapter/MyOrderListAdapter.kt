package com.salonsolution.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toolbar.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.OrderList
import com.salonsolution.app.data.model.ServiceList
import com.salonsolution.app.databinding.MyOrderListItemBinding
import com.salonsolution.app.databinding.MyOrderServiceListItemBinding
import com.salonsolution.app.interfaces.MyOrderListClickListener
import com.salonsolution.app.utils.AvatarGenerator

class MyOrderListAdapter(val listener: MyOrderListClickListener) :
    ListAdapter<OrderList, MyOrderListAdapter.OrderListViewHolder>(
        OrderListListDiffUtil()
    ) {

    inner class OrderListViewHolder(val itemBinding: MyOrderListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        val binding: MyOrderListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.my_order_list_item,
            parent,
            false
        )
        return OrderListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {

        val item = getItem(position)
        item?.let { list ->
            with(holder.itemBinding) {
                tvOrderId.text = list.orderId
                tvOrderDate.text = list.orderDate
                tvGrandTotal.text = list.totalValue
                addServiceList(
                    this@with,
                    item.serviceList
                )
                // canbeCancelled =1 : user can cancel the order, canbeCancelled = 0 : check order status to show order status
                var isCancellable = false
                if (list.canbeCancelled == 0) {
                    when (list.orderStatus) {
                        1 -> {
                            btCancelOrder.text = root.context.getString(R.string.cancel_order)
                            isCancellable = true
                        }
                        2 -> {
                            btCancelOrder.text = root.context.getString(R.string.order_cancelled)
                        }
                        3 -> {
                            btCancelOrder.text = root.context.getString(R.string.order_completed)
                        }
                        else -> {
                            btCancelOrder.text = root.context.getString(R.string.cancel_order)
                            isCancellable = true
                        }
                    }
                } else {
                    btCancelOrder.text = root.context.getString(R.string.cancel_order)
                    isCancellable = true
                }
                btCancelOrder.isClickable = isCancellable
                btCancelOrder.isFocusable = isCancellable
                btCancelOrder.isEnabled = isCancellable

                btViewDetails.setOnClickListener {
                    listener.onViewDetailsClick(holder.absoluteAdapterPosition, list)
                }
                btBugItAgain.setOnClickListener {
                    listener.onBuyItAgainClick(holder.absoluteAdapterPosition, list)
                }
                btCancelOrder.setOnClickListener {
                    if (isCancellable)
                        listener.onCancelOrderClick(holder.absoluteAdapterPosition, list)

                }

            }
        }

    }

    private fun addServiceList(
        myOrderListItemBinding: MyOrderListItemBinding,
        serviceList: ArrayList<ServiceList>
    ) {
        //remove previous views
        myOrderListItemBinding.llServiceList.removeAllViews()

        // add product item into LinearLayout
        for (i in 0 until serviceList.size) {
            val item = serviceList[i]
            val inflater: LayoutInflater =
                LayoutInflater.from(myOrderListItemBinding.llServiceList.context).context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
                ) as LayoutInflater

            val listItemBinding: MyOrderServiceListItemBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.my_order_service_list_item,
                    null,
                    false
                )

            with(listItemBinding) {
                val imgAvatar = AvatarGenerator.avatarImage(
                    context = root.context,
                    width = 500,
                    height = 500,
                    avatarTextSizeRatio = 0.33f,
                    name = item.serviceName,
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
                    .load(item.serviceImage)
                    .placeholder(imgAvatar)
                    .centerInside()
                    .into(ivService)

                tvCategoryName.text = item.categoryName
                tvServiceName.text = item.serviceName
                tvDate.text = item.bookingDate
                tvStaff.text = item.staffName
                tvTime.text = item.serviceTime
                tvPrice.text = item.serviceCost

                //product
                if (item.productName.isNullOrEmpty()) {
                    productLayout.visibility = View.GONE
                } else {
                    productLayout.visibility = View.VISIBLE
                    val imgAvatar2 = AvatarGenerator.avatarImage(
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
                        .placeholder(imgAvatar2)
                        .centerInside()
                        .into(ivProduct)
                    tvProductName.text = item.productName
                    tvMoreProduct.visibility =
                        if ((item.productMore ?: 0) > 0) View.VISIBLE else View.GONE
                    tvMoreProduct.text =
                        root.context.getString(R.string.more_item, item.productMore)
                }

                //food
                if (item.foodName.isNullOrEmpty()) {
                    foodLayout.visibility = View.GONE
                } else {
                    foodLayout.visibility = View.VISIBLE
                    val imgAvatar3 = AvatarGenerator.avatarImage(
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
                        .placeholder(imgAvatar3)
                        .centerInside()
                        .into(ivFood)
                    tvFoodName.text = item.foodName
                    tvMoreFood.visibility =
                        if ((item.foodMore ?: 0) > 0) View.VISIBLE else View.GONE
                    tvMoreFood.text = root.context.getString(R.string.more_item, item.foodMore)
                }

            }

            //setMargin
            val layoutParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(20, 0, 20, 20)
            listItemBinding.root.layoutParams = layoutParams

            //add child view to the linear layout
            myOrderListItemBinding.llServiceList.addView(listItemBinding.root)

        }
    }


    class OrderListListDiffUtil : DiffUtil.ItemCallback<OrderList>() {
        override fun areItemsTheSame(oldItem: OrderList, newItem: OrderList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderList, newItem: OrderList): Boolean {
            return oldItem == newItem
        }

    }

}