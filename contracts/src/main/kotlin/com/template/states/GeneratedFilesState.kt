package com.template.states

import com.template.contracts.HashedFilesContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.crypto.SecureHash
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(HashedFilesContract::class)
data class GeneratedFilesState(
        val fileHashes: List<String> = listOf(),
        override val participants: List<AbstractParty> = listOf(),
        override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState{

}
