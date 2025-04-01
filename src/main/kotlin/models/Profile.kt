package models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val profileId: Int = 0, // Valor predeterminado para IDs generados automáticamente
    val profilename: String // Nombre del perfil
)
