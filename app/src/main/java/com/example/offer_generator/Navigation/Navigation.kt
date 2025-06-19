package com.example.offer_generator.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.offer_generator.Screens.Internship.InternDashboard
import com.example.offer_generator.Screens.HR.HRDashboard
import com.example.offer_generator.Screens.HR.JobOpeningsManager
import com.example.offer_generator.Screens.HR.JobOpeningsRepository
import com.example.offer_generator.Screens.HomeScreen
import com.example.offer_generator.Screens.AvailableJobRoles
import com.example.offer_generator.Screens.Login
import com.example.offer_generator.Screens.OfferLetters.OfferLetterGenerator
import com.example.offer_generator.Screens.Registration
import com.example.offer_generator.StartScreen
import com.example.offer_generator.ViewModels.JobOpeningsViewModel

@Composable
fun Navigation(navController: NavHostController = rememberNavController()){
    val repository = remember { JobOpeningsRepository() }
    val whoLoginViewModel : WhoLoginViewModel = viewModel()
    val viewModel: JobOpeningsViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {

        composable(Screen.StartScreen.route) {
            StartScreen(navController, viewModel = whoLoginViewModel)
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController, whoLoginViewModel)
        }
        composable(Screen.AvailableJobRoles.route) {
            AvailableJobRoles(navController,whoLoginViewModel,viewModel)
        }
        composable(Screen.LoginScreen.route) {
            Login(navController, whoLoginViewModel)
        }
        composable(Screen.RegisterScreen.route) {
            Registration(navController)
        }

//        candidate
        composable(Screen.CandidateDashboard.route) {
            InternDashboard(navController,whoLoginViewModel)
        }
        composable(Screen.ApplicationForm.route) {
            ApplicationScreen(navController,whoLoginViewModel,viewModel)
        }

//        HR

        composable(Screen.HrDashboard.route) {
            HRDashboard(navController,whoLoginViewModel)
        }

        composable(Screen.JobOpeningsManager.route) {
            JobOpeningsManager(navController = navController, viewModel)
        }

        composable(
            route = "offer_generator_screen/{applicationId}/{applicationType}",
            arguments = listOf(
                navArgument("applicationId") { type = NavType.StringType },
                navArgument("applicationType") { type = NavType.StringType }
            )
        ) {
            OfferLetterGenerator(
                navController,
                whoLoginViewModel,
                it.arguments?.getString("applicationId"),
                it.arguments?.getString("applicationType")
            )
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