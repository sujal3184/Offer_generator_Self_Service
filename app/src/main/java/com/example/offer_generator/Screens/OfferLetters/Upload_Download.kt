package com.example.offer_generator.Screens.OfferLetters

import OfferLetter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class to store uploaded document information
data class UploadedDocument(
    val uri: Uri,
    val fileName: String,
    val fileSize: Int,
    val data: ByteArray,
    val uploadedAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    val localFilePath: String? = null,
    val isUploadedToServer: Boolean = false // New field to track server upload status
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadedDocument

        if (uri != other.uri) return false
        if (fileName != other.fileName) return false
        if (fileSize != other.fileSize) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + fileSize
        result = 31 * result + data.contentHashCode()
        return result
    }
}

// Updated DocumentStorage.kt to support per-offer document storage
object DocumentStorage {
    private val documents = mutableMapOf<String, UploadedDocument?>()

    // Generate unique key for each offer/application
    private fun generateKey(offerId: String?, applicationId: String?, candidateEmail: String?): String {
        return when {
            !offerId.isNullOrEmpty() -> "offer_$offerId"
            !applicationId.isNullOrEmpty() -> "app_$applicationId"
            !candidateEmail.isNullOrEmpty() -> "email_$candidateEmail"
            else -> "default" // Fallback, though this shouldn't happen
        }
    }

    fun getDocument(offerId: String? = null, applicationId: String? = null, candidateEmail: String? = null): UploadedDocument? {
        val key = generateKey(offerId, applicationId, candidateEmail)
        return documents[key]
    }

    fun setDocument(document: UploadedDocument, offerId: String? = null, applicationId: String? = null, candidateEmail: String? = null) {
        val key = generateKey(offerId, applicationId, candidateEmail)
        documents[key] = document
    }

    fun clearDocument(offerId: String? = null, applicationId: String? = null, candidateEmail: String? = null) {
        val key = generateKey(offerId, applicationId, candidateEmail)
        documents.remove(key)
    }

    // Optional: Clear all documents (for testing or cleanup)
    fun clearAllDocuments() {
        documents.clear()
    }

