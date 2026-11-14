package pe.edu.diarioespacial.data
import org.jsoup.Jsoup
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Official NASA APOD Basic response: url is an article, hdurl is the actual image. */
data class ApodBasicDto(val date: String?=null,val title: String?=null,val explanation: String?=null,val media_type: String?=null,val hdurl: String?=null,val copyright: String?=null)
interface ApodBasicApi {
 @GET("wp-json/wp/v2/apod-basic") suspend fun apods(@Query("date_from") start: String,@Query("date_to") end: String,@Query("per_page") pageSize: Int=25): List<ApodBasicDto>
}
fun legacyDate(date: String): String=LocalDate.parse(date).format(DateTimeFormatter.ofPattern("yyMMdd"))
fun plainText(html: String?): String=Jsoup.parse(html.orEmpty()).text()
private fun imageUrl(value: String?): String? = value?.takeIf { candidate ->
 try { val u=URI(candidate);u.scheme=="https" && !u.host.isNullOrBlank() && !candidate.contains("nasa-logo",ignoreCase=true) } catch(_: Exception) { false }
}
fun ApodBasicDto.toEntity(): ApodEntity? {
 if(!validDate(date)) return null
 val type=when(media_type) { "video","iframe" -> "video";"image" -> "image";else -> "unknown" }
 val image=if(type=="image") imageUrl(hdurl) else null
 return ApodEntity(date!!,plainText(title).ifBlank { "Sin título" },plainText(explanation).ifBlank { "Sin explicación disponible" },image,image,type,plainText(copyright).takeIf { it.isNotBlank() })
}
fun ApodEntity.isPlaceholder(): Boolean=title.trim()=="NASA Science" || listOfNotNull(url,hdurl).any { it.contains("nasa-logo",ignoreCase=true) }
