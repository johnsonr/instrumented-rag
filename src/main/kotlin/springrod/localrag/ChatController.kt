package springrod.localrag

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
internal class ChatController(
    private val conversionSession: ConversationSession,
    private val chatService: ChatService,
) {

    private val logger: Logger = LoggerFactory.getLogger(ChatController::class.java)

    @GetMapping("/messages", "/")
    fun getMessages(model: Model, @RequestParam("htmx", required = false) htmx: Boolean?): String {
        model.addAttribute("messages", conversionSession.messages())
        return if (htmx == true) {
            "fragments :: messageList"  // Return only the fragment for HTMX requests
        } else {
            model.addAttribute("conversationId", conversionSession.conversationId)
            "messages"  // Return the full page
        }
    }

    /**
     * Add a user message but don't ask to respond to it.
     * This enables us to update the UI quickly.
     */
    @PutMapping("/messages")
    fun addUserMessage(model: Model, @RequestParam("message") message: String): String {
        logger.info("Added user message '$message'")
        model.addAttribute("messages", conversionSession.messages() + UserMessage(message))
        return "fragments :: messageList"  // Return the updated fragment with the user's message
    }

    @PostMapping("/respond")
    fun respond(message: String, model: Model): String {
        logger.info("Asking model to reply in conversation '${conversionSession.conversationId}': ${conversionSession.messages().size} so far")
        chatService.respondToUserMessage(conversionSession, message)
        model.addAttribute("messages", conversionSession.messages())
        return "fragments :: messageList"
    }

}

