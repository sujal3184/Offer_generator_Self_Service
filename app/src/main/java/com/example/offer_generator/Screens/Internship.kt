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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.offer_generator.common.AnimatedButton
import com.example.offer_generator.common.AppNavigationDrawer
import com.example.offer_generator.common.FloatingCard
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.Screens.HR.JobDomain
import com.example.offer_generator.Screens.HR.EmploymentType
import com.example.offer_generator.ViewModels.JobOpeningsViewModel
import com.example.offer_generator.ViewModels.JobStatus
import com.example.offer_generator.common.bottomBar
import kotlinx.coroutines.delay

fun getUserTypeString(whoLoginViewModel: WhoLoginViewModel): String {
    return when {
        whoLoginViewModel.isHrLoggedIn.value -> "HR"
        whoLoginViewModel.isFreeLancerLoggedIn.value -> "Freelancer"
        whoLoginViewModel.isUserLoggedIn.value -> "Internship"
        whoLoginViewModel.isFulltimeEmployeeLoggedIn.value -> "Full-Time"
        else -> "Career"
    }
}

fun getUserEmploymentType(whoLoginViewModel: WhoLoginViewModel): EmploymentType? {
    return when {
        whoLoginViewModel.isFreeLancerLoggedIn.value -> EmploymentType.FREELANCER
        whoLoginViewModel.isUserLoggedIn.value -> EmploymentType.INTERN
        whoLoginViewModel.isFulltimeEmployeeLoggedIn.value -> EmploymentType.FULL_TIME
        else -> null
    }
}

fun getScreenTitleAndDescription(whoLoginViewModel: WhoLoginViewModel): Pair<String, String> {
    return when {
        whoLoginViewModel.isHrLoggedIn.value -> Pair(
            "Job Opening Management",
            "Manage and oversee all job openings across different domains. Add, remove, or modify positions as needed."
        )
        whoLoginViewModel.isFreeLancerLoggedIn.value -> Pair(
            "Freelance Opportunities",
            "Discover flexible freelance projects that match your skills and schedule. Work with diverse clients on exciting projects."
        )
        whoLoginViewModel.isUserLoggedIn.value -> Pair(
            "Internship Opportunities",
            "Kickstart your career with hands-on experience and mentorship from industry experts. Our internships are designed to help you grow professionally and personally."
        )
        whoLoginViewModel.isFulltimeEmployeeLoggedIn.value -> Pair(
            "Career Opportunities",
            "Join our team as a full-time employee and build a long-term career with comprehensive benefits and growth opportunities."
        )
        else -> Pair(
            "Career Opportunities",
            "Explore various career paths and find the perfect opportunity that matches your skills and aspirations."
        )
    }
}

fun getBenefitsForUserType(whoLoginViewModel: WhoLoginViewModel): List<Pair<String, String>> {
    return when {
        whoLoginViewModel.isHrLoggedIn.value -> listOf(
            "Comprehensive Management" to "Access to all job domains and complete control over opening management.",
            "Real-time Analytics" to "Track application metrics and manage the entire hiring pipeline efficiently.",
            "Streamlined Process" to "Simplified tools to add, remove, and modify job openings across all departments."
        )
        whoLoginViewModel.isFreeLancerLoggedIn.value -> listOf(
            "Flexible Projects" to "Work on diverse projects that fit your schedule and expertise.",
            "Competitive Rates" to "Earn competitive compensation for your specialized skills and experience.",
            "Professional Network" to "Build connections with industry professionals and expand your client base."
        )
        whoLoginViewModel.isUserLoggedIn.value -> listOf(
            "Hands On Experience" to "Work on real projects that make an impact and build your portfolio with meaningful contributions.",
            "Professional Mentorship" to "Learn from industry experts who will guide you throughout your internship journey.",
            "Career Development" to "Gain skills that will set you apart in the job market and build your professional network."
        )
        whoLoginViewModel.isFulltimeEmployeeLoggedIn.value -> listOf(
            "Career Growth" to "Clear advancement paths and opportunities for professional development within the organization.",
            "Comprehensive Benefits" to "Full benefits package including health insurance, retirement plans, and paid time off.",
            "Job Security" to "Stable employment with long-term career prospects and continuous learning opportunities."
        )
        else -> listOf(
            "Professional Growth" to "Develop your skills and advance your career in a supportive environment.",
            "Industry Experience" to "Gain valuable experience working with cutting-edge technologies and methodologies.",
            "Networking Opportunities" to "Connect with professionals and build lasting relationships in your field."
        )
    }
}

