package com.example.omniunz

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.angellira.app_1_eduardo.preferences.PreferencesManager
import com.example.omniunz.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.UUID

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var preferencesManager: PreferencesManager
    private var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private val PICK_IMAGE_REQUEST = 71


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        preferencesManager = PreferencesManager(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        mostrarDadosUser()

        binding.imageView.setOnClickListener {
            openImageChooser()

        }

        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.configuracoes.setOnClickListener {
            startActivity(Intent(this,ContaActivity::class.java))
        }
        binding.notificacao.setOnClickListener {
            startActivity(Intent(this,NotificacoesActivity::class.java))
        }
        binding.suporte.setOnClickListener {
            startActivity(Intent(this,SuporteActivity::class.java))
        }
        binding.planos.setOnClickListener {
            startActivity(Intent(this,PlanosActivity::class.java))
        }
        binding.deslogar.setOnClickListener {
            preferencesManager.isLogged = false
            startActivity(Intent(this,SplashActivity::class.java))
            finishAffinity()
        }
        getUserData()


    }

    private fun mostrarDadosUser() {
        binding.imageView.load(preferencesManager.userImage)
        binding.username.text = preferencesManager.userName
    }

    private fun getUserData() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            databaseReference.child("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val image = snapshot.child("profileImageUrl").value?.toString()
                    preferencesManager.userEmail = email
                    preferencesManager.userName = name
                    preferencesManager.userImage = image.toString()
                    mostrarDadosUser()


                    Toast.makeText(this, "Name: $name, Email: $email", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.imageView.setImageURI(imageUri)

            // Carregar a imagem para o Firebase Storage
            if (imageUri != null) {
                val fileName = UUID.randomUUID().toString() + ".jpg"
                val imageRef = storageRef.child("profile_images/$fileName")

                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            // Agora vocÃª pode salvar o URL da imagem no Firebase Realtime Database
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                val userRef = database.reference.child("users").child(uid)
                                val userUpdates = mapOf("profileImageUrl" to imageUrl)

                                userRef.updateChildren(userUpdates)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setupView() {
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}