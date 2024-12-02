import kotlinx.serialization.Serializable

@Serializable
data class PredictionResult(
    val classe: String,
    val probabilidade: String
)