// Enhanced InfoCard to work with employment types
@Composable
fun InfoCard(
    domain: JobDomain,
    onClick: () -> Unit,
    isHR: Boolean = false,
    userEmploymentType: EmploymentType? = null,
    jobViewModel: JobOpeningsViewModel
) {
    // Get posted jobs for this domain
    val postedJobs = jobViewModel.postedJobsRepository.postedJobs.value
        .filter { it.domain == domain.name && it.status == JobStatus.ACTIVE }

    // Create a map of posted job counts by role -> employmentType
    val postedJobCounts = mutableMapOf<String, MutableMap<EmploymentType, Int>>()
    postedJobs.forEach { job ->
        postedJobCounts
            .getOrPut(job.role) { mutableMapOf() }
            .put(job.employmentType, job.count)
    }

    // Get available roles based on user type from POSTED jobs
    val availableRoles = if (isHR) {
        // HR can see all posted roles with breakdown
        postedJobCounts.keys.associateWith { role ->
            postedJobCounts[role]?.values?.sum() ?: 0
        }
    } else if (userEmploymentType != null) {
        // Specific user type can only see roles for their employment type
        postedJobCounts.keys.associateWith { role ->
            postedJobCounts[role]?.get(userEmploymentType) ?: 0
        }.filter { it.value > 0 }
    } else {
        // Guest users can see all available roles
        postedJobCounts.keys.associateWith { role ->
            postedJobCounts[role]?.values?.sum() ?: 0
        }.filter { it.value > 0 }
    }

    val totalPositions = availableRoles.values.sum()

    // Only show card if there are available positions or if user is HR
    if (totalPositions > 0 || isHR) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.dp, vertical = 24.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(36.dp),
                    ambientColor = domain.color.copy(alpha = 0.9f),
                    spotColor = domain.color.copy(alpha = 0.8f)
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
                        text = domain.name,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(16.dp))

                    if (isHR) {
                        // Show employment type breakdown for HR from posted jobs
                        val employmentTypeSummary = EmploymentType.values().associateWith { empType ->
                            postedJobs.filter { it.employmentType == empType }.sumOf { it.count }
                        }

                        employmentTypeSummary.forEach { (empType, count) ->
                            if (count > 0) {
                                Text(
                                    text = "${empType.displayName}: $count positions",
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "$totalPositions position${if (totalPositions > 1) "s" else ""} Available",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        if (isHR) {
                            // Show all roles for HR with employment type breakdown from posted jobs
                            availableRoles.forEach { (role, roleTotal) ->
                                val roleEmploymentData = postedJobCounts[role] ?: emptyMap()

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (roleTotal > 0) colorResource(id = R.color.purple) else Color.Gray
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "$role (Posted: $roleTotal)",
                                            style = MaterialTheme.typography.body1,
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            color = if (roleTotal > 0) Color.Black else Color.Gray
                                        )

                                        // Show breakdown by employment type from posted jobs
                                        roleEmploymentData.forEach { (empType, count) ->
                                            if (count > 0) {
                                                Text(
                                                    text = "  • ${empType.displayName}: $count",
                                                    style = MaterialTheme.typography.caption,
                                                    modifier = Modifier.padding(start = 16.dp),
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Show only available roles for the specific user type from posted jobs
                            availableRoles.forEach { (role, count) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = colorResource(id = R.color.purple)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = "$role ($count available)",
                                        style = MaterialTheme.typography.body1,
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    TextButton(
                        onClick = onClick,
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.White,
                            contentColor = colorResource(id = R.color.purple),
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            if (isHR) "  Manage These Roles  " else "  Apply For These Roles  ",
                            fontSize = 16.sp
                        )
                    }
                }
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
                description = "If selected, you'll receive an official offer letter for the position.",
                isLast = false
            )

            // Step 4
            ApplicationStep(
                stepNumber = 4,
                title = "Onboarding",
                description = "Accept the offer, complete the onboarding process, and start your professional journey!",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableJobRoles(
    navController: NavController,
    whoLoginViewModel: WhoLoginViewModel,
    jobViewModel: JobOpeningsViewModel
) {
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

    // Get job openings data
    val jobOpenings by jobViewModel.repository.jobOpenings
    val userEmploymentType = getUserEmploymentType(whoLoginViewModel)

    // Filter job openings based on user type and employment type
    val filteredJobOpenings = getFilteredJobOpenings(
        jobOpenings,
        whoLoginViewModel,
        jobViewModel,
        userEmploymentType
    )

    // Determine screen title and description based on user type
    val (screenTitle, screenDescription) = getScreenTitleAndDescription(whoLoginViewModel)

    AppNavigationDrawer(
        navController = navController,
        currentScreen = Screen.AvailableJobRoles.route,
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
                                screenTitle,
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
                                        text = "Join Lokachakra ${getUserTypeString(whoLoginViewModel)} Program",
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
                                        screenDescription,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(Modifier.height(24.dp))

                                AnimatedButton(
                                    onclick = {
                                            when {
                                                whoLoginViewModel.isHrLoggedIn.value -> navController.navigate(Screen.HrDashboard.route)
                                                else -> navController.navigate(Screen.ApplicationForm.route)
                                            }
                                    },
                                    text = if (whoLoginViewModel.isHrLoggedIn.value) "Manage Openings" else "Apply Now",
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

            // Show filtered job openings
            if (filteredJobOpenings.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 44.dp, vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No openings available at the moment for ${userEmploymentType?.displayName ?: "your profile"}",
                                style = MaterialTheme.typography.h6,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredJobOpenings) { domain ->
                    InfoCard(
                        domain = domain,
                        onClick = {
                            if(whoLoginViewModel.isUserLoggedIn.value) {
                                when {
                                    whoLoginViewModel.isHrLoggedIn.value -> navController.navigate(Screen.HrDashboard.route)
                                    else -> navController.navigate(Screen.CandidateDashboard.route)
                                }
                            } else {
                                navController.navigate(Screen.LoginScreen.route)
                            }
                        },
                        isHR = whoLoginViewModel.isHrLoggedIn.value,
                        userEmploymentType = userEmploymentType,
                        jobViewModel = jobViewModel
                    )
                }
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

                // Benefits cards - customize based on user type
                val benefits = getBenefitsForUserType(whoLoginViewModel)
                benefits.forEachIndexed { index, (title, description) ->
                    BenefitCard(
                        title = title,
                        description = description,
                        isLast = index == benefits.size - 1
                    )
                    if (index < benefits.size - 1) {
                        Spacer(Modifier.height(24.dp))
                    }
                }

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
                                    text = "Take the first step towards a rewarding career by joining our ${getUserTypeString(whoLoginViewModel).lowercase()} program today.",
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                TextButton(
                                    onClick = {
                                        if(whoLoginViewModel.isUserLoggedIn.value) {
                                            when {
                                                whoLoginViewModel.isHrLoggedIn.value -> navController.navigate(Screen.HrDashboard.route)
                                                else -> navController.navigate(Screen.ApplicationForm.route)
                                            }
                                        } else {
                                            navController.navigate(Screen.LoginScreen.route)
                                        }
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = colorResource(id = R.color.white),
                                        contentColor = colorResource(id = R.color.purple)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        if (whoLoginViewModel.isHrLoggedIn.value) "Manage Job Openings"
                                        else "Apply For ${getUserTypeString(whoLoginViewModel)}"
                                    )
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

// Enhanced helper function to filter job openings based on POSTED jobs
fun getFilteredJobOpenings(
    jobOpenings: Map<String, JobDomain>,
    whoLoginViewModel: WhoLoginViewModel,
    jobViewModel: JobOpeningsViewModel,
    userEmploymentType: EmploymentType?
): List<JobDomain> {
    // Get posted jobs instead of template jobs
    val postedJobs = jobViewModel.postedJobsRepository.postedJobs.value

    // Create a map of posted job counts by domain -> role -> employmentType
    val postedJobCounts = mutableMapOf<String, MutableMap<String, MutableMap<EmploymentType, Int>>>()

    postedJobs.filter { it.status == JobStatus.ACTIVE }.forEach { job ->
        postedJobCounts
            .getOrPut(job.domain) { mutableMapOf() }
            .getOrPut(job.role) { mutableMapOf() }
            .put(job.employmentType, job.count)
    }

    return when {
        whoLoginViewModel.isHrLoggedIn.value -> {
            // HR can see all domains that have any posted jobs
            jobOpenings.values.filter { domain ->
                postedJobCounts.containsKey(domain.name)
            }
        }

        userEmploymentType != null -> {
            // Specific user type can only see domains with posted jobs for their employment type
            jobOpenings.values.filter { domain ->
                postedJobCounts[domain.name]?.values?.any { roleMap ->
                    (roleMap[userEmploymentType] ?: 0) > 0
                } ?: false
            }
        }

        else -> {
            // Guest users can see all domains with any posted jobs
            jobOpenings.values.filter { domain ->
                postedJobCounts.containsKey(domain.name) &&
                        postedJobCounts[domain.name]!!.values.any { roleMap ->
                            roleMap.values.sum() > 0
                        }
            }
        }
    }
}