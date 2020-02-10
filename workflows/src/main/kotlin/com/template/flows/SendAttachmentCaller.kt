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
        private val linearId: UniqueIdentifier)  : FlowLogic<Unit>() {

    @Suspendable
    override fun call(){
        //grab the list of hashes for the generated files
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val hashFilesState = serviceHub.vaultService.queryBy<HashedFilesState>(queryCriteria).states.single()
        val fileSet = hashFilesState.state.data.fileHashes

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        //for each of the hashes found, call the send attachment flow for the buyer and seller
        for(attachmentHash in fileSet){
            subFlow(SendAttachment(buyer, attachmentHash))
            subFlow(SendAttachment(seller, attachmentHash))
        }
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
