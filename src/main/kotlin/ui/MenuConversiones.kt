package ui

import config.Rutas
import model.Videojuego
import repository.CsvRepository
import repository.JsonRepository
import repository.XmlRepository
import service.ConversionService
import java.nio.file.Path

object MenuConversiones {

    fun mostrar() {
        while (true) {
            println()
            println("--- Conversiones ---")
            println("1. CSV → JSON")
            println("2. CSV → XML")
            println("3. JSON → CSV")
            println("4. JSON → XML")
            println("5. XML → CSV")
            println("6. XML → JSON")
            println("0. Volver")

            when (Consola.leerInt("Elige una opción: ", 0, 6)) {
                1 -> ejecutar(Rutas.CSV_INI, Rutas.JSON_FIN, CsvRepository::leer, JsonRepository::escribir)
                2 -> ejecutar(Rutas.CSV_INI, Rutas.XML_FIN, CsvRepository::leer, XmlRepository::escribir)
                3 -> ejecutar(Rutas.JSON_INI, Rutas.CSV_FIN, JsonRepository::leer, CsvRepository::escribir)
                4 -> ejecutar(Rutas.JSON_INI, Rutas.XML_FIN, JsonRepository::leer, XmlRepository::escribir)
                5 -> ejecutar(Rutas.XML_INI, Rutas.CSV_FIN, XmlRepository::leer, CsvRepository::escribir)
                6 -> ejecutar(Rutas.XML_INI, Rutas.JSON_FIN, XmlRepository::leer, JsonRepository::escribir)
                0 -> return
            }
        }
    }

    private fun ejecutar(
        origen: Path,
        destino: Path,
        leer: (Path) -> List<Videojuego>,
        escribir: (Path, List<Videojuego>) -> Boolean
    ) {
        val convertidos = ConversionService.convertir(origen, destino, leer, escribir)
        if (convertidos == null) {
            println("\tNo se ha podido realizar la conversión.")
        } else {
            println("\tFichero generado: $destino ($convertidos registros convertidos).")
        }
    }
}