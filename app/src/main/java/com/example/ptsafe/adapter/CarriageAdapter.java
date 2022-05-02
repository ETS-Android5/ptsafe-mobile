package com.example.ptsafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ptsafe.R;
import com.example.ptsafe.model.Carriage;

import java.util.List;

public class CarriageAdapter extends RecyclerView.Adapter<CarriageAdapter.ViewHolder>  {
    private List<Carriage> carriageItems;
    private CarriageAdapter.ClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView carriageNameTv;
        private TextView crowdednessNameTv;
        private TextView crowdednessPercentageTv;
        private Button viewCarriageBtn;

        //Train view holder constructors
        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            carriageNameTv = itemView.findViewById(R.id.carriage_name_tv);
            crowdednessNameTv = itemView.findViewById(R.id.crowdedness_name_tv);
            crowdednessPercentageTv = itemView.findViewById(R.id.crowdedness_level_percentage_tv);
            viewCarriageBtn = itemView.findViewById(R.id.view_carriage_details_btn);

            viewCarriageBtn.setOnClickListener(new View.OnClickListener() {
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

    public CarriageAdapter(List<Carriage> carriageItems, CarriageAdapter.ClickListener listener){
        this.carriageItems = carriageItems;
        this.listener = listener;
    }

    public void addCarriages(List<Carriage> carriageItems) {
        this.carriageItems = carriageItems;
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return carriageItems.size();
    }

    public CarriageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the view from an XML layout file
        View carriageItemsView = inflater.inflate(R.layout.carriage_rv_layout, parent, false);

        // construct the viewholder with the new view
        CarriageAdapter.ViewHolder viewHolder = new CarriageAdapter.ViewHolder(carriageItemsView, listener);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull CarriageAdapter.ViewHolder viewHolder,
                                 int position) {
        final Carriage item = carriageItems.get(position);

        // viewholder binding with its data at the specified position
        TextView carriageNameTv = viewHolder.carriageNameTv;
        TextView crowdednessNameTv =viewHolder.crowdednessNameTv;
        TextView crowdednessLevelPercentageTv = viewHolder.crowdednessPercentageTv;
        carriageNameTv.setText("carriage " + item.getCarriageNumber());
        getCrowdednessIndicator(item, crowdednessNameTv, crowdednessLevelPercentageTv);

    }

    private void getCrowdednessIndicator(Carriage item, TextView crowdedNessNameTv, TextView crowdednessLevelPercentageTv) {
        if (item.getAverageCrowdednessLevel() > 0.8) {
            crowdedNessNameTv.setText("crowdedness level high");
            crowdedNessNameTv.setTextColor(Color.RED);
            crowdednessLevelPercentageTv.setText("crowdedness level: " + item.getAverageCrowdednessLevel());
        }
        else if (item.getAverageCrowdednessLevel() > 0.5) {
            crowdedNessNameTv.setText("crowdedness level medium");
            crowdedNessNameTv.setTextColor(Color.CYAN);
            crowdednessLevelPercentageTv.setText("crowdedness level: " + item.getAverageCrowdednessLevel());
        }
        else if (item.getAverageCrowdednessLevel() == -1) {
            crowdedNessNameTv.setText("No crowdedness level detected");
            crowdedNessNameTv.setTextColor(Color.GRAY);
        }
        else {
            crowdedNessNameTv.setText("crowdedness level low");
            crowdedNessNameTv.setTextColor(Color.GREEN);
            crowdednessLevelPercentageTv.setText("crowdedness level: " + item.getAverageCrowdednessLevel());
        }
    }
}


