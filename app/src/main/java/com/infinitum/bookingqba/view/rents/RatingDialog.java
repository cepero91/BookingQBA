package com.infinitum.bookingqba.view.rents;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
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
import com.infinitum.bookingqba.databinding.DialogRatingBinding;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.RatingVote;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class RatingDialog extends DialogFragment implements View.OnClickListener {

    private DialogRatingBinding dialogRatingBinding;
    private static final String RENTID = "rentid";
    private static final String RENTOWNERID = "rentOwner";
    private String argRentId;
    private String argRentOwnerId;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;

    private RentViewModel rentViewModel;
    private String userId;

    public RatingDialog() {
        // Required empty public constructor
    }

    public static RatingDialog newInstance(String rentUuid, String rentOwnerId) {
        RatingDialog dialogComment = new RatingDialog();
        Bundle arg = new Bundle();
        arg.putString(RENTID, rentUuid);
        arg.putString(RENTOWNERID, rentOwnerId);
        dialogComment.setArguments(arg);
        return dialogComment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argRentId = getArguments().getString(RENTID);
            argRentOwnerId = getArguments().getString(RENTOWNERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dialogRatingBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_rating, container, false);
        // Inflate the layout for this fragment
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialogRatingBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userId = sharedPreferences.getString(USER_ID, "");
        compositeDisposable = new CompositeDisposable();
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        dialogRatingBinding.btnVote.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_vote:
                saveRating();
                break;
        }
    }

    //------------------------------ VOTE ---------------------------

    private void saveRating() {
        if (validateRatingInput()) {
            RatingVote ratingVote = createRating();
            if (networkHelper.isNetworkAvailable()) {
                saveRatingToServer(sharedPreferences.getString(USER_TOKEN, ""), ratingVote);
            } else {
                saveRatingToLocal();
            }
        }
    }

    private void saveRatingToServer(String token, RatingVote ratingVote) {
        disposable = rentViewModel.sendRating(token, ratingVote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.data != null && resultResource.data.getCode() == 200) {
                        AlertUtils.showSuccessToast(getActivity(), getString(R.string.votacion_enviada));
                        dismiss();
                    } else {
                        AlertUtils.showErrorToast(getActivity(), Constants.OPERATIONAL_ERROR_MSG);
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    AlertUtils.showErrorToast(getActivity(), Constants.OPERATIONAL_ERROR_MSG);
                });
        compositeDisposable.add(disposable);
    }

    private RatingVote createRating() {
        return new RatingVote(argRentId, dialogRatingBinding.srScaleRating.getRating(),
                dialogRatingBinding.etVote.getText().toString(), sharedPreferences.getString(USER_ID, ""));
    }

    private void saveRatingToLocal() {
        Map<String, Object> ratingParams = new HashMap<>();
        ratingParams.put("id", UUID.randomUUID().toString());
        ratingParams.put("rating", dialogRatingBinding.srScaleRating.getRating());
        ratingParams.put("comment", dialogRatingBinding.etVote.getText().toString());
        ratingParams.put("userId", sharedPreferences.getString(USER_ID, ""));
        ratingParams.put("rent", argRentId);
        disposable = rentViewModel.addRating(ratingParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> AlertUtils.showSuccessToast(getActivity(), getString(R.string.votacion_guardada)), Timber::e);
        compositeDisposable.add(disposable);
    }

    private boolean validateRatingInput() {
        boolean isValid = true;
        if (dialogRatingBinding.etVote.getText().toString().length() == 0) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            dialogRatingBinding.etVote.startAnimation(animation);
            dialogRatingBinding.etVote.setError(getString(R.string.campo_requerido));
        }
        return isValid;
    }

    //------------------------------ LIFE CYCLE ---------------------

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        compositeDisposable.clear();
        super.onDestroyView();
    }
}
