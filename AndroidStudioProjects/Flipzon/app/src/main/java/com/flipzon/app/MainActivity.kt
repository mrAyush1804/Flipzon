package com.flipzon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flipzon.app.core.datastore.SessionManager
import com.flipzon.app.feature.cart.data.local.CartDatabase
import com.flipzon.app.feature.splash.TwinLinesAnimationWrapper
import com.flipzon.app.navigation.NavGraph
import com.flipzon.app.navigation.Screen
import com.flipzon.app.navigation.navigateToBottomBarRoute
import com.flipzon.app.core.ui.theme.components.FlipzonBottomBar
import com.flipzon.app.core.ui.theme.components.FlipzonTopBar
import com.flipzon.app.core.ui.theme.theme.FlipzonTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var cartDatabase: CartDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val startDestination = runBlocking {
            if (sessionManager.sessionFlow.first() != null) {
                Screen.Home.route
            } else {
                Screen.Login.route
            }
        }

        setContent {

            FlipzonTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val scope = rememberCoroutineScope()
                val sessionData by sessionManager.sessionFlow.collectAsState(initial = null)

                Scaffold(
                    topBar = {

                        if (currentRoute != Screen.Login.route && sessionData != null) {
                            FlipzonTopBar(
                                imageUrl = sessionData?.imageUrl ?: "",
                                fullName = "${sessionData?.firstName} ${sessionData?.lastName}",
                                email = sessionData?.email ?: "",
                                onLogoutClick = {

                                    scope.launch {
                                        sessionManager.clearSession()
                                        cartDatabase.clearAllTables()
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentRoute != Screen.Login.route) {
                            FlipzonBottomBar(
                                currentRoute = currentRoute,
                                onItemClick = { route ->
                                    navController.navigateToBottomBarRoute(route)
                                }
                            )
                        }
                    }
                ) { innerPadding ->


                    TwinLinesAnimationWrapper(
                        content = {
                            NavGraph(
                                navController = navController,
                                startDestination = startDestination,
                                modifier = Modifier.padding(innerPadding)
                            )
                        },
                        Logo = "FLIPZON"
                    )

                }
            }
        }
    }
}
