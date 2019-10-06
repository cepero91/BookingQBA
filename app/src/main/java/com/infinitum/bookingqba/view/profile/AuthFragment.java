package com.infinitum.bookingqba.view.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentAuthBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.errors.ErrorUtils;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.mateware.snacky.Snacky;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.LAST_EMAIL_REGISTER;

public class AuthFragment extends Fragment implements View.OnClickListener {

    public static final String SIGNIN = "signin";
    public static final String SIGNUP = "signup";
    public static final String CODE = "code";
    private String imeiArg;
    private String lastEmailRegisterArg;
    private Map<String, String> tempUserLoginMap;

    private FragmentAuthBinding fragmentAuthBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    NetworkHelper networkHelper;

    private UserViewModel userViewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private String email;
    private boolean imHost = false;
    private AuthInteraction authInteraction;

    public AuthFragment() {

    }

    public static AuthFragment newInstance(String imei, String lastEmailRegister) {
        AuthFragment authFragment = new AuthFragment();
        Bundle args = new Bundle();
        args.putString(IMEI, imei);
        args.putString(LAST_EMAIL_REGISTER, lastEmailRegister);
        authFragment.setArguments(args);
        return authFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            imeiArg = getArguments().getString(IMEI);
            lastEmailRegisterArg = getArguments().getString(LAST_EMAIL_REGISTER);
        }
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
        fragmentAuthBinding.llBtnAccount.setTag(SIGNIN);

        fragmentAuthBinding.llBtnAccount.setOnClickListener(this);
        fragmentAuthBinding.send.setOnClickListener(this);
        fragmentAuthBinding.tvResendActivationCode.setOnClickListener(this);

