package fr.robar.android.robarBLE

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.util.Log

object GattManager {
    private val TAG = "ROBAR_GattManager"

    private val handler: Handler = Handler()

    private val ban = mutableSetOf<String>()

    val robarDevices = mutableSetOf<RobarDevice>()
    val deviceListeners = mutableListOf<(List<RobarDevice>) -> Unit>()
    val refreshListeners = mutableListOf<(Boolean) -> Unit>()

    var bleScanner: BluetoothLeScanner? = null
    var scanContext: Context? = null

    private fun startDiscover(context: Context, scanner: BluetoothLeScanner) {
        refreshListeners.forEach { it(true) }
        Log.d(TAG, "Start Gattmanager Discover")
        bleScanner?.stopScan(BLEScanner)
        bleScanner = scanner
        val t = bleScanner?.startScan(BLEScanner)
        this.scanContext = context
    }

    fun startDiscover(context: Context, scanner: BluetoothLeScanner, `for`: Long) {
        startDiscover(context, scanner)
        handler.postDelayed({
            stopDiscover()
        }, `for`)
    }

    private fun stopDiscover() {
        refreshListeners.forEach { it(false) }
        Log.d(TAG, "Stop Gattmanager Discover")
        bleScanner?.stopScan(BLEScanner)
        this.scanContext = null
        this.bleScanner = null
    }

    private val BLEScanner = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, scanResult: ScanResult) {
            if (robarDevices.find { it.gattDevice.device.address == scanResult.device.address } != null) return
            if (ban.contains(scanResult.device.address)) return // Prevent asking services a device twice
            scanResult.device.connectGatt(scanContext, true, GattScanner)
        }
    }

    val GattScanner = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server:  ${gatt?.device?.address}")
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery: ${gatt?.discoverServices()}")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server: ${gatt?.device?.address}")
                if (gatt != null && robarDevices.remove(RobarDevice(gatt)))
                    notifyDeviceListener()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.i(TAG, "onServicesDiscovered received: $status")
            if (gatt != null) {
                gatt.services.find { it.uuid == RobarDevice.SERVICE_UUID }?.also {
                    robarDevices.add(RobarDevice(gatt))
                    notifyDeviceListener()
                } ?: run {
                    ban.add(gatt.device.address)
                    gatt.disconnect()
                }

            }
        }
    }

    private fun notifyDeviceListener() {
        deviceListeners.forEach { it(robarDevices.toList()) }
    }

    fun addRefreshListener(listener: (refreshing: Boolean) -> Unit) {
        refreshListeners.add(listener)
    }
    fun addDevicesListener(listener: (List<RobarDevice>) -> Unit) {
        deviceListeners.add(listener)
    }
}
