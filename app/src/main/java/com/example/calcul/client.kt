package com.example.calcul

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class client : AppCompatActivity() {

    private var client: SocketClient? = null
    private lateinit var tvStatus: TextView
    private lateinit var buttonclient : Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonclient = findViewById<Button>( R.id.buttonclient)
        tvStatus = findViewById(R.id.tvStatus)

        buttonclient.setOnClickListener {
            if (client == null) {
                client = SocketClient("192.168.1.102", 12344) // IP сервера
                client!!.connect {
                    runOnUiThread {
                        tvStatus.text = "Статус: подключен"
                    }
                    client!!.send("Подключение установлено")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client?.disconnect()
    }
}

class SocketClient(private val host: String, private val port: Int) {
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    fun connect(onConnected: () -> Unit) {
        Thread {
            try {
                socket = Socket(host, port)
                writer = PrintWriter(socket!!.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                onConnected()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun send(message: String) {
        Thread {
            writer?.println(message)
        }.start()
    }

    fun disconnect() {
        try {
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
