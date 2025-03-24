package com.thriber.thriverx

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.thriber.thriverx.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var camera: Camera
    private lateinit var outputDirectory: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val patientId = intent.getStringExtra("itemId")?:""
        val shrinkFade = AnimationUtils.loadAnimation(this, R.anim.shrink)
        val normal = AnimationUtils.loadAnimation(this, R.anim.normal)
        outputDirectory = getOutputDirecotry(patientId)

        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, constants.REQUIRED_PERMISSION, constants.REQUEST_CODE_PERMMISION
            )
        }
            binding.btnTakePhoto.setOnClickListener {
                binding.btnTakePhoto.startAnimation(shrinkFade)
                takePhoto(patientId)

                binding.btnTakePhoto.startAnimation(normal)

            }
        binding.flashButton.setOnClickListener{
            toggleFlash()
        }

    }

    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private fun toggleFlash() {
        flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            ImageCapture.FLASH_MODE_ON
        } else {
            ImageCapture.FLASH_MODE_OFF
        }
        updateFlashButtonIcon()
    }

    private fun updateFlashButtonIcon() {
        val flashButton = binding.flashButton
        val icon = if (flashMode == ImageCapture.FLASH_MODE_ON) {
            ContextCompat.getDrawable(this, R.drawable.ic_flash_on)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_flash_off)
        }
        flashButton.setImageDrawable(icon)
    }


    fun getOutputDirecotry(patientId:String): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply {
                mkdir()
            }
        }
        val patient = File(mediaDir,"patient").apply {
            mkdir()
        }

            val patientDir = File(patient, ".Patient_$patientId").apply {
                mkdirs()

        }

        File(patientDir, ".nomedia").apply {
            if (!exists()) {
                createNewFile()
            }
        }
        return patientDir
    }

    fun takePhoto(patientId: String) {

        val imageCapture = imageCapture ?: return
        val outputDirectory = getOutputDirecotry(patientId)
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                constants.FIle_NAME_FORMAT,
                Locale.getDefault()
            ).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.flashMode = flashMode

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    val savedUri = outputFileResults.savedUri
                    Toast.makeText(this@MainActivity, "PHOTO SAVED", Toast.LENGTH_SHORT).show()

                    val intent = Intent(
                        this@MainActivity,
                        ImageCapturePreview::class.java
                    )
                    intent.putExtra("IMAGE_URI", savedUri.toString())
                    intent.putExtra("PATIENT_ID", patientId)
                    startActivity(intent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(constants.TAG, "onError:${exception.message}", exception)
                }
            }
        )
    }


    fun startCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()

                .also { mPreview ->
                    mPreview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

             imageCapture = ImageCapture.Builder().build()
//                imageCapture.flashMode = flashMode

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {

                cameraProvider.unbindAll()

                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                pinchToZoom()
            } catch (e: Exception) {
                Log.d(constants.TAG, "start Camera Fail:", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun pinchToZoom(){
        val scalefactorThreshold=0.1f
        val listener=object :ScaleGestureDetector.SimpleOnScaleGestureListener(){

            override fun onScale(detector: ScaleGestureDetector): Boolean {

                val currentZoomRatio= camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                val delta=detector.scaleFactor

                if(Math.abs(delta-1f)<scalefactorThreshold) return false

                val newZoomRatio = currentZoomRatio * delta
                val clampedZoomRatio = newZoomRatio.coerceIn(1f,5f)

                camera.cameraControl.setZoomRatio(clampedZoomRatio)
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(this,listener)
        binding.viewFinder.setOnTouchListener { view, event ->
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) {

                val factory = binding.viewFinder.meteringPointFactory
                val point = factory.createPoint(event.x, event.y)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .setAutoCancelDuration(2, TimeUnit.SECONDS).build()
                val x = event.x
                val y = event.y
                val focusCircel = RectF(x - 50, y - 50, x + 50, y + 50)

                binding.focusCircleView.focusCircle = focusCircel
                binding.focusCircleView.invalidate()

                camera.cameraControl.startFocusAndMetering(action)

                view.performClick()
            }
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == constants.REQUEST_CODE_PERMMISION) {

            if (allPermissionGranted()) {


                startCamera()
                toggleFlash()
            } else {
                Toast.makeText(
                    this, "Permission is not granted by the user",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    }
    fun allPermissionGranted() =
        constants.REQUIRED_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
}