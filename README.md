# Diario espacial — PC02 DAP

Aplicación Android de Tovar (24100514) y Rojas (24100478).

## Acuerdo de coordinación
- Henry Tovar Landa 1: Tovar (`HenTovar`): Imágenes APOD, lista/detalle/favoritos.
- Julia Rojas Estrada: Rojas (`jutt-pixel`): Asteroides NeoWs, lista/detalle/filtro/favoritos.
- Tovar prepara la base compartida (Retrofit, Database, navegación y configuración); Rojas integra Asteroides sobre esa base. Ambas tablas forman parte de la Database común.
- Cada integrante debe revisar el PR del otro, dejar un comentario y aprobar. No hacer push directo a `main`.

## Ejecutar
1. Android Studio con JDK 17, Android SDK 35 y conexión a Internet para descargar dependencias.
2. Abrir este repositorio y sincronizar Gradle.
3. Obtener una clave propia en https://api.nasa.gov (no usar DEMO_KEY).
4. En `local.properties` (ignorado por Git) mantener `sdk.dir` de tu instalación y añadir `NASA_API_KEY=tu_clave`.
5. Ejecutar `app` en dispositivo/emulador Android 8.0/API 26 o superior.
6. `./gradlew testDebugUnitTest assembleDebug lintDebug`; pruebas de Room en dispositivo: `./gradlew connectedDebugAndroidTest`.

No se incluye ninguna clave. Sin clave se puede compilar, pero la app muestra configuración pendiente y no llama a NASA. Una clave dentro del APK no es un secreto inaccesible: no publicar APK con una clave de privilegios sensibles.

## Flujo de datos
Retrofit → DTO → conversión explícita → Room → Flow/StateFlow → Compose. Las listas y los detalles se leen desde Room. Refrescar conserva favoritos mediante actualización de contenido sin sustituir el estado favorito. Los errores de red conservan caché local y permiten reintentar. APOD vídeo no se entrega a Coil. Asteroides se identifican por ID + fecha para no perder aproximaciones.

## Entrega y límites
Las pruebas automatizadas y el estado de compilación se documentan en VALIDACION.md. La prueba real NASA requiere la clave de la pareja y la aceptación en un emulador o teléfono; no se reemplaza por fixtures. Revisar el contrato APOD actual de NASA si el servicio devuelve un formato distinto al del enunciado.
