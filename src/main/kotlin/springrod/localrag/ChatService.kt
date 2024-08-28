package springrod.localrag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class ChatService(
    chatModel: ChatModel,
    vectorStore: VectorStore,
    chatMemory: ChatMemory,
) {

    private val chatClient = ChatClient
        .builder(chatModel)
        .defaultSystem(
            """
                You are a helpful knowledge retrieval agent.
                If you don't know the answer you say so, rather than guessing.
                You will reply from the context you're given rather than your training data.
            """.trimIndent()
        )
        .defaultAdvisors(
            MessageChatMemoryAdvisor(chatMemory),
            QuestionAnswerAdvisor(
                vectorStore,
                SearchRequest.defaults().withSimilarityThreshold(.5)
            ),
            CaptureMemoryAdvisor(),
            SimpleLoggerAdvisor(),
        )
        .build()

    fun respond(
        conversationId: String,
        message: String,
    ): ChatResponse {
        val chatResponse = chatClient
            .prompt()
            .advisors { it.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId) }
            .advisors { it.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 50) }
            .user(message)
            .call()
            .chatResponse()
        return chatResponse
    }
}