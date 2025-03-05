package models

import kotlinx.serialization.Serializable

@Serializable
data class User(
 
val username: String, // Nombre de usuario
    val password: String, // Contraseña
   
)
