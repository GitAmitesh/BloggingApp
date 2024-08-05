package com.example.blogging_app.model

data class ThreadModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val image: String = "",
    val userId: String = "",
    val timeStamp: String = "",
    val likes: Map<String, Boolean> = emptyMap() // userId as key, liked status as value
)