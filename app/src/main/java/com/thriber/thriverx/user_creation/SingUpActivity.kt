package com.thriber.thriverx.user_creation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope

import com.thriber.thriverx.FirebaseClass.DataInterface.DataInterface
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import com.thriber.thriverx.PreviewActivity
import com.thriber.thriverx.R
import com.thriber.thriverx.user_creation.ui.theme.bg_main
import com.thriber.thriverx.user_creation.ui.theme.button_login_signup
import kotlinx.coroutines.launch


class SingUpActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            greeting()
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





    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun signUpScreen() {

        var New_userName by rememberSaveable { mutableStateOf("") }
        var New_Password by rememberSaveable { mutableStateOf("") }
        val (passwordVisible, setPasswordVisible) = remember { mutableStateOf(false) }
        val emailFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg_main)
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = "Sign up to begin",
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Email",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 1.dp)
                    )

                    OutlinedTextField(
                        value = New_userName,
                        onValueChange = { New_userName = it },
                        placeholder = {
                            if (New_userName.isEmpty()) {
                                Text(
                                    text = "Enter your email",
                                    color = Color.Gray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White
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
                        value = New_Password,
                        onValueChange = { New_Password = it },
                        trailingIcon = {
                            val icon = if (passwordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                modifier = Modifier
                                    .clickable { setPasswordVisible(!passwordVisible) }
                                    .background(Color.Transparent)
                                    .padding(8.dp)
                            )
                        },
                        placeholder = {
                            if (New_Password.isEmpty()) {
                                Text(
                                    text = "Enter password",
                                    color = Color.Gray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .focusRequester(passwordFocusRequester)
                            .fillMaxWidth()
                            .height(55.dp)
                    )


                }
            }

            Button(
                onClick = { register( New_userName, New_Password) },
                modifier = Modifier
                    .bounceClick()
                    .fillMaxWidth(0.6f)
                    .height(65.dp)
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = button_login_signup
                )
            ) {
                Text(
                    text = "Sign Up",
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
        }
    }






    @Composable
    fun MainScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F192E))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(120.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, bottom = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    centeredImage()
                }

                Spacer(modifier = Modifier.height(1.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(bottom = 140.dp)
                ) {
                    signUpScreen()
                }
            }
        }
    }

   private val SingupDao: DataInterface = FirebaseDao()

    fun register(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val userid = SingupDao.register(username, password)
                Toast.makeText(
                    this@SingUpActivity,
                    "Registration successful! Verification email sent.",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this@SingUpActivity, PreviewActivity::class.java).apply {
                    putExtra("USER_ID", userid)
                }
                startActivity(intent)
                this@SingUpActivity.finish()
            }
            catch (e:Exception){
                Toast.makeText(this@SingUpActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    @Preview(showSystemUi = true , showBackground = true)
    @Composable
    private fun greeting() {
        MainScreen()
        signUpScreen()
        centeredImage()

    }

}