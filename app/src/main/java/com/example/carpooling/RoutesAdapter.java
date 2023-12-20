package com.example.carpooling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    private final List<Route> routes;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Route route);
    }

    public RoutesAdapter(List<Route> routes, OnItemClickListener onItemClickListener) {
        this.routes = routes;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = routes.get(position);
        holder.bind(route , onItemClickListener);
//        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(route));
    }


    @Override
    public int getItemCount() {
        return routes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button btnRoute;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRoute = itemView.findViewById(R.id.btnRoute);
        }

        public void bind(Route route , OnItemClickListener onItemClickListener ) {
            btnRoute.setText(route.getLocation());
            btnRoute.setOnClickListener(v -> onItemClickListener.onItemClick(route));
        }
    }
}
