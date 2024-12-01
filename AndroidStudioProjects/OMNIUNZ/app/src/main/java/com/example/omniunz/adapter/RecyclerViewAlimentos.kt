package com.example.omniunz.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.omniunz.databinding.PostItemAlimentosBinding
import com.example.omniunz.model.Alimento
import com.example.omniunz.model.MyDataItem

class RecyclerViewAlimentos (
    private val posts: List<Alimento>,
    val onItemClickListener: (Alimento) -> Unit


    ) : RecyclerView.Adapter<RecyclerViewAlimentos.ViewHolder>() {

        inner class ViewHolder(
            private val postsitembinding: PostItemAlimentosBinding
        ) :
            RecyclerView.ViewHolder(postsitembinding.root) {

            private lateinit var post: Alimento


            fun bind(post: Alimento) {
                this.post = post
                postsitembinding.NameAlimento.text = post.nome
                if (post.url == "imagem" || post.url == null ||  post.url == "") {
                    postsitembinding.ImageAlimento.load("https://firebasestorage.googleapis.com/v0/b/aplicativomobile-4adc3.appspot.com/o/semImagem.jpg?alt=media&token=f31ed409-302f-4c71-aab2-5376dcf08a2a")
                } else {
                    postsitembinding.ImageAlimento.load(post.url) {
                        listener(
                            onSuccess = { _, _ ->
                                // Quando a imagem for carregada com sucesso
                                postsitembinding.progressBar2.visibility = GONE
                                postsitembinding.ImageAlimento.visibility = VISIBLE
                            },
                            onError = { _, _ ->
                                // Caso ocorra erro no carregamento da imagem
                                postsitembinding.progressBar2.visibility = GONE

                            }
                        )
                    }

                }



                postsitembinding.root.setOnClickListener {
                    onItemClickListener(post) // Passa o item clicado
                }

            }

        }



        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder =
            ViewHolder(
                PostItemAlimentosBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = posts.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val post = posts[position]
            holder.bind(post)
        }
}