package com.example.carpooling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpComingDriverRidesAdapter extends RecyclerView.Adapter<UpComingDriverRidesAdapter.ViewHolder> {

    private List<Reservation> reservations;
    private ButtonClickListener buttonClickListener;

    public interface ButtonClickListener {
        void onFirstButtonClick(Reservation reservation);
        void onSecondButtonClick(Reservation reservation);
    }

    public UpComingDriverRidesAdapter(List<Reservation> reservations, ButtonClickListener buttonClickListener) {
        this.reservations = reservations;
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upcoming_driver_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        // Set data to views
        holder.customerNameTextView.setText(reservation.getUserName());
        holder.locationTextView.setText(reservation.getLocation());
        holder.timeTextView.setText(reservation.getTime());
        holder.statusTextView.setText(reservation.getStatus());

        // Set click listener for buttons
        holder.firstButton.setOnClickListener(v -> {
            if (buttonClickListener != null) {
                buttonClickListener.onFirstButtonClick(reservation);
            }
        });

        holder.secondButton.setOnClickListener(v -> {
            if (buttonClickListener != null) {
                buttonClickListener.onSecondButtonClick(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;
        TextView locationTextView;
        TextView timeTextView;
        TextView statusTextView;
        Button firstButton;
        Button secondButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.textCustomerName);
            locationTextView = itemView.findViewById(R.id.textLocation);
            timeTextView = itemView.findViewById(R.id.textTime);
            statusTextView = itemView.findViewById(R.id.textStatus);
            firstButton = itemView.findViewById(R.id.first_button);
            secondButton = itemView.findViewById(R.id.second_button);
        }
    }
}
