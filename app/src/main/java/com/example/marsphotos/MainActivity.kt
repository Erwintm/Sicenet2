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
import com.example.marsphotos.ui.screens.CargaAcademicaScreen
import com.example.marsphotos.ui.screens.LoginPantalla
import com.example.marsphotos.ui.screens.MenuScreen
import com.example.marsphotos.ui.screens.PerfilPantalla
import com.example.marsphotos.ui.screens.KardexScreen // Asegúrate de que esta importación esté
// Importación para el futuro
import com.example.marsphotos.ui.theme.MarsPhotosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarsPhotosTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        // 1. Pantalla de Login
                        composable("login") {
                            LoginPantalla(onLoginSuccess = { matricula ->
                                // Al loguear con éxito, vamos al perfil pasando la matrícula
                                navController.navigate("perfil/$matricula")
                            })
                        }

                        // 2. Pantalla de Perfil (Recibe parámetro)
                        composable("perfil/{matricula}") { backStackEntry ->
                            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
                            PerfilPantalla(
                                matricula = matricula,
                                navController = navController
                            )
                        }

                        // 3. Menú Principal
                        composable("menu") {
                            MenuScreen(navController = navController)
                        }

                        // 4. Carga Académica (La que hace tu amigo)
                        composable("carga") {
                            CargaAcademicaScreen(navController = navController)
                        }

                        // 5. Kardex Escolar (La que estás haciendo tú)
                        composable("kardex") {
                            KardexScreen(navController = navController)
                        }

                        // 6. Calificaciones por Unidad

                    }
                }
            }
        }
    }
}