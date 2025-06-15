package com.example.offer_generator.Navigation

sealed class Screen(val route: String) {
    object StartScreen : Screen("start_screen")
    object LoginScreen : Screen("login_screen")
    object RegisterScreen : Screen("register_screen")
    object HomeScreen : Screen("home_screen")
    object InternshipScreen : Screen("internship_screen")

//    candidate
    object CandidateDashboard : Screen("candidate_dashboard")
    object ApplicationForm : Screen("application_form")

//    HR
    object HrDashboard : Screen("hr_dashboard")

//    freelancer
    object FlDashboard : Screen("freelancer_dashboard")

//    Full time
    object FullTimejobDashboard : Screen("fulltime_dashboard")

}