package com.infinitum.bookingqba.viewmodel;


import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.view.adapters.rendered.filter.CheckableItem;
import com.infinitum.bookingqba.view.adapters.rendered.filter.ReferenceZoneViewItem;
import com.infinitum.bookingqba.view.adapters.rendered.filter.AmenitieViewItem;
import com.infinitum.bookingqba.view.adapters.rendered.filter.StarViewItem;
import com.infinitum.bookingqba.view.adapters.rent.RentListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RentViewModel extends android.arch.lifecycle.ViewModel {

    private static final int MAX_STAR = 5;

    private AmenitiesRepository amenitiesRepository;
    private RentRepository rentRepository;
    private ReferenceZoneRepository rzoneRepository;
    private Map<String, List<ViewModel>> filterMap;
    private LiveData<PagedList<RentListItem>> ldRentsList;

    @Inject
    public RentViewModel(AmenitiesRepository amenitiesRepository, RentRepository rentRepository, ReferenceZoneRepository rzoneRepository) {
        this.amenitiesRepository = amenitiesRepository;
        this.rentRepository = rentRepository;
        this.rzoneRepository = rzoneRepository;
        filterMap = new HashMap<>();
    }

    public LiveData<PagedList<RentListItem>> getLdRentsList(){
        DataSource.Factory<Integer,RentListItem> dataSource = rentRepository.getRentPaged().mapByPage(new Function<List<RentAndGalery>, List<RentListItem>>() {
            @Override
            public List<RentListItem> apply(List<RentAndGalery> input) {
                List<RentListItem> itemsUi = new ArrayList<>();
                for(RentAndGalery entity: input){
                    itemsUi.add(new RentListItem(entity.getId(),entity.getName(),entity.getPrice(),entity.getAddress(),entity.getGaleries().get(0).getImageByte(),entity.getRating()));
                }
                return itemsUi;
            }
        });
        LivePagedListBuilder<Integer, RentListItem> pagedListBuilder= new LivePagedListBuilder<>(dataSource, 10);
        ldRentsList = pagedListBuilder.build();
        return ldRentsList;
    }

    public Flowable<Map<String, List<ViewModel>>> getMapFilterItems() {
        if(filterMap!=null && filterMap.size()>0){
            return Flowable.just(filterMap);
        }else{
            return getMapFlowable();
        }
    }

    private Flowable<Map<String, List<ViewModel>>> getMapFlowable() {
        Flowable<List<ViewModel>> amenitieFlow = amenitiesRepository.fetchLocal()
                .subscribeOn(Schedulers.io())
                .flatMap(this::transformAmenitieToMap);
        Flowable<List<ViewModel>> zoneFlow = rzoneRepository.allReferencesZone()
                .subscribeOn(Schedulers.io())
                .flatMap(this::transformRZoneToMap);
        Flowable<List<ViewModel>> starFlow = Flowable.just(getStarItem()).subscribeOn(Schedulers.io());
        return Flowable.combineLatest(amenitieFlow, zoneFlow,starFlow, (viewModels, viewModels2, viewModels3) -> {
            filterMap.put("Amenities", viewModels);
            filterMap.put("Zone", viewModels2);
            filterMap.put("Star", viewModels3);
            return filterMap;
        });
    }

    private Flowable<List<ViewModel>> transformAmenitieToMap(Resource<List<AmenitiesEntity>> listResource) {
        ArrayList<ViewModel> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (AmenitiesEntity entity : listResource.data) {
                viewItemList.add(new AmenitieViewItem(entity.getId(), entity.getName(), false));
            }
        }
        return Flowable.just(viewItemList);
    }

    private Flowable<List<ViewModel>> transformRZoneToMap(Resource<List<ReferenceZoneEntity>> listResource) {
        ArrayList<ViewModel> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (ReferenceZoneEntity entity : listResource.data) {
                viewItemList.add(new ReferenceZoneViewItem(entity.getId(), entity.getName(), false, entity.getImage()));
            }
        }
        return Flowable.just(viewItemList);
    }

    public Flowable<Resource<List<RentListItem>>> getRents(char orderType) {
        return rentRepository.allRentWithFirstImage(orderType)
                .flatMap(this::transformRentEntity)
                .subscribeOn(Schedulers.io());
    }


    private Flowable<Resource<List<RentListItem>>> transformRentEntity(Resource<List<RentAndGalery>> listResource) {
        List<RentListItem> items = new ArrayList<>();
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentAndGalery entity : listResource.data) {
                items.add(new RentListItem(entity.getId(), entity.getName(), entity.getPrice(), entity.getAddress(), entity.getGaleries().get(0).getImageByte(),entity.getRating()));
            }
        }
        return Flowable.just(items)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    private List<ViewModel> getStarItem(){
        List<ViewModel> starViewItemList = new ArrayList<>();
        for(int i=1; i <= MAX_STAR; i++){
            starViewItemList.add(new StarViewItem(UUID.randomUUID().toString(),String.valueOf(i),false));
        }
        return starViewItemList;
    }


    public void changeStateFilterItem(String key, String id) {
        if (filterMap.containsKey(key)) {
            Flowable.fromIterable(filterMap.get(key))
                    .filter(viewModel -> ((CheckableItem) viewModel).getId().equals(id))
                    .doOnNext(viewModel -> ((CheckableItem) viewModel).setChecked(!((CheckableItem) viewModel).isChecked()))
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    public void resetAllFilterItem() {
        Observable.fromIterable(filterMap.values())
                .flatMap(this::interateViewItem)
                .doOnNext(viewModel -> ((CheckableItem)viewModel).setChecked(false))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Observable<ViewModel> interateViewItem(List<ViewModel> viewModels) {
        return Observable.fromIterable(viewModels);
    }


}
