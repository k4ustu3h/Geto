package com.android.geto.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.geto.common.NAV_KEY_APP_NAME
import com.android.geto.common.NAV_KEY_PACKAGE_NAME
import com.android.geto.common.Screens
import com.android.geto.presentation.user_app_list.UserAppListScreen
import com.android.geto.presentation.user_app_settings.UserAppSettingsScreen
import com.android.geto.ui.theme.DevOpsHideNoRootTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DevOpsHideNoRootTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController, startDestination = Screens.UserAppList.route
                    ) {
                        composable(route = Screens.UserAppList.route) {
                            UserAppListScreen(navController = navController)
                        }

                        composable(route = Screens.UserAppSettings.route + "/{$NAV_KEY_PACKAGE_NAME}/{$NAV_KEY_APP_NAME}") {
                            UserAppSettingsScreen()
                        }
                    }
                }
            }
        }
    }
}