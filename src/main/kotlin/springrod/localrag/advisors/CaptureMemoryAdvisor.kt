package springrod.localrag.advisors

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.Logger
import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.RequestResponseAdvisor
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.ClassPathResource
import org.springframework.retry.support.RetryTemplate
import org.springframework.retry.support.RetryTemplateBuilder

/**
 * Capture a memory
 */
class CaptureMemoryAdvisor(
    private val vectorStore: VectorStore,
    chatModel: ChatModel,
    private val retryTemplate: RetryTemplate =
        RetryTemplateBuilder().maxAttempts(3).fixedBackoff(1000).build()
) : RequestResponseAdvisor {

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(CaptureMemoryAdvisor::class.java)

    private val chatClient = ChatClient
        .builder(chatModel)
        .defaultSystem(ClassPathResource("prompts/capture_memory.md"))
        .build()

    // Be sure Jackson can bind Kotlin
    private val kotlinAwareObjectMapper = ObjectMapper().registerKotlinModule()


    /**
     * We don't change the request, we merely look at it
     */
    override fun adviseRequest(request: AdvisedRequest, context: MutableMap<String, Any>): AdvisedRequest {
        // Allow for flaky model
        try {
            retryTemplate.execute<Boolean, Throwable> {
                extractMemoryIfPossible(request)
            }
        } catch (t: Throwable) {
            logger.error("We tried really hard but the model kept failing. Don't fail the advisor chain", t)
        }

        return request
    }

    private fun extractMemoryIfPossible(request: AdvisedRequest): Boolean {
        // We only use the latest user message for the demo: Would want to make that fancier
        val memoryLlmResponse = chatClient.prompt()
            .user(request.userText)
            .call()
            .entity(
                BeanOutputConverter(
                    MemoryLlmResponse::class.java,
                    kotlinAwareObjectMapper,
                )
            )
        if (memoryLlmResponse.worthKeeping()) {
            logger.info("Adding memory: {}", memoryLlmResponse)
            vectorStore.add(
                listOf(
                    Document(
                        """
                    Remember this about the user:
                    ${memoryLlmResponse.content}
                """.trimIndent()
                    )
                )
            )
            return true
        }
        logger.info("Ignoring useless potential memory: {}", memoryLlmResponse)
        return false
    }

    // Lots of little private classes can be handy in your LLM interaction code
    private data class MemoryLlmResponse(
        val content: String? = null,
        val useful: Boolean,
    ) {
        fun worthKeeping() = useful && content != null
    }

}