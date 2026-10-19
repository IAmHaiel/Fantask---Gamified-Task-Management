package com.fantask.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fantask.data.model.BadgeConditionType
import com.fantask.data.model.BadgeEntity
import com.fantask.data.model.BadgeUnlockEntity
import com.fantask.data.model.CompletionLogEntity
import com.fantask.data.model.RewardClaimEntity
import com.fantask.data.model.RewardEntity
import com.fantask.data.model.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        CompletionLogEntity::class,
        RewardEntity::class,
        RewardClaimEntity::class,
        BadgeEntity::class,
        BadgeUnlockEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FantaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun completionLogDao(): CompletionLogDao
    abstract fun rewardDao(): RewardDao
    abstract fun rewardClaimDao(): RewardClaimDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: FantaskDatabase? = null

        fun getInstance(context: Context): FantaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FantaskDatabase::class.java,
                    "fantask.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).badgeDao().insertBadges(PRESET_BADGES)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        val PRESET_BADGES = listOf(
            BadgeEntity(
                name = "First Step",
                description = "Complete your very first task",
                iconName = "Star",
                conditionType = BadgeConditionType.TASKS_COMPLETED_TOTAL,
                threshold = 1,
                isPreset = true
            ),
            BadgeEntity(
                name = "Getting Started",
                description = "Complete 5 tasks",
                iconName = "CheckCircle",
                conditionType = BadgeConditionType.TASKS_COMPLETED_TOTAL,
                threshold = 5,
                isPreset = true
            ),
            BadgeEntity(
                name = "Task Master",
                description = "Complete 25 tasks",
                iconName = "EmojiEvents",
                conditionType = BadgeConditionType.TASKS_COMPLETED_TOTAL,
                threshold = 25,
                isPreset = true
            ),
            BadgeEntity(
                name = "Centurion",
                description = "Complete 100 tasks",
                iconName = "WorkspacePremium",
                conditionType = BadgeConditionType.TASKS_COMPLETED_TOTAL,
                threshold = 100,
                isPreset = true
            ),
            BadgeEntity(
                name = "Consistency Flame",
                description = "Maintain a 3-day streak",
                iconName = "LocalFireDepartment",
                conditionType = BadgeConditionType.STREAK_DAYS,
                threshold = 3,
                isPreset = true
            ),
            BadgeEntity(
                name = "Habit Champion",
                description = "Maintain a 7-day streak",
                iconName = "Whatshot",
                conditionType = BadgeConditionType.STREAK_DAYS,
                threshold = 7,
                isPreset = true
            ),
            BadgeEntity(
                name = "Unstoppable",
                description = "Reach an incredible 30-day streak",
                iconName = "MilitaryTech",
                conditionType = BadgeConditionType.STREAK_DAYS,
                threshold = 30,
                isPreset = true
            ),
            BadgeEntity(
                name = "Point Collector",
                description = "Earn 50 total points",
                iconName = "MonetizationOn",
                conditionType = BadgeConditionType.POINTS_EARNED_TOTAL,
                threshold = 50,
                isPreset = true
            ),
            BadgeEntity(
                name = "Fortune Seeker",
                description = "Earn 200 total points",
                iconName = "Savings",
                conditionType = BadgeConditionType.POINTS_EARNED_TOTAL,
                threshold = 200,
                isPreset = true
            ),
            BadgeEntity(
                name = "Self Rewarder",
                description = "Claim your first reward",
                iconName = "CardGiftcard",
                conditionType = BadgeConditionType.REWARDS_CLAIMED,
                threshold = 1,
                isPreset = true
            ),
            BadgeEntity(
                name = "Tough Nut",
                description = "Complete 3 Hard or Super Hard tasks",
                iconName = "Shield",
                conditionType = BadgeConditionType.HARD_TASKS_COMPLETED,
                threshold = 3,
                isPreset = true
            )
        )
    }
}
