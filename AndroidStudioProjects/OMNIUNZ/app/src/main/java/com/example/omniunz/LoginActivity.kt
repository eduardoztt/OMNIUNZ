package com.example.omniunz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.angellira.app_1_eduardo.preferences.PreferencesManager
import com.example.omniunz.databinding.ActivityLoginBinding
import com.example.omniunz.network.Api
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferencesManager: PreferencesManager
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        preferencesManager = PreferencesManager(this)
        auth = FirebaseAuth.getInstance()


        binding.buttonCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        binding.ButtonEntrar.setOnClickListener {

            val email = binding.Emailtext.text.toString()
            val password = binding.senhatext.text.toString()

            loginUser(email, password)


//
//            if (email.isNotEmpty() && password.isNotEmpty()) {
//
//                try {
//                    lifecycleScope.launch(IO) {
//                        val users = Api.retrofitService.getUser()
//
//                        withContext(Main) {
//                            val matchedUser = users.values.find {
//                                it.email == email && it.password == password
//                            }
//                            if (matchedUser != null) {
//                                preferencesManager.isLogged = true
//                                preferencesManager.userEmail = matchedUser.email
//                                startActivity(
//                                    Intent(
//                                        this@LoginActivity,
//                                        SplashActivity::class.java
//                                    )
//                                )
//                                Log.d("Login", "Usuário encontrado: $matchedUser")
//                            } else {
//                                Log.d("Login", "Usuário não encontrado.")
//                            }
//
//                        }
//                    }
//                } catch (e: Exception) {
//                    Toast.makeText(this, "Erro ao acessar dados", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

    private fun getUserData() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            database.child("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val image = snapshot.child("profileImageUrl").value?.toString()
                    preferencesManager.userEmail = email
                    preferencesManager.userName = name
                    preferencesManager.userImage = image.toString()



                    Log.i("USER","${name} ${email} ${image}")
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    preferencesManager.isLogged = true
                    getUserData()
                    startActivity(Intent(this,SplashActivity::class.java))

                } else {
                    Toast.makeText(this, "Login error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupView() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}