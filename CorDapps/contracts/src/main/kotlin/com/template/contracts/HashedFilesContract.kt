package com.template.contracts

import com.template.states.GeneratedFilesState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

// ************
// * Contract *
// ************
class HashedFilesContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.template.contracts.HashedFilesContract"
    }

    // Used to indicate the transaction's intent.
    interface Commands: CommandData{
        class Issue : TypeOnlyCommandData(), Commands
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<HashedFilesContract.Commands>()
        when (command.value) {
            is Commands.Issue -> requireThat {
                "No inputs should be consumed when issuing a Model." using (tx.inputs.isEmpty())
                "Only one output state should be created when issuing a Model." using (tx.outputs.size == 1)
                val model = tx.outputsOfType<GeneratedFilesState>().single()
                "There must be at least one hashed file." using (model.participants.isNotEmpty())
            }
        }
    }
}