package repository

import model.Videojuego
import java.nio.file.Path

interface VideojuegoRepository {
     fun leer(ruta: Path): List<Videojuego>
    fun escribir(ruta: Path, lista: List<Videojuego>): Boolean
}