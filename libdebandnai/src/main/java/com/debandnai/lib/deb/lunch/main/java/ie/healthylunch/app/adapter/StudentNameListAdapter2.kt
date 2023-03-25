package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants


class StudentNameListAdapter2(
    var activity: Activity,
    var studentNameList: ArrayList<ie.healthylunch.app.data.model.studentListModel.DataItem>,
    var listener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<StudentNameListAdapter2.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.student_name_item, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int = studentNameList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        fun bind(position: Int) {
            val studentNameTv = itemView.findViewById<TextView>(R.id.studentNameTv)
            val studentNameCheckUncheckIv =
                itemView.findViewById<ImageView>(R.id.studentNameCheckUncheckIv)
            val itemLayout = itemView.findViewById<LinearLayout>(R.id.item_Layout)


            val dataItem: ie.healthylunch.app.data.model.studentListModel.DataItem =
                studentNameList[position]
            studentNameTv.text = "${dataItem.fName} ${dataItem.lName}"



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
                for (i in studentNameList.indices)
                    studentNameList[i].isSelected = false

                studentNameList[position].isSelected = true

                this@StudentNameListAdapter2.notifyDataSetChanged()

                listener.onAdapterItemClick(studentNameList, position, "studentNameList")
            }
            if (Constants.SELECTED_STUDENT_POSITION == position) {
                studentNameList[position].isSelected = true
            }

        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
