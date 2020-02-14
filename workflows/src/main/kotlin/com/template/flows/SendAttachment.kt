package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.ReceiverContract
import com.template.states.ReceiverState
import net.corda.core.contracts.requireThat
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder


// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class SendAttachment(
        private val receiver: Party,
        private val attachmentHash: String
        ) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction{
        val attachmentHash = SecureHash.parse(attachmentHash)

        val outputState = ReceiverState(attachmentHash, listOf(ourIdentity, receiver))
        val commandData = ReceiverContract.Commands.Issue()

        //build the buyer TX
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val transactionBuilder = TransactionBuilder(notary)
        transactionBuilder.addCommand(commandData, ourIdentity.owningKey, receiver.owningKey)
                .addOutputState(outputState, ReceiverContract.ID)
                .addAttachment(attachmentHash)
                .verify(serviceHub)
        val ptx = serviceHub.signInitialTransaction(transactionBuilder)
        val session = initiateFlow(receiver)
        val stx = subFlow(CollectSignaturesFlow(ptx, listOf(session)))
        return subFlow(FinalityFlow(stx, listOf(session)))
    }
}

// Responder flow isn't needed because no other sigs are needed...

@InitiatedBy(SendAttachment::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                if (stx.tx.attachments.isEmpty()) {
                    throw FlowException("No Jar was being sent")
                }

            }
        }
        val txId = subFlow(signTransactionFlow).id
        subFlow(ReceiveFinalityFlow(counterpartySession, expectedTxId = txId))
    }
}
