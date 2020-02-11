package fr.robar.android

import android.Manifest.permission.*
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*


class TrashActivity : AppCompatActivity() {

    val TAG = "ROBAR_MainActivity"


    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }



    private var mScanning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if ((ContextCompat.checkSelfPermission(
                this,
                BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                this,
                BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            )) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted

            ActivityCompat.requestPermissions(
                this, arrayOf(
                    BLUETOOTH,
                    BLUETOOTH_ADMIN,
                    ACCESS_FINE_LOCATION
                ), 1
            )
        }


        initScanning(bluetoothAdapter!!.bluetoothLeScanner)

    }


    private fun initScanning(bleScanner: BluetoothLeScanner) {
        bleScanner.startScan(getScanCallback())
    }

    private fun getScanCallback(): ScanCallback? {
        return object : ScanCallback() {
            override fun onScanResult(callbackType: Int, scanResult: ScanResult) {
                super.onScanResult(callbackType, scanResult)
                getServiceUUIDsList(scanResult)
            }
        }
    }

    var once = false

    private fun getServiceUUIDsList(scanResult: ScanResult) {
        if(once) return
        if (scanResult.device.address.toLowerCase() == "a4:cf:12:85:d3:4e") {
            once = true
            scanResult.scanRecord?.serviceData
            scanResult.device.connectGatt(this, true, GattCallback)
        }
    }

    val GattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.")
                // Attempts to discover services after successful connection.
                Log.i(
                    TAG, "Attempting to start service discovery:" +
                            gatt?.discoverServices()
                )
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.i(TAG, "onServicesDiscovered received: $status")
            val characteristic = gatt!!.services[2].characteristics[0]
            characteristic.value = byteArrayOf(1)
            gatt.writeCharacteristic(characteristic)
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Log.i(TAG, "onCharacteristicRead received: $status")
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            Log.i(TAG, "onCharacteristicChanged received: $characteristic")
        }
    }
}
