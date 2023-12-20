package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class PreviousRides extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PreviousRidesAdapter previousRidesAdapter;
    private DatabaseReference reservationsDatabaseRef;
    private ProgressBar progressBar;
    private String userEmail;

    @Override
    protected void onResume() {
        super.onResume();
        // Set no item as selected when the activity is resumed
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_empty);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previous_rides);

        recyclerView = findViewById(R.id.recyclerViewUpcomingRides);
        progressBar = findViewById(R.id.progressBarUpcomingRides);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_empty);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Upcoming) {
                Intent intent = new Intent(PreviousRides.this, UpcomingRides.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.Previous) {
                return false;
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


        // Load previous rides
        loadPreviousRides();
    }

    private void loadPreviousRides() {
        // Show ProgressBar while loading
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve previous rides from Firebase
        reservationsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Reservation> previousRides = new ArrayList<>();

                for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {

                    Reservation reservation = reservationSnapshot.getValue(Reservation.class);

                    Log.d("PreviousRides" , "Username -> " + reservation.getUserName());
                    if (reservation != null && reservation.getUserName().equalsIgnoreCase(userEmail) && ("Completed".equals(reservation.getStatus()) || "Rejected".equals(reservation.getStatus()))) {
                        previousRides.add(reservation);
                    }
                }

                // Update RecyclerView with previous rides
                updateRecyclerViewWithPreviousRides(previousRides);

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

    private void updateRecyclerViewWithPreviousRides(List<Reservation> previousRides) {
        previousRidesAdapter = new PreviousRidesAdapter(previousRides);
        recyclerView.setAdapter(previousRidesAdapter);
    }
}
