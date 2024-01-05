package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var questionListView: ListView
    private var questionSetId: Long = 0  // Chuyển đổi kiểu dữ liệu từ String sang Long
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var Email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        questionListView = findViewById(R.id.questionListView)
        Email = intent.getStringExtra("USER_EMAIL").toString()
        // Get the list of question sets from SQLite
        val questionSets = dbHelper.getQuestionSetNames()

        // Use ArrayAdapter to display question sets in ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, questionSets)
        questionListView.adapter = adapter

        questionListView.setOnItemClickListener { _, _, position, _ ->
            // Handle the event when the user selects a question set from the list
            onQuestionSetSelected(position)
        }

        val icon1: ImageView = findViewById(R.id.menu_icon1)
        val icon2: ImageView = findViewById(R.id.menu_icon2)


        icon1.setOnClickListener {
            showToast("Icon 1 clicked")
            // Add your logic for Icon 1 click here
        }

        icon2.setOnClickListener {

            val intent = Intent(this, Profile::class.java)
            // Nhận thông tin người dùng từ Intent



            intent.putExtra("USER_EMAIL", Email)

            println(Email)
            startActivity(intent)
        }


    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onQuestionSetSelected(position: Int) {
        // Handle the event when the user selects a question set from the list

        val playButton: Button = findViewById(R.id.playButton)
        playButton.visibility = View.VISIBLE
        playButton.setOnClickListener {
           val intent = Intent(this, QuizQuestionsActivity::class.java)
            val selectedQuestionSet = questionListView.getItemAtPosition(position).toString()

            questionSetId = dbHelper.getQuestionSetIdByName(selectedQuestionSet)

            Toast.makeText(this, "Selected question set: $selectedQuestionSet", Toast.LENGTH_SHORT).show()
            intent.putExtra("question_set_id", questionSetId)
            startActivity(intent)
        }
    }
}
