package com.example.demo_gridview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val txtTenSP = findViewById<TextView>(R.id.txtTenSP)
        val txtGia = findViewById<TextView>(R.id.txtGia)
        val imgSP = findViewById<ImageView>(R.id.imgSP)
        val btnBack = findViewById<Button>(R.id.btnBack)

        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")
        val image = intent.getIntExtra("image", 0)

        txtTenSP.text = name
        txtGia.text = price
        imgSP.setImageResource(image)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Xóa các Activity trên MainActivity để tránh chồng lớp
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}
