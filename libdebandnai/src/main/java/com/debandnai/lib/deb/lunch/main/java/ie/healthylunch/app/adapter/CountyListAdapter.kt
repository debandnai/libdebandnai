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
import ie.healthylunch.app.utils.AdapterItemOnclickListener


class CountyListAdapter(
    var activity: Activity,
    var countryList: List<ie.healthylunch.app.data.model.countryModel.DataItem>?,
    var listener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<CountyListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.student_name_item, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int = countryList?.size ?: 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(position: Int) {
            val studentNameTv = itemView.findViewById<TextView>(R.id.studentNameTv)
            val studentNameCheckUncheckIv =
                itemView.findViewById<ImageView>(R.id.studentNameCheckUncheckIv)
            val itemLayout = itemView.findViewById<LinearLayout>(R.id.item_Layout)


            val dataItem: ie.healthylunch.app.data.model.countryModel.DataItem =
                countryList!![position]
            studentNameTv.text = dataItem.countyName
            //dataItem.isSelected = SELECTED_COUNTRY_POSITION==position


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
                for (i in countryList!!.indices)
                    countryList!![i].isSelected = false

                countryList!![position].isSelected = true

                this@CountyListAdapter.notifyDataSetChanged()

                listener.onAdapterItemClick(countryList!!, position, "countyList")
            }

        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
