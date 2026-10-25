# Catálogo de Videojuegos

Programa de consola en Kotlin para gestionar un catálogo de videojuegos con persistencia en ficheros.
Permite gestionar el catálogo en CSV, importar datos desde XML y JSON, convertir entre los tres
formatos y gestionar un fichero binario de acceso aleatorio.

## 1. Estructura de datos

### Data class

```kotlin
@Serializable
data class Videojuego(
    val id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
```

### Registro binario (`videojuegos.bin`)

| Campo  | Tipo   | Tamaño   | Posición en el registro |
|--------|--------|----------|-------------------------|
| id     | Int    | 4 bytes  | 0 – 3                   |
| titulo | String | 40 bytes | 4 – 43                  |
| genero | String | 20 bytes | 44 – 63                 |
| anio   | Int    | 4 bytes  | 64 – 67                 |
| nota   | Double | 8 bytes  | 68 – 75                 |

**Tamaño total del registro: 76 bytes.**

## 2. Estructura del proyecto

- `config/Rutas.kt`: rutas del proyecto y creación de directorios.
- `model/`: `Videojuego` y `VideojuegosXML` (wrapper de la raíz XML).
- `repository/`: un repository por formato (CSV, XML, JSON y binario) con una interfaz común.
- `service/`: lógica del catálogo (`CatalogoService`) y conversiones (`ConversionService`).
- `ui/`: menús y lectura validada de consola (`Consola`).

## 3. Instrucciones de ejecución

- **Requisitos:** JDK 24 (o el configurado en `jvmToolchain`).
- **Compilación:** abrir el proyecto en IntelliJ IDEA y sincronizar Gradle.
- **Datos iniciales:** colocar `videojuegos.csv`, `videojuegos.xml` y `videojuegos.json` en `proyecto/datos/datos_ini/`.
- **Ejecución:** ejecutar `Main.kt`. Las carpetas `datos_ini` y `datos_fin` se crean automáticamente si no existen.
- Los ficheros generados (conversiones y binario) se guardan en `proyecto/datos/datos_fin/`.

## 4. Decisiones de diseño

- **CSV como formato principal:** el catálogo se carga desde el CSV al iniciar y se guarda tras cada cambio, porque es sencillo de editar a mano.
- **Arquitectura por capas:** repository (ficheros), service (lógica) y ui (menús), para que cada parte tenga una sola responsabilidad.
- **Interfaz `VideojuegoRepository`:** permite que la importación y las conversiones funcionen con cualquier formato sin duplicar código.
- **Listas inmutables:** las operaciones devuelven listas nuevas en vez de modificar la original.
- **Escritura con resultado:** los métodos `escribir` devuelven `Boolean` para no confirmar al usuario un cambio que no se ha guardado.
- **Tamaño de los textos:** 40 bytes para el título (el más largo tiene 39 caracteres) y 20 para el género (margen sobre los actuales).
- **Codificación ISO-8859-1** en el binario: garantiza 1 byte por carácter, incluidas tildes y ñ, para que el tamaño del registro sea fijo.
- **Eliminación con fichero temporal:** evita desplazar bytes dentro del fichero original.