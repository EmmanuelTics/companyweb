package routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import models.User
import config.Database.collectionUser  // Ensure proper import
import org.litote.kmongo.*

fun Route.userRoutes() {
    route("/user") {
        // Crear un usuario
        post("/add") {
            try {
                val user = call.receive<User>()
                collectionUser.insertOne(user) // This now works because the collection is typed as User
                call.respond(HttpStatusCode.Created, "User added successfully")
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, "Invalid user data: ${e.message}")
            }
        }
post("/login") {
    try {
        // Recibe los datos de login como un objeto User (con solo username y password)
        val loginRequest = call.receive<User>()
        
        // Busca el usuario en la base de datos
        val foundUser = collectionUser.findOne(User::username eq loginRequest.username)

        // Valida el login comparando el password
        if (foundUser != null && foundUser.password == loginRequest.password) {
            call.respond(HttpStatusCode.OK, "Login successful")
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        call.respond(HttpStatusCode.InternalServerError, "Error during login: ${e.message}")
    }
}



        // Buscar un usuario por username
        get("/search/{username}") {
            try {
                val username = call.parameters["username"] ?: throw IllegalArgumentException("Username parameter is missing")
                val user = collectionUser.find(User::username eq username).firstOrNull()

                if (user != null) {
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
                val users = collectionUser.find().toList() // Correct reference to collectionUser
                call.respond(HttpStatusCode.OK, users)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error fetching users")
            }
        }

        // Actualizar un usuario por username
        put("/update/{username}") {
            try {
                val username = call.parameters["username"] ?: throw IllegalArgumentException("Username parameter is missing")
                val updatedUser = call.receive<User>()

                val result = collectionUser.updateOne(
                    User::username eq username,
                    set(
                        User::username setTo updatedUser.username,
                        User::password setTo updatedUser.password,
                      
                    )
                )

                if (result.matchedCount > 0) {
                    call.respond(HttpStatusCode.OK, "User updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error updating user: ${e.message}")
            }
        }

        // Eliminar un usuario por username
        delete("/delete/{username}") {
            try {
                val username = call.parameters["username"] ?: throw IllegalArgumentException("Username parameter is missing")
                val result = collectionUser.deleteOne(User::username eq username)

                if (result.deletedCount > 0) {
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
