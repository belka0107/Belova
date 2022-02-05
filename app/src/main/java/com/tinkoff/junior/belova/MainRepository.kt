package com.tinkoff.junior.belova

class MainRepository constructor(private val retrofitService: RetrofitService) {
    suspend fun getRandom() = retrofitService.getRandom()
}