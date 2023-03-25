package ie.healthylunch.app.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import ie.healthylunch.app.data.model.transactionListModel.DataItem
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionList
import ie.healthylunch.app.databinding.TransactionViewListBinding
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants.Companion.TRANSACTION_INFO_TAG


class TransactionPagingAdapter(
    private val listener: AdapterItemOnclickListener
) : PagingDataAdapter<TransactionList, TransactionPagingAdapter.ViewHolder>(Comparator()){

    lateinit var ivInfo: ImageView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = TransactionViewListBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.ivInfo.setOnClickListener {
            this.ivInfo = holder.binding.ivInfo
            listener.onAdapterItemClick(
                snapshot(),
                holder.layoutPosition,
                TRANSACTION_INFO_TAG
            )

        }
    }


    inner class ViewHolder(val binding: TransactionViewListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")

        fun bind(listItem: TransactionList?) = with(itemView) {
            binding.transactionList=listItem
            binding.executePendingBindings()
        }

    }



    class Comparator : DiffUtil.ItemCallback<TransactionList>() {
        override fun areItemsTheSame(oldItem: TransactionList, newItem: TransactionList): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: TransactionList, newItem: TransactionList): Boolean {
            return oldItem == newItem
        }

    }
}