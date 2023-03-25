package com.salonsolution.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.FoodImages
import com.salonsolution.app.databinding.ImageSliderItemBinding
import com.salonsolution.app.interfaces.ImageSliderClickListener

class FoodImageSlideAdapter(var images: ArrayList<FoodImages>, val listener: ImageSliderClickListener) :
    RecyclerView.Adapter<FoodImageSlideAdapter.ImageSliderViewHolder>() {


    inner class ImageSliderViewHolder(val itemBinding: ImageSliderItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        val binding: ImageSliderItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.image_slider_item,
            parent,
            false
        )
        return ImageSliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        with(holder.itemBinding) {
            val item = images[position]
            Glide.with(holder.itemBinding.root.context)
                .load(item.imageLink)
                .placeholder(R.drawable.placeholder_image)
                .into(imageView)
           // imageView.transitionName = position.toString()

           root.setOnClickListener {
                listener.onItemClick(holder.absoluteAdapterPosition, item.imageLink,imageView)
            }
        }
    }

    override fun getItemCount(): Int {
       return images.size
    }
}