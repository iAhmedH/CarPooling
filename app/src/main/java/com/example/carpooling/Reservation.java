package com.example.carpooling;

public class Reservation {
    private String id;
    private String driverName;
    private String location;
    private String time;
    private String carType;
    private String carModel;

    private String rideID;

    private String status;

    private String userName;

    private String price;

    private String seats;

    private String driverEmail;
    // Default constructor (required for Firebase)
    public Reservation() {
    }

    // Constructor to initialize the fields
    public Reservation(String id, String driverName, String location, String time, String carType, String carModel, String rideID, String status, String userName, String price, String seats, String driverEmail) {
        this.id = id;
        this.driverName = driverName;
        this.location = location;
        this.time = time;
        this.carType = carType;
        this.carModel = carModel;
        this.rideID = rideID;
        this.status = status;
        this.userName = userName;
        this.price = price;
        this.seats = seats;
        this.driverEmail = driverEmail;
    }

    // Getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }
    public String getRideID() {
        return rideID;
    }

    public void setRideID(String rideID) {
        this.rideID = rideID;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String seats) {
        this.driverEmail = driverEmail;
    }
}
