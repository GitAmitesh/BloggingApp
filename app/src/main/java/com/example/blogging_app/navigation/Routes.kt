package com.example.blogging_app.navigation



sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object SignIn : Routes("signin")
    object SignUp : Routes("signup")
    object BottomNav : Routes("bottomnav")

    object Home : Routes("home_route")
    object Search : Routes("search_route")
    object Add : Routes("add_route")
    object SavedBlog:Routes("saved_route")
    // object Notification : Routes("notification_route")
    object Profile : Routes("profile_route")
    object OtherUsers:Routes("other_users/{data}")
    object Edit:Routes("edit/{threadId}")
}