package com.example.mangashelf.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mangashelf.R
import org.w3c.dom.Text

// Set of Material typography styles to start with
val publicaSansFontFamily = FontFamily(
    Font(R.font.publica_sans_light)
)
val lastShurikenFontFamily = FontFamily(
    Font(R.font.the_last_shuriken)
)
val urbaneRoughFontFamily = FontFamily(
    Font(R.font.urbane_rough_medium)
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = lastShurikenFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = lastShurikenFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = lastShurikenFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    titleMedium = TextStyle(
        fontFamily = urbaneRoughFontFamily,
        fontSize = 20.sp,
        color = Color(0xFFf8f8f8)
    ),
    titleSmall = TextStyle(
        fontFamily = publicaSansFontFamily,
        fontSize = 14.sp,
        color = Color(0xFFc1c1c1)
    )

)