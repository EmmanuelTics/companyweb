package config

import java.sql.Connection
import java.sql.DriverManager

object Database {
private const val URL = "jdbc:sqlserver://localhost:1433;databaseName=CompanyDB;encrypt=true;trustServerCertificate=true"

    private const val USER = "sa"
    private const val PASSWORD = "1234"

    private var connection: Connection? = null

    init {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD)
            println("Conexión exitosa a SQL Server")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al conectar a la base de datos: ${e.message}")
        }
    }

    fun getConnection(): Connection? {
        return connection
    }
}
