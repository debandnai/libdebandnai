package com.salonsolution.app.bindingAdapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.salonsolution.app.R
import com.salonsolution.app.utils.AvatarGenerator

@BindingAdapter(
    value = ["imageUrl", "placeHolder", "placeHolderText", "error"],
    requireAll = false
)
fun loadImageUrl(
    view: ImageView,
    url: String?,
    placeHolder: Drawable?,
    placeHolderText: String?,
    error: Drawable?
) {

    url?.let {
        //create request options
        val requestOptions = RequestOptions()
        placeHolderText?.let { text ->
            val imgAvatar = AvatarGenerator.avatarImage(
                context = view.context,
                width = 500,
                height = 500,
                avatarTextSizeRatio = 0.33f,
                name = text,
                avatarTextStyle = Typeface.NORMAL,
                backgroundColor = ContextCompat.getColor(
                    view.context,
                    R.color.avatar_background_color
                ),
                avatarTextColor = ContextCompat.getColor(
                    view.context,
                    R.color.avatar_text_color
                )
            )
            requestOptions.placeholder(imgAvatar)
        }
        placeHolder?.let { img ->
            requestOptions.placeholder(img)
        }
        error?.let { img -> requestOptions.error(img) }


        Glide.with(view.context)
            .load(url)
//           .diskCacheStrategy(DiskCacheStrategy.NONE)
//           .skipMemoryCache(true)
            .apply(requestOptions)
            .into(view)
    }
}

@BindingAdapter(
    value = ["imageUri", "placeHolderUri", "placeHolderUriText", "errorUri"],
    requireAll = false
)
fun loadImageUri(
    view: ImageView,
    uri: Uri?,
    placeHolderUri: Drawable?,
    placeHolderUriText: String?,
    errorUri: Drawable?
) {

    uri?.let {
        //create request options
        val requestOptions = RequestOptions()
        placeHolderUriText?.let { text ->
            val imgAvatar = AvatarGenerator.avatarImage(
                context = view.context,
                width = 500,
                height = 500,
                avatarTextSizeRatio = 0.33f,
                name = text,
                avatarTextStyle = Typeface.NORMAL,
                backgroundColor = ContextCompat.getColor(
                    view.context,
                    R.color.avatar_background_color
                ),
                avatarTextColor = ContextCompat.getColor(
                    view.context,
                    R.color.avatar_text_color
                )
            )
            requestOptions.placeholder(imgAvatar)
        }
        placeHolderUri?.let { img -> requestOptions.placeholder(img) }
        errorUri?.let { img -> requestOptions.error(img) }

        Glide.with(view.context)
            .load(it)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .apply(requestOptions)
            .into(view)
    }
}

@BindingAdapter("imageBitmap")
fun setImageFromBitmap(view: ImageView, bitmap: Bitmap?) {
    view.setImageBitmap(bitmap)
}

@BindingAdapter("base64String")
fun setImageFromBase64(view: ImageView, base64String: String?) {
    val decodedString: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
    val bitmap =
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    view.setImageBitmap(bitmap)
}


@BindingAdapter("loadImageUrlWithLoader")
fun loadImageUrlWithLoader(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        val circularProgressDrawable = CircularProgressDrawable(view.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(
            ContextCompat.getColor(
                view.context,
                R.color.brand_color
            )
        )
        circularProgressDrawable.start()

        Glide.with(view.context)
            .load(url)
            .placeholder(circularProgressDrawable)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(
                        view.context,
                        view.context.getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(view)
    }
}

@BindingAdapter("loadImageUriWithLoader")
fun loadImageUriWithLoader(view: ImageView, uri: Uri?) {
    uri?.let {
        val circularProgressDrawable = CircularProgressDrawable(view.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(
            ContextCompat.getColor(
                view.context,
                R.color.brand_color
            )
        )
        circularProgressDrawable.start()

        Glide.with(view.context)
            .load(it)
            .placeholder(circularProgressDrawable)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(
                        view.context,
                        view.context.getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(view)
    }
}