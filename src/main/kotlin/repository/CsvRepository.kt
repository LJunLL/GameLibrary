package repository

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import model.Videojuego
import java.nio.file.Files
import java.nio.file.Path

object CsvRepository : VideojuegoRepository{
    override fun leer(ruta: Path): List<Videojuego> {
        if(!Files.isReadable(ruta)){
            println("El archivo $ruta no se puede leer")
            return emptyList();
        }

        val reader = csvReader{ delimiter = ';'}
        val filas: List<List<String>> = reader.readAll(ruta.toFile())

        val videojuegos: List<Videojuego> = filas.mapNotNull { fila ->
            if (fila.size >= 5) {
                try {
                    Videojuego(
                        id = fila[0].toInt(),
                        titulo = fila[1],
                        genero = fila[2],
                        anio = fila[3].toInt(),
                        nota = fila[4].toDouble()
                    )
                } catch (e: NumberFormatException) {
                    println("Fila inválida ignorada: $fila")
                    null
                }
            } else {
                println("Fila incompleta ignorada: $fila")
                null
            }
        }
        return videojuegos
    }

    override fun escribir(ruta: Path, lista: List<Videojuego>): Boolean {
        val filas : List<List<String>> = lista.map{ juego ->
            listOf(
                juego.id.toString(),
                juego.titulo,
                juego.genero,
                juego.anio.toString(),
                juego.nota.toString()
            )
        }
        return try{
            csvWriter{ delimiter = ';'}.writeAll(filas,ruta.toFile())
            true
        }catch(e: Exception){
            println("Error al escribir en el fichero CSV: ${e.message}")
            false
        }
    }
}