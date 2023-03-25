package ie.healthylunch.app.utils.coverflow.core

import android.annotation.SuppressLint
import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller

class CustomScroller : Scroller {
    private var mScrollFactor = 1.0

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, interpolator: Interpolator?) : super(context, interpolator) {}

    @SuppressLint("NewApi")
    constructor(context: Context?, interpolator: Interpolator?, flywheel: Boolean) : super(
        context,
        interpolator,
        flywheel
    ) {
    }

    fun setScrollDurationFactor(scrollFactor: Double) {
        mScrollFactor = scrollFactor
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(
            startX, startY, dx, dy,
            (mDuration * mScrollFactor).toInt()
        )
    }

    companion object {
        private const val mDuration = 500
    }
}