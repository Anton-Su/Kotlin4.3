package com.example.a43

class SearchByNameUseCase(private val repository: Repository) {
    suspend operator fun invoke(prefix: String): List<RepositoryItem> {
        return repository.searchByName(prefix)
    }
}