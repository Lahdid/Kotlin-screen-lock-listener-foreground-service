package com.example.workshop
import android.content.Intent
import android.os.Build
import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val intent = Intent(this, PanicService::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Demarrage mta service lel android eli baad android OREO khater mch kif
         startForegroundService(intent)
        }
        else {
            //Demarrage mta service lel android eli kbal android OREO
            startService(intent)
        }
    }

}



