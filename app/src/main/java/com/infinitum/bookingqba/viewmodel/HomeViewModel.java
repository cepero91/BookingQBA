package com.infinitum.bookingqba.viewmodel;


import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.repository.province.ProvinceRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.view.adapters.baseitem.RecyclerViewItem;
import com.infinitum.bookingqba.view.adapters.home.HeaderItem;
import com.infinitum.bookingqba.view.adapters.home.RentPopItem;
import com.infinitum.bookingqba.view.adapters.home.RZoneItem;

import java.util.ArrayList;
import java.util.List;

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

    public Flowable<Resource<List<ViewModel>>> getReferencesZone() {
        return referenceZoneRepository.allReferencesZone()
                .flatMap(this::transformRZoneEntity).subscribeOn(Schedulers.io());
    }

    private Flowable<Resource<List<ViewModel>>> transformRZoneEntity(Resource<List<ReferenceZoneEntity>> listResource) {
        List<ViewModel> compositeList = new ArrayList<>();
        List<RZoneItem> items = new ArrayList<>();
        if (listResource.data != null && listResource.data.size() > 0) {
            for(ReferenceZoneEntity entity: listResource.data){
                items.add(new RZoneItem(entity.getId(),entity.getName(),entity.getImage()));
            }
            compositeList.add(new RecyclerViewItem(1,items));
        }
        return Flowable.just(compositeList)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<Resource<List<ViewModel>>> getRents() {
        return rentRepository.allRent()
                .flatMap(this::transformRentEntity)
                .subscribeOn(Schedulers.io());
    }

    private Flowable<Resource<List<ViewModel>>> transformRentEntity(Resource<List<RentAndGalery>> listResource) {
        List<com.github.vivchar.rendererrecyclerviewadapter.ViewModel> compositeList = new ArrayList<>();
        List<RentPopItem> items = new ArrayList<>();
        if (listResource.data != null && listResource.data.size() > 0) {
            for(RentAndGalery entity: listResource.data){
                items.add(new RentPopItem(entity.getId(),entity.getName(),entity.getGaleries().get(0).getImageByte()));
            }
            compositeList.add(new RecyclerViewItem(3,items));
        }
        return Flowable.just(compositeList)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<Resource<List<ViewModel>>> getAllItems(){
        return Flowable.combineLatest(getReferencesZone(), getRents(), (listResource, listResource2) -> {
            List<com.github.vivchar.rendererrecyclerviewadapter.ViewModel> allItems = new ArrayList<>();
            if(listResource.data!=null && listResource2.data!=null) {
                allItems.addAll(listResource.data);
                allItems.add(new HeaderItem("2","Lo mas popular"));
                allItems.addAll(listResource2.data);
            }
            return Resource.success(allItems);
        }).subscribeOn(Schedulers.io());
    }







}
