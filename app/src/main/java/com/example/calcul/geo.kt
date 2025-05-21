package com.example.calcul

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class geo : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvAltitude: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvStatus: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_geo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        tvAltitude = findViewById(R.id.tvAltitude)
        tvAccuracy = findViewById(R.id.tvAccuracy)
        tvTime = findViewById(R.id.tvTime)
        tvStatus = findViewById(R.id.tvStatus)

        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        } else {
            startGpsTracking()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startGpsTracking() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_MS,
                MIN_DISTANCE_M,
                this
            )
            tvStatus.text = "Поиск GPS сигнала"
        } catch (e: SecurityException) {
            Toast.makeText(this, "Ошибка доступа к GPS", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val altitude = location.altitude
        val accuracy = location.accuracy
        val time = location.time

        val dateFormat = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault())
        val timeString = dateFormat.format(Date(time))

        tvLatitude.text = "Широта: $latitude"
        tvLongitude.text = "Долгота: $longitude"
        tvAltitude.text = "Высота: ${"%.2f".format(altitude)} м"
        tvAccuracy.text = "Точность: ${"%.1f".format(accuracy)} м"
        tvTime.text = "Время: $timeString"
        tvStatus.text = "Данные получены: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}"

        if (accuracy <= 15) {
            Toast.makeText(this, "15 метров", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startGpsTracking()
            } else {
                Toast.makeText(this, "Необходимы все разрешения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val MIN_TIME_MS = 1000L
        private const val MIN_DISTANCE_M = 0.1f
    }
}