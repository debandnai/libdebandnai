package ie.healthylunch.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R

class StudentNameListAdapterDashboard: RecyclerView.Adapter<StudentNameListAdapterDashboard.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentNameListAdapterDashboard.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_student_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentNameListAdapterDashboard.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 0
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {

        }
    }

}