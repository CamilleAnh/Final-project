    package edu.uef.appquiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem

class ChangePassword : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var userEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dbHelper = DatabaseHelper(this)

        oldPasswordEditText = findViewById(R.id.oldPasswordEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            handleChangePassword()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle ActionBar back button click
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun handleChangePassword() {
        val oldPassword = oldPasswordEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // Kiểm tra xem các trường đã được nhập chưa
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin.")
            return
        }

        // Kiểm tra mật khẩu cũ có đúng không
         userEmail = intent.getStringExtra("USER_EMAIL").toString()
        val user = dbHelper.getUserByEmailAndPassword(userEmail, oldPassword)

        if (user != null) {

            if (newPassword == confirmPassword) {
                // Cập nhật mật khẩu mới vào cơ sở dữ liệu
                user.Password = newPassword
                dbHelper.updateUser(user)

                showToast("Đổi mật khẩu thành công.")
                finish() // Kết thúc Activity sau khi đổi mật khẩu thành công
            } else {
                showToast("Mật khẩu mới và xác nhận mật khẩu không khớp.")
            }
        } else {
            showToast("Mật khẩu cũ không đúng.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
