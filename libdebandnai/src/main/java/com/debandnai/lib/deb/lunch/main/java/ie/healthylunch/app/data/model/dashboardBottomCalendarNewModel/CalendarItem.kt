package ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel

data class CalendarItem(
    val dayText: String,
    val isFree: Boolean = false,
    val isPending: Boolean = false,
    val isDelivered: Boolean = false,
    val isFutureDate: Boolean = false,
)