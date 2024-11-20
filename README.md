
# COMP 3606 Assignment 2.
# Anari Manwah 816024772
### About
This project has taken me so long to make. It has been redone over and over again and I have actually learnt alot more than I thought. It was hard as hell to figure out what I was doing, but the lab resources, as well as the online resources really pulled me through this. 
Sir, I may have lost half of my sanity, and hair doing this, but it works. I followed guides and solutions for specific sections (such as the calculations of speed and distance) and I also used guides for the google maps API. I also used the android studio guide to help me figure out some of the more complex functions of this assignment.

## MQTT Configuration
- **Broker:** broker.sundaebytestt.com
- **Port:** 1883
- **Topic:** assignment/location

## Publisher Application

An Android application that publishes location data to an MQTT broker.

### Features
- User input for student ID identification
- Location tracking and publishing
- Start/Stop transmission controls
- Real-time location updates every 5 seconds or 5 meters of movement

### Usage
1. Enter your student ID in the provided text field
2. Press "Start Publishing" to begin location transmission
3. Press "Stop Publishing" to cease transmission

## Subscriber Application

An Android application that receives, stores, and visualizes location data from multiple publishers.

### Features
- Real-time location tracking on Google Maps
- Device path visualization using polylines
- Speed statistics tracking (min/max speeds)
- SQLite database storage
- Detailed reporting system with date/time filtering

### Main Screen
- Google Maps display showing all active devices
- Device list showing:
  - Device ID
  - Minimum Speed
  - Maximum Speed
  - "View More" option for detailed reports


### Key Components
- MQTT Communication using HiveMQ client
- Google Maps API for visualization
- SQLite database for data persistence
- Location services integration
- RecyclerView for device listing
- DateTimePicker for report filtering

### Resources
- https://www.youtube.com/watch?v=Nnt-xgwn3YQ
- https://www.youtube.com/watch?v=_xUcYfbtfsI
- https://www.youtube.com/watch?v=pOKPQ8rYe6g
- https://youtu.be/7TIAT5zlrmc?si=H87Ry7J5NsuJ9A_h
- https://youtu.be/CdDXbvBFXLY?si=MVhosa5iz8hkf0Va
- https://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2
- https://stackoverflow.com/questions/21536116/calculating-distance-traveled-in-android-google-maps
- https://medium.com/codex/mqtt-in-android-ff3b083f2221
- https://cedalo.com/blog/mqtt-on-android-guide-using-mosquitto/
- https://medium.com/swlh/android-and-mqtt-a-simple-guide-cb0cbba1931c
-     

