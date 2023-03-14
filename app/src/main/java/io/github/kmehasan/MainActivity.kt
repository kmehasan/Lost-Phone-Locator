package io.github.kmehasan

import android.Manifest.permission.READ_SMS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private val RC_READ_SMS = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startServiceSmsReadService()

        var phone = intent.getStringExtra("phone")
        var body = intent.getStringExtra("body")
        if(phone!=null && body!=null){
            findViewById<TextView>(R.id.message).text = "$phone : $body"
        }


    }

    private fun startServiceSmsReadService() {
        // Check if the READ_SMS permission is already granted
        if (EasyPermissions.hasPermissions(this, READ_SMS,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )) {
            // Permission is already granted, do your work here
        } else {
            // Request the READ_SMS permission
            EasyPermissions.requestPermissions(this, "This app needs permission to read SMS and locations",
                RC_READ_SMS,
                READ_SMS,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == RC_READ_SMS) {
            // Permission is granted, do your work here
            Toast.makeText(this,"Permission Granted!!!",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == RC_READ_SMS) {
            // Permission is denied, show a message or take some action
            Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
        }
    }


}