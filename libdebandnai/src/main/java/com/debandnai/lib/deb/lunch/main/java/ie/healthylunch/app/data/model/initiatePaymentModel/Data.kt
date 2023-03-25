package ie.healthylunch.app.data.model.initiatePaymentModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("stripe_key")
                val stripeKey: String = "",
                @SerializedName("enc_stripe_secret")
                val encStripeSecret: String = "",
                @SerializedName("stripe_secret")
                val stripeSecret: String = "",
                @SerializedName("client_secret")
                val clientSecret: String = "",
                @SerializedName("enc_stripe_key")
                val encStripeKey: String = "")