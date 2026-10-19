package com.fantask.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fantask.data.db.FantaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == Intent.ACTION_TIME_CHANGED
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = FantaskDatabase.getInstance(context)
                    val scheduler = AlarmScheduler(context)
                    scheduler.rescheduleAllAlarms(db)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
