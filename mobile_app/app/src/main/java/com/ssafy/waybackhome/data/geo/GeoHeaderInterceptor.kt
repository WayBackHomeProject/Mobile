package com.ssafy.waybackhome.data.geo

import com.ssafy.waybackhome.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class GeoHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .addHeader("X-NCP-APIGW-API-KEY-ID", BuildConfig.NAVER_API_ID)
            .addHeader("X-NCP-APIGW-API-KEY", BuildConfig.NAVER_API_KEY)
        return chain.proceed(builder.build())
    }
}