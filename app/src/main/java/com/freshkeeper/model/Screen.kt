package com.freshkeeper.model

sealed class Screen(
    val route: String,
) {
    data object SignIn : Screen("signIn")

    data object SignUp : Screen("signUp")

    data object EmailSignUp : Screen("emailSignUp")

    data object NameInput : Screen("nameInput")

    data object SelectProfilePicture : Screen("selectProfilePicture")

    data object EmailSignIn : Screen("emailSignIn")

    data object Home : Screen("home")

    data object Chat : Screen("chat")

    data object Inventory : Screen("inventory")

    data object Household : Screen("household")

    data object Settings : Screen("settings")

    data object Notifications : Screen("notifications")

    data object ProfileSettings : Screen("profileSettings")

    data object NotificationSettings : Screen("notificationSettings")

    data object HouseholdSettings : Screen("householdSettings")

    data object Statistics : Screen("statistics")

    data object LandingPage : Screen("landingPage")

    data object Tips : Screen("tips")

    data object Contact : Screen("contact")

    data object Help : Screen("help")
}
