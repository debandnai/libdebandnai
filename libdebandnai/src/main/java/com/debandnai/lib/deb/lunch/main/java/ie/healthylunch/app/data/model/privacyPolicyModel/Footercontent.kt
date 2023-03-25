package ie.healthylunch.app.data.model.privacyPolicyModel

import com.google.gson.annotations.SerializedName

data class Footercontent(@SerializedName("meta_description")
                         val metaDescription: String = "",
                         @SerializedName("meta_title")
                         val metaTitle: String = "",
                         @SerializedName("meta_keywords")
                         val metaKeywords: String = "",
                         @SerializedName("slug")
                         val slug: String = "",
                         @SerializedName("content")
                         val content: String = "",
                         @SerializedName("pagename")
                         val pagename: String = "")