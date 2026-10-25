package ui

object Consola {

    fun leerTexto(mensaje: String): String {
        while (true) {
            print(mensaje)
            val entrada = readlnOrNull()?.trim()
            if (!entrada.isNullOrEmpty()) return entrada
            println("\tEl texto no puede estar vacío.")
        }
    }

    fun leerInt(mensaje: String, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int {
        while (true) {
            print(mensaje)
            val numero = readlnOrNull()?.trim()?.toIntOrNull()
            when {
                numero == null -> println("\tDebes introducir un número entero.")
                numero !in min..max -> println("\tEl número debe estar entre $min y $max.")
                else -> return numero
            }
        }
    }

    fun leerDouble(mensaje: String, min: Double, max: Double): Double {
        while (true) {
            print(mensaje)
            val numero = readlnOrNull()?.trim()?.replace(',', '.')?.toDoubleOrNull()
            when {
                numero == null -> println("\tDebes introducir un número (ej: 8.5).")
                numero !in min..max -> println("\tEl número debe estar entre $min y $max.")
                else -> return numero
            }
        }
    }

    fun confirmar(mensaje: String): Boolean {
        while (true) {
            print("$mensaje (s/n): ")
            when (readlnOrNull()?.trim()?.lowercase()) {
                "s" -> return true
                "n" -> return false
                else -> println("\tResponde 's' o 'n'.")
            }
        }
    }
}