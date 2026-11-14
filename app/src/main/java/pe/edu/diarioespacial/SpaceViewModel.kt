package pe.edu.diarioespacial
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.diarioespacial.data.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class SpaceViewModel(app: Application): AndroidViewModel(app) {
 private val repo=SpaceRepository(Retrofit.Builder().baseUrl("https://api.nasa.gov/").addConverterFactory(GsonConverterFactory.create()).build().create(NasaApi::class.java),Room.databaseBuilder(app,SpaceDatabase::class.java,"diario.db").build())
 private val range=week()
 val images=repo.db.apods().recent(range.first,range.second).stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val favoriteImages=repo.db.apods().favorites().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val asteroids=repo.db.asteroids().recent(range.first,range.second).stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val favoriteAsteroids=repo.db.asteroids().favorites().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 private val _loading=MutableStateFlow<Set<String>>(emptySet()); val loading=_loading.asStateFlow()
 private val _errors=MutableStateFlow<Map<String,String>>(emptyMap()); val errors=_errors.asStateFlow()
 private val loaded=mutableSetOf<String>()
 fun refresh(kind: String,force: Boolean=false) {
  if (kind in _loading.value || (!force && kind in loaded)) return
  viewModelScope.launch {
   _loading.update { it+kind }; _errors.update { it-kind }
   try { if(kind=="images") repo.refreshImages() else repo.refreshAsteroids(); loaded.add(kind) }
   catch(e: CancellationException) { throw e }
   catch(e: Exception) { _errors.update { it+(kind to if(BuildConfig.NASA_API_KEY.isBlank()) "Falta NASA_API_KEY en local.properties. Consulta el README." else "No se pudo actualizar. Se conservan los datos locales. Revisa conexión, clave o cuota y reintenta.") } }
   finally { _loading.update { it-kind } }
  }
 }
 fun image(date: String)=repo.db.apods().one(date)
 fun asteroid(id: String,date: String)=repo.db.asteroids().one(id,date)
 fun toggleImage(date: String) { viewModelScope.launch { repo.db.apods().toggle(date) } }
 fun toggleAsteroid(id: String,date: String) { viewModelScope.launch { repo.db.asteroids().toggle(id,date) } }
}
