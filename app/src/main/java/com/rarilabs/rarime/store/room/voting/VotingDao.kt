package com.rarilabs.rarime.store.room.voting

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rarilabs.rarime.store.room.voting.models.VotingEntityData

@Dao
interface VotingDao {
    @Query("SELECT * FROM voting")
    suspend fun getAllVoting(): List<VotingEntityData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoting(vote: VotingEntityData)

    @Update
    suspend fun updateVoting(vote: VotingEntityData)

    @Delete
    suspend fun deleteVoting(vote: VotingEntityData)

    @Query("SELECT * FROM voting")
    fun getAllVotingSync(): List<VotingEntityData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVotingSync(vote: VotingEntityData)

    @Update
    fun updateVotingSync(vote: VotingEntityData)

    @Delete
    fun deleteVotingSync(vote: VotingEntityData)

}