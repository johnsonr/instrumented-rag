package springrod.localrag

import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.RequestResponseAdvisor

/**
 * Capture a memory
 */
class CaptureMemoryAdvisor : RequestResponseAdvisor {

    /**
     * We don't change the request, we merely look at it
     */
    override fun adviseRequest(request: AdvisedRequest, context: MutableMap<String, Any>): AdvisedRequest {
        println(request.messages)

        return request
    }

}