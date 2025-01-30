package com.example.mangashelf.domain.model

enum class MangaSortingOrder(val key: String) {
    SCORE(key = "By Score"),
    PUBLISHED_YEAR(key = "By Date"),
    POPULARITY(key = "By Popularity")
}
