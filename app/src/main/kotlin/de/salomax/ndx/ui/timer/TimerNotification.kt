package de.salomax.ndx.ui.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.salomax.ndx.R
import de.salomax.ndx.util.TextUtils.toTimeString

class TimerNotification(private val context: Context) {

   private val channelId = "de.salomax.ndx.timer.timer_notification"

   fun getTimerNotification(millisCurrent: Long?, millisTotal: Long?): Notification {
      createNotificationChannel()

      val notificationBuilder = NotificationCompat.Builder(context, channelId)
         .setSmallIcon(R.drawable.ic_timer_white_24dp)
         .setTicker("${context.getString(R.string.app_name)} ${context.getString(R.string.title_timer)}") // NDx Timer
         .setContentText(context.getString(R.string.title_timer)) // Timer
         .setShowWhen(false)
         .setPriority(NotificationCompat.PRIORITY_DEFAULT)
         .setOnlyAlertOnce(true)
         // click on notification: go back to activity
         .setContentIntent(Intent(context, TimerActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(
               context,
               0,
               notificationIntent,
               PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
         })
      if (millisCurrent != null && millisTotal != null) {
         notificationBuilder
            .setContentTitle(millisTotal.minus(millisCurrent).toTimeString())
      }

      return notificationBuilder.build()
   }

   private fun createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         val channel = NotificationChannel(
            channelId,
            "${context.getString(R.string.app_name)} ${context.getString(R.string.title_timer)}", // NDx Timer
            NotificationManager.IMPORTANCE_LOW
         )
         channel.setSound(null, null)
         // Register the channel with the system
         NotificationManagerCompat.from(context).createNotificationChannel(channel)
      }
   }

}
