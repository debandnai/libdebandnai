package com.salonsolution.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salonsolution.app.R
import com.salonsolution.app.data.model.Description
import com.salonsolution.app.databinding.ServiceDetailsTabItemBinding

class ServiceDetailsTabAdapter(var details: ArrayList<Description>) :
    RecyclerView.Adapter<ServiceDetailsTabAdapter.DetailsTabViewHolder>() {


    inner class DetailsTabViewHolder(val itemBinding: ServiceDetailsTabItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsTabViewHolder {
        val binding: ServiceDetailsTabItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.service_details_tab_item,
            parent,
            false
        )
        return DetailsTabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailsTabViewHolder, position: Int) {
        holder.itemBinding.tvDetails.text = details[position].description
    }

    override fun getItemCount(): Int {
       return details.size
    }
}