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
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.util.DateUtils;

import java.util.UUID;

public class DialogComment extends DialogFragment implements View.OnClickListener {

    private FragmentCommentBinding commentBinding;
    private CommentInteraction commentInteraction;

    public static final String ARG_USERNAME = "userName";
    public static final String ARG_USERID = "userId";
    public static final String ARG_RENTID = "rentId";

    private String argUserName;
    private String argUserId;
    private String argRentId;

    public DialogComment() {
        // Required empty public constructor
    }

    public static DialogComment newInstance(String userName, String userId, String rentId) {
        DialogComment dialogComment = new DialogComment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, userName);
        args.putString(ARG_USERID, userId);
        args.putString(ARG_RENTID, rentId);
        dialogComment.setArguments(args);
        return dialogComment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argUserName = getArguments().getString(ARG_USERNAME);
            argUserId = getArguments().getString(ARG_USERID);
            argRentId = getArguments().getString(ARG_RENTID);
        }
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

        commentBinding.ratingView.setNameForSmile(BaseRating.TERRIBLE, "Terrible");
        commentBinding.ratingView.setNameForSmile(BaseRating.BAD, "Malo");
        commentBinding.ratingView.setNameForSmile(BaseRating.OKAY, "Mejorable");
        commentBinding.ratingView.setNameForSmile(BaseRating.GOOD, "Bueno");
        commentBinding.ratingView.setNameForSmile(BaseRating.GREAT, "Excelente");
        commentBinding.ratingView.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.poppinsbold));
        commentBinding.btnComment.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof CommentInteraction){
            commentInteraction = (CommentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        commentInteraction = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                addComment();
                break;
        }
    }

    private void addComment() {
        if (validateInputs()) {
            commentBinding.tvError.setVisibility(View.INVISIBLE);
            int emotionLevel = commentBinding.ratingView.getRating();
            String decription = commentBinding.etComment.getText().toString();
            Comment comment = createComment(emotionLevel, decription);
            commentInteraction.sendComment(comment);
            dismiss();
        }else{
            commentBinding.tvError.setVisibility(View.VISIBLE);
            commentBinding.tvError.setText("Llene los campos requeridos");
        }
    }

    private Comment createComment(int emotionLevel, String decription) {
        Comment comment = new Comment(UUID.randomUUID().toString(),argUserName);
        comment.setUserid(argUserId);
        comment.setRent(argRentId);
        comment.setDescription(decription);
        comment.setActive(false);
        comment.setAvatar(null);
        comment.setOwner(false);
        comment.setEmotion(emotionLevel);
        comment.setCreated(DateUtils.currentDateToString());
        return comment;
    }

    private boolean validateInputs() {
        boolean isValid = true;
        if(commentBinding.ratingView.getRating() == 0){
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.shake_animation);
            commentBinding.ratingView.startAnimation(animation);
        }
        if(commentBinding.etComment.getText().toString().length()==0){
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.shake_animation);
            commentBinding.etComment.startAnimation(animation);
        }
        return isValid;
    }

    public interface CommentInteraction{
        void sendComment(Comment comment);
    }
}
