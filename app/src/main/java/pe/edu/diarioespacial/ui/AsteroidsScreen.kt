package pe.edu.diarioespacial.ui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.edu.diarioespacial.SpaceViewModel
import pe.edu.diarioespacial.data.AsteroidEntity
import pe.edu.diarioespacial.data.numberWithUnit
@Composable fun AsteroidsScreen(vm: SpaceViewModel,favorites: Boolean,onAction: ()->Unit,onOpen: (String,String)->Unit) {
 val rows by (if(favorites) vm.favoriteAsteroids else vm.asteroids).collectAsStateWithLifecycle()
 val loading by vm.loading.collectAsStateWithLifecycle(); val errors by vm.errors.collectAsStateWithLifecycle()
 var hazardous by rememberSaveable { mutableStateOf(false) }
 LaunchedEffect(favorites) { if(!favorites) vm.refresh("asteroids") }
 val visible=if(hazardous) rows.filter { it.hazardous } else rows
 Column {
  Header(if(favorites) "Mis asteroides" else "Asteroides",if(favorites) "Volver" else "Favoritos",onAction)
  Row(Modifier.fillMaxWidth().padding(horizontal=12.dp)) { Text("Solo potencialmente peligrosos",Modifier.weight(1f)); Switch(checked=hazardous,onCheckedChange={hazardous=it}) }
  if(!favorites) Status("asteroids" in loading,errors["asteroids"]){vm.refresh("asteroids",true)}
  if(visible.isEmpty()) Text("No hay asteroides para mostrar.",Modifier.padding(16.dp))
  LazyColumn { items(visible,key={it.id+":"+it.date}) { a -> Card(Modifier.fillMaxWidth().padding(8.dp)) { Column(Modifier.padding(12.dp)) {
   Column(Modifier.fillMaxWidth().clickable {onOpen(a.id,a.date)}) { Text(a.name,style=MaterialTheme.typography.titleMedium);AsteroidValues(a,false) }
   Favorite(a.favorite){vm.toggleAsteroid(a.id,a.date)}
  } } } }
 }
}
@Composable private fun AsteroidValues(a: AsteroidEntity,detail: Boolean) {
 Text("Aproximación: ${a.date}")
 if(detail) { Text("ID: ${a.id}");Text("Diámetro mínimo: ${numberWithUnit(a.minMeters,"m")}") }
 Text("Diámetro máximo: ${numberWithUnit(a.maxMeters,"m")}");Text("Velocidad: ${numberWithUnit(a.speedKmh,"km/h")}");Text("Distancia de paso: ${numberWithUnit(a.distanceKm,"km")}")
 if(a.hazardous) Text("⚠ Potencialmente peligroso",color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.labelLarge) else if(detail) Text("No potencialmente peligroso")
}
@Composable fun AsteroidDetail(vm: SpaceViewModel,id: String,date: String,onBack: ()->Unit) {
 val item by remember(id,date) { vm.asteroid(id,date) }.collectAsStateWithLifecycle(initialValue=null)
 Column { Header("Detalle de asteroide","Volver",onBack);val a=item
  if(a==null) Text("Asteroide no encontrado",Modifier.padding(16.dp)) else LazyColumn(contentPadding=PaddingValues(16.dp)) { item {Text(a.name,style=MaterialTheme.typography.headlineSmall);AsteroidValues(a,true);Favorite(a.favorite){vm.toggleAsteroid(a.id,a.date)}} }
 }
}
