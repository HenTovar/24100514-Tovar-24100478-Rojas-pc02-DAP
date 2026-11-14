package pe.edu.diarioespacial.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable fun Header(title: String,action: String,onAction: ()->Unit) { Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween) { Text(title,style=MaterialTheme.typography.titleLarge,modifier=Modifier.weight(1f));TextButton(onClick=onAction){Text(action)} } }
@Composable fun Favorite(active: Boolean,onClick: ()->Unit) { TextButton(onClick=onClick){Text(if(active) "★ Quitar de favoritos" else "☆ Añadir a favoritos")} }
@Composable fun Status(loading: Boolean,error: String?,retry: ()->Unit) { if(loading) LinearProgressIndicator(Modifier.fillMaxWidth()); if(error!=null) { Text(error,Modifier.padding(12.dp)); TextButton(onClick=retry){Text("Reintentar")} } }
