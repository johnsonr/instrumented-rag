package springrod.review

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.nio.file.Files

@Node
data class Review(
    val rating: Float,
    val title: String,
    val text: String,
    val asin: String,
    val userId: String,
    val verifiedPurchase : Boolean,
    @Id @GeneratedValue val id: Long? = null,
)

interface ReviewRepository : Neo4jRepository<Review, Long>


@Service
class Importer (
        private val repository: ReviewRepository,
        private val objectMapper: ObjectMapper
) {

        fun importJsonlToNeo4j(filePath: String) {
            Files.lines(File(filePath).toPath()).use { lines ->
                lines.forEach { line: String? ->
                    try {
                        val entity = objectMapper.readValue(line, Review::class.java)
                        repository.save(entity)
                    } catch (e: IOException) {
                        e.printStackTrace() // Handle the exception as needed
                    }
                }
            }
        }
    }
