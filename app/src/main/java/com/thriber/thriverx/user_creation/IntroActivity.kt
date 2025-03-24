package com.thriber.thriverx.user_creation

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.Spacer
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thriber.thriverx.R
import com.thriber.thriverx.user_creation.ui.theme.bg_button_dark_blue
import com.thriber.thriverx.user_creation.ui.theme.bg_button_light_blue
import com.thriber.thriverx.user_creation.ui.theme.bg_main
import com.thriber.thriverx.user_creation.ui.theme.dark_red
import com.thriber.thriverx.user_creation.ui.theme.light_red

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicIntro()
        }
    }


    @Composable
    fun BasicIntro() {
        val mcontext = LocalContext.current
        Box(
            modifier = Modifier
                .background(color = bg_main)
        ) {



            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())


            )

            {
                Spacer(modifier = Modifier.height(90.dp))

//                Text(
//                    text = "We are your solution for data managment for your hospital patner with us on \n  on this beautiful journey",
//                    fontSize = 25.sp,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(0.dp, 50.dp, 0.dp, 0.dp)
//                )
                Image(
                    painter = painterResource(id = R.drawable.thriverlogo),
                    contentDescription = "Logo Image",
                    modifier = Modifier.run {width(130.dp)
                                        .height(120.dp)
                        .align(Alignment.CenterHorizontally)
                    }
                )


                Column (modifier = Modifier.padding(top = 30.dp)){
                        Button(
                            onClick = {
                                mcontext.startActivity(Intent(mcontext, SingUpActivity::class.java))

                            }, modifier = Modifier.bounceClick()
                                .fillMaxWidth()

                                .padding(20.dp, 20.dp, 20.dp, 0.dp)   .graphicsLayer(
                                    shadowElevation = 20f, // Elevation for the shadow
                                    shape = RoundedCornerShape(12.dp),
                                    ambientShadowColor = light_red, // Deep black for shadow
                                    spotShadowColor = dark_red
                                                             )
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            bg_button_light_blue,
                                            bg_button_dark_blue,
                                            light_red,
                                            dark_red
                                        )
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ).border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            bg_button_light_blue,
                                            bg_button_dark_blue
                                        )
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(15.dp),
                            elevation = null

                        ) {
                            Text(text = "Sign-Up",fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }


                    Button(
                        onClick = {
                            mcontext.startActivity(
                                Intent(mcontext, LoginActivity::class.java)
                            )

                        }, modifier = Modifier.bounceClick()
                            .fillMaxWidth()

                            .padding(20.dp, 20.dp, 20.dp, 0.dp)   .graphicsLayer(
                                shadowElevation = 30f, // Elevation for the shadow
                                shape = RoundedCornerShape(20.dp),
                                ambientShadowColor = light_red,
                                spotShadowColor = dark_red
                            )
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        bg_button_light_blue,
                                        bg_button_dark_blue,
                                        light_red,
                                        dark_red
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            ).border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(

                                        bg_button_light_blue,
                                        bg_button_dark_blue
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(15.dp),
                        elevation = null

                    ) {
                        Text(text = "Login",fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    @Preview(showBackground = true, device = Devices.PIXEL, showSystemUi = true)
    @Composable
    fun intro() {
        BasicIntro()
    }

}