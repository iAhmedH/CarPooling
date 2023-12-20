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

public class OnGoingDriverRides extends AppCompatActivity implements OnGoingDriverRidesAdapter.ButtonClickListener {

    private RecyclerView recyclerView;
    private OnGoingDriverRidesAdapter ongoingDriverRidesAdapter;
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
        setContentView(R.layout.ongoing_rides);

        recyclerView = findViewById(R.id.recyclerViewUpcomingRides);
        progressBar = findViewById(R.id.progressBarUpcomingRides);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomDriverNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_empty);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Upcoming) {
                Intent intent = new Intent(OnGoingDriverRides.this, UpComingDriverRides.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.OnGoing) {
                return false;
            }
            else if (itemId == R.id.AddRide) {
                Intent intent = new Intent(OnGoingDriverRides.this, AddRide.class);
                startActivity(intent);
                finish();
                return true;
            }
            else if (itemId == R.id.Previous) {
                Intent intent = new Intent(OnGoingDriverRides.this, PreviousDriverRides.class);
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
        loadOnGoingRides();
    }

    private void loadOnGoingRides() {
        // Show ProgressBar while loading
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve ongoing rides from Firebase
        reservationsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Reservation> onGoingRides = new ArrayList<>();

                for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot driverSnapshot = reservationSnapshot.child("driverEmail");
                    String reservationDriverEmail = driverSnapshot.getValue(String.class);
                    Reservation reservation = reservationSnapshot.getValue(Reservation.class);

                    if (reservation != null && reservationDriverEmail.equalsIgnoreCase(driverEmail) && "Accepted".equals(reservation.getStatus())) {
                        onGoingRides.add(reservation);
                    }
                }

                // Update RecyclerView with ongoing rides
                updateRecyclerViewWithOnGoingRides(onGoingRides);

                // Hide ProgressBar after loading
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("OnGoingDriverRides", "Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void updateRecyclerViewWithOnGoingRides(List<Reservation> onGoingRides) {
        ongoingDriverRidesAdapter = new OnGoingDriverRidesAdapter(onGoingRides, this);
        recyclerView.setAdapter(ongoingDriverRidesAdapter);
    }

    @Override
    public void onButtonClick(Reservation reservation) {
        // Handle the click on the first button
       finishReservationStatus(reservation);
    }


    private void finishReservationStatus(Reservation reservation) {
        DatabaseReference reservationRef = reservationsDatabaseRef.child(reservation.getId());

        // Update the status to "Completed"
        reservation.setStatus("Completed");

        // Update the status in Firebase
        reservationRef.child("status").setValue(reservation.getStatus());

        // Notify the adapter about the data change if needed
        ongoingDriverRidesAdapter.notifyDataSetChanged();
    }


}
