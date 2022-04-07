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
import com.example.ptsafe.model.Comment;
import com.example.ptsafe.model.Emergency;
import com.example.ptsafe.model.News;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ViewHolder>  {
    private List<Emergency> emergencyItems;
    private EmergencyAdapter.ClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView organizationNameTv;
        public TextView organizationPhoneTv;
        public TextView organizationLocationTv;
        public Button callBtn;

        //emergency view holder constructors
        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            organizationNameTv = itemView.findViewById(R.id.organization_name_tv);
            organizationPhoneTv = itemView.findViewById(R.id.organization_phone_tv);
            organizationLocationTv = itemView.findViewById(R.id.organization_location_tv);
            callBtn = itemView.findViewById(R.id.call_btn);

            callBtn.setOnClickListener(new View.OnClickListener() {
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

    public EmergencyAdapter(List<Emergency> emergencyItems, EmergencyAdapter.ClickListener listener){
        this.emergencyItems = emergencyItems;
        this.listener = listener;
    }

    public void addEmergency(List<Emergency> emergencyItems) {
        this.emergencyItems = emergencyItems;
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return emergencyItems.size();
    }

    public EmergencyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the view from an XML layout file
        View emergencyItemsView = inflater.inflate(R.layout.emergency_rv_layout, parent, false);

        // construct the viewholder with the new view
        EmergencyAdapter.ViewHolder viewHolder = new EmergencyAdapter.ViewHolder(emergencyItemsView, listener);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull EmergencyAdapter.ViewHolder viewHolder,
                                 int position) {
        final Emergency item = emergencyItems.get(position);

        // viewholder binding with its data at the specified position
        TextView organizationNameTv = viewHolder.organizationNameTv;
        organizationNameTv.setText(item.getOrganizationName());
        TextView organizationPhoneTv =viewHolder.organizationPhoneTv;
        organizationPhoneTv.setText(item.getOrganizationNumber());
        TextView organizationLocationTv = viewHolder.organizationLocationTv;
        organizationLocationTv.setText(item.getOrganizationAddress());
    }
}
