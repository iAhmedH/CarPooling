package com.example.carpooling;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PreviousRidesAdapter extends RecyclerView.Adapter<PreviousRidesAdapter.ViewHolder> {

    private List<Reservation> previousRides;

    public PreviousRidesAdapter(List<Reservation> previousRides) {
        this.previousRides = previousRides;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_previous_rides, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = previousRides.get(position);

        // Update the UI elements with reservation details
        holder.driverNameTextView.setText("Driver: " + reservation.getDriverName());
        holder.locationTextView.setText("Location: " + reservation.getLocation());
        holder.timeTextView.setText("Time: " + reservation.getTime());
        holder.statusTextView.setText("Status: " + reservation.getStatus());
    }

    @Override
    public int getItemCount() {
        return previousRides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView driverNameTextView;
        TextView locationTextView;
        TextView timeTextView;
        TextView statusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driverNameTextView = itemView.findViewById(R.id.textViewDriverName);
            locationTextView = itemView.findViewById(R.id.textViewLocation);
            timeTextView = itemView.findViewById(R.id.textViewTime);
            statusTextView = itemView.findViewById(R.id.textStatus);
        }
    }
}
