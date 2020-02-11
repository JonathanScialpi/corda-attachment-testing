package com.template.states

import com.template.contracts.ReceiverContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.crypto.SecureHash
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(ReceiverContract::class)
data class ReceiverState(
        val attachmentHash: SecureHash,
        override val participants: List<AbstractParty> = listOf()
) : ContractState{

}
