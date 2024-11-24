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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        binding.cadastrarButton.setOnClickListener {
            val newUser = User(
                binding.usuarioText.text.toString(),
                binding.EmailText.text.toString(),
                binding.PasswordText.text.toString()
            )
            try {
                lifecycleScope.launch(IO) {
                    Api.retrofitService.saveUser(newUser)
                    startActivity(Intent(this@CadastroActivity,LoginActivity::class.java))
                }
            }catch (e:Exception){
                Toast.makeText(this, "Erro ao Cadastrar Usuário", Toast.LENGTH_SHORT).show()
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