        setHasOptionsMenu(true);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        if (!lastEmailRegisterArg.equals(""))
            email = lastEmailRegisterArg;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof AuthInteraction) {
            authInteraction = (AuthInteraction) context;
            compositeDisposable = new CompositeDisposable();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        compositeDisposable.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_btn_account:
                if (fragmentAuthBinding.llBtnAccount.getTag().equals(SIGNIN)) {
                    changeFormUi("Ya eres miembro?", "Entra", SIGNUP, true);
                } else {
                    changeFormUi("No tienes cuenta?", "Registrate", SIGNIN, false);
                }
                break;
            case R.id.send:
                if (v.getTag().equals(SIGNUP)) {
                    validateUserRegister();
                } else if (v.getTag().equals(CODE)) {
                    validateUserActivationCode();
                } else if (v.getTag().equals(SIGNIN)) {
                    validateUserLogin();
                }
                break;
            case R.id.tv_resend_activation_code:
                resendActivationCode();
                break;
        }
    }

    private void changeFormUi(String tvAccountText, String tvBtnAccountText, String tag, boolean isSignUp) {
        fragmentAuthBinding.tvAccountQuestion.setText(tvAccountText);
        fragmentAuthBinding.tvAccountBtn.setText(tvBtnAccountText);
        fragmentAuthBinding.send.setTag(tag);
        fragmentAuthBinding.llBtnAccount.setTag(tag);
        fragmentAuthBinding.setIsSignup(isSignUp);
    }

    private void changeFormUi(String tvAccountText, String tvBtnAccountText, String tag, boolean isSignUp, boolean isActCode) {
        fragmentAuthBinding.tvAccountQuestion.setText(tvAccountText);
        fragmentAuthBinding.tvAccountBtn.setText(tvBtnAccountText);
        fragmentAuthBinding.send.setTag(tag);
        fragmentAuthBinding.llBtnAccount.setTag(tag);
        fragmentAuthBinding.setIsSignup(isSignUp);
        fragmentAuthBinding.setIsCode(isActCode);
    }

    //------------------------------------------ USER LOGIN ---------------------------------
    private void validateUserLogin() {
        if (networkHelper.isNetworkAvailable()) {
            if (validateInputs()) {
                Map<String, String> userLoginParams = getUserLoginParams();
                sendUserLoginParams(userLoginParams);
            }
        } else {
            showErrorDialog("Aviso!!", "No se puede efectuar la operacion. Sin conexion");
        }
    }

    @NonNull
    private Map<String, String> getUserLoginParams() {
        String params1 = fragmentAuthBinding.etUsername.getText().toString();
        String params2 = fragmentAuthBinding.etPassword.getText().toString();
        String params3 = imeiArg;
        Map<String, String> userLoginParams = new HashMap<>();
        userLoginParams.put("username", params1);
        userLoginParams.put("password", params2);
        userLoginParams.put("imei", params3);
        return userLoginParams;
    }

    private void sendUserLoginParams(Map<String, String> userLoginParams) {
        fragmentAuthBinding.send.setEnabled(false);
        disposable = userViewModel.login(userLoginParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful() && response.code() == 200) {
                        authInteraction.signinSuccess(response.body());
                        fragmentAuthBinding.send.setEnabled(true);
                    } else {
                        String errorMsg = ErrorUtils.parseError(response).getMsg();
                        if (errorMsg.equals("500")) {
                            setRegisterUserToActivateCount(true);
                            fragmentAuthBinding.send.setEnabled(true);
                        } else {
                            showErrorSnackbar(errorMsg);
                            fragmentAuthBinding.send.setEnabled(true);
                        }
                    }
                }, throwable -> {
                    String msg = "Un error ha ocurrido";
                    if (throwable instanceof ConnectException || throwable instanceof SocketException) {
                        msg = "Error al conectarse";
                    }
                    showErrorSnackbar(msg);
                    fragmentAuthBinding.send.setEnabled(true);
                });
        compositeDisposable.add(disposable);
    }

    //------------------------------------------- USER REGISTER -------------------------------------------------

    private void validateUserRegister() {
        fragmentAuthBinding.send.setEnabled(false);
        if (networkHelper.isNetworkAvailable()) {
            if (validateInputs()) {
                tempUserLoginMap = getUserLoginParams();
                Map<String, String> registerMap = getUserRegisterParams();
                disposable = userViewModel.register(registerMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resource -> {
                            if (resource.status == Resource.Status.SUCCESS) {
                                verifyUserStatus(resource.data);
                            } else {
                                showErrorSnackbar(resource.message);
                            }
                            fragmentAuthBinding.send.setEnabled(true);
                        }, throwable -> {
                            String msg = "";
                            if (throwable instanceof ConnectException || throwable instanceof SocketException) {
                                msg = "Error al conectarse";
                            }
                            showErrorSnackbar(msg);
                            fragmentAuthBinding.send.setEnabled(true);
                        });
                compositeDisposable.add(disposable);
            } else {
                fragmentAuthBinding.send.setEnabled(true);
            }
        } else {
            showErrorDialog("Aviso!!", "No se puede efectuar la operacion. Sin conexion");
            fragmentAuthBinding.send.setEnabled(true);
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
                        showSuccessSnackbar("Codigo reeviado con exito");
                    } else {
                        showErrorSnackbar("Oops.. algo anda mal");
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void validateUserActivationCode() {
        if (networkHelper.isNetworkAvailable()) {
            if (validateInputs()) {
                String activationCode = fragmentAuthBinding.etActivationCode.getText().toString();
                Map<String, String> map = new HashMap<>();
                map.put("activationCode", activationCode);
                map.put("email", email);
                map.put("imei", imeiArg);
                disposable = userViewModel.activate(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resultResource -> {
                            if (resultResource.status.equals(Resource.Status.SUCCESS)) {
                                verifyActivationCode(resultResource);
                            } else {
                                showErrorSnackbar(resultResource.message);
                            }
                        }, throwable -> {
                            String msg = "Un error ha ocurrido";
                            if (throwable instanceof ConnectException || throwable instanceof SocketException) {
                                msg = "Error al conectarse";
                            }
                            showErrorSnackbar(msg);
                            fragmentAuthBinding.send.setEnabled(true);
                        });
                compositeDisposable.add(disposable);
            }
        } else {
            showErrorDialog("Aviso!!", "No se puede efectuar la operacion. Sin conexion");
        }
    }

    private void verifyActivationCode(Resource<ResponseResult> resultResource) {
        if (resultResource.data != null && resultResource.data.getCode() == 200) {
            AlertUtils.showSuccessSnackbar(getActivity(), "Usuario activado, ya puede autenticarse !!!");
            changeFormUi("No tienes cuenta?", "Registrate", SIGNIN, false, false);
        } else if (resultResource.data != null && resultResource.data.getCode() != 200) {
            showErrorSnackbar(resultResource.data.getMsg());
        }
    }

    private void verifyUserStatus(ResponseResult data) {
        if (data != null && data.getCode() == 200) {
            authInteraction.signupSuccess(email);
            setRegisterUserToActivateCount(false);
        } else if (data != null && data.getCode() != 200) {
            showErrorSnackbar(data.getMsg());
        }
    }

    private void setRegisterUserToActivateCount(boolean isPending) {
        AlertUtils.showCFPositiveInfoAlert(getActivity(), isPending ? getString(R.string.user_pending_activation) : getString(R.string.success_register_user), "Ok, lo entiendo",
                ((dialog, which) -> dialog.dismiss()));
        fragmentAuthBinding.send.setTag(CODE);
        fragmentAuthBinding.setIsCode(true);
        fragmentAuthBinding.llBtnAccount.setVisibility(View.GONE);
    }

    @NonNull
    private Map<String, String> getUserRegisterParams() {
        String username = fragmentAuthBinding.etUsername.getText().toString();
        email = fragmentAuthBinding.etEmail.getText().toString();
        String password = fragmentAuthBinding.etPassword.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("email", email);
        map.put("password", password);
        map.put("imei", imeiArg);
        return map;
    }

    private void resetRegisterForm() {
        fragmentAuthBinding.etUsername.setText("");
        fragmentAuthBinding.etEmail.setText("");
        fragmentAuthBinding.etPassword.setText("");
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String password = fragmentAuthBinding.etPassword.getText().toString();
        String repeatPass = fragmentAuthBinding.etPasswordRepeat.getText().toString();
        String username = fragmentAuthBinding.etUsername.getText().toString();
        String email = fragmentAuthBinding.etEmail.getText().toString();
        String code = fragmentAuthBinding.etActivationCode.getText().toString();
        if (username.equals("")) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            fragmentAuthBinding.etUsername.startAnimation(animation);
            fragmentAuthBinding.etUsername.setError("Campo requerido");
        }
        if (password.equals("")) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            fragmentAuthBinding.etPassword.startAnimation(animation);
            fragmentAuthBinding.etPassword.setError("Campo requerido");
        }
        if (fragmentAuthBinding.send.getTag().equals(SIGNUP)) {
            if (email.equals("")) {
                isValid = false;
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
                fragmentAuthBinding.etEmail.startAnimation(animation);
                fragmentAuthBinding.etEmail.setError("Campo requerido");

            }
            if (repeatPass.equals("")) {
                isValid = false;
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
                fragmentAuthBinding.etPasswordRepeat.startAnimation(animation);
                fragmentAuthBinding.etPasswordRepeat.setError("Campo requerido");
            }
            if (!repeatPass.equals(password)) {
                isValid = false;
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
                fragmentAuthBinding.etPasswordRepeat.startAnimation(animation);
                fragmentAuthBinding.etPasswordRepeat.setError("Contraseñas diferentes");
                fragmentAuthBinding.etPassword.startAnimation(animation);
            }
        }
        if (fragmentAuthBinding.send.getTag().equals(CODE)) {
            if (code.equals("")) {
                isValid = false;
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
                fragmentAuthBinding.etActivationCode.startAnimation(animation);
            }
        }
        return isValid;
    }

    //------------------------------- NOTIFICATION ------------------------------

    private void showErrorDialog(String title, String msg) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.addButton("Ok, lo entiendo", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE,
                CFAlertDialog.CFAlertActionAlignment.CENTER, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showSuccessDialog(String title, String msg) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.addButton("ok", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE,
                CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showErrorSnackbar(String message) {
        Snacky.builder()
                .setActivity(getActivity())
                .setText(message)
                .error()
                .show();
    }

    private void showSuccessSnackbar(String message) {
        Snacky.builder()
                .setActivity(getActivity())
                .setText(message)
                .setDuration(8000)
                .setBackgroundColor(Color.parseColor("#009688"))
                .setIcon(R.drawable.ic_check_circle)
                .build()
                .show();
    }

    private void showWarningSnackbar(String message) {
        Snacky.builder()
                .setActivity(getActivity())
                .setText(message)
                .setDuration(8000)
                .setBackgroundColor(Color.parseColor("#FFC107"))
                .setIcon(R.drawable.ic_exclamation_triangle)
                .build()
                .show();
    }


    //------------------------------- INTERFACE ---------------------------------

    public interface AuthInteraction {
        void signinSuccess(User user);

        void signupSuccess(String email);
    }
}
