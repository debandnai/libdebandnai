package com.merkaaz.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.ProfitlossProduct
import com.merkaaz.app.databinding.ProfitLossDetailsItemsLayoutBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants

class ProfitLossDetailsProductAdapter(
    val listener: AdapterItemClickListener,
    var profitLossProduct: List<ProfitlossProduct?>?
) : RecyclerView.Adapter<ProfitLossDetailsProductAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = ProfitLossDetailsItemsLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {profitLossProduct?.get(position)?.let { holder.bind(it,position) }}

    override fun getItemCount() = profitLossProduct?.size ?: 0

    inner class ViewHolder(val binding: ProfitLossDetailsItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProfitlossProduct, position: Int) {
            binding.profitlossProduct = item
            binding.btnCalculate.setOnClickListener {
                listener.onAdapterItemClick(profitLossProduct,position,binding.btnCalculate,
                    Constants.CALCULATE
                )
            }
            binding.executePendingBindings()
        }
    }


}