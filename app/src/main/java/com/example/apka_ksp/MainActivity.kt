package com.example.apka_ksp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.apka_ksp.SocketIO.Companion.getValueFromJSON
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    private var socket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonChart.setOnClickListener {
            if (socket == null) {
                Toast.makeText(this, "You are not connected", Toast.LENGTH_SHORT).show()
            } else {
                openChartActivity()
            }
        }
        buttonConnect.setOnClickListener {
            if (buttonConnect.text == "Connect") {
                connectToServer()
            } else {
                socket?.disconnect()
            }
        }
    }

    private fun openChartActivity() {
        val intent = Intent(this, ChartActivity::class.java)
        startActivity(intent)
    }

    private fun connectToServer() {
        socket = SocketIO.instance.createSocket(editTextAddress.text.toString())
        if (socket == null) {
            Toast.makeText(this, "Cannot connect", Toast.LENGTH_SHORT).show()
        } else {
            socket?.connect()
            socket?.on(Socket.EVENT_CONNECT) {
                onEventConnect()
            }?.on(Socket.EVENT_DISCONNECT) {
                onEventDisconnect()
            }
        }
    }

    private fun onEventDisconnect() {
        runOnUiThread {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
            buttonConnect.text = "Connect"
            buttonChart.isEnabled = false
        }
        SocketIO.instance.listOfValues.clear()
    }

    private fun onEventConnect() {
        //TODO chyba tu trzeba wrzucić create room ale dodało mi kilka pokoi na raz :/
        // socket?.emit("createRoom", "roomName")
        runOnUiThread {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            buttonConnect.text = "Disconnect"
            buttonChart.isEnabled = true
        }
        addCurrentValueListener()
    }

    private fun addCurrentValueListener() {
        socket?.on("currentValue") { args ->
            socket?.emit("confirmation", true)
            val value = getValueFromJSON(args)
            SocketIO.instance.listOfValues.add(value)
            drawValue(value)
        }
    }

    private fun drawValue(value: Double) {
        runOnUiThread {
            if (isWhole(value)) {
                textViewTemp.text = "${value.toInt()}℃"
            } else {
                textViewTemp.text = "$value℃"
            }
        }
    }

    private fun isWhole(value: Double) = value - value.toInt() == 0.0
}