package com.thriber.thriverx.user_creation


import android.content.Intent
import android.os.Bundle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.thriber.thriverx.PreviewActivity
import com.thriber.thriverx.R
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.lifecycleScope
import com.thriber.thriverx.FirebaseClass.DataInterface.DataInterface
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import com.thriber.thriverx.user_creation.ui.theme.bg_main
import com.thriber.thriverx.user_creation.ui.theme.button_login_signup
import com.thriber.thriverx.user_creation.ui.theme.login_text
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            auth = FirebaseAuth.getInstance()
            mainScreen()
        }
    }

    @Composable
    fun centeredImage() {
        Image(
            painter = painterResource(id = R.drawable.thriverlogo),
            contentDescription = "Logo Image",
            modifier = Modifier
                .width(130.dp)
                .height(120.dp)
        )
    }
    @Composable
    fun loginScreen() {
        val context = LocalContext.current
        var loginUserName by rememberSaveable { mutableStateOf("") }
        var loginUserPassword by rememberSaveable { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        val emailFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg_main)
                .padding(horizontal = 16.dp)
                .padding(top = 40.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Email",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                )



                        OutlinedTextField(
                            value = loginUserName,
                            onValueChange = { loginUserName = it },
                            placeholder = {
                                if (loginUserName.isEmpty()) {
                                    Text(
                                        text = "example@example.com",
                                        color = Color.Gray,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                            ),
                            shape = RoundedCornerShape(15.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    passwordFocusRequester.requestFocus()
                                }
                            ),
                            modifier = Modifier
                                .focusRequester(emailFocusRequester)
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                                .height(55.dp)
                        )


                Text(
                    text = "Password",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                )

                OutlinedTextField(
                    value = loginUserPassword,
                    onValueChange = { loginUserPassword = it },
                    placeholder = {
                        if (loginUserPassword.isEmpty()) {
                            Text(
                                text = "**********",
                                color = Color.Gray,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        Icon(
                            imageVector = icon,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier
                                .clickable { passwordVisible = !passwordVisible }
                                .background(Color.Transparent)
                                .padding(8.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .focusRequester(passwordFocusRequester)
                        .fillMaxWidth()
                        .height(55.dp)
                )

                Text(
                    text = "Forgot Password",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                        .clickable {


                                val intent = Intent(context, ResetPasswordActivity::class.java)
                                context.startActivity(intent)

                        }
                )


                Button(
                    onClick = { login( loginUserName, loginUserPassword) },
                    modifier = Modifier.bounceClick()
                        .fillMaxWidth(0.6f)
                        .height(65.dp)
                        .padding(top = 25.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = button_login_signup
                    )
                ) {
                    Text(
                        text = "Log In",
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }


                Text(
                    text = "Don't have an account?",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp)
                )
                val context= LocalContext.current
                    Text(
                        text = "Sign Up",
                        color = login_text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 1.dp)
                            .clickable {

                                val intent=Intent(context,SingUpActivity::class.java)
                                context.startActivity(intent)
                                finish()
                            }
                    )

            }
        }
    }

    @Composable
    fun mainScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F192E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .offset(y = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    centeredImage()
                }

                Spacer(modifier = Modifier.height(1.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 140.dp)
                ) {
                    loginScreen()
                }
            }
        }
    }
    private val loginDao: DataInterface = FirebaseDao()

    fun login( username: String, password: String) {
         lifecycleScope.launch{
             try{
                 val userId=loginDao.login(username,password)
                 Toast.makeText(
                     this@LoginActivity,
                     "Welcome to thriveRx.",
                     Toast.LENGTH_LONG
                 ).show()
                 val intent = Intent(this@LoginActivity, PreviewActivity::class.java).apply {
                     putExtra("USER_ID", userId)
                 }
                 startActivity(intent)
                 this@LoginActivity.finish()
             }
             catch (e:Exception){
                 Toast.makeText(this@LoginActivity,e.message,Toast.LENGTH_LONG).show()
             }
         }
    }

    @Preview(showSystemUi = true, showBackground = true)
    @Composable
    fun previewLoginScreen() {
        mainScreen()
    }
}