package ie.healthylunch.app.adapter

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.studentListModel.DataItem
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.DEIS
import ie.healthylunch.app.utils.Constants.Companion.GROUP_ID
import ie.healthylunch.app.utils.Constants.Companion.JOIN_GROUP
import ie.healthylunch.app.utils.Constants.Companion.PRIVATE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.VALUE


class StudentListPagerAdapter(
    var activity: Activity,
    var studentList: List<DataItem>?
) :
    RecyclerView.Adapter<StudentListPagerAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = studentList!!.count()

    override

    fun getItemViewType(position: Int): Int {
        return R.layout.student_item
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class RecyclerViewHolder(val item: View) : RecyclerView.ViewHolder(item) {

        fun bind(position: Int) {
            val studentNameTv = item.findViewById<TextView>(R.id.studentNameTv)


            //val dataItem: DataItem = studentList[position]
            studentNameTv.text = studentList!![position].fName

            val firebaseAnalytics = FirebaseAnalytics.getInstance(activity)
            val bundle = Bundle()
            if (studentList!![position].schoolType==STATUS_ONE || studentList!![position].user_type.equals(
                    Constants.TEACHER,true)) {
                bundle.putInt(VALUE,STATUS_ONE)
            }
            else{
                bundle.putInt(VALUE,STATUS_TWO)
            }
            bundle.putString(GROUP_ID,studentList!![position].uniqueSchoolName)

            firebaseAnalytics.logEvent(JOIN_GROUP, bundle)
        }

    }

}