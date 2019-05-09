package com.infinitum.bookingqba.view.profile;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentLoginBinding;
import com.infinitum.bookingqba.model.remote.Oauth;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.view.interaction.LoginInteraction;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.IMEI;


public class LoginFragment extends DialogFragment implements View.OnClickListener {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentLoginBinding loginBinding;

    private LoginInteraction interaction;

    private UserViewModel userViewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    public static final int RESULT_DISMISS = 1;
    public static final int RESULT_ERROR = 2;


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

        loginBinding.login.setOnClickListener(this);
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
    public void onDestroyView() {
        interaction = null;
        compositeDisposable.clear();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //TERMINAR EN LA CASA ESTOS EVENTOS
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            loginBinding.setIsLoading(true);
            v.setEnabled(false);
            String params1 = loginBinding.etUsername.getText().toString();
            String params2 = loginBinding.etPassword.getText().toString();
            String params3 = sharedPreferences.getString(IMEI, "");
            Oauth oauth = new Oauth(params1, params2, params3);
            disposable = userViewModel.userLogin(oauth)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map((Function<Response<User>, Pair<Boolean, ArrayList<String>>>) response -> {
                        loginBinding.setIsLoading(false);
                        if (response.code() == 200 && response.body() != null) {
                            return new Pair<>(true, response.body().getRentsId());
                        } else {
                            return new Pair<>(false, new ArrayList<>());
                        }
                    })
                    .flatMap(this::checkExist)
                    .map(booleanBooleanPair -> {
                        if (booleanBooleanPair.first && booleanBooleanPair.second) {
                            interaction.showGroupMenuProfile(true);
                            return RESULT_DISMISS;
                        } else if (booleanBooleanPair.first && !booleanBooleanPair.second) {
                            interaction.showNotificationToUpdate(getResources().getString(R.string.notification_sync_msg));
                            return RESULT_DISMISS;
                        } else {
                            return RESULT_ERROR;
                        }
                    })
                    .doOnSuccess(integer -> {
                        if (integer == RESULT_DISMISS) {
                            dismiss();
                        } else {
                            v.setEnabled(true);
                        }
                    })
                    .onErrorReturn(throwable -> {
                        Timber.e(throwable);
                        return RESULT_ERROR;
                    })
                    .subscribe();
            compositeDisposable.add(disposable);

        }
    }

    private Single<Pair<Boolean, Boolean>> checkExist(Pair<Boolean, ArrayList<String>> pair) {
        if (pair.first && pair.second.size() > 0) {
            return userViewModel.checkIfRentExists(pair.second)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(exist -> new Pair<>(true, exist));
        } else {
            return Single.just(new Pair<>(pair.first, false));
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
