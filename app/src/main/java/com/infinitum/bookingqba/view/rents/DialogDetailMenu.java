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
import com.infinitum.bookingqba.databinding.FragmentDetailMenuBinding;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.RatingVote;
import com.infinitum.bookingqba.util.AlertUtils;
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

public class DialogDetailMenu extends DialogFragment implements View.OnClickListener {

    private FragmentDetailMenuBinding menuBinding;
    private static final String WISHED = "wished";
    private static final String RENTID = "rentid";
    private static final String RENTOWNERID = "rentOwner";
    private String argRentId;
    private String argRentOwnerId;
    private int argWished = 0;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;

    private RentViewModel rentViewModel;

    public DialogDetailMenu() {
        // Required empty public constructor
    }

    public static DialogDetailMenu newInstance(int wished, String rentOwnerId, String rentid) {
        DialogDetailMenu dialogComment = new DialogDetailMenu();
        Bundle arg = new Bundle();
        arg.putInt(WISHED, wished);
        arg.putString(RENTID, rentid);
        arg.putString(RENTOWNERID, rentOwnerId);
        dialogComment.setArguments(arg);
        return dialogComment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argWished = getArguments().getInt(WISHED);
            argRentId = getArguments().getString(RENTID);
            argRentOwnerId = getArguments().getString(RENTOWNERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        menuBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_menu, container, false);
        // Inflate the layout for this fragment
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return menuBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        compositeDisposable = new CompositeDisposable();
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        int width = getResources().getDimensionPixelSize(R.dimen.dialog_login_width);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);

        setupRatingSmileView();
        setupViewListener();
        setupBtnEnabled();

