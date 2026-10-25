import config.Rutas
import ui.MenuPrincipal

fun main() {
    Rutas.crearDirectorios()
    Rutas.mostrarEstructura()
    MenuPrincipal.mostrar()
}