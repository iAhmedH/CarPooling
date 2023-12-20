package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RideDetails extends AppCompatActivity {

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_booking);

        // Retrieve data from the intent
        String driverName = getIntent().getStringExtra("driverName");
        String driverEmail = getIntent().getStringExtra("driverEmail");
        String location = getIntent().getStringExtra("location");
        String time = getIntent().getStringExtra("time");
        String carType = getIntent().getStringExtra("carType");
        String carModel = getIntent().getStringExtra("carModel");
        String rideID = getIntent().getStringExtra("rideID");
        String price = getIntent().getStringExtra("price");
        String seats = getIntent().getStringExtra("seats");
        String selectedRoute = getIntent().getStringExtra("selectedRoute");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in, and you can get the email
            userEmail = currentUser.getEmail();
        } else {
            // No user is signed in, handle this case accordingly
        }

        // Populate views in the layout with the retrieved data
        TextView driverNameTextView = findViewById(R.id.textDriverName);
        TextView locationTextView = findViewById(R.id.textLocation);
        TextView timeTextView = findViewById(R.id.textTime);
        TextView carTypeTextView = findViewById(R.id.textCarType);
        TextView carModelTextView = findViewById(R.id.textCarModel);
        TextView priceTextView = findViewById(R.id.textPrice);
        TextView seatsTextView = findViewById(R.id.textSeats);

        driverNameTextView.setText("Driver's Name: " + driverName);
        locationTextView.setText("Location: " + location);
        timeTextView.setText("Time: " + time);
        carTypeTextView.setText("Car Type: " + carType);
        carModelTextView.setText("Car Model: " + carModel);
        priceTextView.setText("Price: " + price);
        seatsTextView.setText("Seats: " + seats);

        // Add a listener to the book button
        Button bookButton = findViewById(R.id.bookButton);
        bookButton.setOnClickListener(v -> {
            // Perform the reservation and save to Firebase
            saveReservation(driverName, location, time, carType, carModel, rideID, price, seats, driverEmail, selectedRoute);
        });
    }

    private void saveReservation(String driverName, String location, String time, String carType, String carModel, String rideID, String price, String seats, String driverEmail, String selectedRoute) {
        // Create a DatabaseReference to the "reservations" node in your Firebase database
        DatabaseReference reservationsRef = FirebaseDatabase.getInstance().getReference("reservations");

        // Generate a unique key for the reservation
        String reservationId = reservationsRef.push().getKey();

        // Create a Reservation object
        Reservation reservation = new Reservation (reservationId, driverName, location, time, carType, carModel, rideID, "Pending", userEmail, price, seats, driverEmail);

        // Save the reservation to Firebase
        reservationsRef.child(reservationId).setValue(reservation);

        // Display a success message
        Toast.makeText(this, "Booked successfully!", Toast.LENGTH_SHORT).show();

        // Get the reference to the "routes" node in your Firebase database
        DatabaseReference routesRef = FirebaseDatabase.getInstance().getReference("routes");

        // Find and remove the ride from the associated route
        routesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    if (route != null && route.getRides() != null && route.getRides().containsKey(rideID)) {
                        // Remove the ride from the route
                        routesRef.child(selectedRoute).child("rides").child(rideID).removeValue();
                        navigateBackToRoutesScreen();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Firebase", "Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
    }
    private void navigateBackToRoutesScreen() {
        // You can use Intent to navigate back to the routes screen

        Intent intent = new Intent(RideDetails.this, Routes.class);
        startActivity(intent);
        // Finish the current activity to remove it from the back stack
        finish();
    }
}
