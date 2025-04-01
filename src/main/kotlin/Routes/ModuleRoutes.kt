package routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import models.Module
import models.Submodule1
import config.Database
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

fun Route.moduleRoutes() {
    route("/modules") {
        // Obtener todos los módulos con id, nombre y submódulos concatenados
        get("/get") {
            try {
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

                val query = """
                    SELECT 
                        m.id AS idModulo,
                        m.nombreModulo,
                        sm.id AS idSubmodulo,
                        sm.nombreSubModulo AS nombreSubmodulo,
                        smHijo.id AS idSubmoduloHijo,
                        smHijo.nombreSubModulo AS nombreSubmoduloHijo
                    FROM Modulo m
                    LEFT JOIN SubModulo sm ON sm.idModulo = m.id
                    LEFT JOIN SubModulo smHijo ON smHijo.idSubModuloPadre = sm.id
                    ORDER BY m.id, sm.id, smHijo.id;
                """

                val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                val resultSet: ResultSet = preparedStatement.executeQuery()

                val moduleMap = mutableMapOf<Int, Module>()

                while (resultSet.next()) {
                    val moduleId = resultSet.getInt("idModulo")
                    val moduleName = resultSet.getString("nombreModulo")
                    val submoduleId = resultSet.getInt("idSubmodulo")
                    val submoduleName = resultSet.getString("nombreSubmodulo")
                    val submoduleChildId = resultSet.getInt("idSubmoduloHijo")
                    val submoduleChildName = resultSet.getString("nombreSubmoduloHijo")

                    // Si el módulo no existe en el mapa, lo creamos
                    val module = moduleMap.getOrPut(moduleId) {
                        Module(
                            moduleId = moduleId,
                            modulename = moduleName,
                            submodules = mutableListOf()
                        )
                    }

                    // Si hay un submódulo, lo agregamos a la lista
                    if (submoduleId != 0) {
                        val submodulesList = module.submodules as MutableList<Submodule1>

                        // Buscar si ya existe este submódulo
                        val existingSubmodule = submodulesList.find { it.idSubmodulo == submoduleId }

                        if (existingSubmodule == null) {
                            // Si el submódulo no existe, lo agregamos
                            submodulesList.add(
                                Submodule1(
                                    idSubmodulo = submoduleId,
                                    name = submoduleName,
                                    children = mutableListOf()
                                )
                            )
                        }

                        // Si hay un submódulo hijo, lo agregamos a la lista de hijos
                        if (submoduleChildId != 0) {
                            val parentSubmodule = submodulesList.find { it.idSubmodulo == submoduleId }

                            parentSubmodule?.children?.add(
                                Submodule1(
                                    idSubmodulo = submoduleChildId,
                                    name = submoduleChildName,
                                    children = mutableListOf()
                                )
                            )
                        }
                    }
                }

                call.respond(HttpStatusCode.OK, moduleMap.values)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error fetching modules and submodules")
            }
        }
    }
}
