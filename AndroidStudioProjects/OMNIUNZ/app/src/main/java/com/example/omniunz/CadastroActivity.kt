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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
            verificacoesDosCampos()
        }
    }

    private fun verificacoesDosCampos() {

        val email = binding.EmailText.text.toString()
        val name = binding.usuarioText.text.toString()
        val password = binding.PasswordText.text.toString()
        val confirmSenha = binding.confirmarPasswordText.text.toString()


        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmSenha.isNotEmpty()) {
            if (password == confirmSenha) {
                try {
                    binding.cadastrarButton.isEnabled = false
                    registerUser(email, password, name)
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao Cadastrar Usuário", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "O Campo confirmar Senha deve ser igual a Senha",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val user = mapOf(
                        "name" to name,
                        "email" to email,
                        "metaDiaria" to 0
                    )
                    if (uid != null) {
                        database.child("users").child(uid).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                } else {
                                    binding.cadastrarButton.isEnabled = true
                                    Toast.makeText(this, "Erro ao salvar dados no banco de dados: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    binding.cadastrarButton.isEnabled = true
                    // Exibindo erro de autenticação
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> "A senha fornecida é muito fraca. Tente uma senha mais forte."
                        is FirebaseAuthInvalidCredentialsException -> "O formato do E-mail é inválido."
                        is FirebaseAuthUserCollisionException -> "Já existe uma conta com este E-mail."
                        else -> "Erro no cadastro: ${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
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