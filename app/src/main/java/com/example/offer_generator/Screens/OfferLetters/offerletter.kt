package com.example.offer_generator.Screens.OfferLetters

// Add these imports to your file
import OfferLetter
import OfferLetterStats
import OfferStatus
import OfferType
import SignedOfferDocument
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.offer_generator.R
import com.example.offer_generator.Screens.Internship.EnhancedStatCard
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import kotlinx.coroutines.flow.internal.NoOpContinuation.context
import kotlinx.coroutines.launch
import withSignedDocument
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

@Composable
fun OfferLettersContent(
    offerLetters: List<OfferLetter>,
    statistics: OfferLetterStats,
    onViewDetails: (OfferLetter) -> Unit
) {
    // Statistics Cards for Offers
    Text(
        "Offer Letters Overview",
        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = Color.DarkGray
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        EnhancedStatCard("Total", statistics.totalOffers.toString(), Icons.Default.Mail, Color(0xFF2196F3))
        EnhancedStatCard("Accepted", statistics.acceptedOffers.toString(), Icons.Default.CheckCircle, Color(0xFF4CAF50))
        EnhancedStatCard("Pending", statistics.pendingOffers.toString(), Icons.Default.Schedule, Color(0xFFFF9500))
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Offer Letters Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Your Offer Letters",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Gray.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            androidx.compose.material3.Icon(
                Icons.Default.Mail,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "${offerLetters.size} Offers",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Offer Letters List Header
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Company",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Position",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center
            )
            Text(
                "Status",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                "Detail",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Offer Letters List
    offerLetters.forEach { offer ->
        EnhancedOfferCard(
            offer = offer,
            onViewDetails = onViewDetails
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun EnhancedOfferCard(
    offer: OfferLetter,
    onViewDetails: (OfferLetter) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Company
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = offer.companyName,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Position
            Column(
                modifier = Modifier.weight(1.2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = offer.position,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Status
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                OfferStatusChip(status = offer.status.toString())
            }

            // View Details Icon
            androidx.compose.material3.IconButton(
                onClick = { onViewDetails(offer) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.purple).copy(alpha = 0.1f))
            ) {
                androidx.compose.material3.Icon(
                    Icons.Default.Visibility,
                    contentDescription = "View Details",
                    tint = colorResource(id = R.color.purple),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun OfferStatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "accepted" -> Color(0xFF4CAF50) to Color.White
        "pending" -> Color(0xFFFF9500) to Color.White
        "declined" -> Color(0xFFF44336) to Color.White
        else -> Color.Gray to Color.White
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NoOfferLetters(viewModel: WhoLoginViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Icon(
            Icons.Default.Mail,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.6f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Offer Letters Yet",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        if(!viewModel.isHrLoggedIn.value){
            Text(
                text = "Your offer letters will appear here once companies send them to you.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color.Gray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        else{
            Text(
                text = "You have not generated any offer letter till now.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color.Gray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

    }
}

// Example implementation of the upload handler in your main activity/viewmodel

fun handleSignedOfferUpload(
    uri: Uri,
    offer: OfferLetter,
    callback: (Boolean, OfferLetter?) -> Unit
) {
    scope.launch {
        try {
            // 1. Get file information
            val fileInfo = getFileInfoFromUri(context, uri)
            val (fileName, fileSize, currentDate) = fileInfo

            // 2. Validate file (optional)
            if (fileSize > 10 * 1024 * 1024) { // 10MB limit
                callback(false, null)
                return@launch
            }

            // 3. Save file to your storage (local/cloud)
            val savedFilePath = saveFileToStorage(uri, offer.id)

            // 4. Create SignedOfferDocument
            val signedDocument = SignedOfferDocument(
                fileName = fileName,
                filePath = savedFilePath,
                uploadDate = currentDate,
                fileSize = fileSize,
                mimeType = "application/pdf",
                documentUrl = savedFilePath // or cloud URL
            )

            // 5. Update offer letter
            val updatedOffer = offer.withSignedDocument(signedDocument)

            // 6. Save to database/repository
            val success = saveOfferLetter(updatedOffer)

            // 7. Return result
            callback(success, if (success) updatedOffer else null)

        } catch (e: Exception) {
            Log.e("UploadHandler", "Error uploading signed document", e)
            callback(false, null)
        }
    }
}

// Helper function to save file to storage
private suspend fun saveFileToStorage(uri: Uri, offerId: String): String {
    // Implementation depends on your storage solution
    // This could be:
    // - Local file storage
    // - Firebase Storage
    // - AWS S3
    // - etc.

    val fileName = "signed_offer_${offerId}_${System.currentTimeMillis()}.pdf"

    // Example for local storage:
    val internalDir = File(context.filesDir, "signed_offers")
    if (!internalDir.exists()) {
        internalDir.mkdirs()
    }

    val targetFile = File(internalDir, fileName)

    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        FileOutputStream(targetFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return targetFile.absolutePath
}

@Composable
fun SignedOfferUploadSection(
    offer: OfferLetter,
    onUploadSigned: (Uri) -> Unit,
    onViewSigned: () -> Unit,
    isUploading: Boolean = false
) {
    val context = LocalContext.current
    var uploadedFileInfo by remember { mutableStateOf<Triple<String, Long, String>?>(null) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Get file info
            val fileInfo = getFileInfoFromUri(context, selectedUri)
            uploadedFileInfo = fileInfo
            onUploadSigned(selectedUri)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Signed Offer Letter",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        // Check if offer has signed document or if file is uploaded
        if (offer.hasSignedDocument || uploadedFileInfo != null) {
            // Show uploaded/existing signed document
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E8)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Signed Document Available",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                fontSize = 16.sp
                            )

                            // Show file info if recently uploaded
                            uploadedFileInfo?.let { (fileName, fileSize, uploadDate) ->
                                Text(
                                    text = "File: $fileName",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = "Size: ${formatFileSize(fileSize)} • Uploaded: $uploadDate",
                                    color = Color(0xFF666666),
                                    fontSize = 11.sp
                                )
                            } ?: run {
                                // Show info for existing signed documents
                                offer.signedDocument?.let { signedDoc ->
                                    Text(
                                        text = "File: ${signedDoc.fileName}",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Text(
                                        text = "Size: ${formatFileSize(signedDoc.fileSize)} • Uploaded: ${signedDoc.uploadDate}",
                                        color = Color(0xFF666666),
                                        fontSize = 11.sp
                                    )
                                } ?: Text(
                                    text = "Signed offer letter is available",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // View Document Button
                        Button(
                            onClick = onViewSigned,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "View Document",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        // Replace Document Button
                        Button(
                            onClick = { filePickerLauncher.launch("application/pdf") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isUploading
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = Color.White,
                                    strokeWidth = 1.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isUploading) "Updating..." else "Replace",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Show upload section when no signed document exists
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8FAFC)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(2.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Upload Icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = Color(0xFF6366F1).copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Upload Text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Upload Signed Offer Letter",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Select a PDF file containing the signed offer letter",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Upload Button
                    Button(
                        onClick = { filePickerLauncher.launch("application/pdf") },
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1),
                            disabledContainerColor = Color(0xFF6366F1).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Uploading...",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Upload,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Choose PDF File",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // File requirements
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEF3C7)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Please upload PDF files only. Maximum size: 10MB",
                                fontSize = 11.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to get file info from URI
private fun getFileInfoFromUri(context: Context, uri: Uri): Triple<String, Long, String> {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    var fileName = "signed_offer_letter.pdf"
    var fileSize = 0L

    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

            if (nameIndex >= 0) {
                fileName = it.getString(nameIndex) ?: fileName
            }
            if (sizeIndex >= 0) {
                fileSize = it.getLong(sizeIndex)
            }
        }
    }

    val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    return Triple(fileName, fileSize, currentDate)
}

// Helper function to format file size
private fun formatFileSize(bytes: Long): String {
    if (bytes == 0L) return "0 B"

    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()

    return DecimalFormat("#,##0.#").format(
        bytes / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}

@Composable
fun OfferLetterDetailDialog(
    viewModel: WhoLoginViewModel,
    offer: OfferLetter,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onUploadSigned: (Uri, OfferLetter, (Boolean, OfferLetter?) -> Unit) -> Unit = { _, _, callback -> callback(false, null) }, // Modified callback
    onViewSigned: () -> Unit = {}
) {
    val context = LocalContext.current
    val isGeneratingPdf = remember { mutableStateOf(false) }
    val isUploadingSigned = remember { mutableStateOf(false) }

    // State to hold the current offer (will be updated when document is uploaded)
    var currentOffer by remember { mutableStateOf(offer) }

    // Tab state - 0 for Unsigned, 1 for Signed
    val selectedTabIndex = remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // Create a mock GenericApplication from the offer letter for template generation
    val mockApplication = GenericApplication(
        id = currentOffer.applicationId,
        fullName = currentOffer.candidateName,
        email = currentOffer.candidateEmail,
        role = currentOffer.position,
        branch = null,
        skills = listOf("Professional Skills"),
        availableFrom = currentOffer.startDate,
        availableUntil = currentOffer.endDate,
        type = when (currentOffer.type) {
            OfferType.INTERN -> ApplicationType.INTERNSHIP
            OfferType.FREELANCER -> ApplicationType.FREELANCER
            OfferType.FULLTIME -> ApplicationType.FULLTIME
        }
    )

    // Generate the full offer letter content using the formal template
    val fullOfferContent = OfferTemplateGenerator.generateOfferContent(
        template = OfferLetterTemplate.FORMAL,
        application = mockApplication,
        companyName = currentOffer.companyName,
        position = currentOffer.position,
        salary = currentOffer.salary,
        startDate = currentOffer.startDate,
        endDate = currentOffer.endDate,
        location = currentOffer.location,
        benefits = currentOffer.benefits,
        terms = currentOffer.terms,
        validUntil = currentOffer.validUntil,
        generatedBy = currentOffer.generatedBy
    )

    // Helper function to get file info from URI
    fun getFileInfoFromUri(uri: Uri): Triple<String, Long, String> {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        var fileName = "signed_offer_letter.pdf"
        var fileSize = 0L

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

                if (nameIndex >= 0) {
                    fileName = it.getString(nameIndex) ?: fileName
                }
                if (sizeIndex >= 0) {
                    fileSize = it.getLong(sizeIndex)
                }
            }
        }

        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        return Triple(fileName, fileSize, currentDate)
    }

    // Improved PDF Generation Function
    fun generateAndDownloadPdf() {
        isGeneratingPdf.value = true

        val pdfDocument = PdfDocument()
        try {
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            // Set up paint for text
            val titlePaint = Paint().apply {
                textSize = 20f
                color = android.graphics.Color.BLACK
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }

            val headerPaint = Paint().apply {
                textSize = 16f
                color = android.graphics.Color.BLACK
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }

            val bodyPaint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color.BLACK
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }

            var yPosition = 60f
            val margin = 50f
            val pageWidth = pageInfo.pageWidth - (margin * 2)
            val lineHeight = 20f

            // Draw title
            canvas.drawText("OFFER LETTER", margin, yPosition, titlePaint)
            yPosition += 50f

            // Draw company and position
            canvas.drawText(currentOffer.companyName, margin, yPosition, headerPaint)
            yPosition += 30f
            canvas.drawText("Position: ${currentOffer.position}", margin, yPosition, headerPaint)
            yPosition += 40f

            // Draw full offer content with robust multi-page handling
            val lines = fullOfferContent.split("\n")
            var currentPage = page
            val maxPageHeight = pageInfo.pageHeight - 100 // Leave margin at bottom

            fun createNewPage(): PdfDocument.Page {
                pdfDocument.finishPage(currentPage)
                val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val newPage = pdfDocument.startPage(newPageInfo)
                canvas = newPage.canvas
                yPosition = 60f
                return newPage
            }

            for (line in lines) {
                if (line.trim().isNotEmpty()) {
                    val words = line.trim().split(" ")
                    var currentLine = ""

                    for (word in words) {
                        // Check if we need a new page before processing
                        if (yPosition > maxPageHeight) {
                            currentPage = createNewPage()
                        }

                        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                        val textWidth = bodyPaint.measureText(testLine)

                        if (textWidth > pageWidth && currentLine.isNotEmpty()) {
                            // Draw current line
                            canvas.drawText(currentLine, margin, yPosition, bodyPaint)
                            yPosition += lineHeight

                            // Check if we need a new page after drawing
                            if (yPosition > maxPageHeight) {
                                currentPage = createNewPage()
                            }

                            currentLine = word
                        } else {
                            currentLine = testLine
                        }
                    }

                    // Draw remaining text in currentLine
                    if (currentLine.isNotEmpty()) {
                        // Check if we need a new page before drawing
                        if (yPosition > maxPageHeight) {
                            currentPage = createNewPage()
                        }
                        canvas.drawText(currentLine, margin, yPosition, bodyPaint)
                        yPosition += lineHeight
                    }
                } else {
                    // Empty line - just add spacing
                    yPosition += lineHeight / 2
                }
            }

            pdfDocument.finishPage(currentPage)

            // Save PDF with improved error handling
            val fileName = "OfferLetter_${currentOffer.candidateName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above - using MediaStore
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                try {
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { outputStream ->
                            pdfDocument.writeTo(outputStream)
                            Toast.makeText(context, "PDF saved to Downloads folder!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        throw Exception("Failed to create file URI")
                    }
                } catch (e: Exception) {
                    Log.e("PDFGeneration", "Error with MediaStore: ${e.message}")
                    throw e
                }
            } else {
                // For older Android versions - check permissions first
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Storage permission required to save PDF", Toast.LENGTH_LONG).show()
                    return
                }

                try {
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!downloadsDir.exists()) {
                        downloadsDir.mkdirs()
                    }

                    val file = File(downloadsDir, fileName)
                    FileOutputStream(file).use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                        Toast.makeText(context, "PDF saved to Downloads folder!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("PDFGeneration", "Error with file system: ${e.message}")
                    throw e
                }
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("PDFGeneration", "Error generating PDF", e)
        } finally {
            pdfDocument?.close()
            isGeneratingPdf.value = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with gradient background and fixed height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6),
                                    Color(0xFFA855F7)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val typeEmoji = when (currentOffer.type) {
                            OfferType.INTERN -> "🎓"
                            OfferType.FREELANCER -> "💼"
                            OfferType.FULLTIME -> "🏢"
                        }

                        Text(
                            text = typeEmoji,
                            fontSize = 28.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Column {
                            Text(
                                text = "Offer Letter",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${currentOffer.position} • ${currentOffer.companyName}",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Status Badge
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = when (currentOffer.status) {
                                    OfferStatus.PENDING -> Color(0xFFFFA726)
                                    OfferStatus.ACCEPTED -> Color(0xFF4CAF50)
                                    OfferStatus.REJECTED -> Color(0xFFEF5350)
                                    else -> Color.Gray
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = currentOffer.status.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Tab Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Unsigned Offer Letter Tab
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTabIndex.value = 0 },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTabIndex.value == 0)
                                Color(0xFF6366F1) else Color(0xFFF1F5F9)
                        ),
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (selectedTabIndex.value == 0) 4.dp else 0.dp
                        )
                    ) {
                        Text(
                            text = "Unsigned Offer Letter",
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedTabIndex.value == 0) Color.White else Color(0xFF64748B)
                        )
                    }

                    // Signed Offer Letter Tab
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTabIndex.value = 1 },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTabIndex.value == 1)
                                Color(0xFF6366F1) else Color(0xFFF1F5F9)
                        ),
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (selectedTabIndex.value == 1) 4.dp else 0.dp
                        )
                    ) {
                        Text(
                            text = "Signed Offer Letter",
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedTabIndex.value == 1) Color.White else Color(0xFF64748B)
                        )
                    }
                }

                // Tab Content
                when (selectedTabIndex.value) {
                    0 -> {
                        // Unsigned Offer Letter Content
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                // Full Letter Content Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFAFAFA)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = fullOfferContent,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                            ),
                                            color = Color(0xFF2D3748),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }

                            // Show status section if offer is accepted/declined
                            if (currentOffer.status != OfferStatus.PENDING) {
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = when (currentOffer.status) {
                                                OfferStatus.ACCEPTED -> Color(0xFFE8F5E8)
                                                OfferStatus.REJECTED -> Color(0xFFFFEBEE)
                                                else -> Color(0xFFF5F5F5)
                                            }
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = when (currentOffer.status) {
                                                        OfferStatus.ACCEPTED -> Icons.Default.CheckCircle
                                                        OfferStatus.REJECTED -> Icons.Default.Cancel
                                                        else -> Icons.Default.Info
                                                    },
                                                    contentDescription = null,
                                                    tint = when (currentOffer.status) {
                                                        OfferStatus.ACCEPTED -> Color(0xFF4CAF50)
                                                        OfferStatus.REJECTED -> Color(0xFFE53935)
                                                        else -> Color(0xFF757575)
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = when (currentOffer.status) {
                                                        OfferStatus.ACCEPTED -> "Offer Accepted"
                                                        OfferStatus.REJECTED -> "Offer Declined"
                                                        else -> "Status Update"
                                                    },
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (currentOffer.status) {
                                                        OfferStatus.ACCEPTED -> Color(0xFF2E7D32)
                                                        OfferStatus.REJECTED -> Color(0xFFD32F2F)
                                                        else -> Color(0xFF424242)
                                                    }
                                                )
                                            }

                                            if (currentOffer.status == OfferStatus.ACCEPTED) {
                                                Text(
                                                    text = "✓ Offer accepted by ${currentOffer.candidateName}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF4CAF50),
                                                    fontWeight = FontWeight.Medium
                                                )

                                                if (!currentOffer.acceptedDate.isNullOrEmpty()) {
                                                    Text(
                                                        text = "Accepted on: ${currentOffer.acceptedDate}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color(0xFF666666),
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    )
                                                }
                                            } else if (currentOffer.status == OfferStatus.REJECTED) {
                                                Text(
                                                    text = "This offer was declined by the candidate.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFFD32F2F),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Action buttons for Unsigned tab
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                // Show action buttons only for pending offers and candidates
                                if (currentOffer.status == OfferStatus.PENDING && !viewModel.isHrLoggedIn.value) {
                                    // Close Button
                                    Button(
                                        onClick = onDismiss,
                                        modifier = Modifier
                                            .weight(0.85f)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF6B7280)
                                        )
                                    ) {
                                        Text(
                                            text = "Close",
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            maxLines = 1
                                        )
                                    }

                                    // Decline Button with icon
                                    Button(
                                        onClick = onDecline,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFEF5350)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Decline",
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    // Accept Button with icon
                                    Button(
                                        onClick = onAccept,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Accept",
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                } else {
                                    // For accepted/declined offers or HR view
                                    Button(
                                        onClick = onDismiss,
                                        modifier = Modifier
                                            .widthIn(min = 120.dp)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF6B7280)
                                        )
                                    ) {
                                        Text(
                                            text = "Close",
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }

                                    Button(
                                        onClick = { generateAndDownloadPdf() },
                                        enabled = !isGeneratingPdf.value,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .animateContentSize(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF6366F1),
                                            disabledContainerColor = Color(0xFF6366F1).copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (isGeneratingPdf.value) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Generating...",
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.FileDownload,
                                                    contentDescription = "Download PDF",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Download PDF",
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Signed Offer Letter Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            // Signed offer upload section
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(20.dp)
                            ) {
                                // Update the onUploadSigned call in your dialog's signed tab section:

                                SignedOfferUploadSection(
                                    offer = currentOffer, // Use currentOffer instead of offer
                                    onUploadSigned = { uri ->
                                        scope.launch {
                                            isUploadingSigned.value = true
                                            try {
                                                // Call the upload function and handle the callback
                                                onUploadSigned(uri, currentOffer) { success, updatedOffer ->
                                                    isUploadingSigned.value = false
                                                    if (success && updatedOffer != null) {
                                                        // Update the current offer with the new signed document info
                                                        currentOffer = updatedOffer
                                                        Toast.makeText(context, "Signed offer letter uploaded successfully!", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "Upload failed. Please try again.", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                isUploadingSigned.value = false
                                                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onViewSigned = onViewSigned,
                                    isUploading = isUploadingSigned.value
                                )
                            }

                            // Action buttons for Signed tab - Only Close and Download
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    // Close Button
                                    Button(
                                        onClick = onDismiss,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF6B7280)
                                        )
                                    ) {
                                        Text(
                                            text = "Close",
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }

                                    // Download PDF Button
                                    Button(
                                        onClick = { generateAndDownloadPdf() },
                                        enabled = !isGeneratingPdf.value,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .animateContentSize(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF6366F1),
                                            disabledContainerColor = Color(0xFF6366F1).copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (isGeneratingPdf.value) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Generating...",
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.FileDownload,
                                                    contentDescription = "Download PDF",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Download PDF",
                                                    color = Color.White,
                                                    fontSize = 14.sp,
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
            }
        }
    }
}