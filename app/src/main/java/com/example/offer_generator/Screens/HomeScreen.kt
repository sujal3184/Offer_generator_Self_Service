package com.example.offer_generator.Screens

import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.offer_generator.common.AnimatedButton
import com.example.offer_generator.common.AnimatedDomainCard
import com.example.offer_generator.common.AnimatedFeatureRow
import com.example.offer_generator.common.AnimatedInteractiveButton
import com.example.offer_generator.common.AppNavigationDrawer
import com.example.offer_generator.common.FloatingCard
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.Screens.HR.JobOpeningsManager
import com.example.offer_generator.Screens.HR.JobOpeningsRepository
import com.example.offer_generator.common.bottomBar
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController, whoLoginViewModel: WhoLoginViewModel) {

    val repository = remember { JobOpeningsRepository() }

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    val activity = LocalActivity.current

    // Launch animations on screen load
    LaunchedEffect(Unit) {
        isVisible = true
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
        currentScreen = "Home",
        onBackPressed = {activity?.finish()},
        whoLoginViewModel = whoLoginViewModel
    ) {
        HomeScreenContent(
            navController = navController,
            isVisible = isVisible,
            showContent = showContent,
            floatingOffset = floatingOffset,
            infiniteTransition = infiniteTransition,
            whoLoginViewModel = whoLoginViewModel
        )
    }
}

