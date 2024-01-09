package edu.uef.appquiz

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val TABLE_NAME_USERS = "users"
        const val TABLE_NAME_QUESTION_SETS = "question_sets"
        const val TABLE_NAME_QUESTIONS = "questions"
        private const val DATABASE_NAME = "DataBase"
        private const val DATABASE_VERSION = 1
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ROLE = "role"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_QUESTION_SET_ID = "question_set_id"
        const val COLUMN_QUESTION_SET_NAME = "question_set_name"
        const val COLUMN_QUESTION_TEXT = "question_text"
        const val COLUMN_OPTION_A = "option_a"
        const val COLUMN_OPTION_B = "option_b"
        const val COLUMN_OPTION_C = "option_c"
        const val COLUMN_OPTION_D = "option_d"
        const val COLUMN_ANSWERED = "answered"
        const val COLUMN_CORRECT_ANSWER_INDEX = "correct_answer_index"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("YourTag", "onCreate called")
        Log.d("YourTag", "Creating table: $TABLE_NAME_USERS")
        val createUserTableQuery = "CREATE TABLE $TABLE_NAME_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_EMAIL TEXT," +
                "$COLUMN_ROLE TEXT," +
                "$COLUMN_PASSWORD TEXT)"
        db?.execSQL(createUserTableQuery)

        Log.d("YourTag", "Creating table: $TABLE_NAME_QUESTION_SETS")
        val createQuestionSetsTableQuery = "CREATE TABLE $TABLE_NAME_QUESTION_SETS (" +
                "$COLUMN_QUESTION_SET_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_QUESTION_SET_NAME TEXT)"
        db?.execSQL(createQuestionSetsTableQuery)

        Log.d("YourTag", "Creating table: $TABLE_NAME_QUESTIONS")
        val createQuestionsTableQuery = "CREATE TABLE $TABLE_NAME_QUESTIONS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_QUESTION_SET_ID INTEGER," +
                "$COLUMN_QUESTION_TEXT TEXT," +
                "$COLUMN_OPTION_A TEXT," +
                "$COLUMN_OPTION_B TEXT," +
                "$COLUMN_OPTION_C TEXT," +
                "$COLUMN_OPTION_D TEXT," +
                "$COLUMN_CORRECT_ANSWER_INDEX INTEGER," +
                "$COLUMN_ANSWERED INTEGER DEFAULT 0," + // Thêm trường này
                "FOREIGN KEY($COLUMN_QUESTION_SET_ID) REFERENCES $TABLE_NAME_QUESTION_SETS($COLUMN_QUESTION_SET_ID))"
        db?.execSQL(createQuestionsTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }

    fun addUser(user: User) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_EMAIL, user.email)
        values.put(COLUMN_ROLE, user.role)
        values.put(COLUMN_PASSWORD, user.Password)
        db.insert(TABLE_NAME_USERS, null, values)
        Log.d("YourTag", "User added to the database")
        db.close()
    }

    fun addQuestionSet(questionSetName: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_QUESTION_SET_NAME, questionSetName)

        try {
            val result = db.insert(TABLE_NAME_QUESTION_SETS, null, values)
            Log.d("YourTag", "Result from insert: $result")

            if (result == -1L) {
                Log.e("YourTag", "Error: Failed to insert the question set into the database")
            } else {
                Log.d("YourTag", "Question Set added to the database successfully")
            }
        } catch (e: Exception) {
            Log.e("YourTag", "Error during insert: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun getAllQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_QUESTIONS"
        val cursor: Cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val question = Question(
                questionSetId = cursor.getLong(cursor.getColumnIndex(COLUMN_QUESTION_SET_ID)),
                questionText = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT)),
                options = listOf(
                    cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_A)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_B)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_C)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_D))
                ),
                correctAnswer = cursor.getInt(cursor.getColumnIndex(COLUMN_CORRECT_ANSWER_INDEX)),
            )
            questions.add(question)
        }

        cursor.close()
        return questions
    }

    fun deleteQuestionsBySetId(questionSetId: Long): Boolean {
        val db = this.writableDatabase

        val whereClauseQuestions = "$COLUMN_QUESTION_SET_ID = ?"
        val whereArgsQuestions = arrayOf(questionSetId.toString())
        val resultQuestions = db.delete(TABLE_NAME_QUESTIONS, whereClauseQuestions, whereArgsQuestions)

        db.close()

        return resultQuestions != -1
    }

    fun deleteQuestionSet(questionSetName: String): Boolean {
        val db = this.writableDatabase

        val questionSetId = getQuestionSetIdByName(questionSetName)

        val whereClauseSets = "$COLUMN_QUESTION_SET_NAME = ?"
        val whereArgsSets = arrayOf(questionSetName)
        val resultSets = db.delete(TABLE_NAME_QUESTION_SETS, whereClauseSets, whereArgsSets)

        // Delete associated questions
        val resultQuestions = deleteQuestionsBySetId(questionSetId)

        db.close()

        return resultSets != -1 && resultQuestions
    }

    fun getUserByEmailAndPassword(email: String, password: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(email, password))
        return if (cursor.moveToFirst()) {
            val user = User(
                cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }
    // Add this method to your DatabaseHelper class


    fun getQuestionSetDetails(questionSetName: String?): QuestionDetails {
        val questionDetails = QuestionDetails("", "", "", "", "", "")
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_QUESTIONS WHERE $COLUMN_QUESTION_SET_ID = ? "
        val cursor: Cursor = db.rawQuery(query, arrayOf(questionSetName))

        if (cursor.moveToFirst()) {
            questionDetails.question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT))
            questionDetails.option1 = cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_A))
            questionDetails.option2 = cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_B))
            questionDetails.option3 = cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_C))
            questionDetails.option4 = cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_D))
            questionDetails.correctAnswer = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_ANSWER_INDEX))


        }

        cursor.close()
        return questionDetails
    }
    fun retrieveQuestionId(questionSetId: Long, questionPosition: Int): Long {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ID FROM $TABLE_NAME_QUESTIONS WHERE $COLUMN_QUESTION_SET_ID = ? LIMIT 1 OFFSET ?"
        val cursor = db.rawQuery(query, arrayOf(questionSetId.toString(), questionPosition.toString()))

        return if (cursor.moveToFirst()) {
            val questionId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
            cursor.close()
            questionId
        } else {
            cursor.close()
            -1L  // Return -1 or another default value if the question ID is not found
        }
    }
    fun updateQuestionDetails(questionSetId: Long, questionId: Long, questionDetails: QuestionDetails) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_QUESTION_TEXT, questionDetails.question)
        values.put(COLUMN_OPTION_A, questionDetails.option1)
        values.put(COLUMN_OPTION_B, questionDetails.option2)
        values.put(COLUMN_OPTION_C, questionDetails.option3)
        values.put(COLUMN_OPTION_D, questionDetails.option4)
        values.put(COLUMN_CORRECT_ANSWER_INDEX, questionDetails.correctAnswer.toInt())

        // Thực hiện cập nhật dựa trên questionSetId và questionId
        db.update(
            TABLE_NAME_QUESTIONS,
            values,
            "$COLUMN_QUESTION_SET_ID=? AND $COLUMN_ID=?",
            arrayOf(questionSetId.toString(), questionId.toString())
        )

        db.close()
    }
    fun getQuestionSetDetailsById(questionSetId: Long): List<QuestionDetails> {
        val questionDetailsList = mutableListOf<QuestionDetails>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_QUESTIONS WHERE $COLUMN_QUESTION_SET_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(questionSetId.toString()))

        while (cursor.moveToNext()) {
            val questionDetails = QuestionDetails(
                question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT)),
                option1 = cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_A)),
                option2 = cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_B)),
                option3 = cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_C)),
                option4 = cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_D)),
                correctAnswer = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_ANSWER_INDEX))
            )
            questionDetailsList.add(questionDetails)
        }

        cursor.close()
        return questionDetailsList
    }

    fun updateQuestionInSet(
        questionSetId: Long,
        questionId: Long,
        newQuestion: String,
        newOption1: String,
        newOption2: String,
        newOption3: String,
        newOption4: String,
        newCorrectAnswer: String
    ) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_QUESTION_TEXT, newQuestion)
        values.put(COLUMN_OPTION_A, newOption1)
        values.put(COLUMN_OPTION_B, newOption2)
        values.put(COLUMN_OPTION_C, newOption3)
        values.put(COLUMN_OPTION_D, newOption4)
        values.put(COLUMN_CORRECT_ANSWER_INDEX, newCorrectAnswer.toInt())

        db.update(
            TABLE_NAME_QUESTIONS,
            values,
            "$COLUMN_QUESTION_SET_ID=? AND $COLUMN_ID=?",
            arrayOf(questionSetId.toString(), questionId.toString())
        )

        db.close()
    }

    fun getQuestionSetIdByName(questionSetName: String): Long {
        val db = readableDatabase
        val query = "SELECT $COLUMN_QUESTION_SET_ID FROM $TABLE_NAME_QUESTION_SETS WHERE $COLUMN_QUESTION_SET_NAME = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(questionSetName))

        return if (cursor.moveToFirst()) {
            val questionSetId = cursor.getLong(cursor.getColumnIndex(COLUMN_QUESTION_SET_ID))
            cursor.close()
            questionSetId
        } else {
            cursor.close()
            -1L
        }
    }

    fun getQuestionsBySetId(questionSetId: Long): List<Question> {
        println(questionSetId)
        val questions = mutableListOf<Question>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_QUESTIONS WHERE $COLUMN_QUESTION_SET_ID = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(questionSetId.toString()))



        while (cursor.moveToNext()) {


            val questionSetIdIndex = cursor.getColumnIndex(COLUMN_QUESTION_SET_ID)
            val questionTextIndex = cursor.getColumnIndex(COLUMN_QUESTION_TEXT)
            val optionAIndex = cursor.getColumnIndex(COLUMN_OPTION_A)
            val optionBIndex = cursor.getColumnIndex(COLUMN_OPTION_B)
            val optionCIndex = cursor.getColumnIndex(COLUMN_OPTION_C)
            val optionDIndex = cursor.getColumnIndex(COLUMN_OPTION_D)
            val correctAnswerIndex = cursor.getColumnIndex(COLUMN_CORRECT_ANSWER_INDEX)

            if ( questionSetIdIndex != -1 && questionTextIndex != -1 &&
                optionAIndex != -1 && optionBIndex != -1 && optionCIndex != -1 && optionDIndex != -1 &&
                correctAnswerIndex != -1) {

                val question = Question(

                    questionSetId = cursor.getLong(questionSetIdIndex),
                    questionText = cursor.getString(questionTextIndex),
                    options = listOf(
                        cursor.getString(optionAIndex),
                        cursor.getString(optionBIndex),
                        cursor.getString(optionCIndex),
                        cursor.getString(optionDIndex)

                    ),

                    correctAnswer = cursor.getInt(correctAnswerIndex)
                )
                questions.add(question)

            }
        }

        cursor.close()
        return questions
    }

    fun deleteUser(email: String): Boolean {
        val db = this.writableDatabase

        val whereClause = "$COLUMN_EMAIL = ?"
        val whereArgs = arrayOf(email)
        val result = db.delete(TABLE_NAME_USERS, whereClause, whereArgs)

        db.close()

        return result != -1
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_USERS"
        val cursor: Cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val user = User(
                username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                role = cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)),
                Password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            )
            users.add(user)
        }

        cursor.close()
        return users
    }
    fun updateUser(user: User) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_EMAIL, user.email)
        values.put(COLUMN_ROLE, user.role)
        values.put(COLUMN_PASSWORD, user.Password)

        db.update(
            TABLE_NAME_USERS,
            values,
            "$COLUMN_EMAIL = ?",
            arrayOf(user.email)
        )

        db.close()
    }

    fun getUserNameByEmail(email: String): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_USERNAME FROM $TABLE_NAME_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        var userName: String? = null
        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
        }

        cursor.close()
        return userName
    }
    fun getQuestionSetNames(): List<String> {
        val questionSets = mutableListOf<String>()
        val db = readableDatabase
        val query = "SELECT $COLUMN_QUESTION_SET_NAME FROM $TABLE_NAME_QUESTION_SETS"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val questionSetName = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_SET_NAME))
            questionSets.add(questionSetName)
        }

        cursor.close()
        return questionSets
    }

    fun getQuestionsBySetName(questionSetName: String?): List<Question> {
        val questions = mutableListOf<Question>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_QUESTIONS WHERE $COLUMN_QUESTION_SET_NAME = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(questionSetName))

        while (cursor.moveToNext()) {
            val questionSetId = cursor.getLong(cursor.getColumnIndex(COLUMN_QUESTION_SET_ID))

            val question = Question(

                questionText = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT)),
                options = listOf(
                    cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_A)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_B)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_C)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OPTION_D))
                ),
                correctAnswer = cursor.getInt(cursor.getColumnIndex(COLUMN_CORRECT_ANSWER_INDEX)),
                questionSetId = questionSetId
            )
            questions.add(question)
        }

        cursor.close()
        return questions
    }


    fun getQuestionSets(): List<String> {
        val questionSets = mutableListOf<String>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_QUESTION_SETS"
        val cursor: Cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val questionSetName = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_SET_NAME))
            questionSets.add(questionSetName)
        }

        cursor.close()
        return questionSets
    }
    // Trong DatabaseHelper.kt
    fun getQuestionSetNameById(questionSetId: Long): String? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME_QUESTION_SETS,
            arrayOf(COLUMN_QUESTION_SET_NAME),
            "$COLUMN_QUESTION_SET_ID = ?",
            arrayOf(questionSetId.toString()),
            null,
            null,
            null
        )

        var questionSetName: String? = null
        if (cursor.moveToFirst()) {
            questionSetName = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_SET_NAME))
        }

        cursor.close()
        return questionSetName
    }

    fun addQuestion(question: Question) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_QUESTION_SET_ID, question.questionSetId)
        values.put(COLUMN_QUESTION_TEXT, question.questionText)
        values.put(COLUMN_OPTION_A, question.options[0])
        values.put(COLUMN_OPTION_B, question.options[1])
        values.put(COLUMN_OPTION_C, question.options[2])
        values.put(COLUMN_OPTION_D, question.options[3])
        values.put(COLUMN_CORRECT_ANSWER_INDEX, question.correctAnswer)

        db.insert(TABLE_NAME_QUESTIONS, null, values)
        Log.d("YourTag", "Question added to database")
        db.close()
    }
    fun updateUser(email: String, editedUser: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_USERNAME, editedUser.username)
        values.put(COLUMN_EMAIL, editedUser.email)
        values.put(COLUMN_ROLE, editedUser.role)
        values.put(COLUMN_PASSWORD, editedUser.Password)

        val whereClause = "$COLUMN_EMAIL = ?"
        val whereArgs = arrayOf(email)

        // Thực hiện cập nhật thông tin người dùng dựa trên email
        val result = db.update(TABLE_NAME_USERS, values, whereClause, whereArgs)

        db.close()

        return result != -1
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(email))
        return if (cursor.moveToFirst()) {
            val user = User(
                cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }



}