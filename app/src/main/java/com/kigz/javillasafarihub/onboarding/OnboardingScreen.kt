package com.kigz.javillasafarihub.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme
import com.kigz.javillasafarihub.ui.theme.SavannahGold

@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
) {
    val language = LocalLanguageManager.current.currentLanguage
    Box(modifier = Modifier.fillMaxSize()) {
        // Giraffe pattern background
        Image(
            painter = painterResource(id = com.kigz.javillasafarihub.R.drawable.safari),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Balanced gradient overlay for pattern visibility and text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = translate("Welcome to Javilla SafariHub", language),
                style = MaterialTheme.typography.headlineLarge,
                color = SavannahGold,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = translate("Discover destinations, activities, transport and useful travel tools across Kenya.", language),
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(translate("Get Started", language), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    JavillaSafariHubTheme {
        OnboardingScreen(onContinue = {})
    }
}