@Composable
private fun HomeScreenContent(
    navController: NavController,
    isVisible: Boolean,
    showContent: Boolean,
    floatingOffset: Float,
    infiniteTransition: androidx.compose.animation.core.InfiniteTransition,
    whoLoginViewModel: WhoLoginViewModel
) {
    // Pulsing animation for title
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Determine user role - you may need to adjust these based on your actual user role logic
    val userRole = getUserRole(whoLoginViewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Animated main title
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Lokachakra",
                    style = MaterialTheme.typography.h4,
                    color = Color.Black,
                    modifier = Modifier.scale(pulseScale)
                )
                Text(
                    getRoleSpecificSubtitle(userRole),
                    style = MaterialTheme.typography.h4,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Role-specific description
        AnimatedVisibility(
            visible = showContent,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(animationSpec = tween(800))
        ) {
            Row(Modifier.padding(horizontal = 24.dp)) {
                Text(
                    getRoleSpecificDescription(userRole),
                    style = MaterialTheme.typography.body1,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login/Dashboard buttons
        AnimatedVisibility(
            visible = showContent,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(1000))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (whoLoginViewModel.getCurrentUserType() != null) {
                    AnimatedInteractiveButton(
                        onclick = {
                            when(whoLoginViewModel.getCurrentUserType()){
                                "intern" -> navController.navigate(Screen.CandidateDashboard.route)
                                "freelancer" -> navController.navigate(Screen.FlDashboard.route)
                                "fulltime" -> navController.navigate(Screen.FullTimejobDashboard.route)
                                "hr" -> navController.navigate(Screen.HrDashboard.route)
                            } },
                        text = "    Go To Dashboard    ",
                        filled = true
                    )
                } else {
                    AnimatedInteractiveButton(
                        onclick = { navController.navigate(Screen.LoginScreen.route) },
                        text = "    Login    ",
                        filled = true
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    AnimatedInteractiveButton(
                        onclick = { navController.navigate(Screen.LoginScreen.route) },
                        text = "  Get Started  ",
                        filled = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(44.dp))

        // Role-specific content for guests
        if (userRole == "GUEST") {
            GuestContent(navController, showContent)
        }

        // Role-specific features card
        AnimatedVisibility(
            visible = showContent,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(1400))
        ) {
            FloatingCard(
                modifier = Modifier.offset(y = floatingOffset.dp),
                content = {
                    Column(
                        Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getRoleSpecificFeatureTitle(userRole),
                            style = MaterialTheme.typography.h5,
                            fontWeight = FontWeight.Bold,
                            color = Color.Magenta
                        )
                        Spacer(Modifier.height(24.dp))

                        getRoleSpecificFeatures(userRole).forEachIndexed { index, feature ->
                            AnimatedFeatureRow(feature, (index * 200).toLong())
                            if (index < getRoleSpecificFeatures(userRole).size - 1) {
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
        Divider()
        Spacer(modifier = Modifier.height(50.dp))

        // Role-specific quick actions
        when (userRole) {
            "FREELANCER" -> FreelancerQuickActions(navController, showContent)
            "INTERN" -> InternQuickActions(navController, showContent)
            "JOB_SEEKER" -> JobSeekerQuickActions(navController, showContent)
            "HR" -> HRQuickActions(navController, showContent)
        }

        // Role-specific domain opportunities
        if (userRole != "GUEST") {
            RoleSpecificDomains(userRole, showContent)
        }

        bottomBar(navController, whoLoginViewModel)
    }
}

// Helper function to get user role - updated implementation
@Composable
private fun getUserRole(whoLoginViewModel: WhoLoginViewModel): String {
    return when {
        whoLoginViewModel.isFreeLancerLoggedIn.value -> "FREELANCER"
        whoLoginViewModel.isUserLoggedIn.value -> "INTERN" // assuming isUserLoggedIn represents intern
        whoLoginViewModel.isFulltimeEmployeeLoggedIn.value -> "JOB_SEEKER"
        whoLoginViewModel.isHrLoggedIn.value -> "HR"
        whoLoginViewModel.isAdminLoggedIn.value -> "ADMIN" // if you want to handle admin separately
        else -> "GUEST"
    }
}


@Composable
private fun RoleSpecificDomains(userRole: String, showContent: Boolean) {
    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn(animationSpec = tween(1600))
    ) {
        Text(
            text = when (userRole) {
                "FREELANCER" -> "Popular Freelance Categories"
                "INTERN" -> "Internship Opportunities"
                "JOB_SEEKER" -> "Career Opportunities"
                "HR" -> "Talent Categories"
                else -> "Available Opportunities"
            },
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 30.sp
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Show domains relevant to the user role
    val domains = getRoleSpecificDomains(userRole)
    domains.forEachIndexed { index, domain ->
        AnimatedDomainCard(
            title = domain.title,
            items = domain.items,
            delay = 600 + (index * 200).toLong(),
            isLast = index == domains.size - 1
        )
        if (index < domains.size - 1) {
            Spacer(Modifier.height(24.dp))
        }
    }
}

private data class DomainCategory(val title: String, val items: List<String>)


@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // Changed from fillMaxSize to fillMaxWidth
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 18.sp,
                    maxLines = 2 // Allow title to wrap if needed
                )

                Spacer(modifier = Modifier.height(16.dp)) // Slightly more spacing

                Text(
                    text = description,
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 3 // Allow description to wrap if needed
                )

            Spacer(modifier = Modifier.height(16.dp)) // Use width for horizontal spacing in Row

            AnimatedButton(
                onclick = onClick,
                text = buttonText,
                delay = 300,
                filled = true
            )
        }
    }
}

// FREELANCER CONTENT
private fun getFreelancerSubtitle(): String = "Freelance Hub"

private fun getFreelancerDescription(): String =
    "Transform your skills into income with premium freelance opportunities. Connect with verified clients, work on cutting-edge projects, and build a thriving independent career with complete flexibility and competitive compensation."

private fun getFreelancerFeatureTitle(): String = "Why Freelance with Lokachakra?"

private fun getFreelancerFeatures(): List<String> = listOf(
    "🎯 Smart Project Matching: AI-powered system finds projects that perfectly match your skills and interests",
    "💰 Premium Rates: Access high-paying projects with transparent pricing and secure milestone-based payments",
    "⏰ Ultimate Flexibility: Choose your projects, set your schedule, and work from anywhere in the world",
    "🤝 Long-term Partnerships: Build lasting relationships with enterprise clients and recurring project opportunities",
    "📈 Skill Development: Work on diverse projects that expand your expertise and portfolio",
    "🛡️ Payment Protection: Escrow system ensures you get paid on time, every time"
)

private fun getFreelancerDomains(): List<DomainCategory> = listOf(
    DomainCategory("High-Demand Tech Projects", listOf(
        "Full-Stack Development (React, Node.js, Python)",
        "AI/ML Model Development & Implementation",
        "Mobile App Development (iOS/Android/Flutter)",
        "Blockchain & Smart Contract Development",
        "Cloud Architecture & DevOps Solutions",
        "API Development & System Integration"
    )),
    DomainCategory("Creative & Design Services", listOf(
        "UI/UX Design & Product Design",
        "Brand Identity & Logo Design",
        "Web Design & Landing Pages",
        "Video Production & Motion Graphics",
        "Content Writing & Technical Documentation",
        "Social Media Design & Marketing Assets"
    )),
    DomainCategory("Business & Consulting", listOf(
        "Digital Marketing & SEO Strategy",
        "Business Analysis & Process Optimization",
        "Data Analysis & Business Intelligence",
        "E-commerce Solutions & Online Stores",
        "Financial Modeling & Analysis",
        "Project Management & Agile Consulting"
    ))
)

// INTERN CONTENT
private fun getInternSubtitle(): String = "Internship Portal"

private fun getInternDescription(): String =
    "Kickstart your career with world-class internship programs. Gain hands-on experience at leading companies, receive mentorship from industry experts, and build the foundation for your professional journey with real projects and meaningful impact."

private fun getInternFeatureTitle(): String = "Why Choose Our Internship Program?"

private fun getInternFeatures(): List<String> = listOf(
    "🎓 Structured Learning: Comprehensive 3-6 month programs with clear learning objectives and skill development paths",
    "👨‍🏫 Expert Mentorship: One-on-one guidance from senior professionals and industry leaders",
    "🚀 Real Impact Projects: Work on actual business projects that make a difference, not just busy work",
    "📜 Career Certification: Completion certificates, LinkedIn recommendations, and portfolio building support",
    "🌱 Career Pathway: 70% of our interns receive full-time offers or advanced internship opportunities",
    "💰 Competitive Stipends: Fair compensation with performance bonuses and learning incentives"
)

private fun getInternDomains(): List<DomainCategory> = listOf(
    DomainCategory("Technology & Engineering", listOf(
        "Software Development Internship (Web/Mobile)",
        "Data Science & Machine Learning Internship",
        "Cybersecurity & Information Security Internship",
        "Cloud Computing & DevOps Internship",
        "Product Management & Tech Strategy Internship",
        "Quality Assurance & Testing Internship"
    )),
    DomainCategory("Business & Operations", listOf(
        "Business Development & Strategy Internship",
        "Financial Analysis & Investment Internship",
        "Marketing & Brand Management Internship",
        "Human Resources & Talent Acquisition Internship",
        "Operations & Supply Chain Internship",
        "Sales & Customer Success Internship"
    )),
    DomainCategory("Creative & Media", listOf(
        "Graphic Design & Creative Internship",
        "Content Creation & Social Media Internship",
        "Digital Marketing & SEO Internship",
        "Video Production & Multimedia Internship",
        "UX Research & Design Internship",
        "Public Relations & Communications Internship"
    ))
)

// JOB SEEKER (FULL-TIME) CONTENT
private fun getJobSeekerSubtitle(): String = "Career Portal"

private fun getJobSeekerDescription(): String =
    "Discover your dream career with full-time opportunities at innovative companies. Access exclusive job openings, comprehensive benefits packages, and long-term career growth paths in industries that are shaping the future."

private fun getJobSeekerFeatureTitle(): String = "Why Build Your Career Here?"

private fun getJobSeekerFeatures(): List<String> = listOf(
    "🎯 Perfect Job Matching: AI-powered career matching based on your skills, experience, and career aspirations",
    "💼 Comprehensive Benefits: Full-time roles with health insurance, retirement plans, and professional development budgets",
    "📈 Career Advancement: Clear promotion pathways with skills training and leadership development programs",
    "🏢 Top-Tier Companies: Exclusive access to opportunities at Fortune 500 companies and fast-growing startups",
    "🤝 Interview Support: Resume optimization, interview coaching, and salary negotiation assistance",
    "🌟 Work-Life Balance: Remote-friendly positions with flexible schedules and wellness programs"
)

private fun getJobSeekerDomains(): List<DomainCategory> = listOf(
    DomainCategory("Senior Technical Roles", listOf(
        "Senior Software Engineer & Tech Lead Positions",
        "DevOps Engineer & Cloud Architecture Roles",
        "Data Scientist & Machine Learning Engineer",
        "Cybersecurity Specialist & Information Security",
        "Mobile Development Lead (iOS/Android)",
        "Full-Stack Developer & System Architect"
    )),
    DomainCategory("Leadership & Management", listOf(
        "Product Manager & Senior Product Owner",
        "Engineering Manager & Technical Director",
        "Project Manager & Program Director",
        "Business Development Manager",
        "Marketing Manager & Growth Lead",
        "Operations Manager & Team Lead"
    )),
    DomainCategory("Specialized Professional Roles", listOf(
        "UX/UI Designer & Design System Lead",
        "Digital Marketing Manager & SEO Specialist",
        "Financial Analyst & Investment Associate",
        "Legal Counsel & Compliance Officer",
        "Sales Manager & Account Executive",
        "HR Business Partner & Talent Specialist"
    ))
)

// HR/EMPLOYER CONTENT
private fun getHRSubtitle(): String = "Talent Hub"

private fun getHRDescription(): String =
    "Build exceptional teams with access to pre-vetted, high-quality talent. Streamline your hiring process with our AI-powered matching system and connect with skilled professionals ready to drive your business forward."

private fun getHRFeatureTitle(): String = "Why Hire Through Lokachakra?"

private fun getHRFeatures(): List<String> = listOf(
    "👥 Premium Talent Pool: Access to pre-screened, verified candidates with proven track records",
    "⚡ Rapid Hiring: 50% faster hiring process with streamlined interviews and automated screening",
    "📊 Smart Analytics: Detailed candidate insights, performance predictions, and cultural fit analysis",
    "🎯 Precision Matching: AI-powered recommendations that match candidates to your specific requirements",
    "💰 Cost-Effective: Reduce recruitment costs by up to 60% compared to traditional hiring methods",
    "🔄 Flexible Hiring: Choose from freelance, contract, part-time, or full-time engagement models"
)

private fun getHRDomains(): List<DomainCategory> = listOf(
    DomainCategory("Technical Talent", listOf(
        "Software Engineers & Full-Stack Developers",
        "Data Scientists & Machine Learning Engineers",
        "DevOps Engineers & Cloud Specialists",
        "Cybersecurity Experts & Information Security",
        "Mobile Developers & App Development Teams",
        "AI/ML Specialists & Research Engineers"
    )),
    DomainCategory("Business & Strategy Professionals", listOf(
        "Product Managers & Strategy Consultants",
        "Business Analysts & Operations Managers",
        "Marketing & Growth Professionals",
        "Financial Analysts & Accounting Specialists",
        "Sales Leaders & Business Development",
        "Project Managers & Agile Coaches"
    )),
    DomainCategory("Creative & Design Talent", listOf(
        "UX/UI Designers & Product Designers",
        "Graphic Designers & Brand Specialists",
        "Content Writers & Marketing Copywriters",
        "Video Producers & Multimedia Specialists",
        "Social Media Managers & Community Builders",
        "Creative Directors & Art Directors"
    ))
)

// GUEST CONTENT (Enhanced)
private fun getGuestSubtitle(): String = "Career Portal"

private fun getGuestDescription(): String =
    "Connect talent with opportunity. Whether you're seeking internships, freelance projects, full-time careers, or looking to hire top talent - we bridge the gap between ambition and achievement in the modern workplace."

private fun getGuestFeatureTitle(): String = "Why Choose Lokachakra?"

private fun getGuestFeatures(): List<String> = listOf(
    "🎯 AI-Powered Matching: Advanced algorithms connect you with opportunities that perfectly fit your profile",
    "🔒 Verified Opportunities: All positions are thoroughly vetted and verified for authenticity and quality",
    "📈 Career Growth Support: Comprehensive skill development resources and career advancement guidance",
    "🤝 Thriving Community: Join thousands of professionals, mentors, and industry leaders",
    "🌍 Global Opportunities: Access to remote and international positions across multiple industries",
    "💡 Innovation Focus: Opportunities at cutting-edge companies working on tomorrow's technologies"
)

// QUICK ACTION CARDS CONTENT

// Freelancer Quick Actions
private fun getFreelancerQuickActions(): List<QuickAction> = listOf(
    QuickAction(
        title = "Browse Active Projects",
        description = "Discover high-paying freelance opportunities matching your skills and availability",
        buttonText = "View Projects",
        icon = "🎯"
    ),
    QuickAction(
        title = "My Project Proposals",
        description = "Track all your submitted proposals and their current status with clients",
        buttonText = "View Proposals",
        icon = "📋"
    ),
    QuickAction(
        title = "Apply for New Project",
        description = "Find and apply for fresh freelance opportunities that match your expertise",
        buttonText = "Apply Now",
        icon = "🚀"
    )
)

// Intern Quick Actions
private fun getInternQuickActions(): List<QuickAction> = listOf(
    QuickAction(
        title = "Internship Opportunities",
        description = "Apply for structured internship programs at leading companies",
        buttonText = "Apply Now",
        icon = "🎓"
    ),
    QuickAction(
        title = "Application Status",
        description = "Track your applications and interview schedules in real-time",
        buttonText = "View Status",
        icon = "📊"
    ),
    QuickAction(
        title = "View Available Roles",
        description = "Browse all available internship positions across different departments",
        buttonText = "View Roles",
        icon = "👀"
    )
)

// Job Seeker Quick Actions
private fun getJobSeekerQuickActions(): List<QuickAction> = listOf(
    QuickAction(
        title = "Career Opportunities",
        description = "Explore full-time positions with growth potential and excellent benefits",
        buttonText = "Browse Jobs",
        icon = "🚀"
    ),
    QuickAction(
        title = "Application Tracker",
        description = "Monitor your job applications and interview progress",
        buttonText = "Track Progress",
        icon = "📈"
    ),
    QuickAction(
        title = "Apply for New Job",
        description = "Search and apply for new job opportunities that match your career goals",
        buttonText = "Apply Now",
        icon = "✨"
    )
)

// HR Quick Actions
private fun getHRQuickActions(): List<QuickAction> = listOf(
    QuickAction(
        title = "Candidate Pipeline",
        description = "Review and manage incoming applications from qualified candidates",
        buttonText = "Review Applications",
        icon = "👥"
    ),
    QuickAction(
        title = "Post New Opening",
        description = "Create and publish job postings to attract top talent",
        buttonText = "Post Job",
        icon = "📝"
    ),
    QuickAction(
        title = "View Available Jobs",
        description = "Browse all current job openings and their application status",
        buttonText = "View Jobs",
        icon = "💼"
    )
)

// Data class for Quick Actions
data class QuickAction(
    val title: String,
    val description: String,
    val buttonText: String,
    val icon: String
)

// Updated Role Cards for Guest Content
private fun getGuestRoleCards(): List<RoleCardData> = listOf(
    RoleCardData(
        title = "🎓 Launch Your Career with Internships",
        description = "Transform theoretical knowledge into practical skills through hands-on internship experiences. Work alongside industry professionals, contribute to real projects, and build a strong foundation for your career journey.",
        features = listOf(
            "3-6 month structured programs with clear learning objectives",
            "Direct mentorship from experienced professionals and team leads",
            "Real-world projects that add value to your portfolio",
            "Completion certificates and professional LinkedIn recommendations",
            "Networking opportunities with industry professionals",
            "Potential conversion to full-time positions (70% success rate)"
        ),
        buttonText = "Apply for Internships",
        primaryColor = "#4CAF50"
    ),
    RoleCardData(
        title = "💼 Freelance Your Way to Success",
        description = "Build a thriving independent career with diverse freelance projects. Enjoy complete flexibility while working with verified clients on challenging projects that expand your skills and portfolio.",
        features = listOf(
            "Project-based work with flexible schedules and remote options",
            "Competitive rates with secure milestone-based payment systems",
            "Work with verified clients from startups to Fortune 500 companies",
            "Build long-term partnerships and recurring project opportunities",
            "Access to premium projects across technology, design, and business",
            "Professional portfolio building and client testimonial system"
        ),
        buttonText = "Browse Freelance Projects",
        primaryColor = "#2196F3"
    ),
    RoleCardData(
        title = "🚀 Build Your Dream Career",
        description = "Secure permanent positions that offer stability, growth, and comprehensive benefits. Join innovative companies that invest in your professional development and long-term career success.",
        features = listOf(
            "Full-time permanent positions with job security and stability",
            "Comprehensive benefits including health, dental, and retirement plans",
            "Clear career progression pathways with skills development support",
            "Work-life balance with flexible schedules and remote work options",
            "Professional development budgets and conference attendance",
            "Team collaboration opportunities and leadership development"
        ),
        buttonText = "Explore Career Opportunities",
        primaryColor = "#FF9800"
    ),
    RoleCardData(
        title = "🏢 Hire Exceptional Talent",
        description = "Transform your hiring process and build world-class teams. Access a curated pool of pre-vetted professionals across all skill levels and engagement types to drive your business forward.",
        features = listOf(
            "Access to pre-screened talent pool with verified skills and experience",
            "Flexible hiring models: full-time, contract, freelance, and internship",
            "AI-powered matching system for precise candidate recommendations",
            "Streamlined interview process with integrated scheduling and feedback",
            "Detailed candidate analytics and cultural fit assessments",
            "Ongoing support for successful team integration and retention"
        ),
        buttonText = "Start Hiring Today",
        primaryColor = "#9C27B0"
    )
)

// Data class for Role Cards
data class RoleCardData(
    val title: String,
    val description: String,
    val features: List<String>,
    val buttonText: String,
    val primaryColor: String
)

// Replace your existing functions with these updated versions:

private fun getRoleSpecificSubtitle(userRole: String): String {
    return when (userRole) {
        "FREELANCER" -> getFreelancerSubtitle()
        "INTERN" -> getInternSubtitle()
        "JOB_SEEKER" -> getJobSeekerSubtitle()
        "HR" -> getHRSubtitle()
        else -> getGuestSubtitle()
    }
}

private fun getRoleSpecificDescription(userRole: String): String {
    return when (userRole) {
        "FREELANCER" -> getFreelancerDescription()
        "INTERN" -> getInternDescription()
        "JOB_SEEKER" -> getJobSeekerDescription()
        "HR" -> getHRDescription()
        else -> getGuestDescription()
    }
}

private fun getRoleSpecificFeatureTitle(userRole: String): String {
    return when (userRole) {
        "FREELANCER" -> getFreelancerFeatureTitle()
        "INTERN" -> getInternFeatureTitle()
        "JOB_SEEKER" -> getJobSeekerFeatureTitle()
        "HR" -> getHRFeatureTitle()
        else -> getGuestFeatureTitle()
    }
}

private fun getRoleSpecificFeatures(userRole: String): List<String> {
    return when (userRole) {
        "FREELANCER" -> getFreelancerFeatures()
        "INTERN" -> getInternFeatures()
        "JOB_SEEKER" -> getJobSeekerFeatures()
        "HR" -> getHRFeatures()
        else -> getGuestFeatures()
    }
}

private fun getRoleSpecificDomains(userRole: String): List<DomainCategory> {
    return when (userRole) {
        "FREELANCER" -> getFreelancerDomains()
        "INTERN" -> getInternDomains()
        "JOB_SEEKER" -> getJobSeekerDomains()
        "HR" -> getHRDomains()
        else -> getGuestDomains()
    }
}

// Updated Quick Action functions
@Composable
private fun FreelancerQuickActions(navController: NavController, showContent: Boolean) {
    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn(animationSpec = tween(1500))
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Freelancer Dashboard",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 30.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            getFreelancerQuickActions().forEach { action ->
                QuickActionCard(
                    title = "${action.icon} ${action.title}",
                    description = action.description,
                    buttonText = action.buttonText,
                    onClick = {
                        when(action.buttonText) {
                            "Apply Now" -> navController.navigate(Screen.ApplicationForm.route)
                            "View Proposals" -> navController.navigate(Screen.FlDashboard.route)
                            "View Projects" -> navController.navigate(Screen.AvailableJobRoles.route)
                            else -> navController.navigate(Screen.ApplicationForm.route)
                        }
                    }
                )
                Spacer(Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(15.dp))
            Divider()
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
private fun InternQuickActions(navController: NavController, showContent: Boolean) {
    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn(animationSpec = tween(1500))
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Internship Dashboard",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 30.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            getInternQuickActions().forEach { action ->
                QuickActionCard(
                    title = "${action.icon} ${action.title}",
                    description = action.description,
                    buttonText = action.buttonText,
                    onClick = {
                        when(action.buttonText) {
                            "Apply Now" -> navController.navigate(Screen.ApplicationForm.route)
                            "View Status" -> navController.navigate(Screen.CandidateDashboard.route)
                            "View Roles" -> navController.navigate(Screen.AvailableJobRoles.route)
                            else -> navController.navigate(Screen.ApplicationForm.route)
                        }
                    }
                )
                Spacer(Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(15.dp))
            Divider()
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
private fun JobSeekerQuickActions(navController: NavController, showContent: Boolean) {
    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn(animationSpec = tween(1500))
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Career Dashboard",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 30.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            getJobSeekerQuickActions().forEach { action ->
                QuickActionCard(
                    title = "${action.icon} ${action.title}",
                    description = action.description,
                    buttonText = action.buttonText,
                    onClick = {
                        when(action.buttonText) {
                            "Apply Now" -> navController.navigate(Screen.ApplicationForm.route)
                            "Track Progress" -> navController.navigate(Screen.FullTimejobDashboard.route)
                            "Browse Jobs" -> navController.navigate(Screen.AvailableJobRoles.route)
                            else -> navController.navigate(Screen.ApplicationForm.route)
                        }
                    }
                )
                Spacer(Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(15.dp))
            Divider()
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
private fun HRQuickActions(navController: NavController, showContent: Boolean) {
    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn(animationSpec = tween(1500))
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Employer Dashboard",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 30.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            getHRQuickActions().forEach { action ->
                QuickActionCard(
                    title = "${action.icon} ${action.title}",
                    description = action.description,
                    buttonText = action.buttonText,
                    onClick = {
                        when (action.buttonText) {
                            "Post Job" -> navController.navigate(Screen.JobOpeningsManager.route)
                            "View Jobs" -> navController.navigate(Screen.AvailableJobRoles.route)
                            "Review Applications" -> navController.navigate(Screen.HrDashboard.route)

                        }
                    }
                )
                Spacer(Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(15.dp))
            Divider()
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// Updated GuestContent function
@Composable
private fun GuestContent(navController: NavController, showContent: Boolean) {
    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn(animationSpec = tween(1200))
    ) {
        Column {
            Text(
                text = "Choose Your Career Path",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            getGuestRoleCards().forEach { roleCard ->
                EnhancedRoleCard(
                    title = roleCard.title,
                    description = roleCard.description,
                    features = roleCard.features,
                    buttonText = roleCard.buttonText,
                    onClick = { navController.navigate(Screen.LoginScreen.route) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Enhanced Role Card with better styling
@Composable
private fun EnhancedRoleCard(
    title: String,
    description: String,
    features: List<String>,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.body1,
                color = Color.DarkGray,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            features.forEach { feature ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "✅",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.body2,
                        color = Color.Black,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedButton(
                onclick = onClick,
                text = buttonText,
                delay = 300,
                filled = true
            )
        }
    }
}

// Add missing getGuestDomains function
private fun getGuestDomains(): List<DomainCategory> = listOf(
    DomainCategory("Technology & Innovation", listOf(
        "AI/ML/DL/GenAI/LLM Development",
        "Blockchain & Web3 Engineering",
        "Full Stack Development",
        "Mobile App Development",
        "DevSecOps & Cloud Engineering",
        "Cybersecurity & Data Protection"
    )),
    DomainCategory("Business & Strategy", listOf(
        "Business Strategy & Analysis",
        "Product Management & Strategy",
        "Project Management & Agile",
        "Digital Marketing & Growth",
        "Financial Analysis & Planning",
        "Operations & Process Optimization"
    )),
    DomainCategory("Creative & Professional", listOf(
        "UX/UI Design & Research",
        "Content Creation & Copywriting",
        "Brand Design & Marketing",
        "Video Production & Multimedia",
        "Legal Advisory & Compliance",
        "HR & Talent Management"
    ))
)