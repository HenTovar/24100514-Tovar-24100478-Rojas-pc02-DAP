package pe.edu.diarioespacial.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import pe.edu.diarioespacial.SpaceViewModel
import pe.edu.diarioespacial.data.ApodEntity

private val GalleryBackground = Color(0xFF0B1020)
private val GallerySurface = Color(0xFF151E32)
private val GalleryBorder = Color(0xFF33415C)
private val GalleryText = Color(0xFFF3F5FC)
private val GalleryMuted = Color(0xFFB6C3D9)
private val GalleryAccent = Color(0xFF8DE5DA)

@Composable
fun ImagesScreen(vm: SpaceViewModel, favorites: Boolean, onAction: () -> Unit, onOpen: (String) -> Unit) {
    val rows by (if (favorites) vm.favoriteImages else vm.images).collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val errors by vm.errors.collectAsStateWithLifecycle()
    LaunchedEffect(favorites) { if (!favorites) vm.refresh("images") }
    ImagesContent(rows, favorites, !favorites && "images" in loading,
        if (favorites) null else errors["images"], onAction, onOpen,
        { vm.toggleImage(it.date) }, { vm.refresh("images", true) })
}

@Composable
private fun ImagesContent(
    rows: List<ApodEntity>, favorites: Boolean, loading: Boolean, error: String?,
    onAction: () -> Unit, onOpen: (String) -> Unit, onFavorite: (ApodEntity) -> Unit, onRetry: () -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize().background(GalleryBackground), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("DIARIO ESPACIAL", color = GalleryAccent, fontSize = 13.sp, letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onAction, border = BorderStroke(1.dp, GalleryBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GalleryText)) {
                    Text(if (favorites) "Volver" else "Favoritas")
                }
            }
        }
        item { GalleryHero(if (favorites) "Mis favoritas" else "Imágenes",
            if (favorites) "Tu pequeño archivo del universo" else "Una ventana al universo · últimos 7 días") }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GalleryStat("Fotos", rows.count { it.mediaType == "image" }, Modifier.weight(1f))
                GalleryStat("Vídeos", rows.count { it.mediaType == "video" }, Modifier.weight(1f))
                GalleryStat("Favoritas", rows.count { it.favorite }, Modifier.weight(1f))
            }
        }
        if (loading) item { LinearProgressIndicator(Modifier.fillMaxWidth(), color = GalleryAccent) }
        if (error != null) item {
            Surface(color = Color(0xFF38272B), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(error, color = GalleryText, fontSize = 15.sp)
                    TextButton(onClick = onRetry) { Text("Reintentar", color = GalleryAccent) }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(if (favorites) "TU COLECCIÓN" else "DESCUBRIMIENTOS RECIENTES", color = GalleryMuted,
                    fontSize = 13.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("${rows.size}", color = GalleryAccent, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
        if (rows.isEmpty()) item {
            Surface(color = GallerySurface, shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, GalleryBorder)) {
                Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (favorites) "Guarda lo que te inspire" else "El universo está por aparecer", color = GalleryText,
                        fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (favorites) "Toca la estrella de una imagen para encontrarla aquí."
                        else if (loading) "Consultando las publicaciones de NASA…" else "Aún no hay imágenes guardadas. Puedes reintentar la actualización.",
                        color = GalleryMuted, fontSize = 15.sp)
                    if (!favorites && !loading && error == null) TextButton(onClick = onRetry) { Text("Actualizar", color = GalleryAccent) }
                }
            }
        }
        items(rows, key = { it.date }) { image ->
            ImageCard(image, { onOpen(image.date) }, { onFavorite(image) })
        }
    }
}

@Composable
private fun GalleryHero(title: String, subtitle: String) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF1B3048), Color(0xFF24203E))))) {
        Canvas(Modifier.align(Alignment.CenterEnd).size(145.dp)) {
            val center = Offset(size.width * .72f, size.height * .43f)
            drawCircle(GalleryAccent.copy(alpha = .08f), size.width * .33f, center)
            drawCircle(GalleryAccent.copy(alpha = .17f), size.width * .23f, center, style = Stroke(1.5.dp.toPx()))
            drawCircle(Color(0xFFA69EE7).copy(alpha = .17f), size.width * .16f, center)
            listOf(.16f to .22f, .35f to .68f, .62f to .83f, .88f to .17f).forEach { (x, y) ->
                drawCircle(GalleryText.copy(alpha = .45f), 1.5.dp.toPx(), Offset(size.width * x, size.height * y))
            }
        }
        Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("ASTRONOMÍA DEL DÍA", color = GalleryAccent, fontSize = 13.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
            Text(title, color = GalleryText, fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = GalleryMuted, fontSize = 15.sp, modifier = Modifier.fillMaxWidth(.8f))
        }
    }
}

