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

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class LoginFragment extends DialogFragment implements View.OnClickListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private FragmentLoginBinding loginBinding;

    private LoginInteraction interaction;

    private UserViewModel userViewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;


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
            compositeDisposable = new CompositeDisposable();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        interaction = null;
        compositeDisposable.clear();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            loginBinding.pbLogin.setIndeterminate(true);
            loginBinding.pbLogin.setVisibility(View.VISIBLE);
            if (validateInputs()) {
                String params1 = loginBinding.etUsername.getText().toString();
                String params2 = loginBinding.etPassword.getText().toString();
                disposable = userViewModel.fakeLogin(params1,params2)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(user -> {
                            interaction.onLogin(user);
                            return user.getRentsId();
                        })
                        .flatMapCompletable(this::checkExist)
                        .doOnComplete(() -> dismiss())
                        .subscribe();
                compositeDisposable.add(disposable);
            }
        }
    }

    private Completable checkExist(List<String> uuids){
        Single<Boolean> booleanSingle = userViewModel.checkIfRentExists(uuids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(exist -> {
                    loginBinding.pbLogin.setVisibility(View.INVISIBLE);
                    if(!exist){
                        interaction.showNotificationToUpdate("Para visualizar el perfil de propietario debes actualizar la base de datos");
                    }else{
                        interaction.showGroupMenuProfile(true);
                    }
                });
        return Completable.fromSingle(booleanSingle);
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
