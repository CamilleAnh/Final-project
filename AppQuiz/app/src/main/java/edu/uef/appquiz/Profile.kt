package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class Profile : AppCompatActivity() {
    private var darkModeSwitch: Switch? = null
    private var usernameTextView: TextView? = null
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var  userEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DarkModePrefManager(this).isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHelper(this)

        // Nhận thông tin người dùng từ Intent
        userEmail = intent.getStringExtra("USER_EMAIL").toString()

        if (userEmail != null) {
            val userName = dbHelper.getUserNameByEmail(userEmail)
            usernameTextView = findViewById(R.id.usernameTextView)
            usernameTextView?.text = userName
        }
        val changePasswordTextView: TextView = findViewById(R.id.changePasswordTextView)
        val logoutTextView: TextView = findViewById(R.id.logoutTextView)
        // Set a click listener for the TextView
        changePasswordTextView.setOnClickListener {
            // Handle the click event, e.g., navigate to the ChangePasswordActivity
            val intent = Intent(this, ChangePassword::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
        val editProfileTextView: TextView = findViewById(R.id.editProfileTextView)

        editProfileTextView.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        logoutTextView.setOnClickListener {

            logout()
        }
        setDarkModeSwitch()
    }
    private fun logout() {
        // Add your logout logic here
        // For example, you can clear user session, navigate to the login screen, etc.

        // In this example, I'm assuming LoginActivity is your login screen
        val intent = Intent(this, LoginActivity::class.java)
        // Clear the back stack, so the user can't navigate back to the profile screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setDarkModeSwitch() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        darkModeSwitch?.isChecked = DarkModePrefManager(this).isNightMode
        darkModeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            val darkModePrefManager = DarkModePrefManager(this@Profile)
            darkModePrefManager.setDarkMode(isChecked)
            AppCompatDelegate.setDefaultNightMode(if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
            recreate()
        }
    }
}
