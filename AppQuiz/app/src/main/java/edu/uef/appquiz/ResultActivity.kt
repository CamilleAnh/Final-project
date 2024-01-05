package edu.uef.appquiz
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val tvName: TextView = findViewById(R.id.congratulationsTv)
        val tvScore: TextView = findViewById(R.id.scoreTv)
        val btnFinish: Button = findViewById(R.id.btnRestart)

       // tvName.text = intent.getStringExtra(USER_NAME)

        val totalQuestions = intent.getIntExtra(TOTAL_QUESTIONS, 0)
      val correctAnswers = intent.getIntExtra(CORRECT_ANSWERS, 0)

     tvScore.text = "Your Score is $correctAnswers out of $totalQuestions"

        btnFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}