package com.example.omniunz

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.util.Patterns
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.Toast.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import androidx.core.widget.doOnTextChanged
import com.angellira.app_1_eduardo.preferences.PreferencesManager
import com.example.omniunz.databinding.ActivityContaBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ContaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContaBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var editSenha: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        preferencesManager = PreferencesManager(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.iconButtonBack.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        binding.iconButtonEditSenha.setOnClickListener {
            editSenha()
        }
        binding.iconButtonEditUsuario.setOnClickListener {
            editUsuario()
        }

        val editable = Editable.Factory.getInstance().newEditable(preferencesManager.userEmail)
        binding.emailText.text = editable
        val editableuser = Editable.Factory.getInstance().newEditable(preferencesManager.userName)
        binding.usuarioText.text = editableuser
        val editablesenha = Editable.Factory.getInstance().newEditable("*****")
        binding.senhaText.text = editablesenha


    }

    private fun cardSolicitarRedefinirSenha(
        layout: LinearLayout, editText: TextInputEditText, editText2: TextInputEditText? = null
    ): AlertDialog {

        lateinit var dialog: AlertDialog

        val positiveButton = Button(this).apply {
            text = "Alterar"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.white)) // Cor do texto
            setBackgroundColor(ContextCompat.getColor(context, R.color.Button)) // Cor de fundo
            setPadding(80, 20, 80, 20) // Padding do botão

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 90f // Tamanho do raio da borda
                setColor(ContextCompat.getColor(context, R.color.Button)) // Cor de fundo
            }
            background = drawable
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER // Centraliza o botão no layout
                setMargins(0, 70, 0, 70)
            }

            //codigo para o botao alterar do card
            setOnClickListener {
                val text = editText.text.toString()
                val text2 = editText2?.text?.toString()
                try {
                    if (editSenha == false) {
                        val name = text.trim()
                        if (name.isEmpty()) {
                            Toast.makeText(
                                this@ContaActivity,
                                "Name cannot be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            updateName(name.toString())
                        }
                    } else {
                        if (text == text2) {
                            val password = text.trim()
                            if (password.isEmpty()) {
                                Toast.makeText(
                                    this@ContaActivity,
                                    "Password cannot be empty",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                updatePassword(password.toString())
                            }
                        } else {
                            makeText(
                                this@ContaActivity,
                                "O campo confirmar senha deve ser igual a senha",
                                LENGTH_SHORT
                            ).show()
                        }
                    }
                    dialog.dismiss()
                } catch (e: Exception) {
                    //logica caso quebrar o app
                }

            }
        }

        // Adiciona o botão ao layout fornecido
        layout.addView(positiveButton)

        dialog = MaterialAlertDialogBuilder(this)
            .setView(layout) // Define o layout customizado com EditText e botão
            .create()

        return dialog
    }

    private fun editSenha() {
        editSenha = true
        //chama a função para criar o layout do EditText
        val (textInputLayoutSenha, editTextSenha) = layoutEditText("Nova Senha")
        val (textInputLayoutconfirmar, editTextconfirmar) = layoutEditText("Confirmar Senha")


        //adiciona o EditText ao layout do Material Desing
        textInputLayoutSenha.addView(editTextSenha)
        textInputLayoutconfirmar.addView(editTextconfirmar)

        //cria o linearLayout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(42, 100, 42, 16) // adiciona um espaço se necessario
            addView(textInputLayoutSenha)
            addView(textInputLayoutconfirmar)// adiciona o layout do Material Desing a um LinearLayout
        }

        //chama a função para criar o dialog
        val dialog = cardSolicitarRedefinirSenha(layout, editTextSenha, editTextconfirmar)

        //configuração do botão de enviar do dialog
        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setTextColor(android.graphics.Color.BLACK)//deixa a cor do botão preta

            //função para atualizar se o botão esta habilitado ou desabilitato
            fun updateButtonVisibility() {
                if (editTextSenha.text.toString() == editTextconfirmar.text.toString()) {
                    button.isEnabled = true
                } else {
                    button.isEnabled = false
                }
            }
            //chama a função de atualizar o estado do botão ao mudar o texto
            editTextSenha.doOnTextChanged { _, _, _, _ ->
                updateButtonVisibility()
            }
            editTextconfirmar.doOnTextChanged { _, _, _, _ ->
                updateButtonVisibility()
            }
            //inicia o botão
            updateButtonVisibility()
        }
        dialog.show()
    }


    private fun editUsuario() {
        editSenha = false

        //chama a função para criar o layout do EditText
        val (textInputLayout, editText) = layoutEditText("Usuário")


        //adiciona o EditText ao layout do Material Desing
        textInputLayout.addView(editText)

        //cria o linearLayout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(42, 100, 42, 16) // adiciona um espaço se necessario
            addView(textInputLayout) // adiciona o layout do Material Desing a um LinearLayout
        }

        //chama a função para criar o dialog
        val dialog = cardSolicitarRedefinirSenha(layout, editText)

        //configuração do botão de enviar do dialog
        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setTextColor(android.graphics.Color.BLACK)//deixa a cor do botão preta

            //função para atualizar se o botão esta habilitado ou desabilitato
            fun updateButtonVisibility() {
                if (editText.text.toString().trim().isNotEmpty()) {
                    button.isEnabled = true
                } else {
                    button.isEnabled = false
                }
            }
            //chama a função de atualizar o estado do botão ao mudar o texto
            editText.doOnTextChanged { _, _, _, _ ->
                updateButtonVisibility()
            }
            //inicia o botão
            updateButtonVisibility()
        }
        dialog.show()
    }

    private fun layoutEditText(texto: String): Pair<TextInputLayout, TextInputEditText> {
        //Adiciona um layout para o EditText do Material Desingn 3
        val textInputLayout = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            hint = texto
            boxBackgroundMode =
                TextInputLayout.BOX_BACKGROUND_OUTLINE // Adiciona as Bordas ao redor do EditText, sem isso fica como um EditText Normal
            boxStrokeColor =
                ContextCompat.getColor(context, R.color.Button) // seleciona a cor da borda
            setBoxCornerRadii(
                12f, 12f, 12f, 12f
            ) // arredondamento da borda
            boxStrokeWidth = 2 // largura da borda

        }

        //Cria um EditText do Material Desingn 3
        val editText = TextInputEditText(this).apply {

            val heightInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 70f, resources.displayMetrics
            ).toInt()

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, heightInPx
            )
            setTextColor(
                ContextCompat.getColor(
                    context, R.color.black
                )
            ) //cor do texto de email

            textSize = 22f // altera o tamanho das letras dentro do EditText
            isSingleLine = true
            setHorizontallyScrolling(true)
            background =
                null // tira  a borda apenas em baixo que vem normal, para deixar a borda ao redor do EditText inteiro
            setPadding(40, 20, 20, 20) // deixar um espaço entre o texto e borda

            //filtro de caracteres
            filters = arrayOf(InputFilter.LengthFilter(50), InputFilter { source, _, _, _, _, _ ->

                //não deixa quebra linhas e nem dar espaços
                if (source.contains('\n') || source.contains(' ')) {
                    ""
                } else {
                    null
                }
            })
        }
//        editText.doAfterTextChanged {
//            val source = editText.text.toString()
//            if (source.contains('\n') || source.contains(' ')) {
//                editText.setText(source.trim())
//                editText.setSelection(editText.text.toString().length)
//            }
//        }
        return Pair(textInputLayout, editText)
    }

    // Função para atualizar o nome no Firebase Realtime Database, mas precisa de melhoria pois depois de um tempo do usuario logado não funciona mais a alteração
    private fun updateName(name: String) {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val userRef = database.reference.child("users").child(uid)
            val userUpdates = mapOf("name" to name)

            userRef.updateChildren(userUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error updating name", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Função para atualizar a senha no Firebase Authentication, mas precisa de melhoria pois depois de um tempo do usuario logado não funciona mais a alteração
    private fun updatePassword(password: String) {
        val user = auth.currentUser

        user?.updatePassword(password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao alterar Senha", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun setupView() {
        binding = ActivityContaBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}