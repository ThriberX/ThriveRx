package com.thriber.thriverx.user_creation.navigation


/**
 * Navigation routes for the User Creation and Authentication flow.
 */
sealed class AuthRoutes(val route: String) {
    object Intro : AuthRoutes("auth_intro")
    object Login : AuthRoutes("auth_login")
    object Signup : AuthRoutes("auth_signup")
    object ResetPassword : AuthRoutes("auth_reset_password")
}