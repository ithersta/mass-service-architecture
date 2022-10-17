package ru.spbstu.architecture.ui

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import ru.spbstu.architecture.simulation.Simulator
import ru.spbstu.architecture.simulation.Table

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AutoScreen(
    config: Simulator.Config,
    viewModel: AutoViewModel = viewModel { AutoViewModel(config) }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Автоматическая симуляция") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val state by viewModel.state.collectAsState()
            state?.let { table ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp)
                ) {
                    sourcesTable(table.sources)
                    item { Spacer(modifier = Modifier.size(24.dp)) }
                    devicesTable(table.devices)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.sourcesTable(sources: List<Table.SourceRow>) {
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
                    Heading("№")
                    Heading("Заявок")
                    Heading("Pотк")
                    Heading("Tпреб")
                    Heading("Tбп")
                    Heading("Tобсл")
                    Heading("Dбп")
                    Heading("Dобсл")
                }
            }
        }
    }
    items(sources) { row ->
        Row(modifier = Modifier.fillMaxWidth()) {
            Cell("И${row.index}")
            IntCell(row.requestCount)
            DoubleCell(row.denyProbability)
            DoubleCell(row.averageTimeSpent)
            DoubleCell(row.averageWaitingTime)
            DoubleCell(row.averageProcessingTime)
            DoubleCell(row.waitingTimeVariance)
            DoubleCell(row.processingTimeVariance)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.devicesTable(sources: List<Table.DeviceRow>) {
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
                    Heading("№", weight = 0.2f)
                    Heading("Коэффициент использования")
                }
            }
        }
    }
    items(sources) { row ->
        Row(modifier = Modifier.fillMaxWidth()) {
            Cell("П${row.index}", weight = 0.2f)
            DoubleCell(value = row.utilization)
        }
    }
}

@Composable
private fun RowScope.Heading(value: String, weight: Float = 1f) {
    Text(
        value,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
private fun RowScope.Cell(value: String, weight: Float = 1f) {
    Text(
        value,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
private fun RowScope.IntCell(value: Int, weight: Float = 1f) {
    Text(
        value.toString(),
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
private fun RowScope.DoubleCell(value: Double?, format: String = "%.3f", weight: Float = 1f) {
    Text(
        value?.let { format.format(value) } ?: "–",
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelSmall
    )
}