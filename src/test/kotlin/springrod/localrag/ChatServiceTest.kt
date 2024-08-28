package springrod.localrag

import org.junit.jupiter.api.Test
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private lateinit var chatService: ChatService

    @Autowired
    private lateinit var embeddingModel: EmbeddingModel

    @Autowired
    private lateinit var vectorStore: VectorStore

    @Test
    fun testThing() {
        chatService.respondToUserMessage("1", "Hello")
    }
}