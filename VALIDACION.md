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
