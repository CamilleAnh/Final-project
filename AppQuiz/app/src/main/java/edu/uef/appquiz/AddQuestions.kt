// AddQuestions.kt
package edu.uef.appquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class AddQuestions : AppCompatActivity() {

    private lateinit var questionSetName: String
    private lateinit var dbHelper: DatabaseHelper
    private var currentQuestionIndex = 1
    private val totalQuestions = 5  // Change this to the desired number of questions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_questions)

        dbHelper = DatabaseHelper(this)

        val textViewQuestionSetName = findViewById<TextView>(R.id.textViewQuestionSetName)
        val saveQuestionButton = findViewById<Button>(R.id.buttonSave)

        questionSetName = intent.getStringExtra("questionSetName") ?: "DefaultSet"
        textViewQuestionSetName.text = "Question Set Name: $questionSetName"

        saveQuestionButton.setOnClickListener {
            saveQuestion()
        }
    }

    private fun saveQuestion() {
        val questionEditText = findViewById<EditText>(R.id.editTextQuestion)
        val answerAEditText = findViewById<EditText>(R.id.editTextAnswerA)
        val answerBEditText = findViewById<EditText>(R.id.editTextAnswerB)
        val answerCEditText = findViewById<EditText>(R.id.editTextAnswerC)
        val answerDEditText = findViewById<EditText>(R.id.editTextAnswerD)
        val correctAnswerIndexEditText = findViewById<EditText>(R.id.editTextCorrectAnswer)

        val questionText = questionEditText.text.toString()
        val answerA = answerAEditText.text.toString()
        val answerB = answerBEditText.text.toString()
        val answerC = answerCEditText.text.toString()
        val answerD = answerDEditText.text.toString()
        val correctAnswerIndex = correctAnswerIndexEditText.text.toString()

        if (areFieldsValid(questionText, answerA, answerB, answerC, answerD, correctAnswerIndex)) {
            val question = Question(
                questionSetId = dbHelper.getQuestionSetIdByName(questionSetName),
                options = listOf(answerA, answerB, answerC, answerD),
                questionText = questionText,
                correctAnswer = correctAnswerIndex.toIntOrNull() ?: -1
            )

            dbHelper.addQuestion(question)
            Toast.makeText(this, "Question $currentQuestionIndex added successfully", Toast.LENGTH_SHORT).show()

            currentQuestionIndex++

            if (currentQuestionIndex > totalQuestions) {
                finish()  // Finish the activity after adding all questions
            } else {
                // Clear fields for the next question
                questionEditText.text.clear()
                answerAEditText.text.clear()
                answerBEditText.text.clear()
                answerCEditText.text.clear()
                answerDEditText.text.clear()
                correctAnswerIndexEditText.text.clear()

                // Update question index in the UI
                findViewById<TextView>(R.id.textViewQuestionIndex).text = "Question $currentQuestionIndex"
            }
        }
    }

    private fun areFieldsValid(
        questionText: String,
        answerA: String,
        answerB: String,
        answerC: String,
        answerD: String,
        correctAnswerIndex: String
    ): Boolean {
        // Add your validation logic here
        // You can check if fields are not empty, etc.
        return true
    }
}
