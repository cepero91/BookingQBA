package com.infinitum.bookingqba.view.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.infinitum.bookingqba.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import static com.infinitum.bookingqba.util.Constants.BASE_URL_API;

public class SuccessDialogContentView extends LinearLayout {

    private CircularImageView avatar;
    private TextView userWelcome;

    public SuccessDialogContentView(Context context) {
        this(context, null, 0);
    }

    public SuccessDialogContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuccessDialogContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ((Activity)getContext())
                .getLayoutInflater()
                .inflate(R.layout.login_success_dialog, this, true);

        init();
    }

    private void init() {
        setupSubviews();
    }

    private void setupSubviews() {
        avatar = findViewById(R.id.user_avatar);
        userWelcome = findViewById(R.id.tv_username);
    }

    public void updateUserView(String userAvatar, String username){
        String url = BASE_URL_API + "/" + userAvatar;
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.user_placeholder)
                .into(avatar);
        userWelcome.setText(String.format("Bienvenido/a %s",username));
    }

}
