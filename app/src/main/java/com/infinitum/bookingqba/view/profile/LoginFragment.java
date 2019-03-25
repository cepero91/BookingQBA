package com.infinitum.bookingqba.view.profile;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentLoginBinding;
import com.infinitum.bookingqba.model.remote.Login;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.repository.user.UserRepository;
import com.infinitum.bookingqba.view.interaction.LoginInteraction;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends DialogFragment implements View.OnClickListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private FragmentLoginBinding loginBinding;

    private LoginInteraction interaction;

    private UserViewModel userViewModel;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return loginBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        int width = getResources().getDimensionPixelSize(R.dimen.dialog_login_width);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);

        loginBinding.btnLogin.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

        if (context instanceof LoginInteraction) {
            interaction = (LoginInteraction) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        interaction = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            if (validateInputs()) {
                userViewModel.userLogin(loginBinding.etUsername.getText().toString(), loginBinding.etPassword.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if(response.isSuccessful()){
                                    interaction.onLogin(response.body());
                                }else{
                                    Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }


    /**
     * Hay que mejorar esta validacion
     *
     * @return
     */
    private boolean validateInputs() {
        boolean isValid = true;
        String password = loginBinding.etPassword.getText().toString();
        String username = loginBinding.etUsername.getText().toString();
        if (username.equals("") || password.equals("")) {
            isValid = false;
        }
        return isValid;
    }
}
