package ie.healthylunch.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.data.model.productInfoDetailsModel.NutritionValuesItem
import ie.healthylunch.app.databinding.NutritionItemBinding

class ProductInfoNutritionAdapter(private val nutritionList: List<NutritionValuesItem?>?) :
    RecyclerView.Adapter<ProductInfoNutritionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = NutritionItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        nutritionList?.get(position)?.let { holder.bind(it) }

    }

    override fun getItemCount() = nutritionList?.size ?: 0

    inner class ViewHolder(val binding: NutritionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NutritionValuesItem) {
            binding.dataItem = item
            binding.executePendingBindings()
        }

    }
}