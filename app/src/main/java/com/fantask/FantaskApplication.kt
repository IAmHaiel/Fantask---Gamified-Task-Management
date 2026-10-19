package com.fantask

import android.app.Application
import com.fantask.data.FantaskRepository
import com.fantask.data.db.FantaskDatabase
import com.fantask.data.pref.UserPreferencesRepository
import com.fantask.notify.AlarmScheduler
import com.fantask.notify.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FantaskApplication : Application() {

    lateinit var database: FantaskDatabase
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var repository: FantaskRepository
        private set

    lateinit var alarmScheduler: AlarmScheduler
        private set

    override fun onCreate() {
        super.onCreate()

        database = FantaskDatabase.getInstance(this)
        preferencesRepository = UserPreferencesRepository(this)
        repository = FantaskRepository(database, preferencesRepository)
        alarmScheduler = AlarmScheduler(this)

        NotificationHelper.createNotificationChannels(this)

        // Streak check on application launch
        CoroutineScope(Dispatchers.IO).launch {
            repository.checkDailyStreak()
        }
    }
}
