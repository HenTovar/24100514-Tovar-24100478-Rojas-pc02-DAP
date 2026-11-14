package pe.edu.diarioespacial
import org.junit.Assert.*
import org.junit.Test
import pe.edu.diarioespacial.data.*
import java.time.LocalDate
class ApodMappingTest {
 @Test fun weekIncludesSevenDays() { assertEquals("2026-11-08" to "2026-11-14",week(LocalDate.of(2026,11,14))) }
 @Test fun incompleteApodDoesNotCrash() { assertNull(ApodDto().toEntity());assertEquals("Sin título",ApodDto(date="2026-11-14").toEntity()!!.title) }
 @Test fun videoRemainsVideoWithoutImage() { val a=ApodDto(date="2026-11-14",mediaType="video").toEntity()!!;assertEquals("video",a.mediaType);assertNull(a.url) }
 @Test fun imagePreservesOptionalHdAndAuthor() { val a=ApodDto(date="2026-11-14",url="low",hdurl="high",copyright="Author").toEntity()!!;assertEquals("high",a.hdurl);assertEquals("Author",a.copyright) }
 @Test fun invalidDateIsDiscarded() { assertNull(ApodDto(date="broken").toEntity()) }
}
