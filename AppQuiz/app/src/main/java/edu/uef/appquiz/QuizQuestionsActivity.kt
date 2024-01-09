package edu.uef.appquiz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private var mCurrentPosition: Int = 1
    private var mQuestionsList: List<Question>? = null
    private var mSelectedOptionPosition: Int = 0
    private var mCorrectAnswers: Int = 0
    private var questionSetId: Long = 0
    private lateinit var dbHelper: DatabaseHelper
    private val TAG = "QuizQuestionsActivity"
    private lateinit var questionSetName: String
    private lateinit var tvContent: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView
    private lateinit var optionOne: TextView
    private lateinit var optionTwo: TextView
    private lateinit var optionThree: TextView
    private lateinit var optionFour: TextView
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)
        dbHelper = DatabaseHelper(this)
        if (intent != null) {
            // Lấy giá trị Long từ Intent
            questionSetId = intent.getLongExtra("question_set_id", 0)
        }

        // Ánh xạ các phần tử trong layout XML
        tvContent = findViewById(R.id.tvContent)
        tvQuestion = findViewById(R.id.tvQuestion)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)
        optionOne = findViewById(R.id.optionOne)
        optionTwo = findViewById(R.id.optionTwo)
        optionThree = findViewById(R.id.optionThree)
        optionFour = findViewById(R.id.optionFour)
        btnSubmit = findViewById(R.id.btnSubmit)

        questionSetName = dbHelper.getQuestionSetNameById(questionSetId) ?: ""
        mQuestionsList = dbHelper.getQuestionsBySetId(questionSetId)
        setQuestion()
        optionOne.setOnClickListener(this)
        optionTwo.setOnClickListener(this)
        optionThree.setOnClickListener(this)
        optionFour.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }

    private fun setQuestion() {
        defaultOptionViews()

        if (mCurrentPosition <= mQuestionsList!!.size) {
            val question: Question = mQuestionsList!![mCurrentPosition - 1]

            progressBar.progress = mCurrentPosition
            tvProgress.text = "${mCurrentPosition} / ${mQuestionsList!!.size}"
            tvQuestion.text = question.questionText
            optionOne.text = question.options[0]
            optionTwo.text = question.options[1]
            optionThree.text = question.options[2]
            optionFour.text = question.options[3]

            if (mCurrentPosition == mQuestionsList!!.size) {
                btnSubmit.text = "FINISH"
            } else {
                btnSubmit.text = "SUBMIT"
            }

        } else {
            showResult()
        }

    }

    private fun defaultOptionViews() {
        val options = ArrayList<TextView>()
        optionOne?.let {
            options.add(0, it)
        }

        optionTwo?.let {
            options.add(1, it)
        }

        optionThree?.let {
            options.add(2, it)
        }

        optionFour?.let {
            options.add(3, it)
        }

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_border_bg
            )
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionViews()
        mSelectedOptionPosition = selectedOptionNum
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            this,
            R.drawable.selected_option_border_bg
        )
    }

    private fun checkAnswer() {
        val question = mQuestionsList!![mCurrentPosition - 1]
        if (question.correctAnswer == mSelectedOptionPosition) {
            mCorrectAnswers++
        }
    }

    private fun showResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("correct_answers", mCorrectAnswers)
        intent.putExtra("total_questions", mQuestionsList!!.size)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.optionOne -> {
                optionOne?.let {
                    selectedOptionView(it, 1)
                }
            }

            R.id.optionTwo -> {
                optionTwo?.let {
                    selectedOptionView(it, 2)
                }
            }

            R.id.optionThree -> {
                optionThree?.let {
                    selectedOptionView(it, 3)
                }
            }

            R.id.optionFour -> {
                optionFour?.let {
                    selectedOptionView(it, 4)
                }
            }

            R.id.btnSubmit -> {
                if (mSelectedOptionPosition == 0) {

                    mCurrentPosition++

                    when {
                        mCurrentPosition <= mQuestionsList!!.size -> {
                            setQuestion()
                        }

                        else -> {
                            val intent = Intent(this, ResultActivity::class.java)
                             intent.putExtra(CORRECT_ANSWERS, mCorrectAnswers)
                             intent.putExtra(TOTAL_QUESTIONS, mQuestionsList?.size)
                             startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    handleSelectedAnswer()
                    val question1 = mQuestionsList?.get(mCurrentPosition - 1)
                    if (question1!!.correctAnswer != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                    } else {
                        mCorrectAnswers++
                    }
                    answerView(question1.correctAnswer, R.drawable.correct_option_border_bg)

                    if (mCurrentPosition == mQuestionsList!!.size) {
                        btnSubmit?.text = "FINISH"
                    } else {
                        btnSubmit?.text = "Go To Next Question"
                    }

                    mSelectedOptionPosition = 0
                }
            }
        }
    }
    private fun answerView(answer: Int?, drawableView: Int) {
        when (answer) {
            1 -> {
                optionOne?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }

            2 -> {
                optionTwo?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }

            3 -> {
                optionThree?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }

            4 -> {
                optionFour?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }
        }
    }
    private fun handleSelectedAnswer() {
        val question = mQuestionsList!![mCurrentPosition - 1]
        if (question.correctAnswer == mSelectedOptionPosition) {
            mCorrectAnswers++
        }

        // Rest of the logic...
    }
}