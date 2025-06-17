package com.example.offer_generator.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import kotlinx.coroutines.delay

@Composable
fun bottomBar(navController: NavController, whoLoginViewModel: WhoLoginViewModel = WhoLoginViewModel()) {
    val email by remember { mutableStateOf("contact@lokachakra.com") }
    val phone by remember { mutableStateOf("+91 1234567890") }

    // Always show animations - no state needed
    val isVisible = true

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 800)
        ) + fadeIn(animationSpec = tween(durationMillis = 800))
    ) {
        Column(
            Modifier.padding(bottom = 12.dp)
                .fillMaxSize()
                .background(colorResource(id = R.color.topbarbackgound))
        ) {
            Spacer(Modifier.height(25.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "About",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.Magenta
                )
            }

            // Animated sections with staggered delays for smooth sequence
            AnimatedBottomSection(
                whoLoginViewModel = whoLoginViewModel,
                navController = navController,
                title = "Lokachakra",
                content = "Empowering the next generation of talent through meaningful internship opportunities.",
                delay = 100
            )

            AnimatedBottomSection(
                whoLoginViewModel = whoLoginViewModel,
                navController = navController,
                title = "Quick Links",
                content = null,
                delay = 200,
                hasButtons = true
            )

            AnimatedBottomSection(
                whoLoginViewModel = whoLoginViewModel,
                navController = navController,
                title = "Contact",
                content = "E-Mail : $email\nPhone : $phone",
                delay = 500
            )

            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))
            AnimatedVisibility(
                visible = isVisible, // Uses the same visibility state
                enter = fadeIn(animationSpec = tween(2000))
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("© 2025 Lokachakra. All rights reserved.")
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun AnimatedBottomSection(
    whoLoginViewModel: WhoLoginViewModel,
    navController: NavController,
    title: String,
    content: String?,
    delay: Long,
    hasButtons: Boolean = false
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = title) { // Use title as key to prevent reset on recomposition
        delay(delay)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(durationMillis = 600)
        ) + fadeIn(animationSpec = tween(durationMillis = 600))
    ) {
        Column {
            Row(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                )
            }

            Row(Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                if (hasButtons) {
                    Column {
                        AnimatedBottomButton(onclick = {navController.navigate(Screen.HomeScreen.route)},"Home", 1000)
                        AnimatedBottomButton(onclick = {navController.navigate(Screen.AvailableJobRoles.route)},"Available Roles", 120)
                        AnimatedBottomButton(onclick = {if(whoLoginViewModel.isUserLoggedIn.value) navController.navigate(Screen.ApplicationForm.route)
                        else navController.navigate(Screen.LoginScreen.route)},"Apply Now", 1400)
                    }
                } else if (content != null) {
                    if (content.contains('\n')) {
                        Column {
                            content.split('\n').forEach { line ->
                                Text(line, color = Color.White)
                            }
                        }
                    } else {
                        Text(content, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedBottomButton(onclick: () -> Unit,text: String, delay: Long) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = text) { // Use text as key to prevent reset
        delay(delay)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(durationMillis = 500)
                )
    ) {
        TextButton(onClick = { onclick()}) {
            Text(
                text,
                fontSize = 16.sp,
                color = colorResource(id = R.color.white)
            )
        }
    }
}