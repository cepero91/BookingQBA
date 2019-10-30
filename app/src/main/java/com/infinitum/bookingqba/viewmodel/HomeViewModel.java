package com.infinitum.bookingqba.viewmodel;


import android.support.annotation.NonNull;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.pojo.RentMostComment;
import com.infinitum.bookingqba.model.local.pojo.RentMostRating;
import com.infinitum.bookingqba.model.repository.province.ProvinceRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.baseitem.RecyclerViewItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentMostRatingItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentMostCommentItem;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends android.arch.lifecycle.ViewModel {

    private ReferenceZoneRepository referenceZoneRepository;
    private RentRepository rentRepository;
    private ProvinceRepository provinceRepository;


    @Inject
    public HomeViewModel(ReferenceZoneRepository referenceZoneRepository, RentRepository rentRepository, ProvinceRepository provinceRepository) {
        this.referenceZoneRepository = referenceZoneRepository;
        this.rentRepository = rentRepository;
        this.provinceRepository = provinceRepository;
    }


    //----------------------------------- GET METHOD ------------------------------------------//

    public Flowable<Resource<CommonSpinnerList>> getProvinces() {
        return provinceRepository
                .allProvinces()
                .map(this::transformProvinces)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }



    public Flowable<Resource<List<BaseItem>>> getFiveMostCommentRent(String province) {
        return rentRepository.fiveMostCommentRents(province)
                .map(this::transformMostCommentRentEntity)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<Resource<List<BaseItem>>> getFiveMostRatingRents(String province) {
        return rentRepository.fiveMostRatingRents(province)
                .map(this::transformMostRatingRentEntity)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<Resource<Map<String,List<BaseItem>>>> getAllItems(String province) {
        return Flowable.combineLatest(getFiveMostCommentRent(province), getFiveMostRatingRents(province),
                this::getCompositeRecyclerViewModels)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }


    @NonNull
    private Map<String,List<BaseItem>> getCompositeRecyclerViewModels(Resource<List<BaseItem>> listResource, Resource<List<BaseItem>> listResource2) {
        Map<String,List<BaseItem>> listMap = new HashMap<>();
        listMap.put("MostCommented", listResource.data);
        listMap.put("MostRating", listResource2.data);
        return listMap;
    }


    //----------------------------------- TRANSFORM METHOD -------------------------------------//

    private Resource<List<BaseItem>> transformMostRatingRentEntity(Resource<List<RentMostRating>> listResource) {
        List<BaseItem> compositeList = new ArrayList<>();
        RentMostRatingItem tempItem;
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentMostRating entity : listResource.data) {
                String imagePath = entity.getImageAtPos(0);
                tempItem = new RentMostRatingItem(entity.getId(), entity.getName(), imagePath, (float) entity.getPrice(),entity.getRentMode());
                tempItem.setRating(entity.getRating());
                tempItem.setRatingCount(entity.getRatingCount());
                compositeList.add(tempItem);
            }
            return Resource.success(compositeList);
        } else {
            return Resource.error("Null or empty values");
        }
    }

    private Resource<List<BaseItem>> transformMostCommentRentEntity(Resource<List<RentMostComment>> listResource) {
        List<BaseItem> items = new ArrayList<>();
        RentMostCommentItem tempItem;
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentMostComment entity : listResource.data) {
                String imagePath = entity.getImageAtPos(0);
                tempItem = new RentMostCommentItem(entity.getId(), entity.getName(), imagePath, (float) entity.getPrice(), entity.getRentMode());
                tempItem.setTotalComment(entity.getTotalComment());
                tempItem.setEmotionAvg((int)entity.getEmotionAvg());
                tempItem.setRating(entity.getRating());
                tempItem.setRatingCount(entity.getRatingCount());
                items.add(tempItem);
            }
            return Resource.success(items);
        } else {
            return Resource.error("Null or empty values");
        }
    }


    private CommonSpinnerList transformProvinces(Resource<List<ProvinceEntity>> listResource) {
        List<CommonSpinnerItem> spinnerProvinceItemList =  new ArrayList<>();
        CommonSpinnerList provinceSpinnerList = new CommonSpinnerList(spinnerProvinceItemList);
        if(listResource.data!= null && listResource.data.size()>0){
           for(ProvinceEntity entity: listResource.data){
               spinnerProvinceItemList.add(new CommonSpinnerItem(entity.getId(),entity.getName()));
           }
           provinceSpinnerList = new CommonSpinnerList(spinnerProvinceItemList);
        }
        return provinceSpinnerList;
    }


}
