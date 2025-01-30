package com.example.mangashelf.di

import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.mangashelf.data.local.dao.MangaDao
import com.example.mangashelf.data.local.MangaDatabase
import com.example.mangashelf.data.remote.MangaRemoteApi
import com.example.mangashelf.data.repository.MangaRepository
import com.example.mangashelf.util.MangaShelfConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MangaShelfModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): MangaDatabase {
        return Room.databaseBuilder(
            context.applicationContext, MangaDatabase::class.java, MangaShelfConstants.APP_DB
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMangaDao(database: MangaDatabase): MangaDao {
        return database.mangaDao()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit (@ApplicationContext context: Context): MangaRemoteApi {
        return Retrofit.Builder().baseUrl("https://www.jsonkeeper.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(context))
            .build()
            .create(MangaRemoteApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMangaRepository(
        mangaRemoteApi: MangaRemoteApi, mangaDao: MangaDao
    ): MangaRepository {
        return MangaRepository(mangaRemoteApi, mangaDao)
    }
}