package pe.edu.diarioespacial.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.edu.diarioespacial.SpaceViewModel
import pe.edu.diarioespacial.data.AsteroidEntity
import pe.edu.diarioespacial.data.numberWithUnit

private val SpaceBackground = Color(0xFF0B1020)
private val SpaceSurface = Color(0xFF151E32)
private val SpaceBorder = Color(0xFF33415C)
private val SpaceText = Color(0xFFF3F5FC)
private val SpaceMuted = Color(0xFFB6C3D9)
private val SpaceAccent = Color(0xFF8DE5DA)
private val SpaceWarning = Color(0xFFFFB7A3)

@Composable
fun AsteroidsScreen(vm: SpaceViewModel, favorites: Boolean, onAction: () -> Unit, onOpen: (String, String) -> Unit) {
    val rows by (if (favorites) vm.favoriteAsteroids else vm.asteroids).collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val errors by vm.errors.collectAsStateWithLifecycle()
    var hazardous by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(favorites) { if (!favorites) vm.refresh("asteroids") }
    AsteroidsContent(rows, favorites, hazardous, { hazardous = it }, "asteroids" in loading,
        if (favorites) null else errors["asteroids"], onAction, onOpen,
        { vm.toggleAsteroid(it.id, it.date) }, { vm.refresh("asteroids", true) })
}

@Composable
private fun AsteroidsContent(
    rows: List<AsteroidEntity>, favorites: Boolean, hazardous: Boolean,
    onFilter: (Boolean) -> Unit, loading: Boolean, error: String?, onAction: () -> Unit,
    onOpen: (String, String) -> Unit, onFavorite: (AsteroidEntity) -> Unit, onRetry: () -> Unit,
) {
    val visible = if (hazardous) rows.filter { it.hazardous } else rows
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(SpaceBackground),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("DIARIO ESPACIAL", color = SpaceAccent, fontSize = 13.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onAction, border = BorderStroke(1.dp, SpaceBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SpaceText)) {
                    Text(if (favorites) "Volver" else "Mis asteroides")
                }
            }
        }
        item { OrbitHero(if (favorites) "Mis asteroides" else "Asteroides",
            if (favorites) "Tu colección, siempre a mano" else "Objetos cercanos a la Tierra · últimos 7 días") }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryStat("Visibles", visible.size, Modifier.weight(1f))
                SummaryStat("Peligrosos", rows.count { it.hazardous }, Modifier.weight(1f), SpaceWarning)
                SummaryStat("Favoritos", rows.count { it.favorite }, Modifier.weight(1f))
            }
        }
        item {
            Surface(color = SpaceSurface, shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, SpaceBorder)) {
                Row(Modifier.fillMaxWidth().toggleable(value = hazardous, role = Role.Switch, onValueChange = onFilter)
                    .padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Solo potencialmente peligrosos", color = SpaceText, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("Filtrar clasificación NASA", color = SpaceMuted, fontSize = 13.sp)
                    }
                    Switch(checked = hazardous, onCheckedChange = null,
                        colors = SwitchDefaults.colors(checkedTrackColor = SpaceAccent, checkedThumbColor = SpaceBackground))
                }
            }
        }
        if (!favorites && loading) item { LinearProgressIndicator(Modifier.fillMaxWidth(), color = SpaceAccent) }
        if (error != null) item {
            Surface(color = Color(0xFF38272B), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(error, color = SpaceText)
                    TextButton(onClick = onRetry) { Text("Reintentar", color = SpaceAccent) }
                }
            }
        }
        item {
            Text(if (favorites) "TU COLECCIÓN" else "APROXIMACIONES RECIENTES", color = SpaceMuted,
                fontSize = 13.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
        }
        if (visible.isEmpty()) item {
            Surface(color = SpaceSurface, shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(24.dp)) {
                    Text(if (hazardous) "Sin coincidencias" else if (favorites) "Tu colección empieza aquí" else "Aún no hay asteroides",
                        color = SpaceText, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(if (hazardous) "Desactiva el filtro para ver los demás objetos."
                        else if (favorites) "Marca la estrella de un asteroide para guardarlo aquí."
                        else if (loading) "Consultando las aproximaciones de los últimos siete días…"
                        else "Cuando haya datos, aparecerán aquí ordenados por fecha.", color = SpaceMuted, fontSize = 15.sp)
                }
            }
        }
        items(visible, key = { it.id + ":" + it.date }) { a ->
            AsteroidCard(a, { onOpen(a.id, a.date) }, { onFavorite(a) })
        }
    }
}

