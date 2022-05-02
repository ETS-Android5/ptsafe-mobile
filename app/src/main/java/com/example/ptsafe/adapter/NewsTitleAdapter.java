package com.example.ptsafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ptsafe.R;
import com.example.ptsafe.model.NewsStop;

import java.util.List;

public class NewsTitleAdapter extends RecyclerView.Adapter<NewsTitleAdapter.ViewHolder>  {
    private List<NewsStop> newsStopItems;
    private NewsTitleAdapter.ClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView newsStopTitleTv;

        //emergency view holder constructors
        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            newsStopTitleTv = itemView.findViewById(R.id.news_title_stop_tv);

            newsStopTitleTv.setOnClickListener(new View.OnClickListener() {
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

    public NewsTitleAdapter(List<NewsStop> newsStopItems, NewsTitleAdapter.ClickListener listener){
        this.newsStopItems = newsStopItems;
        this.listener = listener;
    }

    public void addNewsStopItems(List<NewsStop> newsStopItems) {
        this.newsStopItems = newsStopItems;
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return newsStopItems.size();
    }

    public NewsTitleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                          int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the view from an XML layout file
        View newsStopItemView = inflater.inflate(R.layout.news_title_rv_layout, parent, false);

        // construct the viewholder with the new view
        NewsTitleAdapter.ViewHolder viewHolder = new NewsTitleAdapter.ViewHolder(newsStopItemView, listener);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull NewsTitleAdapter.ViewHolder viewHolder,
                                 int position) {
        final NewsStop item = newsStopItems.get(position);

        // viewholder binding with its data at the specified position
        TextView newsStopTitleTv = viewHolder.newsStopTitleTv;
        newsStopTitleTv.setText(item.getNewsTitle());
    }
}
