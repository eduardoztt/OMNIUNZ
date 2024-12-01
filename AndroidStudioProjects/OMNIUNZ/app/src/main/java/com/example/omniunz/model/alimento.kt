    package com.example.omniunz.model

    import android.provider.ContactsContract.RawContacts.Data
    import kotlinx.serialization.Serializable

    @Serializable
    data class Alimento(
        val nome: String = "",
        val proteinas: String = "",
        val carboidrato: String = "",
        val gorduras: String = "",
        val calorias: String = "",
        val url: String = ""
    ) : java.io.Serializable
