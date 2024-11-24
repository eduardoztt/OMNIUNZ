import android.provider.ContactsContract.RawContacts.Data
import kotlinx.serialization.Serializable

@Serializable
data class Historico(
    val name: String,
    val proteina: String,
    val carboidrato: String,
    val gordura: String,
    val caloria: String,
    val data: String,
    val image: String
)
