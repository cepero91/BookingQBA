package com.infinitum.bookingqba.view.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentAuthBinding;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class AuthFragment extends Fragment implements View.OnClickListener {

    public static final String SIGNIN = "signin";
    public static final String SIGNUP = "signup";
    public static final String CODE = "code";

    private FragmentAuthBinding fragmentAuthBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    private UserViewModel userViewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    public AuthFragment() {

    }

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAuthBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false);
        return fragmentAuthBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragmentAuthBinding.setIsSignup(false);
        fragmentAuthBinding.setIsCode(false);
        fragmentAuthBinding.send.setTag(SIGNIN);

        fragmentAuthBinding.btnSignin.setOnClickListener(this);
        fragmentAuthBinding.btnSignup.setOnClickListener(this);
        fragmentAuthBinding.send.setOnClickListener(this);

        setHasOptionsMenu(true);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
//        if (context instanceof FragmentNavInteraction) {
//            mListener = (FragmentNavInteraction) context;
        compositeDisposable = new CompositeDisposable();
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
        compositeDisposable.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                fragmentAuthBinding.send.setTag(SIGNIN);
                fragmentAuthBinding.setIsSignup(false);
                fragmentAuthBinding.btnSignin.setBackgroundColor(Color.WHITE);
                fragmentAuthBinding.btnSignup.setBackgroundColor(Color.parseColor("#e0e0e0"));
                break;
            case R.id.btn_signup:
                fragmentAuthBinding.send.setTag(SIGNUP);
                fragmentAuthBinding.setIsSignup(true);
                fragmentAuthBinding.btnSignin.setBackgroundColor(Color.parseColor("#e0e0e0"));
                fragmentAuthBinding.btnSignup.setBackgroundColor(Color.WHITE);
                break;
            case R.id.send:
                if (v.getTag().equals(SIGNUP)) {
                    fragmentAuthBinding.send.setTag(CODE);
                    fragmentAuthBinding.setIsCode(true);
                    fragmentAuthBinding.send.setText("Cancelar Activacion");
                    fragmentAuthBinding.send.setBackgroundColor(Color.parseColor("#F44336"));
                    fragmentAuthBinding.send.setIconResource("\uf00d");
                    fragmentAuthBinding.btnSignin.setEnabled(false);
                } else if (v.getTag().equals(CODE)) {
                    fragmentAuthBinding.send.setTag(SIGNUP);
                    fragmentAuthBinding.setIsSignup(true);
                    fragmentAuthBinding.setIsCode(false);
                    fragmentAuthBinding.send.setText("Enviar");
                    fragmentAuthBinding.send.setBackgroundColor(Color.parseColor("#009689"));
                    fragmentAuthBinding.send.setIconResource("\uf00c");
                    fragmentAuthBinding.btnSignin.setEnabled(true);
                } else if(v.getTag().equals(SIGNIN)){
                    //do something
                }
                break;
        }
    }
}
