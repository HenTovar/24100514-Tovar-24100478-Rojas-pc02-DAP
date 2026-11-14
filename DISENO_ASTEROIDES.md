# Asteroides: actualización visual 1.2

## Cambios
- Cabecera espacial con ilustración vectorial decorativa (no es fotografía ni órbita medida).
- Resumen de objetos visibles, clasificación de peligro y favoritos de la lista.
- Tarjetas con nombre/fecha, clasificación explícita, diámetro, velocidad y distancia agrupados.
- Acceso a favoritos con estrella, etiqueta accesible y estado seleccionado.
- Filtro con área de pulsación completa y contenido que puede desplazarse en pantallas pequeñas.
- Detalle con medidas separadas y botón de guardar/quitar.
- Estados de carga, error y lista vacía conservados.

## Alcance
Sólo cambia `ui/AsteroidsScreen.kt`, además de incrementar la versión a 1.2 para instalar como actualización. Los ocho archivos comprobados de datos/API, ViewModel, navegación e Imágenes son byte-idénticos a main. No cambia el esquema Room ni se borran favoritos.

## Validación
- Compilación del APK debug y AndroidTest: correctas.
- 17 pruebas unitarias versionadas + 5 pruebas de contrato en laboratorio: 22 sin fallos.
- Lint sin errores. Las pruebas de negocio no sustituyen una revisión visual.
- Contrastes calculados para los colores principales de texto: mínimo 7,69:1 en las parejas comprobadas.
- Previews Compose incluidas para lista y detalle. No se afirma renderizado o ejecución de esta UI en emulador/teléfono desde el laboratorio.
- Revisión final en el teléfono: comprobar diseño, estrella sin abrir el detalle, filtro, navegación y favoritos conservados.
