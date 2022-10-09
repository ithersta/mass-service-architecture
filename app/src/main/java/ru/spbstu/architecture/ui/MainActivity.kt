@file:OptIn(ExperimentalMaterial3Api::class)

package ru.spbstu.architecture.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import ru.spbstu.architecture.simulation.Config
import ru.spbstu.architecture.ui.theme.ArchitectureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArchitectureTheme {
                StepByStepScreen(config = Config(
                    sourceCount = 6,
                    deviceCount = 3,
                    bufferSize = 5,
                    sourceIntensity = 1.0,
                    deviceProcessingTime = 1.0..2.0
                ))
            }
        }
    }
}