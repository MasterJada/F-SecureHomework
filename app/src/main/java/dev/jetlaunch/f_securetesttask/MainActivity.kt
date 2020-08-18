package dev.jetlaunch.f_securetesttask

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jetlaunch.locationtracker.LocationService
import dev.jetlaunch.locationtracker.api.LocationApi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val locApi by lazy { LocationApi() }
    private val adapter by lazy { CoordsAdapter() }

    private val btnState = arrayOf("Start service", "Stop service")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt_service.text = btnState[if (LocationService.isOnline) 1 else 0]
        bt_service.setOnClickListener(this)
        bt_update_timeout.setOnClickListener(this)
        rv_coords.layoutManager = LinearLayoutManager(this)
        rv_coords.adapter = adapter
        loadData()
    }


    private fun loadData(){
        tv_update_timeout.setText( locApi.getCurrentTimeout(this).toString())
        adapter.items = locApi.getLastLocation(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.bt_service -> {
                if(locApi.isServiceActive()) {
                    locApi.stopLocationUpdates(this)
                    bt_service.text = btnState[0]
                }else{
                  startLocationRequests()
                }

            }
            R.id.bt_update_timeout -> {
                sendTimeoutUpdate()
            }
        }
    }

    private fun sendTimeoutUpdate() {
        val time = tv_update_timeout.text.toString().toLongOrNull()

        if (time != null && time > 0L) {
            locApi.changeLocationTimeout(time, this)
        } else {
            Toast.makeText(this, "Value must be only digit and > 0", Toast.LENGTH_SHORT).show()
        }
    }


    private fun startLocationRequests(){
        if(  Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            locApi.startLocationUpdates(this)
            bt_service.text = btnState[1]
            return
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            locApi.startLocationUpdates(this)
            bt_service.text = btnState[1]
        }else{
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 900)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            grantResults[1] == PackageManager.PERMISSION_GRANTED){
            startLocationRequests()
        }
    }
}