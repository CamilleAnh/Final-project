// Inside EditQuestionSetActivity.kt
package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditQuestionSet : AppCompatActivity() {
    private lateinit var textViewQuestionSetName: TextView
    private lateinit var textViewQuestionNumber: TextView
    private lateinit var etQuestionToEdit: EditText
    private lateinit var etOption1ToEdit: EditText
    private lateinit var etOption2ToEdit: EditText
    private lateinit var etOption3ToEdit: EditText
    private lateinit var etOption4ToEdit: EditText
    private lateinit var etCorrectAnswerToEdit: EditText
    private lateinit var btnSaveEdit: Button
    private var questionDetailsList = mutableListOf<QuestionDetails>()
    private var currentQuestionPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_question_set)

        etQuestionToEdit = findViewById(R.id.etQuestionToEdit)
        etOption1ToEdit = findViewById(R.id.etOption1ToEdit)
        etOption2ToEdit = findViewById(R.id.etOption2ToEdit)
        etOption3ToEdit = findViewById(R.id.etOption3ToEdit)
        etOption4ToEdit = findViewById(R.id.etOption4ToEdit)
        textViewQuestionSetName = findViewById(R.id.textViewQuestionSetName)
        textViewQuestionNumber = findViewById(R.id.textViewQuestionNumber)

        etCorrectAnswerToEdit = findViewById(R.id.etCorrectAnswerToEdit)
        btnSaveEdit = findViewById(R.id.btnSaveEdit)
        updateQuestionSetNameAndNumber()

        val questionSetId = intent.getLongExtra("questionSetId", -1)
        questionDetailsList = retrieveQuestionDetails(questionSetId).toMutableList()

        // Display the existing question details in the EditText fields
        displayQuestionDetails(questionDetailsList[currentQuestionPosition])

        btnSaveEdit.setOnClickListener {
            // Save the edited question details
            saveEditedQuestionDetails()

            // Move to the next question (if available)
            currentQuestionPosition++
            if (currentQuestionPosition < questionDetailsList.size) {
                displayQuestionDetails(questionDetailsList[currentQuestionPosition])
                updateQuestionSetNameAndNumber()
            } else {
                // All questions have been edited, you can handle this case (e.g., show a message)

                // Navigate back to AdminActivity
                val adminIntent = Intent(this, AdminActivity::class.java)
                startActivity(adminIntent)
            }
        }
    }
    private fun updateQuestionSetNameAndNumber() {
        textViewQuestionSetName.text = "Tên Bộ Câu Hỏi: ${intent.getStringExtra("questionSetName") ?: "N/A"}"
        textViewQuestionNumber.text = "Câu Hỏi Thứ: ${currentQuestionPosition + 1}"
    }
    private fun retrieveQuestionDetails(questionSetId: Long): List<QuestionDetails> {
        val databaseHelper = DatabaseHelper(this)
        return databaseHelper.getQuestionSetDetailsById(questionSetId)
    }

    // Function to display question details in the EditText fields
    private fun displayQuestionDetails(questionDetails: QuestionDetails) {
        etQuestionToEdit.setText(questionDetails.question)
        etOption1ToEdit.setText(questionDetails.option1)
        etOption2ToEdit.setText(questionDetails.option2)
        etOption3ToEdit.setText(questionDetails.option3)
        etOption4ToEdit.setText(questionDetails.option4)
        etCorrectAnswerToEdit.setText(questionDetails.correctAnswer)
    }

    // Function to save the edited question details
    private fun saveEditedQuestionDetails() {
        // Lấy thông tin câu hỏi đã chỉnh sửa từ giao diện
        val editedQuestionDetails = QuestionDetails(
            etQuestionToEdit.text.toString(),
            etOption1ToEdit.text.toString(),
            etOption2ToEdit.text.toString(),
            etOption3ToEdit.text.toString(),
            etOption4ToEdit.text.toString(),
            etCorrectAnswerToEdit.text.toString()
        )
        if (isValidAnswer(editedQuestionDetails.correctAnswer)) {
            // Lấy questionSetId và currentQuestionPosition từ Intent hoặc nơi khác
            val questionSetId = intent.getLongExtra("questionSetId", -1)

            // Gọi hàm cập nhật trong DatabaseHelper
            val databaseHelper = DatabaseHelper(this)
            val questionId = databaseHelper.retrieveQuestionId(questionSetId, currentQuestionPosition)

            databaseHelper.updateQuestionDetails(questionSetId, questionId, editedQuestionDetails)

            // Hiển thị thông báo hoặc thực hiện hành động khác sau khi lưu thành công
        } else {
            // Nếu câu trả lời không hợp lệ, bạn có thể hiển thị thông báo hoặc thực hiện xử lý khác
             Toast.makeText(this, "Câu trả lời phải là một số từ 1 đến 4", Toast.LENGTH_SHORT).show()
        }

    }
    private fun isValidAnswer(answer: String): Boolean {
        val answerValue = answer.toIntOrNull()
        return answerValue != null && answerValue in 1..4
    }

}
