package springrod.localrag

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
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
class ConversationSession(
    val chatMemory: ChatMemory,
    nameGenerator: NameGenerator = MobyNameGenerator,
) {

    private val logger: Logger = LoggerFactory.getLogger(ConversationSession::class.java)

    private var promptPath = "prompts/pretentious_system.md"

    var direction: String = ""

    val conversationId: String = nameGenerator.generateName()

    fun messages(): List<Message> {
        return chatMemory.get(conversationId, 100)
    }

    fun promptResource(): Resource {
        return if (direction.isBlank()) {
            logger.info("Prompting with $promptPath")
            ClassPathResource(promptPath)
        } else {
            logger.info("Prompting with prompts/obedient_system.md to follow directions")
            ClassPathResource("prompts/obedient_system.md")
        }
    }

    fun obeyDirection(direction: String) {
        logger.info("Obeying direction: $direction")
        this.direction = direction
    }

    fun clearDirection() {
        this.direction = ""
    }
}