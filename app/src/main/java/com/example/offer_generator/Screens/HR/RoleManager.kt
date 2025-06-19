package com.example.offer_generator.Screens.HR

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.offer_generator.ViewModels.JobOpeningsViewModel
import kotlinx.coroutines.launch


// Enhanced data class to support employment types
data class JobDomain(
    val name: String,
    val roles: Map<String, Map<String, Int>>, // role -> (employmentType -> count)
    val color: Color
)

// Employment types enum
enum class EmploymentType(val displayName: String) {
    FREELANCER("freelancer"),
    INTERN("intern"),
    FULL_TIME("fulltime")
}


// Enhanced Repository class with sample data for testing
class JobOpeningsRepository {
    private val _jobOpenings = mutableStateOf(getInitialJobStructure())
    val jobOpenings: State<Map<String, JobDomain>> = _jobOpenings

    private fun getInitialJobStructure(): Map<String, JobDomain> {
        // Add some sample data for testing - you can remove this later
        val sampleEmploymentTypes = mapOf(
            EmploymentType.FREELANCER.name to 0,
            EmploymentType.INTERN.name to 0,
            EmploymentType.FULL_TIME.name to 0
        )

        val emptyEmploymentTypes = mapOf(
            EmploymentType.FREELANCER.name to 0,
            EmploymentType.INTERN.name to 0,
            EmploymentType.FULL_TIME.name to 0
        )

        return mapOf(
            "technical" to JobDomain(
                name = "Technical",
                color = Color(0xFF667eea),
                roles = mapOf(
                    "AI/ML" to sampleEmploymentTypes, // Sample data
                    "Blockchain" to mapOf(
                        EmploymentType.FREELANCER.name to 0,
                        EmploymentType.INTERN.name to 0,
                        EmploymentType.FULL_TIME.name to 0
                    ),
                    "Web3" to emptyEmploymentTypes,
                    "Frontend" to mapOf(
                        EmploymentType.FREELANCER.name to 0,
                        EmploymentType.INTERN.name to 0,
                        EmploymentType.FULL_TIME.name to 0
                    ),
                    "Backend" to sampleEmploymentTypes,
                    "Fullstack" to emptyEmploymentTypes,
                    "DevSecOps" to emptyEmploymentTypes
                )
            ),
            "business" to JobDomain(
                name = "Business",
                color = Color(0xFF6B73d6),
                roles = mapOf(
                    "Business Strategy" to mapOf(
                        EmploymentType.FREELANCER.name to 0,
                        EmploymentType.INTERN.name to 0,
                        EmploymentType.FULL_TIME.name to 0
                    ),
                    "Business Analyst" to sampleEmploymentTypes,
                    "Product Management" to emptyEmploymentTypes,
                    "Project Management" to mapOf(
                        EmploymentType.FREELANCER.name to 0,
                        EmploymentType.INTERN.name to 0,
                        EmploymentType.FULL_TIME.name to 0
                    ),
                    "Marketing" to emptyEmploymentTypes
                )
            ),
            "design" to JobDomain(
                name = "Design",
                color = Color(0xFF7061be),
                roles = mapOf(
                    "UI/UX" to mapOf(
                        EmploymentType.FREELANCER.name to 0,
                        EmploymentType.INTERN.name to 0,
                        EmploymentType.FULL_TIME.name to 0
                    )
                )
            ),
            "professional" to JobDomain(
                name = "Professional",
                color = Color(0xFF764ba2),
                roles = mapOf(
                    "Legal" to mapOf(
                        EmploymentType.FREELANCER.name to 0,
                        EmploymentType.INTERN.name to 0,
                        EmploymentType.FULL_TIME.name to 0
                    ),
                    "Chartered Accountant" to emptyEmploymentTypes,
                    "Company Secretary" to emptyEmploymentTypes,
                    "HR" to mapOf(
                        EmploymentType.FREELANCER.name to 0,
                        EmploymentType.INTERN.name to 0,
                        EmploymentType.FULL_TIME.name to 0
                    )
                )
            )
        )
    }

