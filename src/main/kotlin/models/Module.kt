package models

import kotlinx.serialization.Serializable

@Serializable
data class Module(
    val moduleId: Int = 0,
    val modulename: String,
    val submodules: List<Submodule1> 
)


@Serializable
data class Submodule1(
    val idSubmodulo: Int,
    val name: String,
    val children: MutableList<Submodule1> = mutableListOf()
)
