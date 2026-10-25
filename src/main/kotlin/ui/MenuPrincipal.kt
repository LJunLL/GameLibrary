package ui

import config.Rutas
import model.Videojuego
import repository.CsvRepository
import repository.JsonRepository
import repository.VideojuegoRepository
import repository.XmlRepository
import service.CatalogoService
import java.nio.file.Path

object MenuPrincipal {

    fun mostrar() {
        var catalogo = CsvRepository.leer(Rutas.CSV_INI)
        println("\tCatálogo cargado: ${catalogo.size} videojuegos.")

        while (true) {
            println()
            println("===== CATÁLOGO DE VIDEOJUEGOS =====")
            println("1. Gestión CSV")
            println("2. Leer catálogo desde XML")
            println("3. Leer catálogo desde JSON")
            println("4. Conversiones entre formatos")
            println("5. Gestión fichero BIN")
            println("0. Salir")
            println("====================================")

            when (Consola.leerInt("Elige una opción: ", 0, 5)) {
                1 -> catalogo = MenuCsv.mostrar(catalogo)
                2 -> catalogo = importar(catalogo, XmlRepository, Rutas.XML_INI, "XML")
                3 -> catalogo = importar(catalogo, JsonRepository, Rutas.JSON_INI, "JSON")
                4 -> MenuConversiones.mostrar()
                5 -> MenuBin.mostrar()
                0 -> {
                    println("¡Hasta luego!")
                    return
                }
            }
        }
    }

    private fun importar(
        catalogo: List<Videojuego>,
        repo: VideojuegoRepository,
        ruta: Path,
        formato: String
    ): List<Videojuego> {
        val importados = repo.leer(ruta)
        if (importados.isEmpty()) {
            println("\tNo se ha leído ningún videojuego del $formato.")
            return catalogo
        }

        println("\n--- Videojuegos leídos del $formato ---")
        importados.forEach { MenuCsv.mostrarJuego(it) }

        val integrado = CatalogoService.integrarCatalogo(catalogo, importados)
        val nuevos = integrado.size - catalogo.size
        if (nuevos == 0) {
            println("\tNo hay videojuegos nuevos: todos los IDs ya existían.")
            return catalogo
        }

        if (!CsvRepository.escribir(Rutas.CSV_INI, integrado)) {
            println("\tNo se ha podido guardar el catálogo.")
            return catalogo
        }
        println("\tSe han integrado $nuevos videojuegos nuevos desde $formato.")
        return integrado
    }
}