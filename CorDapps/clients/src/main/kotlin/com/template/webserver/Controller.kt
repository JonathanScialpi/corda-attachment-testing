package com.template.webserver

import com.fasterxml.jackson.annotation.JsonCreator
import com.template.flows.GenerateMockAttachments
import com.template.states.GeneratedFilesState
import net.corda.core.messaging.startFlow
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = ["/status"])
    private fun isAlive() = "Up and running!"

    @RequestMapping(value = "/generateMockAttachments", method = arrayOf(RequestMethod.POST))
    private fun generateMockAttachments(@RequestBody generatedFiles: NewFilesRequest): ResponseEntity<Any?> {
        val start = System.currentTimeMillis()
        var result = proxy.startFlow(
                ::GenerateMockAttachments,
                generatedFiles.testName,
                generatedFiles.content.toByte(),
                generatedFiles.fileSize.toInt(),
                generatedFiles.numberOfFiles.toInt())
        val elapsedTime = System.currentTimeMillis() - start
        val hashedFilesList = (result.returnValue.get().tx.outputs[0].data as GeneratedFilesState).fileHashes.toString()
        val responseMap = HashMap<String, Any>()
        responseMap["HashedFiles"] = hashedFilesList
        responseMap["ElapsedTime"] = elapsedTime
        return ResponseEntity.ok(responseMap.toString())
    }

    @RequestMapping(value = "/")

    data class NewFilesRequest @JsonCreator constructor(
            val testName: String,
            val content: String,
            val fileSize: String,
            val numberOfFiles: String
    )
}