@Composable
private fun OrbitHero(title: String, subtitle: String) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF1B3048), Color(0xFF24203E))))) {
        OrbitDrawing(Modifier.align(Alignment.CenterEnd).size(150.dp))
        Column(Modifier.fillMaxWidth().padding(22.dp)) {
            Text("EXPLORA EL SISTEMA SOLAR", color = SpaceAccent, fontSize = 13.sp,
                letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Text(title, color = SpaceText, fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(subtitle, color = SpaceMuted, fontSize = 15.sp, modifier = Modifier.fillMaxWidth(.8f))
        }
    }
}

/** Decorative vector illustration, not a photograph or a measured orbit. */
@Composable
private fun OrbitDrawing(modifier: Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width * .62f, size.height * .5f)
        rotate(-28f, c) {
            drawOval(SpaceAccent.copy(alpha = .18f), Offset(c.x - size.width * .48f, c.y - size.height * .18f),
                Size(size.width * .96f, size.height * .36f), style = Stroke(1.5.dp.toPx()))
            drawOval(SpaceAccent.copy(alpha = .12f), Offset(c.x - size.width * .6f, c.y - size.height * .29f),
                Size(size.width * 1.2f, size.height * .58f), style = Stroke(1.dp.toPx()))
        }
        val rock = Path().apply {
            moveTo(c.x - 23.dp.toPx(), c.y - 12.dp.toPx()); lineTo(c.x - 9.dp.toPx(), c.y - 29.dp.toPx())
            lineTo(c.x + 20.dp.toPx(), c.y - 21.dp.toPx()); lineTo(c.x + 30.dp.toPx(), c.y + 4.dp.toPx())
            lineTo(c.x + 10.dp.toPx(), c.y + 26.dp.toPx()); lineTo(c.x - 20.dp.toPx(), c.y + 19.dp.toPx()); close()
        }
        drawPath(rock, Color(0xFF51647D).copy(alpha = .42f))
        drawPath(rock, SpaceMuted.copy(alpha = .28f), style = Stroke(1.dp.toPx()))
        drawCircle(SpaceBackground.copy(alpha = .45f), 6.dp.toPx(), c + Offset(8.dp.toPx(), -5.dp.toPx()))
        drawCircle(SpaceBackground.copy(alpha = .4f), 4.dp.toPx(), c + Offset(-9.dp.toPx(), 9.dp.toPx()))
        drawCircle(SpaceAccent.copy(alpha = .5f), 2.dp.toPx(), Offset(size.width * .25f, size.height * .26f))
    }
}

