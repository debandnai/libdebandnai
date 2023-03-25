package ie.healthylunch.app.utils.coverflow.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.support.v4.view.LinkagePager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

class LinkagePagerContainer : FrameLayout, LinkagePager.OnPageChangeListener {
    private var mPager: LinkagePager? = null
    private var mNeedsRedraw = false
    private var isOverlapEnabled = false
    private var pageItemClickListener: PageItemClickListener? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    private fun init() {
        //Disable clipping of children so non-selected pages are visible
        clipChildren = false

        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setOverlapEnabled(overlapEnabled: Boolean) {
        isOverlapEnabled = overlapEnabled
    }

    fun setPageItemClickListener(pageItemClickListener: PageItemClickListener?) {
        this.pageItemClickListener = pageItemClickListener
    }

    @SuppressLint("MissingSuperCall")
    override fun onFinishInflate() {
        try {
            mPager = getChildAt(0) as LinkagePager
            mPager!!.addOnPageChangeListener(this)
        } catch (e: Exception) {
            throw IllegalStateException("The root child of PagerContainer must be a ViewPager")
        }
    }

    val viewPager: LinkagePager?
        get() = mPager
    private val mCenter = Point()
    private val mInitialTouch = Point()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d("@@@", "w:$w\nh: $h")
        mCenter.x = w / 2
        mCenter.y = h / 2
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialTouch.x = ev.x.toInt()
                mInitialTouch.y = ev.y.toInt()
                ev.offsetLocation(
                    (mCenter.x - mInitialTouch.x).toFloat(),
                    (mCenter.y - mInitialTouch.y).toFloat()
                )
            }
            MotionEvent.ACTION_UP -> {
                val delta = mPager?.let {
                    Utils.isInNonTappableRegion(
                        width, it.getWidth(),
                        mInitialTouch.x.toFloat(), ev.x
                    )
                }
                if (delta != 0) {
                    val preItem: Int = mPager!!.currentItem
                    val currentItem = preItem + delta!!
                    mPager!!.currentItem = currentItem
                    ev.offsetLocation(
                        (mCenter.x - mInitialTouch.x).toFloat(),
                        (mCenter.y - mInitialTouch.y).toFloat()
                    )
                    if (pageItemClickListener != null) {
                        pageItemClickListener!!.onItemClick(
                            mPager!!.getChildAt(currentItem),
                            currentItem
                        )
                    }
                }
            }
        }
        return mPager?.dispatchTouchEvent(ev)!!
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mNeedsRedraw) invalidate()
    }

    override fun onPageSelected(position: Int) {
        if (isOverlapEnabled) {
            //Counter for loop
            var loopCounter = 0
            val PAGER_LOOP_THRESHOLD = 2

            //SET THE START POINT back 2 fragments
            if (position >= PAGER_LOOP_THRESHOLD) {
                loopCounter = position - PAGER_LOOP_THRESHOLD
            }
            do {
                if (loopCounter < mPager?.adapter!!.count) {
                    val `object`: Any = mPager!!.adapter!!.instantiateItem(mPager!!, loopCounter)
                    //Elevate the Center View if it's the selected position and de-elevate the left and right fragment
                    if (`object` is Fragment) {
                        val fragment = `object`
                        if (loopCounter == position) {
                            ViewCompat.setElevation(fragment.requireView(), 12.0f)
                        } else {
                            ViewCompat.setElevation(fragment.requireView(), 0.0f)
                        }
                    } else {
                        val view = `object` as View
                        if (loopCounter == position) {
                            ViewCompat.setElevation(view, 12.0f)
                        } else {
                            ViewCompat.setElevation(view, 0.0f)
                        }
                    }
                }
                loopCounter++
            } while (loopCounter < position + PAGER_LOOP_THRESHOLD)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        mNeedsRedraw = state != ViewPager.SCROLL_STATE_IDLE
    }
}