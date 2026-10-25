package ui

import config.Rutas
import model.Videojuego
import repository.CsvRepository
import service.CatalogoService

object MenuCsv {

    fun mostrar(catalogoInicial: List<Videojuego>): List<Videojuego> {
        var catalogo = catalogoInicial
        while (true) {
            println()
            println("--- Gestión CSV ---")
            println("1. Listar todos los videojuegos")
            println("2. Buscar videojuego por ID")
            println("3. Añadir nuevo videojuego")
            println("4. Modificar nota de un videojuego")
            println("5. Eliminar videojuego")
            println("0. Volver al menú principal")

            when (Consola.leerInt("Elige una opción: ", 0, 5)) {
                1 -> listarVideojuegos(catalogo)
                2 -> buscarPorId(catalogo)
                3 -> catalogo = anadirVideojuego(catalogo)
                4 -> catalogo = modificarNota(catalogo)
                5 -> catalogo = eliminarVideojuego(catalogo)
                0 -> return catalogo
            }
        }
    }

    private fun listarVideojuegos(lista: List<Videojuego>) {
        if (lista.isEmpty()) {
            println("\tEl catálogo está vacío.")
            return
        }
        println("\n--- Catálogo (${lista.size} videojuegos) ---")
        lista.forEach { mostrarJuego(it) }
    }

    private fun buscarPorId(lista: List<Videojuego>) {
        val id = Consola.leerInt("ID a buscar: ")
        val juego = CatalogoService.buscarPorId(lista, id)
        if (juego == null) println("\tNo existe ningún videojuego con ID $id.")
        else mostrarJuego(juego)
    }

    private fun anadirVideojuego(lista: List<Videojuego>): List<Videojuego> {
        val titulo = Consola.leerTexto("Título: ")
        val genero = Consola.leerTexto("Género: ")
        val anio = Consola.leerInt("Año: ", 1950, 2100)
        val nota = Consola.leerDouble("Nota (0-10): ", 0.0, 10.0)

        val nuevo = Videojuego(CatalogoService.siguienteId(lista), titulo, genero, anio, nota)
        val nuevaLista = CatalogoService.anadir(lista, nuevo)
        if (!CsvRepository.escribir(Rutas.CSV_INI, nuevaLista)) {
            println("\tNo se ha añadido el videojuego.")
            return lista
        }
        println("\tVideojuego '${nuevo.titulo}' añadido con ID ${nuevo.id}.")
        return nuevaLista
    }

    private fun modificarNota(lista: List<Videojuego>): List<Videojuego> {
        val id = Consola.leerInt("ID del videojuego a modificar: ")
        val juego = CatalogoService.buscarPorId(lista, id)
        if (juego == null) {
            println("\tNo existe ningún videojuego con ID $id.")
            return lista
        }

        println("\t${juego.titulo} (nota actual: ${juego.nota})")
        val nuevaNota = Consola.leerDouble("Nueva nota (0-10): ", 0.0, 10.0)
        val nuevaLista = CatalogoService.modificarNota(lista, id, nuevaNota)
        if(!CsvRepository.escribir(Rutas.CSV_INI, nuevaLista)) {
            println("\tNo se ha modificado el videojuego")
            return lista
        }
        println("\tNota de '${juego.titulo}' actualizada a $nuevaNota.")
        return nuevaLista
    }

    private fun eliminarVideojuego(lista: List<Videojuego>): List<Videojuego> {
        val id = Consola.leerInt("ID del videojuego a eliminar: ")
        val juego = CatalogoService.buscarPorId(lista, id)
        if (juego == null) {
            println("\tNo existe ningún videojuego con ID $id.")
            return lista
        }

        if (!Consola.confirmar("¿Seguro que quieres eliminar '${juego.titulo}'?")) {
            println("\tEliminación cancelada.")
            return lista
        }

        val nuevaLista = CatalogoService.eliminar(lista, id)
        if (!CsvRepository.escribir(Rutas.CSV_INI, nuevaLista)) {
            println("\tNo se ha eliminado el videojuego.")
            return lista
        }
        println("\tVideojuego '${juego.titulo}' eliminado.")
        return nuevaLista
    }

     fun mostrarJuego(juego: Videojuego) {
        println("\t[${juego.id}] ${juego.titulo} | ${juego.genero} | ${juego.anio} | Nota: ${juego.nota}")
    }
}