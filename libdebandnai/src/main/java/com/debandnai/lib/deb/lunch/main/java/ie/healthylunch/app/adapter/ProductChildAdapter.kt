package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.data.model.productListByMenuTemplateModel.ProductItem
import ie.healthylunch.app.databinding.ProductChildItemBinding
import ie.healthylunch.app.ui.ProductActivity
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO


class ProductChildAdapter(
    val activity: Activity,
    private val productChildList: List<ProductItem?>?,
    val groupPosition: Int,
    val maxNumber: Int?,
    val childItemListener: AdapterItemOnclickListener,
    var isXpLayoutVisible: Boolean
) :
    RecyclerView.Adapter<ProductChildAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductChildAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = ProductChildItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ProductChildAdapter.ViewHolder, position: Int) {
        productChildList?.get(position)?.let { holder.bind(it, position) }
    }

    override fun getItemCount(): Int = productChildList?.size ?: 0

    inner class ViewHolder(val binding: ProductChildItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n", "NotifyDataSetChanged")
        fun bind(dataItem: ProductItem, position: Int) {
            dataItem.isXpLayoutVisible =
                this@ProductChildAdapter.isXpLayoutVisible && (dataItem.xpPoints ?: 0) > 0
            binding.dataItem = dataItem
            val childItem = productChildList?.get(position)

            var priceInFloat = 0F
            try {
                priceInFloat = dataItem.additionalPrice?.toFloat() ?: 0F
            } catch (_: Exception) {
            }

            if (priceInFloat > 0)
                binding.priceTv.visibility = View.VISIBLE
            else
                binding.priceTv.visibility = View.GONE

            binding.itemLayout.setOnClickListener {

                val state: Int? = childItem?.isOrdered
                productChildList?.get(position)?.isOrdered = if (state == 0) 1 else 0

                //get product group list from activity
                val productGroupList = (activity as ProductActivity).productGroupList

                if (productChildList?.get(position)?.isOrdered == 1) {

                    //check if maximum selection number exceeds or not
                    // if maximum selection exceeds then all item except will be deselected
                    //Here we check if selected number equal to the max number because we are checking first and plus the Int later
                    if ((productGroupList?.get(groupPosition)?.selectedChildItemNumber
                            ?: 0) >= (maxNumber ?: 1)
                    ) {
                        for (i in productChildList?.indices!!) {
                            productChildList[i]?.isOrdered = 0

                        }
                        productGroupList?.get(groupPosition)?.selectedChildItemNumber =
                            0


                    }
                    //when (isNoType == 1)  product is selected the other item will be deselected
                    else if (childItem?.isNoType == 1) {
                        for (i in productChildList?.indices!!) {
                            productChildList[i]?.isOrdered = 0


                        }
                        productGroupList?.get(groupPosition)?.selectedChildItemNumber =
                            0

                    }
                    //when (isNoType == 0)  product is selected the (isNoType == 0) product  will be deselected
                    else if (childItem?.isNoType == STATUS_ZERO) {
                        for (i in productChildList?.indices!!) {
                            if (productChildList[i]?.isNoType == STATUS_ONE && productChildList[i]?.isOrdered == STATUS_ONE) {
                                productChildList[i]?.isOrdered = STATUS_ZERO

                                productGroupList?.get(groupPosition)?.selectedChildItemNumber =
                                    productGroupList?.get(groupPosition)?.selectedChildItemNumber?.minus(
                                        STATUS_ONE
                                    )
                                break
                            }
                        }


                    }

                    //This line for if selected item exceeds max number then all  item will be deselected and the current item will br selected only
                    productChildList?.get(position)?.isOrdered = STATUS_ONE

                    productGroupList?.get(groupPosition)?.selectedChildItemNumber =
                        productGroupList?.get(groupPosition)?.selectedChildItemNumber?.plus(
                            STATUS_ONE
                        )

                } else {
                    productGroupList?.get(groupPosition)?.selectedChildItemNumber =
                        productGroupList?.get(groupPosition)?.selectedChildItemNumber?.minus(
                            STATUS_ONE
                        )
                }


                //Replace the modified child list in the Product Group item's child list
                productGroupList?.get(groupPosition)?.product = productChildList

                notifyDataSetChanged()

                childItemListener.onAdapterItemClick(
                    productGroupList,
                    groupPosition,
                    "child"
                )


            }

            binding.infoIv.setOnClickListener {
                childItemListener.onAdapterItemClick(productChildList, position, "info")
            }

            binding.executePendingBindings()
        }

    }
}