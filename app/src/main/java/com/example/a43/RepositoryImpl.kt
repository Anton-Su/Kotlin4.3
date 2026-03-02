package com.example.a43

import kotlinx.coroutines.delay


class RepositoryImpl(private val dataSource: JsonDataSource) : Repository {
    private var repos: List<RepositoryItem>? = null
    private suspend fun ensureLoaded() {
        if (repos == null) {
            repos = dataSource.getReposFromJson()
        }
    }
    override suspend fun getAll(): List<RepositoryItem> {
        ensureLoaded()
        delay(1000)
        return repos ?: emptyList()
    }
    override suspend fun searchByName(prefix: String): List<RepositoryItem> {
        // ensureLoaded() по факту не нужен
        delay(1500)
        if (prefix.isEmpty())
            return emptyList()
        return repos!!.filter { it.name.contains(prefix) }
    }
}
