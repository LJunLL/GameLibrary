package repository

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import model.Videojuego
import model.VideojuegosXML
import java.nio.file.Files
import java.nio.file.Path

object XmlRepository: VideojuegoRepository {

    override fun leer(ruta: Path): List<Videojuego> {
        if (!Files.isReadable(ruta)) {
            println("El archivo $ruta no se puede leer")
            return emptyList()
        }

        return try {
            val mapper = XmlMapper().registerKotlinModule()
            val wrapper = mapper.readValue<VideojuegosXML>(ruta.toFile())
            wrapper.juegos
        } catch (e: Exception) {
            println("Error al leer el XML: ${e.message}")
            emptyList()
        }
    }

    override fun escribir(ruta: Path, lista: List<Videojuego>): Boolean {
        return try {
            val mapper = XmlMapper().registerKotlinModule()
            val texto = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(VideojuegosXML(lista))
            Files.writeString(ruta, texto)
            true
        } catch (e: Exception) {
            println("Error al escribir el XML: ${e.message}")
            false
        }
    }
}