@Composable
private fun GalleryStat(label: String, value: Int, modifier: Modifier) {
    Surface(modifier, color = GallerySurface, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(value.toString(), color = GalleryAccent, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text(label, color = GalleryMuted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ImageCard(image: ApodEntity, onOpen: () -> Unit, onFavorite: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clickable(onClick = onOpen), color = GallerySurface,
        shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, GalleryBorder)) {
        Column {
            Box {
                ApodMedia(image, detail = false)
                Surface(Modifier.align(Alignment.TopStart).padding(12.dp), color = Color(0xE60B1020), shape = RoundedCornerShape(10.dp)) {
                    Text(image.date, color = GalleryText, fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                }
                FilledTonalIconToggleButton(checked = image.favorite, onCheckedChange = { onFavorite() },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).semantics {
                        contentDescription = if (image.favorite) "Quitar ${image.title} de favoritas" else "Guardar ${image.title} en favoritas"
                    }, colors = IconButtonDefaults.filledTonalIconToggleButtonColors(
                        containerColor = Color(0xE6151E32), checkedContainerColor = Color(0xFF264B4B),
                        contentColor = GalleryText, checkedContentColor = GalleryAccent)) {
                    Text(if (image.favorite) "★" else "☆", fontSize = 24.sp, modifier = Modifier.clearAndSetSemantics {})
                }
            }
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (image.mediaType == "video") "VÍDEO · NASA APOD" else "NASA · APOD", color = GalleryAccent,
                    fontSize = 13.sp, letterSpacing = .8.sp, fontWeight = FontWeight.SemiBold)
                Text(image.title, color = GalleryText, fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold)
                HorizontalDivider(color = GalleryBorder)
                Text("Explorar publicación  ›", color = GalleryAccent, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun ApodMedia(image: ApodEntity, detail: Boolean) {
    val modifier = Modifier.fillMaxWidth().aspectRatio(if (detail) 1f else 1.6f).background(Color(0xFF080D18))
    Box(modifier, contentAlignment = Alignment.Center) {
        if (image.mediaType == "image") {
            val url = if (detail) image.hdurl?.takeIf { it.isNotBlank() } ?: image.url else image.url
            if (url.isNullOrBlank()) MediaMessage("Imagen no disponible", "Puedes consultar la explicación en el detalle.")
            else SubcomposeAsyncImage(model = url, contentDescription = image.title, modifier = Modifier.fillMaxSize(),
                contentScale = if (detail) ContentScale.Fit else ContentScale.Crop,
                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(32.dp), color = GalleryAccent)
                } },
                error = { MediaMessage("No se pudo cargar la imagen", "Revisa tu conexión y vuelve a abrir la publicación.") })
        } else {
            MediaMessage(if (image.mediaType == "video") "Contenido en video" else "Contenido no disponible",
                if (image.mediaType == "video") "La explicación está disponible en el detalle." else "Consulta la información de esta publicación.")
        }
    }
}

@Composable
private fun MediaMessage(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = GalleryText, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text(subtitle, color = GalleryMuted, fontSize = 14.sp)
    }
}

@Composable
fun ImageDetail(vm: SpaceViewModel, date: String, onBack: () -> Unit) {
    val image by remember(date) { vm.image(date) }.collectAsStateWithLifecycle(initialValue = null)
    ImageDetailContent(image, onBack) { vm.toggleImage(it.date) }
}

@Composable
private fun ImageDetailContent(image: ApodEntity?, onBack: () -> Unit, onFavorite: (ApodEntity) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().background(GalleryBackground), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { TextButton(onClick = onBack) { Text("‹  Volver a imágenes", color = GalleryAccent) } }
        val a = image
        if (a == null) item { Text("Imagen no encontrada", color = GalleryText, fontSize = 16.sp) }
        else {
            item {
                Surface(color = GallerySurface, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, GalleryBorder)) {
                    ApodMedia(a, detail = true)
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("NASA APOD · ${a.date}", color = GalleryAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(a.title, color = GalleryText, fontSize = 28.sp, lineHeight = 35.sp, fontWeight = FontWeight.Bold)
                }
            }
            item {
                Button(onClick = { onFavorite(a) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GalleryAccent, contentColor = GalleryBackground),
                    shape = RoundedCornerShape(16.dp)) {
                    Text(if (a.favorite) "★  Quitar de mis favoritas" else "☆  Guardar en mis favoritas", fontSize = 15.sp)
                }
            }
            item {
                Surface(color = GallerySurface, shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, GalleryBorder)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("La historia detrás", color = GalleryText, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
                        Text(a.explanation, color = GalleryMuted, fontSize = 16.sp, lineHeight = 26.sp)
                    }
                }
            }
            a.copyright?.takeIf { it.isNotBlank() }?.let { author ->
                item {
                    Column(Modifier.padding(horizontal = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("AUTOR / CRÉDITOS", color = GalleryAccent, fontSize = 13.sp, letterSpacing = .8.sp)
                        Text(author, color = GalleryMuted, fontSize = 14.sp, lineHeight = 21.sp)
                    }
                }
            }
        }
    }
}

private val GalleryPreview = listOf(ApodEntity("2026-11-14", "Una ventana al universo", "La explicación completa de esta publicación aparece en el detalle.", null, null, "image", "NASA", true))
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable private fun ImagesDesignPreview() { MaterialTheme(colorScheme = darkColorScheme()) {
    ImagesContent(GalleryPreview, false, false, null, {}, {}, {}, {})
} }
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable private fun ImageDetailDesignPreview() { MaterialTheme(colorScheme = darkColorScheme()) {
    ImageDetailContent(GalleryPreview[0], {}) {}
} }
