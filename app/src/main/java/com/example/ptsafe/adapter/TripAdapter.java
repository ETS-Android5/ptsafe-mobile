package com.example.ptsafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ptsafe.R;
import com.example.ptsafe.model.Carriage;
import com.example.ptsafe.model.TripWishlist;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder>  {
    private List<TripWishlist> tripItems;
    private TripAdapter.ClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView sourceNameTv;
        private TextView destinationNameTv;
        private TextView stopNameTv;
        private TextView routeNameTv;
        private TextView departureTimeTv;
        private TextView carriageNumberTv;
        private Button deleteTripBtn;

        //Train view holder constructors
        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            sourceNameTv = itemView.findViewById(R.id.source_name_tv);
            destinationNameTv = itemView.findViewById(R.id.destination_name_tv);
            stopNameTv = itemView.findViewById(R.id.stop_name_tv);
            routeNameTv = itemView.findViewById(R.id.route_name_tv);
            departureTimeTv = itemView.findViewById(R.id.departure_time_trip_tv);
            carriageNumberTv = itemView.findViewById(R.id.carriage_number_tv);
            deleteTripBtn = itemView.findViewById(R.id.delete_trip_btn);

            deleteTripBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onViewButtonClick(getAdapterPosition());
                }
            });
        }

    }

    public interface ClickListener{
        void onViewButtonClick(int position);
    }

    public TripAdapter(List<TripWishlist> tripItems, TripAdapter.ClickListener listener){
        this.tripItems = tripItems;
        this.listener = listener;
    }

    public void addTripWishlistItems(List<TripWishlist> tripItems) {
        this.tripItems = tripItems;
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return tripItems.size();
    }

    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the view from an XML layout file
        View tripItemsView = inflater.inflate(R.layout.trips_rv_layout, parent, false);

        // construct the viewholder with the new view
        TripAdapter.ViewHolder viewHolder = new TripAdapter.ViewHolder(tripItemsView, listener);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull TripAdapter.ViewHolder viewHolder,
                                 int position) {
        final TripWishlist item = tripItems.get(position);

        // viewholder binding with its data at the specified position
        TextView sourceNameTv = viewHolder.sourceNameTv;
        TextView destinationNameTv =viewHolder.destinationNameTv;
        TextView stopNameTv = viewHolder.stopNameTv;
        TextView routeNameTv = viewHolder.routeNameTv;
        TextView departureTimeTv = viewHolder.departureTimeTv;
        TextView carriageNumberTv = viewHolder.carriageNumberTv;
        sourceNameTv.setText(item.getSourceName());
        destinationNameTv.setText(item.getDestinationName());
        stopNameTv.setText(Html.fromHtml("<b>" + "station: " + "</b> " + item.getStopName()));
        routeNameTv.setText(Html.fromHtml("<b>" + "train: " + "</b> " + item.getRouteLongName()));
        departureTimeTv.setText(Html.fromHtml("<b>" + "departure time: " + "</b> " + item.getDepartureTime()));
        carriageNumberTv.setText(Html.fromHtml("<b>" + "carriage number: " + "</b> " + String.valueOf(item.getCarriageNumber())));

    }
}
