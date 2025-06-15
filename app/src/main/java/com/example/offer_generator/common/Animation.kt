package com.example.offer_generator.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.offer_generator.R
import kotlinx.coroutines.delay

@Composable
fun AnimatedNavigationItem(
    text: String,
    delay: Long,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
    ) {
        NavigationDrawerItem(
            label = { Text(text, color = Color.Black) },
            selected = false,
            onClick = onClick
        )
    }
}

@Composable
fun AnimatedButton(
    onclick: () -> Unit,
    text: String,
    delay: Long,
    filled: Boolean,
    fontsize : TextUnit = 16.sp,
    color: Color = Color.White
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
    ) {
        TextButton(
            onClick = { onclick()},
            colors = ButtonDefaults.textButtonColors(
                containerColor = if (filled) {
                    if(color != Color.White) {
                        color
                    }
                    else{
                        colorResource(id = R.color.purple)
                    }
                } else Color.White,
                contentColor = if (filled) Color.White else colorResource(id = R.color.purple),
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, if(color != Color.White) color else colorResource(id = R.color.purple)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .height(45.dp)
        ) {
            Text(text, fontSize = fontsize, fontWeight = if (filled) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun AnimatedInteractiveButton(
    onclick: () -> Unit,
    text: String,
    filled: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    TextButton(
        onClick = {onclick()},
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (filled) colorResource(id = R.color.purple) else Color.White,
            contentColor = if (filled) Color.White else colorResource(id = R.color.purple),
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (!filled) BorderStroke(2.dp, colorResource(id = R.color.purple)) else null,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = !isPressed
            }
    ) {
        Text(text)
    }
}

@Composable
fun FloatingCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 45.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = colorResource(id = R.color.purple).copy(alpha = 0.9f),
                spotColor = colorResource(id = R.color.purple).copy(alpha = 0.8f)
            ),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.Lightpurple),
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun AnimatedFeatureRow(text: String, delay: Long) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
    ) {
        Row {
            // Animated check icon with rotation
            val rotation by rememberInfiniteTransition(label = "check").animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )
            Spacer(Modifier.width(24.dp))
            Text(text)
        }
    }
}

@Composable
fun AnimatedDomainCard(
    title: String,
    items: List<String>,
    delay: Long,
    isLast: Boolean = false
) {
    var isVisible by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isHovered) 16f else 12f,
        animationSpec = spring(),
        label = "elevation"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(800))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 44.dp,
                    end = 44.dp,
                    bottom = if (isLast) 32.dp else 0.dp
                )
                .scale(scale)
                .shadow(
                    elevation = elevation.dp,
                    shape = RoundedCornerShape(36.dp),
                    ambientColor = if(isHovered) colorResource(id = R.color.purple).copy(alpha = 0.9f) else colorResource(id = R.color.blue).copy(alpha = 0.9f),
                    spotColor = if(isHovered) colorResource(id = R.color.purple).copy(alpha = 0.9f) else colorResource(id = R.color.blue).copy(alpha = 0.9f)
                )
                .border(
                    width = 0.1.dp,
                    color = colorResource(id = R.color.purple),
                    shape = RoundedCornerShape(36.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isHovered = !isHovered
                },
            shape = RoundedCornerShape(36.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.white),
                contentColor = Color.White
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
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(24.dp))

                    items.forEachIndexed { index, item ->
                        AnimatedFeatureItem(item, index * 100L)
                        if (index < items.size - 1) {
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedFeatureItem(text: String, delay: Long) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
    ) {
        Row {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(Modifier.width(24.dp))
            Text(text, color = Color.Black)
        }
    }
}

