package com.test.otp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.test.otp.ui.screens.DetailScreen
import com.test.otp.ui.screens.MainScreen
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(navController: NavHostController, feedback: (String) -> Unit ) {
    SharedTransitionLayout {
        NavHost(
            navController,
            startDestination = ScreenMain,
        ) {
            composable<ScreenMain> {
                MainScreen(navController, this)
            }
            composable<ScreenDetail> {
                feedback(it.toRoute<ScreenDetail>().search)
                DetailScreen(args = it.toRoute<ScreenDetail>(), this)
            }
        }
    }
}

@Serializable
object ScreenMain

@Serializable
data class ScreenDetail(
    val id: String,
    val search: String
)