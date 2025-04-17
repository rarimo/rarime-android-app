package com.rarilabs.rarime.store.room.notifications

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import com.rarilabs.rarime.store.room.voting.VotingDao
import com.rarilabs.rarime.store.room.voting.models.VotingEntityData

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notifications ADD COLUMN type TEXT")
        db.execSQL("ALTER TABLE notifications ADD COLUMN data TEXT")
    }
}


val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS voting (
                proposalId INTEGER NOT NULL PRIMARY KEY
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE voting ADD COLUMN votingBlob TEXT NOT NULL DEFAULT ''")
    }
}

@Database(
    entities = [NotificationEntityData::class, VotingEntityData::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationsDao(): NotificationsDao

    abstract fun votingDao(): VotingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "room_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}