package ie.healthylunch.app.utils.sideMenu.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import ie.healthylunch.app.R

class SlideMenuOptionView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr) {
    private var mOptionViewHolder: OptionViewHolder? = null
    private var mIsSideSelectorEnabled = false
    private var mIsSelectorEnabled = false
    private fun initialize() {


        //ViewGroup rootView = (ViewGroup) inflate(getContext(), R.layout.slide_menu_view_option, this);
        val rootView = inflate(context, R.layout.slide_menu_list_row_item, this) as ViewGroup
        mOptionViewHolder = OptionViewHolder(rootView)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        isSelected = isSelected
    }
    /**
     * Check if the side selector is enabled or not.
     *
     * @return True if the side selector is enabled.
     */
    /**
     * Set the option view side selector enabled.
     * By default a red rectangle in front of the option text and when enabled
     * in front of the selector.
     *
     * @param sideSelectorEnabled Either true or false. Enabling/disabling the side selector.
     */
    var isSideSelectorEnabled: Boolean
        get() = mIsSideSelectorEnabled
        set(sideSelectorEnabled) {
            mIsSideSelectorEnabled = sideSelectorEnabled
            invalidate()
            requestLayout()
        }
    /**
     * Check if the selector is enabled or not.
     *
     * @return True if the selector is enabled.
     */
    /**
     * Set the option view selector enabled.
     * By default a little white circle in front of the option text.
     *
     * @param selectorEnabled Either true or false. Enabling/disabling the selector.
     */
    var isSelectorEnabled: Boolean
        get() = mIsSelectorEnabled
        set(selectorEnabled) {
            mIsSelectorEnabled = selectorEnabled
            invalidate()
            requestLayout()
        }

    /**
     * Set the option view as selected.
     * Using the wishes of the programmer.
     * By default only makes the option text white.
     *
     * @param selected Either true or false. Setting the option view as selected/unselected.
     */
    override fun setSelected(selected: Boolean) {

    }

    /**
     * Check if the option view is selected or not.
     *
     * @return True if the option view is selected.
     */
    override fun isSelected(): Boolean {
        return mOptionViewHolder!!.tvOptionText.alpha == ALPHA_CHECKED
    }

    /**
     * Binds the option view with it's content
     *
     * @param optionText Text to show as option in the menu.
     */
    fun bind(optionText: String?) {
        mOptionViewHolder!!.tvOptionText.text = optionText
        mOptionViewHolder!!.tvOptionText.alpha = ALPHA_UNCHECKED
    }

    /**
     * Binds the option view with it's content
     *
     * @param optionText       Text to show as option in the menu.
     * @param selectorDrawable Selector to show when option is selected.
     * Set to "null" to use it's default.
     * By default it shows a white circle.
     */
    fun bind(optionText: String?, selectorDrawable: Drawable?) {
        mOptionViewHolder!!.tvOptionText.text = optionText
        mOptionViewHolder!!.tvOptionText.alpha = ALPHA_UNCHECKED
        isSelectorEnabled = true
    }

    /**
     * Binds the option view with it's content
     *
     * @param optionText           Text to show as option in the menu.
     * @param selectorDrawable     Selector to show when option is selected.
     * Set to "null" to use it's default.
     * By default it shows a white circle.
     * @param selectorSideDrawable Side selector to show when option is selected.
     * Set to "null" to use it's default.
     * By default it shows a red rectangle.
     */
    fun bind(optionText: String?, selectorDrawable: Drawable?, selectorSideDrawable: Drawable?) {
        mOptionViewHolder!!.tvOptionText.text = optionText
        mOptionViewHolder!!.tvOptionText.alpha = ALPHA_UNCHECKED
        isSelectorEnabled = true
        isSideSelectorEnabled = true
    }

    /**
     * Binds the option view with it's content
     *
     * @param optionText           Text to show as option in the menu.
     * @param selectorDrawable     Selector to show when option is selected.
     * Set to "null" to use it's default.
     * By default it shows a white circle.
     * @param selectorSideDrawable Side selector to show when option is selected.
     * Set to "null" to use it's default.
     * By default it shows a red rectangle.
     */
    fun bind(
        position: Int?,
        optionText: String?,
        optionImg: Int?,
        selectorDrawable: Drawable?,
        selectorSideDrawable: Drawable?,
        profileTextVisibility: Int,
        viewVisibility: Int,
        layoutVisibility: Int,
        imageViewVisibility: Int,
        typeFace: Int,
        textColor: Int,
        first_chr: String,
        profiletxt: String

    ) {
        mOptionViewHolder!!.tvOptionText.text = optionText
        mOptionViewHolder!!.ivIcon.setImageResource(optionImg!!)
        mOptionViewHolder!!.tvOptionText.setTypeface(null, typeFace)
        mOptionViewHolder!!.tvOptionText.setTextColor(textColor)
        mOptionViewHolder!!.profileTv.visibility = profileTextVisibility
        mOptionViewHolder!!.iconUnderlinedView.visibility = viewVisibility
        mOptionViewHolder!!.rl_Name.visibility = layoutVisibility
        mOptionViewHolder!!.ivIcon.visibility = imageViewVisibility
        mOptionViewHolder!!.tv_name.text = first_chr.toUpperCase()
        isSelectorEnabled = true
        isSideSelectorEnabled = true
        mOptionViewHolder!!.profileTv.text = profiletxt
    }

    /**
     * View holder that holds the views for this layout.
     */
    private inner class OptionViewHolder internal constructor(rootView: ViewGroup) {

        val ivIcon: ImageView
        val iconUnderlinedView: View
        val tvOptionText: TextView
        val profileTv: TextView
        val tv_name: TextView
        val rl_Name: RelativeLayout

        /**
         * By default both selectors are disabled.
         */


        init {
            ivIcon = rootView.findViewById<View>(R.id.ivIcon) as ImageView
            iconUnderlinedView = rootView.findViewById(R.id.iconUnderlinedView)
            tvOptionText = rootView.findViewById(R.id.tvOptionText)
            profileTv = rootView.findViewById(R.id.profileTv)
            tv_name = rootView.findViewById(R.id.tv_name)
            rl_Name = rootView.findViewById(R.id.rl_Name)
        }
    }

    companion object {
        private const val ALPHA_CHECKED = 1f
        private const val ALPHA_UNCHECKED = 0.5f
    }

    init {
        initialize()
    }
}