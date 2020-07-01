package com.example.apka_ksp

import android.app.Activity
import android.os.Bundle
import com.example.apka_ksp.SocketIO.Companion.getValueFromJSON
import com.github.nkzawa.socketio.client.Socket
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.chart.*

class ChartActivity : Activity() {
    private var socket: Socket = SocketIO.instance.getSocket()
    private lateinit var series: LineGraphSeries<DataPoint>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chart)
        socket.on("currentValue") { args ->
            addEntry(getValueFromJSON(args))
        }

        series = LineGraphSeries()
        series.isDrawDataPoints = true
        graph.addSeries(series)

        addValuesFromMainActivity()
        formatLabel()
        customizeGraphViewport(graph.viewport)
    }

    override fun onResume() {
        super.onResume()
        addValuesFromMainActivity()
    }

    private fun addValuesFromMainActivity() {
        SocketIO.instance.listOfValues.forEach {
            addEntry(it)
        }
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
        viewport.isYAxisBoundsManual = true
        viewport.isXAxisBoundsManual = true
        viewport.setMinY(0.0)
        viewport.setMaxX(20.0)
        viewport.setMinX(0.0)
        viewport.setMaxY(50.0)
    }

    private var lastX = 0.0
    private fun addEntry(value: Double) {
        runOnUiThread {
            series.appendData(DataPoint(lastX++, value), false, 20)
            if (lastX >= 20.0) {
                graph.viewport.scrollToEnd()
            }
        }
    }
}