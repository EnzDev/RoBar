package fr.robar.android.robarBLE

import android.bluetooth.BluetoothGatt
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import fr.robar.android.R
import kotlinx.android.synthetic.main.robar_device_item_layout.view.*
import java.util.*

open class RobarDevice(
    var gattDevice: BluetoothGatt
) : AbstractItem<RobarDevice.ViewHolder>() {
    override fun equals(other: Any?): Boolean {
        if (other !is RobarDevice) return false
        return gattDevice.device.address == other.gattDevice.device.address
    }

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.robar_device_item

    /** defines the layout which will be used for this item in the list  */
    override val layoutRes: Int
        get() = R.layout.robar_device_item_layout

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + gattDevice.device.address.hashCode()
        return result
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<RobarDevice>(view) {
        var name = view.name
        var address = view.address

        override fun bindView(item: RobarDevice, payloads: MutableList<Any>) {
            name.text = item.gattDevice.device.name
            address.text = item.gattDevice.device.address
        }

        override fun unbindView(item: RobarDevice) {
            name.text = null
            address.text = null
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214")
        val LAYOUT = R.layout.robar_device_item_layout
    }
}