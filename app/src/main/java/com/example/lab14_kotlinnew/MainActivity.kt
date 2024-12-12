package com.example.lab14_kotlinnew

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val markers = mutableListOf<Marker>() // 保存已新增的標記

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (checkLocationPermission()) {
            map.isMyLocationEnabled = true
            addMarkers()
            drawPolyline()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(25.035, 121.54), 13f))
            setMapLongClickListener()  // 設置長按監聽器
            setMarkerClickListener()   // 設置標記點擊監聽器
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0
        )
    }

    private fun addMarkers() {
        val locations = listOf(
            LatLng(25.033611, 121.565000) to "台北101",
            LatLng(25.047924, 121.517081) to "台北車站"
        )
        locations.forEach { (latLng, title) ->
            val marker = map.addMarker(MarkerOptions().position(latLng).title(title).draggable(true))
            if (marker != null) {
                markers.add(marker)  // 新增標記到列表中
            }
        }
    }

    private fun drawPolyline() {
        val polylineOpt = PolylineOptions()
            .add(LatLng(25.033611, 121.565000))
            .add(LatLng(25.032435, 121.534905))
            .add(LatLng(25.047924, 121.517081))
            .color(Color.BLUE)
            .width(10f)
        map.addPolyline(polylineOpt)
    }

    private fun setMapLongClickListener() {
        map.setOnMapLongClickListener { latLng ->
            // 在長按的地方新增大頭針
            val marker = map.addMarker(MarkerOptions().position(latLng).title("新標記").draggable(true))
            if (marker != null) {
                markers.add(marker)  // 將新增的標記保存到列表
            }
        }
    }

    private fun setMarkerClickListener() {
        map.setOnMarkerClickListener { marker ->
            // 當點擊標記時，彈出提示框詢問是否刪除
            showDeleteDialog(marker)
            true  // 返回 true 表示已處理該事件
        }
        
    }

    private fun showDeleteDialog(marker: Marker) {
        // 使用 AlertDialog 來詢問是否刪除標記
        AlertDialog.Builder(this)
            .setTitle("刪除標記")
            .setMessage("確定要刪除該標記嗎？")
            .setPositiveButton("刪除") { dialog, _ ->
                // 刪除標記
                markers.remove(marker)   // 從列表中移除標記
                marker.remove()          // 從地圖上移除標記
                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()  // 取消操作
            }
            .show()
    }

    private fun loadMap() {
        (supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment)
            .getMapAsync(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            loadMap()
        } else {
            finish()
        }
    }
}
