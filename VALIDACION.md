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
