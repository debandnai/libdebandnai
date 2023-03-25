package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.schoolListModel.DataItem
import ie.healthylunch.app.utils.AdapterItemOnclickListener


class SchoolListAdapter(
    var activity: Activity,
    var schoolList: List<DataItem>?,
    var listener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<SchoolListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.student_name_item, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int = schoolList!!.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(position: Int) {
            val studentNameTv = itemView.findViewById<TextView>(R.id.studentNameTv)
            val studentNameCheckUncheckIv =
                itemView.findViewById<ImageView>(R.id.studentNameCheckUncheckIv)
              val itemLayout = itemView.findViewById<LinearLayout>(R.id.item_Layout)


            val dataItem: DataItem = schoolList!![position]
            studentNameTv.text = dataItem.schoolName
            //dataItem.isSelected = Constants.SELECTED_SCHOOL_POSITION ==position


            if (dataItem.isSelected)
                studentNameCheckUncheckIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.student_check_active
                    )
                )
            else
                studentNameCheckUncheckIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.green_circle_inactive
                    )
                )

             itemLayout.setOnClickListener {
                 for (i in schoolList!!.indices)
                     schoolList!![i].isSelected = false

                 schoolList!![position].isSelected = true

                 this@SchoolListAdapter.notifyDataSetChanged()

                 listener.onAdapterItemClick(schoolList!!, position, "schoolList")
             }

        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
