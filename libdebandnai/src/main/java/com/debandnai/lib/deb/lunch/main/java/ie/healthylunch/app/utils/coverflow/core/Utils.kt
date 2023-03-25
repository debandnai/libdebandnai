package ie.healthylunch.app.utils.coverflow.core

import android.content.Context
import android.util.DisplayMetrics

object Utils {
    fun isInNonTappableRegion(containerWidth: Int, pagerWidth: Int, oldX: Float, newX: Float): Int {
        var nonTappableWidth = (containerWidth - pagerWidth) / 2
        if (oldX < nonTappableWidth && newX < nonTappableWidth) {
            return (-Math.ceil(((nonTappableWidth - newX) / pagerWidth.toFloat()).toDouble())).toInt()
        }
        nonTappableWidth = (containerWidth + pagerWidth) / 2
        return if (oldX > nonTappableWidth && newX > nonTappableWidth) {
            Math.ceil(((newX - nonTappableWidth) / pagerWidth.toFloat()).toDouble())
                .toInt()
        } else 0
    }

    fun getFloat(value: Float, minValue: Float, maxValue: Float): Float {
        return Math.min(maxValue, Math.max(minValue, value))
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}