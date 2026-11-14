package pe.edu.diarioespacial
import org.junit.Assert.*
import org.junit.Test
import pe.edu.diarioespacial.data.*
class ApodBasicTest {
 @Test fun dateRangeUsesOfficialLegacyFormat() {assertEquals("261108",legacyDate("2026-11-08"))}
 @Test fun mapsActualImageInsteadOfArticle() {val a=ApodBasicDto("2026-11-14","Moon","<p>Hello &amp; stars</p>","image","https://assets.science.nasa.gov/moon.jpg","<a>Author</a>").toEntity()!!;assertEquals("https://assets.science.nasa.gov/moon.jpg",a.url);assertEquals("Hello & stars",a.explanation);assertEquals("Author",a.copyright)}
 @Test fun rejectsLogoAsImage() {val a=ApodBasicDto("2026-11-14",hdurl="https://science.nasa.gov/nasa-logo.png",media_type="image").toEntity()!!;assertNull(a.url)}
 @Test fun videosNeverBecomeImageRequests() {val a=ApodBasicDto("2026-11-14",media_type="iframe",hdurl="https://assets.science.nasa.gov/preview.jpg").toEntity()!!;assertEquals("video",a.mediaType);assertNull(a.url)}
 @Test fun missingDataIsSafe() {assertNull(ApodBasicDto().toEntity());assertNull(ApodBasicDto("2026-11-14",media_type="image").toEntity()!!.url)}
 @Test fun oldServiceLogoIsDetected() {assertTrue(ApodEntity("2026-11-14","NASA Science","","https://science.nasa.gov/nasa-logo.png",null,"image",null).isPlaceholder());assertFalse(ApodEntity("2026-11-14","Moon","","https://assets.science.nasa.gov/moon.jpg",null,"image",null).isPlaceholder())}
 @Test fun cannotReadLocalFileAsImage() {assertNull(ApodBasicDto("2026-11-14",media_type="image",hdurl="file:///etc/passwd").toEntity()!!.url)}
}
