package com.example.workshop

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast


class PanicService : Service() {

    val Service_ID = 1
    private var clickTimeStamps = mutableListOf<Long>()



    //Receiver eli yokeed yestana screen locked
    private val Receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            // ken lka screen tsakret
            if (action == Intent.ACTION_SCREEN_OFF) {
                // ken screen off izid el timestamp mta screen off lel lista bech icompareha baad
                clickTimeStamps.add(System.currentTimeMillis())
                Log.d("Timestamps mta clicks",clickTimeStamps.toString())
                // Check if the last 5 screen off events have a time difference of more than 10 seconds
                if (clickTimeStamps.size >= 5) {
                    val lastFiveClicks = clickTimeStamps.takeLast(5)
                    val timeDiff = lastFiveClicks.last() - lastFiveClicks.first()
                    Log.d("El wakt bin clicks",timeDiff.toString())
                    if (timeDiff < 10000) {

                        Toast.makeText(context, "Screen lock button pressed 5 consecutive times !", Toast.LENGTH_SHORT).show()
                        clickTimeStamps.clear()
                    }
                }
            }
            // ken tel tsaker w taawed thal
            else if (action == Intent.ACTION_BOOT_COMPLETED) {
                // ken tel tsaker w taawed thal lezm naawdo start service bech yokeed dima yekhdem service
                val serviceIntent = Intent(context, PanicService::class.java)
                context?.startService(serviceIntent)
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onCreate() {
        super.onCreate()


        //Notification
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            val channelId = "PanicService"
            val channel = NotificationChannel(channelId,"Default",NotificationManager.IMPORTANCE_DEFAULT)
           val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification =  Notification.Builder(this,channelId).apply {
                setContentTitle("We Parent is running in the background")
                setContentText("Press the volume up button 5 times if you're in danger.")
                setSmallIcon(R.drawable.ic_launcher_foreground)
            }.build()
            startForeground(Service_ID,notification)
        }

        //Register el receiver bech yestana el "Screen off"
        val screenOffFilter = IntentFilter("android.intent.action.SCREEN_OFF")
        registerReceiver(Receiver, screenOffFilter)

        //Register el receiver bech yestana el reboot mta tel
        val rebootFilter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
        registerReceiver(Receiver, rebootFilter)


    }




    override fun onDestroy() {
        super.onDestroy()
        // Unregister el receiver kif service yekef
        unregisterReceiver(Receiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}