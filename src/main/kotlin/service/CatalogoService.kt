package service

import model.Videojuego

object CatalogoService {

    fun buscarPorId(lista: List<Videojuego>, id: Int): Videojuego? =
        lista.firstOrNull { it.id == id }

    fun siguienteId(lista: List<Videojuego>): Int =
        if (lista.isEmpty()) 1 else lista.maxOf { it.id } + 1

    fun anadir(lista: List<Videojuego>, juego: Videojuego): List<Videojuego> =
        lista + juego

    fun modificarNota(lista: List<Videojuego>, id: Int, nuevaNota: Double): List<Videojuego> =
        lista.map { juego ->
            if (juego.id == id) juego.copy(nota = nuevaNota) else juego
        }

    fun eliminar(lista: List<Videojuego>, id: Int): List<Videojuego> =
        lista.filter { it.id != id }

    fun integrarCatalogo(actual: List<Videojuego>, importados: List<Videojuego>): List<Videojuego> {
        val nuevos = importados.filter { importado -> actual.none { it.id == importado.id } }
        return actual + nuevos
    }
}