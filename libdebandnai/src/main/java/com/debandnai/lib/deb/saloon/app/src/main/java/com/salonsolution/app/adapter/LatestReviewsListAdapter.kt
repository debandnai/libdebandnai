package com.salonsolution.app.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.LatestReviewList
import com.salonsolution.app.data.model.PopularCategoryList
import com.salonsolution.app.databinding.LatestReviewListItemBinding
import com.salonsolution.app.databinding.RecentServicesListItemBinding
import com.salonsolution.app.utils.AvatarGenerator

class LatestReviewsListAdapter :
    ListAdapter<LatestReviewList, LatestReviewsListAdapter.LatestReviewsListViewHolder>(ListDiffUtil()) {

    inner class LatestReviewsListViewHolder(val itemBinding: LatestReviewListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestReviewsListViewHolder {
        val binding: LatestReviewListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.latest_review_list_item,
            parent,
            false
        )
        return LatestReviewsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LatestReviewsListViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.itemBinding) {
            tvName.text = item.customerName
            tvDate.text  = item.reviewDate
            tvReview.text = item.reviewText
            tvRating.text = item.reviewPoint.toString()

            val imgAvatar = AvatarGenerator.avatarImage(
                context = root.context,
                width = 500,
                height = 500,
                avatarTextSizeRatio = 0.33f,
                name = item.customerName,
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
                .load(item.customerImage)
                .placeholder(imgAvatar)
                .centerInside()
                .into(ivCustomer)
        }

    }


    class ListDiffUtil : DiffUtil.ItemCallback<LatestReviewList>() {
        override fun areItemsTheSame(
            oldItem: LatestReviewList,
            newItem: LatestReviewList
        ): Boolean {
            return oldItem.reviewText == newItem.reviewText
        }

        override fun areContentsTheSame(
            oldItem: LatestReviewList,
            newItem: LatestReviewList
        ): Boolean {
            return oldItem == newItem
        }


    }


}