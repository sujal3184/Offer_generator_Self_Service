package com.example.offer_generator.Screens.OfferLetters

import OfferLetter
import android.Manifest
import android.content.ContentValues
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
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OfferLetterDetailDialog(
    viewModel: WhoLoginViewModel,
    offer: OfferLetter,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    val context = LocalContext.current
    val isGeneratingPdf = remember { mutableStateOf(false) }

    // State to hold the current offer (will be updated when document is uploaded)
    var currentOffer by remember { mutableStateOf(offer) }

    // Tab state - 0 for Unsigned, 1 for Signed
    val selectedTabIndex = remember { mutableStateOf(0) }


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
        template = currentOffer.templateType, // Use the selected template
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
        generatedBy = currentOffer.generatedBy,
        customTemplateId = currentOffer.customTemplateId, // Pass custom template ID
        customTemplateContent = currentOffer.customTemplateContent  // Pass custom template content
    )


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
                        .height(80.dp)
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
                        // Signed Offer Letter
                        SignedOfferLetterScreen(viewModel,currentOffer,onDismiss)
                    }
                }
            }
        }
    }
}