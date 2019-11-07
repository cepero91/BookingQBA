package com.infinitum.bookingqba.view.rents;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.hendraanggrian.reveallayout.Radius;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerCommentBinding;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.ColorUtil;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.util.EmotionUtil;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.customview.StateView;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
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


public class RentCommentFragment extends DialogFragment implements View.OnClickListener {

    private FragmentInnerCommentBinding innerCommentBinding;

    private static final String ARG_COMMENT = "comment";
    private static final String ARG_RENT_UUID = "rentUuid";
    private static final String ARG_RENT_OWNER = "rentOwnerId";

    private ArrayList<RentCommentItem> argComment;

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
    private String ownerId;
    private String argRentUuid;
    private boolean isOpenRatingView = false;
    private int lastEmotionLevelSelected = -1;


    public RentCommentFragment() {
        // Required empty public constructor
    }

    public static RentCommentFragment newInstance(String argRentUuid, String argOwnerId, ArrayList<RentCommentItem> argComment) {
        RentCommentFragment fragment = new RentCommentFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_COMMENT, argComment);
        args.putString(ARG_RENT_UUID, argRentUuid);
        args.putString(ARG_RENT_OWNER, argOwnerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogFragment);
        if (getArguments() != null) {
            argComment = getArguments().getParcelableArrayList(ARG_COMMENT);
            argRentUuid = getArguments().getString(ARG_RENT_UUID);
            ownerId = getArguments().getString(ARG_RENT_OWNER);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        innerCommentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inner_comment, container, false);
        return innerCommentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
        innerCommentBinding.stateView.setStatus(StateView.Status.LOADING);
        userId = sharedPreferences.getString(USER_ID, "");
        innerCommentBinding.setCommentVisible(sharedPreferences.getBoolean(USER_IS_AUTH, false) ? View.VISIBLE : View.GONE);
        compositeDisposable = new CompositeDisposable();
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        innerCommentBinding.ivShowRating.setOnClickListener(this);
        innerCommentBinding.ivSend.setOnClickListener(this);
        innerCommentBinding.ivBtnBack.setOnClickListener(this);
        setupRatingSmileView();
        setupCommentAdapter(argComment);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        compositeDisposable.clear();
        super.onDestroyView();
    }

    private void setupRatingSmileView() {
        innerCommentBinding.ratingView.setNameForSmile(BaseRating.TERRIBLE, "Furioso");
        innerCommentBinding.ratingView.setNameForSmile(BaseRating.BAD, "Disgustado");
        innerCommentBinding.ratingView.setNameForSmile(BaseRating.OKAY, "Serio");
        innerCommentBinding.ratingView.setNameForSmile(BaseRating.GOOD, "Contento");
        innerCommentBinding.ratingView.setNameForSmile(BaseRating.GREAT, "Excelente");
        innerCommentBinding.ratingView.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.montserrat_bold));
        innerCommentBinding.ratingView.setTextSelectedColor(getResources().getColor(R.color.material_color_blue_grey_500));
        innerCommentBinding.ratingView.setTextNonSelectedColor(getResources().getColor(R.color.material_color_blue_grey_300));
        innerCommentBinding.ratingView.setOnSmileySelectionListener((level, reselected) -> {
            updateIconRating(level);
            onClick(innerCommentBinding.ivShowRating);
        });
    }

    private void updateIconRating(int level) {
        lastEmotionLevelSelected = level + 1;
        innerCommentBinding.ivShowRating.setImageTintList(null);
        innerCommentBinding.ivShowRating.setImageResource(EmotionUtil.getEmotionDrawableId(lastEmotionLevelSelected));
    }

    private void setupCommentAdapter(ArrayList<RentCommentItem> argComment) {
        if (argComment != null && argComment.size() > 0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getCommentBinder());
            adapter.setItems(argComment);
            innerCommentBinding.rvComment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            innerCommentBinding.rvComment.setAdapter(adapter);
            innerCommentBinding.rvComment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        } else {
            innerCommentBinding.stateView.setStatus(StateView.Status.NO_COMMENT);
        }
    }

    private ViewBinder<?> getCommentBinder() {
        return new ViewBinder<>(
                R.layout.recycler_comment_item,
                RentCommentItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_username, (ViewProvider<TextView>) view -> {
                            if (model.isOwner()) {
                                view.setTextColor(Color.parseColor("#FFC400"));
                                view.setText(R.string.el_anfitrion);
                            } else {
                                view.setText(model.getUsername());
                            }
                        })
                        .find(R.id.iv_emotion, (ViewProvider<AppCompatImageView>) view -> {
                            view.setImageResource(EmotionUtil.getEmotionDrawableId(model.getEmotion()));
                        })
                        .find(R.id.tv_description, (ViewProvider<TextView>) view -> view.setText(Html.fromHtml("&ldquo;" + model.getDescription() + "&rdquo;")))
                        .find(R.id.tv_relative_date, (ViewProvider<TextView>) view -> {
                            TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build();
                            String dateRelative = TimeAgo.using(model.getCreated().getTime(), messages);
                            view.setText(dateRelative);
                        })
                        .find(R.id.siv_avatar, (ViewProvider<CircularImageView>) view -> {
                            view.setImageBitmap(BitmapFactory.decodeByteArray(model.getAvatar(), 0, model.getAvatar().length));
                        }));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_show_rating:
                int cx = (innerCommentBinding.ratingView.getLeft() + innerCommentBinding.ratingView.getRight());
                int cy = innerCommentBinding.ratingView.getTop();
                int radius = Math.max(innerCommentBinding.ratingView.getWidth(), innerCommentBinding.ratingView.getHeight());
                if (isOpenRatingView) {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(innerCommentBinding.ratingView, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            innerCommentBinding.ratingView.setAlpha(0);
                            innerCommentBinding.revealLayout.setAlpha(0);
                            innerCommentBinding.ratingView.setEnabled(false);
                        }
                    });
                    anim.start();
                    isOpenRatingView = false;
                } else {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(innerCommentBinding.ratingView, cx, cy, 0, radius);
                    innerCommentBinding.revealLayout.setAlpha(1);
                    innerCommentBinding.ratingView.setAlpha(1);
                    innerCommentBinding.ratingView.setEnabled(true);
                    anim.start();
                    isOpenRatingView = true;
                }
                break;
            case R.id.iv_send:
                saveComment();
                break;
            case R.id.iv_btn_back:
                dismiss();
                break;
        }
    }

    //-------------------------------------------- COMMENT ----------------------------------

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
                    if (resultResource.data != null && resultResource.data.getCode() == 200) {
                        Toast.makeText(getActivity(), getString(R.string.comentario_enviado), Toast.LENGTH_SHORT).show();
                        resetAllInputs();
                    } else {
                        Toast.makeText(getActivity(), Constants.OPERATIONAL_ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    Toast.makeText(getActivity(), Constants.OPERATIONAL_ERROR_MSG, Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(disposable);
    }

    private void resetAllInputs() {
        lastEmotionLevelSelected = -1;
        innerCommentBinding.ratingView.setSelectedSmile(BaseRating.NONE);
        innerCommentBinding.etComment.setText("");
        innerCommentBinding.ivShowRating.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.material_color_blue_grey_100)));
    }

    private void saveCommentToLocal(Comment comment) {
        disposable = rentViewModel.addComment(comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getActivity(), getString(R.string.comentario_guardado), Toast.LENGTH_SHORT).show();
                    resetAllInputs();
                }, throwable -> {
                    Timber.e(throwable);
                    Toast.makeText(getActivity(), Constants.OPERATIONAL_ERROR_MSG, Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(disposable);
    }

    private Comment createComment() {
        String userName = sharedPreferences.getString(USER_NAME, "");
        int emotionLevel = innerCommentBinding.ratingView.getRating();
        String decription = innerCommentBinding.etComment.getText().toString();
        Comment comment = new Comment(UUID.randomUUID().toString(), userName);
        comment.setUserid(userId);
        comment.setRent(argRentUuid);
        comment.setDescription(decription);
        comment.setActive(false);
        comment.setAvatar(null);
        comment.setOwner(userId.equals(ownerId));
        comment.setEmotion(emotionLevel);
        comment.setCreated(DateUtils.currentDateToString());
        return comment;
    }

    private boolean validateInputs() {
        boolean isValid = true;
        if (innerCommentBinding.etComment.getText().toString().length() == 0) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            innerCommentBinding.etComment.startAnimation(animation);
            innerCommentBinding.etComment.setError("Este campo es requerido");
        }
        if (lastEmotionLevelSelected == -1) {
            isValid = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            innerCommentBinding.ivShowRating.startAnimation(animation);
        }
        return isValid;
    }
}
