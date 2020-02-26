package com.template.webserver

import com.fasterxml.jackson.annotation.JsonCreator
import com.template.flows.HashedFilesIssue
import com.template.states.GeneratedFilesState
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.startFlow
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.utilities.getOrThrow
import org.apache.catalina.servlet4preview.http.HttpServletRequest
import org.apache.logging.log4j.core.appender.rolling.FileSize
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.file.Files

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

    @RequestMapping(value = "/generateAttachments", method = arrayOf(RequestMethod.POST))
    private fun hashedFilesIssue(@RequestBody generatedFiles: NewFilesRequest): ResponseEntity<Any?> {
        val start = System.currentTimeMillis()
        var result = proxy.startFlow(
                ::HashedFilesIssue,
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
}