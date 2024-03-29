package com.example.tsaiimuapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tsaiimuapp.ui.theme.TsaiIMUAppTheme
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


//Global arrays that will be used to graph data
val zeroArray = FloatArray(100) { 0.0f }
val timeArray = Array<BigDecimal>(100) { BigDecimal.ZERO }

var time = mutableStateOf(ArrayList<BigDecimal>(timeArray.asList()))
var yaw = mutableStateOf(ArrayList<Float>(zeroArray.asList()))
var pitch = mutableStateOf(ArrayList<Float>(zeroArray.asList()))
var roll = mutableStateOf(ArrayList<Float>(zeroArray.asList()))
var accelX = mutableStateOf(ArrayList<Float>(zeroArray.asList()))
var accelY = mutableStateOf(ArrayList<Float>(zeroArray.asList()))
var accelZ = mutableStateOf(ArrayList<Float>(zeroArray.asList()))


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TsaiIMUAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(readCSV = ::readCSV)

                }
            }
        }


    }

    /**
     * Reads the CSV and updates the value in the global arrays as they are read in
     */
    suspend fun readCSV() {
        withContext(Dispatchers.IO) {
            val file = assets.open("e2e_ref.csv")
            val isr = InputStreamReader(file)
            val reader = BufferedReader(isr)

            var line = reader.readLine()

            while (line != null) {
                //get current line values
                line = reader.readLine()
                var current : List<String> = line.split(',')
                var curTime = BigDecimal(current[1]) - BigDecimal(1666302376558)
                var curAccelX = current[2].toFloat()
                var curAccelY = current[3].toFloat()
                var curAccelZ = current[4].toFloat()
                var curYaw = current[12].toFloat()
                var curPitch = current[13].toFloat()
                var curRoll = current[14].toFloat()

                //update the global lists
                time.value = (time.value.drop(1) + curTime) as ArrayList<BigDecimal>

                accelX.value = (accelX.value.drop(1) + curAccelX) as ArrayList<Float>

                accelY.value = (accelY.value.drop(1) + curAccelY) as ArrayList<Float>

                accelZ.value = (accelZ.value.drop(1) + curAccelZ) as ArrayList<Float>

                yaw.value = (yaw.value.drop(1) + curYaw) as ArrayList<Float>

                pitch.value = (pitch.value.drop(1) + curPitch) as ArrayList<Float>

                roll.value = (roll.value.drop(1) + curRoll) as ArrayList<Float>

                //update every 100 ms (about how long it takes between updates in the csv)
                Thread.sleep(100)

            }
            //simulates no readings
            while (true) {
                time.value = (time.value.drop(1) + BigDecimal(0)) as ArrayList<BigDecimal>

                accelX.value = (accelX.value.drop(1) + 0f) as ArrayList<Float>

                accelY.value = (accelY.value.drop(1) + 0f) as ArrayList<Float>

                accelZ.value = (accelZ.value.drop(1) + 0f) as ArrayList<Float>

                yaw.value = (yaw.value.drop(1) + 0f) as ArrayList<Float>

                pitch.value = (pitch.value.drop(1) + 0f) as ArrayList<Float>

                roll.value = (roll.value.drop(1) + 0f) as ArrayList<Float>

                Thread.sleep(100)
            }


        }
    }


}



