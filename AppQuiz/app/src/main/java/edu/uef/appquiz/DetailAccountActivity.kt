// DetailAccountActivity.kt
package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_account)

        // Nhận thông tin người dùng từ intent
        val username = intent.getStringExtra("user_username")
        val email = intent.getStringExtra("user_email")
        val password = intent.getStringExtra("user_password")
        val role = intent.getStringExtra("user_role")

        // Đặt thông tin người dùng vào TextViews
        findViewById<TextView>(R.id.textViewUsername).text = "Username: $username"
        findViewById<TextView>(R.id.textViewEmail).text = "Email: $email"
        findViewById<TextView>(R.id.textViewPassword).text = "Password: $password"
        findViewById<TextView>(R.id.textViewRole).text = "Role: $role"

        // Lấy tham chiếu đến nút "Edit User"
        val editButton = findViewById<Button>(R.id.editAccountButton)

        // Đặt sự kiện click cho nút "Edit User"
        editButton.setOnClickListener {
            // Chuyển đến màn hình chỉnh sửa người dùng
            val intent = Intent(this, EditAccountActivity::class.java)
            // Truyền thông tin người dùng để chỉnh sửa
            intent.putExtra("user_username", username)
            intent.putExtra("user_email", email)
            intent.putExtra("user_password", password)
            intent.putExtra("user_role", role)
            startActivity(intent)
        }
    }
}
