package com.visionanalyzer.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.visionanalyzer.app.camera.CameraScanner
import com.visionanalyzer.app.data.AnalysisState
import com.visionanalyzer.app.data.AnalysisSummary
import com.visionanalyzer.app.data.TimelineEvent
import com.visionanalyzer.app.features.LocalAnalysisEngine
import com.visionanalyzer.app.sensor.PhoneSensorMonitor
import com.visionanalyzer.app.ui.theme.VisionAnalyzerAccent
import com.visionanalyzer.app.ui.theme.VisionAnalyzerBorder
import com.visionanalyzer.app.ui.theme.VisionAnalyzerDanger
import com.visionanalyzer.app.ui.theme.VisionAnalyzerElevated
import com.visionanalyzer.app.ui.theme.VisionAnalyzerMuted
import com.visionanalyzer.app.ui.theme.VisionAnalyzerPrimary
import com.visionanalyzer.app.ui.theme.VisionAnalyzerSurface
import com.visionanalyzer.app.ui.theme.VisionAnalyzerText
import com.visionanalyzer.app.ui.theme.VisionAnalyzerWarning
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VisionAnalyzerScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraScanner = remember { CameraScanner(context, lifecycleOwner) }
    val sensorMonitor = remember { PhoneSensorMonitor(context) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(cameraPermission.status) {
        hasCameraPermission = cameraPermission.status.isGranted
    }

    LaunchedEffect(Unit) {
        sensorMonitor.sensorFlow().collectLatest { }
    }

    val summaries = remember {
        LocalAnalysisEngine.compileSummary()
    }
    val timeline = remember { LocalAnalysisEngine.buildTimeline() }
    val sensor = remember { LocalAnalysisEngine.sensorSnapshot() }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(VisionAnalyzerSurface),
        color = VisionAnalyzerSurface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderCard(summaries)
            }

            item {
                CameraCard(cameraScanner, hasCameraPermission, cameraPermission) {
                    cameraPermission.launchPermissionRequest()
                }
            }

            item {
                MetricStrip(summaries)
            }

            items(summaries) { summary ->
                AnalysisCard(summary)
            }

            item {
                TimelineCard(timeline)
            }

            item {
                SensorCard(sensor)
            }
        }
    }
}

@Composable
private fun HeaderCard(summaries: List<AnalysisSummary>) {
    val dominant = summaries.maxByOrNull { it.confidence } ?: summaries.first()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = VisionAnalyzerElevated),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = VisionAnalyzerAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "VisionAnalyzer",
                    style = MaterialTheme.typography.headlineLarge,
                    color = VisionAnalyzerText,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = dominant.title,
                color = VisionAnalyzerMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(label = "Status", value = dominant.state.name)
                StatusChip(label = "Confidence", value = "${dominant.confidence}%")
            }
        }
    }
}

@Composable
private fun CameraCard(
    cameraScanner: CameraScanner,
    hasCameraPermission: Boolean,
    cameraPermission: com.google.accompanist.permissions.PermissionState,
    onGrantClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = VisionAnalyzerElevated),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = VisionAnalyzerPrimary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Live scanner",
                    style = MaterialTheme.typography.titleLarge,
                    color = VisionAnalyzerText,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .border(1.dp, VisionAnalyzerBorder, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            PreviewView(context).apply {
                                cameraScanner.bind(this)
                            }
                        }
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = VisionAnalyzerWarning)
                        Text("Camera permission required", color = VisionAnalyzerText)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("FPS 30", color = VisionAnalyzerMuted)
                Text("Zoom 1.0x", color = VisionAnalyzerMuted)
                Text("Torch OFF", color = VisionAnalyzerMuted)
            }
            if (!hasCameraPermission) {
                androidx.compose.material3.Button(
                    onClick = onGrantClick,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = VisionAnalyzerPrimary)
                ) {
                    Text("Grant camera access")
                }
            }
        }
    }
}

@Composable
private fun MetricStrip(summaries: List<AnalysisSummary>) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        summaries.take(3).forEach { summary ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = VisionAnalyzerElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(summary.title, color = VisionAnalyzerText, fontWeight = FontWeight.SemiBold)
                    Text("${summary.confidence}% confidence", color = VisionAnalyzerMuted)
                }
            }
        }
    }
}

@Composable
private fun AnalysisCard(summary: AnalysisSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = VisionAnalyzerElevated),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = statusColor(summary.state)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(summary.title, color = VisionAnalyzerText, fontWeight = FontWeight.Bold)
            }
            Text(summary.summary, color = VisionAnalyzerMuted)
            summary.details.forEach {
                Text("• $it", color = VisionAnalyzerText)
            }
            summary.evidence.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.label, color = VisionAnalyzerMuted)
                    Text("${item.confidence}%", color = statusColorForConfidence(item.confidence))
                }
            }
        }
    }
}

@Composable
private fun TimelineCard(events: List<TimelineEvent>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = VisionAnalyzerElevated),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Evidence timeline", color = VisionAnalyzerText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            events.forEach { event ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${event.timestamp} • ${event.tag}", color = VisionAnalyzerPrimary)
                    Text(event.title, color = VisionAnalyzerText, fontWeight = FontWeight.SemiBold)
                    Text(event.description, color = VisionAnalyzerMuted)
                }
            }
        }
    }
}

@Composable
private fun SensorCard(sensor: com.visionanalyzer.app.data.SensorSnapshot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = VisionAnalyzerElevated),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Audio and sensor analysis", color = VisionAnalyzerText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Accelerometer: ${sensor.accelerometer}", color = VisionAnalyzerMuted)
            Text("Gyroscope: ${sensor.gyroscope}", color = VisionAnalyzerMuted)
            Text("Light: ${sensor.light}", color = VisionAnalyzerMuted)
            Text("Motion risk: ${sensor.motion.name}", color = statusColor(sensor.motion))
        }
    }
}

@Composable
private fun StatusChip(label: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VisionAnalyzerPrimary.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(50.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = VisionAnalyzerMuted)
            Spacer(modifier = Modifier.size(6.dp))
            Text(value, color = VisionAnalyzerText, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun statusColor(state: AnalysisState): androidx.compose.ui.graphics.Color = when (state) {
    AnalysisState.NORMAL -> VisionAnalyzerAccent
    AnalysisState.CHANGE -> VisionAnalyzerWarning
    AnalysisState.UNKNOWN -> VisionAnalyzerMuted
    AnalysisState.LOW -> VisionAnalyzerPrimary
    AnalysisState.MEDIUM -> VisionAnalyzerWarning
    AnalysisState.HIGH -> VisionAnalyzerDanger
}

private fun statusColorForConfidence(confidence: Int): androidx.compose.ui.graphics.Color = when {
    confidence >= 80 -> VisionAnalyzerAccent
    confidence >= 60 -> VisionAnalyzerWarning
    else -> VisionAnalyzerDanger
}

