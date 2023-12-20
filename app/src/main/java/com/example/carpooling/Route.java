package com.example.carpooling;

import java.util.Map;

public class Route {
    private String name;
    private String location;
    private Map<String, Ride> rides;  // Include this attribute

    // Required no-argument constructor for Firebase deserialization
    public Route() {
        // Default constructor required for calls to DataSnapshot.getValue(Route.class)
    }

    public Route(String name, String location, Map<String, Ride> rides) {
        this.name = name;
        this.location = location;
        this.rides = rides;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Map<String, Ride> getRides() {
        return rides;
    }

    public void setRides(Map<String, Ride> rides) {
        this.rides = rides;
    }
}
