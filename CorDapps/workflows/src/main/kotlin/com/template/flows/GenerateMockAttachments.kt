package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.GeneratedFilesContract
import com.template.states.GeneratedFilesState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.internal.InputStreamAndHash
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*


// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class GenerateMockAttachments(
        private val testName: String,
        private val content: Byte,
        private val fileSize: Int,
        private val numberOfFiles: Int) : FlowLogic<SignedTransaction>() {

    private companion object{
        val Int.MB: Long get() = this * 1024L * 1024L
    }
    @Suspendable
    override fun call(): SignedTransaction{
        val filesHashList =  mutableListOf<String>()
        var counter = 0

        while(counter < numberOfFiles){
            var fileName = testName + counter
            var generatedStreamHash =  InputStreamAndHash.createInMemoryTestZip(fileSize.MB.toInt(), content, fileName)
            var attachmentHash = serviceHub.attachments.importAttachment(
                    generatedStreamHash.inputStream, ourIdentity.toString(), fileName
            )
            filesHashList.add(attachmentHash.toString())
            counter++
        }

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val transactionBuilder = TransactionBuilder(notary)
        val output = GeneratedFilesState(filesHashList, listOf(ourIdentity), UniqueIdentifier())
        val commandData = GeneratedFilesContract.Commands.Issue()
        transactionBuilder.addCommand(commandData,ourIdentity.owningKey)
        transactionBuilder.addOutputState(output, GeneratedFilesContract.ID)
        transactionBuilder.verify(serviceHub)
        val stx = serviceHub.signInitialTransaction(transactionBuilder)
        return subFlow(FinalityFlow(stx, HashSet<FlowSession>(0)))
    }
}
