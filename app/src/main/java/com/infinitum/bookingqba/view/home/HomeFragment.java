package com.infinitum.bookingqba.view.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vivchar.rendererrecyclerviewadapter.CompositeViewHolder;
import com.github.vivchar.rendererrecyclerviewadapter.CompositeViewState;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewState;
import com.github.vivchar.rendererrecyclerviewadapter.binder.CompositeViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.CompositeViewStateProvider;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.composite.RecyclerViewItem;
import com.infinitum.bookingqba.view.adapters.headeritem.HeaderItem;
import com.infinitum.bookingqba.view.adapters.rentitem.RentNewItem;
import com.infinitum.bookingqba.view.adapters.rentitem.RentPopItem;
import com.infinitum.bookingqba.view.adapters.rzoneitem.RZoneItem;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;

import java.lang.reflect.Type;
import java.util.Collections;

import dagger.android.support.AndroidSupportInjection;


public class HomeFragment extends BaseNavigationFragment {

    private static final int MAX_SPAN_COUNT = 3;

    private RendererRecyclerViewAdapter recyclerViewAdapter;
    private GridLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private DataGenerator dataGenerator = new DataGenerator();

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initRecycleView(view);
        return view;
    }


    public void initRecycleView(View view){
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerViewAdapter = new RendererRecyclerViewAdapter(getActivity());

        recyclerViewAdapter.registerRenderer(getHeader());

        recyclerViewAdapter.registerRenderer(getCompositeRent());

        recyclerViewAdapter.setItems(dataGenerator.getRZComposite());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(setupLayoutManager());
        recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
    }

    @NonNull
    private CompositeViewBinder<?> getCompositeRent() {
        return (CompositeViewBinder) new CompositeViewBinder<>(
                R.layout.recycler_composite,
                R.id.recycler_view,
                RecyclerViewItem.class,
                Collections.singletonList(new BetweenSpacesItemDecoration(5, 5)),
                new CompositeViewStateProvider<RecyclerViewItem, CompositeViewHolder>() {
                    @Override
                    public ViewState createViewState(@NonNull final CompositeViewHolder holder) {
                        return new CompositeViewState(holder);
                    }

                    @Override
                    public int createViewStateID(@NonNull final RecyclerViewItem model) {
                        return model.getID();
                    }
                }
        ).registerRenderer(getReferenceZone())
                .registerRenderer(getRentPopItem(R.layout.recycler_rent_pop_item))
                .registerRenderer(getRentNewItem(R.layout.recycler_rent_new_item));
    }

    public RecyclerView.LayoutManager setupLayoutManager() {
        mLayoutManager = new GridLayoutManager(getActivity(), MAX_SPAN_COUNT);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                final Type type = recyclerViewAdapter.getType(position);
                if (type.equals(RentPopItem.class) || type.equals(RZoneItem.class)) {
                    return 1;
                }
                return 3;
            }
        });
        return mLayoutManager;
    }


    private ViewBinder<?> getHeader() {
        return new ViewBinder<>(
                R.layout.recycler_header_popular,
                HeaderItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_header_title, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .setOnClickListener(R.id.tv_view_all, (v -> onItemClick(v,model)))
        );
    }

    private ViewBinder<?> getReferenceZone() {
        return new ViewBinder<>(
                R.layout.recycler_ref_zone_item,
                RZoneItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.fork_name, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .find(R.id.fork_avatar, (ViewProvider<AppCompatImageView>) view -> view.setImageResource(model.getIdImage()))
                        .setOnClickListener(R.id.ll_ref_zone_content, (v -> onItemClick(v,model)))
        );
    }

    private ViewBinder<?> getRentPopItem(int layout) {
        return new ViewBinder<>(
                layout,
                RentPopItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .setOnClickListener(R.id.cl_rent_home_content, (v -> onItemClick(v,model)))
        );
    }

    private ViewBinder<?> getRentNewItem(int layout) {
        return new ViewBinder<>(
                layout,
                RentNewItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .setOnClickListener(R.id.cl_rent_home_content, (v -> onItemClick(v,model)))
        );
    }

    public void onItemClick(View view, BaseItem viewModel) {
        if (viewModel instanceof HeaderItem) {
            Toast.makeText(getActivity(), viewModel.getmName() + " View All", Toast.LENGTH_SHORT).show();
        }else if (viewModel instanceof RZoneItem) {
            Toast.makeText(getActivity(), viewModel.getmName(), Toast.LENGTH_SHORT).show();
        } else if (viewModel instanceof RentPopItem) {
            Toast.makeText(getActivity(), viewModel.getmName(), Toast.LENGTH_SHORT).show();
        }else if (viewModel instanceof RentNewItem) {
            Toast.makeText(getActivity(), viewModel.getmName(), Toast.LENGTH_SHORT).show();
        }
    }






}
