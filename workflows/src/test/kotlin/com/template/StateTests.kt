package com.template
import com.template.flows.HashedFilesIssue
import com.template.states.GeneratedFilesState
import net.corda.core.identity.CordaX500Name
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before
import org.junit.Test

class StateTests {
    lateinit var mockNetwork: MockNetwork
    lateinit var a: StartedMockNode
    lateinit var b: StartedMockNode
    lateinit var c: StartedMockNode

    @Before
    fun setup() {
        mockNetwork = MockNetwork(
                listOf("com.template"),
                notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary","East", "US")))
        )
        a = mockNetwork.createNode(MockNodeParameters())
        b = mockNetwork.createNode(MockNodeParameters())
        c = mockNetwork.createNode(MockNodeParameters())
        mockNetwork.runNetwork()
    }

    @After
    fun tearDown() {
        mockNetwork.stopNodes()
    }

    @Test
    fun flowReturnsSignedTransaction(){
        val flow = HashedFilesIssue("test-2-11-777-",1, 1, 1)
        val future = a.startFlow(flow)
        mockNetwork.runNetwork()
        val stx: SignedTransaction = future.getOrThrow()
        assert(stx.tx.inputs.isEmpty())
        assert(stx.tx.outputs.single().data is GeneratedFilesState)
        assert(stx.tx.attachments.isNotEmpty())
        stx.verifyRequiredSignatures()
    }

//    @Test
//    fun allPartiesHaveReceivedAttachments(){
//        val partyA = a.info.chooseIdentityAndCert().party
//        val partyB = b.info.chooseIdentityAndCert().party
//        val partyC = c.info.chooseIdentityAndCert().party
//        val hashedFilesIssueFlow = HashedFilesIssue("test-2-10-311-",1, 1, 1)
//        val hashedFilesFuture = a.startFlow(hashedFilesIssueFlow)
//        mockNetwork.runNetwork()
//
//        val stx: SignedTransaction = hashedFilesFuture.getOrThrow()
//        val outputState: HashedFilesState = stx.tx.outputStates.get(0) as HashedFilesState
//
//        val sendAttachmentsCallerFlow = SendAttachmentCaller(partyB, partyC, outputState.linearId)
//        val sendAttachmentCallerFuture = a.startFlow(sendAttachmentsCallerFlow)
//        mockNetwork.runNetwork()
//
//        val attachmentsFoundPartyB = b.services.attachments.queryAttachments(
//                AttachmentQueryCriteria.AttachmentsQueryCriteria(
//                        uploaderCondition = Builder.like("test-", false)
//                )
//        )
//
//        assert(attachmentsFoundPartyB.isNotEmpty())
//
//        val attachmentsFoundPartyC = c.services.attachments.queryAttachments(
//                AttachmentQueryCriteria.AttachmentsQueryCriteria(
//                        uploaderCondition = Builder.like("test-", false)
//                )
//        )
//
//        assert(attachmentsFoundPartyC.isNotEmpty())
//
//    }
}