package com.salonsolution.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.ReviewList
import com.salonsolution.app.databinding.ReviewListItemBinding
import com.salonsolution.app.utils.AvatarGenerator

class StaffReviewsPagingAdapter :
    PagingDataAdapter<ReviewList, StaffReviewsPagingAdapter.ReviewListViewHolder>(ReviewListDiffUtil()) {

    inner class ReviewListViewHolder(val itemBinding: ReviewListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
        val binding: ReviewListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.review_list_item,
            parent,
            false
        )
        return ReviewListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {

        val item = getItem(position)
        item?.let { list ->
            with(holder.itemBinding) {
                val imgAvatar = AvatarGenerator.avatarImage(
                    context = root.context,
                    width = 500,
                    height = 500,
                    avatarTextSizeRatio = 0.33f,
                    name = list.customerName,
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
                    .load(item.imageLink)
                    .placeholder(imgAvatar)
                    .centerInside()
                    .into(ivCustomer)
                tvName.text = list.customerName
                tvDate.text = list.reviewDate
                tvReview.text = list.reviewData

                ratingbar.rating = try {
                    list.reviewPoint?.toFloat() ?: 0F
                } catch (e: Exception) {
                    0F
                }

            }
        }

    }


    class ReviewListDiffUtil : DiffUtil.ItemCallback<ReviewList>() {
        override fun areItemsTheSame(oldItem: ReviewList, newItem: ReviewList): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ReviewList, newItem: ReviewList): Boolean {
            return oldItem == newItem
        }

    }

}