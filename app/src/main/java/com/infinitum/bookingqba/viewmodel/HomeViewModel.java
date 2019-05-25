package com.infinitum.bookingqba.viewmodel;


import android.support.annotation.NonNull;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.repository.province.ProvinceRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.view.adapters.items.baseitem.RecyclerViewItem;
import com.infinitum.bookingqba.view.adapters.items.home.HeaderItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentNewItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentPopItem;
import com.infinitum.bookingqba.view.adapters.items.home.RZoneItem;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_NEW;
import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_POPULAR;

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


    public Flowable<Resource<List<ViewModel>>> getReferencesZone(String province) {
        return referenceZoneRepository.allReferencesZone(province)
                .map(this::transformRZoneEntity)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }


    public Flowable<Resource<List<ViewModel>>> getPopRents(String province) {
        return rentRepository.fivePopRentByProvince(province)
                .map(this::transformPopRentEntity)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<Resource<List<ViewModel>>> getNewRents(String province) {
        return rentRepository.fiveNewRentByProvince(province)
                .map(this::transformNewRentEntity)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<Resource<List<ViewModel>>> getAllItems(String province) {
        return Flowable.combineLatest(getReferencesZone(province), getPopRents(province), getNewRents(province),
                this::getCompositeRecyclerViewModels)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @NonNull
    private List<ViewModel> getCompositeRecyclerViewModels(Resource<List<ViewModel>> listResource, Resource<List<ViewModel>> listResource2, Resource<List<ViewModel>> listResource3) {
        List<ViewModel> allItems = new ArrayList<>();
        if (listResource.data != null && listResource2.data != null && listResource3.data != null) {
            allItems.addAll(listResource.data);
            allItems.add(new HeaderItem(UUID.randomUUID().toString(), "Lo más popular", ORDER_TYPE_POPULAR));
            allItems.addAll(listResource2.data);
            allItems.add(new HeaderItem(UUID.randomUUID().toString(), "Lo más nuevo", ORDER_TYPE_NEW));
            allItems.addAll(listResource3.data);
        }
        return allItems;
    }


    //----------------------------------- TRANSFORM METHOD -------------------------------------//

    private Resource<List<ViewModel>> transformNewRentEntity(Resource<List<RentAndGalery>> listResource) {
        List<ViewModel> compositeList = new ArrayList<>();
        List<RentNewItem> items = new ArrayList<>();
        RentNewItem tempItem;
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentAndGalery entity : listResource.data) {
                String imagePath = entity.getImageAtPos(0);
                tempItem = new RentNewItem(entity.getId(), entity.getName(), imagePath, entity.getIsWished());
                tempItem.setRating(entity.getRating());
                tempItem.setImagePath(imagePath);
                tempItem.setWished(entity.getIsWished());
                items.add(tempItem);
            }
            compositeList.add(new RecyclerViewItem(UUID.randomUUID().hashCode(), items));
            return Resource.success(compositeList);
        } else {
            return Resource.error("Null or empty values");
        }
    }

    private Resource<List<ViewModel>> transformPopRentEntity(Resource<List<RentAndGalery>> listResource) {
        List<ViewModel> compositeList = new ArrayList<>();
        List<RentPopItem> items = new ArrayList<>();
        RentPopItem tempItem;
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentAndGalery entity : listResource.data) {
                String imagePath = entity.getImageAtPos(0);
                tempItem = new RentPopItem(entity.getId(), entity.getName(), imagePath, entity.getIsWished());
                tempItem.setPrice(entity.getPrice());
                tempItem.setWished(entity.getIsWished());
                tempItem.setImagePath(imagePath);
                tempItem.setRating(entity.getRating());
                items.add(tempItem);
            }
            compositeList.add(new RecyclerViewItem(UUID.randomUUID().hashCode(), items));
            return Resource.success(compositeList);
        } else {
            return Resource.error("Null or empty values");
        }
    }

    private Resource<List<ViewModel>> transformRZoneEntity(Resource<List<ReferenceZoneEntity>> listResource) {
        List<ViewModel> compositeList = new ArrayList<>();
        List<RZoneItem> items = new ArrayList<>();
        if (listResource.data != null && listResource.data.size() > 0) {
            for (ReferenceZoneEntity entity : listResource.data) {
                items.add(new RZoneItem(entity.getId(), entity.getName(), entity.getImage()));
            }
            compositeList.add(new RecyclerViewItem(UUID.randomUUID().hashCode(), items));
            return Resource.success(compositeList);
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
