package config

import models.Employee
import models.User
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object Database {
    private val client = KMongo.createClient("mongodb+srv://ema888176:Emmanuel123@companyemma.6mh1x.mongodb.net/?retryWrites=true&w=majority&appName=CompanyEmma") // Coloca aquí la cadena de conexión completa
    private val database = client.getDatabase("CompanyEmma") // Nombre de la base de datos
    val collection = database.getCollection<Employee>("Employee") // Colección "users"
   val collectionUser = database.getCollection<User>("User")
}
