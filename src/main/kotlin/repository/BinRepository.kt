package repository

import model.Videojuego
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption.*

object BinRepository : VideojuegoRepository {

    const val TAMANO_ID = Int.SIZE_BYTES         // 4
    const val TAMANO_TITULO = 40
    const val TAMANO_GENERO = 20
    const val TAMANO_ANIO = Int.SIZE_BYTES       // 4
    const val TAMANO_NOTA = Double.SIZE_BYTES    // 8
    const val TAMANO_REGISTRO = TAMANO_ID + TAMANO_TITULO + TAMANO_GENERO + TAMANO_ANIO + TAMANO_NOTA  // 76
    private const val DESPLAZAMIENTO_NOTA = TAMANO_ID + TAMANO_TITULO + TAMANO_GENERO + TAMANO_ANIO    // 68

    // ---------- Conversión Videojuego <-> bytes ----------

    private fun textoFijo(texto: String, tamano: Int): ByteArray =
        texto.take(tamano).padEnd(tamano, ' ').toByteArray(Charsets.ISO_8859_1)

    private fun aBuffer(juego: Videojuego): ByteBuffer {
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        buffer.putInt(juego.id)
        buffer.put(textoFijo(juego.titulo, TAMANO_TITULO))
        buffer.put(textoFijo(juego.genero, TAMANO_GENERO))
        buffer.putInt(juego.anio)
        buffer.putDouble(juego.nota)
        buffer.flip()
        return buffer
    }

    private fun deBuffer(buffer: ByteBuffer): Videojuego {
        val id = buffer.getInt()
        val tituloBytes = ByteArray(TAMANO_TITULO)
        buffer.get(tituloBytes)
        val generoBytes = ByteArray(TAMANO_GENERO)
        buffer.get(generoBytes)
        val anio = buffer.getInt()
        val nota = buffer.getDouble()
        return Videojuego(
            id,
            String(tituloBytes, Charsets.ISO_8859_1).trim(),
            String(generoBytes, Charsets.ISO_8859_1).trim(),
            anio,
            nota
        )
    }

    private fun escribirBuffer(canal: FileChannel, buffer: ByteBuffer) {
        while (buffer.hasRemaining()) canal.write(buffer)
    }

    // ---------- Lectura y escritura completas (interfaz) ----------

    override fun leer(ruta: Path): List<Videojuego> {
        if (!Files.isReadable(ruta)) {
            println("\tEl fichero binario no existe o no se puede leer.")
            return emptyList()
        }
        val juegos = mutableListOf<Videojuego>()
        return try {
            FileChannel.open(ruta, READ).use { canal ->
                val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
                while (canal.read(buffer) == TAMANO_REGISTRO) {
                    buffer.flip()
                    juegos.add(deBuffer(buffer))
                    buffer.clear()
                }
            }
            juegos
        } catch (e: Exception) {
            println("Error al leer el fichero binario: ${e.message}")
            emptyList()
        }
    }

    override fun escribir(ruta: Path, lista: List<Videojuego>): Boolean {
        return try {
            FileChannel.open(ruta, WRITE, CREATE, TRUNCATE_EXISTING).use { canal ->
                lista.forEach { escribirBuffer(canal, aBuffer(it)) }
            }
            true
        } catch (e: Exception) {
            println("Error al escribir el fichero binario: ${e.message}")
            false
        }
    }

    // ---------- Operaciones sobre registros ----------

    fun buscar(ruta: Path, id: Int): Videojuego? =
        if (Files.exists(ruta)) leer(ruta).firstOrNull { it.id == id } else null

    fun anadir(ruta: Path, juego: Videojuego): Boolean {
        return try {
            FileChannel.open(ruta, WRITE, CREATE, APPEND).use { canal ->
                escribirBuffer(canal, aBuffer(juego))
            }
            true
        } catch (e: Exception) {
            println("Error al añadir al fichero binario: ${e.message}")
            false
        }
    }

    // Acceso aleatorio: salta de registro en registro leyendo solo los 4 bytes del id
    private fun posicionDe(canal: FileChannel, id: Int): Long? {
        val bufferId = ByteBuffer.allocate(TAMANO_ID)
        val totalRegistros = canal.size() / TAMANO_REGISTRO
        for (i in 0 until totalRegistros) {
            val inicio = i * TAMANO_REGISTRO
            bufferId.clear()
            canal.read(bufferId, inicio)
            bufferId.flip()
            if (bufferId.getInt() == id) return inicio
        }
        return null
    }

    fun modificarNota(ruta: Path, id: Int, nuevaNota: Double): Boolean {
        return try {
            FileChannel.open(ruta, READ, WRITE).use { canal ->
                val inicio = posicionDe(canal, id) ?: return false
                val bufferNota = ByteBuffer.allocate(TAMANO_NOTA)
                bufferNota.putDouble(nuevaNota)
                bufferNota.flip()
                canal.position(inicio + DESPLAZAMIENTO_NOTA)
                escribirBuffer(canal, bufferNota)
            }
            true
        } catch (e: Exception) {
            println("Error al modificar el fichero binario: ${e.message}")
            false
        }
    }

    fun eliminar(ruta: Path, id: Int): Boolean {
        val temporal = Path.of("$ruta.tmp")
        return try {
            FileChannel.open(ruta, READ).use { lectura ->
                FileChannel.open(temporal, WRITE, CREATE, TRUNCATE_EXISTING).use { escritura ->
                    val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
                    while (lectura.read(buffer) == TAMANO_REGISTRO) {
                        buffer.flip()
                        if (buffer.getInt(0) != id) escribirBuffer(escritura, buffer)
                        buffer.clear()
                    }
                }
            }
            Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING)
            true
        } catch (e: Exception) {
            println("Error al eliminar del fichero binario: ${e.message}")
            Files.deleteIfExists(temporal)
            false
        }
    }
}