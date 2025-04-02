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
        // Obtener todos los módulos con id, nombre y submódulos anidados
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
                        smHijo.nombreSubModulo AS nombreSubmoduloHijo,
                        smHijo2.id AS idSubmoduloHijo2,
                        smHijo2.nombreSubModulo AS nombreSubmoduloHijo2,
                        smHijo3.id AS idSubmoduloHijo3,
                        smHijo3.nombreSubModulo AS nombreSubmoduloHijo3,
                        smHijo4.id AS idSubmoduloHijo4,
                        smHijo4.nombreSubModulo AS nombreSubmoduloHijo4
                    FROM Modulo m
                    LEFT JOIN SubModulo sm ON sm.idModulo = m.id
                    LEFT JOIN SubModulo smHijo ON smHijo.idSubModuloPadre = sm.id
                    LEFT JOIN SubModulo smHijo2 ON smHijo2.idSubModuloPadre = smHijo.id
                    LEFT JOIN SubModulo smHijo3 ON smHijo3.idSubModuloPadre = smHijo2.id
                    LEFT JOIN SubModulo smHijo4 ON smHijo4.idSubModuloPadre = smHijo3.id
                    ORDER BY m.id, sm.id, smHijo.id, smHijo2.id, smHijo3.id, smHijo4.id;
                """

                val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                val resultSet: ResultSet = preparedStatement.executeQuery()

                val moduleMap = mutableMapOf<Int, Module>()

                while (resultSet.next()) {
                    val moduleId = resultSet.getInt("idModulo")
                    val moduleName = resultSet.getString("nombreModulo") ?: ""
                    val submoduleId = resultSet.getInt("idSubmodulo")
                    val submoduleName = resultSet.getString("nombreSubmodulo") ?: ""
                    val submoduleChildId = resultSet.getInt("idSubmoduloHijo")
                    val submoduleChildName = resultSet.getString("nombreSubmoduloHijo") ?: ""
                    val submoduleChildId2 = resultSet.getInt("idSubmoduloHijo2")
                    val submoduleChildName2 = resultSet.getString("nombreSubmoduloHijo2") ?: ""
                    val submoduleChildId3 = resultSet.getInt("idSubmoduloHijo3")
                    val submoduleChildName3 = resultSet.getString("nombreSubmoduloHijo3") ?: ""
                    val submoduleChildId4 = resultSet.getInt("idSubmoduloHijo4")
                    val submoduleChildName4 = resultSet.getString("nombreSubmoduloHijo4") ?: ""

                    val module = moduleMap.getOrPut(moduleId) {
                        Module(
                            moduleId = moduleId,
                            modulename = moduleName,
                            submodules = mutableListOf()
                        )
                    }

                    if (submoduleId != 0) {
                        val submodulesList = module.submodules as MutableList<Submodule1>
                        val submodule = submodulesList.find { it.idSubmodulo == submoduleId }
                            ?: Submodule1(submoduleId, submoduleName, mutableListOf()).also {
                                submodulesList.add(it)
                            }

                        if (submoduleChildId != 0) {
                            val subChild1 = submodule.children.find { it.idSubmodulo == submoduleChildId }
                                ?: Submodule1(submoduleChildId, submoduleChildName, mutableListOf()).also {
                                    submodule.children.add(it)
                                }

                            if (submoduleChildId2 != 0) {
                                val subChild2 = subChild1.children.find { it.idSubmodulo == submoduleChildId2 }
                                    ?: Submodule1(submoduleChildId2, submoduleChildName2, mutableListOf()).also {
                                        subChild1.children.add(it)
                                    }

                                if (submoduleChildId3 != 0) {
                                    val subChild3 = subChild2.children.find { it.idSubmodulo == submoduleChildId3 }
                                        ?: Submodule1(submoduleChildId3, submoduleChildName3, mutableListOf()).also {
                                            subChild2.children.add(it)
                                        }

                                    if (submoduleChildId4 != 0) {
                                        subChild3.children.add(
                                            Submodule1(submoduleChildId4, submoduleChildName4, mutableListOf())
                                        )
                                    }
                                }
                            }
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
