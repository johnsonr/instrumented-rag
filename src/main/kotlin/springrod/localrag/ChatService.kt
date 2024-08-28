package springrod.localrag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service
import springrod.localrag.advisors.CaptureMemoryAdvisor
import java.util.concurrent.Executor

@Service
class ChatService(
    private val chatModel: ChatModel,
    private val localChatModel: OllamaChatModel,
    private val vectorStore: VectorStore,
    private val executor: Executor,
) {

    private fun chatClientFor(conversationSession: ConversationSession): ChatClient {
        return ChatClient
            .builder(chatModel)
            .defaultAdvisors(
                MessageChatMemoryAdvisor(conversationSession.chatMemory),
                CaptureMemoryAdvisor(
                    vectorStore = vectorStore,
                    chatModel = localChatModel,
                    executor = executor,
                ),
                QuestionAnswerAdvisor(
                    vectorStore,
                    SearchRequest.defaults().withSimilarityThreshold(.8)
                ),
                SimpleLoggerAdvisor(),
            )
            .defaultSystem(conversationSession.promptResource())
            .build()
    }

    fun respond(
        conversationSession: ConversationSession,
        message: String,
    ): ChatResponse {
        val chatResponse = chatClientFor(conversationSession)
            .prompt()
            .advisors { it.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationSession.conversationId) }
            .advisors { it.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 50) }
            .user(message)
            .call()
            .chatResponse()
        return chatResponse
    }
}