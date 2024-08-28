package springrod.localrag

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.annotation.SessionScope
import java.util.*

fun interface NameGenerator {
    fun generateName(): String
}

val MobyNameGenerator = NameGenerator {
    info.schnatterer.mobynamesgenerator.MobyNamesGenerator.getRandomName()
}

val RandomNameGenerator = NameGenerator {
    UUID.randomUUID().toString()
}

@Component
@SessionScope
internal class ConversationSession(
    private val chatMemory: ChatMemory,
    nameGenerator: NameGenerator = MobyNameGenerator,
) {

    val conversationId: String = nameGenerator.generateName()

    fun messages(): List<Message> {
        return chatMemory.get(conversationId, 100)
    }
}

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
        model.addAttribute("messages", conversionSession.messages() + UserMessage(message))
        return "fragments :: messageList"  // Return the updated fragment with the user's message
    }

    @PostMapping("/respond")
    fun respond(message: String, model: Model): String {
        logger.info("Asking model to reply in conversation '${conversionSession.conversationId}': messages are '${conversionSession.messages()}'")
        val chatResponse = chatService.respond(conversionSession.conversationId, message)
        model.addAttribute("messages", conversionSession.messages())
        return "fragments :: messageList"
    }

}

