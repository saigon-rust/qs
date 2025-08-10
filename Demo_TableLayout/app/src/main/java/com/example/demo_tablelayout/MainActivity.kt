package com.example.demo_tablelayout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var edtDL: EditText
    private lateinit var edtAL: TextView
    private lateinit var btnCD: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ View
        edtDL = findViewById(R.id.edtDL)
        edtAL = findViewById(R.id.edtAL)
        btnCD = findViewById(R.id.btnCD)

        // Xử lý nút "Chuyển"
        btnCD.setOnClickListener {
            val yearDL = edtDL.text.toString().toIntOrNull()
            if (yearDL != null) {
                val canList = arrayOf("Canh", "Tân", "Nhâm", "Quý", "Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ")
                val chiList = arrayOf("Thân", "Dậu", "Tuất", "Hợi", "Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi")

                val can = canList[yearDL % 10]
                val chi = chiList[yearDL % 12]

                edtAL.text = "$can $chi"
            } else {
                edtAL.text = "Năm không hợp lệ"
            }
        }
    }
}
