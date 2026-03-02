package com.example.a43

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class JsonDataSource(private val context: Context)  {
    suspend fun getReposFromJson(): List<RepositoryItem> {
        return withContext(Dispatchers.IO) {
            delay(1000L)
            val json = context.assets.open("github_repos.json").bufferedReader().use { it.readText() }
            Json.decodeFromString<List<RepositoryItem>>(json)
        }
    }
}