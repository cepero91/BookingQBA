package com.infinitum.bookingqba.view.info;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentFeedbackBinding;

public class DialogFeedback extends DialogFragment implements View.OnClickListener {

    private FeedbackInteraction feedbackInteraction;
    private FragmentFeedbackBinding feedbackBinding;

    public DialogFeedback() {
        // Required empty public constructor
    }

    public static DialogFeedback newInstance() {
        return new DialogFeedback();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        feedbackBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false);
        // Inflate the layout for this fragment
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return feedbackBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        feedbackBinding.btnVote.setOnClickListener(this);

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
        if(context instanceof FeedbackInteraction){
            feedbackInteraction = (FeedbackInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        feedbackInteraction = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_vote:
                if(!feedbackBinding.etFeedback.getText().toString().equals("")){
                    feedbackInteraction.sendFeedback(feedbackBinding.etFeedback.getText().toString());
                }
                dismiss();
                break;
        }
    }


    public interface FeedbackInteraction{
        void sendFeedback(String feedback);
    }
}
