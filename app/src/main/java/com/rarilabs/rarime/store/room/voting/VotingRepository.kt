package com.rarilabs.rarime.store.room.voting

import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.store.room.voting.models.VotingEntityData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VotingRepository @Inject constructor(private val votingDao: VotingDao) {

    suspend fun getAllVoting(): List<Poll> {
        return withContext(Dispatchers.IO) {
            val allVoting = votingDao.getAllVoting()

            allVoting.map {
                VotingEntityData.fromEntityDataToVote(it)
            }
        }
    }

    suspend fun insertVoting(vote: Poll) {
        return withContext(Dispatchers.IO) {
            val voteData = VotingEntityData.fromVoteToVotingEntityData(vote)
            votingDao.insertVoting(voteData)
        }
    }

    suspend fun updateVoting(vote: Poll) {
        return withContext(Dispatchers.IO) {
            val voteData = VotingEntityData.fromVoteToVotingEntityData(vote)
            votingDao.updateVoting(voteData)
        }
    }

    suspend fun deleteVoting(vote: Poll) {
        return withContext(Dispatchers.IO) {
            val voteData = VotingEntityData.fromVoteToVotingEntityData(vote)
            votingDao.deleteVoting(voteData)
        }
    }

    //Blocking

    fun getAllVotingSync(): List<Poll> {
        val allVoting = votingDao.getAllVotingSync()

        return allVoting.map {
            VotingEntityData.fromEntityDataToVote(it)
        }
    }

    fun insertVotingSync(vote: Poll) {
        val voteData = VotingEntityData.fromVoteToVotingEntityData(vote)
        votingDao.insertVotingSync(voteData)
    }

    fun updateVotingSync(vote: Poll) {
        val voteData = VotingEntityData.fromVoteToVotingEntityData(vote)
        votingDao.updateVotingSync(voteData)
    }

    fun deleteVotingSync(vote: Poll) {
        val voteData = VotingEntityData.fromVoteToVotingEntityData(vote)
        votingDao.deleteVotingSync(voteData)
    }
}