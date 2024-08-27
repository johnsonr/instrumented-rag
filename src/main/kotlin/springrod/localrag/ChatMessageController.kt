package springrod.localrag

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import kotlin.random.Random

@Controller
@RequestMapping("/messages")
class ChatMessageController {

    @GetMapping
    fun getMessages(model: Model, @RequestParam("htmx", required = false) htmx: Boolean?): String {
        model.addAttribute("messages", messagesFromService)
        return if (htmx == true) {
            "fragments :: messageList"  // Return only the fragment for HTMX requests
        } else {
            "messages"  // Return the full page for direct navigation
        }
    }

    private val messagesFromService: List<String>
        get() = generateRandomStrings(10, 10)
}

fun generateRandomStrings(count: Int, length: Int): List<String> {
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    return List(count) {
        (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}