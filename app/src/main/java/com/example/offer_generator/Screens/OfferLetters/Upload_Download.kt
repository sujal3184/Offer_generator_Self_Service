package com.example.offer_generator.Screens.OfferLetters

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
import androidx.core.content.FileProvider
import com.example.offer_generator.ViewModels.WhoLoginViewModel
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
    val localFilePath: String? = null
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

// Object to store uploaded documents globally
object DocumentStorage {
    private var _uploadedDocument: UploadedDocument? = null

    fun setDocument(document: UploadedDocument) {
        _uploadedDocument = document
    }

    fun getDocument(): UploadedDocument? = _uploadedDocument

    fun clearDocument() {
        _uploadedDocument = null
    }

    fun hasDocument(): Boolean = _uploadedDocument != null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignedOfferLetterScreen(
    viewModel: WhoLoginViewModel,
    onClose: () -> Unit = {}
) {
    var uploadedDocument by remember { mutableStateOf(DocumentStorage.getDocument()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isHR = viewModel.isHrLoggedIn.value

    // Enhanced color scheme
    val purpleGradient = listOf(Color(0xFF7B68EE), Color(0xFF9370DB))
    val lightPurple = Color(0xFFE6E0FF)
    val darkPurple = Color(0xFF5A4FCF)
    val accentPurple = Color(0xFF8A7FFF)
    val successGreen = Color(0xFF10B981)
    val errorRed = Color(0xFFEF4444)

    // Animated values for enhanced UI
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Download PDF to phone's internal storage
    fun downloadPDFToStorage(document: UploadedDocument) {
        isDownloading = true
        try {
            val fileName = "signed_offer_letter_${System.currentTimeMillis()}.pdf"

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
            DocumentStorage.setDocument(updatedDocument)
            showSuccessAnimation = true

            Toast.makeText(
                context,
                "PDF saved successfully!",
                Toast.LENGTH_LONG
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
        DocumentStorage.clearDocument()
        errorMessage = null
        showSuccessAnimation = false
        Toast.makeText(context, "Document removed successfully", Toast.LENGTH_SHORT).show()
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
                        data = data
                    )
                    uploadedDocument = document
                    DocumentStorage.setDocument(document)
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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lightPurple.copy(alpha = 0.4f),
                        Color.White,
                        lightPurple.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {


            Spacer(modifier = Modifier.height(24.dp))

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
                        HREmptyStateContent(darkPurple, accentPurple, pulseAlpha)
                    } else {
                        // Candidate View - Upload interface
                        CandidateUploadContent(
                            isLoading = isLoading,
                            darkPurple = darkPurple,
                            accentPurple = accentPurple,
                            lightPurple = lightPurple,
                            purpleGradient = purpleGradient,
                            onUploadClick = { pdfPickerLauncher.launch("application/pdf") }
                        )
                    }
                } else {
                    // Document uploaded - show for both HR and Candidate
                    DocumentUploadedContent(
                        document = uploadedDocument!!,
                        isHR = isHR,
                        showSuccessAnimation = showSuccessAnimation,
                        darkPurple = darkPurple,
                        accentPurple = accentPurple,
                        lightPurple = lightPurple,
                        successGreen = successGreen,
                        onViewPDF = { openPDFWithBuiltInViewer(uploadedDocument!!) },
                        onRemoveDocument = { removeDocument() }
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
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = errorRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = error,
                                    color = errorRed,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Actions (only for candidates when document is uploaded)
            if (!isHR && uploadedDocument != null) {
                BottomActionButtons(
                    darkPurple = darkPurple,
                    errorRed = errorRed,
                    onRemove = { removeDocument() },
                    onClose = onClose
                )
            }
        }

        // Loading overlay
        if (isLoading || isDownloading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = accentPurple,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isLoading) "Processing document..." else "Saving to device...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = darkPurple
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HREmptyStateContent(
    darkPurple: Color,
    accentPurple: Color,
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
                    accentPurple.copy(alpha = pulseAlpha),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = darkPurple
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Waiting for Candidate",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = darkPurple
        )

        Text(
            text = "The candidate has not uploaded their signed offer letter yet. Once uploaded, it will appear here for review.",
            style = MaterialTheme.typography.bodyLarge,
            color = accentPurple,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
private fun CandidateUploadContent(
    isLoading: Boolean,
    darkPurple: Color,
    accentPurple: Color,
    lightPurple: Color,
    purpleGradient: List<Color>,
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
            color = darkPurple
        )

        Text(
            text = "Please select and upload your signed offer letter document (PDF format)",
            style = MaterialTheme.typography.bodyLarge,
            color = accentPurple,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
        )

        // Enhanced Upload Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isLoading) { onUploadClick() }
                .shadow(12.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isLoading) Color.Gray.copy(alpha = 0.1f)
                else lightPurple.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = accentPurple,
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Processing your document...",
                        style = MaterialTheme.typography.titleMedium,
                        color = accentPurple,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(purpleGradient),
                                shape = CircleShape
                            )
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Tap to Upload PDF",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = darkPurple
                    )

                    Text(
                        text = "Select your signed offer letter from device storage",
                        style = MaterialTheme.typography.bodyMedium,
                        color = accentPurple,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
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
    darkPurple: Color,
    accentPurple: Color,
    lightPurple: Color,
    successGreen: Color,
    onViewPDF: () -> Unit,
    onRemoveDocument: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Enhanced Success Animation
        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        successGreen.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    modifier = Modifier.size(40.dp),
                    tint = successGreen
                )
            }
        }

        if (!showSuccessAnimation) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        lightPurple.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    modifier = Modifier.size(20.dp),
                    tint = successGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isHR) "Document Received!" else "Upload Successful!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = darkPurple
        )

        Text(
            text = if (isHR) "Candidate has uploaded their signed offer letter"
            else "Your signed offer letter has been uploaded successfully",
            style = MaterialTheme.typography.bodyMedium,
            color = accentPurple,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Enhanced File Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.Red.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = document.fileName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = darkPurple
                        )
                        Text(
                            text = formatFileSize(document.fileSize),
                            style = MaterialTheme.typography.bodyMedium,
                            color = accentPurple
                        )
                        Text(
                            text = "Uploaded: ${document.uploadedAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentPurple.copy(alpha = 0.7f)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(20.dp))

                // View PDF Button
                Button(
                    onClick = onViewPDF,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "View PDF Document",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionButtons(
    darkPurple: Color,
    errorRed: Color,
    onRemove: () -> Unit,
    onClose: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Remove Button
            OutlinedButton(
                onClick = onRemove,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = errorRed
                ),
                border = BorderStroke(1.dp, errorRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Remove", fontWeight = FontWeight.Medium)
            }

            // Close Button
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = darkPurple
                ),
                border = BorderStroke(1.dp, darkPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Close", fontWeight = FontWeight.Medium)
            }
        }
    }
}

// Helper function to get file name from URI
fun getFileName(context: Context, uri: Uri): String? {
    var fileName: String? = null

    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
    }

    if (fileName == null) {
        fileName = uri.path?.let { path ->
            val cut = path.lastIndexOf('/')
            if (cut != -1) path.substring(cut + 1) else path
        }
    }

    return fileName
}

// Helper function to format file size
fun formatFileSize(bytes: Int): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "${String.format("%.1f", kb)} KB"
    val mb = kb / 1024.0
    return "${String.format("%.1f", mb)} MB"
}