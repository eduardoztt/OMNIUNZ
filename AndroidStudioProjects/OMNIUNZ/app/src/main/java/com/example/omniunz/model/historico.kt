import kotlinx.serialization.Serializable

@Serializable
data class Historico(
    var name: String = "",
    var proteina: String = "",
    var carboidrato: String = "",
    var gordura: String = "",
    var caloria: String = "",
    var data: String = "",
    var image: String = ""
)
