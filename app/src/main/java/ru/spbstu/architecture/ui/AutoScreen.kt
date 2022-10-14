package ru.spbstu.architecture.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import io.data2viz.axis.Orient
import io.data2viz.color.Colors
import io.data2viz.color.col
import io.data2viz.scale.Scales
import io.data2viz.viz.Margins
import io.github.pt2121.collage.Axis
import io.github.pt2121.collage.CollageElements
import io.github.pt2121.collage.CollageGroup
import io.github.pt2121.collage.d2v.path
import io.github.pt2121.collage.rememberCollagePainter
import ru.spbstu.architecture.simulation.AutoSimulator

enum class Tabs {
    Sources {
        override val title = "Источники"
        override fun getPlots(result: AutoSimulator.Result) = result.varyingSourcePlots
    },
    Devices {
        override val title = "Приборы"
        override fun getPlots(result: AutoSimulator.Result) = result.varyingDevicePlots
    },
    Buffer {
        override val title = "Буфер"
        override fun getPlots(result: AutoSimulator.Result) = result.varyingBufferPlots
    };

    abstract val title: String
    abstract fun getPlots(result: AutoSimulator.Result): AutoSimulator.Result.Plots
}

enum class Plots {
    AverageUtilization {
        override val title = "Средняя загруженность"
        override fun getPlot(plots: AutoSimulator.Result.Plots) = plots.averageUtilization
    },
    AverageTimeSpent {
        override val title = "Среднее время в системе"
        override fun getPlot(plots: AutoSimulator.Result.Plots) = plots.averageTimeSpent
    },
    DenyProbability {
        override val title = "Вероятность отказа"
        override fun getPlot(plots: AutoSimulator.Result.Plots) = plots.denyProbability
    };

    abstract val title: String
    abstract fun getPlot(plots: AutoSimulator.Result.Plots): List<Pair<Int, Double>>
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
@Destination
fun AutoScreen(
    viewModel: AutoViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Автоматическая симуляция") }
            )
        }
    ) { padding ->
        val targetState by viewModel.state.collectAsState(initial = null)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            var selectedTab by remember { mutableStateOf(Tabs.values().first()) }
            TabRow(
                selectedTabIndex = Tabs.values().indexOf(selectedTab),
                modifier = Modifier.fillMaxWidth()
            ) {
                Tabs.values().forEach { tab ->
                    Tab(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(text = tab.title)
                        }
                    )
                }
            }

            AnimatedContent(targetState = targetState) { state ->
                if (state == null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        )
                    }
                } else {
                    val plots = selectedTab.getPlots(state)
                    Column {
                        Plots.values().forEach {
                            Text(
                                text = it.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                            Plot(
                                points = it.getPlot(plots),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Plot(points: List<Pair<Int, Double>>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val margins = Margins(
            top = 12.0,
            right = 32.0,
            bottom = 10.0,
            left = 40.0
        )
        val height = maxHeight.value.toDouble() - margins.vMargins
        val width = maxWidth.value.toDouble() - margins.hMargins
        val xScale = Scales.Continuous.linear {
            domain = listOf(.0, points.maxOf { it.first }.toDouble())
            range = listOf(0.0, width)
        }
        val yScale = Scales.Continuous.linear {
            domain = listOf(0.0, points.maxOf { it.second } * 1.2)
            range = listOf(height, 0.0)
        }
        val painter = rememberCollagePainter(
            defaultWidth = maxWidth,
            defaultHeight = maxHeight
        ) { _, _ ->
            CollageGroup(
                translationX = margins.left.dp,
                translationY = -margins.bottom.dp
            ) {
                Axis(
                    orient = Orient.LEFT,
                    scale = yScale,
                    tickStroke = MaterialTheme.colorScheme.onBackground.toArgb().col,
                    axisStroke = MaterialTheme.colorScheme.onBackground.toArgb().col,
                    fontColor = MaterialTheme.colorScheme.onBackground.toArgb().col
                )
                CollageElements(nodes = listOf(
                    path {
                        fill = null
                        strokeColor = MaterialTheme.colorScheme.secondary.toArgb().col
                        strokeWidth = 2.0
                        moveTo(
                            xScale(points.first().first.toDouble()),
                            yScale(points.first().second)
                        )
                        (1..points.lastIndex).forEach {
                            lineTo(
                                xScale(points[it].first.toDouble()),
                                yScale(points[it].second)
                            )
                        }
                    }
                ))
            }
            CollageGroup(
                translationX = margins.left.dp,
                translationY = (height - margins.bottom).dp
            ) {
                Axis(
                    orient = Orient.BOTTOM,
                    scale = xScale,
                    tickPadding = 16.0,
                    tickFormat = { it.toInt().toString() },
                    tickStroke = MaterialTheme.colorScheme.onBackground.toArgb().col,
                    axisStroke = MaterialTheme.colorScheme.onBackground.toArgb().col,
                    fontColor = MaterialTheme.colorScheme.onBackground.toArgb().col
                )
            }
        }
        Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize())
    }
}