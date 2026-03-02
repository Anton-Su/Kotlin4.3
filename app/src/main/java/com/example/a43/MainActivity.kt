package com.example.a43

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a43.ui.theme._43Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val jsonDataSource = JsonDataSource(this)
        val repository = RepositoryImpl(jsonDataSource)
        val getTodosUseCase = GetAllUseCase(repository)
        val toggleTodoUseCase = SearchByNameUseCase(repository)
        val viewModel = ViewModel(getTodosUseCase, toggleTodoUseCase)
        setContent {
            _43Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        viewModel,
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting(modifier: Modifier = Modifier, viewModel: ViewModel) {
    val reposState = viewModel.repos.collectAsState()
    val isLoadingState = viewModel.isLoading.collectAsState()
    val textState = remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(top = 60.dp, start = 16.dp, end = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()){
            TextField(
                value = textState.value,
                onValueChange = {
                    textState.value = it
                    viewModel.searchByPrefix(it) },
                modifier = Modifier.weight(1f)
            )
            if (isLoadingState.value.not()) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp)
                )
            }
            else {
                Text(text = "", modifier = Modifier.size(40.dp))
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            items(reposState.value.size) { index ->
                Column {
                    Text(text = "Name: ${reposState.value[index].name}")
                    Text(text = "Description: ${reposState.value[index].description}")
                    Text(text = "Stars: ${reposState.value[index].stargazersCount}")
                    Text(text = "Language: ${reposState.value[index].language}")
                }
            }
        }
    }
}