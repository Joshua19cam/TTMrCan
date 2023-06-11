package com.example.ttmrcan

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat

class AlarmNotification: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val titulo = intent.getStringExtra("titulo")
        val subtitulo = intent.getStringExtra("subtitulo")
        val idNotifi = intent.getIntExtra("id",0)

        createSimpleNotification(context, titulo.toString(), subtitulo.toString(),idNotifi)
    }

    private fun createSimpleNotification(context: Context, title: String, subtitle: String, notificationId: Int) {
        val intent = Intent(context, FlashScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val notification = NotificationCompat.Builder(context, FragmentoCitas.MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mascota)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)

    }
}