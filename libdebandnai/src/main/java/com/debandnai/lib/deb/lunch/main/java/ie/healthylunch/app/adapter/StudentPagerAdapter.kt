package ie.healthylunch.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.studentListModel.DataItem


class StudentPagerAdapter(
    var activity: Activity,
    var studentList: List<DataItem>
) :
    RecyclerView.Adapter<StudentPagerAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = studentList.size

    override fun getItemViewType(position: Int): Int {
        return R.layout.student_item
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class RecyclerViewHolder(val item: View) : RecyclerView.ViewHolder(item) {

        fun bind(position: Int) {
            val studentNameTv = item.findViewById<TextView>(R.id.studentNameTv)


            //val dataItem: DataItem = studentList[position]
            studentNameTv.text = studentList[position].fName
        }

    }

}