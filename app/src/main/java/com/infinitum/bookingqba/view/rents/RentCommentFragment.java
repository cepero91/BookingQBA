package com.infinitum.bookingqba.view.rents;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerCommentBinding;
import com.infinitum.bookingqba.util.ColorUtil;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;

import java.util.ArrayList;
import java.util.Locale;


public class
RentCommentFragment extends Fragment {

    private FragmentInnerCommentBinding innerCommentBinding;

    private static final String ARG_COMMENT = "comment";

    private ArrayList<RentCommentItem> argComment;
    private Locale locale;


    public RentCommentFragment() {
        // Required empty public constructor
    }

    public static RentCommentFragment newInstance(ArrayList<RentCommentItem> argComment) {
        RentCommentFragment fragment = new RentCommentFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_COMMENT, argComment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argComment = getArguments().getParcelableArrayList(ARG_COMMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        innerCommentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inner_comment, container, false);
        return innerCommentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locale = new Locale("es", "ES");
        setupCommentAdapter(argComment);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
    }

    private void setupCommentAdapter(ArrayList<RentCommentItem> argComment) {
        if (argComment != null && argComment.size() > 0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getCommentBinder());
            adapter.setItems(argComment);
            innerCommentBinding.rvComment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            innerCommentBinding.rvComment.setAdapter(adapter);
            innerCommentBinding.rvComment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
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
                                view.setText("El Anfitrion");
                            } else {
                                view.setText(model.getUsername());
                            }
                        })
                        .find(R.id.iv_emotion,(ViewProvider<AppCompatImageView>) view -> {
                            view.setImageResource(getEmotionDrawableId(model.getEmotion()));
                        })
                        .find(R.id.tv_description, (ViewProvider<TextView>) view -> view.setText(model.getDescription()))
                        .find(R.id.tv_relative_date, (ViewProvider<TextView>) view -> {
                            TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(locale).build();
                            String dateRelative = TimeAgo.using(model.getCreated().getTime(), messages);
                            view.setText(dateRelative);
                        })
                        .find(R.id.siv_avatar, (ViewProvider<CircularImageView>) view -> GlideApp.with(getView()).load(model.getAvatar()).into(view))
        );
    }

    public int getEmotionDrawableId(int emotionLevel){
        int id = -1;
        switch (emotionLevel){
            case 1:
                id = R.drawable.ic_angry_emotion;
                break;
            case 2:
                id = R.drawable.ic_frown_emotion;
                break;
            case 3:
                id = R.drawable.ic_meh_emotion;
                break;
            case 4:
                id = R.drawable.ic_grin_emotion;
                break;
            case 5:
                id = R.drawable.ic_laugh_emotion;
                break;
        }
        return id;
    }
}
