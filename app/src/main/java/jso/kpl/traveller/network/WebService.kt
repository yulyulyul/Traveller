package jso.kpl.traveller.network

import jso.kpl.traveller.App
import jso.kpl.traveller.R
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Retrofit 설정
 */
object WebService
{
    private lateinit var interceptor: HttpLoggingInterceptor
    private lateinit var okHttpClient: OkHttpClient
    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {
            interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
//                .connectionSpecs(
//                    Arrays.asList(
//                        ConnectionSpec.MODERN_TLS,
//                        ConnectionSpec.COMPATIBLE_TLS))
//                .followRedirects(true)
//                .followSslRedirects(true)
//                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)

                .writeTimeout(20, TimeUnit.SECONDS)
                .cache(null)
                .build()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(App.INSTANCE.getString(R.string.server_ip_port))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retrofit!!
        }
}