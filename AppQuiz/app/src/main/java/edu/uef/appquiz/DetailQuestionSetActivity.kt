// DetailQuestionSetActivity.kt
package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailQuestionSetActivity : AppCompatActivity() {
    private lateinit var questionSetNameTextView: TextView
    private lateinit var dbHelper: DatabaseHelper
    private var questionSetId: Long = 0
    private var questionId: Long = 0
    private lateinit var container: LinearLayout
    private lateinit var editQuestionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_question_set)
        dbHelper = DatabaseHelper(this)

        // Lấy questionSetId từ intent
        questionSetId = intent.getLongExtra("question_set_id", 0)


        questionSetNameTextView = findViewById(R.id.questionSetNameTextView)
        container = findViewById(R.id.questionsContainer)
        editQuestionButton = findViewById(R.id.editQuestionButton)

        // Sử dụng hàm getQuestionsBySetId để lấy danh sách chi tiết câu hỏi
        val questionDetailsList = dbHelper.getQuestionSetDetailsById(questionSetId)
        val questionSetName = dbHelper.getQuestionSetNameById(questionSetId) ?: ""
        questionSetNameTextView.text = "Question Set Name: $questionSetName"

        // Hiển thị tất cả các câu hỏi
        // Hiển thị tất cả các câu hỏi
        for ((index, questionDetails) in questionDetailsList.withIndex()) {
            val questionText = "Question ${index + 1}: ${questionDetails.question}"
            val option1Text = "Option 1: ${questionDetails.option1}"
            val option2Text = "Option 2: ${questionDetails.option2}"
            val option3Text = "Option 3: ${questionDetails.option3}"
            val option4Text = "Option 4: ${questionDetails.option4}"
            val correctAnswerText = "Correct Answer: ${questionDetails.correctAnswer}"

            // Tạo TextView và thêm vào container
            val questionTextView = TextView(this)
            questionTextView.text = "$questionText\n$option1Text\n$option2Text\n$option3Text\n$option4Text\n$correctAnswerText"
            container.addView(questionTextView)
            questionTextView.setTextSize(18f) // Adjust the size as needed

            // Tạo khoảng cách giữa các câu hỏi sử dụng Space
            val space = Space(this)
            space.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 16) // Điều chỉnh chiều cao của khoảng cách
            container.addView(space)
        }


        // Đặt lắng nghe sự kiện cho nút chỉnh sửa câu hỏi
        editQuestionButton.setOnClickListener {
            val selectedQuestionSetId = questionSetId // Lấy ID của bộ câu hỏi
            val selectedQuestionId = questionId
            // Mở EditQuestionActivity và truyền thông tin câu hỏi
            val intent = Intent(this, EditQuestionSet::class.java)
            intent.putExtra("questionSetName",questionSetName)
            intent.putExtra("questionSetId",questionSetId)
            intent.putExtra("questionId",questionId)
            startActivity(intent)
            println(questionSetId)
            println(questionId)
            startActivity(intent)


        }
    }
}
