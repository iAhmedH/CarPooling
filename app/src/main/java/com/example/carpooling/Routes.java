package com.example.carpooling;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Routes extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoutesAdapter adapter;
    private DatabaseReference routesDatabaseRef;

    private ProgressBar progressBar;

    @Override
    protected void onResume() {
        super.onResume();
        // Set no item as selected when the activity is resumed
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_empty);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routes_screen);
        // Initialize Firebase Database reference
        routesDatabaseRef = FirebaseDatabase.getInstance().getReference("routes");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_empty);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Upcoming) {
                Intent intent = new Intent(Routes.this,UpcomingRides.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.Previous) {
                Intent intent = new Intent(Routes.this, PreviousRides.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });



        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showProgressBar();
        // Retrieve routes from Firebase
        generateRoutesFromFirebase();
    }

    private void generateRoutesFromFirebase() {
        routesDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Route> routes = new ArrayList<>();
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    if (route != null) {
                        routes.add(route);
                    }
                }

                // Update the RecyclerView with the retrieved routes
                updateRecyclerViewWithRoutes(routes);
                hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if data retrieval fails
                hideProgressBar();
            }
        });
    }

    private void updateRecyclerViewWithRoutes(List<Route> routes) {
        // Update the RecyclerView with the retrieved routes
        adapter = new RoutesAdapter(routes, Routes.this::onRouteClick);
        recyclerView.setAdapter(adapter);
    }

    private void onRouteClick(Route route) {
        Intent intent = new Intent(Routes.this, Rides.class);

        // Pass the selected route location as an extra
        intent.putExtra("selectedRoute", route.getName());

        // Start the Rides activity
        startActivity(intent);
    }
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}