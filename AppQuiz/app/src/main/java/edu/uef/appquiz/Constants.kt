package edu.uef.appquiz

data class User(
    var username: String,
    val email: String,
    val role: String,
    var Password: String
) {

}

// Ví dụ đơn giản của một lớp SessionManager
class SessionManager {
    companion object {
        private var user: User? = null

        fun updateUser(updatedUser: User) {
            user = updatedUser
        }

        fun getCurrentUser(): User? {
            return user
        }

        fun clearSession() {
            user = null
        }
    }
}

const val TOTAL_QUESTIONS: String = "total_questions"
const val CORRECT_ANSWERS: String = "correct_answers"
data class Question(
    val questionSetId: Long,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: Int
) {


}
data class QuestionSet(
    val questionSetId: Long,
    val questionSetName: String?,
)
data class QuestionDetails(
    var question: String,
    var option1: String,
    var option2: String,
    var option3: String,
    var option4: String,
    var correctAnswer: String
)
