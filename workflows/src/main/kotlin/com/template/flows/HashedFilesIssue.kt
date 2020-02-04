package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.HashedFilesContract
import com.template.states.HashedFilesState
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.internal.InputStreamAndHash
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*


// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class HashedFilesIssue(
        private val fileSize: Int,
        private val numberOfFiles: Int) : FlowLogic<SignedTransaction>() {

    private companion object{ val Int.MB: Long get() = this * 1024L * 1024L }
    @Suspendable
    override fun call(): SignedTransaction{
        val filesHashList =  mutableListOf<SecureHash>()
        var counter = 0
        while(counter < numberOfFiles){
            var fileName = "test-file$counter"
            var generatedStreamHash =  InputStreamAndHash.createInMemoryTestZip(fileSize.MB.toInt(), 0, fileName)
            var attachmentHash = serviceHub.attachments.importAttachment(
                    generatedStreamHash.inputStream, ourIdentity.toString(), fileName
            )
            filesHashList.add(attachmentHash)
        }

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val transactionBuilder = TransactionBuilder(notary)
        val output = HashedFilesState(filesHashList, listOf(ourIdentity))
        val commandData = HashedFilesContract.Commands.Issue()
        transactionBuilder.addCommand(commandData,ourIdentity.owningKey)
        transactionBuilder.addOutputState(output, HashedFilesContract.ID)
        transactionBuilder.verify(serviceHub)
        val stx = serviceHub.signInitialTransaction(transactionBuilder)
        return subFlow(FinalityFlow(stx, HashSet<FlowSession>(0)))
    }
}

// Responder flow isn't needed because no other sigs are needed...

//@InitiatedBy(HashedFilesIssue::class)
//class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
//    @Suspendable
//    override fun call() {
//
//    }
//}
