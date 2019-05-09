package com.infinitum.bookingqba.view.rents;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
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
import com.infinitum.bookingqba.databinding.FragmentCommentBinding;
import com.infinitum.bookingqba.viewmodel.UserViewModel;

public class DialogComment extends DialogFragment {

    private FragmentCommentBinding commentBinding;

    public DialogComment() {
        // Required empty public constructor
    }

    public static DialogComment newInstance() {
        return new DialogComment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        commentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);
        // Inflate the layout for this fragment
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return commentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
}
