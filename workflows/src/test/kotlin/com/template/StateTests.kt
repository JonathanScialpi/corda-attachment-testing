package com.template
import com.template.flows.HashedFilesIssue
import com.template.states.HashedFilesState
import net.corda.core.identity.CordaX500Name
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
        //val pcx = a.info.chooseIdentityAndCert().party
        //val buyer = b.info.chooseIdentityAndCert().party
        //val seller = c.info.chooseIdentityAndCert().party
        val flow = HashedFilesIssue("test-2-10-308-",1, 1, 1)
        val future = a.startFlow(flow)
        mockNetwork.runNetwork()
        val stx: SignedTransaction = future.getOrThrow()
        assert(stx.tx.inputs.isEmpty())
        assert(stx.tx.outputs.single().data is HashedFilesState)
        assert(stx.tx.attachments.isNotEmpty())
        stx.verifyRequiredSignatures()
    }
}