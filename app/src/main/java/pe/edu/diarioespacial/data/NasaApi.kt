package pe.edu.diarioespacial.data
import retrofit2.http.GET
import retrofit2.http.Query
interface NasaApi {
 @GET("planetary/apod") suspend fun apods(@Query("api_key") key: String,@Query("start_date") start: String,@Query("end_date") end: String): List<ApodDto>
 @GET("neo/rest/v1/feed") suspend fun feed(@Query("api_key") key: String,@Query("start_date") start: String,@Query("end_date") end: String): FeedDto
}
