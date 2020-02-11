package com.template
import com.template.flows.HashedFilesIssue
import com.template.flows.SendAttachmentCaller
import com.template.states.HashedFilesState
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.services.vault.AttachmentQueryCriteria
import net.corda.core.node.services.vault.Builder
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.internal.chooseIdentityAndCert
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.internal.findCordapp
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
        // val startedNodes = arrayListOf(a, b, c)
        // For real nodes this happens automatically, but we have to manually register the flow for tests
        //startedNodes.forEach { it.registerInitiatedFlow(IOUIssueFlowResponder::class.java) }
        mockNetwork.runNetwork()
    }

    @After
    fun tearDown() {
        mockNetwork.stopNodes()
    }

    @Test
    fun flowReturnsSignedTransaction(){
        val flow = HashedFilesIssue("test-2-11-1151-",1, 1, 1)
        val future = a.startFlow(flow)
        mockNetwork.runNetwork()
        val stx: SignedTransaction = future.getOrThrow()
        assert(stx.tx.inputs.isEmpty())
        assert(stx.tx.outputs.single().data is HashedFilesState)
        assert(stx.tx.attachments.isNotEmpty())
        stx.verifyRequiredSignatures()
    }

//    @Test
//    fun allPartiesHaveReceivedAttachments(){
//        val pcx = a.info.chooseIdentityAndCert().party
//        val buyer = b.info.chooseIdentityAndCert().party
//        val seller = c.info.chooseIdentityAndCert().party
//        val hashedFilesIssueFlow = HashedFilesIssue("test-2-10-311-",1, 1, 1)
//        val hashedFilesFuture = a.startFlow(hashedFilesIssueFlow)
//        mockNetwork.runNetwork()
//
//        val stx: SignedTransaction = hashedFilesFuture.getOrThrow()
//        val outputState: HashedFilesState = stx.tx.outputStates.get(0) as HashedFilesState
//
//        val sendAttachmentsCallerFlow = SendAttachmentCaller(buyer, seller, outputState.linearId)
//        val sendAttachmentCallerFuture = a.startFlow(sendAttachmentsCallerFlow)
//        mockNetwork.runNetwork()
//
//        val attachmentsFoundBuyer = b.services.attachments.queryAttachments(
//                AttachmentQueryCriteria.AttachmentsQueryCriteria(
//                        uploaderCondition = Builder.like("test-", false)
//                )
//        )
//
//        assert(attachmentsFoundBuyer.isNotEmpty())
//
//        val attachmentsFoundSeller = c.services.attachments.queryAttachments(
//                AttachmentQueryCriteria.AttachmentsQueryCriteria(
//                        uploaderCondition = Builder.like("test-", false)
//                )
//        )
//
//        assert(attachmentsFoundSeller.isNotEmpty())
//
//    }
}