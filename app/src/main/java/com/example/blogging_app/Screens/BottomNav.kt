// BottomNav.kt
package com.example.blogging_app.Screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.blogging_app.R
import com.example.blogging_app.model.BottomNavItem
import com.example.blogging_app.navigation.Routes


@Composable
fun BottomNav(navController: NavHostController) {
    val navController1 = rememberNavController()
    Scaffold(
        bottomBar = { MyBottomBar(navController1) }
    ) { innerPadding ->
        NavHost(
            navController = navController1,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Home.route) { HomeScreen(navController) }
            composable(Routes.Search.route) { Search(navController1) }
            composable(Routes.Add.route) { AddPost(navController1) }
            composable(Routes.SavedBlog.route) { SavedBlog(navController1) }
            composable(Routes.Profile.route) { Profile(navController) }
        }
    }
}

@Composable
fun MyBottomBar(navController1: NavHostController) {
    val backStackEntry by navController1.currentBackStackEntryAsState()
    val items = listOf(
        BottomNavItem(
            "Home",
            Routes.Home.route,
            ImageVector.vectorResource(id = R.drawable.baseline_home_24)
        ),
        BottomNavItem(
            "Search",
            Routes.Search.route,
            ImageVector.vectorResource(id = R.drawable.baseline_search_24)
        ),
        BottomNavItem(
            "Add",
            Routes.Add.route,
            ImageVector.vectorResource(id = R.drawable.baseline_add_circle_24)
        ),
        BottomNavItem(
            "Saved",
            Routes.SavedBlog.route,
            ImageVector.vectorResource(id = R.drawable.baseline_bookmarks_24)
        ),
        BottomNavItem(
            "Profile",
            Routes.Profile.route,
            ImageVector.vectorResource(id = R.drawable.baseline_person_24)
        ),
    )

    BottomAppBar(
        containerColor = Color(0xFF635383)
    ) {
        items.forEach {
            val selected = it.route == backStackEntry?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController1.navigate(it.route) {
                        popUpTo(navController1.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.title,
                        tint = if (selected) Color(0xFF4D036F) else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                ),
            )
        }
    }
}