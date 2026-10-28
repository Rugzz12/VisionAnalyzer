package com.visionanalyzer.app.data

enum class AnalysisState {
    NORMAL,
    CHANGE,
    UNKNOWN,
    LOW,
    MEDIUM,
    HIGH
}

enum class SeverityLevel {
    LOW,
    MEDIUM,
    HIGH
}

data class EvidenceItem(
    val label: String,
    val value: String,
    val confidence: Int,
    val source: String? = null
)

data class TimelineEvent(
    val tag: String,
    val title: String,
    val description: String,
    val source: String? = null,
    val timestamp: String
)

data class AnalysisSummary(
    val title: String,
    val state: AnalysisState,
    val confidence: Int,
    val summary: String,
    val details: List<String>,
    val evidence: List<EvidenceItem>,
    val sources: List<String>
)

data class SensorSnapshot(
    val accelerometer: String,
    val gyroscope: String,
    val light: String,
    val motion: AnalysisState
)

data class CameraMetrics(
    val fps: Int,
    val zoom: Float,
    val torchEnabled: Boolean,
    val autofocus: Boolean,
    val focusStatus: String,
    val exposure: String,
)
