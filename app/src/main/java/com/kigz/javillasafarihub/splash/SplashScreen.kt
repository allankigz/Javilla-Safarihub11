package com.kigz.javillasafarihub.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.ui.theme.ForestGreen
import kotlinx.coroutines.delay
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

@Composable
fun SplashScreen(
    onNavigate: () -> Unit
) {
    val language = LocalLanguageManager.current.currentLanguage
    LaunchedEffect(Unit) {
        delay(2500)
        onNavigate()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Iconic Savannah landscape (Acacia tree at sunset)
        Image(
            painter = painterResource(id = com.kigz.javillasafarihub.R.drawable.splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Balanced gradient overlay for visibility and readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 300.dp)
        ) {
            Text(
                text = translate("Javilla SafariHub", language),
                style = MaterialTheme.typography.headlineLarge,
                color = ForestGreen,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 38.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = translate("Majestically Discover Kenya", language),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    JavillaSafariHubTheme {
        SplashScreen(onNavigate = {})
    }
}
