package springrod.localrag

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.ResourceLoader
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class DocumentUpload(
    val url: String
)

@RestController
class UploadController(
    private val vectorStore: VectorStore,
    private val resourceLoader: ResourceLoader,
) {

    private val logger: Logger = LoggerFactory.getLogger(UploadController::class.java)

    @PutMapping("/documents")
    fun addDocument(@RequestBody documentUpload: DocumentUpload): List<String> {
        logger.info("Adding document from '${documentUpload.url}'")
        val r = resourceLoader.getResource(documentUpload.url)
        val content = TikaDocumentReader(documentUpload.url).get()
        val documents = TokenTextSplitter().split(content)
        vectorStore.add(documents)
        return documents.map { it.id}
    }
}