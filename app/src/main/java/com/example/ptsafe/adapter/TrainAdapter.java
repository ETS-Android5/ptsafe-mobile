package com.example.ptsafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ptsafe.R;
import com.example.ptsafe.model.Train;

import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder>  {
    private List<Train> trainItems;
    private TrainAdapter.ClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView trainLongNameTv;
        public TextView tripHeadsignTv;
        public TextView departureTimeTv;
        public Button listCarriageBtn;

        //Train view holder constructors
        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            trainLongNameTv = itemView.findViewById(R.id.train_long_name_tv);
            tripHeadsignTv = itemView.findViewById(R.id.trip_headsign_tv);
            departureTimeTv = itemView.findViewById(R.id.departure_time_tv);
            listCarriageBtn = itemView.findViewById(R.id.check_carriage_btn);

            listCarriageBtn.setOnClickListener(new View.OnClickListener() {
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

    public TrainAdapter(List<Train> trainItems, TrainAdapter.ClickListener listener){
        this.trainItems = trainItems;
        this.listener = listener;
    }

    public void addTrains(List<Train> trainItems) {
        this.trainItems = trainItems;
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return trainItems.size();
    }

    public TrainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                          int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the view from an XML layout file
        View trainItemsView = inflater.inflate(R.layout.train_rv_layout, parent, false);

        // construct the viewholder with the new view
        TrainAdapter.ViewHolder viewHolder = new TrainAdapter.ViewHolder(trainItemsView, listener);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull TrainAdapter.ViewHolder viewHolder,
                                 int position) {
        final Train item = trainItems.get(position);

        // viewholder binding with its data at the specified position
        TextView trainLongNameTv = viewHolder.trainLongNameTv;
        trainLongNameTv.setText(item.getRouteLongName());
        TextView tripHeadsignTv =viewHolder.tripHeadsignTv;
        tripHeadsignTv.setText(item.getTripHeadSign());
        TextView departureTimeTv = viewHolder.departureTimeTv;
        departureTimeTv.setText("departure time: " + item.getDepartureTime());
    }
}
