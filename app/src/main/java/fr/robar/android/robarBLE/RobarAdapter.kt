package fr.robar.android.robarBLE

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.robar.android.R

class RobarAdapter(private val robarList: ArrayList<RobarDevice>) : RecyclerView.Adapter<RobarDevice.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RobarDevice.ViewHolder {
        // create view holder to hold reference
        return RobarDevice.ViewHolder( LayoutInflater.from(parent.context).inflate(RobarDevice.LAYOUT, parent, false))
    }

    override fun onBindViewHolder(holder: RobarDevice.ViewHolder, position: Int) {
        //set values
        holder.bindView(robarList[position], mutableListOf())
    }

    override fun getItemCount(): Int {
        return robarList.size
    }
    // update your data
    fun updateData(scanResult: List<RobarDevice>) {
        robarList.clear()
        notifyDataSetChanged()
        robarList.addAll(scanResult)
        notifyDataSetChanged()

    }
}