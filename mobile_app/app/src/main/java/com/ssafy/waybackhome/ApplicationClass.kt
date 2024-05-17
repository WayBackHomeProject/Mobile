package com.ssafy.waybackhome

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.waybackhome.data.LocalRepository
import com.ssafy.waybackhome.data.geo.GeoHeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    fun initAPI(){

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val defaultClient = OkHttpClient().newBuilder()
            .readTimeout(5_000, TimeUnit.MILLISECONDS)
            .connectTimeout(5_000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) // interceptor enables log of http transitions
            //.addInterceptor(HeaderInterceptor()) // interceptor for adding auth header
            .build()

        val naverGeoClient = OkHttpClient().newBuilder()
            .readTimeout(5_000, TimeUnit.MILLISECONDS)
            .connectTimeout(5_000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) // interceptor enables log of http transitions
            .addInterceptor(GeoHeaderInterceptor())
            .build()

        serverAPI = initializeAPI(gson, defaultClient, BuildConfig.SERVER_URL)
        naverAPI = initializeAPI(gson, naverGeoClient,"https://naveropenapi.apigw.ntruss.com/map-geocode/v2/")
    }
    override fun onCreate() {
        super.onCreate()
        LocalRepository.initialize(this)
        initAPI()
    }
    companion object{
        lateinit var naverAPI : Retrofit
        lateinit var serverAPI : Retrofit

        fun initializeAPI(gson : Gson, client : OkHttpClient, baseUrl : String) : Retrofit{

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }
}