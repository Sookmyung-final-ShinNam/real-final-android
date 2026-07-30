package com.veryshinnam.myapp.common.component

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import com.veryshinnam.myapp.R

@Composable
fun InstructionText(
    text: String,
    textStyle: TextStyle,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier
) {
    StrokeTitle(
        titleText = text,
        textAlign = textAlign,
        titleColor = colorResource(R.color.main_orange),
        strokeColor = Color.White,
        strokeWidth = 8f,
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        colorResource(R.color.lemon_yellow).copy(0.8f),
                        colorResource(R.color.lemon_yellow),
                        colorResource(R.color.lemon_yellow).copy(0.8f),
                        Color.Transparent
                    )
                )
            ),
        titleTextStyle  = textStyle.copy(
            fontWeight = SemiBold,
            lineHeight =  textStyle.lineHeight * 1.1f
        ),
    )
}