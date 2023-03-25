package ie.healthylunch.app.utils.quickActionPopup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ie.healthylunch.app.R
import java.util.*

/**
 * QuickAction dialog, shows action list as icon and text like the one in Gallery3D app. Currently supports vertical
 * and horizontal layout.
 *
 * @author Lorensius W. L. T <lorenz></lorenz>@londatiga.net>
 *
 * Contributors:
 * - Kevin Peck <kevinwpeck></kevinwpeck>@gmail.com>
 */
class QuickActionMenu @JvmOverloads constructor(
    context: Context,
    private val mOrientation: Int = VERTICAL
) : PopupWindows(context), PopupWindow.OnDismissListener {
    override var mRootView: View? = null
    private var mArrowUp: ImageView? = null
    private var mArrowDown: ImageView? = null
    private val mInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mTrack: ViewGroup? = null
    private var mScroller: ScrollView? = null
    private val actionItems: MutableList<ActionItem> = ArrayList()
    private val mDismissListener: PopupWindow.OnDismissListener? = null
    private var mDidAction = false
    private var mChildPos: Int
    private var mInsertPos = 0
    private var mAnimStyle: Int
    private var rootWidth = 0
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
        val onTop = dyTop > dyBottom
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

    fun showInBottom(anchor: View) {
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
            xPos =
                anchorRect.left - (rootWidth - anchor.width)
            xPos = if (xPos < 0) 0 else xPos
            arrowPos = anchorRect.centerX() - xPos
        } else if ((screenWidth - anchorRect.left) >= rootWidth) {
            xPos =
                anchorRect.left - (rootWidth - anchor.width)
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
        val onTop = (screenHeight - dyTop) <= rootHeight
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

    fun setAnimStyle(mAnimStyle: Int) {
        this.mAnimStyle = mAnimStyle
    }

    private fun setAnimationStyle(screenWidth: Int, requestedX: Int, onTop: Boolean) {
        val arrowPos = requestedX - mArrowUp!!.measuredWidth / 2
        when (mAnimStyle) {
            ANIM_GROW_FROM_LEFT -> mWindow.animationStyle =
                if (onTop) R.style.Animations_PopUpMenu_Left else R.style.Animations_PopDownMenu_Left
            ANIM_GROW_FROM_RIGHT -> mWindow.animationStyle =
                if (onTop) R.style.Animations_PopUpMenu_Right else R.style.Animations_PopDownMenu_Right
            ANIM_GROW_FROM_CENTER -> mWindow.animationStyle =
                if (onTop) R.style.Animations_PopUpMenu_Center else R.style.Animations_PopDownMenu_Center
            ANIM_REFLECT -> mWindow.animationStyle =
                if (onTop) R.style.Animations_PopUpMenu_Reflect else R.style.Animations_PopDownMenu_Reflect
            ANIM_AUTO -> if (arrowPos <= screenWidth / 4) {
                mWindow.animationStyle =
                    if (onTop) R.style.Animations_PopUpMenu_Left else R.style.Animations_PopDownMenu_Left
            } else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
                mWindow.animationStyle =
                    if (onTop) R.style.Animations_PopUpMenu_Center else R.style.Animations_PopDownMenu_Center
            } else {
                mWindow.animationStyle =
                    if (onTop) R.style.Animations_PopUpMenu_Right else R.style.Animations_PopDownMenu_Right
            }
        }
    }

    private fun showArrow(whichArrow: Int, requestedX: Int) {
        val showArrow: View? = if (whichArrow == R.id.arrow_up) mArrowUp else mArrowDown
        val hideArrow: View? = if (whichArrow == R.id.arrow_up) mArrowDown else mArrowUp
        val arrowWidth = mArrowUp!!.measuredWidth
        showArrow!!.visibility = View.VISIBLE
        val param = showArrow.layoutParams as ViewGroup.MarginLayoutParams
        param.leftMargin = requestedX - arrowWidth / 2
        hideArrow!!.visibility = View.INVISIBLE
    }

    override fun onDismiss() {}

    @SuppressLint("InflateParams")
    fun addActionItem(action: ActionItem) {
        actionItems.add(action)
        val title = action.getTitle()
        val product = action.getProduct()
        val line = action.getText_line()
        val icon = action.getIcon()
        val container: View = if (mOrientation == HORIZONTAL) {
            mInflater.inflate(R.layout.action_item_horizontal_menu, null)
        } else {
            mInflater.inflate(R.layout.action_item_vertical_menu, null)
        }

        //ImageView img 	= (ImageView) container.findViewById(R.id.iv_icon);
        val text = container.findViewById<View>(R.id.tv_title) as TextView
        val tv_product = container.findViewById<View>(R.id.tv_product) as TextView
        //TextView text_line 	= (TextView) container.findViewById(R.id.text_line);

        /*if (icon != null) {
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}*/if (title != null) {
            text.text = title
        }
        if (tv_product != null) {
            tv_product.text = product
        }


        val pos = mChildPos
        val actionId = action.getActionId()
        container.setOnClickListener { /*if (mItemClickListener != null) {
					mItemClickListener.onItemClick(QuickActionMenu.this, pos, actionId);
				}

				if (!getActionItem(pos).isSticky()) {
					mDidAction = true;

					dismiss();
				}*/
            dismiss()
        }
        container.isFocusable = true
        container.isClickable = true
        if (mOrientation == HORIZONTAL && mChildPos != 0) {
            val separator = mInflater.inflate(R.layout.horiz_separator, null)
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT
            )
            separator.layoutParams = params
            separator.setPadding(5, 0, 5, 0)
            mTrack!!.addView(separator, mInsertPos)
            mInsertPos++
        } else {
            val separator = mInflater.inflate(R.layout.horiz_separator, null)
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT
            )
            separator.layoutParams = params
            separator.setPadding(1, 0, 1, 0)
            mTrack!!.addView(separator, mInsertPos)
        }
        mTrack!!.addView(container, mInsertPos)
        mChildPos++
        mInsertPos++
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

    init {
        if (mOrientation == HORIZONTAL) {
            setRootViewId(R.layout.popup_horizontal_menu)
        } else {
            setRootViewId(R.layout.popup_vertical_menu)
        }
        mAnimStyle = ANIM_AUTO
        mChildPos = 0
    }
}