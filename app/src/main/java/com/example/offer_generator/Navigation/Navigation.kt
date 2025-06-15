package com.example.offer_generator.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.offer_generator.Screens.ApplicationForm.ApplicationScreen
import com.example.offer_generator.Screens.Freelancer.FreelancerDashboard
import com.example.offer_generator.Screens.FulltimeJob.FullTimeDashboard
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.Screens.Internship.CandidateDashboard
import com.example.offer_generator.Screens.HR.HRDashboard
import com.example.offer_generator.Screens.HomeScreen
import com.example.offer_generator.Screens.Internship
import com.example.offer_generator.Screens.Login
import com.example.offer_generator.Screens.OfferLetters.OfferLetterGenerator
import com.example.offer_generator.Screens.Registration
import com.example.offer_generator.StartScreen

@Composable
fun Navigation(navController: NavHostController = rememberNavController()){
    val whoLoginViewModel : WhoLoginViewModel = viewModel()
    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {

        composable(Screen.StartScreen.route) {
            StartScreen(navController, viewModel = whoLoginViewModel)
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController, whoLoginViewModel)
        }
        composable(Screen.InternshipScreen.route) {
            Internship(navController,whoLoginViewModel)
        }
        composable(Screen.LoginScreen.route) {
            Login(navController, whoLoginViewModel)
        }
        composable(Screen.RegisterScreen.route) {
            Registration(navController)
        }

//        candidate
        composable(Screen.CandidateDashboard.route) {
            CandidateDashboard(navController,whoLoginViewModel)
        }
        composable(Screen.ApplicationForm.route) {
            ApplicationScreen(navController,whoLoginViewModel)
        }

//        HR

        composable(Screen.HrDashboard.route) {
            HRDashboard(navController,whoLoginViewModel)
        }

        composable(
            route = "offer_generator_screen/{applicationId}",
            arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
        ) {
            OfferLetterGenerator(navController, whoLoginViewModel, it.arguments?.getString("applicationId"),"freelancer")
        }

//       Freelancer

        composable(Screen.FlDashboard.route) {
            FreelancerDashboard(navController,whoLoginViewModel)
        }

//        Full time

        composable(Screen.FullTimejobDashboard.route){
            FullTimeDashboard(navController,whoLoginViewModel)
        }
    }
}