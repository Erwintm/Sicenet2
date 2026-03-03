package com.example.marsphotos

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.marsphotos.model.CargaViewModel
import com.example.marsphotos.model.CargaViewModelFactory
import com.example.marsphotos.ui.screens.*
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
                                navController.navigate("perfil/$matricula")
                            })
                        }

                        // 2. Pantalla de Perfil
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

                        // 4. Carga Académica
                        composable("carga") {
                            val context = LocalContext.current
                            val app = context.applicationContext as MarsPhotosApplication
                            val isOnline = isNetworkAvailable(context)
                            val viewModel: CargaViewModel = viewModel(
                                factory = CargaViewModelFactory(app.container.snRepository, app)
                            )
                            CargaAcademicaScreen(
                                navController = navController,
                                viewModel = viewModel,
                                isOnline = isOnline
                            )
                        }

                        // 5. Kardex Escolar
                        composable("kardex") {
                            KardexScreen(navController = navController)
                        }

                        // 6. Calificaciones por Unidad (CORREGIDO)
                        composable("notas") {
                            val context = LocalContext.current
                            val app = context.applicationContext as MarsPhotosApplication
                            val repository = app.container.snRepository

                            val viewModel: NotasUnidadesViewModel = viewModel(
                                factory = NotasUnidadesViewModelFactory(app, repository)
                            )
                            NotasUnidadesScreen(viewModel = viewModel)
                        }

                        // 7. Calificaciones Finales (CORREGIDO)
                        composable("finales") {
                            val context = LocalContext.current
                            val app = context.applicationContext as MarsPhotosApplication
                            val repository = app.container.snRepository

                            val viewModel: CalifFinalViewModel = viewModel(
                                factory = CalifFinalViewModelFactory(app, repository)
                            )
                            CalifFinalScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}