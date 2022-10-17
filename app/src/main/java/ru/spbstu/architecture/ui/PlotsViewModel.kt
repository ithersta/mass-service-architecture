package ru.spbstu.architecture.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.spbstu.architecture.simulation.Plotter

class PlotsViewModel(config: Plotter.Config) : ViewModel() {
    private val _state = MutableStateFlow<Plotter.Result?>(null)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            _state.value = Plotter(config).simulate()
        }
    }
}