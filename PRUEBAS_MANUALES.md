# Revisión de la pareja antes de fusionar

Cada integrante revisa la rama del otro, deja al menos un comentario de revisión y aprueba sólo si lo comprobó. La implementación asistida no sustituye esa revisión.

## APOD (revisa Rojas)
1. Con clave NASA propia, abrir Imágenes. Verificar últimos siete días, fechas descendentes y tarjeta con título/fecha.
2. Abrir un detalle de imagen; comprobar explicación completa, autor si existe y URL de alta resolución cuando esté disponible.
3. Si el rango contiene vídeo, comprobar «Contenido en video» sin carga de imagen. Si no lo contiene, conservar la prueba unitaria de este caso y declararlo no observado en vivo.
4. Marcar desde lista; abrir detalle y comprobar la estrella. Desmarcar desde detalle; volver a lista y verificar consistencia.
5. Abrir Favoritas, reiniciar la app y comprobar persistencia. Refrescar datos y comprobar que no se pierden favoritos.

## Asteroides (revisa Tovar)
1. Consultar últimos siete días. Verificar fechas de aproximación descendentes y valores/unidades con máximo dos decimales.
2. Activar/desactivar el filtro. Ningún no-peligroso debe quedar visible cuando esté activo.
3. Abrir detalle: comprobar ID, diámetro mínimo/máximo, velocidad, distancia y clasificación.
4. Marcar/quitar desde lista y detalle; abrir Mis asteroides y reiniciar para comprobar persistencia.

## Ambos
- Durante actualización mostrar progreso; ante API sin datos mostrar mensaje.
- Desconectar red y reintentar: conservar datos ya guardados sin cierre inesperado. Una primera instalación sin red debe mostrar mensaje y permitir reintentar.
- Sin clave propia, mostrar configuración pendiente; no usar DEMO_KEY ni incorporar claves al repositorio.
- Verificar navegación inferior y botón Volver en ambas opciones.
- Ejecutar `connectedDebugAndroidTest` en teléfono/emulador para comprobar Room real.

Si NASA cambia el formato o falla, registrar el error real; no considerar un mock como evidencia de funcionamiento contra NASA.
