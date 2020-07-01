package com.shoppersdeck.nasapicoftheday.data.Factory

import com.google.gson.GsonBuilder
import com.shoppersdeck.nasapicoftheday.data.network.NetworkService
import com.shoppersdeck.nasapicoftheday.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
    val getClient: NetworkService
        get() {

            val gson = GsonBuilder()
                .setLenient()
                .create()
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(NetworkService::class.java)

        }
    }