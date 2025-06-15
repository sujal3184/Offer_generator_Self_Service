package com.example.offer_generator.Screens.Internship

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.offer_generator.R
import java.io.File

// Add this composable for CV viewing
@Composable
fun CVViewerDialog(
    cvUri: Uri?,
    cvFilePath: String,
    cvFileName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorResource(id = R.color.purple),
                                    colorResource(id = R.color.blue)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "CV Viewer",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                cvFileName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row {
                            // Open externally button
                            IconButton(
                                onClick = {
                                    openCVExternally(context, cvUri, cvFilePath)
                                }
                            ) {
                                Icon(
                                    Icons.Default.OpenInNew,
                                    contentDescription = "Open Externally",
                                    tint = Color.White
                                )
                            }

                            // Close button
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // CV Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CVContent(
                        cvUri = cvUri,
                        cvFilePath = cvFilePath,
                        cvFileName = cvFileName,
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
fun CVContent(
    cvUri: Uri?,
    cvFilePath: String,
    cvFileName: String,
    context: Context
) {
    val fileExtension = cvFileName.substringAfterLast(".", "").lowercase()

    when (fileExtension) {
        "pdf" -> {
            PDFViewer(cvUri, cvFilePath, context)
        }
        "doc", "docx" -> {
            DocumentViewer(cvUri, cvFilePath, cvFileName, context)
        }
        "jpg", "jpeg", "png" -> {
            ImageViewer(cvUri, cvFilePath, context)
        }
        else -> {
            UnsupportedFileViewer(cvFileName, context, cvUri, cvFilePath)
        }
    }
}

@Composable
fun PDFViewer(cvUri: Uri?, cvFilePath: String, context: Context) {
    var webViewError by remember { mutableStateOf(false) }

    if (webViewError) {
        FileNotSupportedCard(
            message = "PDF viewing not available",
            subtitle = "Please open externally to view the PDF",
            onOpenExternally = { openCVExternally(context, cvUri, cvFilePath) }
        )
    } else {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            webViewError = true
                        }
                    }
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { webView ->
            try {
                val uri = cvUri ?: Uri.fromFile(File(cvFilePath))
                val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=${uri}"
                webView.loadUrl(googleDocsUrl)
            } catch (e: Exception) {
                webViewError = true
            }
        }
    }
}

@Composable
fun DocumentViewer(cvUri: Uri?, cvFilePath: String, cvFileName: String, context: Context) {
    FileNotSupportedCard(
        message = "Document Preview",
        subtitle = "Word documents require external app to view",
        onOpenExternally = { openCVExternally(context, cvUri, cvFilePath) }
    )
}

@Composable
fun ImageViewer(cvUri: Uri?, cvFilePath: String, context: Context) {
    // For image files, you can use AsyncImage from Coil library
    // For now, showing a placeholder that opens externally
    FileNotSupportedCard(
        message = "Image CV",
        subtitle = "Tap to view in gallery app",
        onOpenExternally = { openCVExternally(context, cvUri, cvFilePath) }
    )
}

@Composable
fun UnsupportedFileViewer(
    cvFileName: String,
    context: Context,
    cvUri: Uri?,
    cvFilePath: String
) {
    FileNotSupportedCard(
        message = "Unsupported File Format",
        subtitle = "Open with external app to view $cvFileName",
        onOpenExternally = { openCVExternally(context, cvUri, cvFilePath) }
    )
}

@Composable
fun FileNotSupportedCard(
    message: String,
    subtitle: String,
    onOpenExternally: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onOpenExternally() },
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = colorResource(id = R.color.purple)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onOpenExternally,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.purple)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Externally")
            }
        }
    }
}

// Helper function to open CV externally
fun openCVExternally(context: Context, cvUri: Uri?, cvFilePath: String) {
    try {
        val uri = cvUri ?: Uri.fromFile(File(cvFilePath))
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, getMimeType(cvFilePath))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Check if there's an app that can handle this intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // If no app can handle it, try with a generic intent
            val genericIntent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(genericIntent, "Open CV with"))
        }
    } catch (e: Exception) {
        // Handle error - maybe show a toast
        e.printStackTrace()
    }
}

// Helper function to get MIME type
fun getMimeType(filePath: String): String {
    val extension = filePath.substringAfterLast(".", "").lowercase()
    return when (extension) {
        "pdf" -> "application/pdf"
        "doc" -> "application/msword"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        else -> "*/*"
    }
}