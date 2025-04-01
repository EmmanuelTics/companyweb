package routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import config.Database
import models.Submodule
import models.ModuleWithSubmodules
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

fun Route.routesModulesRoutes() {
    route("/Modules") {
        get("/getByProfile") {
            val connection: Connection? = Database.getConnection()
            if (connection == null) {
                call.respond(HttpStatusCode.InternalServerError, "Unable to connect to database")
                return@get
            }

            val idPerfil = call.request.queryParameters["idPerfil"]?.toIntOrNull()
            if (idPerfil == null) {
                call.respond(HttpStatusCode.BadRequest, "El parámetro 'idPerfil' es necesario y debe ser un número válido")
                return@get
            }

            var preparedStatement: PreparedStatement? = null
            var resultSet: ResultSet? = null

            try {
                val query = """
                    SELECT 
                        m.id AS module_id,
                        m.nombreModulo AS module_name, 
                        sm.id AS submodule_id,
                        sm.nombreSubModulo AS submodule_name, 
                        sm.urlSubModulo AS submodule_url, 
                        smh.id AS submodule_child_id, 
                        smh.nombreSubModulo AS submodule_child_name, 
                        smh.urlSubModulo AS submodule_child_url,
                        smn.id AS submodule_grandchild_id,
                        smn.nombreSubModulo AS submodule_grandchild_name,
                        smn.urlSubModulo AS submodule_grandchild_url,
                        smb.id AS submodule_greatgrandchild_id,
                        smb.nombreSubModulo AS submodule_greatgrandchild_name,
                        smb.urlSubModulo AS submodule_greatgrandchild_url,
                        COALESCE(pm.bitAgregar, pmSub.bitAgregar, pmHijo.bitAgregar, pmNieto.bitAgregar, pmBisnieto.bitAgregar) AS bitAgregar,
                        COALESCE(pm.bitEditar, pmSub.bitEditar, pmHijo.bitEditar, pmNieto.bitEditar, pmBisnieto.bitEditar) AS bitEditar,
                        COALESCE(pm.bitEliminar, pmSub.bitEliminar, pmHijo.bitEliminar, pmNieto.bitEliminar, pmBisnieto.bitEliminar) AS bitEliminar,
                        COALESCE(pm.bitConsultar, pmSub.bitConsultar, pmHijo.bitConsultar, pmNieto.bitConsultar, pmBisnieto.bitConsultar) AS bitConsultar,
                        COALESCE(pm.bitExportar, pmSub.bitExportar, pmHijo.bitExportar, pmNieto.bitExportar, pmBisnieto.bitExportar) AS bitExportar,
                        COALESCE(pm.bitBitacora, pmSub.bitBitacora, pmHijo.bitBitacora, pmNieto.bitBitacora, pmBisnieto.bitBitacora) AS bitBitacora
                    FROM 
                        [CompanyDb].[dbo].[Modulo] m
                    LEFT JOIN 
                        [CompanyDb].[dbo].[PerfilModulo] pm ON pm.idModulo = m.id AND pm.idPerfil = ?
                    LEFT JOIN 
                        [CompanyDb].[dbo].[SubModulo] sm ON sm.idModulo = m.id
                    LEFT JOIN 
                        [CompanyDb].[dbo].[PerfilModulo] pmSub ON pmSub.idSubmodulo = sm.id AND pmSub.idPerfil = ?
                    LEFT JOIN 
                        [CompanyDb].[dbo].[SubModulo] smh ON smh.idSubModuloPadre = sm.id
                    LEFT JOIN 
                        [CompanyDb].[dbo].[PerfilModulo] pmHijo ON pmHijo.idSubmodulo = smh.id AND pmHijo.idPerfil = ?
                    LEFT JOIN 
                        [CompanyDb].[dbo].[SubModulo] smn ON smn.idSubModuloPadre = smh.id
                    LEFT JOIN 
                        [CompanyDb].[dbo].[PerfilModulo] pmNieto ON pmNieto.idSubmodulo = smn.id AND pmNieto.idPerfil = ?
                    LEFT JOIN 
                        [CompanyDb].[dbo].[SubModulo] smb ON smb.idSubModuloPadre = smn.id
                    LEFT JOIN 
                        [CompanyDb].[dbo].[PerfilModulo] pmBisnieto ON pmBisnieto.idSubmodulo = smb.id AND pmBisnieto.idPerfil = ?
                    WHERE 
                        (
                        ISNULL(pm.bitAgregar,0) = 1 OR ISNULL(pm.bitEditar,0) = 1 OR ISNULL(pm.bitEliminar,0) = 1 OR 
                        ISNULL(pm.bitConsultar,0) = 1 OR ISNULL(pm.bitExportar,0) = 1 OR ISNULL(pm.bitBitacora,0) = 1 OR
                        ISNULL(pmSub.bitAgregar,0) = 1 OR ISNULL(pmSub.bitEditar,0) = 1 OR ISNULL(pmSub.bitEliminar,0) = 1 OR 
                        ISNULL(pmSub.bitConsultar,0) = 1 OR ISNULL(pmSub.bitExportar,0) = 1 OR ISNULL(pmSub.bitBitacora,0) = 1 OR
                        ISNULL(pmHijo.bitAgregar,0) = 1 OR ISNULL(pmHijo.bitEditar,0) = 1 OR ISNULL(pmHijo.bitEliminar,0) = 1 OR 
                        ISNULL(pmHijo.bitConsultar,0) = 1 OR ISNULL(pmHijo.bitExportar,0) = 1 OR ISNULL(pmHijo.bitBitacora,0) = 1 OR
                        ISNULL(pmNieto.bitAgregar,0) = 1 OR ISNULL(pmNieto.bitEditar,0) = 1 OR ISNULL(pmNieto.bitEliminar,0) = 1 OR 
                        ISNULL(pmNieto.bitConsultar,0) = 1 OR ISNULL(pmNieto.bitExportar,0) = 1 OR ISNULL(pmNieto.bitBitacora,0) = 1 OR
                        ISNULL(pmBisnieto.bitAgregar,0) = 1 OR ISNULL(pmBisnieto.bitEditar,0) = 1 OR ISNULL(pmBisnieto.bitEliminar,0) = 1 OR 
                        ISNULL(pmBisnieto.bitConsultar,0) = 1 OR ISNULL(pmBisnieto.bitExportar,0) = 1 OR ISNULL(pmBisnieto.bitBitacora,0) = 1
                        )
                    ORDER BY m.id, sm.id, smh.id, smn.id, smb.id;
                """.trimIndent()

                preparedStatement = connection.prepareStatement(query)
                // Set parameters for all 5 levels (same profile ID)
                preparedStatement.setInt(1, idPerfil)
                preparedStatement.setInt(2, idPerfil)
                preparedStatement.setInt(3, idPerfil)
                preparedStatement.setInt(4, idPerfil)
                preparedStatement.setInt(5, idPerfil)

                resultSet = preparedStatement.executeQuery()

                val modules = mutableListOf<ModuleWithSubmodules>()
                val submoduleCache = mutableMapOf<Int, Submodule>()
                val childSubmoduleCache = mutableMapOf<Int, Submodule>()
                val grandchildSubmoduleCache = mutableMapOf<Int, Submodule>()

                while (resultSet.next()) {
                    val moduleId = resultSet.getInt("module_id")
                    val moduleName = resultSet.getString("module_name")

                    // Level 1: Module
                    val module = modules.find { it.id == moduleId }
                        ?: ModuleWithSubmodules(moduleId, moduleName, mutableListOf()).also { modules.add(it) }

                    // Level 2: Submodule
                    val submoduleId = resultSet.getInt("submodule_id")
                    if (submoduleId != 0) {
                        val submoduleName = resultSet.getString("submodule_name")
                        val submoduleUrl = resultSet.getString("submodule_url") ?: ""
                        
                        val submodule = submoduleCache.getOrPut(submoduleId) {
                            Submodule(submoduleId, submoduleName, submoduleUrl, mutableListOf())
                        }
                        
                        if (!module.submodules.contains(submodule)) {
                            module.submodules.add(submodule)
                        }

                        // Level 3: Child Submodule
                        val submoduleChildId = resultSet.getInt("submodule_child_id")
                        if (submoduleChildId != 0) {
                            val submoduleChildName = resultSet.getString("submodule_child_name")
                            val submoduleChildUrl = resultSet.getString("submodule_child_url") ?: ""
                            
                            val submoduleChild = childSubmoduleCache.getOrPut(submoduleChildId) {
                                Submodule(submoduleChildId, submoduleChildName, submoduleChildUrl, mutableListOf())
                            }
                            
                            if (!submodule.submodules.any { it.id == submoduleChild.id }) {
                                submodule.submodules.add(submoduleChild)
                            }

                            // Level 4: Grandchild Submodule
                            val submoduleGrandchildId = resultSet.getInt("submodule_grandchild_id")
                            if (submoduleGrandchildId != 0) {
                                val submoduleGrandchildName = resultSet.getString("submodule_grandchild_name")
                                val submoduleGrandchildUrl = resultSet.getString("submodule_grandchild_url") ?: ""
                                
                                val submoduleGrandchild = grandchildSubmoduleCache.getOrPut(submoduleGrandchildId) {
                                    Submodule(submoduleGrandchildId, submoduleGrandchildName, submoduleGrandchildUrl, mutableListOf())
                                }
                                
                                if (!submoduleChild.submodules.any { it.id == submoduleGrandchild.id }) {
                                    submoduleChild.submodules.add(submoduleGrandchild)
                                }

                                // Level 5: Great-grandchild Submodule
                                val submoduleGreatGrandchildId = resultSet.getInt("submodule_greatgrandchild_id")
                                if (submoduleGreatGrandchildId != 0) {
                                    val submoduleGreatGrandchildName = resultSet.getString("submodule_greatgrandchild_name")
                                    val submoduleGreatGrandchildUrl = resultSet.getString("submodule_greatgrandchild_url") ?: ""
                                    
                                    val submoduleGreatGrandchild = Submodule(
                                        submoduleGreatGrandchildId, 
                                        submoduleGreatGrandchildName, 
                                        submoduleGreatGrandchildUrl
                                    )
                                    
                                    if (!submoduleGrandchild.submodules.any { it.id == submoduleGreatGrandchild.id }) {
                                        submoduleGrandchild.submodules.add(submoduleGreatGrandchild)
                                    }
                                }
                            }
                        }
                    }
                }

                call.respond(HttpStatusCode.OK, modules)

            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error fetching modules by profile: ${e.message}")
            } finally {
                try {
                    resultSet?.close()
                    preparedStatement?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}