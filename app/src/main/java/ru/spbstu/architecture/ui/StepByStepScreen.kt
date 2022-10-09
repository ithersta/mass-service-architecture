package ru.spbstu.architecture.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
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
import ru.spbstu.architecture.simulation.Event
import ru.spbstu.architecture.simulation.EventCalendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StepByStepScreen(
    config: Config,
    viewModel: StepByStepViewModel = viewModel { StepByStepViewModel(config) }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пошаговая симуляция") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val state by viewModel.state.collectAsState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), contentPadding = PaddingValues(16.dp)
            ) {
                sourcesTable(state.sources)
                item { Spacer(modifier = Modifier.size(24.dp)) }
                devicesTable(state.devices)
                item { Spacer(modifier = Modifier.size(24.dp)) }
                bufferTable(state.buffer)
            }
            Button(
                onClick = viewModel::step, modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End)
            ) {
                Text(text = "Сделать шаг")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.sourcesTable(sources: List<EventCalendar.SourceRow>) {
    val indexWeight = 0.3f
    val otherWeights = 1f
    stickyHeader {
        Column(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 16.dp)) {
            Text(text = "Источники", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProvideTextStyle(value = MaterialTheme.typography.titleSmall) {
                    Spacer(modifier = Modifier.weight(indexWeight))
                    Text("Время", modifier = Modifier.weight(otherWeights))
                    Text("Заявки", modifier = Modifier.weight(otherWeights))
                    Text("Отказы", modifier = Modifier.weight(otherWeights))
                }
            }
        }
    }
    items(sources) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("И${it.index}", modifier = Modifier.weight(indexWeight))
            Text(
                it.time?.toString() ?: "–",
                modifier = Modifier.weight(otherWeights),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(it.requestCount.toString(), modifier = Modifier.weight(otherWeights))
            Text(it.deniedRequestCount.toString(), modifier = Modifier.weight(otherWeights))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.devicesTable(sources: List<EventCalendar.DeviceRow>) {
    val indexWeight = 0.3f
    val otherWeights = 1f
    stickyHeader {
        Column(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 16.dp)) {
            Text(text = "Приборы", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProvideTextStyle(value = MaterialTheme.typography.titleSmall) {
                    Spacer(modifier = Modifier.weight(indexWeight))
                    Text("Время", modifier = Modifier.weight(otherWeights))
                    Text("Свободен", modifier = Modifier.weight(otherWeights))
                    Text("Заявки", modifier = Modifier.weight(otherWeights))
                }
            }
        }
    }
    items(sources) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("П${it.index}", modifier = Modifier.weight(indexWeight))
            Text(
                it.time?.toString() ?: "–",
                modifier = Modifier.weight(otherWeights),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = if (it.isFree) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .weight(otherWeights)
                    .height(16.dp)
            )
            Text(it.requestCount.toString(), modifier = Modifier.weight(otherWeights))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.bufferTable(sources: List<EventCalendar.BufferRow>) {
    val indexWeight = 0.3f
    val otherWeights = 1f
    stickyHeader {
        Column(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 16.dp)) {
            Text(text = "Буфер", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProvideTextStyle(value = MaterialTheme.typography.titleSmall) {
                    Spacer(modifier = Modifier.weight(indexWeight))
                    Text("Время", modifier = Modifier.weight(otherWeights))
                    Text("Источник", modifier = Modifier.weight(otherWeights))
                }
            }
        }
    }
    items(sources) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(it.index.toString(), modifier = Modifier.weight(indexWeight))
            Text(
                it.time?.toString() ?: "–",
                modifier = Modifier.weight(otherWeights),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(it.sourceIndex?.toString() ?: "–", modifier = Modifier.weight(otherWeights))
        }
    }
}
