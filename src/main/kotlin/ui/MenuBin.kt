package ui

import config.Rutas
import model.Videojuego
import repository.BinRepository
import repository.CsvRepository

object MenuBin {

    fun mostrar() {
        while (true) {
            println()
            println("===== CRUD fichero BIN =====")
            println("1. Importar información de un fichero de intercambio")
            println("2. Leer información del fichero binario")
            println("3. Añadir un registro nuevo")
            println("4. Modificar un registro existente (por ID)")
            println("5. Eliminar un registro existente (por ID)")
            println("0. Volver")

            when (Consola.leerInt("Elige una opción: ", 0, 5)) {
                1 -> importar()
                2 -> leer()
                3 -> anadir()
                4 -> modificar()
                5 -> eliminar()
                0 -> return
            }
        }
    }

    private fun importar() {
        val juegos = CsvRepository.leer(Rutas.CSV_INI)
        if (juegos.isEmpty()) {
            println("\tNo hay datos en el CSV para importar.")
            return
        }
        if (BinRepository.escribir(Rutas.BIN, juegos)) {
            println("\tImportados ${juegos.size} videojuegos al fichero binario.")
        } else {
            println("\tNo se ha podido importar.")
        }
    }

    private fun leer() {
        val juegos = BinRepository.leer(Rutas.BIN)
        if (juegos.isEmpty()) {
            println("\tNo hay registros en el fichero binario.")
            return
        }
        println("\n--- Fichero binario (${juegos.size} registros) ---")
        juegos.forEach { MenuCsv.mostrarJuego(it) }
    }

    private fun pedirIdLibre(): Int {
        while (true) {
            val id = Consola.leerInt("ID del nuevo videojuego: ", 1)
            if (BinRepository.buscar(Rutas.BIN, id) == null) return id
            println("\tYa existe un videojuego con ID $id.")
        }
    }

    private fun anadir() {
        val id = pedirIdLibre()
        val titulo = Consola.leerTexto("Título: ")
        if (titulo.length > BinRepository.TAMANO_TITULO) {
            println("\tAviso: el título se recortará a ${BinRepository.TAMANO_TITULO} caracteres.")
        }
        val genero = Consola.leerTexto("Género: ")
        if (genero.length > BinRepository.TAMANO_GENERO) {
            println("\tAviso: el género se recortará a ${BinRepository.TAMANO_GENERO} caracteres.")
        }
        val anio = Consola.leerInt("Año: ", 1950, 2100)
        val nota = Consola.leerDouble("Nota (0-10): ", 0.0, 10.0)

        val juego = Videojuego(id, titulo, genero, anio, nota)
        if (BinRepository.anadir(Rutas.BIN, juego)) {
            println("\tRegistro '${juego.titulo}' añadido con ID $id.")
        } else {
            println("\tNo se ha podido añadir el registro.")
        }
    }

    private fun modificar() {
        val id = Consola.leerInt("ID del registro a modificar: ")
        val juego = BinRepository.buscar(Rutas.BIN, id)
        if (juego == null) {
            println("\tNo existe ningún registro con ID $id.")
            return
        }
        println("\t${juego.titulo} (nota actual: ${juego.nota})")
        val nuevaNota = Consola.leerDouble("Nueva nota (0-10): ", 0.0, 10.0)
        if (BinRepository.modificarNota(Rutas.BIN, id, nuevaNota)) {
            println("\tNota de '${juego.titulo}' actualizada a $nuevaNota.")
        } else {
            println("\tNo se ha podido modificar el registro.")
        }
    }

    private fun eliminar() {
        val id = Consola.leerInt("ID del registro a eliminar: ")
        val juego = BinRepository.buscar(Rutas.BIN, id)
        if (juego == null) {
            println("\tNo existe ningún registro con ID $id.")
            return
        }
        if (!Consola.confirmar("¿Seguro que quieres eliminar '${juego.titulo}'?")) {
            println("\tEliminación cancelada.")
            return
        }
        if (BinRepository.eliminar(Rutas.BIN, id)) {
            println("\tRegistro '${juego.titulo}' eliminado.")
        } else {
            println("\tNo se ha podido eliminar el registro.")
        }
    }
}