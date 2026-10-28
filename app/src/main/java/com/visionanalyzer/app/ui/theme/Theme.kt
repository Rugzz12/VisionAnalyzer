package com.visionanalyzer.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = VisionAnalyzerPrimary,
    secondary = VisionAnalyzerAccent,
    tertiary = VisionAnalyzerWarning,
    background = VisionAnalyzerBackground,
    surface = VisionAnalyzerSurface,
    onPrimary = VisionAnalyzerText,
    onSecondary = VisionAnalyzerText,
    onBackground = VisionAnalyzerText,
    onSurface = VisionAnalyzerText,
)

private val LightColors = lightColorScheme(
    primary = VisionAnalyzerPrimary,
    secondary = VisionAnalyzerAccent,
    tertiary = VisionAnalyzerWarning,
    background = VisionAnalyzerBackground,
    surface = VisionAnalyzerSurface,
    onPrimary = VisionAnalyzerText,
    onSecondary = VisionAnalyzerText,
    onBackground = VisionAnalyzerText,
    onSurface = VisionAnalyzerText,
)

@Composable
fun VisionAnalyzerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
