package com.example.a43

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


fun <T> CoroutineScope.debounce(waitMs: Long = 500L, destinationFunction: suspend(T) -> Unit): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}


class ViewModel(private val getAll: GetAllUseCase, private val getAllWithPrefix: SearchByNameUseCase): ViewModel() {
    private val _repos = MutableStateFlow<List<RepositoryItem>>(emptyList())
    val repos = _repos.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val debouncedSearch = viewModelScope.debounce<String>(1000L) { query ->
        _isLoading.value = false
        _repos.value = getAllWithPrefix(query)
        _isLoading.value = true
    }
    init {
        loadAll()
    }
    private fun loadAll() {
        viewModelScope.launch {
            _isLoading.value = false
            _repos.value = getAll()
            _isLoading.value = true
        }
    }

    fun searchByPrefix(prefix: String) {
        viewModelScope.launch {
            debouncedSearch(prefix)
        }
    }

}