    // Rest of your methods remain the same...
    fun updateJobCountForEmploymentType(domain: String, role: String, employmentType: EmploymentType, action: String) {
        val currentDomain = _jobOpenings.value[domain] ?: return
        val currentRoleTypes = currentDomain.roles[role] ?: return
        val currentCount = currentRoleTypes[employmentType.name] ?: 0
        val newCount = when (action) {
            "add" -> currentCount + 1
            "remove" -> maxOf(0, currentCount - 1)
            else -> currentCount
        }
        val updatedEmploymentTypes = currentRoleTypes.toMutableMap()
        updatedEmploymentTypes[employmentType.name] = newCount
        val updatedRoles = currentDomain.roles.toMutableMap()
        updatedRoles[role] = updatedEmploymentTypes
        val updatedDomain = currentDomain.copy(roles = updatedRoles)
        val updatedJobOpenings = _jobOpenings.value.toMutableMap()
        updatedJobOpenings[domain] = updatedDomain
        _jobOpenings.value = updatedJobOpenings
    }

    fun addNewRole(domain: String, roleName: String) {
        val currentDomain = _jobOpenings.value[domain] ?: return
        val initialEmploymentTypes = mapOf(
            EmploymentType.FREELANCER.name to 0,
            EmploymentType.INTERN.name to 0,
            EmploymentType.FULL_TIME.name to 0
        )
        val updatedRoles = currentDomain.roles.toMutableMap()
        updatedRoles[roleName] = initialEmploymentTypes
        val updatedDomain = currentDomain.copy(roles = updatedRoles)
        val updatedJobOpenings = _jobOpenings.value.toMutableMap()
        updatedJobOpenings[domain] = updatedDomain
        _jobOpenings.value = updatedJobOpenings
    }

    fun removeRole(domain: String, roleName: String) {
        val currentDomain = _jobOpenings.value[domain] ?: return
        val updatedRoles = currentDomain.roles.toMutableMap()
        updatedRoles.remove(roleName)
        val updatedDomain = currentDomain.copy(roles = updatedRoles)
        val updatedJobOpenings = _jobOpenings.value.toMutableMap()
        updatedJobOpenings[domain] = updatedDomain
        _jobOpenings.value = updatedJobOpenings
    }

    fun getTotalOpenings(): Int {
        return _jobOpenings.value.values.sumOf { domain ->
            domain.roles.values.sumOf { employmentTypes ->
                employmentTypes.values.sum()
            }
        }
    }

    fun getTotalForEmploymentType(employmentType: EmploymentType): Int {
        return _jobOpenings.value.values.sumOf { domain ->
            domain.roles.values.sumOf { employmentTypes ->
                employmentTypes[employmentType.name] ?: 0
            }
        }
    }

    fun getDomainTotal(domain: String): Int {
        return _jobOpenings.value[domain]?.roles?.values?.sumOf { employmentTypes ->
            employmentTypes.values.sum()
        } ?: 0
    }

    fun getDomainTotalForEmploymentType(domain: String, employmentType: EmploymentType): Int {
        return _jobOpenings.value[domain]?.roles?.values?.sumOf { employmentTypes ->
            employmentTypes[employmentType.name] ?: 0
        } ?: 0
    }

    fun getRoleCountForEmploymentType(domain: String, role: String, employmentType: EmploymentType): Int {
        return _jobOpenings.value[domain]?.roles?.get(role)?.get(employmentType.name) ?: 0
    }

    fun getEmploymentTypesForRole(domain: String, role: String): Map<EmploymentType, Int> {
        val roleData = _jobOpenings.value[domain]?.roles?.get(role) ?: return emptyMap()
        return EmploymentType.values().associateWith { type ->
            roleData[type.name] ?: 0
        }
    }

