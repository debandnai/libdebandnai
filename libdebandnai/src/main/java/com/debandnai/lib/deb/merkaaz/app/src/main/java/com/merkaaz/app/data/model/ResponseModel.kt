package com.merkaaz.app.data.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ResponseModel(
	@SerializedName("data")
	var data: JsonElement? = null,
	@SerializedName("status")
	var status: StatusModel? = StatusModel(),

	@SerializedName("publish")
	var publish: PublishModel? = PublishModel()

)



