package com.example.carpooling;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
import java.util.Map;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.ViewHolder> {

    private List<Ride> rides;
    private Map<String, Map<String, Object>> driversMap;
    private Context context;

    private static String selectedRoute;

    public RidesAdapter(List<Ride> rides, Map<String, Map<String, Object>> driversMap, Context context, String selectedRoute) {
        this.rides = rides;
        this.driversMap = driversMap;
        this.context = context;
        this.selectedRoute = selectedRoute;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new ViewHolder(view, driversMap);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.bind(ride);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textLocation;
        private final TextView textName;
        private final TextView textTime;
        private final Button btnViewDetails;
        private final Map<String, Map<String, Object>> driversMap;

        public ViewHolder(@NonNull View itemView, Map<String, Map<String, Object>> driversMap) {
            super(itemView);
            this.driversMap = driversMap;

            textLocation = itemView.findViewById(R.id.textLocation);
            textName = itemView.findViewById(R.id.textDriverName);
            textTime = itemView.findViewById(R.id.textTime);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }

        public void bind(Ride ride) {
            textLocation.setText("Location: " + ride.getLocation());

            Map<String, Object> driverData = driversMap.get(ride.getDriver());
            String driverName;
            String driverEmail;
            String carType;
            String carModel;
            if (driverData != null && driverData.containsKey("name")) {
                driverName = (String) driverData.get("name");
                driverEmail = (String)driverData.get("email");
                carType = (String) driverData.get("carType");
                carModel = (String) driverData.get("carModel");
            } else {
                driverName = "Unknown Driver";
                driverEmail = "Unknown Driver Email";
                carType = "Unknown Car Type";
                carModel = "Unknown Car Model";
            }
            textName.setText("Driver's Name: " + driverName);

            textTime.setText("Time: " + ride.getTime());

            btnViewDetails.setOnClickListener(v -> {
                // Handle button click, e.g., navigate to details screen
                Log.e("RidesDetails", "RideID: " + ride.getName());
                navigateToDetailsScreen(driverName, ride.getLocation(), ride.getTime(), carType, carModel, ride.getName(), ride.getPrice(), ride.getSeats(), driverEmail);
            });
        }

        private void navigateToDetailsScreen(String driverName, String location, String time, String carType, String carModel, String rideID, String price, String seats, String driverEmail) {
            Intent intent = new Intent(itemView.getContext(), RideDetails.class);
            intent.putExtra("driverName", driverName);
            intent.putExtra("location", location);
            intent.putExtra("time", time);
            intent.putExtra("carType", carType);
            intent.putExtra("carModel", carModel);
            intent.putExtra("rideID", rideID);
            intent.putExtra("price", price);
            intent.putExtra("seats", seats);
            intent.putExtra("driverEmail", driverEmail);
            intent.putExtra("selectedRoute", selectedRoute);
            itemView.getContext().startActivity(intent);
            ((AppCompatActivity) itemView.getContext()).finish();
        }
    }
}
