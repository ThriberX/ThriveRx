package com.thriber.thriverx.user_creation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.thriber.thriverx.FirebaseClass.DataInterface.DataInterface
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import com.thriber.thriverx.R
import kotlinx.coroutines.launch

class ResetPasswordActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = 0xFF0F192E.toInt()
        auth = FirebaseAuth.getInstance()
        setContent {
            ResetPasswordScreen()
        }
    }

    @Composable
    fun CenteredImage() {
        Image(
            painter = painterResource(id = R.drawable.thriverlogo),
            contentDescription = "Logo Image",
            modifier = Modifier
                .width(130.dp)
                .height(120.dp)
        )
    }

    @Composable
    fun ResetPasswordScreen() {
        val context = LocalContext.current
        var email by remember { mutableStateOf("") }
        val emailFocusRequester = remember { FocusRequester() }

        

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F192E))
                .padding(horizontal = 16.dp)
                .padding(top = 40.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Image
            CenteredImage()

            // Header text
            Text(
                text = "Reset Password",
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            )

            // Email Input Field
            Text(
                text = "Email",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    if (email.isEmpty()) {
                        Text(
                            text = "example@example.com",
                            color = Color.Gray,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                ),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .focusRequester(emailFocusRequester)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(55.dp)
            )

            // Reset Password Button
            Button(
                onClick = { passwordResetEmail(email) },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(65.dp)
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0048A5)
                )
            ) {
                Text(
                    text = "Send Reset Link",
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Back to Login",
                color = Color(0xFF2260FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        if (context is ComponentActivity) {
                            context.finish()
                        }
                    }
            )
        }
    }

    private val password: DataInterface = FirebaseDao()

    private fun  passwordResetEmail(email: String) {
        if (email.isNotEmpty()) {

            lifecycleScope.launch {
                try {
                   password.passwordResetEmail(email)

                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "password email sent.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                catch (e:Exception){
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Error sending password reset email: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

        }

        else{
            Toast.makeText(
                this@ResetPasswordActivity,
                "Please enter a valid email",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewResetPasswordScreen() {
        ResetPasswordScreen()
    }
}
