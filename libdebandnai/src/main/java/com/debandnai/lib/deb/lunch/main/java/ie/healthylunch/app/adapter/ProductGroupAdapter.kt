package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.productListByMenuTemplateModel.ProductGroupItem
import ie.healthylunch.app.data.viewModel.ProductViewModel
import ie.healthylunch.app.databinding.ProductGroupItemBinding
import ie.healthylunch.app.ui.ProductActivity
import ie.healthylunch.app.utils.AdapterItemOnclickListener


class ProductGroupAdapter(
    val activity: Activity,
    var productGroupList: List<ProductGroupItem?>?,
    val productViewModel: ProductViewModel,
    val groupItemOnclickListener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<ProductGroupAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductGroupAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = ProductGroupItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ProductGroupAdapter.ViewHolder, position: Int) {
        holder.bind(productGroupList?.get(position), position)
    }

    override fun getItemCount(): Int = productGroupList?.size ?: 0


    inner class ViewHolder(val binding: ProductGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: ProductGroupItem?, position: Int) {
            binding.dataItem = dataItem
            if (ProductActivity.selectedGroupPosition == position) {
                binding.productGroupTv.setTextColor(
                    ContextCompat.getColor(
                        binding.productGroupTv.context,
                        R.color.green
                    )
                )
                productViewModel.maxNumberProduct.value = dataItem?.maxNumberOfProduct
            } else {
                binding.productGroupTv.setTextColor(
                    ContextCompat.getColor(
                        binding.productGroupTv.context,
                        R.color.light_green
                    )
                )
            }

            binding.itemLayout.setOnClickListener {
                groupItemOnclickListener.onAdapterItemClick(productGroupList,position,"group")
            }

            binding.executePendingBindings()

        }

    }
}