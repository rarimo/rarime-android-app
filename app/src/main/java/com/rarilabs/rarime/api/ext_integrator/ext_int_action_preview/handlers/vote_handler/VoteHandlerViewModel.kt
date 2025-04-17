package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.vote_handler

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.voting.VotingManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class VotingWhitelistData(
    val citizenshipWhitelist: Array<Number>,
    val identityCreationTimestampUpperBound: Number,
    val identityCounterUpperBound: Number,
    val birthDateUpperbound: Number,
    val expirationDateLowerBound: Number
)

data class ProposalInfoDetailsConfigJSON(
    val start_timestamp: Long,
    val duration: Long,
    val multichoice: String,
    val accepted_options: List<String>,
    val description: String,
    val voting_whitelist: List<String>,
    val voting_whitelist_data: VotingWhitelistData
)

data class ProposalInfoDetailsJSON(
    val proposal_smt: String,
    val status: Int,
    val config: ProposalInfoDetailsConfigJSON,
    val voting_results: List<List<String>>
)

data class ProposalInfoJSON(
    val proposal_info: ProposalInfoDetailsJSON,
    val proposal_event_id: String
)

data class ProposalMetadataOption(
    val title: String,
    val variants: List<String>
)

data class ProposalMetadata(
    val title: String,
    val description: String,
    val acceptedOptions: List<ProposalMetadataOption>
)

data class VoteSelections(
    val questionIndex: Number,
    val answerIndex: Number,
)

@HiltViewModel
class VoteHandlerViewModel @Inject constructor(
    private val extIntegratorApiManager: ExtIntegratorApiManager,
    private val votingManager: VotingManager,
    private val passportManager: PassportManager,
    private val contractManager: RarimoContractManager,
    private val identityManager: IdentityManager,
) : ViewModel() {

    val setQrVoting = votingManager::setQrVoting

    val selectedVote = votingManager.selectedPoll

}