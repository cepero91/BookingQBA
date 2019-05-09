package com.infinitum.bookingqba.view.adapters.comments;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.infinitum.bookingqba.databinding.RecyclerCommentItemBinding;
import com.infinitum.bookingqba.view.adapters.items.comment.CommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;

public class CommentViewHolder extends RecyclerView.ViewHolder{

    private RecyclerCommentItemBinding itemBinding;

    public CommentViewHolder(@NonNull RecyclerCommentItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }

    public void bind(CommentItem item, int position) {
//        if (position % 2 == 0){
//            itemBinding.cvComment.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(30,12,0,12);
//            itemBinding.cvComment.setLayoutParams(params);
//        }else{
//            itemBinding.cvComment.setCardBackgroundColor(Color.parseColor("#ECEFF1"));
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0,12,30,12);
//            itemBinding.cvComment.setLayoutParams(params);
//        }
//        itemBinding.setItem(item);
        itemBinding.executePendingBindings();
    }

    public void clear() {
//        itemBinding.setItem(null);
    }
}
