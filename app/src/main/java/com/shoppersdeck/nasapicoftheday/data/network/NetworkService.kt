package com.shoppersdeck.nasapicoftheday.data.network

import com.shoppersdeck.nasapicoftheday.data.models.DataResponse
import com.shoppersdeck.nasapicoftheday.utils.Constants
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET(Constants.GET_IMG_OR_VDO)
    fun getData(
        @Query("api_key") api_key: String,
        @Query("date") date: String
    ): Call<DataResponse>
}