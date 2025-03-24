package com.thriber.thriverx

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.textclassifier.TextSelection
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.storage.StorageReference
import com.thriber.thriverx.FirebaseClass.DataInterface.DataInterface
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import com.yalantis.ucrop.UCrop
import io.grpc.okhttp.OkHttpServerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import java.util.UUID


class ImageCapturePreview : AppCompatActivity() {

        val imageView : ImageView by lazy{ findViewById(R.id.previewImage)}
        private var croppedImageUri: Uri? = null
        private var originalBitmap: Bitmap? = null
        private var processedBitmap: Bitmap? = null
        private var isGrayscaled = false
        private var patientId = ""
        private var progressDialog: ProgressDialog? = null
         val firebaseDao=FirebaseDao()

        @RequiresApi(Build.VERSION_CODES.P)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_image_capture_preview)
                val saveButton: Button by lazy { findViewById(R.id.save) }
                val scanButton: Button = findViewById(R.id.scan)
                val imageUriString = intent.getStringExtra("IMAGE_URI")
                val imageUri = Uri.parse(imageUriString)
            val firebaseAnalytics = Firebase.analytics
                imageView.setImageURI(imageUri)
                val intent = intent
            val shrink_state= AnimationUtils.loadAnimation(this, R.anim.shrink)
            val normal_state = AnimationUtils.loadAnimation(this, R.anim.normal)

             patientId = intent.getStringExtra("PATIENT_ID")?:""

            if (intent != null && intent.hasExtra("IMAGE_URI")) {

                startUCrop(imageUri,patientId)
                // Now you have the URI in the second activity, you can use it as needed.
            }
            if (intent == null || !intent.hasExtra("IMAGE_URI")) {
                Toast.makeText(this ,"error occured", Toast.LENGTH_SHORT).show()
                finish()
            }


            scanButton.setOnClickListener {
                scanButton.startAnimation(shrink_state)
                applyGrayscaleFilter()
                scanButton.startAnimation(normal_state)
            }

            // Call the function to start uCrop


            saveButton.setOnClickListener {
                saveButton.startAnimation(shrink_state)
                showProgressDialog()
                Imageupload(patientId)
                val currenttimestamp=System.currentTimeMillis()
                val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
                val formattedTime = formatter.format(Instant.ofEpochMilli(currenttimestamp))

                val bundle = Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_ID, patientId)
                    putString("button_click", formattedTime)
                }
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
                saveButton.startAnimation(normal_state)

            }


        }

        private fun applyGrayscaleFilter() {
            if (croppedImageUri != null) {
                if (originalBitmap == null) {
                    originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(croppedImageUri!!))
                }

                val rotatedBitmap = correctImageOrientation(originalBitmap!!, croppedImageUri!!)
                processedBitmap = if (isGrayscaled) {
                    // If already grayscaled, revert to the original color
                    originalBitmap
                } else {
                    // If not grayscaled, apply grayscale filter
                    applyColorFilter(rotatedBitmap, ColorMatrix().apply { setSaturation(0f) })
                }



                // Recycle bitmaps to avoid memory leaks
                isGrayscaled = !isGrayscaled

                if (isGrayscaled) {
                    imageView.setImageBitmap(processedBitmap)

                    findViewById<Button>(R.id.scan).text = "Reverse Grayscale"
                } else {
                    imageView.setImageURI(croppedImageUri)

                    findViewById<Button>(R.id.scan).text = "Grayscale"
                }

                rotatedBitmap.recycle()

            } else {
                Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show()
            }
        }

        private fun applyColorFilter(bitmap: Bitmap, colorMatrix: ColorMatrix): Bitmap {
            val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(mutableBitmap, 0f, 0f, paint)
            return mutableBitmap
        }



    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }



    @RequiresApi(Build.VERSION_CODES.P)
    fun Imageupload(patientId: String) {

        if(!isNetworkAvailable(this)){
            progressDialog?.dismiss()
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            Log.e("ImageUpload", "No internet connection available")
            return
        }

        val storageReference = firebaseDao.getStorageReference()
        val imageRef = storageReference.child("$patientId/${UUID.randomUUID()}.jpg")

        // Choose the URI to upload (either croppedImageUri or processedUri)
        val uploadUri = when {
            !isGrayscaled && croppedImageUri != null -> croppedImageUri
            isGrayscaled && processedBitmap != null -> saveBitmapToFile(processedBitmap!!, patientId)
            else -> null
        }



        // Check if there's a valid image URI to upload
        uploadUri?.let { uri ->
            val uploadTask = imageRef.putFile(uri)
            progressDialog?.show()
            // Handler and timeout setup
            val handler = Handler(Looper.getMainLooper()) // use executer in place of looper
            val timeoutRunnable = Runnable {
                progressDialog?.dismiss()
                Toast.makeText(this, "Upload timed out, proceeding to next screen", Toast.LENGTH_SHORT).show()

                val galleryIntent = Intent(this@ImageCapturePreview, ImageGalleryActivity::class.java).apply {
                    putExtra("itemId", patientId)
                }
                try {
                    startActivity(galleryIntent)
                    finish() // Finish the current activity after starting the next one
                } catch (e: Exception) {
                    Log.e("ImageCapturePreview", "Error starting activity: ${e.message}")
                }
            }


            // Schedule the timeout
            handler.postDelayed(timeoutRunnable, 30000)
            // Handle the upload process
            uploadTask.addOnSuccessListener {
                handler.removeCallbacks(timeoutRunnable)
                progressDialog?.dismiss()
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()


               // storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                 //   val downloadUrl = downloadUri.toString()
                CoroutineScope(Dispatchers.IO).launch {

                    val response = sendMessageToServer(patientId, imageRef)
                    Log.d("ServerResponse", "Response from server: $response")
                }


                val currenttimestamp=System.currentTimeMillis()
                val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
                val formattedTime = formatter.format(Instant.ofEpochMilli(currenttimestamp))

                val bundle = Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_ID, patientId)
                    putString("photo_upload", formattedTime)
                }
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)

                val galleryIntent = Intent(this@ImageCapturePreview, ImageGalleryActivity::class.java).apply {
                    putExtra("itemId", patientId)
                }

                try {
                    startActivity(galleryIntent)
                    finish() // Finish the current activity after starting the next one
                } catch (e: Exception) {
                    Log.e("ImageCapturePreview", "Error starting activity: ${e.message}")
                }
            }.addOnFailureListener { e ->
                handler.removeCallbacks(timeoutRunnable)
                progressDialog?.dismiss()
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ImageUpload", "Failed to upload image", e)
            }

        } ?: run {
            // Handle case when there's no valid image to upload
            progressDialog?.dismiss()
            Toast.makeText(this, "No image to upload", Toast.LENGTH_SHORT).show()
            Log.e("ImageUpload", "No image to upload")
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, patientId: String): Uri? {
            try {
                val outputStream = FileOutputStream(File(croppedImageUri?.path!!))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
                outputStream.close()
                return croppedImageUri
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }


        private fun correctImageOrientation(bitmap: Bitmap, imageUri: Uri): Bitmap {
            val exif = ExifInterface(imageUri.path!!)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            val copybitmap=bitmap.copy(bitmap.config,true)
            return Bitmap.createBitmap(copybitmap, 0, 0, copybitmap.width, copybitmap.height, matrix, true)
        }

        private fun showProgressDialog() {
            progressDialog = ProgressDialog(this)
            progressDialog?.setMessage("Uploading image...")
            progressDialog?.setCancelable(false)
            progressDialog?.show()
        }

        private fun hideProgressDialog() {
            progressDialog?.dismiss()
        }

        private fun getOutputDirectory(patientId:String): File {

            val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile ->
                File(mFile, resources.getString(R.string.app_name)).apply {
                    mkdirs()
                }
            }
            return File(mediaDir, "Patient_$patientId").apply {
                mkdirs()
            }
        }

        private fun startUCrop(sourceUri: Uri, patientId: String) : Uri ? {
            val destinationUri = Uri.fromFile(File(getOutputDirectory(patientId), "${System.currentTimeMillis()}croppedImage.jpg"))

            val options = UCrop.Options()

        options.setSharpnessEnabled(false)

            UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this)

            return destinationUri

        }



        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
                val resultUri = UCrop.getOutput(data!!)
                imageView.setImageURI(resultUri)
                croppedImageUri = resultUri
            }
            else if (resultCode == RESULT_CANCELED) {

                    val galleryIntent = Intent(this@ImageCapturePreview,MainActivity::class.java).apply {
                        putExtra("itemId", patientId)
                    }
                    try {
                        startActivity(galleryIntent)
                        finish() // Finish the current activity after starting the next one
                    } catch (e: Exception) {
                        Log.e("ImageCapturePreview", "Error starting activity: ${e.message}")
                    }

                }
            }
    private val number:DataInterface=FirebaseDao()
    suspend fun getPhonenumber(patientId: String):String{

        val patientdata= number.patinetDetails(patientId)
        if (patientdata != null) {
            // Get the phone number from the data Map
             val phone=patientdata["phoneNumber"] as String
            return if (phone.startsWith("+91")) phone else "+91$phone"
        }
        else{
            return "error occurred"//have to make changes
        }
    }
    suspend fun getDownloadUrl( imageRef: StorageReference): String? {
        return try {
            val downloadUri =  imageRef.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Failed to get download URL: ${e.message}")
            null
        }
    }
    private val client = OkHttpClient()
    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun sendMessageToServer(patientId: String,  imageRef: StorageReference): String {
        return withContext(Dispatchers.IO) {
            val phoneNumber = getPhonenumber(patientId)
            if (phoneNumber == "error occurred") {
                return@withContext "Failed to retrieve phone number"
            }

            val downloadUri = getDownloadUrl(imageRef)
            if (downloadUri == null) {
                return@withContext "Failed to retrieve download URL"
            }

            val url = "https://tx-sms-service.onrender.com/send-sms"

            val jsonObject = JSONObject().apply {
                put("to", phoneNumber)
                put("message", downloadUri)


            }

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            return@withContext try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string() ?: "Response body is null"
                    } else {
                        "Request failed with code: ${response.code}"
                    }
                }
            } catch (e: IOException) {
                "Request failed: ${e.message}"
            }
        }
    }


}
