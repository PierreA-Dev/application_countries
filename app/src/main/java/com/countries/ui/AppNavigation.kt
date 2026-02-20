package com.countries.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            CountriesScreen(
                onCountryClick = { code ->
                    navController.navigate("detail/$code")
                }
            )
        }

        composable(
            route = "detail/{countryCode}",
            arguments = listOf(navArgument("countryCode") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("countryCode")
            CountryDetailScreen(countryCode = code ?: "")
        }
    }
}