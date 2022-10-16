package ru.spbstu.architecture.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.spbstu.architecture.simulation.AutoSimulator
import ru.spbstu.architecture.simulation.Simulator
import ru.spbstu.architecture.simulation.Table

class AutoViewModel(config: Simulator.Config) : ViewModel() {
    private val _state = MutableStateFlow<Table?>(null)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            _state.value = AutoSimulator(config)
                .simulateWithPrecision(initialRequestCount = 100, maxRequestCount = 10000)
                .createTable()
        }
    }
}