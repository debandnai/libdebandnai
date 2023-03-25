package com.merkaaz.app.adapter


import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.ProductList
import com.merkaaz.app.databinding.ItemsDetailsLayoutBinding
import com.merkaaz.app.ui.activity.OrderDetailsActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class OrderDetailsProductAdapter(
    val productList: ArrayList<ProductList>,
    private val activity: OrderDetailsActivity
) :
    RecyclerView.Adapter<OrderDetailsProductAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = ItemsDetailsLayoutBinding.inflate(inflater, parent, false)
        return ItemViewHolder(listItem)
        }

    override fun onBindViewHolder(holder: OrderDetailsProductAdapter.ItemViewHolder, position: Int) {
        holder.bind(productList[position],position)
        Handler(Looper.getMainLooper()).postDelayed({
            holder.itemView.post {
        if (position==0 && productList.size>10)
            activity.setRecycleViewHeight(holder.itemView.height)
            }
        }, 2000)
    }

   inner class ItemViewHolder(val binding: ItemsDetailsLayoutBinding) :

        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: ProductList, position: Int) = with(itemView) {
            binding.productList=items
            if (position==productList.size.minus(1))
                binding.viewDotLine.visibility= View.GONE
            else
                binding.viewDotLine.visibility= View.VISIBLE
            binding.executePendingBindings()
       }
   }
    override fun getItemCount()=productList.size
}









