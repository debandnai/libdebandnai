package ie.healthylunch.app.adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ie.healthylunch.app.utils.sideMenu.views.SlideMenuModelClass
import ie.healthylunch.app.utils.sideMenu.views.SlideMenuOptionView
import ie.healthylunch.app.ui.DashBoardActivity


internal class MenuAdapter(
    private var activity: Activity,
    var options: ArrayList<SlideMenuModelClass>,
) : BaseAdapter() {

    private val mOptionViews: ArrayList<SlideMenuOptionView> = ArrayList()
    private var firstChar: String = ""


    override fun getCount(): Int {
        return options.size
    }

    override fun getItem(position: Int): Any {
        return options[position]
    }

    fun setViewSelected(position: Int, selected: Boolean) {

        // Looping through the options in the menu
        // Selecting the chosen option
        for (i in 0 until mOptionViews.size) {
            if (i == position) {
                mOptionViews[i].isSelected = selected
            } else {
                mOptionViews[i].isSelected = !selected
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        val option: String = options[position].menuName
        val firstName: String = options[0].menuName
        firstChar = firstName[0].toString()
        val optionImg: Int = options[position].menuImage
        val profileTextVisibility =
            if (position == 0 && (activity as DashBoardActivity).drawerFirstView) View.VISIBLE else View.INVISIBLE

        val viewVisibility =
            if (position == 0 || position == 9 || (!(activity as DashBoardActivity).drawerFirstView && position == 2)) View.INVISIBLE else View.VISIBLE
        val layoutVisibility =
            if (position == 0 && (activity as DashBoardActivity).drawerFirstView) View.VISIBLE else View.INVISIBLE
        val imageViewVisibility =
            if (position == 0 && (activity as DashBoardActivity).drawerFirstView) View.INVISIBLE else View.VISIBLE
        val textStyle = if (position == 0) Typeface.NORMAL else Typeface.NORMAL
        val textColor =
            if (position == 0) Color.parseColor("#000000") else Color.parseColor("#000000")

        val optionView: SlideMenuOptionView = if (convertView == null) {
            SlideMenuOptionView(parent.context)
        } else {
            convertView as SlideMenuOptionView
        }


        // Using the DuoOptionView's default selectors
        optionView.bind(
            position,
            option,
            optionImg,
            null,
            null,
            profileTextVisibility,
            viewVisibility,
            layoutVisibility,
            imageViewVisibility,
            textStyle,
            textColor,
            firstChar,
            "Profile"
        )

        // Adding the views to an array list to handle view selection
        mOptionViews.add(optionView)

        return optionView
    }

}