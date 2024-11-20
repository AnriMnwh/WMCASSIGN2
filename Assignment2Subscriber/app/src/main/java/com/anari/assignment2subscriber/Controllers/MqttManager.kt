package com.anari.assignment2subscriber.Controllers

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client

class MqttManager {
    private var client: Mqtt5BlockingClient? = null
    
    fun connect(clientId: String) {
        client = Mqtt5Client.builder()
            .identifier(clientId)
            .serverHost("broker.sundaebytestt.com")
            .serverPort(1883)
            .build()
            .toBlocking()
    }

    fun subscribe(onMessageReceived: (String) -> Unit) {
        client?.toAsync()?.subscribeWith()?.topicFilter("assignment/location")?.callback { publish ->
            onMessageReceived(String(publish.payloadAsBytes))
        }?.send()
    }

    fun disconnect() {
        client?.disconnectWith()?.send()
        client = null
    }
} 