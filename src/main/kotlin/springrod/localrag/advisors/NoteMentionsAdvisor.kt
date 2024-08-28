package springrod.localrag.advisors

import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.RequestResponseAdvisor
import org.springframework.context.ApplicationEventPublisher

data class NotedMentionEvent(
    val what: String,
    val fullText: String,
)

class NoteMentionsAdvisor(
    private val whatToNote: String,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : RequestResponseAdvisor {

    override fun adviseRequest(request: AdvisedRequest, context: MutableMap<String, Any>): AdvisedRequest {
        if (request.userText.contains(whatToNote, ignoreCase = true)) {
            applicationEventPublisher.publishEvent(
                NotedMentionEvent(what = whatToNote, fullText = request.userText)
            )
        }
        return request
    }
}