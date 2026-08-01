package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import java.util.Calendar
import com.example.R

@Composable
fun TimeBasedBackground() {
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    
    val drawableRes = when (hour) {
        in 5..11 -> R.drawable.bg_morning_1785590614823 // Morning
        in 12..15 -> R.drawable.bg_afternoon_1785590629985 // Afternoon
        in 16..18 -> R.drawable.bg_evening_1785590646398 // Evening
        else -> R.drawable.bg_night_1785590664964 // Night
    }

    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = "Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
