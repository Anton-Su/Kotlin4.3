package com.example.a43

interface Repository {
    suspend fun getAll(): List<RepositoryItem>
    suspend fun searchByName(prefix: String): List<RepositoryItem>
}


