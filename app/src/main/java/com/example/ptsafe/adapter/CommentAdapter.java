package com.example.ptsafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ptsafe.R;
import com.example.ptsafe.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>  {
    private List<Comment> commentItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView commentTitleTv;
        public TextView commentContentTv;

        //news view holder constructors
        public ViewHolder(View itemView) {
            super(itemView);
            commentTitleTv = itemView.findViewById(R.id.comment_title);
            commentContentTv = itemView.findViewById(R.id.comment_short_content);
        }

    }


    public CommentAdapter(List<Comment> commentItems){
        this.commentItems = commentItems;
    }

    public void addComments(List<Comment> newsItems) {
        this.commentItems = commentItems;
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return commentItems.size();
    }
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from an XML layout file
        View commentItemsView = inflater.inflate(R.layout.comment_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(commentItemsView);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder viewHolder,
                                 int position) {
        final Comment item = commentItems.get(position);
        // viewholder binding with its data at the specified position
        TextView commentTitleTv = viewHolder.commentTitleTv;
        commentTitleTv.setText(item.getCommentTitle());
        TextView commentContentTv =viewHolder.commentContentTv;
        commentContentTv.setText(item.getCommentContent());
    }
}
