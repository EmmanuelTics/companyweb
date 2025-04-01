package config

import java.sql.Connection
import java.sql.DriverManager

object Database {
private const val URL = "jdbc:sqlserver://CompanyDb.mssql.somee.com;databaseName=CompanyDb;encrypt=true;trustServerCertificate=true"

    private const val USER = "Cooker222_SQLLogin_1"
    private const val PASSWORD = "ezfga61q7i"

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
