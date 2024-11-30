package com.example.omniunz

import User
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.omniunz.databinding.ActivityCadastroBinding
import com.example.omniunz.network.Api
import com.example.omniunz.network.ApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()

        auth = FirebaseAuth.getInstance()


        binding.cadastrarButton.setOnClickListener {
//            binding.cadastrarButton.isEnabled = false
            val email = binding.EmailText.text.toString()
            val name = binding.usuarioText.text.toString()
            val password = binding.PasswordText.text.toString()
            val confirmSenha = binding.confirmarPasswordText.text.toString()

            registerUser(email, password, name)

//            if (name.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && confirmSenha.isNotEmpty()) {
//                if (senha == confirmSenha) {
//
//                    val newUser = User(x
//                        name,
//                        email,
//                        senha
//                    )
//                    try {
//
//                        lifecycleScope.launch(IO) {
//                            Api.retrofitService.saveUser(newUser)
//                            startActivity(Intent(this@CadastroActivity, LoginActivity::class.java))
//                        }
//                    } catch (e: Exception) {
//                        Toast.makeText(this, "Erro ao Cadastrar Usuário", Toast.LENGTH_SHORT).show()
//                    }
//                }else{
//                    Toast.makeText(this, "O Campo confirmar Senha deve ser igual a Senha", Toast.LENGTH_SHORT).show()
//                }
//            }else{
//                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val user = mapOf(
                        "name" to name,
                        "email" to email
                    )
                    if (uid != null) {
                        database.child("users").child(uid).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Usuário Cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Database error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Registration error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun setupView() {
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}