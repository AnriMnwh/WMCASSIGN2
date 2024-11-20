package com.anari.assignment2subscriber.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anari.assignment2subscriber.DeviceReportActivity
import com.anari.assignment2subscriber.R
import com.anari.assignment2subscriber.models.Device

class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    private val devices = mutableMapOf<String, Device>()

    class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceId: TextView = view.findViewById(R.id.deviceId)
        val minSpeed: TextView = view.findViewById(R.id.minSpeed)
        val maxSpeed: TextView = view.findViewById(R.id.maxSpeed)
        val viewMore: Button = view.findViewById(R.id.viewMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices.values.elementAt(position)
        holder.deviceId.text = device.deviceId
        holder.minSpeed.text = "${String.format("%.2f", device.minSpeed)} km/h"
        holder.maxSpeed.text = "${String.format("%.2f", device.maxSpeed)} km/h"

        holder.viewMore.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DeviceReportActivity::class.java).apply {
                putExtra("deviceId", device.deviceId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = devices.size

    fun updateDevice(deviceId: String, speed: Double) {
        val device = devices.getOrPut(deviceId) {
            Device(deviceId, Double.MAX_VALUE, Double.MIN_VALUE, System.currentTimeMillis())
        }

        device.minSpeed = minOf(device.minSpeed, speed)
        device.maxSpeed = maxOf(device.maxSpeed, speed)
        device.lastUpdateTime = System.currentTimeMillis()

        notifyDataSetChanged()
    }
} 