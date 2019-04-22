package com.infinitum.bookingqba.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAmenitieAndRelation;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.local.pojo.RentPoiAndRelation;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableItem;
import com.infinitum.bookingqba.view.adapters.items.filter.ReferenceZoneViewItem;
import com.infinitum.bookingqba.view.adapters.items.filter.AmenitieViewItem;
import com.infinitum.bookingqba.view.adapters.items.filter.StarViewItem;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentDetailAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentDetailGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentDetailItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentDetailPoiItem;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
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

    //----------------------------------- GET METHOD -----------------------------------------//

    /**
     * Lista paginada de rentas segun tipo de orden y provincia
     * @return
     */
    public LiveData<PagedList<RentListItem>> getLiveDataRentList(char orderType, String province){
        DataSource.Factory<Integer,RentListItem> dataSource = rentRepository.allRentByOrderType(orderType,province).mapByPage(input -> {
            List<RentListItem> itemsUi = new ArrayList<>();
            for(RentAndGalery entity: input){
                String imagePath = entity.getGalerieAtPos(0).getImageLocalPath()!=null?entity.getGalerieAtPos(0).getImageLocalPath():entity.getGalerieAtPos(0).getImageUrl();
                itemsUi.add(new RentListItem(entity.getId(),entity.getName(),entity.getPrice(),entity.getAddress(),imagePath,entity.getRating()));
            }
            return itemsUi;
        });
        LivePagedListBuilder<Integer, RentListItem> pagedListBuilder= new LivePagedListBuilder<>(dataSource, 10);
        ldRentsList = pagedListBuilder.build();
        return ldRentsList;
    }


    public Flowable<Resource<List<ListWishItem>>> getRentListWish(String province){
        return rentRepository.allWishedRent(province).map(this::transformToWishList).subscribeOn(Schedulers.io());
    }

    public Flowable<Resource<List<GeoRent>>> getGeoRent(){
        return rentRepository.allRent().map(this::transformToGeoRent);
    }


    // --------------------------- FILTER LIST --------------------------------------------- //

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
                .map(this::transformAmenitieToMap);
        Flowable<List<ViewModel>> zoneFlow = rzoneRepository.allReferencesZone()
                .subscribeOn(Schedulers.io())
                .map(this::transformRZoneToMap);
        Flowable<List<ViewModel>> starFlow = Flowable.just(getStarItem()).subscribeOn(Schedulers.io());
        return Flowable.combineLatest(amenitieFlow, zoneFlow,starFlow, (viewModels, viewModels2, viewModels3) -> {
            filterMap.put("Amenities", viewModels);
            filterMap.put("Zone", viewModels2);
            filterMap.put("Star", viewModels3);
            return filterMap;
        });
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
                .flatMap(this::iterateViewItem)
                .doOnNext(viewModel -> ((CheckableItem)viewModel).setChecked(false))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Observable<ViewModel> iterateViewItem(List<ViewModel> viewModels) {
        return Observable.fromIterable(viewModels);
    }

    // ------------------------ RENT DETAIL --------------------------------------------- //

    public Flowable<Resource<RentDetailItem>> getRentDetailById(String uuid){
        return rentRepository
                .getRentDetailById(uuid)
                .map(this::parseRentDetailPojoToItem)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    private RentDetailItem parseRentDetailPojoToItem(Resource<RentDetail> rentDetail){
        RentDetailItem item = new RentDetailItem(rentDetail.data.getRentEntity());
        item.setAmenitieItems(convertAmenitiesPojoToParcel(rentDetail.data.getAmenitieNames()));
        item.setRentModeName(rentDetail.data.getRentModeNameObject());
        item.setGalerieItems(convertGaleriePojoToParcel(rentDetail.data.getGaleries()));
        item.setPoiItems(convertPoisPojoToParcel(rentDetail.data.getRentPoiAndRelations()));
        return item;
    }

    //-------------------------- RENT VISIT COUNT ---------------------------------------- //

    public Completable addOrUpdateRentVisitCount(String id,String rentId){
        return rentRepository.addOrUpdateRentVisitCount(id,rentId);
    }

    // ------------------------- TRANSFORM METHOD ---------------------------------------- //

    private List<ViewModel> transformAmenitieToMap(Resource<List<AmenitiesEntity>> listResource) {
        ArrayList<ViewModel> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (AmenitiesEntity entity : listResource.data) {
                viewItemList.add(new AmenitieViewItem(entity.getId(), entity.getName(), false));
            }
        }
        return viewItemList;
    }

    private List<ViewModel> transformRZoneToMap(Resource<List<ReferenceZoneEntity>> listResource) {
        ArrayList<ViewModel> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (ReferenceZoneEntity entity : listResource.data) {
                viewItemList.add(new ReferenceZoneViewItem(entity.getId(), entity.getName(), false, entity.getImage()));
            }
        }
        return viewItemList;
    }

    private ArrayList<RentDetailGalerieItem> convertGaleriePojoToParcel(List<GalerieEntity> galerieEntities) {
        ArrayList<RentDetailGalerieItem> detailGalerieItems = new ArrayList<>();
        for (GalerieEntity item : galerieEntities) {
            String imagePath = item.getImageLocalPath()!=null?item.getImageLocalPath():item.getImageUrl();
            detailGalerieItems.add(new RentDetailGalerieItem(imagePath));
        }
        return detailGalerieItems;
    }

    private ArrayList<RentDetailPoiItem> convertPoisPojoToParcel(List<RentPoiAndRelation> rentPoiAndRelations) {
        ArrayList<RentDetailPoiItem> detailPoiItems = new ArrayList<>();
        for (RentPoiAndRelation item : rentPoiAndRelations) {
            PoiEntity poiEntity = item.getPoiAndRelationsObject().getPoiEntity();
            PoiTypeEntity poiTypeEntity = item.getPoiAndRelationsObject().getPoiTypeEntitySetObject();
            detailPoiItems.add(new RentDetailPoiItem(poiEntity.getName(), poiTypeEntity.getImage()));
        }
        return detailPoiItems;
    }

    private ArrayList<RentDetailAmenitieItem> convertAmenitiesPojoToParcel(List<RentAmenitieAndRelation> amenitieNameArrayList) {
        ArrayList<RentDetailAmenitieItem> detailAmenitieItems = new ArrayList<>();
        for (RentAmenitieAndRelation item : amenitieNameArrayList) {
            detailAmenitieItems.add(new RentDetailAmenitieItem(item.getAmenitieNameobject()));
        }
        return detailAmenitieItems;
    }

    private Resource<List<GeoRent>> transformToGeoRent(Resource<List<RentAndGalery>> listResource) {
        List<GeoRent> geoRentList = new ArrayList<>();
        GeoRent geoRent;
        if(listResource.data!=null && listResource.data.size()>0) {
            for (RentAndGalery rentAndGalery : listResource.data) {
                geoRent = new GeoRent(rentAndGalery.getId());
                geoRent.setGeoPoint(new GeoPoint(rentAndGalery.getLatitude(), rentAndGalery.getLongitude()));
                geoRent.setName(rentAndGalery.getName());
                geoRent.setPrice(rentAndGalery.getPrice());
                geoRent.setRating(rentAndGalery.getRating());
                String imagePath = rentAndGalery.getGalerieAtPos(0).getImageLocalPath() != null ? rentAndGalery.getGalerieAtPos(0).getImageLocalPath() : rentAndGalery.getGalerieAtPos(0).getImageUrl();
                geoRent.setImagePath(imagePath);
                geoRent.setRentMode(rentAndGalery.getRentMode());
                geoRentList.add(geoRent);
            }
            return Resource.success(geoRentList);
        }else{
            return Resource.error("Null or empty values");
        }
    }

    private Resource<List<ListWishItem>> transformToWishList(Resource<List<RentAndGalery>> listResource) {
        List<ListWishItem> listWishItems = new ArrayList<>();
        ListWishItem item;
        if(listResource.data!=null && listResource.data.size()>0) {
            for (RentAndGalery rentAndGalery : listResource.data) {
                item = new ListWishItem(rentAndGalery.getId(),rentAndGalery.getName());
                item.setAddress(rentAndGalery.getAddress());
                item.setPrice(rentAndGalery.getPrice());
                item.setRating(rentAndGalery.getRating());
                item.setRentMode(rentAndGalery.getRentMode());
                String imagePath = rentAndGalery.getGalerieAtPos(0).getImageLocalPath() != null ? rentAndGalery.getGalerieAtPos(0).getImageLocalPath() : rentAndGalery.getGalerieAtPos(0).getImageUrl();
                item.setImagePath(imagePath);
            }
            return Resource.success(listWishItems);
        }else{
            return Resource.error("Null or empty values");
        }
    }
}
