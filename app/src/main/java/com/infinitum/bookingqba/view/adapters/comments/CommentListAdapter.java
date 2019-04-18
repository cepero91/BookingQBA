package com.infinitum.bookingqba.view.adapters.comments;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerCommentItemBinding;
import com.infinitum.bookingqba.view.adapters.items.comment.CommentItem;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder>{

    private List<CommentItem> commentItemArrayList;

    public CommentListAdapter(List<CommentItem> commentItemArrayList) {
        this.commentItemArrayList = commentItemArrayList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        RecyclerCommentItemBinding itemBinding = DataBindingUtil.inflate(inflater, R.layout.recycler_comment_item, viewGroup, false);
        return (new CommentViewHolder(itemBinding));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        CommentItem item = commentItemArrayList.get(i);
        if(item == null){
            commentViewHolder.clear();
        }else{
            commentViewHolder.bind(item,i);
        }
    }

    @Override
    public int getItemCount() {
        return commentItemArrayList!=null?commentItemArrayList.size():0;
    }
}
