// EditProfileActivity.kt
package edu.uef.appquiz

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfile : AppCompatActivity() {

    private lateinit var editTextNewUsername: EditText
    private lateinit var buttonSaveProfile: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        dbHelper = DatabaseHelper(this)

        // Get user email from intent
        userEmail = intent.getStringExtra("USER_EMAIL").toString()

        // Initialize views
        editTextNewUsername = findViewById(R.id.editTextNewUsername)
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile)

        // Set click listener for the Save button
        buttonSaveProfile.setOnClickListener {
            handleSaveProfile()
        }
    }

    private fun handleSaveProfile() {
        // Get the new username from the EditText
        val newUsername = editTextNewUsername.text.toString()

        // Check if the new username is empty
        if (newUsername.isEmpty()) {
            showToast("Please enter a new username.")
            return
        }

        // Update the user's username in the database
        val user = dbHelper.getUserByEmail(userEmail)
        if (user != null) {
            user.username = newUsername
            dbHelper.updateUser(user)
            showToast("Profile updated successfully")
            SessionManager.updateUser(user)
            showToast("Profile updated successfully")
            finish() // Finish the activity after updating the profile
        } else {
            showToast("User not found.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
