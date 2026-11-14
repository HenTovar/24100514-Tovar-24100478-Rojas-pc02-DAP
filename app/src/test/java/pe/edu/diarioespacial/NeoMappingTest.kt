package pe.edu.diarioespacial
import org.junit.Assert.*
import org.junit.Test
import pe.edu.diarioespacial.data.*
import java.time.LocalDate
class NeoMappingTest {
 @Test fun emptyNeoResponseIsSafe() { assertTrue(FeedDto().toEntities().isEmpty()) }
 @Test fun flattenSortAndParseNumbers() { val n=NeoDto("1","One",true,DiameterDto(MetersDto(1.0,2.0)),listOf(ApproachDto("2026-11-14",VelocityDto("1200.5"),DistanceDto("300.25"))));val rows=FeedDto(mapOf("2026-11-14" to listOf(n),"2026-11-13" to listOf(NeoDto("2")))).toEntities();assertEquals("1",rows.first().id);assertEquals(1200.5,rows.first().speedKmh!!,0.001);assertTrue(rows.first().hazardous) }
 @Test fun missingIdAndBrokenNumbersAreSafe() { val rows=FeedDto(mapOf("2026-11-14" to listOf(NeoDto(),NeoDto("2",approaches=listOf(ApproachDto("2026-11-14",VelocityDto("NaN"),DistanceDto("bad"))))))).toEntities();assertEquals(1,rows.size);assertNull(rows[0].speedKmh);assertNull(rows[0].distanceKm) }
 @Test fun sameIdOnDifferentDaysIsNotLost() { assertEquals(2,FeedDto(mapOf("2026-11-13" to listOf(NeoDto("1")),"2026-11-14" to listOf(NeoDto("1")))).toEntities().size) }
 @Test fun missingNumberIsNotFakeZero() { assertEquals("No disponible",numberWithUnit(null,"m")) }
}
