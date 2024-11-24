package com.example.omniunz

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angellira.app_1_eduardo.preferences.PreferencesManager
import com.example.omniunz.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        preferencesManager = PreferencesManager(this)

        val storage = Firebase.storage
        val pickMedia =
            alterarImagemPerfil(storage)


        binding.userimage.setOnClickListener {
            Log.i("perfil", "Clicado")
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        binding.iconButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.buttonUser.setOnClickListener {
            Log.i("perfil", "Clicado")
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    private suspend fun downloadImage(downloadUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val inputStream = URL(downloadUrl).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun alterarImagemPerfil(
        storage: FirebaseStorage,
    ): ActivityResultLauncher<PickVisualMediaRequest> {

        val user = preferencesManager.userName
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")

                    val storageRef = storage.reference
                    val riversRef = storageRef.child("$user/${uri.lastPathSegment}")


                    val uploadTask = riversRef.putFile(uri)

                    try {
                        uploadTask.addOnFailureListener {
                            Log.e("PhotoPicker", "Upload falhou: ${it.message}")

                        }.addOnSuccessListener { _ ->
                            riversRef.downloadUrl.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadUri = task.result.toString()

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val bitmap = downloadImage(downloadUri)
                                        if (bitmap != null) {
                                            val base64String = convertBitmapToBase64(bitmap)
                                            preferencesManager.image = base64String // Salva no SharedPreferences
                                            Log.d("PhotoPicker", "Imagem salva em Base64 no SharedPreferences ${preferencesManager.image}")
                                        } else {
                                            Log.e("PhotoPicker", "Falha ao baixar ou processar a imagem")
                                        }
                                    }
                                    Log.d("PhotoPicker", "Download URL: $downloadUri")

                                } else {
                                    Log.e(
                                        "PhotoPicker",
                                        "Falha ao obter a URL de download: ${task.exception?.message}"
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "É necessário esta conectado a Internet para atualizar seus dados",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        return pickMedia
    }

    private fun setupView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}