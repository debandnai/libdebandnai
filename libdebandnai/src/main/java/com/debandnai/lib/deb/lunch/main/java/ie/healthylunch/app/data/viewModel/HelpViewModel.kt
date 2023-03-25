package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import ie.healthylunch.app.R
import java.io.File

class HelpViewModel : ViewModel() {
    fun setImageView(view: SubsamplingScaleImageView, activity: Activity) {
        view.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
        view.scaleX = 1.0f
        Glide
            .with(activity)
            .downloadOnly() // notice this is much earlier in the chain
            .load(R.drawable.app_explainer_new)
            .into(object : CustomTarget<File>() {
                override fun onResourceReady(resource: File, transition: Transition<in File>?) {

                    view.setImage(
                        ImageSource.uri(Uri.fromFile(resource)),
                        ImageViewState(0F, PointF(0F, 0F), 0),
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }

}