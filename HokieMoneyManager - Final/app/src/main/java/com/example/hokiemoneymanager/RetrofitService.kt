package edu.vt.cs3714.retrofitrecyclerviewguide

import com.example.hokiemoneymanager.Currency
import io.reactivex.Observable
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The RetrofitService handles the API requests
 *
 */
interface RetrofitService {
    @GET("latest")
    fun getCurrencyExchangeRate(
        @Query("access_key") apiKey: String
    ): Observable<Currency>

    companion object {
        fun create(baseUrl: String): RetrofitService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            .create()
                    )
                )
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(RetrofitService::class.java)
        }
    }
}