package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.HashedFilesContract
import com.template.states.HashedFilesState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.internal.InputStreamAndHash
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*


// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class SendAttachmentCaller(
        private val buyer: Party,
        private val seller: Party,
        private val linearid: UniqueIdentifier)  : FlowLogic<SignedTransaction>() {

    private companion object{ val Int.MB: Long get() = this * 1024L * 1024L }
    @Suspendable
    override fun call(): SignedTransaction{
        //grab the list of hashes for the generated files
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearid))
        val hashFilesState = serviceHub.vaultService.queryBy<HashedFilesState>(queryCriteria).states.single()
        val fileSet = hashFilesState.state.data.fileHashes

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        //for each of the hashes found, call the send attachment flow for the buyer and seller
        for(attachmentHash in fileSet){
            subFlow(SendAttachment(buyer, attachmentHash))
            subFlow(SendAttachment(seller, attachmentHash))
        }




//        val transactionBuilder = TransactionBuilder(notary)
//        val output = HashedFilesState(filesHashList, listOf(ourIdentity))
//        val commandData = HashedFilesContract.Commands.Issue()
//        transactionBuilder.addCommand(commandData,ourIdentity.owningKey)
//        transactionBuilder.addOutputState(output, HashedFilesContract.ID)
//        transactionBuilder.verify(serviceHub)
//        val stx = serviceHub.signInitialTransaction(transactionBuilder)
//        return subFlow(FinalityFlow(stx, HashSet<FlowSession>(0)))
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
