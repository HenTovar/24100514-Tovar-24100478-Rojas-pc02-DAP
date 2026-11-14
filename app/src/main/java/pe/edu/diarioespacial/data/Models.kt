package pe.edu.diarioespacial.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.util.Locale

@Entity(tableName = "apods")
data class ApodEntity(@PrimaryKey val date: String, val title: String, val explanation: String, val url: String?, val hdurl: String?, val mediaType: String, val copyright: String?, val favorite: Boolean = false)
@Entity(tableName = "asteroids", primaryKeys = ["id", "date"])
data class AsteroidEntity(val id: String, val date: String, val name: String, val hazardous: Boolean, val minMeters: Double?, val maxMeters: Double?, val speedKmh: Double?, val distanceKm: Double?, val favorite: Boolean = false)
data class ApodDto(val date: String? = null, val title: String? = null, val explanation: String? = null, val url: String? = null, val hdurl: String? = null, @SerializedName("media_type") val mediaType: String? = null, val copyright: String? = null)
data class FeedDto(@SerializedName("near_earth_objects") val objects: Map<String, List<NeoDto>?>? = null)
data class NeoDto(val id: String? = null, val name: String? = null, @SerializedName("is_potentially_hazardous_asteroid") val hazardous: Boolean? = null, @SerializedName("estimated_diameter") val diameter: DiameterDto? = null, @SerializedName("close_approach_data") val approaches: List<ApproachDto>? = null)
data class DiameterDto(val meters: MetersDto? = null)
data class MetersDto(@SerializedName("estimated_diameter_min") val min: Double? = null, @SerializedName("estimated_diameter_max") val max: Double? = null)
data class ApproachDto(@SerializedName("close_approach_date") val date: String? = null, @SerializedName("relative_velocity") val velocity: VelocityDto? = null, @SerializedName("miss_distance") val distance: DistanceDto? = null)
data class VelocityDto(@SerializedName("kilometers_per_hour") val kmh: String? = null)
data class DistanceDto(val kilometers: String? = null)
fun validDate(s: String?): Boolean = try { s != null && LocalDate.parse(s).toString() == s } catch (_: Exception) { false }
fun ApodDto.toEntity(): ApodEntity? = if (!validDate(date)) null else ApodEntity(date!!, title?.takeIf { it.isNotBlank() } ?: "Sin título", explanation ?: "Sin explicación disponible", url, hdurl, mediaType ?: "unknown", copyright)
fun FeedDto.toEntities(): List<AsteroidEntity> = objects.orEmpty().flatMap { (day, items) ->
 if (!validDate(day)) emptyList() else items.orEmpty().mapNotNull { neo ->
  neo.id?.takeIf { it.isNotBlank() }?.let { id ->
   val approach = neo.approaches.orEmpty().firstOrNull { it.date == day }
   AsteroidEntity(id, day, neo.name ?: "Sin nombre", neo.hazardous ?: false, neo.diameter?.meters?.min, neo.diameter?.meters?.max, approach?.velocity?.kmh?.toDoubleOrNull()?.takeIf { it.isFinite() }, approach?.distance?.kilometers?.toDoubleOrNull()?.takeIf { it.isFinite() })
  }
 }
}.distinctBy { it.id to it.date }.sortedByDescending { it.date }
fun numberWithUnit(n: Double?, unit: String): String = if (n == null || !n.isFinite()) "No disponible" else "${String.format(Locale.getDefault(), "%.2f", n)} $unit"
fun week(today: LocalDate = LocalDate.now()): Pair<String, String> = today.minusDays(6).toString() to today.toString()
