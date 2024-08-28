package springrod.review

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class ImporterTest {

    @Autowired
    private lateinit var importer: Importer

    @Test
    fun test() {
        importer.importJsonlToNeo4j(
            filePath="/Users/rjohnson/dev/localrag/src/main/resources/Musical_Instruments.jsonl")
    }
}