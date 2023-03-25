package ie.healthylunch.app.utils.coverflow.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager

class PagerContainer : FrameLayout, ViewPager.OnPageChangeListener {
    var viewPager: ViewPager? = null
        private set
    var mNeedsRedraw = false
    var isOverlapEnabled = false
    private var pageItemClickListener: PageItemClickListener? = null
    private var pressStartTime: Long = 0
    private var startX = 0f
    private var endX = 0f
    private var stayedWithinClickDistance = false
    private var range = 0f
    private var middle = 0f

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

    @JvmName("setOverlapEnabled1")
    fun setOverlapEnabled(overlapEnabled: Boolean) {
        isOverlapEnabled = overlapEnabled
    }

    fun setPageItemClickListener(pageItemClickListener: PageItemClickListener?) {
        this.pageItemClickListener = pageItemClickListener
    }

    @SuppressLint("MissingSuperCall")
    override fun onFinishInflate() {
        try {
            viewPager = getChildAt(0) as ViewPager
            viewPager!!.addOnPageChangeListener(this)
        } catch (e: Exception) {
            throw IllegalStateException("The root child of PagerContainer must be a ViewPager")
        }
    }

    private val mCenter = Point()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mCenter.x = w / 2
        mCenter.y = h / 2
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //We capture any touches not already handled by the ViewPager
        // to implement scrolling from a touch outside the pager bounds.
        range = (viewPager!!.width / 2).toFloat()
        middle = (this.width / 2).toFloat()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                run {
                    pressStartTime = System.currentTimeMillis()
                    stayedWithinClickDistance = true
                    startX = event.x
                }
                run {
                    if (stayedWithinClickDistance && Math.abs(event.x - startX) > Utils.convertDpToPixel(
                            10F,
                            context
                        )
                    ) {
                        stayedWithinClickDistance = false
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (stayedWithinClickDistance && Math.abs(event.x - startX) > Utils.convertDpToPixel(
                        10F,
                        context
                    )
                ) {
                    stayedWithinClickDistance = false
                }
            }
            MotionEvent.ACTION_UP -> {
                val pressDuration = System.currentTimeMillis() - pressStartTime
                if (pressDuration < 1000 && stayedWithinClickDistance) {
                    endX = event.x
                    if (Math.abs(endX - startX) < 10) {
                        val currentItem = viewPager!!.currentItem
                        if (endX > middle + range && currentItem < viewPager!!.adapter!!.count) {
                            viewPager!!.currentItem = currentItem + 1
                        } else if (endX < middle - range && currentItem > 0) {
                            viewPager!!.currentItem = currentItem - 1
                        } else if (endX < middle + range && endX > middle - range) {
                            if (pageItemClickListener != null) {
                                pageItemClickListener!!.onItemClick(
                                    viewPager!!.getChildAt(
                                        currentItem
                                    ), currentItem
                                )
                            }
                        }
                    }
                }
            }
        }
        return if (isOverlapEnabled) {
            viewPager!!.dispatchTouchEvent(event)
        } else {
            false
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //Force the container to redraw on scrolling.
        //Without this the outer pages render initially and then stay static
        if (mNeedsRedraw) invalidate()
    }

    override fun onPageSelected(position: Int) {
        /*  if (isOverlapEnabled) {
            //Counter for loop
            int loopCounter = 0;
            int PAGER_LOOP_THRESHOLD = 2;

            //SET THE START POINT back 2 views
            if (position >= PAGER_LOOP_THRESHOLD) {
                loopCounter = position - PAGER_LOOP_THRESHOLD;
            }
            do {
                if (loopCounter < mPager.getAdapter().getCount()) {

                    Object object = mPager.getAdapter().instantiateItem(mPager, loopCounter);
                    //Elevate the Center View if it's the selected position and de-elevate the left and right fragment

                    if (object instanceof Fragment) {
                        Fragment fragment = (Fragment) object;
                        if (loopCounter == position) {
                            ViewCompat.setElevation(fragment.getView(), 8.0f);
                        } else {
                            ViewCompat.setElevation(fragment.getView(), 0.0f);
                        }
                    } else {
                        ViewGroup view = (ViewGroup) object;
                        if (loopCounter == position) {
                            ViewCompat.setElevation(view, 8.0f);
                        } else {
                            ViewCompat.setElevation(view, 0.0f);
                        }
                        */
        /*if (object instanceof Fragment) {
                            Fragment fragment = (Fragment) object;
                            if (loopCounter == position) {
                                ViewCompat.setElevation(fragment.getView(), 8.0f);
                            } else {
                                ViewCompat.setElevation(fragment.getView(), 0.0f);
                            }
                        }*/
        /*
                    }
                }
                loopCounter++;
            } while (loopCounter < position + PAGER_LOOP_THRESHOLD);
            //Counter for loop

        }*/
    }

    override fun onPageScrollStateChanged(state: Int) {
        mNeedsRedraw = state != ViewPager.SCROLL_STATE_IDLE
    }
}