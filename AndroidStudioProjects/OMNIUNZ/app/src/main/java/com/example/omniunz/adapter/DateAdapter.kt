    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.recyclerview.widget.RecyclerView
    import com.example.omniunz.R
    import com.example.omniunz.databinding.ItemDataBinding
    import com.example.omniunz.model.MyDataItem

    class DataAdapter(
        private val posts: List<MyDataItem>,
        val onItemClickListener: (MyDataItem) -> Unit // Retorna o item clicado
    ) : RecyclerView.Adapter<DataAdapter.DataViewHolder>() {

        private var selectedPosition: Int = RecyclerView.NO_POSITION // Armazenar a posição do item clicado

        inner class DataViewHolder(private val binding: ItemDataBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(post: MyDataItem, position: Int) {
                binding.textView.text = post.name

                // Definindo o tamanho conforme o tipo do post
                val params = binding.root.layoutParams
//                if (post.isDaily) {
//                    params.width = 260
//                } else {
//                    params.width = 400
//                }
                binding.root.layoutParams = params

                // Alterar o fundo e cor do texto com base na seleção
                if (position == selectedPosition) {
                    binding.root.setBackgroundResource(R.drawable.bg_data)
                    binding.textView.setTextColor(binding.root.context.getColor(android.R.color.white))
                } else {
                    binding.root.setBackgroundColor(binding.root.context.getColor(android.R.color.white))
                    binding.textView.setTextColor(binding.root.context.getColor(android.R.color.black))
                }

                // Configuração do click listener
                binding.root.setOnClickListener {
                    selectedPosition = position // Atualiza a posição do item clicado
                    notifyDataSetChanged() // Notifica que o dataset foi alterado para atualizar os itens

                    onItemClickListener(post) // Passa o item clicado
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
            val binding = ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DataViewHolder(binding)
        }

        override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
            val post = posts[position]
            holder.bind(post, position)
        }

        override fun getItemCount(): Int = posts.size
    }
