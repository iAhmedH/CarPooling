package com.example.carpooling;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rides extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RidesAdapter ridesAdapter;
    private DatabaseReference routesDatabaseRef;
    private String selectedRoute;

    private ProgressBar progressBar;

    // Add a Map to store driver information
    private Map<String, Map<String, Object>> driversMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rides_list);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        routesDatabaseRef = FirebaseDatabase.getInstance().getReference("routes");

        // Retrieve the selected route name from the Intent
        selectedRoute = getIntent().getStringExtra("selectedRoute");
        Log.d("RidesActivity", "The selected route is: " + selectedRoute);

        // Load driver information
        loadDriverInformation();

        // Generate rides data after loading driver information
        generateRidesData();
    }

    private void loadDriverInformation() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference driversDatabaseRef = FirebaseDatabase.getInstance().getReference("drivers");

        driversDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot driverSnapshot : dataSnapshot.getChildren()) {
                        // Store driver information in the driversMap
                        String driverKey = driverSnapshot.getKey();
                        Map<String, Object> driverData = (Map<String, Object>) driverSnapshot.getValue();
                        driversMap.put(driverKey, driverData);
                    }
                } else {
                    Log.d("RidesActivity", "No driver data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RidesActivity", "Driver data retrieval canceled with error: " + databaseError.getMessage());
            }
        });
    }

    private void generateRidesData() {
        List<Ride> ridesList = new ArrayList<>();

        DatabaseReference selectedRouteRef = routesDatabaseRef.child(selectedRoute).child("rides");

        selectedRouteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot rideSnapshot : dataSnapshot.getChildren()) {
                        Ride ride = rideSnapshot.getValue(Ride.class);
                        ridesList.add(ride);
                    }

                    // Pass ridesList and driversMap to the adapter
                    updateRecyclerViewWithRides(ridesList, driversMap);
                } else {
                    Log.d("RidesActivity", "No data found for the selected route");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e("RidesActivity", "Data retrieval canceled with error: " + databaseError.getMessage());
            }
        });
    }

    private void updateRecyclerViewWithRides(List<Ride> rides, Map<String, Map<String, Object>> driversMap) {
        ridesAdapter = new RidesAdapter(rides, driversMap, this, selectedRoute);
        recyclerView.setAdapter(ridesAdapter);
    }

}
