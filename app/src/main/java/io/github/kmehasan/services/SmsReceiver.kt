package io.github.kmehasan.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import android.telephony.SmsMessage
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import io.github.kmehasan.R
import java.util.*


class SmsReceiver: BroadcastReceiver() {
    private fun createNotification(context: Context,phone:String,text:String) {
        var notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sms_channel", "SMS Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        // Create a notification and set its properties
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "sms_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(phone)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification as the foreground notification
        notificationManager.notify(1,builder.build())


    }
    override fun onReceive(context: Context?, intent: Intent?) {
        handleSMS(context,intent)
    }
    private fun handleSMS(context: Context?, intent: Intent?){
        if (intent?.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            val bundle: Bundle? = intent?.extras
            if (bundle != null) {
                val pdus = bundle["pdus"] as Array<*>?
                for (i in pdus!!.indices) {
                    val sms: SmsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    val messageBody: String = sms.messageBody
                    val senderNumber: String? = sms.originatingAddress
                    Log.d("SmsReceiver_getSMS", "$senderNumber : $messageBody")
                    if(context!=null){
                        createNotification(context,senderNumber?:"Number",messageBody)
                        getLocation(context,senderNumber?:"")
                    }

                    //start activity
                    val intent = Intent()
                    intent.setClassName("io.github.kmehasan", "io.github.kmehasan.MainActivity")
                    intent.putExtra("phone",senderNumber)
                    intent.putExtra("body",messageBody)

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context!!.startActivity(intent)
                }
            }
        }
    }
    fun getLocation(context: Context,to: String = ""){
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000*60*1)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(1000*45)
            .setMaxUpdateDelayMillis(1000*60*1)
            .build()
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("TAG", "getLocation: no permission")
            return
        }
        val handlerThread = HandlerThread("RequestLocation")
        handlerThread.start()
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                Log.d("TAG", "onLocationResult: "+location)
                createNotification(context,"${location?.latitude} , ${location?.longitude}", Date().time.toString())
            }
        },handlerThread.looper
        )
    }
}