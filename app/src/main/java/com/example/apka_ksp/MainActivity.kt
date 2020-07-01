package com.example.apka_ksp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : Activity() {
    private var socket: Socket = IO.socket("https://ksp-project.azurewebsites.net/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socket.connect()
        socket.emit("confirmation", true)

        socket.on("currentValue") { args ->
            val data = args[0] as JSONObject
            val value = data.getString("value")
            runOnUiThread {
                tempid.text = "$value℃"
            }
        }
        buttonChart.setOnClickListener {
            val i = Intent(this@MainActivity, Chart::class.java)
            startActivity(i)
        }
    }
}