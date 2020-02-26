package com.template.webserver

import com.fasterxml.jackson.annotation.JsonCreator
import com.template.flows.GenerateMockAttachments
import com.template.flows.TriggerAttachmentDownload
import com.template.states.GeneratedFilesState
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
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

    @RequestMapping(value = "/generateMockAttachments", method = [RequestMethod.POST])
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

    data class NewFilesRequest @JsonCreator constructor(
            val testName: String,
            val content: String,
            val fileSize: String,
            val numberOfFiles: String
    )

    @RequestMapping(value = "/triggerAttachmentDownload", method = [RequestMethod.POST])
    private fun triggerAttachmentDownload(@RequestBody downloadRequest: NewDownloadRequest): ResponseEntity<Any?> {
        val receiverx500Name = CordaX500Name.parse(downloadRequest.receiver)
        val receiverParty = proxy.wellKnownPartyFromX500Name(receiverx500Name) as Party
        val start = System.currentTimeMillis()
        proxy.startFlow(::TriggerAttachmentDownload, receiverParty, downloadRequest.attachmentHash)
        val elapsedTime = System.currentTimeMillis() - start
        return ResponseEntity.ok(elapsedTime.toString())
    }

    data class NewDownloadRequest @JsonCreator constructor(
            val receiver: String,
            val attachmentHash: String
    )
}