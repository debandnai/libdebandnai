package ie.healthylunch.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R

class AddChildrenAllergensAdapter: RecyclerView.Adapter<AddChildrenAllergensAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddChildrenAllergensAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_child_allergens_recycler_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddChildrenAllergensAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 0
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {

        }
    }

}