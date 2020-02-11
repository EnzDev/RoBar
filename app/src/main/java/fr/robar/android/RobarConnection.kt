package fr.robar.android

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import fr.robar.android.robarBLE.GattManager
import fr.robar.android.robarBLE.RobarDevice
import kotlinx.android.synthetic.main.activity_robar_connection.*


class RobarConnection : AppCompatActivity() {
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val itemAdapter = ItemAdapter<RobarDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robar_connection)

        val fastAd = FastAdapter.with(itemAdapter)
        fastAd.onClickListener = { _, _, item, _ ->
            (application as RobarApplication).robarDevice = item
            startActivity(Intent(this, RobarControl::class.java))
            true
        }

        devicesList.layoutManager = LinearLayoutManager(this)
        devicesList.adapter = fastAd

        GattManager.addDevicesListener {
            Log.d("RobarConnection", it.joinToString(" , ") { robar -> robar.gattDevice.device.name })
            runOnUiThread {
                itemAdapter.setNewList(it)
            }
        }

        GattManager.addRefreshListener { refreshing ->
            swiperefresh.isRefreshing = refreshing
        }
        swiperefresh.setOnRefreshListener { GattManager.startDiscover(this, bluetoothAdapter!!.bluetoothLeScanner, 2000) }

        GattManager.startDiscover(this, bluetoothAdapter!!.bluetoothLeScanner, 5000)
    }
}
