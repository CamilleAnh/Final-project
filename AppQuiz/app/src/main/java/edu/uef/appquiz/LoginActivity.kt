package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var signUpLink: TextView
    private lateinit var forgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        signUpLink = findViewById(R.id.SingUpLink)
        forgotPassword = findViewById(R.id.ForgotPassword)

        signUpLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Kiểm tra xem tài khoản có tồn tại trong cơ sở dữ liệu SQLite hay không
                if (isValidCredentials(email, password)) {
                    // Người dùng đăng nhập thành công
                    checkUserRole(email)
                } else {
                    Toast.makeText(
                        this,
                        "Bạn Nhập Sai Email hoặc Mật Khẩu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập email và password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidCredentials(email: String, password: String): Boolean {
        // Kiểm tra xem tài khoản có tồn tại trong cơ sở dữ liệu SQLite hay không
        val user = dbHelper.getUserByEmailAndPassword(email, password)
        return user != null
    }

    private fun checkUserRole(email: String) {
        val user = dbHelper.getUserByEmail(email)
        if (user != null) {
            when (user.role) {
                "admin" -> {
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                }
                "user" -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("USER_EMAIL", email)
                    println(email)
                    startActivity(intent)
                }
                else -> {
                    // Xử lý khi vai trò không xác định
                    Toast.makeText(this, "Vai trò không xác định", Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        } else {
            // Xử lý khi không có thông tin vai trò
            Toast.makeText(this, "Không có thông tin vai trò", Toast.LENGTH_SHORT).show()
        }
    }
}