    fun getJobOpeningsData(): Map<String, JobDomain> {
        return _jobOpenings.value
    }
}
fun PostJobs(jobViewModel: JobOpeningsViewModel) {
    val postedJobs = jobViewModel.postAllJobs()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobOpeningsManager(
    navController: NavController,
    jobViewModel: JobOpeningsViewModel
) {
    var selectedTab by remember { mutableStateOf("technical") }
    var selectedEmploymentType by remember { mutableStateOf(EmploymentType.FULL_TIME) }
    var showDetails by remember { mutableStateOf(false) }
    var showAddJobDialog by remember { mutableStateOf(false) }
    var showRemoveJobDialog by remember { mutableStateOf(false) }
    var roleToRemove by remember { mutableStateOf("") }
    val repository = jobViewModel.repository
    val jobOpenings by repository.jobOpenings
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            // Fixed Header with adjusted text position
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .background(Color(0xFF4C00B0)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Lokachakra",
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 100.dp)
            ) {
                // Enhanced Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF667eea),
                                        Color(0xFF764ba2)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.padding(end = 16.dp)) {
                                Text(
                                    text = "Job Openings Manager",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Total ${selectedEmploymentType.displayName} Openings: ${jobViewModel.getTotalForEmploymentType(selectedEmploymentType)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { showDetails = !showDetails },
                                    modifier = Modifier
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (showDetails) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (showDetails) "Hide Details" else "Show Details",
                                        tint = Color.White
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = "Job Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }

                // Employment Type Toggle Bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EmploymentType.values().forEach { employmentType ->
                            val isSelected = selectedEmploymentType == employmentType
                            val employmentTypeColor = when (employmentType) {
                                EmploymentType.INTERN -> Color(0xFF2196F3)
                                EmploymentType.FREELANCER -> Color(0xFF9C27B0)
                                EmploymentType.FULL_TIME -> Color(0xFF4CAF50)
                            }

                            Button(
                                onClick = { selectedEmploymentType = employmentType },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) employmentTypeColor else Color.Transparent,
                                    contentColor = if (isSelected) Color.White else employmentTypeColor
                                ),
                                border = if (!isSelected) BorderStroke(2.dp, employmentTypeColor) else null,
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = employmentType.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    if (showDetails) {
                                        Text(
                                            text = "(${jobViewModel.getTotalForEmploymentType(employmentType)})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Enhanced Domain Tabs
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = jobOpenings.keys.indexOf(selectedTab),
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        edgePadding = 0.dp,
                        indicator = { tabPositions ->
                            if (tabPositions.isNotEmpty() && selectedTab in jobOpenings.keys) {
                                val selectedIndex = jobOpenings.keys.indexOf(selectedTab)
                                if (selectedIndex in tabPositions.indices) {
                                    TabRowDefaults.Indicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                                        color = jobOpenings[selectedTab]?.color ?: Color.Blue,
                                        height = 3.dp
                                    )
                                }
                            }
                        }
                    ) {
                        jobOpenings.forEach { (key, domain) ->
                            Tab(
                                selected = selectedTab == key,
                                onClick = { selectedTab = key },
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = domain.name,
                                        fontWeight = if (selectedTab == key) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == key) domain.color else Color.Gray
                                    )
                                    if (showDetails) {
                                        Text(
                                            text = "(${jobViewModel.getDomainTotalForEmploymentType(key, selectedEmploymentType)})",
                                            fontSize = 20.sp,
                                            color = domain.color,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showAddJobDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = jobOpenings[selectedTab]?.color ?: Color.Blue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Job",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Role",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = { showRemoveJobDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE53E3E)
                        ),
                        border = BorderStroke(2.dp, Color(0xFFE53E3E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Job",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Remove Role",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Enhanced Job Roles List
                jobOpenings[selectedTab]?.let { domain ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(domain.roles.toList()) { (roleName, employmentTypes) ->
                            EnhancedJobRoleCard(
                                roleName = roleName,
                                count = employmentTypes[selectedEmploymentType.name] ?: 0,
                                allEmploymentTypes = employmentTypes,
                                selectedEmploymentType = selectedEmploymentType,
                                domainColor = domain.color,
                                showDetails = showDetails,
                                onAddClick = {
                                    scope.launch {
                                        jobViewModel.updateJobCountForEmploymentType(selectedTab, roleName, selectedEmploymentType, "add")
                                    }
                                },
                                onRemoveClick = {
                                    scope.launch {
                                        jobViewModel.updateJobCountForEmploymentType(selectedTab, roleName, selectedEmploymentType, "remove")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation Buttons
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp).padding(horizontal = 16.dp).padding(top = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.navigate("home_screen")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53E3E)
                    ),
                    border = BorderStroke(2.dp, Color(0xFFE53E3E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = {
                        PostJobs(jobViewModel)
                        navController.navigate("home_screen")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF38A169)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PostAdd,
                        contentDescription = "Post",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Post Jobs",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Add Job Dialog
    if (showAddJobDialog) {
        AddJobDialog(
            domainName = jobOpenings[selectedTab]?.name ?: "",
            domainColor = jobOpenings[selectedTab]?.color ?: Color.Blue,
            onDismiss = { showAddJobDialog = false },
            onConfirm = { roleName ->
                repository.addNewRole(selectedTab, roleName)
                showAddJobDialog = false
            }
        )
    }

    // Remove Job Dialog
    if (showRemoveJobDialog) {
        RemoveJobDialog(
            domainName = jobOpenings[selectedTab]?.name ?: "",
            domainColor = jobOpenings[selectedTab]?.color ?: Color.Blue,
            availableRoles = jobOpenings[selectedTab]?.roles?.keys?.toList() ?: emptyList(),
            onDismiss = { showRemoveJobDialog = false },
            onConfirm = { roleName ->
                repository.removeRole(selectedTab, roleName)
                showRemoveJobDialog = false
            }
        )
    }
}

@Composable
fun EnhancedJobRoleCard(
    roleName: String,
    count: Int,
    allEmploymentTypes: Map<String, Int>,
    selectedEmploymentType: EmploymentType,
    domainColor: Color,
    showDetails: Boolean,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = roleName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (showDetails) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${selectedEmploymentType.displayName} positions: $count",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Count Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = domainColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                domainColor.copy(alpha = 0.3f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = count.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    // Remove Button
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53E3E))
                            .border(1.dp, Color(0xFFE53E3E).copy(alpha = 0.3f), CircleShape),
                        enabled = count > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove Opening",
                            tint = if (count > 0) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Add Button
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38A169))
                            .border(1.dp, Color(0xFF38A169).copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Opening",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Show all employment types breakdown when details are visible
            if (showDetails && allEmploymentTypes.values.any { it > 0 }) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Employment Type Breakdown:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EmploymentType.values().forEach { empType ->
                        val empCount = allEmploymentTypes[empType.name] ?: 0
                        if (empCount > 0) {
                            val empColor = when (empType) {
                                EmploymentType.FREELANCER -> Color(0xFF9C27B0)
                                EmploymentType.INTERN -> Color(0xFF2196F3)
                                EmploymentType.FULL_TIME -> Color(0xFF4CAF50)
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        color = empColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        empColor.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${empType.displayName}: $empCount",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = empColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJobDialog(
    domainName: String,
    domainColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var jobRoleName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add New $domainName Role",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black, // Fixed: Changed to black
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = jobRoleName,
                    onValueChange = { jobRoleName = it },
                    label = { Text("Job Role Name", color = Color.Black) }, // Fixed: Label color to black
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = domainColor,
                        focusedLabelColor = domainColor,
                        unfocusedLabelColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = Color.Black)
                    }

                    Button(
                        onClick = {
                            if (jobRoleName.isNotBlank()) {
                                onConfirm(jobRoleName.trim())
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = domainColor),
                        shape = RoundedCornerShape(12.dp),
                        enabled = jobRoleName.isNotBlank()
                    ) {
                        Text("Add Role", color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveJobDialog(
    domainName: String,
    domainColor: Color,
    availableRoles: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Remove $domainName Role",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedRole,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Role to Remove", color = Color.Black) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE53E3E),
                            focusedLabelColor = Color(0xFFE53E3E),
                            unfocusedLabelColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        availableRoles.forEach { role ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedRole = role
                                    expanded = false
                                },
                                text = { Text(role, color = Color.Black) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = Color.Black)
                    }

                    Button(
                        onClick = {
                            if (selectedRole.isNotBlank()) {
                                onConfirm(selectedRole)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = selectedRole.isNotBlank()
                    ) {
                        Text("Remove Role", color = Color.White)
                    }
                }
            }
        }
    }
}