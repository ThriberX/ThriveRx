package com.thriber.thriverx.user_creation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import com.thriber.thriverx.ui.screens.IntroScreen
import com.thriber.thriverx.ui.screens.LoginScreen
import com.thriber.thriverx.ui.screens.SignupScreen
import com.thriber.thriverx.ui.screens.ResetPasswordScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thriber.thriverx.user_creation.RegistrationViewModel.LoginViewModel
import com.thriber.thriverx.user_creation.RegistrationViewModel.SignupViewModel

@Composable
fun RegistrationNavHost(onAuthenticated: () -> Unit) {
    val navController = rememberNavController()
    // This creates the FirebaseDao instance to be shared across ViewModels
    val firebaseDao = FirebaseDao()

    NavHost(navController = navController, startDestination = AuthRoutes.Intro.route) {

        // 1. Intro Screen
        composable(AuthRoutes.Intro.route) {
            IntroScreen(onNext = { navController.navigate(AuthRoutes.Login.route) })
        }

        // 2. Login Screen
        composable(AuthRoutes.Login.route) {
            val vm: LoginViewModel = viewModel(
                factory = viewModelFactory { initializer { LoginViewModel(firebaseDao) } }
            )
            LoginScreen(
                viewModel = vm,
                onSuccess =onAuthenticated  ,
                onNavigateToSignup = { navController.navigate(AuthRoutes.Signup.route) },
                onNavigateToReset = { navController.navigate(AuthRoutes.ResetPassword.route) }
            )
        }

        // 3. Signup Screen
        composable(AuthRoutes.Signup.route) {
            val vm: SignupViewModel = viewModel(
                factory = viewModelFactory { initializer { SignupViewModel(firebaseDao) } }
            )
            SignupScreen(
                viewModel = vm,
                onSuccess = onAuthenticated

            )
        }

        // 4. Reset Password Screen
        composable(AuthRoutes.ResetPassword.route) {
            ResetPasswordScreen(onBack = { navController.popBackStack() })
        }
    }
}