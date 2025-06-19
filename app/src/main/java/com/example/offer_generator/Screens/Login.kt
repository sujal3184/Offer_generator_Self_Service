package com.example.offer_generator.Screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.common.bottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavController, whoLoginViewModel: WhoLoginViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isDrawerOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(
        initialValue = if (isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
    )
    val context = LocalContext.current

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
                                navController.navigate(Screen.AvailableJobRoles.route)
                            }
                        )

                        Spacer(Modifier.height(24.dp))

                        // Animated buttons

                        AnimatedButton(
                            onclick = {navController.navigate(Screen.RegisterScreen.route)},
                            text = "  Register  ",
                            delay = 400,
                            filled = true
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
                            .padding(horizontal = 16.dp, vertical = 55.dp),
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
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Login",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Enter your credentials to access your account",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))


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

                            // Forgot Password Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        // Handle forgot password navigation
                                        // navController.navigate(Screen.ForgotPasswordScreen.route)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = colorResource(id = R.color.purple)
                                    )
                                ) {
                                    Text(
                                        text = "Forgot Password?",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }


                            // Submit Button
                            AnimatedButton(
                                onclick = {
                                    if(whoLoginViewModel.selectedRole.value=="hr"){
                                        if(!email.contains("hr")){
                                            Toast.makeText(context, "Enter valid email for HR", Toast.LENGTH_SHORT).show()
                                        }
                                        else {
                                            // Set the user as logged in with their previously selected role
                                            whoLoginViewModel.loginWithSelectedRole()

                                            // Navigate to home screen
                                            navController.navigate(Screen.HomeScreen.route) {
                                                popUpTo(Screen.LoginScreen.route) { inclusive = true }
                                            }
                                        }
                                    }
                                    else {
                                        // Set the user as logged in with their previously selected role
                                        whoLoginViewModel.loginWithSelectedRole()

                                        // Navigate to home screen
                                        navController.navigate(Screen.HomeScreen.route) {
                                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                                        }
                                    }
                                          },
                                text = "Login", filled = true, delay = 500)

                            // Already have an account section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text = "Don't have an account? ",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )

                                TextButton(
                                    onClick = {
                                        // Handle navigation to login screen
                                        navController.navigate(Screen.RegisterScreen.route)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = colorResource(id = R.color.purple)
                                    )
                                ) {
                                    Text(
                                        text = "Register",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center){
                                Column(Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("For Testing Purposes:", fontSize = 10.sp,color = Color.Gray)
                                    Text("- Use any email containing \"hr\" for HR access", fontSize = 10.sp,color = Color.Gray)
                                    Text("- Use any email containing \"candidate\" for candidate access", fontSize = 10.sp,color = Color.Gray)
                                    Text("- Any password will work", fontSize = 10.sp,color = Color.Gray)
                                }

                            }
                        }
                    }
                    bottomBar(navController , whoLoginViewModel)
                }

            }
        }
    }
}