package com.merkaaz.app.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.ProfitLossList
import com.merkaaz.app.databinding.ProfitLossItemsLayoutBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants.PROFIT_LOSS_DETAILS
import com.merkaaz.app.utils.Constants.VIEW_DETAILS


class ProfitLossAdapter(
    private val listener: AdapterItemClickListener
) : PagingDataAdapter<ProfitLossList, ProfitLossAdapter.ViewHolder>(Comparator()){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = ProfitLossItemsLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.tvViewDetails.setOnClickListener{v->
            listener.onAdapterItemClick(snapshot(),position,v,PROFIT_LOSS_DETAILS)
        }
        }


    inner class ViewHolder(val binding: ProfitLossItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")

        fun bind(listItem: ProfitLossList?) = with(itemView) {
            binding.profitLossList=listItem
            binding.executePendingBindings()
        }

    }



    class Comparator : DiffUtil.ItemCallback<ProfitLossList>() {
        override fun areItemsTheSame(oldItem: ProfitLossList, newItem: ProfitLossList): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: ProfitLossList, newItem: ProfitLossList): Boolean {
            return oldItem == newItem
        }

    }
}