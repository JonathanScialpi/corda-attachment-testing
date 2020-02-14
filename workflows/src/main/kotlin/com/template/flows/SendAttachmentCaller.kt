package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.states.GeneratedFilesState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria


// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class SendAttachmentCaller(
        private val recipientA: Party,
        private val recipientB: Party?,
        private val linearId: UniqueIdentifier)  : FlowLogic<Unit>() {

    @Suspendable
    override fun call(){

        //grab the list of hashes for the generated files
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val hashFilesState = serviceHub.vaultService.queryBy<GeneratedFilesState>(queryCriteria).states.single()
        val fileSet = hashFilesState.state.data.fileHashes

        //for each of the hashes found, call the send attachment flow for the buyer and seller
        if (recipientB != null) {
            for(attachmentHash in fileSet){
                subFlow(SendAttachment(recipientA, attachmentHash))
                subFlow(SendAttachment(recipientB, attachmentHash))
            }
        }else{
            for(attachmentHash in fileSet){
                subFlow(SendAttachment(recipientA, attachmentHash))
            }
        }
    }
}