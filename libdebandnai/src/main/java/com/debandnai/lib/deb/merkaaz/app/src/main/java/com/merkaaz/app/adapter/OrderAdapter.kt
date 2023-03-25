package com.merkaaz.app.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.R
import com.merkaaz.app.data.model.OrderList
import com.merkaaz.app.data.model.ProductList
import com.merkaaz.app.databinding.OrderChildItemBinding
import com.merkaaz.app.databinding.OrderItemsLayoutBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants.MANAGE_P_L
import com.merkaaz.app.utils.Constants.VIEW_DETAILS


class OrderAdapter(
    private val listener: AdapterItemClickListener
) : PagingDataAdapter<OrderList, OrderAdapter.ViewHolder>(Comparator()){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = OrderItemsLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.btnViewDetails.setOnClickListener{v->
            listener.onAdapterItemClick(snapshot(),position,v,VIEW_DETAILS)
        }
        holder.binding.btnManagePl.setOnClickListener{v->
            listener.onAdapterItemClick(snapshot(),position,v, MANAGE_P_L)
        }

       }


    inner class ViewHolder(val binding: OrderItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")

        fun bind(listItem: OrderList?) = with(itemView) {
            binding.orderList=listItem
            binding.llProductItem.removeAllViews()
            listItem?.productList?.let { productList ->
                for (i in productList.indices) {
                    //Only 2 product will show...
                    if (i>1)
                        break
                    val productListItem = productList[i]
                    setSubItem(binding.llProductItem, productListItem)

                }

            }
            binding.executePendingBindings()
        }
    }

    private fun setSubItem(
        layout: LinearLayout,
        productList: ProductList
    ) {
        val inflater: LayoutInflater =
            LayoutInflater.from(layout.context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemBinding: OrderChildItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.order_child_item, null, false)
        listItemBinding.listItem = productList
        listItemBinding.executePendingBindings()

        //add child view to the linear layout
        layout.addView(listItemBinding.root)
    }

    class Comparator : DiffUtil.ItemCallback<OrderList>() {
        override fun areItemsTheSame(oldItem: OrderList, newItem: OrderList): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderList, newItem: OrderList): Boolean {
            return oldItem == newItem
        }

    }
}