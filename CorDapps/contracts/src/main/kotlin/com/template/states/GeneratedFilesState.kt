package com.template.states

import com.template.contracts.GeneratedFilesContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(GeneratedFilesContract::class)
data class GeneratedFilesState(
        val fileHashes: List<String> = listOf(),
        override val participants: List<AbstractParty> = listOf(),
        override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState{

}
