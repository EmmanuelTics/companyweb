package models

import kotlinx.serialization.Serializable


@Serializable
data class Employee(
    // Información Personal
    val name: String,
    val address: String? = null,
    val birthdate: String? = null,  // Ahora birthdate es opcional
    val nationality: String? = null,
    val maritalStatus: String? = null,
    val educationLevel: String? = null,

    // Documentación Oficial
    val birthCertificate: String? = null,
    val rfc: String? = null,
    val ine: String? = null,
    val curp: String? = null,
    val nss: String? = null,

    // Información de Contacto
    val phone: String? = null,
    val email: String? = null,

    // Información Bancaria
    val bankAccount: String? = null,
    val bankName: String? = null,
    val salary: Double? = null,  // salary también puede ser nulo

  val workstation: String? = null

)
