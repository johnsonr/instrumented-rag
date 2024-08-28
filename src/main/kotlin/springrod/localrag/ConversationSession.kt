package springrod.localrag

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

    val conversationId: String = nameGenerator.generateName()

    fun messages(): List<Message> {
        return chatMemory.get(conversationId, 100)
    }

    fun promptResource(): Resource {
        return ClassPathResource("prompts/pretentious_system.md")
    }
}