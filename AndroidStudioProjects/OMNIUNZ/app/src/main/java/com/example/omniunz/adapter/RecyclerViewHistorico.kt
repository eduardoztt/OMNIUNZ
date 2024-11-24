
package com.angellira.app_1_eduardo.recyclerview.adapter

import Historico
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.omniunz.databinding.PostItemBinding


class RecyclerViewHistorico(
    private val posts: List<Historico>,

) : RecyclerView.Adapter<RecyclerViewHistorico.ViewHolder>() {

    inner class ViewHolder(
        private val postsitembinding: PostItemBinding
    ) :
        RecyclerView.ViewHolder(postsitembinding.root) {

        private lateinit var post: Historico


        fun bind(post: Historico) {
            this.post = post
            postsitembinding.NameClass.text = post.name
            postsitembinding.carboidratos.text = post.carboidrato
            postsitembinding.gorduras.text = post.gordura
            postsitembinding.proteinas.text = post.proteina
            postsitembinding.calorias.text = "${post.caloria}kcal - 100g"
            if (post.image == "imagem" || post.image == null ||  post.image == "") {
                postsitembinding.ImageClass.load("https://firebasestorage.googleapis.com/v0/b/aplicativomobile-4adc3.appspot.com/o/semImagem.jpg?alt=media&token=f31ed409-302f-4c71-aab2-5376dcf08a2a")
            } else {
                postsitembinding.ImageClass.load("https://firebasestorage.googleapis.com/v0/b/aplicativomobile-4adc3.appspot.com/o/semImagem.jpg?alt=media&token=f31ed409-302f-4c71-aab2-5376dcf08a2a")
            }


        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(
            PostItemBinding.inflate(
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