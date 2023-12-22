package com.example.carpooling;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class UpcomingRides extends AppCompatActivity implements UpComingRidesAdapter.CancelButtonClickListener {

    private RecyclerView recyclerView;
    private UpComingRidesAdapter upcomingRidesAdapter;
    private DatabaseReference reservationsDatabaseRef;
    private ProgressBar progressBar;
    private String userEmail;

//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcoming_rides_list);

        recyclerView = findViewById(R.id.recyclerViewUpcomingRides);
        progressBar = findViewById(R.id.progressBarUpcomingRides);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.Upcoming);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Upcoming) {
                return false;
            } else if (itemId == R.id.Previous) {
                Intent intent = new Intent(UpcomingRides.this, PreviousRides.class);
                startActivity(intent);
                finish();
                return true;
            } else {
                return false;
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in, and you can get the email
            userEmail = currentUser.getEmail();
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

                    Reservation reservation = reservationSnapshot.getValue(Reservation.class);

                    // Check if the ride is upcoming (you may need to define criteria)
                    if (reservation != null && reservation.getUserName().equalsIgnoreCase(userEmail) && ("Pending".equals(reservation.getStatus()) || "Accepted".equals(reservation.getStatus()))) {
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
                Log.e("UpcomingRides", "Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void updateRecyclerViewWithUpcomingRides(List<Reservation> upcomingRides) {
        upcomingRidesAdapter = new UpComingRidesAdapter(upcomingRides, this, this);
        recyclerView.setAdapter(upcomingRidesAdapter);
    }

    @Override
    public void onCancelButtonClick(Reservation reservation) {
        // Handle cancel button click for the specific reservation
        showCancelConfirmationDialog(reservation);
    }

    private void showCancelConfirmationDialog(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked Yes, cancel the reservation
                        cancelReservation(reservation);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void cancelReservation(Reservation reservation) {
        // Remove the reservation from Firebase
        reservationsDatabaseRef.child(reservation.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Reservation removed successfully, reload the activity
                    recreate();
                    // Show a message
                    showToast("Reservation removed successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("UpcomingRides", "Error removing reservation: " + e.getMessage());
                    showToast("Failed to remove reservation");
                });
    }

    private void showToast(String message) {
        // Show a Toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
