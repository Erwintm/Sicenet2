package com.example.marsphotos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.marsphotos.ui.screens.LoginPantalla
import com.example.marsphotos.ui.screens.MenuScreen
import com.example.marsphotos.ui.screens.PerfilPantalla
import com.example.marsphotos.ui.theme.MarsPhotosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarsPhotosTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginPantalla(onLoginSuccess = { matricula ->
                                navController.navigate("perfil/$matricula")
                            })
                        }
                        composable("perfil/{matricula}") { backStackEntry ->
                            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
                            PerfilPantalla(matricula = matricula)
                        }
                        composable("menu") {
                            MenuScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
