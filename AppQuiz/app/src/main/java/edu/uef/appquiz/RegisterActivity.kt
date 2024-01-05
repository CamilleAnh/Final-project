package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginLink: TextView
    private lateinit var userNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        loginLink = findViewById(R.id.LoginLink)
        userNameEditText = findViewById(R.id.UseNameEditText)

        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val username = userNameEditText.text.toString()

            if (!isValidEmail(email) || !isValidPassword(password)) {
                Toast.makeText(this, "Email hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra xem email đã tồn tại hay không
            if (emailExists(email)) {
                Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Nếu email không tồn tại, thêm người dùng mới vào cơ sở dữ liệu
            val newUser = User(username, email, "user", password)
            dbHelper.addUser(newUser)

            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return email.matches(emailPattern)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty()
    }

    private fun emailExists(email: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_NAME_USERS} WHERE ${DatabaseHelper.COLUMN_EMAIL} = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

}
