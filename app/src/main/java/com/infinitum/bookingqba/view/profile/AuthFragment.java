package com.infinitum.bookingqba.view.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class AuthFragment extends Fragment implements View.OnClickListener {

    public static final String SIGNIN = "signin";
    public static final String SIGNUP = "signup";
    public static final String CODE = "code";
    private String imeiArg;

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

    public static AuthFragment newInstance(String imei) {
        AuthFragment authFragment = new AuthFragment();
        Bundle args = new Bundle();
        args.putString(IMEI, imei);
        authFragment.setArguments(args);
        return authFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            imeiArg = getArguments().getString(IMEI);
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
        fragmentAuthBinding.tvImVisitor.setOnClickListener(this);
        fragmentAuthBinding.tvImHost.setOnClickListener(this);

        fragmentAuthBinding.llBtnAccount.setOnClickListener(this);
        fragmentAuthBinding.send.setOnClickListener(this);
        fragmentAuthBinding.tvResendActivationCode.setOnClickListener(this);

        setHasOptionsMenu(true);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

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
                    fragmentAuthBinding.tvAccountQuestion.setText("Ya eres miembro?");
                    fragmentAuthBinding.tvAccountBtn.setText("Entra");
                    fragmentAuthBinding.send.setTag(SIGNUP);
                    fragmentAuthBinding.llBtnAccount.setTag(SIGNUP);
                    fragmentAuthBinding.setIsSignup(true);
                } else {
                    fragmentAuthBinding.tvAccountQuestion.setText("No tienes cuenta?");
                    fragmentAuthBinding.tvAccountBtn.setText("Registrate");
                    fragmentAuthBinding.send.setTag(SIGNIN);
                    fragmentAuthBinding.llBtnAccount.setTag(SIGNIN);
                    fragmentAuthBinding.setIsSignup(false);
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
            case R.id.tv_im_visitor:
                fragmentAuthBinding.tvImVisitor.setBackgroundResource(R.drawable.shape_filter_ship_selected);
                fragmentAuthBinding.tvImVisitor.setTextColor(Color.WHITE);
                fragmentAuthBinding.tvImHost.setBackgroundResource(R.drawable.shape_filter_ship_unselected);
                fragmentAuthBinding.tvImHost.setTextColor(Color.parseColor("#607d8b"));
                imHost = false;
                break;
            case R.id.tv_im_host:
                fragmentAuthBinding.tvImHost.setBackgroundResource(R.drawable.shape_filter_ship_selected);
                fragmentAuthBinding.tvImHost.setTextColor(Color.WHITE);
                fragmentAuthBinding.tvImVisitor.setBackgroundResource(R.drawable.shape_filter_ship_unselected);
                fragmentAuthBinding.tvImVisitor.setTextColor(Color.parseColor("#607d8b"));
                imHost = true;
                break;
        }
    }

    //------------------------------------------ USER LOGIN ---------------------------------
    private void validateUserLogin() {
//        if (networkHelper.isNetworkAvailable()) {
        if (validateInputs()) {
            String params1 = fragmentAuthBinding.etUsername.getText().toString();
            String params2 = fragmentAuthBinding.etPassword.getText().toString();
            String params3 = imeiArg;
            Map<String, String> userLoginParams = new HashMap<>();
            userLoginParams.put("param1", params1);
            userLoginParams.put("param2", params2);
            userLoginParams.put("param3", params3);
            sendUserLoginParams(userLoginParams);
        }
//        } else {
//            showErrorDialog("Aviso!!", "No se puede efectuar la operacion. Sin conexion");
//        }
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
                    } else if(response.isSuccessful() && response.code() == 201){
                        setRegisterUserToActivateCount("Usuario pendiente de activacion.");
                        fragmentAuthBinding.send.setEnabled(true);
                    } else {
                        String errorMsg = ErrorUtils.parseError(response).getMsg();
                        showErrorSnackbar(errorMsg);
                        fragmentAuthBinding.send.setEnabled(true);
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
//        if (networkHelper.isNetworkAvailable()) {
        if (validateInputs()) {
            Map<String, String> map = getUserMap();
            disposable = userViewModel.register(map)
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
        }
//        } else {
//            showErrorDialog("Aviso!!", "No se puede efectuar la operacion. Sin conexion");
//        }
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

    private void validateUserActivationCode() {
//        if (networkHelper.isNetworkAvailable()) {
        if (validateInputs()) {
            String activationCode = fragmentAuthBinding.etActivationCode.getText().toString();
            Map<String, String> map = new HashMap<>();
            map.put("activationCode", activationCode);
            map.put("email", email);
            disposable = userViewModel.activate(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resultResource -> {
                        if (resultResource.status == Resource.Status.SUCCESS) {
                            showSuccessDialog("Aviso!!!", "Usuario activado con exito");
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
//        } else {
//            showErrorDialog("Aviso!!", "No se puede efectuar la operacion. Sin conexion");
//        }
    }

    private void verifyUserStatus(ResponseResult data) {
        if (data != null && data.getCode() == 200) {
            setRegisterUserToActivateCount(data.getMsg());
        } else if (data != null && data.getCode() != 500) {
            showErrorSnackbar(data.getMsg());
        }
    }

    private void setRegisterUserToActivateCount(@Nullable String message) {
        showSuccessSnackbar(message);
        fragmentAuthBinding.send.setTag(CODE);
        fragmentAuthBinding.setIsCode(true);
        fragmentAuthBinding.llBtnAccount.setVisibility(View.GONE);
    }

    @NonNull
    private Map<String, String> getUserMap() {
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
        String username = fragmentAuthBinding.etUsername.getText().toString();
        String email = fragmentAuthBinding.etEmail.getText().toString();
        String code = fragmentAuthBinding.etActivationCode.getText().toString();
        if (username.equals("")) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            fragmentAuthBinding.tilUsername.startAnimation(animation);
        }
        if (password.equals("")) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            fragmentAuthBinding.tilPassword.startAnimation(animation);
        }
        if (fragmentAuthBinding.send.getTag().equals(SIGNUP)) {
            if (email.equals("")) {
                isValid = false;
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
                fragmentAuthBinding.tilEmail.startAnimation(animation);
            }
        }
        if (fragmentAuthBinding.send.getTag().equals(CODE)) {
            if (code.equals("")) {
                isValid = false;
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
                fragmentAuthBinding.tilActivationCode.startAnimation(animation);
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


    //------------------------------- INTERFACE ---------------------------------

    public interface AuthInteraction {
        void signinSuccess(User user);
    }
}
