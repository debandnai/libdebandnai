package ie.healthylunch.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R

class StudentListAdapter: RecyclerView.Adapter<StudentListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_item_county, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentListAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 0
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {

        }
    }

}