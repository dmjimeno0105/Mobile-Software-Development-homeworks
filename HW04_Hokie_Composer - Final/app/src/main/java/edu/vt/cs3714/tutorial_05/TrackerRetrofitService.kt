package edu.vt.cs3714.tutorial_05

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TrackerRetrofitService {
    @POST(value = MainActivity.ROUTE)
    @Headers("Content-Type: application/json")
    fun postLog(@Body json: JSONObject): Call<ResponseBody>

    companion object {
        fun create(baseUrl: String): TrackerRetrofitService {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .build()

            return retrofit.create(TrackerRetrofitService::class.java)
        }
    }
}