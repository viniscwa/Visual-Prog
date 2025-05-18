package com.example.calcul

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class MainActivity : AppCompatActivity() {
    private lateinit var startcalc :Button;
    private lateinit var startplayer : Button;
    private lateinit var startgeo :Button;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startplayer = findViewById(R.id.startplayer);
        startcalc = findViewById(R.id.startcalc);
        startgeo = findViewById(R.id.startgeo);

    }

    override fun onResume() {
        super.onResume()

        startcalc.setOnClickListener({
            val randomIntent = Intent(this@MainActivity, Caluc::class.java)
            startActivity(randomIntent)
        });

        startplayer.setOnClickListener({
            val randomIntent = Intent(this@MainActivity, Playeer::class.java)
            startActivity(randomIntent)
        });

        startgeo.setOnClickListener({
            val randomIntent = Intent(this@MainActivity, geo::class.java)
            startActivity(randomIntent)
        });
    }

}

