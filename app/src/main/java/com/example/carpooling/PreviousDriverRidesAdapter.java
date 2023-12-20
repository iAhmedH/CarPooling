package com.example.carpooling;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PreviousDriverRidesAdapter extends RecyclerView.Adapter<PreviousDriverRidesAdapter.ViewHolder> {

    private List<Reservation> previousRides;

    public PreviousDriverRidesAdapter(List<Reservation> previousRides) {
        this.previousRides = previousRides;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_previous_driver_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = previousRides.get(position);

        // Update the UI elements with reservation details
        holder.customerNameTextView.setText("Customer: " + reservation.getUserName());
        holder.locationTextView.setText("Location: " + reservation.getLocation());
        holder.timeTextView.setText("Time: " + reservation.getTime());
        holder.statusTextView.setText("Status: " + reservation.getStatus());
    }

    @Override
    public int getItemCount() {
        return previousRides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;
        TextView locationTextView;
        TextView timeTextView;
        TextView statusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.textViewCustomerName);
            locationTextView = itemView.findViewById(R.id.textViewLocation);
            timeTextView = itemView.findViewById(R.id.textViewTime);
            statusTextView = itemView.findViewById(R.id.textStatus);
        }
    }
}
