package com.example.prepgenius

const val APP_NAME = "Prep Genius"
const val ROOT = "root"
const val BEGIN = "begin"
const val MIDDLE = "middle"
sealed class Screens (
    val route : String
){
    data object LoginScreen : Screens(route = "login_screen")
    data object RegisterScreen : Screens(route = "register_screen")
    data object HomeScreen : Screens(route = "home_screen")
    data object SpecificSubjectsScreen : Screens(route = "specific_subjects_screen")
    data object QuestionUploadScreen : Screens(route = "question_upload_screen")
    data object InstructionScreen : Screens(route = "instruct_screen")
    data object TestScreen : Screens(route = "test_screen")
    data object ScorecardScreen : Screens(route = "scorecard_screen")

}