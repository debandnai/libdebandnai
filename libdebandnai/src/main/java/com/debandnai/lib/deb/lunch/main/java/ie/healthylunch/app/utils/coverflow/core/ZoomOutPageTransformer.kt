package ie.healthylunch.app.utils.coverflow.core

import android.view.View
import androidx.viewpager.widget.ViewPager

class ZoomOutPageTransformer(isZoomEnable: Boolean) : ViewPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height
        var vertMargin = pageHeight * (1 - MIN_SCALE) / 2
        var horzMargin = pageWidth * (1 - MIN_SCALE) / 2
        view.scaleX = MIN_SCALE
        view.scaleY = MIN_SCALE
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            //view.setAlpha(MIN_ALPHA);
            view.translationX = horzMargin - vertMargin / 2
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
            vertMargin = pageHeight * (1 - scaleFactor) / 2
            horzMargin = pageWidth * (1 - scaleFactor) / 2
            if (position < 0) {
                view.translationX = horzMargin - vertMargin / 2
            } else {
                view.translationX = -horzMargin + vertMargin
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor

            // Fade the page relative to its size.
            /*view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));*/
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            //view.setAlpha(MIN_ALPHA);
            view.translationX = -horzMargin + vertMargin
        }
    }

    companion object {
        private var MIN_SCALE = 1f
        private const val MIN_ALPHA = 0.7f
    }

    init {
        if (isZoomEnable) {
            MIN_SCALE = 0.85f
        } else {
            MIN_SCALE = 1f
        }
    }
}