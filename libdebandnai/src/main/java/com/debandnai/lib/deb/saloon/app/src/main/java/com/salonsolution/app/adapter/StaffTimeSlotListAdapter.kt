package com.salonsolution.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salonsolution.app.R
import com.salonsolution.app.data.model.StaffCalendarTimeSlotModel
import com.salonsolution.app.data.model.TimeSlotModel
import com.salonsolution.app.databinding.StaffCalendarTimeSlotItemBinding
import com.salonsolution.app.databinding.StaffCalendarTimeslotListItemBinding
import com.salonsolution.app.interfaces.StaffCalendarTimeSlotClickListener
import java.util.ArrayList

class StaffTimeSlotListAdapter(val listener: StaffCalendarTimeSlotClickListener) :
    ListAdapter<StaffCalendarTimeSlotModel, StaffTimeSlotListAdapter.SubCategoriesViewHolder>(
        ListDiffUtil()
    ) {


    inner class SubCategoriesViewHolder(val itemBinding: StaffCalendarTimeslotListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoriesViewHolder {
        val binding: StaffCalendarTimeslotListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.staff_calendar_timeslot_list_item,
            parent,
            false
        )
        return SubCategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubCategoriesViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.itemBinding) {
            Log.d("tag","Time: ${item.time}")
            tvTime.text = item.time
            setSubItem(llTimeSlot, item.timeSlotList, holder.absoluteAdapterPosition, listener)
        }
    }


    class ListDiffUtil : DiffUtil.ItemCallback<StaffCalendarTimeSlotModel>() {
        override fun areItemsTheSame(
            oldItem: StaffCalendarTimeSlotModel,
            newItem: StaffCalendarTimeSlotModel
        ): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(
            oldItem: StaffCalendarTimeSlotModel,
            newItem: StaffCalendarTimeSlotModel
        ): Boolean {
            return oldItem == newItem
        }


    }


    private fun setSubItem(
        llTimeSlot: LinearLayout,
        list: ArrayList<TimeSlotModel>,
        slotTimePosition: Int,
        listener: StaffCalendarTimeSlotClickListener
    ) {
        //remove previous views
        llTimeSlot.removeAllViews()

        for (i in 0 until list.size) {
            val item = list[i]
            val inflater: LayoutInflater =
                LayoutInflater.from(llTimeSlot.context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val listItemBinding: StaffCalendarTimeSlotItemBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.staff_calendar_time_slot_item,
                    null,
                    false
                )
            listItemBinding.checkbox.isEnabled = item.isAvailable
            //listItemBinding.checkbox.isClickable = item.isAvailable
            listItemBinding.checkbox.isChecked = item.isChecked
            if (i % 2 == 0) {
                listItemBinding.backLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        llTimeSlot.context,
                        R.color.staff_time_slot_item_bg
                    )
                )
            } else {
                listItemBinding.backLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        llTimeSlot.context,
                        R.color.white
                    )
                )

            }
            //setting weight 1 to the subItem root layout
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.weight = 1F
            listItemBinding.root.layoutParams = lp

            listItemBinding.backLayout.setOnClickListener {
                if(item.isAvailable && !item.isChecked){
                    listener.onClick(slotTimePosition,i,item)
                }
            }

            //add child view to the linear layout
            llTimeSlot.addView(listItemBinding.root)
        }
    }


}