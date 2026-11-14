# Validación — 14-nov-2026

## PR de Tovar (base + APOD)
- JDK 17, Android SDK 35, Gradle 8.10.2, AGP 8.7.3, Kotlin 2.0.21.
- `testDebugUnitTest assembleDebug compileDebugAndroidTestKotlin`: BUILD SUCCESSFUL.
- Cinco pruebas unitarias de APOD: sin fallos.
- APK debug generado; prueba de Room para dispositivo compilada, NO ejecutada en emulador/teléfono.
- La pestaña Asteroides muestra aviso de integración pendiente hasta incorporar el PR de Rojas.
- Ninguna clave NASA usada durante estas pruebas.

## Pendiente de validación humana
Clave NASA propia, ejecución en teléfono/emulador, llamadas reales APOD/NeoWs, UI y Room real con `connectedDebugAndroidTest`. No se acredita esto con una compilación ni con fixtures.

Ver PRUEBAS_MANUALES.md. Debe existir un comentario y aprobación cruzada real antes de fusionar. `main` está protegida con una aprobación obligatoria.

## PR de Rojas (integración completa)
- `testDebugUnitTest assembleDebug lintDebug compileDebugAndroidTestKotlin`: BUILD SUCCESSFUL.
- 10/10 pruebas unitarias verdes: cinco APOD y cinco NeoWs.
- Lint: 0 errores, 13 advertencias (principalmente configuración/versiones de dependencias e icono). No se suprimieron errores para obtener el resultado.
- APK debug de ambas opciones generado. AndroidTest de Room compilado, NO ejecutado.
- El PR de Rojas incluye la base del PR de Tovar: primero revisar/fusionar el PR #1 y después el #2, ambos hacia main. GitHub reducirá el diff del segundo tras fusionar el primero.
- Falta clave NASA propia y prueba en dispositivo; no se afirma E2E real contra NASA.

## Corrección de imágenes APOD (versión 1.1)
La prueba del teléfono mostró el logo NASA. Se reprodujo el problema: el endpoint legacy devolvía HTTP 200, pero siete títulos «NASA Science» y siete URLs del logo. La comprobación anterior de formato/cantidad no certificaba el contenido astronómico.

Se mantiene la llamada del enunciado. Si devuelve marcadores/logo o falla por HTTP/red, se consulta el servicio oficial actual de NASA APOD Basic. En ese contrato `url` es la página HTML y `hdurl` es la imagen; el adaptador usa la imagen real, limpia texto HTML y conserva vídeo sin intentar cargarlo como imagen. No se ejecuta HTML. Las actualizaciones mantienen los favoritos existentes.

Fuente oficial: https://api.nasa.gov/assets/files/apod/APOD-APIs-and-RSS-info.pdf

- 17 pruebas unitarias versionadas y 5 de contrato en laboratorio: 22/22 sin fallos. Una consulta usa Retrofit real contra NASA; el resto comprueba mapeo, datos incompletos y regresiones.
- Las siete imágenes reales devolvieron HTTP 200 e image/jpeg; una se abrió con visor de imágenes.
- APK 1.1 generado, lint sin errores y AndroidTest compilado. La instalación/visualización de esta actualización en el teléfono queda por confirmar.
- Las pruebas de contrato con respuestas de NASA son evidencia de laboratorio; no incorporan claves ni fixtures con enlaces autenticados al repositorio.
