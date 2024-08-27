package springrod.localrag

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatConfiguration {

    @Bean
    fun chatMemory(): ChatMemory {
        return InMemoryChatMemory()
    }
}