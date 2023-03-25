package ie.healthylunch.app.utils.quickActionPopup

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ie.healthylunch.app.R
import java.util.ArrayList

class QuickAction @JvmOverloads constructor(
    context: Context,
    private val mOrientation: Int = VERTICAL
) :
    PopupWindows(context), PopupWindow.OnDismissListener {
    override var mRootView: View? = null
    private var mArrowUp: ImageView? = null
    private var mArrowDown: ImageView? = null
    private val mInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mTrack: ViewGroup? = null
    private var mScroller: ScrollView? = null
    private var mItemClickListener: OnActionItemClickListener? = null
    private var mDismissListener: OnDismissListener? = null
    private val actionItems: MutableList<ActionItem> = ArrayList()
    private var mDidAction = false
    private var mChildPos: Int
    private var mInsertPos = 0
    private var mAnimStyle: Int
    private var rootWidth = 0

    /**
     * Get action item at an index
     *
     * @param index  Index of item (position from callback)
     *
     * @return  Action Item at the position
     */
    private fun getActionItem(index: Int): ActionItem {
        return actionItems[index]
    }

    /**
     * Set root view.
     *
     * @param id Layout resource id
     */
    private fun setRootViewId(id: Int) {
        mRootView = mInflater.inflate(id, null) as ViewGroup
        mTrack = mRootView!!.findViewById<View>(R.id.tracks) as ViewGroup
        mArrowDown = mRootView!!.findViewById<View>(R.id.arrow_down) as ImageView
        mArrowUp = mRootView!!.findViewById<View>(R.id.arrow_up) as ImageView
        mScroller = mRootView!!.findViewById<View>(R.id.scroller) as ScrollView

        //This was previously defined on show() method, moved here to prevent force close that occured
        //when tapping fastly on a view to show quickaction dialog.
        //Thanx to zammbi (github.com/zammbi)
        mRootView!!.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setContentView(mRootView)
    }

    /**
     * Set animation style
     *
     * @param mAnimStyle animation style, default is set to ANIM_AUTO
     */
    fun setAnimStyle(mAnimStyle: Int) {
        this.mAnimStyle = mAnimStyle
    }

    /**
     * Set listener for action item clicked.
     *
     * @param listener Listener
     */
    fun setOnActionItemClickListener(listener: OnActionItemClickListener?) {
        mItemClickListener = listener
    }

    /**
     * Add action item
     *
     * @param action  [ActionItem]
     */
    fun addActionItem(action: ActionItem) {
        actionItems.add(action)
        val title: String? = action.getTitle()
        val product: String? = action.getProduct()
        val line: String? = action.getText_line()
        val icon: Drawable? = action.getIcon()
        val container: View
        if (mOrientation == HORIZONTAL) {
            container = mInflater.inflate(R.layout.action_item_horizontal, null)
        } else {
            container = mInflater.inflate(R.layout.action_item_vertical, null)
        }

        //ImageView img 	= (ImageView) container.findViewById(R.id.iv_icon);
        val text = container.findViewById<TextView>(R.id.tv_title)
        val productTv = container.findViewById<TextView>(R.id.tv_product)
        val dotLineTv = container.findViewById<TextView>(R.id.dotLineTv)

        /*if (icon != null) {
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}*/if (title != null) {

        } else {
            //text.setVisibility(View.GONE);
        }

        if (!title.isNullOrEmpty())
            text.text = title

        if (!product.isNullOrEmpty()) {
            productTv.text = product
            dotLineTv.visibility = View.VISIBLE
        } else {
            dotLineTv.visibility = View.GONE
            productTv.visibility = View.GONE
        }


        /*if (text_line != null) {
			text_line.setText(line);
		} else {
			//text_line.setVisibility(View.GONE);
		}*/
        val pos = mChildPos
        val actionId: Int = action.getActionId()
        container.setOnClickListener {
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(this@QuickAction, pos, actionId)
            }
            if (!getActionItem(pos).isSticky()) {
                mDidAction = true
                dismiss()
            }
        }
        container.isFocusable = true
        container.isClickable = true
        if (mOrientation == HORIZONTAL && mChildPos != 0) {
            val separator: View = mInflater.inflate(R.layout.horiz_separator, null)
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT
            )
            separator.layoutParams = params
            separator.setPadding(5, 0, 5, 0)
            mTrack!!.addView(separator, mInsertPos)
            mInsertPos++
        } else {
            val separator: View = mInflater.inflate(R.layout.horiz_separator, null)
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            separator.layoutParams = params
            separator.setPadding(1, 0, 1, 0)
            mTrack!!.addView(separator, mInsertPos)
        }
        mTrack!!.addView(container, mInsertPos)
        mChildPos++
        mInsertPos++
    }

    /**
     * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
     *
     */
    fun show(anchor: View) {
        preShow()
        var xPos: Int
        val yPos: Int
        val arrowPos: Int
        mDidAction = false
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val anchorRect = Rect(
            location[0], location[1], location[0] + anchor.width, location[1]
                    + anchor.height
        )

        //mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mRootView!!.measure(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val rootHeight = mRootView!!.measuredHeight
        if (rootWidth == 0) {
            rootWidth = mRootView!!.measuredWidth
        }
        val screenWidth = mWindowManager.defaultDisplay.width
        val screenHeight = mWindowManager.defaultDisplay.height

        //automatically get X coord of popup (top left)
        if (anchorRect.left + rootWidth > screenWidth) {
            xPos = anchorRect.left - (rootWidth - anchor.width)
            xPos = if (xPos < 0) 0 else xPos
            arrowPos = anchorRect.centerX() - xPos
        } else {
            xPos = if (anchor.width > rootWidth) {
                anchorRect.centerX() - rootWidth / 2
            } else {
                anchorRect.left
            }
            arrowPos = anchorRect.centerX() - xPos
        }
        val dyTop = anchorRect.top
        val dyBottom = screenHeight - anchorRect.bottom
        val onTop = if (dyTop > dyBottom) true else false
        if (onTop) {
            if (rootHeight > dyTop) {
                yPos = 15
                val l = mScroller!!.layoutParams
                l.height = dyTop - anchor.height
            } else {
                yPos = anchorRect.top - rootHeight
            }
        } else {
            yPos = anchorRect.bottom
            if (rootHeight > dyBottom) {
                val l = mScroller!!.layoutParams
                l.height = dyBottom
            }
        }
        showArrow(if (onTop) R.id.arrow_down else R.id.arrow_up, arrowPos)
        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop)
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos)
    }

    /**
     * Set animation style
     *
     * @param screenWidth screen width
     * @param requestedX distance from left edge
     * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
     * and vice versa
     */
    private fun setAnimationStyle(screenWidth: Int, requestedX: Int, onTop: Boolean) {
        val arrowPos = requestedX - mArrowUp!!.measuredWidth / 2
        when (mAnimStyle) {
            ANIM_GROW_FROM_LEFT -> mWindow.setAnimationStyle(if (onTop) R.style.Animations_PopUpMenu_Left else R.style.Animations_PopDownMenu_Left)
            ANIM_GROW_FROM_RIGHT -> mWindow.setAnimationStyle(if (onTop) R.style.Animations_PopUpMenu_Right else R.style.Animations_PopDownMenu_Right)
            ANIM_GROW_FROM_CENTER -> mWindow.setAnimationStyle(if (onTop) R.style.Animations_PopUpMenu_Center else R.style.Animations_PopDownMenu_Center)
            ANIM_REFLECT -> mWindow.setAnimationStyle(if (onTop) R.style.Animations_PopUpMenu_Reflect else R.style.Animations_PopDownMenu_Reflect)
            ANIM_AUTO -> if (arrowPos <= screenWidth / 4) {
                mWindow.setAnimationStyle(if (onTop) R.style.Animations_PopUpMenu_Left else R.style.Animations_PopDownMenu_Left)
            } else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
                mWindow.setAnimationStyle(if (onTop) R.style.Animations_PopUpMenu_Center else R.style.Animations_PopDownMenu_Center)
            } else {
                mWindow.setAnimationStyle(if (onTop) R.style.Animations_PopUpMenu_Right else R.style.Animations_PopDownMenu_Right)
            }
        }
    }

    /**
     * Show arrow
     *
     * @param whichArrow arrow type resource id
     * @param requestedX distance from left screen
     */
    private fun showArrow(whichArrow: Int, requestedX: Int) {
        val showArrow: View? = if (whichArrow == R.id.arrow_up) mArrowUp else mArrowDown
        val hideArrow: View? = if (whichArrow == R.id.arrow_up) mArrowDown else mArrowUp
        val arrowWidth = mArrowUp!!.measuredWidth
        showArrow!!.visibility = View.VISIBLE
        val param = showArrow.layoutParams as ViewGroup.MarginLayoutParams
        param.leftMargin = requestedX - arrowWidth / 2
        hideArrow!!.visibility = View.INVISIBLE
    }

    /**
     * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
     * by clicking outside the dialog or clicking on sticky item.
     */
    fun setOnDismissListener(listener: OnDismissListener?) {
        setOnDismissListener(this)
        mDismissListener = listener
    }

    override fun onDismiss() {
        if (!mDidAction && mDismissListener != null) {
            mDismissListener!!.onDismiss()
        }
    }

    /**
     * Listener for item click
     *
     */
    interface OnActionItemClickListener {
        fun onItemClick(source: QuickAction?, pos: Int, actionId: Int)
    }

    /**
     * Listener for window dismiss
     *
     */
    interface OnDismissListener {
        fun onDismiss()
    }

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        const val ANIM_GROW_FROM_LEFT = 1
        const val ANIM_GROW_FROM_RIGHT = 2
        const val ANIM_GROW_FROM_CENTER = 3
        const val ANIM_REFLECT = 4
        const val ANIM_AUTO = 5
    }
    /**
     * Constructor allowing orientation override
     *
     * @param context    Context
     * @param mOrientation Layout orientation, can be vartical or horizontal
     */
    /**
     * Constructor for default vertical layout
     *
     * @param context  Context
     */
    init {
        if (mOrientation == HORIZONTAL) {
            setRootViewId(R.layout.popup_horizontal)
        } else {
            setRootViewId(R.layout.popup_vertical)
        }
        mAnimStyle = ANIM_AUTO
        mChildPos = 0
    }
}