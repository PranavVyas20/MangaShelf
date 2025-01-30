package com.example.mangashelf.data.remote

import com.example.mangashelf.domain.model.MangaNetworkResponse
import retrofit2.Response
import retrofit2.http.GET

interface MangaRemoteApi {
    @GET("b/KEJO")
    suspend fun fetchMangaList(): Response<List<MangaNetworkResponse>>
}