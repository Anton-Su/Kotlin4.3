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
        setContent {
            _43Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        context = this
                    )
                }
            }
        }
    }
}


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

suspend fun searchByName(NameFile: String, prefic: String, context: ComponentActivity): List<Repository> {
    // переключение потока, возвращает последнее действие
    return withContext(Dispatchers.IO){
        delay(1000L)
        val jsonRepository = context.assets.open(NameFile).bufferedReader().use { it.readText() }
        if (prefic.isEmpty())
            emptyList()
        else
            Json.decodeFromString<List<Repository>>(jsonRepository).filter { it.name.contains(prefic) }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, context: ComponentActivity) {
    val jsonName = "github_repos.json"
    val scope = rememberCoroutineScope()
    val textState = remember { mutableStateOf("repo-2") }
    val reposState = remember { mutableStateOf<List<Repository>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        reposState.value = searchByName(NameFile = jsonName, prefic = textState.value, context = context)
        isLoading.value = false
    }
    val debouncedFun = remember {
        scope.debounce<String>(1000L) { input ->
            isLoading.value = true
            reposState.value = searchByName(NameFile = jsonName, prefic = input, context = context)
            isLoading.value = false
        }
    }
    Column(modifier = Modifier.padding(top = 60.dp, start = 16.dp, end = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()){
            TextField(
                value = textState.value,
                onValueChange = {
                    textState.value = it
                    debouncedFun(it) },
                modifier = Modifier.weight(1f)
            )
            if (isLoading.value) {
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