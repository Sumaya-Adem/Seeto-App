package com.example.seetoapp

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/tickers/")
    fun getCryptoData(): Call<ApiResponse>
}