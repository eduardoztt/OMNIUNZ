    package com.example.omniunz.model

    import android.provider.ContactsContract.RawContacts.Data
    import kotlinx.serialization.Serializable

    @Serializable
    data class Alimento(
        val name: String,
        val proteina: String,
        val carboidrato: String,
        val gordura: String,
        val caloria: String,
        val image: String
    ) : java.io.Serializable
