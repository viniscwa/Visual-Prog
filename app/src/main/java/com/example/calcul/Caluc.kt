package com.example.calcul

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Caluc : AppCompatActivity() {
    private lateinit var one : Button
    private lateinit var two : Button
    private lateinit var three : Button
    private lateinit var four : Button
    private lateinit var five : Button
    private lateinit var six : Button
    private lateinit var seven : Button
    private lateinit var eight : Button
    private lateinit var nine : Button
    private lateinit var zero : Button
    private lateinit var plus : Button
    private lateinit var minus : Button
    private lateinit var div : Button
    private lateinit var multi : Button
    private lateinit var result : Button
    private lateinit var clear : Button
    private lateinit var mark : Button
    private lateinit var com : Button
    private lateinit var back : Button
    private lateinit var text1 : TextView
    private lateinit var text2 : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_caluc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        one = findViewById( R.id.one)
        two = findViewById<Button>( R.id.two)
        three = findViewById<Button>( R.id.three)
        four = findViewById<Button>( R.id.four)
        five = findViewById<Button>( R.id.five)
        six = findViewById<Button>( R.id.six)
        seven = findViewById<Button>( R.id.seven)
        eight = findViewById<Button>( R.id.eight)
        nine = findViewById<Button>( R.id.nine)
        zero = findViewById<Button>( R.id.zero)
        plus = findViewById<Button>( R.id.plus)
        minus = findViewById<Button>( R.id.minus)
        div = findViewById<Button>( R.id.div)
        multi = findViewById<Button>( R.id.multi)
        result = findViewById<Button>( R.id.eq)
        clear = findViewById<Button>( R.id.clear)
        mark = findViewById<Button>( R.id.mark)
        com = findViewById<Button>( R.id.com)
        back = findViewById<Button>( R.id.back)
        text1 = findViewById<TextView>( R.id.text1)
        text2 = findViewById<TextView>( R.id.text2)


    }
    override fun onResume() {
        super.onResume()
        one.setOnClickListener({ text1.append("1") })
        two.setOnClickListener { text1.append("2") }
        three.setOnClickListener { text1.append("3") }
        four.setOnClickListener { text1.append("4") }
        five.setOnClickListener { text1.append("5") }
        six.setOnClickListener { text1.append("6") }
        seven.setOnClickListener { text1.append("7") }
        eight.setOnClickListener { text1.append("8") }
        nine.setOnClickListener { text1.append("9") }
        zero.setOnClickListener { text1.append("0") }
        plus.setOnClickListener { text1.append(" + ") }
        minus.setOnClickListener { text1.append(" - ") }
        div.setOnClickListener { text1.append(" / " ) }
        multi.setOnClickListener { text1.append(" * ") }
        com.setOnClickListener { text1.append(",") }
        clear.setOnClickListener {
            text1.text = ""
            text2.text = ""
        }
        back.setOnClickListener {
            val s = text1.text.toString()
            if (s != "") {
                text1.text = s.substring(0, s.length - 2)
            }
        }


        result.setOnClickListener{
            val res=text1.text.toString().split(" ")
            if(res.size==3) {
                val v1=res[0].toDoubleOrNull()
                val z=res[1]
                val v2=res[2].toDoubleOrNull()
                if (v1!=null && v2!=null) {
                    if (z=="+"){
                        text2.setText((v1+v2).toString())}
                    if (z=="-"){
                        text2.setText((v1-v2).toString())}
                    if (z=="*"){
                        text2.setText((v1*v2).toString())}
                    if (z=="/"){
                        text2.setText((v1/v2).toString())}


                }
            }
        }



    }



}

