package ie.healthylunch.app.utils.quickActionPopup

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import kotlin.jvm.JvmOverloads

/**
 * Action item, displayed as menu with icon and text.
 *
 * @author Lorensius. W. L. T <lorenz></lorenz>@londatiga.net>
 *
 * Contributors:
 * - Kevin Peck <kevinwpeck></kevinwpeck>@gmail.com>
 */
class ActionItem @JvmOverloads constructor(
    actionId: Int = -1,
    title: String? = null,
    product: String? = null,
    text_line: String? = null,
    icon: Drawable? = null
) {
    private var icon: Drawable?
    private var thumb: Bitmap? = null
    private var title: String?
    private var product: String?
    private var text_line: String?
    private var actionId = -1
    private var selected = false
    private var sticky = false

    /**
     * Constructor
     *
     * @param icon [Drawable] action icon
     */
    constructor(icon: Drawable?) : this(-1, null, null, null, icon) {}

    /**
     * Constructor
     *
     * @param actionId  Action ID of item
     * @param icon      [Drawable] action icon
     */
    constructor(actionId: Int, icon: Drawable?) : this(actionId, null, null, null, icon) {}

    /**
     * Set action title
     *
     * @param title action title
     */
    fun setTitle(title: String?) {
        this.title = title
    }

    /**
     * Get action title
     *
     * @return action title
     */
    fun getTitle(): String? {
        return title
    }

    fun getProduct(): String? {
        return product
    }

    fun setProduct(product: String?) {
        this.product = product
    }

    fun getText_line(): String? {
        return text_line
    }

    fun setText_line(text_line: String?) {
        this.text_line = text_line
    }

    /**
     * Set action icon
     *
     * @param icon [Drawable] action icon
     */
    fun setIcon(icon: Drawable?) {
        this.icon = icon
    }

    /**
     * Get action icon
     * @return  [Drawable] action icon
     */
    fun getIcon(): Drawable? {
        return icon
    }

    /**
     * Set action id
     *
     * @param actionId  Action id for this action
     */
    fun setActionId(actionId: Int) {
        this.actionId = actionId
    }

    /**
     * @return  Our action id
     */
    fun getActionId(): Int {
        return actionId
    }

    /**
     * Set sticky status of button
     *
     * @param sticky  true for sticky, pop up sends event but does not disappear
     */
    fun setSticky(sticky: Boolean) {
        this.sticky = sticky
    }

    /**
     * @return  true if button is sticky, menu stays visible after press
     */
    fun isSticky(): Boolean {
        return sticky
    }

    /**
     * Set selected flag;
     *
     * @param selected Flag to indicate the item is selected
     */
    fun setSelected(selected: Boolean) {
        this.selected = selected
    }

    /**
     * Check if item is selected
     *
     * @return true or false
     */
    fun isSelected(): Boolean {
        return selected
    }

    /**
     * Set thumb
     *
     * @param thumb Thumb image
     */
    fun setThumb(thumb: Bitmap?) {
        this.thumb = thumb
    }

    /**
     * Get thumb image
     *
     * @return Thumb image
     */
    fun getThumb(): Bitmap? {
        return thumb
    }
    /**
     * Constructor
     *
     * @param actionId  Action id for case statements
     * @param title     Title
     * @param icon      Icon to use
     */
    /**
     * Constructor
     */
    /**
     * Constructor
     *
     * @param actionId  Action id of the item
     * @param title     Text to show for the item
     */
    init {
        this.title = title
        this.product = product
        this.text_line = text_line
        this.icon = icon
        this.actionId = actionId
    }
}