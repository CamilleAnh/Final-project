package edu.uef.appquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class Admin_List_Account : AppCompatActivity() {

    private lateinit var accountListView: ListView
    private lateinit var dbHelper: DatabaseHelper
    private var selectedUsername: String? = null
    private lateinit var addButton: Button
    private lateinit var deleteButton: Button
    private lateinit var detailButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_account)
        accountListView = findViewById(R.id.AccountList)
        dbHelper = DatabaseHelper(this)

        // Retrieve the list of users from SQLite
        val users = dbHelper.getAllUsers()

        // Use ArrayAdapter to display users in ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users.map { it.email })
        accountListView.adapter = adapter

        accountListView.setOnItemClickListener { _, _, position, _ ->
            // Handle the event when the user selects a user from the list
            onUserSelected(position)
        }
        detailButton = findViewById(R.id.detailButton)

        deleteButton = findViewById(R.id.deleteAccountButton)

        accountListView.adapter = adapter

        val deleteButton = findViewById<Button>(R.id.deleteAccountButton)
        deleteButton.visibility = View.VISIBLE
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        detailButton.setOnClickListener{

        }


    }



    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete User")
        builder.setMessage("Are you sure you want to delete the selected user?")

        builder.setPositiveButton("Yes") { _, _ ->
            // User clicked Yes, perform the delete operation
            onDeleteUser()
        }

        builder.setNegativeButton("No") { _, _ ->
            // User clicked No, do nothing or provide feedback
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun onDeleteUser() {
        if (selectedUsername != null) {
            dbHelper.deleteUser(selectedUsername!!)
            refreshUserList()
            Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
        }
    }



    private fun refreshUserList() {
        // Handle the event when the Refresh button is clicked
        val users = dbHelper.getAllUsers()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users.map { it.username })
        accountListView.adapter = adapter
        // Notify that the user list has been updated
        Toast.makeText(this, "User list updated", Toast.LENGTH_SHORT).show()
    }

    private fun onUserSelected(position: Int) {
        // Lấy tên người dùng từ danh sách
        selectedUsername = accountListView.getItemAtPosition(position).toString()

        // Hiển thị tên người dùng đã chọn
        Toast.makeText(this, "Selected user: $selectedUsername", Toast.LENGTH_SHORT).show()

        // Lấy nút xóa và layout chứa nút Detail
        val deleteButton = findViewById<Button>(R.id.deleteAccountButton)
        val detailButtonsLayout = findViewById<LinearLayout>(R.id.detailButtonsLayout)
        val detailButtons = findViewById<Button>(R.id.detailButton)

        // Thiết lập sự kiện click cho nút Detail
        detailButton.setOnClickListener {
            // Lấy thông tin người dùng từ cơ sở dữ liệu
            val selectedUser = dbHelper.getUserByEmail(selectedUsername!!)

            // Log giá trị của selectedUser
            Log.d("YourTag", "Selected User: $selectedUser")

            // Tạo Intent để chuyển đến DetailAccountActivity
            val intent = Intent(this, DetailAccountActivity::class.java)

            // Kiểm tra xem người dùng có tồn tại hay không
            if (selectedUser != null) {
                // Log giá trị của selectedUser.email
                Log.d("YourTag", "Selected User Email: ${selectedUser.email}")

                intent.putExtra("user_email", selectedUser.email)
                intent.putExtra("user_username", selectedUser.username)
                intent.putExtra("user_password", selectedUser.Password) // Thay đổi thành password
                intent.putExtra("user_role", selectedUser.role)

                // Chuyển đến DetailAccountActivity
                startActivity(intent)
            } else {
                // Handle the case where the selected user is not found
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
            }
        }

        // Hiển thị nút xóa và layout chứa nút Detail
        deleteButton.visibility = View.VISIBLE
        detailButtonsLayout.visibility = View.VISIBLE
        detailButtons.visibility = View.VISIBLE
    }
}
