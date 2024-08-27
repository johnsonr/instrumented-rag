package springrod.localrag

import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ChatClientTest {

    @Autowired
    private lateinit var chatModel: ChatModel

    @Autowired
    private lateinit var embeddingModel: EmbeddingModel

    @Autowired
    private lateinit var vectorStore: VectorStore

    @Test
    fun testThing() {
        val chatClient = ChatClient
            .builder(chatModel)
            .defaultOptions(OllamaOptions().withModel("gemma2:2b"))
            .defaultSystem("""
                You speak like a dog
            """.trimIndent())
            .defaultAdvisors(
                QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
            .build()
        val chatResponse = chatClient.prompt()
            .user("Tell me a joke")
            .call()
            .chatResponse()
        println(embeddingModel)
        println(chatResponse)
    }
}