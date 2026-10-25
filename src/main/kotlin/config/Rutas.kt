package config

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

object Rutas {
    val DATOS: Path = Path.of("proyecto", "datos")
    val DATOS_INI: Path = DATOS.resolve("datos_ini")
    val DATOS_FIN: Path = DATOS.resolve("datos_fin")
    val CSV_INI: Path = DATOS_INI.resolve("videojuegos.csv")
    val XML_INI: Path = DATOS_INI.resolve("videojuegos.xml")
    val JSON_INI: Path = DATOS_INI.resolve("videojuegos.json")
    val BIN: Path = DATOS_FIN.resolve("videojuegos.bin")
    val CSV_FIN: Path = DATOS_FIN.resolve("videojuegos.csv")
    val XML_FIN: Path = DATOS_FIN.resolve("videojuegos.xml")
    val JSON_FIN: Path = DATOS_FIN.resolve("videojuegos.json")


    fun crearDirectorios() {
        println("CREACIÓN DE RUTAS PROYECTO")
        println("\tCreando rutas...")
        crearSiNoExiste(DATOS_INI)
        crearSiNoExiste(DATOS_FIN)
    }

    private fun crearSiNoExiste(ruta: Path) {
        if (!ruta.exists()) {
            Files.createDirectories(ruta)
            println("\tCreación de ruta para $ruta")
        }
    }

    fun mostrarEstructura() {
        println("MOSTRANDO ESTRUCTURA DE DIRECTORIOS Y FICHEROS")
        Files.walk(DATOS).use { stream ->
            stream.sorted().forEach { path ->
                val profundidad = path.nameCount - DATOS.nameCount
                val sangria = "\t".repeat(profundidad + 1)
                val prefijo = if (path.isDirectory()) "[DIR]" else "[FILE]"
                println("$sangria$prefijo ${path.fileName}")
            }
        }
    }
}