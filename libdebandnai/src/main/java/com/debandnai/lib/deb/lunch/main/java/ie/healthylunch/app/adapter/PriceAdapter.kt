package ie.healthylunch.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.databinding.PriceItemBinding
import ie.healthylunch.app.utils.PriceItem
import ie.healthylunch.app.utils.AdapterItemOnclickListener

class PriceAdapter(
    var priceList: MutableList<PriceItem>,
    var listener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<PriceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PriceAdapter.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = PriceItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: PriceAdapter.ViewHolder, position: Int) {
        holder.bind(priceList[position])
        holder.binding.cardViewLayout.setOnClickListener {
            listener.onAdapterItemClick(
                priceList,
                position,
                "priceList"
            )

        }
    }

    override fun getItemCount(): Int = priceList.size
    inner class ViewHolder(val binding: PriceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PriceItem) {
            binding.dataItem = item
            binding.executePendingBindings()
        }
    }

}