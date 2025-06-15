package com.example.offer_generator.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.offer_generator.common.AnimatedButton
import com.example.offer_generator.common.AnimatedNavigationItem
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.common.bottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registration(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    var isDrawerOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(
        initialValue = if (isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
    )

    // Update drawer state when isDrawerOpen changes
    LaunchedEffect(isDrawerOpen) {
        if (isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    // Update isDrawerOpen when drawer state changes
    LaunchedEffect(drawerState.isOpen) {
        isDrawerOpen = drawerState.isOpen
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.background(Color.White),
                drawerContainerColor = Color.White,
                drawerContentColor = Color.Black,
                windowInsets = WindowInsets(0)
            ) {
                // Sidebar content with slide animation
                AnimatedVisibility(
                    visible = isDrawerOpen,
                    enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) // ✅ Use slideOutHorizontally for exit
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(280.dp)
                            .background(Color.White)
                            .padding(start = 16.dp, top = 44.dp)
                    ) {
                        Text(
                            text = "Menu",
                            style = MaterialTheme.typography.h5,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        androidx.compose.material.Divider(color = Color.Gray)

                        // Animated menu items
                        AnimatedNavigationItem(
                            text = "Home",
                            delay = 100,
                            onClick = {
                                isDrawerOpen = false
                                navController.navigate(Screen.HomeScreen.route)
                            }
                        )

                        AnimatedNavigationItem(
                            text = "InternShip",
                            delay = 200,
                            onClick = {
                                isDrawerOpen = false
                                navController.navigate(Screen.InternshipScreen.route)
                            }
                        )

                        Spacer(Modifier.height(24.dp))

                        // Animated buttons
                        AnimatedButton(
                            onclick = {navController.navigate(Screen.LoginScreen.route)},
                            text = "  Login  ",
                            delay = 300,
                            filled = false
                        )

                    }
                }
            }
        }
    ) {
        androidx.compose.material.Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Lokachakra      ",
                                style = MaterialTheme.typography.h5,
                                fontWeight = FontWeight.Bold,
                                color = Color.Cyan
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { isDrawerOpen = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open menu",
                                tint = colorResource(id = R.color.white)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.topbarbackgound),
                        titleContentColor = colorResource(id = R.color.white)
                    )
                )
            },
            bottomBar = {
                // Bottom bar taking full width
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(colorResource(id = R.color.white))
                ) {
                    // Add your bottom bar content here if needed
                }
            }
        ) { paddingValues ->
            // Main content with scroll capability using LazyColumn instead of Column + verticalScroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 44.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Header
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Create an Account",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Enter your details to register for the internship portal",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Full Name Field
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name") },
                                singleLine = true,
                                placeholder = { Text("John Doe") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    unfocusedLabelColor = Color.Gray,
                                    focusedLabelColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            // Email Field
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                singleLine = true,
                                placeholder = { Text("name@example.com") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    unfocusedLabelColor = Color.Gray,
                                    focusedLabelColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            // Mobile Number Field
                            Column {
                                OutlinedTextField(
                                    value = mobileNumber,
                                    onValueChange = { mobileNumber = it },
                                    label = { Text("Mobile Number") },
                                    singleLine = true,
                                    placeholder = { Text("+91 98765 43210") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color.LightGray,
                                        focusedBorderColor = Color.Gray,
                                        unfocusedTextColor = Color.Black,
                                        focusedTextColor = Color.Black,
                                        unfocusedLabelColor = Color.Gray,
                                        focusedLabelColor = Color.Gray
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                                )

                                Text(
                                    text = "Format: +91 or 10 digit number",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            // Password Field
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    unfocusedLabelColor = Color.Gray,
                                    focusedLabelColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(8.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            // Confirm Password Field
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    unfocusedLabelColor = Color.Gray,
                                    focusedLabelColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(8.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Submit Button

                            AnimatedButton(onclick = {},text = "Register",delay = 500,filled = true)

                            // Already have an account section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Already have an account? ",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )

                                TextButton(
                                    onClick = {
                                        // Handle navigation to login screen
                                        navController.navigate(Screen.LoginScreen.route)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = colorResource(id = R.color.purple)
                                    )
                                ) {
                                    Text(
                                        text = "Login",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    bottomBar(navController )
                }

            }
        }
    }
}