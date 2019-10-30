package com.infinitum.bookingqba.view.rents;


import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerOfferBinding;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentOfferItem;

import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;


public class RentOfferFragment extends DialogFragment {

    private FragmentInnerOfferBinding innerOfferBinding;

    private static final String ARG_OFFER = "offer";

    private ArrayList<RentOfferItem> argOffer;

    public RentOfferFragment() {
        // Required empty public constructor
    }

    public static RentOfferFragment newInstance(ArrayList<RentOfferItem> argOffer) {
        RentOfferFragment fragment = new RentOfferFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_OFFER, argOffer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.MyDialogFragment);
        if (getArguments() != null) {
            argOffer = getArguments().getParcelableArrayList(ARG_OFFER);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        innerOfferBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inner_offer, container, false);
        return innerOfferBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
        setupOfferAdapter(argOffer);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

    }

    private void setupOfferAdapter(ArrayList<RentOfferItem> argOffer) {
        if (argOffer != null && argOffer.size() > 0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getCommentBinder());
            adapter.setItems(argOffer);
            innerOfferBinding.rvOffer.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            innerOfferBinding.rvOffer.setAdapter(adapter);
            innerOfferBinding.rvOffer.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        }
    }

    private ViewBinder<?> getCommentBinder() {
        return new ViewBinder<>(
                R.layout.recycler_offer_item,
                RentOfferItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getTitle()))
                        .find(R.id.tv_description, (ViewProvider<TextView>) view -> view.setText(model.getDescription()))
                        .find(R.id.tv_price, (ViewProvider<TextView>) view -> {
                            view.setText(String.format("$%.2f", model.getPrice()));
                        })
        );
    }
}
