package models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int = 0,        // ID del usuario (autoincremental, clave primaria)
    val username: String,       // Nombre de usuario
    val password: String,       // Contraseña en texto plano
    val profileId: Int,         // ID del perfil asignado (idPerfil en la tabla Usuario)
    val isActive: Boolean = true // Estado del usuario (activo o inactivo)
)



@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)
