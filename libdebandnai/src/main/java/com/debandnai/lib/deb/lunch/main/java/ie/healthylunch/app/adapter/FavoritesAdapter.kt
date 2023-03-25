package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.data.model.favorites.FavouriteOrdersItem
import ie.healthylunch.app.databinding.FavoritesLayoutBinding
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO

class FavoritesAdapter(
    var favouriteOrder: List<FavouriteOrdersItem?>?,
    var xpLayoutVisible: Boolean?,
    var listener: AdapterItemOnclickListener
    )  :
    RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = FavoritesLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        favouriteOrder?.get(position)?.let {favouriteOrder-> holder.bind(favouriteOrder) }
        holder.binding.itemLayout.setOnClickListener{
            favouriteOrder?.let { favouriteOrder->
                for (favorites_item in favouriteOrder) {
                    favorites_item?.isSelected = STATUS_ZERO
                }
                favouriteOrder[position]?.isSelected= STATUS_ONE
                notifyDataSetChanged()
                listener.onAdapterItemClick(favouriteOrder, position, Constants.FAV_ORDER_FAVORITES)
            }

        }
        holder.binding.imgFavorites.setOnClickListener{
            listener.onAdapterItemClick(favouriteOrder, position, Constants.REMOVE_FAVORITES)
        }
    }

    override fun getItemCount() = favouriteOrder?.size?:0

   inner class ViewHolder(val binding: FavoritesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavouriteOrdersItem) {
            item.isXpLayoutVisible =
                xpLayoutVisible == true && (item.xpPoints ?: 0) > 0
            binding.dataItem = item
            binding.executePendingBindings()

        }

    }


}