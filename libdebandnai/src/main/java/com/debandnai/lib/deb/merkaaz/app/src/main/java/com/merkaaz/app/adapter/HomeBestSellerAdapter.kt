package com.merkaaz.app.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.R
import com.merkaaz.app.data.model.FeaturedData
import com.merkaaz.app.databinding.HomeBestSellerItemBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.interfaces.ProductAddRemoveListner
import com.merkaaz.app.ui.dialogs.ProductQtyBottomSheet
import com.merkaaz.app.ui.dialogs.ProductQtyBottomSheetForHome
import com.merkaaz.app.ui.fragments.HomeFragment
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass

class HomeBestSellerAdapter(
    var productList: List<FeaturedData?>?,
    val listener: AdapterItemClickListener,
    private val addRemoveListener: ProductAddRemoveListner,
    private val fragment: HomeFragment,
    private val activity: Activity?,
     var isActive: Int?
) :
    RecyclerView.Adapter<HomeBestSellerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = HomeBestSellerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(productList?.get(position), position, fragment, activity, isActive, this)
        holder.binding.llItemLayout.setOnClickListener { v ->
            listener.onAdapterItemClick(productList, position, v, Constants.HOME_BEST_SELLER)
        }
    }

    class ViewHolder(val binding: HomeBestSellerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            listItem: FeaturedData?,
            position: Int,
            fragment: HomeFragment,
            activity: Activity?,
            isActive: Int?,
            adapter: HomeBestSellerAdapter
        ) {
            binding.featuredData = listItem
            listItem?.let {
                if (listItem.variationData.id == null) {
                    listItem.variationData = listItem.variationDataList[0]
                }
            }
            binding.btnAdd.visibility = View.GONE
            binding.qntLay.visibility = View.VISIBLE


            if ((listItem?.variationData?.variationData_qty ?: 0) > 0) {
                binding.btnAdd.visibility = View.GONE
                binding.qntLay.visibility = View.VISIBLE
                binding.tvOutOfStock.visibility = View.GONE
                binding.tvQuantity.text = listItem?.variationData?.variationData_qty.toString()
            } else if (listItem?.variationData?.presentInCart.equals(Constants.YES, true)) {
                binding.btnAdd.visibility = View.GONE
                binding.qntLay.visibility = View.VISIBLE
                binding.tvOutOfStock.visibility = View.GONE
                listItem?.variationData?.variationData_qty =
                    listItem?.variationData?.cartQuantity ?: 0
                binding.tvQuantity.text = listItem?.variationData?.variationData_qty.toString()
            } else if (listItem?.variationData?.quantity == 0) {
                binding.btnAdd.visibility = View.VISIBLE
                binding.qntLay.visibility = View.GONE
                binding.tvOutOfStock.visibility = View.VISIBLE
                binding.btnAdd.setBackgroundResource(R.drawable.button_ash)
                binding.btnAdd.isEnabled = false
            } else {
                binding.btnAdd.visibility = View.VISIBLE
                binding.btnAdd.isEnabled = true
                binding.btnAdd.setBackgroundResource(R.drawable.view_btn_bg)
                binding.qntLay.visibility = View.GONE
                binding.tvOutOfStock.visibility = View.GONE
                listItem?.variationData?.variationData_qty = 0
            }
            binding.tvUnit.setOnClickListener {
                val modalBottomSheet = listItem?.let { it1 -> ProductQtyBottomSheetForHome(it1,fragment) }
                modalBottomSheet?.show((activity as AppCompatActivity).supportFragmentManager, "")

            }

            binding.btnAdd.setOnClickListener {
                Log.d(TAG, "bind: btnadd data $isActive")
                if (isActive == 1) {
                    val totalQty: Int? = listItem?.variationData?.quantity
                    if (totalQty != null) {

                        if (listItem.variationData.variationData_qty <= totalQty) {
                            adapter.addRemoveListener.onAdditem(
                                listItem.variationData,
                                listItem.productId,
                                position
                            )
                        } else {
                            val msg =
                                binding.root.context.resources.getString(R.string.only) + " " + totalQty + binding.root.context.resources.getString(
                                    R.string.left_stock
                                )
                            MethodClass.custom_msg_dialog(binding.root.context, msg)
                        }
                    }else {
                            val msg =
                                binding.root.context.resources.getString(R.string.only) + " " + totalQty + binding.root.context.resources.getString(
                                    R.string.left_stock
                                )
                            MethodClass.custom_msg_dialog(binding.root.context, msg)
                        }
                } else {
                    activity?.let {
                        MethodClass.custom_msg_dialog(
                            it,
                            it.resources.getString(R.string.you_are_not_an_approved_user)
                        )?.show()
                    }
                }

            }
            binding.ivAdd.setOnClickListener {
                if (isActive == 1) {
                    val totalQty: Int? = listItem?.variationData?.quantity
                    if (totalQty != null)
                        if (listItem.variationData.variationData_qty < totalQty) {
                            adapter.addRemoveListener.onUpdateitem(
                                listItem.variationData,
                                listItem.productId,
                                position,
                                (listItem.variationData.variationData_qty) + 1
                            )
                        } else {
                            val msg =
                                binding.root.context.resources.getString(R.string.only) + " " + totalQty + binding.root.context.resources.getString(
                                    R.string.left_stock
                                )
                            MethodClass.custom_msg_dialog(binding.root.context, msg)?.show()
                        }
                } else {
                    activity?.let {
                        MethodClass.custom_msg_dialog(
                            it,
                            it.resources.getString(R.string.you_are_not_an_approved_user)
                        )?.show()
                    }
                }

            }
            binding.ivRemove.setOnClickListener {
                if (isActive == 1) {
                    if ((listItem?.variationData?.variationData_qty ?: 0) > 1) {
                        adapter.addRemoveListener.onUpdateitem(
                            listItem?.variationData,
                            listItem?.productId,
                            position,
                            (listItem?.variationData?.variationData_qty ?: 0) - 1
                        )
                    } else
                        adapter.addRemoveListener.onRemoveitem(listItem?.variationData, position)
                } else {
                    activity?.let {
                        MethodClass.custom_msg_dialog(
                            it,
                            it.resources.getString(R.string.you_are_not_an_approved_user)
                        )?.show()
                    }
                }
            }





            binding.llItemLayout.setOnClickListener {
                adapter.listener.onAdapterItemClick(
                    adapter.productList,
                    position,
                    it,
                    Constants.BEST_SELLER_PRODUCT
                )
            }
            fragment.observeRecyclerviewData_homebestseller(listItem)
            binding.executePendingBindings()
        }

    }

    override fun getItemCount(): Int = productList?.size ?: 0
}