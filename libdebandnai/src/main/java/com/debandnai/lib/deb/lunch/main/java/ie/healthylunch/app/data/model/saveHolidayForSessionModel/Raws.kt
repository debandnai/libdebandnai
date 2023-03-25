package ie.healthylunch.app.data.model.saveHolidayForSessionModel
import ie.healthylunch.app.data.model.baseModel.Publish

data class Raws(
    val `data`: List<Any>,
    val publish: Publish,
    var success_message: String
)