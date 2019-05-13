package com.infinitum.bookingqba.view.rents;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.hsalf.smilerating.BaseRating;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentCommentBinding;
import com.infinitum.bookingqba.databinding.FragmentRatingBinding;
import com.infinitum.bookingqba.model.remote.pojo.Comment;

import java.util.Calendar;
import java.util.UUID;

public class DialogRating extends DialogFragment implements View.OnClickListener {

    private RatingInteraction ratingInteraction;
    private FragmentRatingBinding ratingBinding;

    public DialogRating() {
        // Required empty public constructor
    }

    public static DialogRating newInstance() {
        DialogRating dialogComment = new DialogRating();
        return dialogComment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ratingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rating, container, false);
        // Inflate the layout for this fragment
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return ratingBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ratingBinding.fbVote.setOnClickListener(this);

        int width = getResources().getDimensionPixelSize(R.dimen.dialog_login_width);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof RentDetailActivity){
            ratingInteraction = (RatingInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ratingInteraction = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fb_vote:
                performRating();
        }
    }

    private void performRating() {
        String voteComment = ratingBinding.etComment.getText().toString();
        float rating = ratingBinding.srScaleRating.getRating();
        ratingInteraction.sendRating(rating,voteComment);
    }

    public interface RatingInteraction{
        void sendRating(float rating, String voteComment);
    }
}
