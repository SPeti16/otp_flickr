package com.test.otp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.test.otp.R
import com.test.otp.navigation.AppNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlickApp(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    var search by remember { mutableStateOf("") }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isMainScreen = navBackStackEntry?.destination?.route?.contains(stringResource(R.string.screen_main)) == true
    Scaffold(
        topBar = {
                TopAppBar(
                    title = { Text(if(isMainScreen) stringResource(R.string.main_title) else search) },
                    navigationIcon =
                    {
                        AnimatedVisibility(!isMainScreen) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            modifier = Modifier.testTag("dark_light_mode"),
                            onClick = onToggleTheme
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                contentDescription = null
                            )
                        }
                    }
                )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
                AppNavHost(
                    navController = navController
                ) {
                    search = it
                }
        }
    }
}