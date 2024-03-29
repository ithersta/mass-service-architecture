package ru.spbstu.architecture.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.spbstu.architecture.simulation.Simulator

class StepByStepViewModel(config: Simulator.Config) : ViewModel() {
    private val simulator = Simulator(config, Int.MAX_VALUE)
    private val _state = MutableStateFlow(simulator.createEventCalendar())
    val state = _state.asStateFlow()

    fun step() {
        simulator.step()
        _state.value = simulator.createEventCalendar()
    }
}