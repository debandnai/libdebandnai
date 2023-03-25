package com.merkaaz.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.R
import com.merkaaz.app.data.model.FeaturedData
import com.merkaaz.app.data.model.VariationDataItem

import com.merkaaz.app.databinding.ChoosePackSizeItemBinding
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass

class BottomSheetChoosePackSizeAdapter(
    private val context: Context?,
    private val itemList: FeaturedData,
    private val addRemoveListener: ProductAddRemoveListner,
    private val isUserActive: Int?
) :
    RecyclerView.Adapter<BottomSheetChoosePackSizeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = ChoosePackSizeItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)

    }

    override fun onBindViewHolder(holder: ViewHolder, position_unit: Int) {
        holder.bind(itemList.variationDataList[position_unit], position_unit)
    }

    inner class ViewHolder(val binding: ChoosePackSizeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: VariationDataItem, position: Int) {
            binding.variationData = item

            if (item.quantity == 0) {
                binding.tvOutOfStock.visibility = View.VISIBLE
                binding.tvOutOfStock.setBackgroundResource(R.drawable.red_rect_top_left_bottom_right_corner_bg)
                binding.btnAddUnit.setBackgroundResource(R.drawable.button_ash)

            } else if (item.isDiscounted.equals(Constants.YES, ignoreCase = true)) {
                binding.tvOutOfStock.visibility = View.VISIBLE
                binding.tvOutOfStock.setBackgroundResource(R.drawable.orange_rect_top_left_bottom_right_corner_bg)
                context?.let {ctx->
                    binding.tvOutOfStock.text = item.discountPercentage + " "+ ctx.resources.getString(R.string.off)
                }

            }

            if (item.variationData_qty > 0) {
                binding.btnAddUnit.visibility = View.GONE
                binding.qntLay.visibility = View.VISIBLE
                binding.tvQuantity.text = item.variationData_qty.toString()
                if (item.isDiscounted.equals(Constants.NO, true)) {
                    binding.tvOutOfStock.visibility = View.INVISIBLE
                }
            } else if (item.presentInCart.equals(Constants.YES, true)) {
                binding.btnAddUnit.visibility = View.GONE
                binding.qntLay.visibility = View.VISIBLE
                item.variationData_qty = item.cartQuantity!!.toInt()
                binding.tvQuantity.text = item.variationData_qty.toString()
            } else {
                binding.btnAddUnit.visibility = View.VISIBLE
                binding.qntLay.visibility = View.GONE
            }


            binding.btnAddUnit.setOnClickListener {
                if (isUserActive == 1) {
                    addRemoveListener.onAdditem(null, null, position)
                } else {
                    context?.let {
                        MethodClass.custom_msg_dialog(
                            it,
                            context.resources.getString(R.string.you_are_not_an_approved_user)
                        )?.show()
                    }
                }
            }
            binding.ivAdd.setOnClickListener {
                if (isUserActive == 1) {
                    val total_qty: Int? = item.quantity
                    if (total_qty != null)
                        if (item.variationData_qty <= total_qty) {
                            addRemoveListener.onUpdateitem(
                                null,
                                null,
                                position,
                                (item.variationData_qty) + 1
                            )
                        } else {
                            val msg =
                                binding.root.context.resources.getString(R.string.only) + total_qty + binding.root.context.resources.getString(
                                    R.string.left_stock
                                )
                            MethodClass.custom_msg_dialog(binding.root.context, msg)
                        }
                    else{
                        val msg = binding.root.context.resources.getString(R.string.only)+total_qty+binding.root.context.resources.getString(R.string.left_stock)
                        MethodClass.custom_msg_dialog(binding.root.context,msg)
                    }
                } else {
                    context?.let {
                        MethodClass.custom_msg_dialog(
                            it,
                            context.resources.getString(R.string.you_are_not_an_approved_user)
                        )?.show()
                    }
                }
            }
            binding.ivRemove.setOnClickListener {

                if (isUserActive == 1) {
                    if (item.variationData_qty > 1) {
                        addRemoveListener.onUpdateitem(
                            null,
                            null,
                            position,
                            (item.variationData_qty) - 1
                        )
                    } else
                        addRemoveListener.onRemoveitem(null, position)
                } else {
                    context?.let {
                        MethodClass.custom_msg_dialog(
                            it,
                            context.resources.getString(R.string.you_are_not_an_approved_user)
                        )?.show()
                    }
                }
            }

            binding.executePendingBindings()

        }

    }

    override fun getItemCount(): Int {
        return itemList.variationDataList.size
    }
}