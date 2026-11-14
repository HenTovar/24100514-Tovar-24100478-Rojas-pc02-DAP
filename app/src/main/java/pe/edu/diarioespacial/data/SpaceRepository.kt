package pe.edu.diarioespacial.data
import pe.edu.diarioespacial.BuildConfig
class SpaceRepository(private val api: NasaApi, val db: SpaceDatabase) {
 suspend fun refreshImages() { val (start,end)=week(); checkKey(); db.apods().save(api.apods(BuildConfig.NASA_API_KEY,start,end).mapNotNull { it.toEntity() }) }
 suspend fun refreshAsteroids() { val (start,end)=week(); checkKey(); db.asteroids().save(api.feed(BuildConfig.NASA_API_KEY,start,end).toEntities()) }
 private fun checkKey() { check(BuildConfig.NASA_API_KEY.isNotBlank() && BuildConfig.NASA_API_KEY != "DEMO_KEY") { "Configura NASA_API_KEY propia en local.properties y recompila." } }
}
