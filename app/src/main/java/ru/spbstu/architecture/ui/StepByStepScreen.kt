package ru.spbstu.architecture.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.spbstu.architecture.simulation.Config
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StepByStepScreen(config: Config, viewModel: StepByStepViewModel = viewModel { StepByStepViewModel(config) }) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пошаговая симуляция") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            val state by viewModel.state.collectAsState()
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .weight(1f), contentPadding = PaddingValues(16.dp)
            ) {
                val indexWeight = 0.3f
                val otherWeights = 1f
                stickyHeader {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Text(text = "Источники", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.size(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.weight(indexWeight))
                            Text("Время", modifier = Modifier.weight(otherWeights))
                            Text("Заявки", modifier = Modifier.weight(otherWeights))
                            Text("Отказы", modifier = Modifier.weight(otherWeights))
                        }
                    }
                }
                items(state.sources) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("И${it.index}", modifier = Modifier.weight(indexWeight))
                        Text(it.time?.toString() ?: "–", modifier = Modifier.weight(otherWeights), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(it.requestCount.toString(), modifier = Modifier.weight(otherWeights))
                        Text(it.deniedRequestCount.toString(), modifier = Modifier.weight(otherWeights))
                    }
                }
            }
            Button(onClick = viewModel::step, modifier = Modifier.padding(16.dp).align(Alignment.End)) {
                Text(text = "Сделать шаг")
            }
        }
    }
}
