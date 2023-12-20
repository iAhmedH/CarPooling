package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpComingDriverRides extends AppCompatActivity implements UpComingDriverRidesAdapter.ButtonClickListener {

    private RecyclerView recyclerView;
    private UpComingDriverRidesAdapter upcomingDriverRidesAdapter;
    private DatabaseReference reservationsDatabaseRef;
    private ProgressBar progressBar;
    private String driverEmail;

    @Override
    protected void onResume() {
        super.onResume();
        // Set no item as selected when the activity is resumed
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomDriverNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_empty);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcoming_driver_rides_list);

        recyclerView = findViewById(R.id.recyclerViewUpcomingRides);
        progressBar = findViewById(R.id.progressBarUpcomingRides);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomDriverNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_empty);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Upcoming) {
                return false;
            } else if (itemId == R.id.OnGoing) {
                Intent intent = new Intent(UpComingDriverRides.this, OnGoingDriverRides.class);
                startActivity(intent);
                finish();
                return true;
            }
            else if (itemId == R.id.AddRide) {
                Intent intent = new Intent(UpComingDriverRides.this, AddRide.class);
                startActivity(intent);
                finish();
                return true;
            }
            else if (itemId == R.id.Previous) {
                Intent intent = new Intent(UpComingDriverRides.this, PreviousDriverRides.class);
                startActivity(intent);
                finish();
                return true;
            }
            else {
                return false;
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();



        if (currentUser != null) {
            // User is signed in, and you can get the email
            driverEmail = currentUser.getEmail();
        } else {
            // No user is signed in, handle this case accordingly
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Set up database reference
        reservationsDatabaseRef = FirebaseDatabase.getInstance().getReference("reservations");

        // Load upcoming rides
        loadUpcomingRides();
    }

    private void loadUpcomingRides() {
        // Show ProgressBar while loading
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve upcoming rides from Firebase
        reservationsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Reservation> upcomingRides = new ArrayList<>();

                for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot driverSnapshot = reservationSnapshot.child("driverEmail");
                    String reservationDriverEmail = driverSnapshot.getValue(String.class);
                    Reservation reservation = reservationSnapshot.getValue(Reservation.class);
                    if (reservationDriverEmail != null && reservationDriverEmail.equalsIgnoreCase(driverEmail) && "Pending".equals(reservation.getStatus())) {
                        upcomingRides.add(reservation);
                    }
                }

                // Update RecyclerView with upcoming rides
                updateRecyclerViewWithUpcomingRides(upcomingRides);

                // Hide ProgressBar after loading
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                // ...
            }
        });
    }


    private void updateRecyclerViewWithUpcomingRides(List<Reservation> upcomingRides) {
        upcomingDriverRidesAdapter = new UpComingDriverRidesAdapter(upcomingRides, this);
        recyclerView.setAdapter(upcomingDriverRidesAdapter);
    }

    @Override
    public void onFirstButtonClick(Reservation reservation) {
        // Handle the click on the first button
        acceptReservationStatus(reservation);
    }
    @Override
    public void onSecondButtonClick(Reservation reservation) {
        // Handle the click on the second button
        rejectReservationStatus(reservation);
    }

    // Method to accept reservation status
    private void acceptReservationStatus(Reservation reservation) {
        DatabaseReference reservationRef = reservationsDatabaseRef.child(reservation.getId());

        // Update the status to "Accepted"
        reservation.setStatus("Accepted");

        // Update the status in Firebase
        reservationRef.child("status").setValue(reservation.getStatus());

        // Notify the adapter about the data change if needed
        upcomingDriverRidesAdapter.notifyDataSetChanged();
    }
    // Method to reject reservation status
    private void rejectReservationStatus(Reservation reservation) {
        DatabaseReference reservationRef = reservationsDatabaseRef.child(reservation.getId());


        reservation.setStatus("Rejected");

        reservationRef.child("status").setValue(reservation.getStatus());
        // Update the status in Firebase
        reservationRef.setValue(reservation);

        // Notify the adapter about the data change if needed
        upcomingDriverRidesAdapter.notifyDataSetChanged();
    }

}
