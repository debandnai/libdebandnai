package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.allergenListModel.DataItem
import ie.healthylunch.app.databinding.EditAllergyItemBinding
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants.Companion.NUTRITIONAL_CONSIDERATION
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO


class NutritionalConsiderationListAdapter(
    var activity: Activity,
    var allergenList: List<DataItem>,
    var listener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<NutritionalConsiderationListAdapter.ViewHolder>() {
    var noItemSelected = true


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NutritionalConsiderationListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = EditAllergyItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: NutritionalConsiderationListAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = allergenList.size

    inner class ViewHolder(val binding: EditAllergyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(position: Int) {
            val dataItem: DataItem = allergenList[position]
            binding.allergensNameTv.text = dataItem.allergenName

            //check if any item is selected previously or not (Except checking the 1st item)
            if (position != STATUS_ZERO && dataItem.isMapped == STATUS_ONE) {
                noItemSelected = false
            }


            if (dataItem.isMapped == STATUS_ONE)
                binding.allergensCheckUncheckIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.green_circle_active
                    )
                )
            else
                binding.allergensCheckUncheckIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.green_circle_inactive
                    )
                )
            if (noItemSelected)
                allergenList[STATUS_ZERO].isMapped == STATUS_ONE
            else
                allergenList[STATUS_ZERO].isMapped == STATUS_ZERO
            binding.itemLayout.setOnClickListener {
                val state: Int = allergenList[position].isMapped
                allergenList[position].isMapped = if (state == STATUS_ZERO) STATUS_ONE else STATUS_ZERO

                for (i in allergenList.indices) {
                    if (position!=i) {
                        allergenList[i].isMapped = STATUS_ZERO
                    }
                }

                /*if (position == STATUS_ZERO) {
                    noItemSelected = true
                    if (allergenList[STATUS_ZERO].isMapped == STATUS_ONE) {
                        //if "No Allergen" is selected then other item will be deselected
                        for (i in STATUS_ONE until allergenList.size) {
                            allergenList[i].isMapped = STATUS_ZERO
                        }
                    } else
                        allergenList[STATUS_ZERO].isMapped = STATUS_ONE //because default should be no allergen

                } else {
                    if (allergenList[position].isMapped == STATUS_ONE)
                    //if any item is selected except "No Allergen" then "No Allergen" item will be deselected
                        allergenList[STATUS_ZERO].isMapped = STATUS_ZERO
                    else {
                        //check for if all items except "No Allergen" are deselected then "No Allergen" will be selected
                        var noAllergen = true
                        for (i in STATUS_ONE until allergenList.size) {
                            if (allergenList[i].isMapped == STATUS_ONE) {
                                noAllergen = false
                                break
                            }
                        }

                        if (noAllergen) {
                            allergenList[STATUS_ZERO].isMapped = STATUS_ONE
                            noItemSelected = true
                        }
                    }
                }*/

                notifyDataSetChanged()

                listener.onAdapterItemClick(allergenList, position, NUTRITIONAL_CONSIDERATION)
            }

        }

    }
}