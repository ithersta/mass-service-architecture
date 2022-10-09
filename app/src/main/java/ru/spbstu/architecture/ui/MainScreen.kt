package ru.spbstu.architecture.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ru.spbstu.architecture.simulation.Config
import ru.spbstu.architecture.ui.destinations.StepByStepScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
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
        var sourceCountText by rememberSaveable { mutableStateOf("6") }
        var deviceCountText by rememberSaveable { mutableStateOf("3") }
        var bufferSizeText by rememberSaveable { mutableStateOf("5") }
        var sourceIntensityText by rememberSaveable { mutableStateOf("0.5") }
        var deviceProcessingTimeStartText by rememberSaveable { mutableStateOf("1.0") }
        var deviceProcessingTimeEndText by rememberSaveable { mutableStateOf("2.0") }
        val config = run {
            Config(
                sourceCount = sourceCountText.toIntOrNull()?.takeIf { it in 1..100 }
                    ?: return@run null,
                deviceCount = deviceCountText.toIntOrNull()?.takeIf { it in 1..100 }
                    ?: return@run null,
                bufferSize = bufferSizeText.toIntOrNull()?.takeIf { it in 1..1000 }
                    ?: return@run null,
                sourceIntensity = sourceIntensityText.toDoubleOrNull()?.takeIf { it in 0.0..10.0 }
                    ?: return@run null,
                deviceProcessingTime = 1.0 to 2.0
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = sourceCountText,
                onValueChange = { sourceCountText = it },
                label = { Text("Число источников") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = deviceCountText,
                onValueChange = { deviceCountText = it },
                label = { Text("Число приборов") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = bufferSizeText,
                onValueChange = { bufferSizeText = it },
                label = { Text("Размер буфера") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = sourceIntensityText,
                onValueChange = { sourceIntensityText = it },
                label = { Text("Интенсивность источников") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            config?.let {
                Button(onClick = { navigator.navigate(StepByStepScreenDestination(it)) }) {
                    Text("Пошаговая симуляция")
                }
            } ?: run {
                Button(onClick = {}, enabled = false) {
                    Text("Пошаговая симуляция")
                }
            }
        }

    }
}