package com.template.states

import com.template.contracts.HashedFilesContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(HashedFilesContract::class)
data class HashedFilesState(val fileHashes: List<String> = listOf(),
                            override val participants: List<AbstractParty> = listOf()) : ContractState{

}
