package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRide extends AppCompatActivity {

    private Spinner spinnerLocation;
    private Spinner spinnerTime;
    private Button btnAddRide;

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
        setContentView(R.layout.add_ride);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomDriverNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_empty);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Upcoming) {
                Intent intent = new Intent(AddRide.this, UpComingDriverRides.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.OnGoing) {
                Intent intent = new Intent(AddRide.this, OnGoingDriverRides.class);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.AddRide) {
                return false;
            }
            else if (itemId == R.id.Previous) {
                Intent intent = new Intent(AddRide.this, PreviousDriverRides.class);
                startActivity(intent);
                return true;
            }
            else {
                return false;
            }
        });

        // Initialize UI components
        spinnerLocation = findViewById(R.id.spinnerLocation);
        spinnerTime = findViewById(R.id.spinnerTime);
        btnAddRide = findViewById(R.id.btnAddRide);

        // Populate the location and time spinners with placeholders
        populateLocationSpinnerWithPlaceholder();
        populateTimeSpinnerWithPlaceholder();

        // Set a click listener for the "Add Ride" button
        btnAddRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the logic to add a ride based on the selected location and time
                addRide();
            }
        });
    }

    private void populateLocationSpinnerWithPlaceholder() {
        // Fetch location options from Firebase or any other data source
        // Example: Assume routesNode is a reference to the "routes" node in your Firebase
        DatabaseReference routesNode = FirebaseDatabase.getInstance().getReference().child("routes");
        routesNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> locationOptions = new ArrayList<>();
                locationOptions.add("Select Location"); // Placeholder
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    String location = routeSnapshot.child("location").getValue(String.class);
                    locationOptions.add(location);
                }

                ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(AddRide.this,
                        android.R.layout.simple_spinner_item, locationOptions);
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocation.setAdapter(locationAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void populateTimeSpinnerWithPlaceholder() {
        // Populate the time spinner with your desired logic
        // Example: Assuming you have a predefined list of times
        List<String> times = new ArrayList<>();
        times.add("Select Time"); // Placeholder
        times.add("5:30");
        times.add("6:30");

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, times);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeAdapter);
    }

    // ... (other parts of your class)

    private void addRide() {
        // Get selected location and time
        String selectedLocation = spinnerLocation.getSelectedItem().toString();
        String selectedTime = spinnerTime.getSelectedItem().toString();

        // Retrieve the current user's email
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            final String[] driverKey = {""};
            // Query the "drivers" node in Firebase to find the driver with the matching email
            DatabaseReference driversRef = FirebaseDatabase.getInstance().getReference().child("drivers");

            driversRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Loop through the result set to get the driver key
                        for (DataSnapshot driverSnapshot : dataSnapshot.getChildren()) {
                            driverKey[0] = driverSnapshot.getKey();
                            break;  // Assuming there is only one driver with the given email
                        }

                        DatabaseReference routesDatabaseRef = FirebaseDatabase.getInstance().getReference("routes");
                        String selectedRoute = "";
                        if ("Tagmo3".equals(selectedLocation)) {
                            selectedRoute = "route1";
                        } else if ("Madient Nasr".equals(selectedLocation)) {
                            selectedRoute = "route2";
                        } else if ("Abbasyaa".equals(selectedLocation)) {
                            selectedRoute = "route3";
                        } else if ("Abdo Basha".equals(selectedLocation)) {
                            selectedRoute = "route4";
                        } else if ("Shorouk".equals(selectedLocation)) {
                            selectedRoute = "route5";
                        } else if ("Madienty".equals(selectedLocation)) {
                            selectedRoute = "route6";
                        }

                        DatabaseReference selectedRouteRef = routesDatabaseRef.child(selectedRoute);
                        DatabaseReference ridesRef = selectedRouteRef.child("rides");

                        // Check if the driver has already added a ride in the selected route
                        ridesRef.orderByChild("driver").equalTo(driverKey[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Driver has already added a ride in this route
                                    showToast("You are already in this route");
                                } else {
                                    // Separate query to count the number of rides
                                    ridesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            long rideCount = dataSnapshot.getChildrenCount();

                                            // Create a new ride entry with a name based on the count of rides
                                            String newRideName = "ride" + (rideCount + 1);

                                            // Set the key for the new ride explicitly
                                            DatabaseReference newRideRef = ridesRef.child(newRideName);

                                            EditText textPrice = findViewById(R.id.textPrice);
                                            EditText textSeats = findViewById(R.id.textSeats);
                                            String price = textPrice.getText().toString();
                                            String seats = textSeats.getText().toString();

                                            Map<String, Object> newRide = new HashMap<>();
                                            newRide.put("driver", driverKey[0]);
                                            newRide.put("location", selectedLocation);
                                            newRide.put("name", newRideName);
                                            newRide.put("time", selectedTime);
                                            newRide.put("price", price);
                                            newRide.put("seats", seats);

                                            newRideRef.setValue(newRide);

                                            // Display a success message using Toast
                                            showToast("Ride added successfully");

                                            Intent intent = new Intent(AddRide.this, UpComingDriverRides.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle errors
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle errors
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    // Helper method to show a Toast message
    private void showToast(String message) {
        Toast.makeText(AddRide.this, message, Toast.LENGTH_SHORT).show();
    }

}
