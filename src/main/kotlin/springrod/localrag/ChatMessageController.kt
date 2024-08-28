package springrod.localrag

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ChatMessageController(
    private val chatService: ChatService,
    private val chatMemory: ChatMemory,
) {

    private val logger: Logger = LoggerFactory.getLogger(ChatMessageController::class.java)

//    // TODO should be per user
    private val conversationId : String = "weoriwoeirowuier"

    @GetMapping("/messages")
    fun getMessages(model: Model, @RequestParam("htmx", required = false) htmx: Boolean?): String {
        model.addAttribute("messages", chatMemory.get(conversationId, 100))
        return if (htmx == true) {
            "fragments :: messageList"  // Return only the fragment for HTMX requests
        } else {
            "messages"  // Return the full page for direct navigation
        }
    }

    /**
     * Add a user message but don't ask to respond to it.
     * This enables us to update the UI quickly.
     */
    @PutMapping("/messages")
    fun addUserMessage(model: Model, @RequestParam("message") message: String): String {
        logger.info("Added user message '$message'")
        model.addAttribute("messages", chatMemory.get(conversationId, 100) + UserMessage(message))
        return "fragments :: messageList"  // Return the updated fragment with the user's message
    }

    private fun messagesIn(conversationId: String): List<Message> {
        return chatMemory.get(conversationId, 100)
    }

    @PostMapping("/respond")
    fun respond(message: String, model: Model): String {
        logger.info("Asking model to reply: messages are '${messagesIn(conversationId)}'")

        val chatResponse = chatService.respond(conversationId, message)
        model.addAttribute("messages", chatMemory.get(conversationId, 100))
        return "fragments :: messageList"
    }

}

