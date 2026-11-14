package pe.edu.diarioespacial.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface ApodDao {
 @Query("SELECT * FROM apods WHERE date BETWEEN :start AND :end ORDER BY date DESC") fun recent(start: String, end: String): Flow<List<ApodEntity>>
 @Query("SELECT * FROM apods WHERE favorite = 1 ORDER BY date DESC") fun favorites(): Flow<List<ApodEntity>>
 @Query("SELECT * FROM apods WHERE date = :date") fun one(date: String): Flow<ApodEntity?>
 @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(items: List<ApodEntity>)
 @Query("UPDATE apods SET title=:title, explanation=:explanation, url=:url, hdurl=:hdurl, mediaType=:mediaType, copyright=:copyright WHERE date=:date") suspend fun updateContent(date: String, title: String, explanation: String, url: String?, hdurl: String?, mediaType: String, copyright: String?)
 @Query("UPDATE apods SET favorite = NOT favorite WHERE date=:date") suspend fun toggle(date: String)
 @Transaction suspend fun save(items: List<ApodEntity>) { insert(items); items.forEach { updateContent(it.date,it.title,it.explanation,it.url,it.hdurl,it.mediaType,it.copyright) } }
}
@Dao
interface AsteroidDao {
 @Query("SELECT * FROM asteroids WHERE date BETWEEN :start AND :end ORDER BY date DESC, name ASC") fun recent(start: String, end: String): Flow<List<AsteroidEntity>>
 @Query("SELECT * FROM asteroids WHERE favorite=1 ORDER BY date DESC, name ASC") fun favorites(): Flow<List<AsteroidEntity>>
 @Query("SELECT * FROM asteroids WHERE id=:id AND date=:date") fun one(id: String, date: String): Flow<AsteroidEntity?>
 @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(items: List<AsteroidEntity>)
 @Query("UPDATE asteroids SET name=:name, hazardous=:hazardous, minMeters=:min, maxMeters=:max, speedKmh=:speed, distanceKm=:distance WHERE id=:id AND date=:date") suspend fun updateContent(id: String,date: String,name: String,hazardous: Boolean,min: Double?,max: Double?,speed: Double?,distance: Double?)
 @Query("UPDATE asteroids SET favorite=NOT favorite WHERE id=:id AND date=:date") suspend fun toggle(id: String,date: String)
 @Transaction suspend fun save(items: List<AsteroidEntity>) { insert(items); items.forEach { updateContent(it.id,it.date,it.name,it.hazardous,it.minMeters,it.maxMeters,it.speedKmh,it.distanceKm) } }
}
@Database(entities=[ApodEntity::class,AsteroidEntity::class],version=1,exportSchema=true)
abstract class SpaceDatabase: RoomDatabase() { abstract fun apods(): ApodDao; abstract fun asteroids(): AsteroidDao }
