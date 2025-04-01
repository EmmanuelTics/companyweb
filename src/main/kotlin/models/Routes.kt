package models

import kotlinx.serialization.Serializable

@Serializable
data class Submodule(
    val id: Int,
    val name: String,
    val url: String,
    val submodules: MutableList<Submodule> = mutableListOf()
)

@Serializable
data class ModuleWithSubmodules(
    val id: Int,           // Aseguramos que el campo id sea de tipo Int
    val moduleName: String,
    val submodules: MutableList<Submodule> = mutableListOf()
)
