package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var editTextSetName: EditText
    private lateinit var buttonAddQuestionSet: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)

        // Initialize views
        editTextSetName = findViewById(R.id.editTextSetName)
        buttonAddQuestionSet = findViewById(R.id.buttonAddQuestionSet)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        // Set click listener for the button
        buttonAddQuestionSet.setOnClickListener {
            addQuestionSet()
        }
    }

    private fun addQuestionSet() {
        var questionSetName = editTextSetName.text.toString().trim()

        if (questionSetName.isNotEmpty()) {
            // Add the question set to the database
            databaseHelper.addQuestionSet(questionSetName)

            // Optionally, you can add additional logic or UI updates here

            // Display a success message
            val intent = Intent(this, AddQuestions::class.java)
            intent.putExtra("questionSetName", questionSetName)
            questionSetName = intent.getStringExtra("questionSetName") ?: "DefaultSet"
            Log.d("YourTag", "Question Set Name: $questionSetName")
            startActivity(intent)
            finish()
        } else {
            // Show an error message if the input is empty
            Toast.makeText(this, "Question Set Name cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}
