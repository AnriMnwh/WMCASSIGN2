package com.anari.assignment2subscriber.models
//made this as I wanted a permanent data class to hold information in
data class Device(
    val deviceId: String,
    var minSpeed: Double,
    var maxSpeed: Double,
    var lastUpdateTime: Long
) 