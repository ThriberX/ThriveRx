    package com.thriber.thriverx

    import android.annotation.SuppressLint
    import android.content.Context
    import android.content.Intent
    import android.graphics.Bitmap
    import android.graphics.drawable.Drawable
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.view.ViewGroup
    import android.view.animation.AnimationUtils
    import android.widget.Button
    import android.widget.ImageView
    import android.widget.LinearLayout
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import androidx.recyclerview.widget.GridLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.bumptech.glide.load.DataSource
    import com.bumptech.glide.load.engine.DiskCacheStrategy
    import com.bumptech.glide.load.engine.GlideException
    import com.bumptech.glide.request.RequestListener
    import com.google.firebase.storage.StorageReference
    import com.thriber.thriverx.FirebaseClass.DataInterface.DataInterface
    import com.thriber.thriverx.FirebaseClass.FirebaseDao
    import kotlinx.coroutines.launch
    import java.io.File
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.Locale

        class ImageGalleryActivity : AppCompatActivity() {

            private lateinit var imageAdapter: ImageAdapter
            var LocalimageUris = mutableSetOf<Pair<Uri,Long>>()
            var patientId=""
            @SuppressLint("MissingInflatedId")
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                setContentView(R.layout.activity_image_gallery)

                // here we get the patient_d with the help of intent
                 patientId = intent.getStringExtra("itemId" )?:""

                // this is the refresh button used to download any new image and used to build display of images
                val refreshBtn: Button=findViewById(R.id.Btn_Refresh)

                // this is the view used to display the images using layout
                val recyclerView = findViewById<RecyclerView>(R.id.image_recycler_view)

                val shrink_state= AnimationUtils.loadAnimation(this, R.anim.shrink)
                val normal_state = AnimationUtils.loadAnimation(this, R.anim.normal)

                imageAdapter = ImageAdapter(this)
                recyclerView.adapter = imageAdapter
                recyclerView.layoutManager = GridLayoutManager(this, 2) // Grid layout with 2 columns


                retrieveImagesFromStorage(patientId)
                displayImages()
                val addDocument=findViewById<Button>(R.id.add_document)
                addDocument.setOnClickListener {
                    addDocument.startAnimation(shrink_state)
                    val intent = Intent(this@ImageGalleryActivity, MainActivity::class.java)
                    intent.putExtra("itemId", patientId)
                    startActivity(intent)
                    finish()
                    addDocument.startAnimation(normal_state)
                }
                refreshBtn.setOnClickListener {
                    refreshBtn.startAnimation(shrink_state)
                    refresh()
                    refreshBtn.startAnimation(normal_state)
                }

               patientDetails(patientId)
            }

            fun patientDetails(patientId: String){
                val details:DataInterface=FirebaseDao()
                lifecycleScope.launch {
                    val patientdocument= details.patinetDetails(patientId)

                    if (patientdocument!=null ) {


                        val patientName = patientdocument["name"] as? String ?: "N/A"
                        val patientGender = patientdocument["gender"] as? String ?: "N/A"
                        val patientPhoneNumber =patientdocument["phoneNumber"] as? String ?: "N/A"
                        val patientAge = patientdocument["age"]as? String ?: "N/A"
                        val patientAddress =patientdocument["address"] as? String ?: "N/A"

                        val patientNameTextView = findViewById<TextView>(R.id.textView2)
                        val patientAgeTextView = findViewById<TextView>(R.id.textView5)
                        val patientGenderTextView = findViewById<TextView>(R.id.textView6)
                        val patientPhoneTextView = findViewById<TextView>(R.id.textView3)
                        val patientAddressTextView = findViewById<TextView>(R.id.textView4)

                        patientNameTextView.text = "Name: $patientName"
                        patientAgeTextView.text = "Age: $patientAge"
                        patientGenderTextView.text = "Gender: $patientGender"
                        patientPhoneTextView.text = "Number: $patientPhoneNumber"
                        patientAddressTextView.text = "Address: $patientAddress"


                    }
                }

            }

            private val storageReferenceForGallery: DataInterface = FirebaseDao()
            private fun retrieveImagesFromStorage(patientId: String) {


                val storageRef = storageReferenceForGallery.getStorageUrl(patientId)

                storageRef.listAll()
                    .addOnSuccessListener { listResult ->
                        val imageRefs = listResult.items

                        val localDir = getOutputDirectory(patientId )


                        val filteredImageRefs = imageRefs.filter { imageRef ->
                            val localFile = File(localDir, imageRef.name)
                            !localFile.exists()
                        }
                        filteredImageRefs.forEach { imageRef ->
                            downloadImage(imageRef) { uri ,creationDate->
                                uri?.let {
                                    LocalimageUris.add(Pair(it, creationDate) as Pair<Uri, Long>)

                                }
                            }
                        }
                    }

                    .addOnFailureListener { exception ->
                        // Handle errors appropriately
                        Log.w("TAG", "Error listing images:", exception)
                    }
            }

            fun refresh(){

                displayImages()
            }


            private fun displayImages() {
                val localDir = getOutputDirectory(patientId)
                val localImageFiles = localDir.listFiles()


                val contentHashes = HashSet<Int>()

                localImageFiles?.forEach { localImageFile ->
                    val contentHash = calculateContentHash(localImageFile)


                    if (!contentHashes.contains(contentHash)) {
                        LocalimageUris.add(Pair(Uri.fromFile(localImageFile), localImageFile.lastModified()))

                        contentHashes.add(contentHash)
                    }
                }

                imageAdapter.setImageData(LocalimageUris)
            }

            // Function to calculate content hash of a file
            private fun calculateContentHash(file: File): Int {
                return try {
                    val bytes = file.readBytes()
                    bytes.contentHashCode()
                } catch (e: Exception) {

                    0
                }
            }

            private fun downloadImage(imageRef: StorageReference, onComplete: (Uri?,Long?) -> Unit) {
                val localDir = getOutputDirectory(patientId )
                val localFile = File(localDir, imageRef.name)

                imageRef.metadata.addOnSuccessListener { metadata->
                    val creationdate=metadata.creationTimeMillis



                imageRef.getFile(localFile)
                    .addOnSuccessListener {
                        onComplete(Uri.fromFile(localFile),creationdate)
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors
                        Log.w("TAG", "Error downloading image", exception)
                        onComplete(null,null)
                    }.addOnFailureListener{
                            exception ->
                        Log.w("TAG", "Error retrieving metadata", exception)
                        onComplete(null, null)
                    }}
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











            class ImageAdapter(val context: Context) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

                private val localImageData = mutableMapOf<Uri, Long?>() // Map of Uri to date

                @SuppressLint("NotifyDataSetChanged")
                fun setImageData(uriDateMap: Set<Pair<Uri, Long?>>) {
                    this.localImageData.clear()
                    this.localImageData.putAll(uriDateMap)
                    notifyDataSetChanged()
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
                    // Create a LinearLayout to contain the ImageView and TextView
                    val layout = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL

                    }

                    // Create an ImageView for the image
                    val imageView = ImageView(context).apply {
                        val size = parent.width / 2 - 16
                        layoutParams = ViewGroup.LayoutParams(size, size)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }

                    // Create a TextView for the date
                    val dateTextView = TextView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        textSize = 12f
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        setTextColor(android.graphics.Color.WHITE)
                    }

                    // Add the ImageView and TextView to the layout
                    layout.addView(imageView)
                    layout.addView(dateTextView)

                    return ImageViewHolder(layout, imageView, dateTextView)
                }

                override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
                    val entry = localImageData.entries.elementAt(position)
                    val uri = entry.key
                    val dateInMillis = entry.value

                    // Load the image with Glide
                  //  https://stackoverflow.com/questions/32503327/glide-listener-doesnt-work  use full in the case of glide image not loading problem
                    Glide.with(context)
                        .load(uri) // Load the image from the given URI
                        .listener (object :RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.imageView.visibility = View.VISIBLE
                            return false
                        }

                    }).placeholder(R.drawable.ic_placeholder_image).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView)


                    // Format the date and set it below the image
                    holder.dateTextView.text = if (dateInMillis != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        dateFormat.format(Date(dateInMillis))
                    } else {
                        "Date not available"
                    }

                    // Set up click listener to open the image in a new activity
                    holder.imageView.setOnClickListener {
                        val intent = Intent(context, LargeImageViewActivity::class.java)
                        intent.putExtra("imageUri", uri.toString())
                        context.startActivity(intent)
                    }
                }

                override fun getItemCount(): Int = localImageData.size

                // ViewHolder class that holds the ImageView and TextView
                inner class ImageViewHolder(layout: LinearLayout, val imageView: ImageView, val dateTextView: TextView) :
                    RecyclerView.ViewHolder(layout)
            }


    }
