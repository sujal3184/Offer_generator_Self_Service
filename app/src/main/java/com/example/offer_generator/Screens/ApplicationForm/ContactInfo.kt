package com.example.offer_generator.Screens.ApplicationForm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.offer_generator.R
import com.example.offer_generator.ViewModels.WhoLoginViewModel

@Composable
fun ContactInfoStep(formData: FormData, viewModel: WhoLoginViewModel) {
    val userType = viewModel.getCurrentUserType()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth()){
            Column{
                Text(
                    text = "Mobile Number *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Alternative approach using focus state and text selection
                var hasFocus by remember { mutableStateOf(false) }
                var hasBeenFocused by remember { mutableStateOf(false) }
                var textFieldValue by remember { mutableStateOf(TextFieldValue(formData.mobileNumber)) }

                // Show error only after field has been focused and then lost focus
                val shouldShowPhoneError = hasBeenFocused && formData.mobileNumber.isNotEmpty() && !isValidPhoneNumberRegex(formData.mobileNumber)

                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        // Ensure +91 is always present
                        val newText = newValue.text
                        if (newText.startsWith("+91 ") || newText == "+91") {
                            textFieldValue = newValue
                            formData.mobileNumber = newText
                        } else if (newText.isEmpty()) {
                            textFieldValue = TextFieldValue("")
                            formData.mobileNumber = ""
                        } else {
                            // If user types without +91, prepend it and set cursor after +91
                            val updatedText = "+91 $newText"
                            textFieldValue = TextFieldValue(
                                text = updatedText,
                                selection = TextRange(4) // Position cursor after "+91 "
                            )
                            formData.mobileNumber = updatedText
                        }
                    },
                    singleLine = true,
                    placeholder = { Text("+91 9876543210", color = Color.DarkGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            hasFocus = focusState.isFocused
                            if (focusState.isFocused) {
                                hasBeenFocused = true
                                // When field gains focus and is empty, add +91 and position cursor
                                if (textFieldValue.text.isEmpty()) {
                                    textFieldValue = TextFieldValue(
                                        text = "+91 ",
                                        selection = TextRange(4) // Position cursor after "+91 "
                                    )
                                    formData.mobileNumber = "+91 "
                                }
                            }
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = if (shouldShowPhoneError) Color.Red else Color.LightGray,
                        focusedBorderColor = if (shouldShowPhoneError) Color.Red else Color.Gray,
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedLabelColor = Color.Gray,
                        focusedLabelColor = Color.Gray,
                        cursorColor = Color.Black,
                        selectionColors = TextSelectionColors(
                            handleColor = Color.Black,
                            backgroundColor = Color.Black.copy(alpha = 0.4f)
                        ),
                        errorTextColor = Color.Black,
                        errorContainerColor = Color.White,
                        errorBorderColor = Color.Red,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = shouldShowPhoneError
                )

                // Show error message for phone
                if (shouldShowPhoneError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Invalid mobile number format",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Enhanced FormTextField for Email with focus tracking
        EnhancedFormTextField(
            value = formData.email,
            onValueChange = { formData.email = it },
            label = "Email *",
            placeholder = "Enter your email address",
            validator = { isValidEmail(it) },
            errorMessage = "Invalid email address format"
        )

        // LinkedIn Profile with conditional requirements
        EnhancedFormTextField(
            value = formData.linkedinProfile,
            onValueChange = { formData.linkedinProfile = it },
            label = "LinkedIn Profile ${if (userType == "freelancer" || userType == "fulltime") "*" else "(optional)"}",
            placeholder = "https://linkedin.com/in/yourprofile",
            validator = { url ->
                if (userType == "freelancer" || userType == "fulltime") {
                    url.isNotEmpty() && isValidLinkedInUrl(url)
                } else {
                    url.isEmpty() || isValidLinkedInUrl(url)
                }
            },
            errorMessage = if (userType == "freelancer" || userType == "fulltime") {
                "LinkedIn profile is required and must be valid"
            } else {
                "Invalid LinkedIn profile URL"
            },
            isRequired = userType == "freelancer" || userType == "fulltime"
        )

        // GitHub Link with conditional requirements
        EnhancedFormTextField(
            value = formData.githubLink,
            onValueChange = { formData.githubLink = it },
            label = "GitHub Link ${if (userType == "freelancer") "*" else "(optional)"}",
            placeholder = "https://github.com/yourusername",
            validator = { url ->
                if (userType == "freelancer") {
                    url.isNotEmpty() && isValidGithubUrl(url)
                } else {
                    url.isEmpty() || isValidGithubUrl(url)
                }
            },
            errorMessage = if (userType == "freelancer") {
                "GitHub profile is required and must be valid"
            } else {
                "Invalid GitHub profile URL"
            },
            isRequired = userType == "freelancer"
        )

        // Portfolio Website with conditional requirements
        EnhancedFormTextField(
            value = formData.portfolioWebsite,
            onValueChange = { formData.portfolioWebsite = it },
            label = "Portfolio Website Link ${if (userType == "freelancer") "*" else "(Optional)"}",
            placeholder = when (userType) {
                "freelancer" -> "https://yourportfolio.com (Required for freelancers)"
                else -> "https://yourportfolio.com"
            },
            validator = { url ->
                if (userType == "freelancer") {
                    url.isNotEmpty() && isValidWebsiteUrl(url)
                } else {
                    url.isEmpty() || isValidWebsiteUrl(url)
                }
            },
            errorMessage = if (userType == "freelancer") {
                "Portfolio website is required and must be valid"
            } else {
                "Invalid website URL"
            },
            isRequired = userType == "freelancer"
        )

        // References field with conditional requirements
        EnhancedFormTextField(
            value = when(userType) {
                "intern" -> formData.references
                "freelancer" -> formData.clientReferences
                "fulltime" -> formData.professionalReferences
                else -> ""
            },
            onValueChange = {
                when(userType){
                    "intern" -> formData.references = it
                    "freelancer" -> formData.clientReferences = it
                    "fulltime" -> formData.professionalReferences = it
                }
            },
            label = when (userType) {
                "fulltime" -> "Professional References *"
                "freelancer" -> "Client References (Optional)"
                else -> "References (Optional)"
            },
            placeholder = when (userType) {
                "fulltime" -> "Enter at least 2 professional references with contact details"
                "freelancer" -> "Enter client references or testimonials"
                else -> "Enter references"
            },
            maxLines = when (userType) {
                "fulltime" -> 5
                else -> 3
            },
            validator = { refs ->
                if (userType == "fulltime") {
                    refs.isNotEmpty() && refs.length >= 30
                } else {
                    true // Optional for other user types
                }
            },
            errorMessage = "Please provide detailed reference information",
            isRequired = userType == "fulltime"
        )

        // Personal statement with different labels and requirements based on user type
        EnhancedFormTextField(
            value = when(userType){
                "intern" -> formData.personalStatement
                "freelancer" -> formData.professionalSummary
                "fulltime" -> formData.coverLetter
                else -> ""
            },
            onValueChange = {
                when(userType) {
                    "intern" -> formData.personalStatement = it
                    "freelancer" -> formData.professionalSummary = it
                    "fulltime" -> formData.coverLetter = it
                }
            },
            label = when (userType) {
                "intern" -> "Personal Statement (Optional)"
                "freelancer" -> "Professional Summary *"
                "fulltime" -> "Cover Letter *"
                else -> "Personal Statement (Optional)"
            },
            placeholder = when (userType) {
                "intern" -> "Tell us about yourself and why you want this internship"
                "freelancer" -> "Describe your skills, experience, and what services you offer"
                "fulltime" -> "Explain why you're interested in this position and what you can bring to the company"
                else -> "Tell us about yourself"
            },
            maxLines = when (userType) {
                "freelancer", "fulltime" -> 7
                else -> 5
            },
            validator = { statement ->
                when (userType) {
                    "freelancer" -> statement.isNotEmpty() && statement.length >= 50
                    "fulltime" -> statement.isNotEmpty() && statement.length >= 100
                    else -> true // Optional for interns
                }
            },
            errorMessage = when (userType) {
                "freelancer" -> "Professional summary must be at least 50 characters long"
                "fulltime" -> "Cover letter must be at least 100 characters long"
                else -> ""
            },
            isRequired = userType == "freelancer" || userType == "fulltime"
        )


    }
}

@Composable
fun EnhancedFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    validator: (String) -> Boolean,
    errorMessage: String,
    isRequired: Boolean = false
) {
    var hasBeenFocused by remember { mutableStateOf(false) }

    // Show error only after field has been focused and then lost focus
    val shouldShowError = hasBeenFocused && !validator(value)

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.DarkGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        hasBeenFocused = true
                    }
                },
            shape = RoundedCornerShape(8.dp),
            maxLines = maxLines,
            isError = shouldShowError,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = if (shouldShowError) Color.Red else Color.Black.copy(alpha = 0.5f),
                unfocusedBorderColor = if (shouldShowError) Color.Red else Color.Black.copy(alpha = 0.3f),
                focusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                cursorColor = Color.DarkGray,
                selectionColors = TextSelectionColors(
                    handleColor = Color.DarkGray,
                    backgroundColor = Color.DarkGray.copy(alpha = 0.4f)
                ),
                errorTextColor = Color.Black,
                errorContainerColor = Color.White,
                errorBorderColor = Color.Red,
            )
        )

        // Show error message if there's an error and field has been interacted with
        if (shouldShowError && errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}