package ru.spbstu.architecture.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.spbstu.architecture.simulation.AutoSimulator

class AutoViewModel : ViewModel() {
    private val autoSimulator = AutoSimulator(
        AutoSimulator.Config(
            defaultSourceCount = 64,
            defaultDeviceCount = 12,
            defaultBufferSize = 8,
            sourceIntensity = 0.5,
            deviceProcessingTime = 0.1 to 1.0,
            sourceCountRange = 1..100,
            deviceCountRange = 1..100,
            bufferSizeRange = 1..100
        )
    )

    private val _state = MutableStateFlow<AutoSimulator.Result?>(null)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            _state.value = autoSimulator.work(500)
        }
    }
}