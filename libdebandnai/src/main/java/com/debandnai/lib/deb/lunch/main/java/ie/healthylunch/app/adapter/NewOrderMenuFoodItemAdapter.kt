package ie.healthylunch.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R

class NewOrderMenuFoodItemAdapter: RecyclerView.Adapter<NewOrderMenuFoodItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewOrderMenuFoodItemAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.order_menu_sub_menu_item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewOrderMenuFoodItemAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 0
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {

        }
    }

}