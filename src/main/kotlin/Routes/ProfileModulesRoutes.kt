package routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import models.profileModule
import config.Database
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import io.ktor.server.request.receive

fun Route.profileModulesRoutes() {  
    route("/profileModule") {

        // Ruta GET para obtener permisos
      get("/getPermissions/{profileId}") {
    try {
        val profileId = call.parameters["profileId"]?.toInt() ?: throw IllegalArgumentException("Perfil no especificado")
        val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

        // Consulta para buscar los permisos asignados a módulos y submódulos
        val query = """
            SELECT 
                pm.id, 
                pm.idPerfil, 
                pm.idModulo, 
                pm.idSubmodulo, 
                pm.bitAgregar, 
                pm.bitEditar, 
                pm.bitEliminar, 
                pm.bitConsultar, 
                pm.bitExportar, 
                pm.bitBitacora, 
                sm.nombreSubModulo, 
                sm.idSubmoduloPadre
            FROM 
                PerfilModulo pm
            LEFT JOIN 
                dbo.SubModulo sm ON pm.idSubmodulo = sm.id
            WHERE 
                pm.idPerfil = ? 
                AND (sm.idSubmoduloPadre IS NULL OR sm.idSubmoduloPadre != sm.id)
        """.trimIndent()

        connection.prepareStatement(query).use { preparedStatement ->
            preparedStatement.setInt(1, profileId)

            preparedStatement.executeQuery().use { resultSet ->
                val perfilModulos = mutableListOf<profileModule>()

                while (resultSet.next()) {
                    val idModulo: Int? = resultSet.getObject("idModulo") as? Int
                    val idSubmodulo: Int? = resultSet.getObject("idSubmodulo") as? Int

                    // Solo agregar el submódulo si no es un submodulo padre
                    if (idSubmodulo != null) {
                        val perfilModulo = profileModule(
                            id = resultSet.getInt("id"),
                            idPerfil = resultSet.getInt("idPerfil"),
                            idModulo = idModulo,  // Puede ser NULL
                            idSubmodulo = idSubmodulo,  // Puede ser NULL
                            bitAgregar = resultSet.getBoolean("bitAgregar"),
                            bitEditar = resultSet.getBoolean("bitEditar"),
                            bitEliminar = resultSet.getBoolean("bitEliminar"),
                            bitConsultar = resultSet.getBoolean("bitConsultar"),
                            bitExportar = resultSet.getBoolean("bitExportar"),
                            bitBitacora = resultSet.getBoolean("bitBitacora")
                        )
                        perfilModulos.add(perfilModulo)
                    }
                }

                // Responder con los permisos encontrados
                call.respond(HttpStatusCode.OK, perfilModulos)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        call.respond(HttpStatusCode.InternalServerError, "Error fetching profileModule data: ${e.message}")
    }
}


        post("/add") {
            try {
                val perfilModulos = call.receive<List<profileModule>>()
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

                // Usar una transacción para insertar o actualizar los permisos
                connection.autoCommit = false

                try {
                    perfilModulos.forEach { perfilModulo ->
                        val checkQuery = """
                            SELECT COUNT(*) FROM PerfilModulo
                            WHERE idPerfil = ? AND idModulo = ? AND idSubmodulo = ?
                        """.trimIndent()

                        val checkPreparedStatement = connection.prepareStatement(checkQuery)
                        checkPreparedStatement.setInt(1, perfilModulo.idPerfil)
                        checkPreparedStatement.setObject(2, perfilModulo.idModulo, java.sql.Types.INTEGER)  // Acepta null para idModulo
                        checkPreparedStatement.setObject(3, perfilModulo.idSubmodulo, java.sql.Types.INTEGER)  // Acepta null para idSubmodulo

                        checkPreparedStatement.executeQuery().use { resultSet ->
                            resultSet.next()
                            val count = resultSet.getInt(1)
                            if (count > 0) {
                                // Si ya existe, actualizar
                                val updateQuery = """
                                    UPDATE PerfilModulo
                                    SET bitAgregar = ?, bitEditar = ?, bitEliminar = ?, bitConsultar = ?, bitExportar = ?, bitBitacora = ?
                                    WHERE idPerfil = ? AND idModulo = ? AND idSubmodulo = ?
                                """.trimIndent()

                                connection.prepareStatement(updateQuery).use { updatePreparedStatement ->
                                    updatePreparedStatement.setBoolean(1, perfilModulo.bitAgregar)
                                    updatePreparedStatement.setBoolean(2, perfilModulo.bitEditar)
                                    updatePreparedStatement.setBoolean(3, perfilModulo.bitEliminar)
                                    updatePreparedStatement.setBoolean(4, perfilModulo.bitConsultar)
                                    updatePreparedStatement.setBoolean(5, perfilModulo.bitExportar)
                                    updatePreparedStatement.setBoolean(6, perfilModulo.bitBitacora)
                                    updatePreparedStatement.setInt(7, perfilModulo.idPerfil)
                                    updatePreparedStatement.setObject(8, perfilModulo.idModulo, java.sql.Types.INTEGER)
                                    updatePreparedStatement.setObject(9, perfilModulo.idSubmodulo, java.sql.Types.INTEGER)

                                    updatePreparedStatement.executeUpdate()
                                }
                            } else {
                                // Si no existe, insertar
                                val insertQuery = """
                                    INSERT INTO PerfilModulo (idPerfil, idModulo, idSubmodulo, bitAgregar, bitEditar, bitEliminar, bitConsultar, bitExportar, bitBitacora)
                                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                """.trimIndent()

                                connection.prepareStatement(insertQuery).use { insertPreparedStatement ->
                                    insertPreparedStatement.setInt(1, perfilModulo.idPerfil)
                                    insertPreparedStatement.setObject(2, perfilModulo.idModulo, java.sql.Types.INTEGER)
                                    insertPreparedStatement.setObject(3, perfilModulo.idSubmodulo, java.sql.Types.INTEGER)
                                    insertPreparedStatement.setBoolean(4, perfilModulo.bitAgregar)
                                    insertPreparedStatement.setBoolean(5, perfilModulo.bitEditar)
                                    insertPreparedStatement.setBoolean(6, perfilModulo.bitEliminar)
                                    insertPreparedStatement.setBoolean(7, perfilModulo.bitConsultar)
                                    insertPreparedStatement.setBoolean(8, perfilModulo.bitExportar)
                                    insertPreparedStatement.setBoolean(9, perfilModulo.bitBitacora)

                                    insertPreparedStatement.executeUpdate()
                                }
                            }
                        }
                    }

                    // Confirmar la transacción
                    connection.commit()
                    call.respond(HttpStatusCode.Created, "Permisos agregados o actualizados exitosamente")
                } catch (e: Exception) {
                    // Si ocurre un error, hacer rollback
                    connection.rollback()
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Error al agregar o actualizar permisos: ${e.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error al agregar permisos: ${e.message}")
            }
        }

        // Ruta PUT para actualizar permisos
    put("/updatePermissions/{profileId}") {
    try {
        val profileId = call.parameters["profileId"]?.toInt() ?: throw Exception("Profile ID missing")
        val updatedPermissions = call.receive<List<profileModule>>()

        val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")
        connection.autoCommit = false

        val updatedEntries = mutableListOf<profileModule>()

        try {
            updatedPermissions.forEach { permiso ->
                val checkQuery = """
                    SELECT COUNT(*) FROM PerfilModulo
                    WHERE idPerfil = ? AND idSubmodulo = ?
                """.trimIndent()

                val checkPreparedStatement = connection.prepareStatement(checkQuery)
                checkPreparedStatement.setInt(1, profileId)
                checkPreparedStatement.setObject(2, permiso.idSubmodulo, java.sql.Types.INTEGER)
                
                checkPreparedStatement.executeQuery().use { resultSet ->
                    resultSet.next()
                    val count = resultSet.getInt(1)
                    if (count > 0) {
                        val updateQuery = """
                            UPDATE PerfilModulo
                            SET bitAgregar = ?, bitEditar = ?, bitEliminar = ?, bitConsultar = ?, bitExportar = ?, bitBitacora = ?
                            WHERE idPerfil = ? AND idSubmodulo = ?
                        """.trimIndent()

                        connection.prepareStatement(updateQuery).use { updatePreparedStatement ->
                            updatePreparedStatement.setBoolean(1, permiso.bitAgregar)
                            updatePreparedStatement.setBoolean(2, permiso.bitEditar)
                            updatePreparedStatement.setBoolean(3, permiso.bitEliminar)
                            updatePreparedStatement.setBoolean(4, permiso.bitConsultar)
                            updatePreparedStatement.setBoolean(5, permiso.bitExportar)
                            updatePreparedStatement.setBoolean(6, permiso.bitBitacora)
                            updatePreparedStatement.setInt(7, profileId)
                            updatePreparedStatement.setObject(8, permiso.idSubmodulo, java.sql.Types.INTEGER)
                            
                            updatePreparedStatement.executeUpdate()
                        }
                    } else {
                        val insertQuery = """
                            INSERT INTO PerfilModulo (idPerfil, idModulo, idSubmodulo, bitAgregar, bitEditar, bitEliminar, bitConsultar, bitExportar, bitBitacora)
                            VALUES (?, NULL, ?, ?, ?, ?, ?, ?, ?)
                        """.trimIndent()

                        connection.prepareStatement(insertQuery).use { insertPreparedStatement ->
                            insertPreparedStatement.setInt(1, profileId)
                            insertPreparedStatement.setObject(2, permiso.idSubmodulo, java.sql.Types.INTEGER)
                            insertPreparedStatement.setBoolean(3, permiso.bitAgregar)
                            insertPreparedStatement.setBoolean(4, permiso.bitEditar)
                            insertPreparedStatement.setBoolean(5, permiso.bitEliminar)
                            insertPreparedStatement.setBoolean(6, permiso.bitConsultar)
                            insertPreparedStatement.setBoolean(7, permiso.bitExportar)
                            insertPreparedStatement.setBoolean(8, permiso.bitBitacora)

                            insertPreparedStatement.executeUpdate()
                        }
                    }
                }

                updatedEntries.add(permiso)
            }

            connection.commit()
            call.respond(HttpStatusCode.OK, updatedEntries)
        } catch (e: Exception) {
            connection.rollback()
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, "Error al actualizar permisos: ${e.message}")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
    }
}
}
}