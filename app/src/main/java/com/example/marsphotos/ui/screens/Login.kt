package com.example.marsphotos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.R

@Composable
fun LoginPantalla(
    viewModel: LoginViewModel = viewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.login), fontSize = 26.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.usuario,
            onValueChange = { viewModel.usuario = it },
            label = { Text(stringResource(R.string.usuario)) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text(stringResource(R.string.contrasena)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.entrar))
        }

        viewModel.mensajeError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}
