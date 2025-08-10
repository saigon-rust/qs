package com.example.demo_constrain

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    // Khai báo các biến giao diện
    private lateinit var edtA: EditText
    private lateinit var edtB: EditText
    private lateinit var edtKQ: EditText
    private lateinit var btnTong: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Ánh xạ id cho các biến giao diện
        edtA = findViewById(R.id.edtA)
        edtB = findViewById(R.id.edtB)
        edtKQ = findViewById(R.id.edtKQ)
        btnTong = findViewById(R.id.btnTong)

        // Xử lý sự kiện khi nhấn nút
        btnTong.setOnClickListener {
            val a = edtA.text.toString().toDoubleOrNull() ?: 0.0
            val b = edtB.text.toString().toDoubleOrNull() ?: 0.0
            val tong = a + b
            edtKQ.setText(tong.toString())
        }
    }
}