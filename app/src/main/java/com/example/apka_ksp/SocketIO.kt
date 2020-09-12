package com.example.apka_ksp

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONObject

class SocketIO {
    private var socket: Socket? = null
    val listOfValues = mutableListOf<Double>()
    fun getSocket(): Socket {
        return socket!!
    }

    fun createSocket(address: String): Socket? {
        socket = try {
            IO.socket(address)
        } catch (e: Exception) {
            null
        }
        return socket
    }

    companion object {
        val instance = SocketIO()

        fun getValueFromJSON(args: Array<Any>): Double {
            val data = args[0] as JSONObject
            return data.getDouble("value")
        }
    }
}