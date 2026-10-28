package com.visionanalyzer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.visionanalyzer.app.ui.VisionAnalyzerScreen
import com.visionanalyzer.app.ui.theme.VisionAnalyzerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VisionAnalyzerTheme {
                VisionAnalyzerScreen()
            }
        }
    }
}
