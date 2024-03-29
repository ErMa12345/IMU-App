package com.example.tsaiimuapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.math.BigDecimal

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(readCSV: suspend () -> Unit) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
    val selectedTabIndex = remember {
        derivedStateOf { pagerState.currentPage }
    }

    val curScreen = remember {
        mutableStateOf(pagerState.currentPage)
    }
    LaunchedEffect(Unit) {
        readCSV()
    }

    // Determine the current orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "IMU App") }) }
    ) {
        if (isLandscape) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    modifier = Modifier.weight(1f)
                ) {
                    HomeTabs.entries.forEachIndexed { index, currentTab ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(currentTab.ordinal)
                                    curScreen.value = currentTab.ordinal
                                }
                            },
                            text = {
                                Text(text = currentTab.text)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(3f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (curScreen.value == 0) {
                            graphArrays(time = time, lineOne = yaw, lineTwo = pitch, lineThree = roll)
                        } else {
                            graphArrays(time = time, lineOne = accelX, lineTwo = accelY, lineThree = accelZ)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HomeTabs.entries.forEachIndexed { index, currentTab ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(currentTab.ordinal)
                                    curScreen.value = currentTab.ordinal
                                }
                            },
                            text = {
                                Text(text = currentTab.text)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (curScreen.value == 0) {
                            graphArrays(time = time, lineOne = yaw, lineTwo = pitch, lineThree = roll)
                        } else {
                            graphArrays(time = time, lineOne = accelX, lineTwo = accelY, lineThree = accelZ)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun graphArrays (time: MutableState<ArrayList<BigDecimal>>, lineOne: MutableState<ArrayList<Float>>, lineTwo: MutableState<ArrayList<Float>>, lineThree: MutableState<ArrayList<Float>>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val minValue = listOf(lineOne.value, lineTwo.value, lineThree.value).flatten().minOrNull() ?: 0f
        val maxValue = listOf(lineOne.value, lineTwo.value, lineThree.value).flatten().maxOrNull() ?: 0f
        val scaleFactor = (maxValue - minValue) / size.height

        fun scaleValue(value: Float): Float {
            return (value - minValue) / scaleFactor
        }

        val points1 = lineOne.value.mapIndexed {
                index, value -> Offset(x = size.width * index / (lineOne.value.size - 1), y = scaleValue(value))
        }

        val points2 = lineTwo.value.mapIndexed {
                index, value -> Offset(x = size.width * index / (lineTwo.value.size - 1), y = scaleValue(value))
        }

        val points3 = lineThree.value.mapIndexed {
                index, value -> Offset(x = size.width * index / (lineThree.value.size - 1), y = scaleValue(value))
        }



        //actual lines

        //line1
        drawPath(
            path = Path().apply {
                moveTo(points1.first().x, points1.first().y)
                points1.forEach { point ->
                    lineTo(point.x, point.y)
                }
            },
            color = Color.Blue,
            style = Stroke(width = 4f)
        )

        //line2
        drawPath(
            path = Path().apply {
                moveTo(points2.first().x, points2.first().y)
                points2.forEach { point ->
                    lineTo(point.x, point.y)
                }
            },
            color = Color.Red,
            style = Stroke(width = 4f)
        )

        //line3
        drawPath(
            path = Path().apply {
                moveTo(points3.first().x, points3.first().y)
                points3.forEach { point ->
                    lineTo(point.x, point.y)
                }
            },
            color = Color.Green,
            style = Stroke(width = 4f)
        )

        val yAxisLabels = listOf(minValue, maxValue).map { value ->
            val yPosition = size.height - scaleValue(value.toFloat())
            val xPosition = 0f
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 100f
                }
                canvas.nativeCanvas.drawText(value.toString(), xPosition, yPosition, paint)
            }
        }


    }
}
enum class HomeTabs (
    val text: String
) {
    Gyroscope(
        text = "Gyroscope"
    ),
    Accelerometer (
        text = "Accelerometer"
    )

}