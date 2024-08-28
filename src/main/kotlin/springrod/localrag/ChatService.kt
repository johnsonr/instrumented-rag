package springrod.localrag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val chatModel: ChatModel,
    private val vectorStore: VectorStore,
) {

    private fun chatClientFor(conversationSession: ConversationSession): ChatClient {
        return ChatClient
            .builder(chatModel)
            .defaultSystem(conversationSession.promptResource())
            .defaultAdvisors(
                MessageChatMemoryAdvisor(conversationSession.chatMemory),
                QuestionAnswerAdvisor(
                    vectorStore,
                    SearchRequest.defaults().withSimilarityThreshold(.5)
                ),
                CaptureMemoryAdvisor(),
                SimpleLoggerAdvisor(),
            )
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