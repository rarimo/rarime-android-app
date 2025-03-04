package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.vote_handler

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.votes.QuestionAnswerVariant
import com.rarilabs.rarime.modules.votes.VoteData
import com.rarilabs.rarime.modules.votes.VoteQuestion
import com.squareup.moshi.Json
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProposalInfoDetailsConfigJSON(
    val startTimestamp: Long,
    val duration: Long,
    val multichoice: String,
    val acceptedOptions: List<String>,
    val Description: String,
    val votingWhitelist: List<String>,
    val votingWhitelistData: List<ByteArray>
)

data class ProposalInfoDetailsJSON(
    val proposalSMT: String,
    val status: Int,
    val config: ProposalInfoDetailsConfigJSON,
    val votingResults: List<List<String>>
)

data class ProposalInfoJSON(
    val proposalInfo: ProposalInfoDetailsJSON,
    val proposalEventId: String
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

@HiltViewModel
class VoteHandlerViewModel @Inject constructor(
    private val extIntegratorApiManager: ExtIntegratorApiManager,
    private val passportManager: PassportManager,
    private val contractManager: RarimoContractManager,
    private val identityManager: IdentityManager,
) : ViewModel() {
    private var _proposalData = MutableStateFlow<ProposalInfoJSON?>(null)
    val proposalData: StateFlow<ProposalInfoJSON?>
        get() = _proposalData.asStateFlow()

    private var _proposalMetadata = MutableStateFlow<ProposalMetadata?>(null)
    val proposalMetadata: StateFlow<ProposalMetadata?>
        get() = _proposalMetadata.asStateFlow()

    private val _voteData = MutableStateFlow<VoteData?>(null)
    val voteData: StateFlow<VoteData?> = _voteData.asStateFlow()

    suspend fun loadDetails(proposalId: String) {
        val proposalInfoBytes = Identity.getProposalInfo(
            "0xf4B99A3891D0a64A0bc3bB8642242E6A01e104e2",
            "https://rpc.qtestnet.org",
            proposalId
        )

        _proposalData.value = Gson().fromJson(proposalInfoBytes.decodeToString(), ProposalInfoJSON::class.java)

        println(
            _proposalData.value.toString()
        )

        val metadataStr = extIntegratorApiManager.queryIpfsData("https://ipfs.rarimo.com/ipfs/" + _proposalData.value!!.proposalInfo.config.Description)

        println(metadataStr)

        _proposalMetadata.value = Gson().fromJson(
            metadataStr,
            ProposalMetadata::class.java
        )

        _voteData.value = VoteData(
            title = _proposalMetadata.value!!.title,
            description = _proposalMetadata.value!!.description,
            durationMillis = _proposalData.value!!.proposalInfo.config.duration,
            participantsCount = 0, // TODO: implement me
            questions = _proposalMetadata.value!!.acceptedOptions.mapIndexed { index, question ->
                VoteQuestion(
                    id = index.toString(),
                    title = question.title,
                    variants = question.variants.mapIndexed { i, variant ->
                        QuestionAnswerVariant(
                            id = i.toString(),
                            title = variant,
                            votedCount = 0.0 // TODO: implement me
                        )
                    },
                )
            },
            endDate = (_proposalData.value!!.proposalInfo.config.startTimestamp + _proposalData.value!!.proposalInfo.config.duration) * 1000
        )
    }

    suspend fun vote(voteOptions: Map<String, String>) {
        println("VoteHandlerViewModel.vote")

        voteOptions.forEach {
            println("Vote option: ${it.key} -> ${it.value}")
        }
    }
}