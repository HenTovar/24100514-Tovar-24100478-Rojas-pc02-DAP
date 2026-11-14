package pe.edu.diarioespacial
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import pe.edu.diarioespacial.ui.*
class MainActivity: ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme(colorScheme=darkColorScheme()) { SpaceApp() } } }
}
@Composable fun SpaceApp(vm: SpaceViewModel=viewModel()) {
 val nav=rememberNavController(); val entry by nav.currentBackStackEntryAsState(); val route=entry?.destination?.route.orEmpty()
 Scaffold(bottomBar={ NavigationBar {
  listOf("images" to "Imágenes","asteroids" to "Asteroides").forEach { (dest,label) ->
   NavigationBarItem(selected=route.startsWith(dest),onClick={nav.navigate(dest){popUpTo(nav.graph.startDestinationId){saveState=true};launchSingleTop=true;restoreState=true}},icon={Text(if(dest=="images") "☀" else "●")},label={Text(label)})
  }
 } }) { padding ->
  Box(Modifier.fillMaxSize().padding(padding)) { NavHost(nav,startDestination="images") {
   composable("images") { ImagesScreen(vm,false,{nav.navigate("images/favorites")},{nav.navigate("images/detail/$it")}) }
   composable("images/favorites") { ImagesScreen(vm,true,{nav.popBackStack()},{nav.navigate("images/detail/$it")}) }
   composable("images/detail/{date}") { ImageDetail(vm,it.arguments?.getString("date").orEmpty()){nav.popBackStack()} }
   composable("asteroids") { AsteroidsScreen(vm,false,{nav.navigate("asteroids/favorites")},{id,date->nav.navigate("asteroids/detail/${android.net.Uri.encode(id)}/$date")}) }
   composable("asteroids/favorites") { AsteroidsScreen(vm,true,{nav.popBackStack()},{id,date->nav.navigate("asteroids/detail/${android.net.Uri.encode(id)}/$date")}) }
   composable("asteroids/detail/{id}/{date}") { AsteroidDetail(vm,it.arguments?.getString("id").orEmpty(),it.arguments?.getString("date").orEmpty()){nav.popBackStack()} }
  } }
 }
}
