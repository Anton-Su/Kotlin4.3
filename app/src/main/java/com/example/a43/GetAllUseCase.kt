package com.example.a43

class GetAllUseCase(private val repository: Repository) {
    suspend operator fun invoke(): List<RepositoryItem> {
        return repository.getAll()
    }
}