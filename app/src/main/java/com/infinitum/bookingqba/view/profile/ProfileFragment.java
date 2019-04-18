package com.infinitum.bookingqba.view.profile;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentProfileBinding;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.comments.CommentListAdapter;
import com.infinitum.bookingqba.view.adapters.items.comment.CommentItem;
import com.infinitum.bookingqba.view.adapters.items.profile.RentProfileItem;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.willy.ratingbar.BaseRatingBar;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding profileBinding;
    private DataGenerator dataGenerator;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return profileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        setupPropertyAdapter();

        setupCommentAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ViewBinder<?> getRentProfileItem(int layout) {
        return new ViewBinder<>(
                layout,
                RentProfileItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .find(R.id.sr_scale_rating, (ViewProvider<BaseRatingBar>) view -> view.setRating(model.getRating()))
                        .find(R.id.siv_rent_image, (ViewProvider<RoundedImageView>) view ->
                                GlideApp.with(getView()).load(model.getRentImageTest()).placeholder(R.drawable.placeholder).into(view))
                        .setOnClickListener(R.id.rl_rent_profile_item_content, (v -> updateViews(model)))
        );
    }

    public void setupRecyclerView() {
        ViewCompat.setNestedScrollingEnabled(profileBinding.rvMyProperty, false);
        ViewCompat.setNestedScrollingEnabled(profileBinding.rvComments, false);
        profileBinding.rvMyProperty.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        profileBinding.rvMyProperty.addItemDecoration(new BetweenSpacesItemDecoration(2, 0));
        profileBinding.rvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
        profileBinding.rvComments.addItemDecoration(new BetweenSpacesItemDecoration(2, 0));
    }

    private void setupPropertyAdapter() {
        RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
        adapter.registerRenderer(getRentProfileItem(R.layout.recycler_profile_rent_item));
        adapter.setItems(DataGenerator.getRentProfileItem());
        profileBinding.rvMyProperty.setAdapter(adapter);
    }

    private void setupCommentAdapter() {
        CommentListAdapter adapter = new CommentListAdapter(DataGenerator.getComments());
        profileBinding.rvComments.setAdapter(adapter);
    }

    private void updateViews(ViewModel model) {

    }

}