        if (argWished == 1) {
            updateWishedUi("Deseada", R.color.colorAccent, false);
        }

    }

    private void setupBtnEnabled() {
        boolean userIsAuth = sharedPreferences.getBoolean(USER_IS_AUTH, false);
        if (!userIsAuth) {
            menuBinding.llComment.setAlpha(0.5f);
            menuBinding.llComment.setEnabled(false);
            menuBinding.llVote.setAlpha(0.5f);
            menuBinding.llVote.setEnabled(false);
        } else {
            menuBinding.llComment.setAlpha(1f);
            menuBinding.llComment.setEnabled(true);
            menuBinding.llVote.setAlpha(1f);
            menuBinding.llVote.setEnabled(true);
        }
    }

    private void setupViewListener() {
        menuBinding.llComment.setOnClickListener(this);
        menuBinding.llVote.setOnClickListener(this);
        menuBinding.llListWich.setOnClickListener(this);
        menuBinding.btnComment.setOnClickListener(this);
        menuBinding.btnVote.setOnClickListener(this);
        menuBinding.btnComment.setOnClickListener(this);
    }

    private void setupRatingSmileView() {
        menuBinding.ratingView.setNameForSmile(BaseRating.TERRIBLE, "Terrible");
        menuBinding.ratingView.setNameForSmile(BaseRating.BAD, "Malo");
        menuBinding.ratingView.setNameForSmile(BaseRating.OKAY, "Mejorable");
        menuBinding.ratingView.setNameForSmile(BaseRating.GOOD, "Bueno");
        menuBinding.ratingView.setNameForSmile(BaseRating.GREAT, "Excelente");
        menuBinding.ratingView.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.poppinsbold));
        menuBinding.ratingView.setTextSelectedColor(getResources().getColor(R.color.material_color_blue_grey_500));
        menuBinding.ratingView.setTextNonSelectedColor(getResources().getColor(R.color.material_color_blue_grey_300));
        menuBinding.ratingView.setSelectedSmile(1);
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
            case R.id.ll_comment:
                menuBinding.llContentBtnMenu.setVisibility(View.GONE);
                menuBinding.llContentComment.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_vote:
                menuBinding.llContentBtnMenu.setVisibility(View.GONE);
                menuBinding.llContentVote.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_list_wich:
                if (argWished == 0) {
                    argWished = 1;
                    updateWishedUi("Deseada", R.color.colorAccent, false);
                    updateWishedRent(argRentId, argWished);
                } else {
                    argWished = 0;
                    updateWishedUi("Desear", R.color.material_color_blue_grey_500, false);
                    updateWishedRent(argRentId, argWished);
                }
                break;
            case R.id.btn_vote:
                saveRating();
                break;
            case R.id.btn_comment:
                saveComment();
                break;
        }
    }

    private void updateWishedUi(String value, @ColorRes int colorId, boolean silently) {
        menuBinding.ivListWich.setImageTintList(ColorStateList.valueOf(getResources().getColor(colorId)));
        menuBinding.tvWishedValue.setText(value);
        if (!silently) {
            switch (colorId) {
                case R.color.colorAccent:
                    AlertUtils.showSuccessToast(getActivity(), "Renta agregada a lista de deseo");
                    break;
                case R.color.material_color_blue_grey_500:
                    AlertUtils.showErrorToast(getActivity(), "Renta removida de lista de deseo");
                    break;
            }
        }
    }

    private void updateWishedRent(String rentUuid, int wished) {
        disposable = rentViewModel.updateRent(rentUuid, wished)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    //------------------------------ COMMENT ------------------------

    private void saveComment() {
        if (validateInputs()) {
            Comment comment = createComment();
            if (networkHelper.isNetworkAvailable()) {
                saveCommentToServer(sharedPreferences.getString(USER_TOKEN, ""), comment);
            } else {
                saveCommentToLocal(comment);
            }
        }
    }

    private void saveCommentToServer(String token, Comment comment) {
        disposable = rentViewModel.sendComment(token, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if(resultResource.data!=null && resultResource.data.getCode() == 200){
                        Timber.e("hola");
                    }
                    AlertUtils.showSuccessToast(getActivity(), "Comentario enviado con exito");
//                    dismiss();
                }, throwable -> {
                    Timber.e(throwable);
                    AlertUtils.showErrorToast(getActivity(), "Un problema ha ocurrido");
                });
        compositeDisposable.add(disposable);
    }

    private void saveCommentToLocal(Comment comment) {
        disposable = rentViewModel.addComment(comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    AlertUtils.showSuccessToast(getActivity(), "Comentario guardado con exito");
                    dismiss();
                }, throwable -> {
                    Timber.e(throwable);
                    AlertUtils.showErrorToast(getActivity(), "Un problema ha ocurrido");
                });
        compositeDisposable.add(disposable);
    }

    private Comment createComment() {
        String userName = sharedPreferences.getString(USER_NAME, "");
        String userId = sharedPreferences.getString(USER_ID, "");
        int emotionLevel = menuBinding.ratingView.getRating();
        String decription = menuBinding.etComment.getText().toString();
        Comment comment = new Comment(UUID.randomUUID().toString(), userName);
        comment.setUserid(userId);
        comment.setRent(argRentId);
        comment.setDescription(decription);
        comment.setActive(false);
        comment.setAvatar(null);
        comment.setOwner(userId.equals(argRentOwnerId));
        comment.setEmotion(emotionLevel);
        comment.setCreated(DateUtils.currentDateToString());
        return comment;
    }

    private boolean validateInputs() {
        boolean isValid = true;
        if (menuBinding.etComment.getText().toString().length() == 0) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            menuBinding.etComment.startAnimation(animation);
            menuBinding.etComment.setError("Este campo es requerido");
        }
        return isValid;
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
            dismiss();
        }
    }

    private void saveRatingToServer(String token, RatingVote ratingVote) {
        disposable = rentViewModel.sendRating(token, ratingVote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.data != null && resultResource.data.getCode() == 200)
                        AlertUtils.showSuccessToast(getActivity(), "Votacion exitosa");
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private RatingVote createRating() {
        return new RatingVote(argRentId, menuBinding.srScaleRating.getRating(),
                menuBinding.etVote.getText().toString(), sharedPreferences.getString(USER_ID,""));
    }

    private void saveRatingToLocal() {
        Map<String, Object> ratingParams = new HashMap<>();
        ratingParams.put("id",UUID.randomUUID().toString());
        ratingParams.put("rating", menuBinding.srScaleRating.getRating());
        ratingParams.put("comment",menuBinding.etVote.getText().toString());
        ratingParams.put("userId",sharedPreferences.getString(USER_ID,""));
        ratingParams.put("rent",argRentId);
        disposable = rentViewModel.addRating(ratingParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> AlertUtils.showSuccessToast(getActivity(), "Votacion exitosa"), Timber::e);
        compositeDisposable.add(disposable);
    }

    private boolean validateRatingInput() {
        boolean isValid = true;
        if (menuBinding.etVote.getText().toString().length() == 0) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            menuBinding.etVote.startAnimation(animation);
            menuBinding.etVote.setError("Este campo es requerido");
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
