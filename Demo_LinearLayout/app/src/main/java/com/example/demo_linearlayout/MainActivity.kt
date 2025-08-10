package com.example.demo_linearlayout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var edtF: EditText
    private lateinit var edtC: EditText
    private lateinit var btnCF: Button
    private lateinit var btnFC: Button
    private lateinit var btnCL: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ view
        edtF = findViewById(R.id.edtF)
        edtC = findViewById(R.id.edtC)
        btnCF = findViewById(R.id.btnCF)
        btnFC = findViewById(R.id.btnFC)
        btnCL = findViewById(R.id.btnCL)

        // Celsius → Fahrenheit
        btnCF.setOnClickListener {
            val celsius = edtC.text.toString().toDoubleOrNull()
            if (celsius != null) {
                val fahrenheit = celsius * 9 / 5 + 32
                edtF.setText(fahrenheit.toString())
            } else {
                edtF.setText("")
            }
        }

        // Fahrenheit → Celsius
        btnFC.setOnClickListener {
            val fahrenheit = edtF.text.toString().toDoubleOrNull()
            if (fahrenheit != null) {
                val celsius = (fahrenheit - 32) * 5 / 9
                edtC.setText(celsius.toString())
            } else {
                edtC.setText("")
            }
        }

        // Xóa dữ liệu
        btnCL.setOnClickListener {
            edtF.text.clear()
            edtC.text.clear()
        }
    }
}
