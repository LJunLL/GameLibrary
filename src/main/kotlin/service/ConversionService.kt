package service

import model.Videojuego
import java.nio.file.Path

object ConversionService {

    fun convertir(
        origen: Path,
        destino: Path,
        leer: (Path) -> List<Videojuego>,
        escribir: (Path, List<Videojuego>) -> Boolean
    ): Int? {
        val lista = leer(origen)
        if (lista.isEmpty()) return null
        return if (escribir(destino, lista)) lista.size else null
    }
}