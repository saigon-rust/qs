package com.example.demo_login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.editPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            } else if (email == "admin" && password == "123") {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        // Đăng ký callback khi nhấn Back (hoặc vuốt back gesture)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmation()
            }
        })
    }

    private fun showExitConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận thoát")
            .setMessage("Bạn có chắc chắn muốn thoát ứng dụng?")
            .setPositiveButton("Có") { _, _ ->
                finishAffinity() // Thoát hẳn app
            }
            .setNegativeButton("Không", null)
            .show()
    }
}
