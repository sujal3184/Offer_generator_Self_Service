package com.example.offer_generator.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.offer_generator.common.AnimatedButton
import com.example.offer_generator.common.AppNavigationDrawer
import com.example.offer_generator.common.FloatingCard
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.common.bottomBar
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Internship(navController: NavController, whoLoginViewModel: WhoLoginViewModel) {
    // Animation states
    var showContent by remember { mutableStateOf(false) }

    // Launch animations on screen load
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    // Floating animation for background elements
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    AppNavigationDrawer(
        navController = navController,
        currentScreen = Screen.InternshipScreen.route,
        onBackPressed = { navController.popBackStack()},
        whoLoginViewModel = whoLoginViewModel
    ) {
        // Main content using LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(animationSpec = tween(800))
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        colorResource(id = R.color.purple),
                                        colorResource(id = R.color.blue)
                                    )
                                )
                            ),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Internship Opportunities",
                                style = MaterialTheme.typography.h4,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(44.dp))

                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(1200))
                ) {
                    FloatingCard(
                        modifier = Modifier.offset(y = floatingOffset.dp),
                        content = {
                            Column(
                                Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Join Lokachakra Internship Program",
                                        style = MaterialTheme.typography.h4,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Magenta
                                    )
                                }

                                Spacer(Modifier.height(24.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "Kickstart your career with hands-on experience" +
                                                " and mentorship from industry experts. Our internships are designed to help you grow " +
                                                "professionally and personally.",
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(Modifier.height(24.dp))

                                AnimatedButton(
                                    onclick =  {if(whoLoginViewModel.isUserLoggedIn.value) navController.navigate(Screen.ApplicationForm.route) else navController.navigate(Screen.LoginScreen.route)},
                                    text = "Apply Now",
                                    delay = 300,
                                    filled = false
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))
                Divider()
                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Available Roles",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Card items directly in LazyColumn
            items(getCardData(onClick = {
                if(whoLoginViewModel.isUserLoggedIn.value) navController.navigate(Screen.CandidateDashboard.route)
                else navController.navigate(Screen.LoginScreen.route)
            })) { card ->   // apply for these roles button
                InfoCard(card)
            }

            item {
                Spacer(Modifier.height(24.dp))
                Divider()
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Program Benefits",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 30.sp
                )

                Spacer(Modifier.height(24.dp))

                // Benefits cards
                BenefitCard(
                    title = "Hands On Experience",
                    description = "Work on real projects that make an impact " +
                            "and build your portfolio with meaningful contributions."
                )

                Spacer(Modifier.height(24.dp))

                BenefitCard(
                    title = "Professional Mentorship",
                    description = "Learn from industry experts who will guide you throughout your internship journey."
                )

                Spacer(Modifier.height(24.dp))

                BenefitCard(
                    title = "Career Development",
                    description = "Gain skills that will set you apart in the job market and build your professional network.",
                    isLast = true
                )

                Spacer(Modifier.height(24.dp))
                Divider()
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Application Process",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontSize = 30.sp
                )

                ApplicationProcessCard()

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(1200))
                ) {
                    FloatingCard(
                        modifier = Modifier.offset(y = floatingOffset.dp),
                        content = {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Ready To Begin Your Professional Journey?",
                                    style = MaterialTheme.typography.h4,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Magenta,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Take the first step towards a rewarding career by joining our internship program today.",
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                TextButton(
                                    onClick = {
                                        if(whoLoginViewModel.isUserLoggedIn.value) navController.navigate(Screen.ApplicationForm.route)
                                        else navController.navigate(Screen.LoginScreen.route)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = colorResource(id = R.color.white),
                                        contentColor = colorResource(id = R.color.purple)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Apply For Internship")
                                }
                            }
                        }
                    )
                }

                Spacer(Modifier.height(32.dp))
                bottomBar(navController)
            }
        }
    }
}

// Extracted benefit card to reduce code duplication
@Composable
private fun BenefitCard(
    title: String,
    description: String,
    isLast: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 44.dp,
                vertical = if (isLast) 0.dp else 0.dp
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(36.dp),
                ambientColor = colorResource(id = R.color.purple).copy(alpha = 0.9f),
                spotColor = colorResource(id = R.color.purple).copy(alpha = 0.8f)
            ),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white),
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(24.dp))

                Text(
                    description,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}

data class CardItem(
    val heading: String,
    val available_positions: Int,
    val points: List<String>,
    val onClick: () -> Unit
)

// Moved card data to a function to avoid recreating it in recomposition
fun getCardData(onClick: () -> Unit): List<CardItem> {
    return listOf(
        CardItem(
            heading = "Technical",
            available_positions = 7,
            points = listOf(
                "AI / ML / DL / GenAI / LLM",
                "Blockchain",
                "Web3",
                "Frontend",
                "Backend",
                "Fullstack",
                "DevSecOps"
            ),
            onClick = { onClick() }
        ),
        CardItem(
            heading = "Business",
            available_positions = 5,
            points = listOf(
                "Business and Strategy Research",
                "Business Analyst",
                "Product Management",
                "Project Management",
                "Marketing"
            ),
            onClick = { onClick() }
        ),
        CardItem(
            heading = "Design",
            available_positions = 1,
            points = listOf("UX/UI"),
            onClick = { onClick() }
        ),
        CardItem(
            heading = "Professional",
            available_positions = 4,
            points = listOf("Legal", "Chartered Accountant", "Company Secretary", "HR"),
            onClick = { onClick() }
        ),
    )
}

@Composable
fun InfoCard(card: CardItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp, vertical = 24.dp).shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(36.dp),
                ambientColor = colorResource(id = R.color.blue).copy(alpha = 0.9f),
                spotColor = colorResource(id = R.color.blue).copy(alpha = 0.8f)
            ),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white),
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = card.heading,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "${card.available_positions}  position${if (card.available_positions > 1) "s" else ""} Available",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(24.dp))

                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    card.points.forEach { point ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = colorResource(id = R.color.purple)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = point,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(vertical = 2.dp),
                                color = Color.Black
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))

                TextButton(
                    onClick = card.onClick,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.White,
                        contentColor = colorResource(id = R.color.purple),
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("  Apply For These Roles  ", fontSize = 16.sp)
                }
            }
        }
    }
}


@Composable
fun ApplicationProcessCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            // Step 1
            ApplicationStep(
                stepNumber = 1,
                title = "Register & Apply",
                description = "Create an account and submit your application with all required documents.",
                isLast = false
            )

            // Step 2
            ApplicationStep(
                stepNumber = 2,
                title = "Application Review",
                description = "Our team will review your application and get back to you within 5-7 business days.",
                isLast = false
            )

            // Step 3
            ApplicationStep(
                stepNumber = 3,
                title = "Offer Letter",
                description = "If selected, you'll receive an official offer letter for the internship.",
                isLast = false
            )

            // Step 4
            ApplicationStep(
                stepNumber = 4,
                title = "Onboarding",
                description = "Accept the offer, complete the onboarding process, and start your internship journey!",
                isLast = true
            )
        }
    }
}

@Composable
fun ApplicationStep(
    stepNumber: Int,
    title: String,
    description: String,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Timeline column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circle
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = Color(0xFF00BCD4),
                        shape = CircleShape
                    )
            )

            // Connecting line (if not last step)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(Color(0xFF00BCD4))
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Content column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 32.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00BCD4),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                lineHeight = 20.sp
            )
        }
    }
}