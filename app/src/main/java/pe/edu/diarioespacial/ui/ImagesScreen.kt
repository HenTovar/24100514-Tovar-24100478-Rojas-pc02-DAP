package pe.edu.diarioespacial.ui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import pe.edu.diarioespacial.SpaceViewModel
import pe.edu.diarioespacial.data.ApodEntity
@Composable fun ImagesScreen(vm: SpaceViewModel,favorites: Boolean,onAction: ()->Unit,onOpen: (String)->Unit) {
 val rows by (if(favorites) vm.favoriteImages else vm.images).collectAsStateWithLifecycle()
 val loading by vm.loading.collectAsStateWithLifecycle(); val errors by vm.errors.collectAsStateWithLifecycle()
 LaunchedEffect(favorites) { if(!favorites) vm.refresh("images") }
 Column {
  Header(if(favorites) "Mis imágenes favoritas" else "Imágenes",if(favorites) "Volver" else "Favoritas",onAction)
  if(!favorites) Status("images" in loading,errors["images"]){vm.refresh("images",true)}
  if(rows.isEmpty()) Text(if(favorites) "No hay imágenes favoritas." else "No hay imágenes guardadas.",Modifier.padding(16.dp))
  LazyColumn { items(rows,key={it.date}) { item -> Card(Modifier.fillMaxWidth().padding(8.dp)) { Column(Modifier.padding(12.dp)) {
   Column(Modifier.fillMaxWidth().clickable { onOpen(item.date) }) { ApodMedia(item,false); Text(item.title,style=MaterialTheme.typography.titleMedium);Text(item.date) }
   Favorite(item.favorite){vm.toggleImage(item.date)}
  } } } }
 }
}
@Composable private fun ApodMedia(item: ApodEntity,detail: Boolean) {
 if(item.mediaType=="image") {
  val url=if(detail) item.hdurl?.takeIf { it.isNotBlank() } ?: item.url else item.url
  if(url.isNullOrBlank()) Text("Imagen no disponible") else SubcomposeAsyncImage(model=url,contentDescription=item.title,modifier=Modifier.fillMaxWidth().height(if(detail) 320.dp else 190.dp),contentScale=ContentScale.Fit,loading={CircularProgressIndicator(Modifier.size(32.dp))},error={Text("No se pudo descargar la imagen. Revisa tu conexión e inténtalo de nuevo.")})
 } else Text(if(item.mediaType=="video") "Contenido en video" else "Contenido no disponible")
}
@Composable fun ImageDetail(vm: SpaceViewModel,date: String,onBack: ()->Unit) {
 val item by remember(date) { vm.image(date) }.collectAsStateWithLifecycle(initialValue=null)
 Column { Header("Detalle de imagen","Volver",onBack)
  val a=item
  if(a==null) Text("Imagen no encontrada",Modifier.padding(16.dp)) else LazyColumn(contentPadding=PaddingValues(16.dp)) { item {
   ApodMedia(a,true);Text(a.title,style=MaterialTheme.typography.headlineSmall);Text(a.date);Favorite(a.favorite){vm.toggleImage(a.date)};Text(a.explanation);a.copyright?.takeIf { it.isNotBlank() }?.let { Text("Autor: $it",Modifier.padding(top=12.dp)) }
  } }
 }
}
