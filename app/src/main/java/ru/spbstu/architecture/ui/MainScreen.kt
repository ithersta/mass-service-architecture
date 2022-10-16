package ru.spbstu.architecture.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ru.spbstu.architecture.simulation.Plotter
import ru.spbstu.architecture.simulation.Simulator
import ru.spbstu.architecture.ui.destinations.*
import ru.spbstu.architecture.ui.utils.toDoubleRangeOrNull
import ru.spbstu.architecture.ui.utils.toIntRangeOrNull
import ru.spbstu.architecture.ui.utils.toPair

enum class Mode { StepByStep, Auto }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(navigator: DestinationsNavigator) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Параметры") }
            )
        }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            var selectedMode by rememberSaveable { mutableStateOf(Mode.StepByStep) }
            Row {
                FilterChip(
                    selected = selectedMode == Mode.StepByStep,
                    onClick = { selectedMode = Mode.StepByStep },
                    label = { Text(text = "Пошаговый") }
                )
                Spacer(modifier = Modifier.size(8.dp))
                FilterChip(
                    selected = selectedMode == Mode.Auto,
                    onClick = { selectedMode = Mode.Auto },
                    label = { Text(text = "Автоматический") }
                )
            }
            AnimatedContent(targetState = selectedMode) { mode ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (mode) {
                        Mode.StepByStep -> StepByStepConfig(
                            onStart = { navigator.navigate(StepByStepScreenDestination(it)) }
                        )

                        Mode.Auto -> StepByStepConfig(
                            onStart = { navigator.navigate(AutoScreenDestination(it)) },
                            addition = { simulatorConfig ->
                                PlotterConfig(
                                    config = simulatorConfig,
                                    onStart = { navigator.navigate(PlotsScreenDestination(it)) }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ColumnScope.StepByStepConfig(
    onStart: (Simulator.Config) -> Unit,
    addition: (@Composable (Simulator.Config?) -> Unit)? = null
) {
    var sourceCountText by rememberSaveable { mutableStateOf("6") }
    var deviceCountText by rememberSaveable { mutableStateOf("3") }
    var bufferSizeText by rememberSaveable { mutableStateOf("5") }
    var sourceIntensityText by rememberSaveable { mutableStateOf("0.5") }
    var deviceProcessingTimeText by rememberSaveable { mutableStateOf("1.0..2.0") }
    val config = run {
        Simulator.Config(
            sourceCount = sourceCountText.toIntOrNull()
                ?.takeIf { it in 1..100 } ?: return@run null,
            deviceCount = deviceCountText.toIntOrNull()
                ?.takeIf { it in 1..100 } ?: return@run null,
            bufferSize = bufferSizeText.toIntOrNull()
                ?.takeIf { it in 1..1000 } ?: return@run null,
            sourceIntensity = sourceIntensityText.toDoubleOrNull()
                ?.takeIf { it in 0.0..100.0 } ?: return@run null,
            deviceProcessingTime = deviceProcessingTimeText.toDoubleRangeOrNull()
                ?.toPair() ?: return@run null
        )
    }
    Text(text = "Источники", style = MaterialTheme.typography.titleMedium)
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = sourceCountText,
            onValueChange = { sourceCountText = it },
            label = { Text("Количество") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = sourceIntensityText,
            onValueChange = { sourceIntensityText = it },
            label = { Text("Интенсивность") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.size(8.dp))
    Text(text = "Приборы", style = MaterialTheme.typography.titleMedium)
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = deviceCountText,
            onValueChange = { deviceCountText = it },
            label = { Text("Количество") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = deviceProcessingTimeText,
            onValueChange = { deviceProcessingTimeText = it },
            label = { Text("Время обслуживания") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.size(8.dp))
    Text(text = "Буфер", style = MaterialTheme.typography.titleMedium)
    OutlinedTextField(
        value = bufferSizeText,
        onValueChange = { bufferSizeText = it },
        label = { Text("Размер") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.size(8.dp))
    config?.let {
        Button(
            onClick = { onStart(it) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Симулировать")
        }
    } ?: run {
        Button(
            onClick = {}, enabled = false,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Некорректные параметры")
        }
    }
    addition?.invoke(config)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ColumnScope.PlotterConfig(
    config: Simulator.Config?,
    onStart: (Plotter.Config) -> Unit
) {
    var sourceCountText by rememberSaveable { mutableStateOf("1..100") }
    var deviceCountText by rememberSaveable { mutableStateOf("1..100") }
    var bufferSizeText by rememberSaveable { mutableStateOf("1..100") }
    val plotterConfig = run {
        Plotter.Config(
            defaultSimulatorConfig = config ?: return@run null,
            sourceCountRange = sourceCountText.toIntRangeOrNull()
                ?.takeIf { it.last < 1000 && it.first > 0 } ?: return@run null,
            deviceCountRange = deviceCountText.toIntRangeOrNull()
                ?.takeIf { it.last < 1000 && it.first > 0 } ?: return@run null,
            bufferSizeRange = bufferSizeText.toIntRangeOrNull()
                ?.takeIf { it.last < 5000 && it.first > 0 } ?: return@run null
        )
    }
    Text(text = "Графики", style = MaterialTheme.typography.titleMedium)
    OutlinedTextField(
        value = sourceCountText,
        onValueChange = { sourceCountText = it },
        label = { Text("Диапазон количества источников") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = deviceCountText,
        onValueChange = { deviceCountText = it },
        label = { Text("Диапазон количества приборов") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = bufferSizeText,
        onValueChange = { bufferSizeText = it },
        label = { Text("Диапазон размера буфера") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.size(8.dp))
    plotterConfig?.let {
        Button(
            onClick = { onStart(it) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Построить графики")
        }
    } ?: run {
        Button(
            onClick = {}, enabled = false,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Некорректные параметры")
        }
    }
    Spacer(modifier = Modifier.size(8.dp))
}