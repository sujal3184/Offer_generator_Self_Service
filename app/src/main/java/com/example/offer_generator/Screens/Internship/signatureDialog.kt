package com.example.offer_generator.Screens.Internship

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import android.util.Base64


// Modified SignatureDialog to save signature data
@Composable
fun SignatureDialog(
    onSave: (String) -> Unit, // This will now receive the actual signature data
    onDismiss: () -> Unit
) {
    var signaturePaths by remember { mutableStateOf(mutableListOf<Path>()) }
    var currentPath by remember { mutableStateOf(Path()) }

    // Function to convert signature to bitmap and then to Base64
    fun convertSignatureToBase64(): String {
        // Create a bitmap from the signature paths
        val bitmap = android.graphics.Bitmap.createBitmap(800, 400, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 8f
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
        }

        // Convert Compose Paths to Android Paths and draw
        signaturePaths.forEach { path ->
            val androidPath = path.asAndroidPath()
            canvas.drawPath(androidPath, paint)
        }

        // Convert to Base64
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        title = {
            Text(
                text = "Digital Signature Required",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column {
                Text(
                    text = "Please provide your digital signature to accept this offer letter:",
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Signature Canvas (same as before)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPath = Path().apply {
                                            moveTo(offset.x, offset.y)
                                        }
                                    },
                                    onDragEnd = {
                                        signaturePaths = signaturePaths.toMutableList().apply {
                                            add(currentPath)
                                        }
                                    }
                                ) { change, _ ->
                                    currentPath.lineTo(change.position.x, change.position.y)
                                    signaturePaths = signaturePaths.toMutableList()
                                }
                            }
                    ) {
                        signaturePaths.forEach { path ->
                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        drawPath(
                            path = currentPath,
                            color = Color.Black,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Text(
                    text = "Sign above to proceed",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        signaturePaths = mutableListOf()
                        currentPath = Path()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Discard", fontWeight = FontWeight.Bold, color = Color.Red)
                }

                TextButton(
                    onClick = {
                        if (signaturePaths.isNotEmpty()) {
                            val signatureBase64 = convertSignatureToBase64()
                            onSave(signatureBase64) // Pass actual signature data
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4CAF50)),
                    enabled = signaturePaths.isNotEmpty() // Only enable if signature exists
                ) {
                    Text("Save", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}


// Compact signature display component for dialog
@Composable
fun SignatureDisplayInDialog(
    signatureData: String,
    modifier: Modifier = Modifier
) {
        val decodedBytes = Base64.decode(signatureData, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        if (bitmap != null) {
            Box(
                modifier = modifier
                    .height(80.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(4.dp)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Digital Signature",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            SignatureErrorDisplayInDialog()
        }
}

@Composable
private fun SignatureErrorDisplayInDialog() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(
                width = 1.dp,
                color = Color.Red.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error loading signature",
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall
        )
    }
}