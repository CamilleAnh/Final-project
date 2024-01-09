package edu.uef.appquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class Admin_List_QuestionSet : AppCompatActivity() {
    private lateinit var questionListView: ListView
    private lateinit var dbHelper: DatabaseHelper
    private var selectedQuestionSetName: String? = null
    private var questionSetId: Long = 0
    private lateinit var addButton: Button
    private lateinit var deleteButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_question_set)
        questionListView = findViewById(R.id.questionList)
        dbHelper = DatabaseHelper(this)

        // Retrieve the list of question sets from SQLite
        val questionSets = dbHelper.getQuestionSets()

        // Use ArrayAdapter to display question sets in ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, questionSets)
        questionListView.adapter = adapter

        questionListView.setOnItemClickListener { _, _, position, _ ->
            // cHandle the event when the user selects a question set from the list
            onQuestionSetSelected(position)
        }

        val detailButtonsLayout = findViewById<LinearLayout>(R.id.detailButtonsLayout)



        deleteButton = findViewById(R.id.deleteQuestionButton)

        questionListView.adapter = adapter




        val deleteButton = findViewById<Button>(R.id.deleteQuestionButton)
        deleteButton.visibility = View.VISIBLE
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }



    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu1, menu)
        return true
    }



    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Question Set")
        builder.setMessage("Are you sure you want to delete the selected question set and its questions?")

        builder.setPositiveButton("Yes") { _, _ ->
            // User clicked Yes, perform the delete operation
            onDeleteQuestionSet()

        }

        builder.setNegativeButton("No") { _, _ ->
            // User clicked No, do nothing or provide feedback
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun onDeleteQuestionSet() {
        if (selectedQuestionSetName != null) {
            dbHelper.deleteQuestionSet(selectedQuestionSetName!!)
            refreshQuestionSetList()
            Toast.makeText(this, "Question set deleted", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                // Xử lý sự kiện khi nhấn vào nút Refresh
                refreshQuestionSetList()
                return true
            }
            R.id.menu_Add -> {
                val intent = Intent(this, AddQuestionActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Add Question Clicked", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun refreshQuestionSetList() {
        // Xử lý khi nhấn vào nút Refresh
        val questionSets = dbHelper.getQuestionSets()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, questionSets)
        questionListView.adapter = adapter
        // Thông báo là danh sách đã được cập nhật
        Toast.makeText(this, "Question list updated", Toast.LENGTH_SHORT).show()
    }

    private fun onEditQuestionClicked() {

    }

    private fun onQuestionSetSelected(position: Int) {
        selectedQuestionSetName = questionListView.getItemAtPosition(position).toString()
        Toast.makeText(this, "Selected question set: $selectedQuestionSetName", Toast.LENGTH_SHORT).show()
        val deleteQuestionButton = findViewById<Button>(R.id.deleteQuestionButton)


        val detailButtonsLayout = findViewById<LinearLayout>(R.id.detailButtonsLayout)
        val detailButtons = findViewById<Button>(R.id.detailButton)
        // Ẩn nút "Add Question Set"


        detailButtons.setOnClickListener {
            val intent = Intent(this, DetailQuestionSetActivity::class.java)
            val selectedQuestionSet = questionListView.getItemAtPosition(position).toString()
            questionSetId = dbHelper.getQuestionSetIdByName(selectedQuestionSet)
            Toast.makeText(this, "Selected question set: $selectedQuestionSet", Toast.LENGTH_SHORT).show()
            println(questionSetId)
            intent.putExtra("question_set_id", questionSetId)

            startActivity(intent)
        }
        // Hiển thị nút "Delete", "Edit", và "Detail"


        deleteQuestionButton.visibility = View.VISIBLE

        detailButtonsLayout.visibility = View.VISIBLE
        detailButtons.visibility = View.VISIBLE
    }
}