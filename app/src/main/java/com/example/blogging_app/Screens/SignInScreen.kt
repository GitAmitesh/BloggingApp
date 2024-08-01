package com.example.blogging_app.Screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.blogging_app.R
import com.example.blogging_app.viewmodel.AuthViewModel

@Composable
fun SignInScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val context = LocalContext.current
    val error by authViewModel.error.observeAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    firebaseUser?.let {
        LaunchedEffect(firebaseUser) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
            navController.navigate("bottomnav") {
                popUpTo("signin") { inclusive = true }
            }
        }
    }

    error?.let {
        LaunchedEffect(error) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            authViewModel.clearError() // Clear the error after showing the toast to avoid repeated toasts
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFfafafa))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_blog),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome Back!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Login to your account",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontWeight = FontWeight.Bold) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Email, contentDescription = "Email Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontWeight = FontWeight.Bold) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Password Icon")
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password Button
            TextButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = Color.Blue)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        authViewModel.login(email, password, context)
                    } else {
                        Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9E6BFF),
                    contentColor = Color.White
                )
            ) {
                androidx.compose.material3.Text(
                    text = "Sign In",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(onClick = {
                navController.navigate("signup") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }) {
                Row {
                    Text(
                        text = "Don't have an account?",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Sign up", color = Color.Blue, fontWeight = FontWeight.Normal)
                }
            }
        }

        if (showResetDialog) {
            ResetPasswordDialog(
                onDismiss = { showResetDialog = false },
                onResetPassword = { email ->
                    authViewModel.resetPassword(email, context)
                    showResetDialog = false
                }
            )
        }
    }
}

@Composable
fun ResetPasswordDialog(onDismiss: () -> Unit, onResetPassword: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    androidx.compose.material.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Reset Password", fontWeight = FontWeight.SemiBold)
        },
        text = {
            Column {
                Text(text = "Enter your email to receive a password reset link.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Email, contentDescription = "Email Icon")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        onResetPassword(email)
                    } else {
                        Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9d6bfe)),
            )
            {
                Text("Send Reset Link", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


