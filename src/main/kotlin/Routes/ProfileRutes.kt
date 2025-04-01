package routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import models.Profile
import config.Database // Importa la clase de conexión a SQL Server
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

fun Route.profileRoutes() {
    route("/profile") {
        // Crear un perfil
        post("/add") {
            try {
                val profile = call.receive<Profile>()
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

                val query = "INSERT INTO Perfil (nombreperfil) VALUES (?)"
                val preparedStatement: PreparedStatement = connection.prepareStatement(query)

                preparedStatement.setString(1, profile.profilename)

                val rowsAffected = preparedStatement.executeUpdate()
                if (rowsAffected > 0) {
                    call.respond(HttpStatusCode.Created, "Profile added successfully")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Failed to add profile")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, "Invalid profile data: ${e.message}")
            }
        }

        // Obtener todos los perfiles
        get("/get") {
            try {
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")
                val query = "SELECT * FROM Perfil"
                val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                val resultSet: ResultSet = preparedStatement.executeQuery()

                val profiles = mutableListOf<Profile>()
                while (resultSet.next()) {
                    val profile = Profile(
                        profileId = resultSet.getInt("id"),
                        profilename = resultSet.getString("nombreperfil")
                    )
                    profiles.add(profile)
                }
                call.respond(HttpStatusCode.OK, profiles)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error fetching profiles")
            }
        }
    }
}
