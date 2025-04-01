package models

import kotlinx.serialization.Serializable

@Serializable
data class profileModule(
    val id: Int = 0,
    val idPerfil: Int,
    val idModulo: Int?,
    val idSubmodulo: Int?,
    val bitAgregar: Boolean = false,
    val bitEditar: Boolean = false,
    val bitEliminar: Boolean = false,
    val bitConsultar: Boolean = false,
    val bitExportar: Boolean = false,
    val bitBitacora: Boolean = false
)
