package edu.uef.appquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val usernameTextView: TextView = findViewById(R.id.usernameTextView)
        //val moreImageView: ImageView = findViewById(R.id.moreImageView)
        val listQuestionTextView: TextView = findViewById(R.id.ListQuestion)
        val listAccountTextView: TextView = findViewById(R.id.ListAccount)
        val logoutTextView: TextView = findViewById(R.id.logoutTextView)

        listQuestionTextView.setOnClickListener {
            val intent = Intent(this, Admin_List_QuestionSet::class.java)
            startActivity(intent)
        }
        listAccountTextView.setOnClickListener {
              val intent = Intent(this, Admin_List_Account::class.java)
               startActivity(intent)
        }
        logoutTextView.setOnClickListener{
            logout()
        }

    }

    private fun logout() {

        val intent = Intent(this, LoginActivity::class.java)
        // Clear the back stack, so the user can't navigate back to the profile screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}