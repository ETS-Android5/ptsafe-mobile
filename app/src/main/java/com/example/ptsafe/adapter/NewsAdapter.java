package com.example.ptsafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ptsafe.R;
import com.example.ptsafe.model.News;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>  {
    private ClickListener listener;
    private List<News> newsItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView newsImageIv;
        public TextView newsTitleTv;
        public TextView newsShortContentTv;
        public TextView newsLabelTv;
        public Button newsButton;

        //news view holder constructors
        public ViewHolder(View itemView, final ClickListener listener) {
            super(itemView);
            newsImageIv = itemView.findViewById(R.id.news_image);
            newsTitleTv = itemView.findViewById(R.id.news_title);
            newsLabelTv = itemView.findViewById(R.id.news_tag_tv);
            newsShortContentTv =itemView.findViewById(R.id.news_short_content);
            newsButton = itemView.findViewById(R.id.read_more_btn);

            newsButton.setOnClickListener(new View.OnClickListener() {
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

    public NewsAdapter(List<News> newsItems, ClickListener listener){
        this.newsItems = newsItems;
        this.listener = listener;
    }

    public void addNews(List<News> newsItems) {
        this.newsItems = newsItems;
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return newsItems.size();
    }
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                          int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from an XML layout file
        View newsItemsView = inflater.inflate(R.layout.news_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(newsItemsView, listener);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder viewHolder,
                                 int position) {
        final News item = newsItems.get(position);
        // viewholder binding with its data at the specified position
        TextView newsTitleTv = viewHolder.newsTitleTv;
        newsTitleTv.setText(item.getNewsTitle());
        Picasso.get().load(item.getImageUrl()).into(viewHolder.newsImageIv);
        TextView newsContentTv =viewHolder.newsShortContentTv;
        newsContentTv.setText(item.getNewsContent());
        TextView newsLabelTv = viewHolder.newsLabelTv;
        setNewsLabelColor(item.getNewsLabel(), newsLabelTv);
    }

    private void setNewsLabelColor(String newsLabel, TextView tv) {
        if (newsLabel.equals("crime")) {
            tv.setTextColor(Color.RED);
        }
        else {
            tv.setTextColor(Color.BLUE);;
        }
        tv.setText(newsLabel);
    }
}
