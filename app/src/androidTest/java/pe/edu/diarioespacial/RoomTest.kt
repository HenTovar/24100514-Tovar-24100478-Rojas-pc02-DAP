package pe.edu.diarioespacial
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import pe.edu.diarioespacial.data.*
@RunWith(AndroidJUnit4::class)
class RoomTest {
 @Test fun refreshPreservesFavoritesAndDetailMatchesList() = runBlocking {
  val db=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext,SpaceDatabase::class.java).build()
  try {
   val a=ApodEntity("2026-11-14","Old","Text",null,null,"video",null)
   db.apods().save(listOf(a));db.apods().toggle(a.date);db.apods().save(listOf(a.copy(title="New")))
   assertTrue(db.apods().one(a.date).first()!!.favorite);assertEquals("New",db.apods().favorites().first().single().title)
   val neo=AsteroidEntity("1","2026-11-14","Test",false,1.0,2.0,3.0,4.0)
   db.asteroids().save(listOf(neo));db.asteroids().toggle(neo.id,neo.date);db.asteroids().save(listOf(neo.copy(name="Updated")))
   assertTrue(db.asteroids().one(neo.id,neo.date).first()!!.favorite)
   assertEquals("Updated",db.asteroids().favorites().first().single().name)
   db.asteroids().toggle(neo.id,neo.date);assertTrue(db.asteroids().favorites().first().isEmpty())
  } finally {db.close()}
 }
}
