package ie.healthylunch.app.data.network

import ie.healthylunch.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource {

    companion object {

        //Development base url
        private const val BASE_URL = "https://app-api-v3.ftdev2.com/v5/"
        //Staging base url
        //private const val BASE_URL = "https://stage-app-api-v3.thelunchbag.ie/v5/"

        //Live base url
        //private const val BASE_URL = "https://app-api-v3.thelunchbag.ie/v5/"

    }

    fun <Api> buildApi(api: Class<Api>): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)

                }
                client.connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(api)
    }

}