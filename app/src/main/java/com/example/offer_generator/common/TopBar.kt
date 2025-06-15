package com.example.offer_generator.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.ViewModels.WhoLoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationDrawer(
    whoLoginViewModel: WhoLoginViewModel,
    navController: NavController,
    currentScreen: String = "",
    onBackPressed: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var isDrawerOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(
        initialValue = if (isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
    )

    BackHandler {
        if(isDrawerOpen) isDrawerOpen = false
        else onBackPressed?.invoke()
    }

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
            DrawerContent(
                isDrawerOpen = isDrawerOpen,
                navController = navController,
                currentScreen = currentScreen,
                onDrawerClose = { isDrawerOpen = false },
                whoLoginViewModel = whoLoginViewModel
            )
        }
    ) {
        AppScaffold(
            isDrawerOpen = isDrawerOpen,
            onMenuClick = { isDrawerOpen = true },
            content = content
        )
    }
}

@Composable
private fun DrawerContent(
    whoLoginViewModel: WhoLoginViewModel,
    isDrawerOpen: Boolean,
    navController: NavController,
    currentScreen: String,
    onDrawerClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.background(Color.White),
        drawerContainerColor = Color.White,
        drawerContentColor = Color.Black,
        windowInsets = WindowInsets(0)
    ) {
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .background(Color.White)
                    .padding(start = 16.dp, top = 44.dp,end = 16.dp)
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.h5,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(color = Color.Gray)

                // Animated menu items
                AnimatedNavigationItem(
                    text = "Home",
                    delay = 100,
                    onClick = {
                        onDrawerClose()
                        navController.navigate(Screen.HomeScreen.route)
                    }
                )

                AnimatedNavigationItem(
                    text = "InternShip",
                    delay = 200,
                    onClick = {
                        onDrawerClose()
                        navController.navigate(Screen.InternshipScreen.route)
                    }
                )

                if(whoLoginViewModel.isUserLoggedIn.value){
                    AnimatedNavigationItem(
                        text = "My Application",
                        delay = 200,
                        onClick = {
                            onDrawerClose()
                            navController.navigate(Screen.CandidateDashboard.route)
                        }
                    )
                }

                Spacer(Modifier.height(24.dp))

                if(whoLoginViewModel.getCurrentUserType() != null){

                    AnimatedButton(
                        onclick = {
                            whoLoginViewModel.logoutAll(

                            )
                            navController.navigate(Screen.StartScreen.route) {
                                popUpTo(0)}
                            },
                        text = "  Log Out  ",
                        delay = 300,
                        filled = false
                    )
                }

                else{
                    // Animated buttons
                    AnimatedButton(
                        onclick = { navController.navigate(Screen.LoginScreen.route) },
                        text = "  Login  ",
                        delay = 300,
                        filled = false,
                    )

                    Spacer(Modifier.height(12.dp))

                    AnimatedButton(
                        onclick = { navController.navigate(Screen.RegisterScreen.route) },
                        text = "  Register  ",
                        delay = 400,
                        filled = true
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold(
    isDrawerOpen: Boolean,
    onMenuClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                isDrawerOpen = isDrawerOpen,
                onMenuClick = onMenuClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    isDrawerOpen: Boolean,
    onMenuClick: () -> Unit
) {
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
            IconButton(onClick = onMenuClick) {
                val rotation by animateFloatAsState(
                    targetValue = if (isDrawerOpen) 90f else 0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "rotation"
                )

                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open menu",
                    tint = colorResource(id = R.color.white),
                    modifier = Modifier.rotate(rotation)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.topbarbackgound),
            titleContentColor = colorResource(id = R.color.white)
        ),
    )
}