package com.infinitum.bookingqba.view.galery;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityGaleryBinding;
import com.infinitum.bookingqba.view.adapters.GaleryPagerAdapter;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class GaleryActivity extends AppCompatActivity {

    private ActivityGaleryBinding galeryBinding;
    private GaleryPagerAdapter galeryPagerAdapter;
    private List<RentGalerieItem> imagePathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galeryBinding = DataBindingUtil.setContentView(this,R.layout.activity_galery);

        if(getIntent().hasExtra("imageList")){
            String id = getIntent().getStringExtra("id");
            imagePathList = getIntent().getParcelableArrayListExtra("imageList");
            galeryPagerAdapter = new GaleryPagerAdapter(this,getLayoutInflater(),imagePathList);
            int index = galeryPagerAdapter.itemPosById(id);
            galeryBinding.vpGalery.setAdapter(galeryPagerAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(galeryBinding.vpGalery);
            galeryBinding.dotsIndicator.setViewPager(galeryBinding.vpGalery);
            galeryBinding.vpGalery.setCurrentItem(index);
        }


    }
}
