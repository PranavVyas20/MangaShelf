package com.example.mangashelf.data.repository

import com.example.mangashelf.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

interface BaseRepository {
    suspend fun <T> getFlowResult(call: suspend () -> Response<T>): Flow<NetworkResponse<T>> =
        flow {
            emit(NetworkResponse.Loading())
            try {
                val response = call()
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        emit(NetworkResponse.Success(body))
                    }
                } else {
                    emit (NetworkResponse.Error(response.message().toString()))
                }
            } catch (e: Exception) {
                emit(NetworkResponse.Error(e.message.toString()))
            }
        }
}