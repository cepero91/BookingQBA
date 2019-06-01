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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
    public static final String LAST_RATING = "lastRating";
    public static final String LAST_COMMENT = "lastComment";
    private float argLastRating;
    private String argLastComment;

    public DialogRating() {
        // Required empty public constructor
    }

    public static DialogRating newInstance(float argLastRating, String argLastComment) {
        DialogRating dialogComment = new DialogRating();
        Bundle args = new Bundle();
        args.putFloat(LAST_RATING,argLastRating);
        args.putString(LAST_COMMENT,argLastComment);
        dialogComment.setArguments(args);
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

        if(getArguments()!=null){
            argLastRating = getArguments().getFloat(LAST_RATING);
            argLastComment = getArguments().getString(LAST_COMMENT);
        }

        ratingBinding.srScaleRating.setRating(argLastRating);
        ratingBinding.etComment.setText(argLastComment);
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
        if(context instanceof RatingInteraction){
            ratingInteraction = (RatingInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        ratingInteraction = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fb_vote:
                if(validateInputs()) {
                    ratingBinding.tvError.setVisibility(View.INVISIBLE);
                    performRating();
                    dismiss();
                }else{
                    ratingBinding.tvError.setVisibility(View.VISIBLE);
                    ratingBinding.tvError.setText("Llene los campos requeridos");
                }
                break;
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        if(ratingBinding.srScaleRating.getRating() == 0){
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.shake_animation);
            ratingBinding.srScaleRating.startAnimation(animation);
        }
        if(ratingBinding.etComment.getText().toString().length()==0){
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.shake_animation);
            ratingBinding.etComment.startAnimation(animation);
        }
        return isValid;
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
