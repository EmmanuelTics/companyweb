package routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import models.User
import models.LoginRequest
import config.Database // Importa la clase de conexión a SQL Server
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val message: String, val profileId: Int)

fun Route.userRoutes() {
    route("/user") {
        // Crear un usuario
        post("/add") {
            try {
                val user = call.receive<User>()
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

                val query = "INSERT INTO Usuario (nombreUsuario, contrasena, idPerfil, isActive) VALUES (?, ?, ?, ?)"
                val preparedStatement: PreparedStatement = connection.prepareStatement(query)

                preparedStatement.setString(1, user.username)
                preparedStatement.setString(2, user.password)
                preparedStatement.setInt(3, user.profileId)
                preparedStatement.setBoolean(4, user.isActive)

                val rowsAffected = preparedStatement.executeUpdate()
                if (rowsAffected > 0) {
                    call.respond(HttpStatusCode.Created, "User added successfully")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Failed to add user")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, "Invalid user data: ${e.message}")
            }
        }

post("/login") {
    try {
        val loginRequest = call.receive<LoginRequest>()
        val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

        val query = "SELECT * FROM Usuario WHERE nombreUsuario = ?"
        val preparedStatement: PreparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, loginRequest.username)

        val resultSet: ResultSet = preparedStatement.executeQuery()

        if (resultSet.next()) {
            val storedPassword = resultSet.getString("contrasena")
            val storedProfileId = resultSet.getInt("idPerfil")
            val isActive = resultSet.getBoolean("isActive")

            if (storedPassword == loginRequest.password) {
                if (isActive) {
                    val response = LoginResponse("Login successful", storedProfileId)
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "User is inactive")
                }
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        call.respond(HttpStatusCode.InternalServerError, "Error during login: ${e.message}")
    }
}




        // Buscar un usuario por nombreUsuario
        get("/search/{username}") {
            try {
                val username = call.parameters["username"] ?: throw IllegalArgumentException("Username parameter is missing")
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

                val query = "SELECT * FROM Usuario WHERE nombreUsuario = ?"
                val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, username)

                val resultSet: ResultSet = preparedStatement.executeQuery()
                if (resultSet.next()) {
                    val user = User(
                        userId = resultSet.getInt("idUsuario"),
                        username = resultSet.getString("nombreUsuario"),
                        password = resultSet.getString("contrasena"),
                        profileId = resultSet.getInt("idPerfil"),
                        isActive = resultSet.getBoolean("isActive")
                    )
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error searching for user: ${e.message}")
            }
        }

        // Obtener todos los usuarios
        get("/get") {
            try {
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")
                val query = "SELECT * FROM Usuario"
                val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                val resultSet: ResultSet = preparedStatement.executeQuery()

                val users = mutableListOf<User>()
                while (resultSet.next()) {
                    val user = User(
                        userId = resultSet.getInt("id"),
                        username = resultSet.getString("nombreUsuario"),
                        password = resultSet.getString("contrasena"),
                        profileId = resultSet.getInt("idPerfil"),
                        isActive = resultSet.getBoolean("isActive")
                    )
                    users.add(user)
                }
                call.respond(HttpStatusCode.OK, users)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error fetching users")
            }
        }

        // Actualizar un usuario por nombreUsuario
        put("/update/{username}") {
            try {
                val username = call.parameters["username"] ?: throw IllegalArgumentException("Username parameter is missing")
                val updatedUser = call.receive<User>()
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

                val query = """
                    UPDATE Usuario SET nombreUsuario = ?, contrasena = ?, idPerfil = ?, isActive = ?
                    WHERE nombreUsuario = ?
                """
                val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, updatedUser.username)
                preparedStatement.setString(2, updatedUser.password)
                preparedStatement.setInt(3, updatedUser.profileId)
                preparedStatement.setBoolean(4, updatedUser.isActive)
                preparedStatement.setString(5, username)

                val rowsAffected = preparedStatement.executeUpdate()
                if (rowsAffected > 0) {
                    call.respond(HttpStatusCode.OK, "User updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error updating user: ${e.message}")
            }
        }

        // Eliminar un usuario por nombreUsuario
        delete("/delete/{username}") {
            try {
                val username = call.parameters["username"] ?: throw IllegalArgumentException("Username parameter is missing")
                val connection: Connection = Database.getConnection() ?: throw Exception("Connection error")

                val query = "DELETE FROM Usuario WHERE nombreUsuario = ?"
                val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, username)

                val rowsAffected = preparedStatement.executeUpdate()
                if (rowsAffected > 0) {
                    call.respond(HttpStatusCode.OK, "User deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error deleting user: ${e.message}")
            }
        }
    }
}
