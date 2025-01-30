package com.example.mangashelf.domain.model

import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class MangaNetworkResponse(
    val dbId: Int,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("image")
    val imageUrl: String? = null,
    @SerializedName("score")
    val score: Float? = null,
    @SerializedName("popularity")
    val popularity: Long? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("publishedChapterDate")
    val publishedDateEpoch: Long? = null,
    @SerializedName("category")
    val category: String? = null,
    val isFavourite: Boolean = false,
    val publishedYear: Int
) {
    fun getPublishedDate(): LocalDateTime {
        val instant = Instant.fromEpochSeconds(publishedDateEpoch ?: 0)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun getMangaScore(): Float {
        val a = "%.1f".format(score).toFloat()
        Log.d("manga_score_tagg", "$a")
        return a
    }

}

fun MangaNetworkResponse.toMangaDto() = MangaDto(
    id = this.id.orEmpty(),
    imageUrl = this.imageUrl.orEmpty(),
    score = this.getMangaScore(),
    title = this.title.orEmpty(),
    category = this.category.orEmpty(),
    publishedYear = this.getPublishedDate().year,
    isFavourite = this.isFavourite,
    popularity = this.popularity ?: 0L
)
