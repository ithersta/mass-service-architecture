@file:OptIn(ExperimentalMaterial3Api::class)

package ru.spbstu.architecture.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.ramcosta.composedestinations.DestinationsNavHost
import ru.spbstu.architecture.simulation.Bruteforcer
import ru.spbstu.architecture.ui.theme.ArchitectureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArchitectureTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }
        Log.d("Bruteforcer", Bruteforcer().generateCsv())
    }
}