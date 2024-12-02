import kotlinx.serialization.Serializable

@Serializable
data class ImageRequest(
    val imagem: String // Deve corresponder ao nome esperado pelo servidor
)