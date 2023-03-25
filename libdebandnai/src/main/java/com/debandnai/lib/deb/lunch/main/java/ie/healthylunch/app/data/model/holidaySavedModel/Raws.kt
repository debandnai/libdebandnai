package ie.healthylunch.app.data.model.holidaySavedModel
import ie.healthylunch.app.data.model.baseModel.Publish

data class Raws(
    val `data`: List<Any>,
    val publish: Publish,
    val success_message: String
)