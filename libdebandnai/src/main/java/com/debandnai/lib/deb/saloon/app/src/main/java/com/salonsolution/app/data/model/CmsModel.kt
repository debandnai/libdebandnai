package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class CmsModel(
    @SerializedName("cms_id") var cmsId: Int? = null,
    @SerializedName("cms_name") var cmsName: String? = null,
    @SerializedName("cms_slug") var cmsSlug: String? = null,
    @SerializedName("meta_title") var metaTitle: String? = null,
    @SerializedName("meta_keyword") var metaKeyword: String? = null,
    @SerializedName("meta_description") var metaDescription: String? = null,
    @SerializedName("cms_content") var cmsContent: String? = null
)
