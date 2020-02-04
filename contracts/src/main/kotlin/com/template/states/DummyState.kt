package com.template.states

import com.template.contracts.HashedFilesContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(HashedFilesContract::class)
data class DummyState(
        override val participants: List<AbstractParty> = listOf()
) : ContractState{

}
