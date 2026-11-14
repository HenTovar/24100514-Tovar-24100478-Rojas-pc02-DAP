package pe.edu.diarioespacial.data
import pe.edu.diarioespacial.BuildConfig
import java.io.IOException
import retrofit2.HttpException
class SpaceRepository(private val api: NasaApi, val db: SpaceDatabase,private val currentApod: ApodBasicApi) {
 suspend fun refreshImages() {
  val (start,end)=week(); checkKey()
  val legacy=try {api.apods(BuildConfig.NASA_API_KEY,start,end).mapNotNull {it.toEntity()}} catch(_: IOException) {emptyList()} catch(_: HttpException) {emptyList()}
  val rows=if(legacy.isEmpty() || legacy.any {it.isPlaceholder()}) currentApod.apods(legacyDate(start),legacyDate(end)).mapNotNull {it.toEntity()} else legacy
  check(rows.isNotEmpty() && rows.none {it.isPlaceholder()}) { "NASA no devolvió imágenes astronómicas válidas." }
  db.apods().save(rows.filter {it.date>=start && it.date<=end})
 }
 suspend fun refreshAsteroids() { val (start,end)=week(); checkKey(); db.asteroids().save(api.feed(BuildConfig.NASA_API_KEY,start,end).toEntities()) }
 private fun checkKey() { check(BuildConfig.NASA_API_KEY.isNotBlank() && BuildConfig.NASA_API_KEY != "DEMO_KEY") { "Configura NASA_API_KEY propia en local.properties y recompila." } }
}
