package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentMyRentsBinding;

public class MyRentsFragment extends Fragment implements View.OnClickListener{

    private FragmentMyRentsBinding binding;
    private AddRentClick addRentClick;

    public MyRentsFragment() {
    }

    public static MyRentsFragment newInstance(){
        return new MyRentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_rents,container,false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.fabAddRent.setOnClickListener(this);

        binding.setIsLoading(false);
        binding.setIsEmpty(true);
        binding.progressPvLinear.stop();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof AddRentClick){
            this.addRentClick = (AddRentClick) context;
        }
    }

    @Override
    public void onDetach() {
        this.addRentClick = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_add_rent:
                addRentClick.onAddRentClick();
                break;
        }
    }

    public interface AddRentClick{
        void onAddRentClick();
    }
}
