
package routes

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import models.Employee
import config.Database.collection
import org.litote.kmongo.* 

fun Route.employeeRoutes() {
    route("/employee") {
post("/add") {
    try {
        val employees = call.receive<List<Employee>>() // Cambiar para recibir una lista de empleados
        employees.forEach { employee ->
            collection.insertOne(employee) // Insertar cada empleado
        }   
        call.respond(HttpStatusCode.Created, "Employees added successfully")
    } catch (e: Exception) {
        e.printStackTrace()
        call.respond(HttpStatusCode.BadRequest, "Invalid employee data: ${e.message}")
    }
}

     get("/search/{name}") {
    try {
        val name = call.parameters["name"] ?: throw IllegalArgumentException("Name parameter is missing")
        val employee = collection.find(Employee::name eq name).firstOrNull()

        if (employee != null) {
            call.respond(HttpStatusCode.OK, employee)
        } else {
            call.respond(HttpStatusCode.NotFound, "Employee not found")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        call.respond(HttpStatusCode.InternalServerError, "Error searching for employee: ${e.message}")
    }


}


        get("/get") {
            try {
                val employees = collection.find().toList()
                call.respond(HttpStatusCode.OK, employees)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error fetching employees")
            }
        }

     put("/update/{name}") {
            try {
                val name = call.parameters["name"] ?: throw IllegalArgumentException("Name parameter is missing")
                val updatedEmployees = call.receive<List<Employee>>() // Recibir una lista de empleados

                // Iterar sobre los empleados y actualizar uno por uno
                updatedEmployees.forEach { updatedEmployee ->
                    val result = collection.updateOne(
                        Employee::name eq name,
                        set(
                            Employee::name setTo updatedEmployee.name,
                            Employee::address setTo updatedEmployee.address,
                            Employee::birthdate setTo updatedEmployee.birthdate,
                            Employee::nationality setTo updatedEmployee.nationality,
                            Employee::maritalStatus setTo updatedEmployee.maritalStatus,
                            Employee::educationLevel setTo updatedEmployee.educationLevel,
                            Employee::birthCertificate setTo updatedEmployee.birthCertificate,
                            Employee::rfc setTo updatedEmployee.rfc,
                            Employee::ine setTo updatedEmployee.ine,
                            Employee::curp setTo updatedEmployee.curp,
                            Employee::nss setTo updatedEmployee.nss,
                            Employee::phone setTo updatedEmployee.phone,
                            Employee::email setTo updatedEmployee.email,
                            Employee::bankAccount setTo updatedEmployee.bankAccount,
                            Employee::bankName setTo updatedEmployee.bankName,
                            Employee::salary setTo updatedEmployee.salary,
                            Employee::workstation setTo updatedEmployee.workstation
                        )
                    )

                    // Verificar si el empleado fue encontrado y actualizado
                    if (result.matchedCount == 0L) { // Comparación con Long
                        call.respond(HttpStatusCode.NotFound, "Employee with name $name not found")
                        return@forEach  // Salir del bucle si no se encuentra el empleado
                    }
                }

                call.respond(HttpStatusCode.OK, "Employees updated successfully")
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error updating employees: ${e.message}")
            }
        }
        delete("/delete/{name}") {
            try {
                val name = call.parameters["name"] ?: throw IllegalArgumentException("Name parameter is missing")
                val result = collection.deleteOne(Employee::name eq name)

                if (result.deletedCount > 0) {
                    call.respond(HttpStatusCode.OK, "Employee deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Employee not found")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error deleting employee: ${e.message}")
            }
        }
    }
}
