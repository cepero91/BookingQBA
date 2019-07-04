package com.infinitum.bookingqba.view.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentAuthBinding;
import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.mateware.snacky.Snacky;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
    private String email;

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
        fragmentAuthBinding.cancel.setOnClickListener(this);
        fragmentAuthBinding.tvResendActivationCode.setOnClickListener(this);

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
                    sendUserRegister();
                } else if (v.getTag().equals(CODE)) {
                    sendUserActivationCode();
//                    fragmentAuthBinding.send.setTag(SIGNUP);
//                    fragmentAuthBinding.setIsSignup(true);
//                    fragmentAuthBinding.setIsCode(false);
//                    fragmentAuthBinding.send.setText("Enviar");
//                    fragmentAuthBinding.send.setBackgroundColor(Color.parseColor("#009689"));
//                    fragmentAuthBinding.send.setIconResource("\uf00c");
//                    fragmentAuthBinding.btnSignin.setEnabled(true);
                } else if (v.getTag().equals(SIGNIN)) {
                    //do something
                }
                break;
            case R.id.cancel:
                fragmentAuthBinding.setIsSignup(true);
                fragmentAuthBinding.setIsCode(false);
                fragmentAuthBinding.btnSignin.setEnabled(true);
                break;
            case R.id.tv_resend_activation_code:
                resendActivationCode();
                break;
        }
    }

    private void resendActivationCode() {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        disposable = userViewModel.resendCode(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.status == Resource.Status.SUCCESS) {
                        Snacky.builder()
                                .setActivity(getActivity())
                                .setText("Codigo reeviado con exito")
                                .success()
                                .show();
//                        AlertUtils.showSuccessAlert(getActivity(),"Codigo Reenviado");
                    } else {
                        //showErrorMessage()
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void sendUserActivationCode() {
        String activationCode = fragmentAuthBinding.etActivationCode.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("activationCode", activationCode);
        map.put("email", email);
        disposable = userViewModel.activate(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.status == Resource.Status.SUCCESS) {
                        AlertUtils.showSuccessAlert(getActivity(), "Usuario Activado");
                    } else {
                        //showErrorMessage()
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void sendUserRegister() {
        Map<String, String> map = getUserMap();
        disposable = userViewModel.register(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resource -> {
                    if (resource.status == Resource.Status.SUCCESS) {
                        verifyUserStatus(resource.data);
                    } else {
                        // showMessageError()
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void verifyUserStatus(ResponseResult data) {
        if (data != null && data.getCode() == 200) {
            setRegisterUserToActivateCount(data.getMsg());
        } else if (data != null && data.getCode() == 500) {
            // showMessageError()
        }
    }

    private void setRegisterUserToActivateCount(@Nullable String message) {
        AlertUtils.showSuccessAlert(getActivity(), message);
        fragmentAuthBinding.send.setTag(CODE);
        fragmentAuthBinding.setIsCode(true);
        fragmentAuthBinding.btnSignin.setEnabled(false);
    }

    @NonNull
    private Map<String, String> getUserMap() {
        String username = fragmentAuthBinding.etUsername.getText().toString();
        String firstName = fragmentAuthBinding.etFirstName.getText().toString();
        String secondName = fragmentAuthBinding.etLastName.getText().toString();
        email = fragmentAuthBinding.etEmail.getText().toString();
        String password = fragmentAuthBinding.etPassword.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("first_name", firstName);
        map.put("last_name", secondName);
        map.put("email", email);
        map.put("password", password);
        return map;
    }

    private void resetRegisterForm() {
        fragmentAuthBinding.etUsername.setText("");
        fragmentAuthBinding.etFirstName.setText("");
        fragmentAuthBinding.etLastName.setText("");
        fragmentAuthBinding.etEmail.setText("");
        fragmentAuthBinding.etPassword.setText("");
    }
}
