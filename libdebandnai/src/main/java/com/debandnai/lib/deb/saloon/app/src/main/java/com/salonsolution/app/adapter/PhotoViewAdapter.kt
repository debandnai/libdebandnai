package com.salonsolution.app.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.salonsolution.app.R
import com.salonsolution.app.databinding.PhotoViewItemBinding

class PhotoViewAdapter(var images: Array<String>) :
    RecyclerView.Adapter<PhotoViewAdapter.ImageSliderViewHolder>() {


    inner class ImageSliderViewHolder(val itemBinding: PhotoViewItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        val binding: PhotoViewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.photo_view_item,
            parent,
            false
        )
        return ImageSliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {

        with(holder.itemBinding) {
            //photoView.transitionName = position.toString()
            progressBar.visibility = View.VISIBLE
            Glide.with(root.context)
                .load(images[position])
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.itemBinding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(photoView)
        }
    }

    override fun getItemCount(): Int {
       return images.size
    }
}