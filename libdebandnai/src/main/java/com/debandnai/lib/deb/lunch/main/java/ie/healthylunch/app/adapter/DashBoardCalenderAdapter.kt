package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel.CalendarItem
import ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel.DataItem
import ie.healthylunch.app.databinding.DashboardBottomCalendarSubItemBinding
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import kotlinx.coroutines.NonDisposableHandle.parent


class DashBoardCalenderAdapter(

    var calendarMainList: List<DataItem>?,
    var listener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<DashBoardCalenderAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = calendarMainList?.size!!

    override fun getItemViewType(position: Int): Int {
        return R.layout.dashboard_bottom_calender_item
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class RecyclerViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        private var itemLayout: RelativeLayout? = null
        private var topCalendarLayout: LinearLayout? = null
        private var belowCalendarLayout: LinearLayout? = null

        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        fun bind(position: Int) {
            itemLayout = item.findViewById(R.id.itemLayout)
            topCalendarLayout = item.findViewById(R.id.topCalendarLayout)
            belowCalendarLayout = item.findViewById(R.id.belowCalendarLayout)

            for (i in calendarMainList?.get(position)?.calendarItemList?.indices!!) {
                val calendarItem = calendarMainList?.get(position)?.calendarItemList!![i]
                if (i < 5)
                    setSubItem(topCalendarLayout, calendarItem)
                else
                    setSubItem(belowCalendarLayout, calendarItem)

            }

/*
            itemLayout?.setOnClickListener {
                calendarMainList?.let { it1 ->
                    listener.onAdapterItemClick(
                        it1,
                        position,
                        "bottom_calendar"
                    )
                }
            }
*/
        }

    }

    fun setSubItem(layout: LinearLayout?, calendarItem: CalendarItem) {


        val inflater: LayoutInflater = LayoutInflater.from(layout?.context)

        val listItemBinding: DashboardBottomCalendarSubItemBinding = DataBindingUtil.inflate(
            inflater, R.layout.dashboard_bottom_calendar_sub_item, null, false
        )
        listItemBinding.listItem = calendarItem
        //setting weight 1 to the subItem root layout
        val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.weight = 1F
        listItemBinding.itemLayout.layoutParams = lp


        listItemBinding.executePendingBindings()

        //add child view to the linear layout
        layout?.addView(listItemBinding.root)
    }
}