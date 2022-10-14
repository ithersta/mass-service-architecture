package ru.spbstu.architecture.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import ru.spbstu.architecture.simulation.EventCalendar
import ru.spbstu.architecture.simulation.Simulator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Destination
@Composable
fun StepByStepScreen(
    config: Simulator.Config,
    viewModel: StepByStepViewModel = viewModel { StepByStepViewModel(config) }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пошаговая симуляция") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val state by viewModel.state.collectAsState()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp)
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
                    .align(Alignment.BottomEnd)
            ) {
                Text(text = "Сделать шаг")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
private fun LazyListScope.sourcesTable(sources: List<EventCalendar.SourceRow>) {
    val indexWeight = 0.3f
    val otherWeights = 1f
    stickyHeader {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Источники", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProvideTextStyle(value = MaterialTheme.typography.titleSmall) {
                    Text("№", modifier = Modifier.weight(indexWeight))
                    Text("Время", modifier = Modifier.weight(otherWeights))
                    Text("Заявок", modifier = Modifier.weight(otherWeights))
                    Text("Отказов", modifier = Modifier.weight(otherWeights))
                }
            }
        }
    }
    items(sources) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("И${it.index}", modifier = Modifier.weight(indexWeight))
            TimeCell(it.time, otherWeights)
            AnimatedTableCell(text = it.requestCount.toString(), weight = otherWeights)
            AnimatedTableCell(text = it.deniedRequestCount.toString(), weight = otherWeights)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.devicesTable(sources: List<EventCalendar.DeviceRow>) {
    val indexWeight = 0.3f
    val otherWeights = 1f
    stickyHeader {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Приборы", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProvideTextStyle(value = MaterialTheme.typography.titleSmall) {
                    Text("№", modifier = Modifier.weight(indexWeight))
                    Text("Время", modifier = Modifier.weight(otherWeights))
                    Text("Свободен", modifier = Modifier.weight(otherWeights))
                    Text("Заявок обр.", modifier = Modifier.weight(otherWeights))
                }
            }
        }
    }
    items(sources) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("П${it.index}", modifier = Modifier.weight(indexWeight))
            TimeCell(it.time, otherWeights)
            Icon(
                imageVector = if (it.isFree) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .weight(otherWeights)
                    .height(16.dp)
            )
            AnimatedTableCell(it.requestCount.toString(), otherWeights)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.bufferTable(sources: List<EventCalendar.BufferRow>) {
    val indexWeight = 0.3f
    val otherWeights = 1f
    stickyHeader {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Буфер", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProvideTextStyle(value = MaterialTheme.typography.titleSmall) {
                    Text("№", modifier = Modifier.weight(indexWeight))
                    Text("Время", modifier = Modifier.weight(otherWeights))
                    Text("Источник", modifier = Modifier.weight(otherWeights))
                }
            }
        }
    }
    items(sources) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(it.index.toString(), modifier = Modifier.weight(indexWeight))
            TimeCell(it.time, otherWeights)
            AnimatedTableCell(it.sourceIndex?.toString() ?: "–", otherWeights)
        }
    }
}

@Composable
private fun RowScope.TimeCell(time: Double?, weight: Float) {
    AnimatedTableCell(text = time?.let { "%.4f".format(it) } ?: "–", weight = weight)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RowScope.AnimatedTableCell(text: String, weight: Float) {
    AnimatedContent(targetState = text, modifier = Modifier.weight(weight), transitionSpec = {
        fadeIn() + slideInVertically { -it / 2 } with fadeOut() + slideOutVertically { it / 2 }
    }) {
        Text(text = it)
    }
}
