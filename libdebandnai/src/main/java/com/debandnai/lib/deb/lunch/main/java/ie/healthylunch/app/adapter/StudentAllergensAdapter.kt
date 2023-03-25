package ie.healthylunch.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R

class StudentAllergensAdapter: RecyclerView.Adapter<StudentAllergensAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentAllergensAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.student_allergens_recycler_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentAllergensAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 0
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {

        }
    }

}