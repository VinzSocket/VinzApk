package com.example.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onLogin: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        TimeBasedBackground()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(28.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Melofy",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
            )
            Text(
                text = "Nikmati Musik Favoritmu Dimana Saja",
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = ""
                            try {
                                val credentialManager = CredentialManager.create(context)
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                                    .build()
                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()
                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = context as Activity
                                )
                                val googleIdTokenCredential =
                                    GoogleIdTokenCredential.createFrom(result.credential.data)
                                val authCredential = GoogleAuthProvider.getCredential(
                                    googleIdTokenCredential.idToken, null
                                )
                                FirebaseAuth.getInstance()
                                    .signInWithCredential(authCredential)
                                    .addOnSuccessListener { authResult ->
                                        val user = authResult.user
                                        onLogin(user?.displayName ?: user?.email ?: "User")
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = e.message ?: "Login gagal"
                                        isLoading = false
                                    }
                            } catch (e: GetCredentialException) {
                                errorMessage = e.message ?: "Login dibatalkan"
                                isLoading = false
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Terjadi kesalahan"
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF38BDF8).copy(alpha = 0.8f),
                                        Color(0xFF818CF8).copy(alpha = 0.8f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Login dengan Google",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            FirebaseAuth.getInstance().signInAnonymously()
                                .addOnSuccessListener { onLogin("Guest") }
                                .addOnFailureListener { e ->
                                    errorMessage = e.message ?: "Login guest gagal"
                                    isLoading = false
                                }
                        }
                    }
                ) {
                    Text("Masuk sebagai Tamu", color = Color.LightGray, fontSize = 13.sp)
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
