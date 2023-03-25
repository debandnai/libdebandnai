package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.allergenListModel.DataItem
import ie.healthylunch.app.utils.AdapterItemOnclickListener


class EditAllergenListAdapter(
    var activity: Activity,
    var allergenList: List<DataItem>?,
    var listener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<EditAllergenListAdapter.ViewHolder>() {
    var noItemSelected = true


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditAllergenListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.edit_allergy_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditAllergenListAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = allergenList!!.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(position: Int) {
            val allergensNameTv = itemView.findViewById<TextView>(R.id.allergensNameTv)
            val allergensCheckUncheckIv =
                itemView.findViewById<ImageView>(R.id.allergensCheckUncheckIv)
            val itemLayout = itemView.findViewById<RelativeLayout>(R.id.itemLayout)


            val dataItem: DataItem = allergenList!![position]
            allergensNameTv.text = dataItem.allergenName

            //check if any item is selected previously or not (Except checking the 1st item)
            if (position != 0 && dataItem.isMapped == 1) {
                noItemSelected = false
            }

            if (dataItem.isMapped == 1)
                allergensCheckUncheckIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.green_circle_active
                    )
                )
            else
                allergensCheckUncheckIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.green_circle_inactive
                    )
                )

            itemLayout.setOnClickListener {
                val state: Int = allergenList!![position].isMapped
                allergenList!![position].isMapped = if (state == 0) 1 else 0

                if (position == 0) {
                    noItemSelected = true
                    if (allergenList!![0].isMapped == 1) {
                        //if "No Allergen" is selected then other item will be deselected
                        for (i in 1 until allergenList!!.size) {
                            allergenList!![i].isMapped = 0
                        }
                    } else
                        allergenList!![0].isMapped = 1 //because default should be no allergen

                } else {
                    if (allergenList!![position].isMapped == 1)
                    //if any item is selected except "No Allergen" then "No Allergen" item will be deselected
                        allergenList!![0].isMapped = 0
                    else {
                        //check for if all items except "No Allergen" are deselected then "No Allergen" will be selected
                        var noAllergen = true
                        for (i in 1 until allergenList!!.size) {
                            if (allergenList!![i].isMapped == 1) {
                                noAllergen = false
                                break
                            }
                        }

                        if (noAllergen) {
                            allergenList!![0].isMapped = 1
                            noItemSelected = true
                        }
                    }
                }

                notifyDataSetChanged()

                listener.onAdapterItemClick(allergenList!!, position, "allergenList")
            }

        }

    }
}