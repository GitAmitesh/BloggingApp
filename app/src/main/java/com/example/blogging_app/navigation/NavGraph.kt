
package com.example.blogging_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.blogging_app.Screens.*
import com.example.blogging_app.Screens.HomeScreen

@Composable fun NavGraph(NavController:NavHostController){
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) { SplashScreen(navController) }
        composable(Routes.SignIn.route) { SignInScreen(navController) }
        composable(Routes.SignUp.route) { SignupScreen(navController) }
        composable(Routes.BottomNav.route) { BottomNav(navController) }
        composable(Routes.Home.route) { HomeScreen(navController) }
        composable(Routes.SavedBlog.route) { SavedBlog(navController) }
        composable(Routes.Search.route) { Search(navController) }
        composable(Routes.Profile.route) { Profile(navController) }
        composable(Routes.OtherUsers.route) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("data")
            uid?.let {
                OtherUserProfile(navController, it)
            }
        }
        composable(Routes.Edit.route) { backStackEntry ->
            val threadId = backStackEntry.arguments?.getString("threadId")
            threadId?.let {
                EditPost(navController, it)
            }
        }
    }
}
