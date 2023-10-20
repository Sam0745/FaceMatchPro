package com.example.material_3_recyclerview.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.material_3_recyclerview.MainActivity
import com.example.material_3_recyclerview.R
import com.example.material_3_recyclerview.adapter.ImageAdapter
import com.example.material_3_recyclerview.databinding.FragmentRecyclerBinding
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlin.math.sqrt

class RecyclerFragment : Fragment() {

    private lateinit var binding: FragmentRecyclerBinding
    private lateinit var faceDetector : FaceDetector

    private var userImage: Bitmap? = null
    private var aadharCardImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecyclerBinding.inflate(inflater, container, false)


        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.1f)
            .enableTracking()
            .build()

        faceDetector = FaceDetection.getClient(options)

        binding.captureButton.setOnClickListener {
            dispatchCameraIntent()
        }

        binding.uploadButton.setOnClickListener {
            dispatchImageUploadIntent()
        }

        binding.compareButton.setOnClickListener {
            if (userImage != null && aadharCardImage != null) {
                detectFace(userImage!!) { userFace ->
                    detectFace(aadharCardImage!!) { aadharFace ->
                        if (userFace != null && aadharFace != null) {
                            val userEyeLandmark = userFace.getLandmark(FaceLandmark.LEFT_EYE)
                            val aadharEyeLandmark = aadharFace.getLandmark(FaceLandmark.LEFT_EYE)

                            if (userEyeLandmark != null && aadharEyeLandmark != null) {
                                val userEyePosition = userEyeLandmark.position
                                val aadharEyePosition = aadharEyeLandmark.position

                                // Calculate the distance between the left eyes
                                val distance = calculateDistance(userEyePosition, aadharEyePosition)

                                // Set your threshold value (adjust as needed)
                                val threshold = 20.0 // Adjust as needed

                                if (distance <= threshold) {
                                    binding.resultText.text = "Images Match"
                                } else {
                                    binding.resultText.text = "Images Do Not Match"
                                }
                            } else {
                                binding.resultText.text = "Left eye landmark not detected in one of the images."
                            }
                        } else {
                            binding.resultText.text = "No face detected in one of the images."
                        }
                    }
                }
            }
        }

    }


    private fun detectFace(bitmap: Bitmap, callback: (Face?) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)

        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                val detectedFace = if (faces.isNotEmpty()) faces[0] else null
                callback(detectedFace)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                callback(null)
            }
    }

    private fun calculateDistance(
        point1: android.graphics.PointF,
        point2: android.graphics.PointF
    ): Float {
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        return sqrt(dx * dx + dy * dy)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA_CAPTURE -> {
                    userImage = data?.extras?.get("data") as Bitmap
                    binding.userImageView.setImageBitmap(userImage)
                }
                REQUEST_IMAGE_UPLOAD -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val inputStream = context?.contentResolver?.openInputStream(selectedImageUri)
                        aadharCardImage = BitmapFactory.decodeStream(inputStream)
                        binding.aadharImageView.setImageBitmap(aadharCardImage)
                    }
                }

            }
        }
    }

    private fun dispatchCameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA_CAPTURE)
    }

    private fun dispatchImageUploadIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_UPLOAD)
    }

    companion object {
        private const val REQUEST_CAMERA_CAPTURE = 1
        private const val REQUEST_IMAGE_UPLOAD = 2
    }


}