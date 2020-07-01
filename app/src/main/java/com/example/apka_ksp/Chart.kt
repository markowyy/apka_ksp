package com.example.apka_ksp

import android.app.Activity
import android.os.Bundle
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.chart.*
import org.json.JSONObject

class Chart : Activity() {
    private var socket: Socket = IO.socket("https://ksp-project.azurewebsites.net/")
    private lateinit var series: LineGraphSeries<DataPoint>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chart)

        socket.connect()
        socket.emit("confirmation", true)
        socket.on("currentValue") { args ->
            val data = args[0] as JSONObject
            val value = data.getDouble("value")
            addEntry(value)
        }

        series = LineGraphSeries()
        graph.addSeries(series)

        formatLabel()
        customizeGraphViewport(graph.viewport)
    }

    private fun formatLabel() {
        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    super.formatLabel(value, isValueX) + "[t]"
                } else {
                    super.formatLabel(value, isValueX) + "[℃]"
                }
            }
        }
    }

    private fun customizeGraphViewport(viewport: Viewport) {
        viewport.computeScroll()
        viewport.isScalable = true
        viewport.isScrollable = true
        viewport.isYAxisBoundsManual = false
        viewport.isXAxisBoundsManual = true
        viewport.setMinY(0.0)
        viewport.setMaxX(20.0)
        viewport.setMinX(0.0)
        viewport.setMaxY(35.0)
    }

    private var lastX = 0.0
    private fun addEntry(value: Double) {
        runOnUiThread {
            series.appendData(DataPoint(lastX++, value), true, 30)
        }
    }
}