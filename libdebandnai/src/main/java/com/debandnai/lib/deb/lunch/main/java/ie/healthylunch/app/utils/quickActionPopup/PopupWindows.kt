package ie.healthylunch.app.utils.quickActionPopup

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow

/**
 * Custom popup window.
 *
 * @author Lorensius W. L. T <lorenz></lorenz>@londatiga.net>
 */
open class PopupWindows(protected var mContext: Context) {
    @JvmField
	protected var mWindow: PopupWindow = PopupWindow(mContext)

    protected open var mRootView: View? = null
    private var mBackground: Drawable? = null
    @JvmField
	protected var mWindowManager: WindowManager

    /**
     * On dismiss
     */
    protected open fun onDismiss() {}

    /**
     * On show
     */
    protected fun onShow() {}

    /**
     * On pre show
     */
    protected fun preShow() {
        checkNotNull(mRootView) { "setContentView was not called with a view to display." }
        onShow()
        /*if (mBackground == null) mWindow.setBackgroundDrawable(BitmapDrawable()) else mWindow.setBackgroundDrawable(
            mBackground
        )*/
        mWindow.setBackgroundDrawable(null)
        mWindow.width = 400
        mWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        mWindow.isTouchable = true
        mWindow.isFocusable = true
        mWindow.isOutsideTouchable = true
        mWindow.contentView = mRootView
    }

    /**
     * Set background drawable.
     *
     * @param background Background drawable
     */
    fun setBackgroundDrawable(background: Drawable?) {
        mBackground = background
    }

    /**
     * Set content view.
     *
     * @param root Root view
     */
    fun setContentView(root: View?) {
        mRootView = root
        mWindow.contentView = root
    }

    /**
     * Set content view.
     *
     * @param layoutResID Resource id
     */
    fun setContentView(layoutResID: Int) {
        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        setContentView(inflator.inflate(layoutResID, null))
    }

    /**
     * Set listener on window dismissed.
     *
     * @param listener
     */
    fun setOnDismissListener(listener: PopupWindow.OnDismissListener?) {
        mWindow.setOnDismissListener(listener)
    }

    /**
     * Dismiss the popup window.
     */
    fun dismiss() {
        mWindow.dismiss()
    }

    /**
     * Constructor.
     *
     * @param context Context
     */
    init {
        mWindow.setTouchInterceptor(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                mWindow.dismiss()
                return@OnTouchListener true
            }
            false
        })
        mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}