    // Optional: Get all stored documents (for debugging)
    fun getAllDocuments(): Map<String, UploadedDocument?> {
        return documents.toMap()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignedOfferLetterScreen(
    viewModel: WhoLoginViewModel,
    offer: OfferLetter? = null, // Add offer parameter
    onClose: () -> Unit = {}
) {
    // Use offer-specific document storage
    var uploadedDocument by remember {
        mutableStateOf(
            DocumentStorage.getDocument(
                offerId = offer?.id,
                applicationId = offer?.applicationId,
                candidateEmail = offer?.candidateEmail
            )
        )
    }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isHR = viewModel.isHrLoggedIn.value

    val coroutineScope = rememberCoroutineScope()

    // Professional color scheme
    val primaryBlue = Color(0xFF2563EB)
    val primaryBlueLight = Color(0xFF3B82F6)
    val primaryBlueDark = Color(0xFF1D4ED8)
    val backgroundGray = Color(0xFFF8FAFC)
    val cardWhite = Color(0xFFFFFFFF)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val successGreen = Color(0xFF059669)
    val errorRed = Color(0xFFDC2626)
    val warningAmber = Color(0xFFF59E0B)
    val borderLight = Color(0xFFE2E8F0)

    // Animated values
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    suspend fun uploadToServer(
        document: UploadedDocument,
        onStart: () -> Unit,
        onSuccess: (UploadedDocument) -> Unit,
        onError: (String) -> Unit
    ) {
        onStart()

        try {
            // Simulate network delay
            delay(2000) // Simulate upload time

            // Here you would make your actual API call
            // For now, we'll simulate success
            val updatedDocument = document.copy(isUploadedToServer = true)
            onSuccess(updatedDocument)

        } catch (e: Exception) {
            onError("Upload failed: ${e.message}")
        }
    }

    fun handleUpload(document: UploadedDocument) {
        coroutineScope.launch {
            uploadToServer(
                document = document,
                onStart = {
                    isUploading = true
                    errorMessage = null
                },
                onSuccess = { updatedDocument ->
                    uploadedDocument = updatedDocument
                    // Save with offer-specific key
                    DocumentStorage.setDocument(
                        updatedDocument,
                        offerId = offer?.id,
                        applicationId = offer?.applicationId,
                        candidateEmail = offer?.candidateEmail
                    )
                    showSuccessAnimation = true
                    isUploading = false

                    Toast.makeText(
                        context,
                        "Document uploaded successfully!",
                        Toast.LENGTH_LONG
                    ).show()

                    // Add a delay to show the success animation, then close the dialog
                    coroutineScope.launch {
                        delay(1500) // Wait 1.5 seconds to show success animation
                        onClose() // Close the dialog
                    }
                },
                onError = { error ->
                    errorMessage = error
                    isUploading = false
                }
            )
        }
    }

    // Download PDF to phone's internal storage
    fun downloadPDFToStorage(document: UploadedDocument) {
        isDownloading = true
        try {
            val fileName = "signed_offer_letter_${offer?.candidateName?.replace(" ", "_") ?: "candidate"}_${System.currentTimeMillis()}.pdf"

            val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            } else {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            }

            file.parentFile?.mkdirs()

            FileOutputStream(file).use { fos ->
                fos.write(document.data)
                fos.flush()
            }

            val updatedDocument = document.copy(localFilePath = file.absolutePath)
            uploadedDocument = updatedDocument
            // Save with offer-specific key
            DocumentStorage.setDocument(
                updatedDocument,
                offerId = offer?.id,
                applicationId = offer?.applicationId,
                candidateEmail = offer?.candidateEmail
            )

            Toast.makeText(
                context,
                "PDF saved to device storage",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error saving PDF: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            isDownloading = false
        }
    }

    // Remove uploaded document
    fun removeDocument() {
        uploadedDocument?.localFilePath?.let { path ->
            try {
                File(path).delete()
            } catch (e: Exception) {
                // Ignore deletion errors
            }
        }
        uploadedDocument = null
        // Clear with offer-specific key
        DocumentStorage.clearDocument(
            offerId = offer?.id,
            applicationId = offer?.applicationId,
            candidateEmail = offer?.candidateEmail
        )
        errorMessage = null
        showSuccessAnimation = false
        Toast.makeText(context, "Document removed", Toast.LENGTH_SHORT).show()
    }

    // Activity result launcher for picking PDF files
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isLoading = true
            errorMessage = null

            try {
                val fileName = getFileName(context, it) ?: "signed_offer_letter.pdf"
                val inputStream = context.contentResolver.openInputStream(it)
                val data = inputStream?.readBytes()
                inputStream?.close()

                if (data != null) {
                    val document = UploadedDocument(
                        uri = it,
                        fileName = fileName,
                        fileSize = data.size,
                        data = data,
                        isUploadedToServer = false
                    )
                    uploadedDocument = document
                    // Save with offer-specific key
                    DocumentStorage.setDocument(
                        document,
                        offerId = offer?.id,
                        applicationId = offer?.applicationId,
                        candidateEmail = offer?.candidateEmail
                    )
                    downloadPDFToStorage(document)
                } else {
                    errorMessage = "Error reading PDF file"
                }

                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error reading PDF: ${e.message}"
                isLoading = false
            }
        }
    }

