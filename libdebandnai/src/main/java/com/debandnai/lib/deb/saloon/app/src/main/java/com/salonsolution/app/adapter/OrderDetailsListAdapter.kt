package com.salonsolution.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.DetailsFoodList
import com.salonsolution.app.data.model.DetailsProductList
import com.salonsolution.app.data.model.DetailsServiceList
import com.salonsolution.app.databinding.OrderDetailsFoodListItemBinding
import com.salonsolution.app.databinding.OrderDetailsProductListItemBinding
import com.salonsolution.app.databinding.OrderDetailsServiceListItemBinding
import com.salonsolution.app.interfaces.OrderDetailsListClickListener
import com.salonsolution.app.utils.AvatarGenerator

class OrderDetailsListAdapter(val listener: OrderDetailsListClickListener) :
    ListAdapter<DetailsServiceList, OrderDetailsListAdapter.OrderDetailsViewHolder>(
        OrderListListDiffUtil()
    ) {

    inner class OrderDetailsViewHolder(val itemBinding: OrderDetailsServiceListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding: OrderDetailsServiceListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.order_details_service_list_item,
            parent,
            false
        )
        return OrderDetailsViewHolder(binding)
    }


    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {

        val item = getItem(position)
        item?.let { list ->
            with(holder.itemBinding) {
                val imgAvatar = AvatarGenerator.avatarImage(
                    context = root.context,
                    width = 500,
                    height = 500,
                    avatarTextSizeRatio = 0.33f,
                    name = item.serviceName,
                    avatarTextStyle = Typeface.NORMAL,
                    backgroundColor = ContextCompat.getColor(
                        root.context,
                        R.color.avatar_background_color
                    ),
                    avatarTextColor = ContextCompat.getColor(
                        root.context,
                        R.color.avatar_text_color
                    )
                )
                Glide.with(root.context)
                    .load(item.serviceImage)
                    .placeholder(imgAvatar)
                    .centerInside()
                    .into(ivService)
                tvCategoryName.text = list.categoryName
                tvServiceName.text = list.serviceName
                tvDate.text = list.bookingDate
                tvDate2.text = list.bookingDate
                tvStaff.text = list.staffName
                tvStaff2.text = list.staffName
                tvTime.text = list.serviceTime
                tvTime2.text = list.serviceTime
                tvPrice.text = list.serviceCost
                groupReview.visibility = if (list.isReviewAdded == 0 && list.canReview==1) View.VISIBLE else View.GONE
                tvWriteReview.text =
                    if (list.isReviewAdded == 0) root.context.getString(R.string.add_a_written_review)
                    else
                        root.context.getString(R.string.review_already_added)
                tvWriteReview.visibility = if (list.canReview==1) View.VISIBLE else View.GONE

                addProductList(holder.itemBinding, list.productList)
                addFoodList(holder.itemBinding, list.foodList)
                tvCommentError.visibility = View.GONE
                btSubmit.setOnClickListener {
                    if(etReview.text.isNullOrEmpty()){
                        tvCommentError.text = root.context.getString(R.string.please_write_something)
                        tvCommentError.visibility = View.VISIBLE
                    }else if(ratingbar.rating==0F){
                        tvCommentError.text = root.context.getString(R.string.please_select_your_rating)
                        tvCommentError.visibility = View.VISIBLE
                    }
                    else{
                        tvCommentError.visibility = View.GONE
                        listener.onReviewSubmitClick(holder.absoluteAdapterPosition,list,ratingbar.rating,etReview.text.trim().toString())
                    }
                }
                etReview.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        tvCommentError.visibility = View.GONE
                    }
                })
            }
        }

    }


    private fun addFoodList(
        orderDetailsBinding: OrderDetailsServiceListItemBinding,
        foodList: ArrayList<DetailsFoodList>,
    ) {
        if (foodList.isEmpty()) {
            orderDetailsBinding.llFoodList.visibility = View.GONE
            orderDetailsBinding.tvFood.visibility = View.GONE
            orderDetailsBinding.viewFoodBorder.visibility = View.GONE
        } else {
            //remove previous views
            orderDetailsBinding.llFoodList.removeAllViews()

            // add product item into LinearLayout
            for (i in 0 until foodList.size) {
                val item = foodList[i]
                val inflater: LayoutInflater =
                    LayoutInflater.from(orderDetailsBinding.llFoodList.context).context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                    ) as LayoutInflater

                val listItemBinding: OrderDetailsFoodListItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.order_details_food_list_item,
                        null,
                        false
                    )

                with(listItemBinding) {
                    val imgAvatar = AvatarGenerator.avatarImage(
                        context = root.context,
                        width = 500,
                        height = 500,
                        avatarTextSizeRatio = 0.33f,
                        name = item.foodName,
                        avatarTextStyle = Typeface.NORMAL,
                        backgroundColor = ContextCompat.getColor(
                            root.context,
                            R.color.avatar_background_color
                        ),
                        avatarTextColor = ContextCompat.getColor(
                            root.context,
                            R.color.avatar_text_color
                        )
                    )
                    Glide.with(root.context)
                        .load(item.foodImage)
                        .placeholder(imgAvatar)
                        .centerInside()
                        .into(ivFood)

                    tvName.text = item.foodName
                    tvPrice.text = item.foodCost
                    tvDescription.text = item.foodDesc

                }

                //add child view to the linear layout
                orderDetailsBinding.llFoodList.addView(listItemBinding.root)

            }

        }

    }

    private fun addProductList(
        orderDetailsBinding: OrderDetailsServiceListItemBinding,
        productList: ArrayList<DetailsProductList>,
    ) {
        if (productList.isEmpty()) {
            orderDetailsBinding.llProductList.visibility = View.GONE
            orderDetailsBinding.tvProduct.visibility = View.GONE
            orderDetailsBinding.viewProductBorder.visibility = View.GONE
        } else {
            //remove previous views
            orderDetailsBinding.llProductList.removeAllViews()

            // add product item into LinearLayout
            for (i in 0 until productList.size) {
                val item = productList[i]
                val inflater: LayoutInflater =
                    LayoutInflater.from(orderDetailsBinding.llProductList.context).context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                    ) as LayoutInflater

                val listItemBinding: OrderDetailsProductListItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.order_details_product_list_item,
                        null,
                        false
                    )

                with(listItemBinding) {
                    val imgAvatar = AvatarGenerator.avatarImage(
                        context = root.context,
                        width = 500,
                        height = 500,
                        avatarTextSizeRatio = 0.33f,
                        name = item.productName,
                        avatarTextStyle = Typeface.NORMAL,
                        backgroundColor = ContextCompat.getColor(
                            root.context,
                            R.color.avatar_background_color
                        ),
                        avatarTextColor = ContextCompat.getColor(
                            root.context,
                            R.color.avatar_text_color
                        )
                    )
                    Glide.with(root.context)
                        .load(item.productImage)
                        .placeholder(imgAvatar)
                        .centerInside()
                        .into(ivProduct)

                    tvName.text = item.productName
                    tvPrice.text = item.productCost
                    tvDescription.text = item.productDesc

                }

                //add child view to the linear layout
                orderDetailsBinding.llProductList.addView(listItemBinding.root)

            }

        }

    }


    class OrderListListDiffUtil : DiffUtil.ItemCallback<DetailsServiceList>() {
        override fun areItemsTheSame(
            oldItem: DetailsServiceList,
            newItem: DetailsServiceList
        ): Boolean {
            return oldItem.serviceId == newItem.serviceId
        }

        override fun areContentsTheSame(
            oldItem: DetailsServiceList,
            newItem: DetailsServiceList
        ): Boolean {
            return oldItem == newItem
        }

    }

}