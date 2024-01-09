// EditAccountActivity.kt
package edu.uef.appquiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        // Nhận thông tin người dùng từ intent
        val username = intent.getStringExtra("user_username")
        val email = intent.getStringExtra("user_email")
        val password = intent.getStringExtra("user_password")
        val role = intent.getStringExtra("user_role")

        // Lấy tham chiếu đến các EditText để nhập thông tin chỉnh sửa
        val etUserName = findViewById<EditText>(R.id.etUserName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassWord)
        val etRole = findViewById<EditText>(R.id.etRole)

        // Đặt thông tin người dùng hiện tại vào các EditText
        etUserName.setText(username)
        etEmail.setText(email)
        etPassword.setText(password)
        etRole.setText(role)

        // Lấy tham chiếu đến nút "Lưu Chỉnh Sửa"
        val btnSaveEdit = findViewById<Button>(R.id.btnSaveEdit)

        // Đặt sự kiện click cho nút "Lưu Chỉnh Sửa"
        btnSaveEdit.setOnClickListener {
            // Lấy thông tin đã chỉnh sửa từ các EditText
            val editedUsername = etUserName.text.toString()
            val editedEmail = etEmail.text.toString()
            val editedPassword = etPassword.text.toString()
            val editedRole = etRole.text.toString()

            // Tạo một đối tượng User từ thông tin đã chỉnh sửa
            val editedUser = User(editedUsername, editedEmail, editedRole, editedPassword)

            // Gọi hàm updateUser để cập nhật thông tin người dùng trong cơ sở dữ liệu
            val dbHelper = DatabaseHelper(this)
            val success = dbHelper.updateUser(intent.getStringExtra("user_email") ?: "", editedUser)

            if (success) {
                // TODO: Hiển thị thông báo hoặc thực hiện các bước khác sau khi cập nhật người dùng
                // Ví dụ: Chuyển trở lại màn hình Detail sau khi cập nhật
                finish()
            } else {
                // TODO: Xử lý trường hợp cập nhật không thành công
                // Ví dụ: Hiển thị thông báo lỗi
            }
        }

    }
}
