package com.infinitum.bookingqba.viewmodel;


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
import com.infinitum.bookingqba.view.adapters.items.spinneritem.ProvinceSpinnerList;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.SpinnerProvinceItem;

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

    public Flowable<Resource<ProvinceSpinnerList>> getProvinces() {
        return provinceRepository
                .getAllProvinces()
                .flatMap(resource -> transformProvinces(resource, new ArrayList<>()))
                .subscribeOn(Schedulers.io());
    }


    public Flowable<Resource<List<ViewModel>>> getReferencesZone() {
        return referenceZoneRepository.allReferencesZone()
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
        return Flowable.combineLatest(getReferencesZone(), getPopRents(province), getNewRents(province), (listResource, listResource2, listResource3) -> {
            List<com.github.vivchar.rendererrecyclerviewadapter.ViewModel> allItems = new ArrayList<>();
            if (listResource.data != null && listResource2.data != null) {
                allItems.addAll(listResource.data);
                allItems.add(new HeaderItem(UUID.randomUUID().toString(), "Lo más popular", ORDER_TYPE_POPULAR));
                allItems.addAll(listResource2.data);
                allItems.add(new HeaderItem(UUID.randomUUID().toString(), "Lo más nuevo", ORDER_TYPE_NEW));
                allItems.addAll(listResource3.data);
            }
            return Resource.success(allItems);
        }).onErrorReturn(Resource::error).subscribeOn(Schedulers.io());
    }


    //----------------------------------- TRANSFORM METHOD -------------------------------------//

    private Resource<List<ViewModel>> transformNewRentEntity(Resource<List<RentAndGalery>> listResource) {
        List<ViewModel> compositeList = new ArrayList<>();
        List<RentNewItem> items = new ArrayList<>();
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentAndGalery entity : listResource.data) {
                String imagePath = entity.getGalerieAtPos(0).getImageLocalPath() != null ? entity.getGalerieAtPos(0).getImageLocalPath() : entity.getGalerieAtPos(0).getImageUrl();
                items.add(new RentNewItem(entity.getId(), entity.getName(), imagePath, entity.getRating()));
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
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentAndGalery entity : listResource.data) {
                String imagePath = entity.getGalerieAtPos(0).getImageLocalPath() != null ? entity.getGalerieAtPos(0).getImageLocalPath() : entity.getGalerieAtPos(0).getImageUrl();
                items.add(new RentPopItem(entity.getId(), entity.getName(), imagePath, entity.getRating(), entity.getPrice()));
            }
            compositeList.add(new RecyclerViewItem(UUID.randomUUID().hashCode(), items));
            return Resource.success(compositeList);
        }else{
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

    private Flowable<Resource<ProvinceSpinnerList>> transformProvinces(Resource<List<ProvinceEntity>> listResource, List<SpinnerProvinceItem> listOutput) {
        return Flowable.fromIterable(listResource.data)
                .map(entity -> new SpinnerProvinceItem(entity.getId(), entity.getName()))
                .doOnNext(listOutput::add)
                .toList()
                .toFlowable()
                .map(ProvinceSpinnerList::new)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }


}
