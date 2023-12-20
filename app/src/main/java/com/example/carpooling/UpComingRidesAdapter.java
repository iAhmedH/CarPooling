package com.example.carpooling;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpComingRidesAdapter extends RecyclerView.Adapter<UpComingRidesAdapter.ViewHolder> {

    private final List<Reservation> reservations;
    private final Context context;
    private final CancelButtonClickListener cancelButtonClickListener;

    public interface CancelButtonClickListener {
        void onCancelButtonClick(Reservation reservation);
    }

    public UpComingRidesAdapter(List<Reservation> reservations, Context context, CancelButtonClickListener listener) {
        this.reservations = reservations;
        this.context = context;
        this.cancelButtonClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation);

        // Check the reservation status and show/hide the cancel button accordingly
        if ("Pending".equals(reservation.getStatus())) {
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setOnClickListener(v -> {
                // Handle cancel button click
                if (cancelButtonClickListener != null) {
                    cancelButtonClickListener.onCancelButtonClick(reservation);
                }
            });
        } else {
            holder.cancelButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView driverName;
        private final TextView location;
        private final TextView time;
        private final TextView carType;
        private final TextView carModel;
        private final TextView status;
        private final TextView price;
        private final TextView seats;

        private final Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.textDriverName);
            location = itemView.findViewById(R.id.textLocation);
            time = itemView.findViewById(R.id.textTime);
            carType = itemView.findViewById(R.id.textCarType);
            carModel = itemView.findViewById(R.id.textCarModel);
            status = itemView.findViewById(R.id.textStatus);
            seats = itemView.findViewById(R.id.textSeats);
            price = itemView.findViewById(R.id.textPrice);
            cancelButton = itemView.findViewById(R.id.cancel_button);

        }

        public void bind(Reservation reservation) {
            driverName.setText("Driver: " + reservation.getDriverName());
            location.setText("Location: " + reservation.getLocation());
            time.setText("Time: " + reservation.getTime());
            carType.setText("Car Type: " + reservation.getCarType());
            carModel.setText("Car Model: " + reservation.getCarModel());
            status.setText("Status: " + reservation.getStatus());
            price.setText("Price: " + reservation.getPrice());
            seats.setText("Seats: " + reservation.getSeats());

        }
    }
}
