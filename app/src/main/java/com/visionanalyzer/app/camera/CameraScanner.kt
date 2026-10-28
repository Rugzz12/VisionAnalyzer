package com.visionanalyzer.app.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraScanner(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private val tag = "CameraScanner"
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    fun bind(previewView: PreviewView, enableTorch: Boolean = false) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            try {
                val provider = providerFuture.get()
                cameraProvider = provider

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(executor) { imageProxy ->
                            // local sampling / analysis hook for scanner pipeline
                            imageProxy.close()
                        }
                    }

                val selector = CameraSelector.DEFAULT_BACK_CAMERA
                provider.unbindAll()
                camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )

                camera?.cameraControl?.enableTorch(enableTorch)
            } catch (e: Exception) {
                Log.e(tag, "Camera bind failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun enableTorch(enabled: Boolean) {
        camera?.cameraControl?.enableTorch(enabled)
    }

    fun release() {
        cameraProvider?.unbindAll()
        executor.shutdown()
    }
}