@Composable
private fun SummaryStat(label: String, value: Int, modifier: Modifier, accent: Color = SpaceAccent) {
    Surface(modifier, color = SpaceSurface, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(value.toString(), color = accent, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text(label, color = SpaceMuted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun AsteroidCard(a: AsteroidEntity, onOpen: () -> Unit, onFavorite: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen), color = SpaceSurface,
        shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, if (a.hazardous) Color(0xFF71504D) else SpaceBorder)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(a.name, color = SpaceText, fontSize = 21.sp, lineHeight = 27.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("Aproximación · ${a.date}", color = SpaceMuted, fontSize = 14.sp)
                }
                FilledTonalIconToggleButton(checked = a.favorite, onCheckedChange = { onFavorite() },
                    modifier = Modifier.semantics { contentDescription = if (a.favorite) "Quitar ${a.name} de favoritos" else "Guardar ${a.name} en favoritos" },
                    colors = IconButtonDefaults.filledTonalIconToggleButtonColors(
                        containerColor = Color(0xFF24334A), checkedContainerColor = Color(0xFF264B4B),
                        contentColor = SpaceMuted, checkedContentColor = SpaceAccent)) {
                    Text(if (a.favorite) "★" else "☆", fontSize = 24.sp,
                        modifier = Modifier.padding(2.dp).clearAndSetSemantics {})
                }
            }
            HazardBadge(a.hazardous)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Metric("DIÁMETRO MÁX.", numberWithUnit(a.maxMeters, "m"), Modifier.weight(1f))
                Metric("VELOCIDAD", numberWithUnit(a.speedKmh, "km/h"), Modifier.weight(1f))
            }
            HorizontalDivider(color = SpaceBorder)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("DISTANCIA DE PASO", color = SpaceMuted, fontSize = 13.sp, letterSpacing = .5.sp)
                    Text(numberWithUnit(a.distanceKm, "km"), color = SpaceText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
                Text("Ver detalle  ›", color = SpaceAccent, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun HazardBadge(hazardous: Boolean) {
    Surface(color = if (hazardous) Color(0xFF402D2E) else Color(0xFF223C40), shape = RoundedCornerShape(8.dp)) {
        Text(if (hazardous) "●  Potencialmente peligroso" else "●  No potencialmente peligroso",
            color = if (hazardous) SpaceWarning else SpaceAccent, fontSize = 13.sp,
            fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, color = SpaceMuted, fontSize = 13.sp, letterSpacing = .5.sp)
        Text(value, color = SpaceText, fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AsteroidDetail(vm: SpaceViewModel, id: String, date: String, onBack: () -> Unit) {
    val item by remember(id, date) { vm.asteroid(id, date) }.collectAsStateWithLifecycle(initialValue = null)
    AsteroidDetailContent(item, onBack) { a -> vm.toggleAsteroid(a.id, a.date) }
}

@Composable
private fun AsteroidDetailContent(item: AsteroidEntity?, onBack: () -> Unit, onFavorite: (AsteroidEntity) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().background(SpaceBackground), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { TextButton(onClick = onBack) { Text("‹  Volver a asteroides", color = SpaceAccent) } }
        val a = item
        if (a == null) item { Text("Asteroide no encontrado", color = SpaceText) }
        else {
            item { OrbitHero(a.name, "Objeto cercano a la Tierra · NEO ${a.id}") }
            item { HazardBadge(a.hazardous) }
            item {
                Surface(color = SpaceSurface, shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, SpaceBorder)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Metric("FECHA DE APROXIMACIÓN", a.date)
                        HorizontalDivider(color = SpaceBorder)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Metric("DIÁMETRO MÍNIMO", numberWithUnit(a.minMeters, "m"), Modifier.weight(1f))
                            Metric("DIÁMETRO MÁXIMO", numberWithUnit(a.maxMeters, "m"), Modifier.weight(1f))
                        }
                        Metric("VELOCIDAD RELATIVA", numberWithUnit(a.speedKmh, "km/h"))
                        Metric("DISTANCIA DE PASO", numberWithUnit(a.distanceKm, "km"))
                    }
                }
            }
            item {
                Button(onClick = { onFavorite(a) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpaceAccent, contentColor = SpaceBackground),
                    shape = RoundedCornerShape(16.dp)) {
                    Text(if (a.favorite) "★  Quitar de mis asteroides" else "☆  Guardar en mis asteroides", fontSize = 15.sp)
                }
            }
            item { Text("Datos de NASA · NeoWs", color = SpaceMuted, fontSize = 13.sp) }
        }
    }
}

private val PreviewAsteroids = listOf(
    AsteroidEntity("3542519", "2026-11-14", "(2010 PK9)", false, 120.0, 268.3, 24671.82, 6354998.12, true),
    AsteroidEntity("2468101", "2026-11-13", "(2001 FO32)", true, 440.0, 983.86, 89000.0, 2000000.0),
)
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable private fun AsteroidsDesignPreview() { MaterialTheme(colorScheme = darkColorScheme()) {
    AsteroidsContent(PreviewAsteroids, false, false, {}, false, null, {}, { _, _ -> }, {}, {})
} }
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable private fun AsteroidDetailDesignPreview() { MaterialTheme(colorScheme = darkColorScheme()) {
    AsteroidDetailContent(PreviewAsteroids[1], {}) {}
} }
