package repository

import kotlinx.serialization.json.Json
import model.Videojuego
import java.nio.file.Files
import java.nio.file.Path

object JsonRepository : VideojuegoRepository {

    private val json = Json { prettyPrint = true }

    override fun leer(ruta: Path): List<Videojuego> {
        if (!Files.isReadable(ruta)) {
            println("El archivo $ruta no se puede leer")
            return emptyList()
        }
        return try {
            json.decodeFromString<List<Videojuego>>(Files.readString(ruta))
        } catch (e: Exception) {
            println("Error al leer el JSON: ${e.message}")
            emptyList()
        }
    }

    override fun escribir(ruta: Path, lista: List<Videojuego>): Boolean {
        return try {
            Files.writeString(ruta, json.encodeToString(lista))
            true
        } catch (e: Exception) {
            println("Error al escribir el JSON: ${e.message}")
            false
        }
    }
}