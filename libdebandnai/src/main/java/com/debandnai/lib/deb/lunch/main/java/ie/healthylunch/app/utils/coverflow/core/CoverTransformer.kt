package ie.healthylunch.app.utils.coverflow.core

import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager

class CoverTransformer(scale: Float, pagerMargin: Float, spaceValue: Float, rotationY: Float) :
    ViewPager.PageTransformer {
    var scale = 0f
    private var pagerMargin = 0f
    private var spaceValue = 0f
    private val rotationX = 0f
    private var rotationY = 0f
    override fun transformPage(page: View, position: Float) {
        Log.d(TAG, "position:$position")
        if (rotationY != 0f) {
            val realRotationY = Math.min(rotationY, Math.abs(position * rotationY))
            page.rotationY = if (position < 0f) realRotationY else -realRotationY
        }
        if (scale != 0f) {
            val realScale: Float =
                Utils.getFloat(1 - Math.abs(position * scale), SCALE_MIN, SCALE_MAX)
            page.scaleX = realScale
            page.scaleY = realScale
        }
        if (pagerMargin != 0f) {
            var realPagerMargin = position * pagerMargin
            if (spaceValue != 0f) {
                val realSpaceValue: Float =
                    Utils.getFloat(Math.abs(position * spaceValue), MARGIN_MIN, MARGIN_MAX)
                realPagerMargin += if (position > 0) realSpaceValue else -realSpaceValue
            }
            page.translationX = realPagerMargin
        }
    }

    companion object {
        const val TAG = "CoverTransformer"
        const val SCALE_MIN = 0.3f
        const val SCALE_MAX = 1f
        const val MARGIN_MIN = 0f
        const val MARGIN_MAX = 50f
    }

    init {
        this.scale = scale
        this.pagerMargin = pagerMargin
        this.spaceValue = spaceValue
        this.rotationY = rotationY
    }
}