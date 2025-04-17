package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.api.voting.VotingManager
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VoteAppSheetViewModel @Inject constructor(
    private val votingManager: VotingManager,
    private val settingsManager: SettingsManager,
    private val registrationManager: RegistrationManager,
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager
) : ViewModel() {

    val checkIsVoted = votingManager::checkIsVoted

    val vote = votingManager::vote

    val currentSchema = settingsManager.colorScheme

    val passport = passportManager.passport

    val a = identityManager.registrationProof

    val registrationProof = registrationManager.registrationProof

    fun setSelectedPoll(poll: Poll?) {
        votingManager.setSelectedPoll(poll)
    }

}

