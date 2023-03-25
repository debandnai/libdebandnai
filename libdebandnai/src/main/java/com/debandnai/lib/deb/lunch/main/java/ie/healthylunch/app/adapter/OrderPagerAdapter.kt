package ie.healthylunch.app.adapter


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.dashBoardViewResponseModel.DashboardListItem
import ie.healthylunch.app.databinding.OrderItemBinding
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_OFF
import ie.healthylunch.app.utils.Constants.Companion.DASHBOARD_ID_VALUE
import ie.healthylunch.app.utils.Constants.Companion.FAVORITES_LIST
import ie.healthylunch.app.utils.Constants.Companion.HOLIDAY_STATUS
import ie.healthylunch.app.utils.Constants.Companion.ORDER_CLEAR
import ie.healthylunch.app.utils.Constants.Companion.ORDER_VALUE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.YES
import java.util.*


class OrderPagerAdapter(

    var orderList: List<DashboardListItem?>?,
    var schoolType: Int?,
    var listener: AdapterItemOnclickListener
) :
    RecyclerView.Adapter<OrderPagerAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {


        val inflater = LayoutInflater.from(parent.context)
        val listItem = OrderItemBinding.inflate(inflater, parent, false)
        return RecyclerViewHolder(listItem)
    }

    override fun getItemCount(): Int = orderList?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return R.layout.order_item
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(orderList?.get(position), position)
    }

    inner class RecyclerViewHolder(val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        fun bind(item: DashboardListItem?, position: Int) {
            binding.dataItem = item
            binding.executePendingBindings()
            item?.isRepeatOrderAndHasOrder = false
            //set alpha in cover View for all conditions
            //binding.coverViewLayout.alpha = 1F
            //set visibility at the ques mark icon for all conditions
            //binding.quesIv.isVisible = true
            //set visibility at the sugar Calorie CardView for all conditions
            binding.llSugarCalorie.visibility = View.VISIBLE
            //binding.calendarOffView.visibility = View.GONE

            binding.productNameTv.text =
                item?.productName.toString().replace("[", "", true).replace("]", "", true)
            binding.tvProductName2.text =
                item?.productName.toString().replace("[", "", true).replace("]", "", true)


            binding.llTenPerOrderLayout.isVisible = true


            /*binding.productNameTv.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.font_green3
                )
            )*/

            if (item?.dayOff.equals(YES, true)) {
                //create new order layout, no order textview
                //        binding.rlCreateNewOrder.isVisible = false
                binding.tvNoOrder.isVisible = true
                binding.imgNoOrder.isVisible = true


                //for order already placed
                if (item?.dashboardId != DASHBOARD_ID_VALUE) {
                    //ordered layout, no order layout, calendar off layout
                    binding.llOrderDetailsLayout.isVisible = true
                    //binding.llCalendarOffLayout.isVisible = false
                    binding.llNoOrderPlacedLayout.isVisible = false
                    binding.llTenPerOrderLayout.isVisible = false
                    //bottom layout
                    binding.bottomLayout.isVisible = true
                    binding.clearOrderTv.isVisible = true
                    binding.ivFavoriteImage.isVisible = true
                    binding.ivFavoriteImage2.isVisible = true

                } else {
                    //ordered layout, no order layout, calendar off layout
                    binding.llOrderDetailsLayout.isVisible = false
                    // binding.llCalendarOffLayout.isVisible = false
                    binding.llNoOrderPlacedLayout.isVisible = true
                    binding.llTenPerOrderLayout.isVisible = false
                    //bottom layout
                    binding.bottomLayout.isVisible = false
                    binding.clearOrderTv.isVisible = false
                    // binding.viewFavorites.isVisible= false
                    binding.ivFavoriteImage.isVisible = false
                    binding.ivFavoriteImage2.isVisible = false
                    binding.rlCreateNewOrder.isVisible = false
                    binding.rlFavorites.isVisible = false
                }

                //day off end
            } else if (item?.holidayStatus == HOLIDAY_STATUS) {
                //set visibility at the ques mark icon
                //binding.quesIv.isVisible = false

                //create new order layout, no order textview
                //  binding.rlCreateNewOrder.isVisible = true
                binding.tvNoOrder.isVisible = false
                binding.imgNoOrder.isVisible = false
                //binding.calendarOffView.visibility = View.GONE

                //templateNameTv text color is red only for calendar off
                /*binding.templateNameTv.setTextColor(
                    ContextCompat.getColor(
                            binding.templateNameTv.context,
                        R.color.red2
                    )
                )*/

                if (item.dashboardId != STATUS_ZERO) {
                    //set alpha in cover View
                    //binding.coverViewLayout.alpha = 0.7F
                    //binding.calendarOffView.visibility = View.VISIBLE

                    //ordered layout, no order layout, calendar off layout
                    binding.llOrderDetailsLayout.isVisible = true
                    //   binding.llCalendarOffLayout.isVisible = false
                    binding.llNoOrderPlacedLayout.isVisible = false
                    binding.llTenPerOrderLayout.isVisible = false
                    item.isRepeatOrderAndHasOrder = true
                    //bottom layout
                    binding.bottomLayout.isVisible = true
                    binding.clearOrderTv.isVisible = true
                    binding.ivFavoriteImage.isVisible = true
                    binding.ivFavoriteImage2.isVisible = true
                    //sugar Calorie CardView
                    binding.llSugarCalorie.visibility = View.VISIBLE

                    /*binding.productNameTv.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.red2
                        )
                    )*/

                } else {
                    //ordered layout, no order layout, calendar off layout
                    binding.llOrderDetailsLayout.isVisible = false
                    //  binding.llCalendarOffLayout.isVisible = false
                    binding.llNoOrderPlacedLayout.isVisible = true
                    binding.llTenPerOrderLayout.isVisible = false

                    //bottom layout
                    binding.bottomLayout.isVisible = false
                    binding.clearOrderTv.isVisible = false
                    binding.ivFavoriteImage.isVisible = false
                    binding.ivFavoriteImage2.isVisible = false
                    //    binding.viewFavorites.isVisible= true
                }

            } else if (item?.dashboardId != DASHBOARD_ID_VALUE) {
                //create new order layout, no order textview
                //    binding.rlCreateNewOrder.isVisible = true
                binding.tvNoOrder.isVisible = false
                binding.imgNoOrder.isVisible = false

                //ordered layout, no order layout, calendar off layout
                binding.llOrderDetailsLayout.isVisible = true
                //  binding.llCalendarOffLayout.isVisible = false
                binding.llNoOrderPlacedLayout.isVisible = false
                binding.llTenPerOrderLayout.isVisible = false

                //bottom layout
                binding.bottomLayout.isVisible = true
                binding.clearOrderTv.isVisible = true
                binding.ivFavoriteImage.isVisible = true
                binding.ivFavoriteImage2.isVisible = true
                //item?.isHoliday = false
            } else {
                //create new order layout, no order textview
                //   binding.rlCreateNewOrder.isVisible = true
                binding.tvNoOrder.isVisible = false
                binding.imgNoOrder.isVisible = false

                //ordered layout, no order layout, calendar off layout
                binding.llOrderDetailsLayout.isVisible = false
                //  binding.llCalendarOffLayout.isVisible = false
                //binding.llNoOrderPlacedLayout.isVisible = true

                //      binding.viewFavorites.isVisible= true
                //bottom layout
                binding.bottomLayout.isVisible = false
                binding.clearOrderTv.isVisible = false
                binding.ivFavoriteImage.isVisible = false
                binding.ivFavoriteImage2.isVisible = false

                binding.rlCreateNewOrder.isVisible = true
                binding.rlFavorites.isVisible = true

                //10% order screen
                //If ten_per_order tag not null then TenPerOrderLayout will visible.

                if (item.ten_per_order?.isNotEmpty() == true && item.ten_per_order.equals(
                        STATUS_ONE.toString(),
                        true
                    )
                ) {
                    binding.llTenPerOrderLayout.isVisible = true
                    binding.llNoOrderPlacedLayout.isVisible = false
                } else {
                    binding.llTenPerOrderLayout.isVisible = false
                    binding.llNoOrderPlacedLayout.isVisible = true
                }


                if (item.isHoliday) {
                    //binding.coverViewLayout.alpha = 0.6F
                    //binding.calendarOffView.visibility = View.VISIBLE
                }
            }


            //Show Hide Repeat Order Layout
            if (Objects.equals(
                    item?.orderType?.lowercase(),
                    Constants.REPEAT
                ) && item?.dashboardId != DASHBOARD_ID_VALUE /*&& item?.holidayStatus!=HOLIDAY_STATUS*/) {
                binding.repeatOrderTextTv.visibility = View.VISIBLE
                item?.isRepeatOrderAndHasOrder = true
                // binding.ivRepeatImage.visibility = View.VISIBLE
            } else {
                item?.isRepeatOrderAndHasOrder = false
                binding.repeatOrderTextTv.visibility = View.INVISIBLE
                //   binding.ivRepeatImage.visibility = View.GONE
            }

            binding.tvOrder.setOnClickListener {
                if (item?.isHoliday == false) {
                    listener.onAdapterItemClick(orderList, position, ORDER_VALUE)
                } else {
                    listener.onAdapterItemClick(orderList, position, CALENDAR_OFF)
                }
            }
            binding.coverViewLayout.setOnClickListener {
                if (item?.isHoliday == false) {
                    if (item.ten_per_order == null || item.ten_per_order?.equals(
                            STATUS_ONE.toString(),
                            true
                        ) == false || item?.dashboardId != DASHBOARD_ID_VALUE
                    ) {
                        listener.onAdapterItemClick(orderList, position, ORDER_VALUE)
                    }
                } else {
                    listener.onAdapterItemClick(orderList, position, CALENDAR_OFF)
                }
            }
            binding.clearOrderTv.setOnClickListener {
//                if (Objects.equals(
//                        item?.clearOrder?.lowercase(),
//                        YES
//                    ) && item?.isHoliday == false
//                ) {
//                    listener.onAdapterItemClick(orderList, position, ORDER_CLEAR)
                if (item?.isHoliday == false) {
                    if (Objects.equals(item?.clearOrder?.lowercase(), YES)) {
                        listener.onAdapterItemClick(orderList, position, ORDER_CLEAR)
                    } else {
                        listener.onAdapterItemClick(orderList, position, ORDER_VALUE)
                    }
                } else {
                    listener.onAdapterItemClick(orderList, position, CALENDAR_OFF)
                }
            }

            /*binding.quesIv.setOnClickListener {
                listener.onAdapterItemClick(orderList, position, FOOD_ITEMS)
            }*/
            binding.rlFavorites.setOnClickListener {
                if (item?.isHoliday == false) {
                    listener.onAdapterItemClick(orderList, position, FAVORITES_LIST)
                } else {
                    listener.onAdapterItemClick(orderList, position, CALENDAR_OFF)
                }
            }
            binding.ivFavoriteImage.setOnClickListener {
                if (item?.isHoliday == false) {
                    if (item?.favourite_order_id == STATUS_ZERO) {
                        listener.onAdapterItemClick(orderList, position, Constants.ADD_FAVORITES)
                    } else {
                        listener.onAdapterItemClick(orderList, position, Constants.REMOVE_FAVORITES)
                    }
                } else {
                    listener.onAdapterItemClick(orderList, position, CALENDAR_OFF)
                }

            }

            binding.ivFavoriteImage2.setOnClickListener {
                if (item?.isHoliday == false) {
                    if (item?.favourite_order_id == STATUS_ZERO) {
                        listener.onAdapterItemClick(orderList, position, Constants.ADD_FAVORITES)
                    } else {
                        listener.onAdapterItemClick(orderList, position, Constants.REMOVE_FAVORITES)
                    }
                } else {
                    listener.onAdapterItemClick(orderList, position, CALENDAR_OFF)
                }

            }
        }

    }


}