    // Open PDF with built-in viewer
    fun openPDFWithBuiltInViewer(document: UploadedDocument) {
        try {
            val fileToOpen = if (document.localFilePath != null && File(document.localFilePath).exists()) {
                File(document.localFilePath)
            } else {
                val tempFile = File(context.cacheDir, "temp_${document.fileName}")
                FileOutputStream(tempFile).use { fos ->
                    fos.write(document.data)
                }
                tempFile
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                fileToOpen
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                val chooserIntent = Intent.createChooser(intent, "Open PDF with...")
                chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(chooserIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Main Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header with offer information
            if (offer != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${offer.position} in ${offer.companyName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Candidate:   ${offer.candidateName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (uploadedDocument == null) {
                    if (isHR) {
                        // HR View - No document uploaded yet
                        HREmptyStateContent(
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            primaryBlue = primaryBlue,
                            pulseAlpha = pulseAlpha
                        )
                    } else {
                        // Candidate View - Upload interface
                        CandidateUploadContent(
                            isLoading = isLoading,
                            primaryBlue = primaryBlue,
                            primaryBlueLight = primaryBlueLight,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            cardWhite = cardWhite,
                            onUploadClick = { pdfPickerLauncher.launch("application/pdf") }
                        )
                    }
                } else {
                    // Document uploaded - show for both HR and Candidate
                    DocumentUploadedContent(
                        document = uploadedDocument!!,
                        isHR = isHR,
                        showSuccessAnimation = showSuccessAnimation,
                        primaryBlue = primaryBlue,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        successGreen = successGreen,
                        cardWhite = cardWhite,
                        borderLight = borderLight,
                        onViewPDF = { openPDFWithBuiltInViewer(uploadedDocument!!) }
                    )
                }

                // Error Message
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    errorMessage?.let { error ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = errorRed.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, errorRed.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = errorRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = error,
                                    color = errorRed,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Actions
            if (!isHR && uploadedDocument != null) {
                BottomActionButtons(
                    document = uploadedDocument!!,
                    primaryBlue = primaryBlue,
                    primaryBlueDark = primaryBlueDark,
                    errorRed = errorRed,
                    textSecondary = textSecondary,
                    cardWhite = cardWhite,
                    isUploading = isUploading,
                    onUpload = { handleUpload(uploadedDocument!!) },
                    onRemove = { removeDocument() },
                    onClose = onClose
                )
            }
        }

        // Loading overlay
        if (isLoading || isDownloading || isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardWhite),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.shadow(16.dp, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = primaryBlue,
                            modifier = Modifier.size(56.dp),
                            strokeWidth = 6.dp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = when {
                                isLoading -> "Processing document..."
                                isDownloading -> "Saving to device..."
                                isUploading -> "Uploading to server..."
                                else -> "Please wait..."
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = textPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HREmptyStateContent(
    textPrimary: Color,
    textSecondary: Color,
    primaryBlue: Color,
    pulseAlpha: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated waiting icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    primaryBlue.copy(alpha = pulseAlpha * 0.3f),
                    CircleShape
                )
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = primaryBlue
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Waiting for Document",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = textPrimary
        )

        Text(
            text = "The candidate hasn't uploaded their signed offer letter yet. You'll be notified once it's available for review.",
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun CandidateUploadContent(
    isLoading: Boolean,
    primaryBlue: Color,
    primaryBlueLight: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardWhite: Color,
    onUploadClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Upload Signed Offer Letter",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = textPrimary
        )

        Text(
            text = "Please select your signed offer letter document in PDF format from your device storage",
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
            lineHeight = 24.sp
        )

        // Enhanced Upload Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isLoading) { onUploadClick() }
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isLoading) Color.Gray.copy(alpha = 0.1f) else cardWhite
            ),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                width = 2.dp,
                color = if (isLoading) Color.Gray.copy(alpha = 0.3f) else primaryBlue.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(72.dp),
                        color = primaryBlue,
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Processing Document",
                        style = MaterialTheme.typography.titleLarge,
                        color = textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Please wait while we prepare your document...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(primaryBlue, primaryBlueLight)
                                ),
                                shape = CircleShape
                            )
                            .shadow(12.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            modifier = Modifier.size(44.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Choose PDF Document",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    Text(
                        text = "Tap here to browse and select your signed offer letter from device storage",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 12.dp),
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentUploadedContent(
    document: UploadedDocument,
    isHR: Boolean,
    showSuccessAnimation: Boolean,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    successGreen: Color,
    cardWhite: Color,
    borderLight: Color,
    onViewPDF: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Icon
        val statusIcon = if (document.isUploadedToServer) {
            Icons.Default.CheckCircle to successGreen
        } else {
            Icons.Default.CloudUpload to primaryBlue
        }

        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = scaleIn(animationSpec = tween(500)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        statusIcon.second.copy(alpha = 0.15f),
                        CircleShape
                    )
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon.first,
                    contentDescription = "Status",
                    modifier = Modifier.size(48.dp),
                    tint = statusIcon.second
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = when {
                isHR -> "Document Received"
                document.isUploadedToServer -> "Upload Complete"
                else -> "Document Ready"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = textPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Enhanced File Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = cardWhite),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, borderLight)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Color(0xFFDC2626).copy(alpha = 0.1f),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = document.fileName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatFileSize(document.fileSize),
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecondary
                            )
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecondary
                            )
                            Text(
                                text = if (document.isUploadedToServer) "Uploaded" else "Local",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (document.isUploadedToServer) successGreen else primaryBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = document.uploadedAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecondary.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // View PDF Button
                Button(
                    onClick = onViewPDF,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "View Document",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionButtons(
    document: UploadedDocument,
    primaryBlue: Color,
    primaryBlueDark: Color,
    errorRed: Color,
    textSecondary: Color,
    cardWhite: Color,
    isUploading: Boolean,
    onUpload: () -> Unit,
    onRemove: () -> Unit,
    onClose: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(24.dp))

        if (!document.isUploadedToServer) {

            // Remove and Upload buttons before server upload
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Remove Button
                OutlinedButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = errorRed,
                        containerColor = cardWhite
                    ),
                    border = BorderStroke(2.dp, errorRed),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isUploading
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Remove",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Upload Button
                Button(
                    onClick = onUpload,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isUploading,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isUploading) "Uploading..." else "Upload",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    result = it.getString(displayNameIndex)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result
}

// Utility function to format file size
private fun formatFileSize(bytes: Int): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1 -> String.format("%.1f GB", gb)
        mb >= 1 -> String.format("%.1f MB", mb)
        kb >= 1 -> String.format("%.1f KB", kb)
        else -> "$bytes B"
    }
}