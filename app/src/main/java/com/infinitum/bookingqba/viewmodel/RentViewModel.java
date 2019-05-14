package com.infinitum.bookingqba.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.CommentEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAmenitieAndRelation;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.local.pojo.RentPoiAndRelation;
import com.infinitum.bookingqba.model.local.tconverter.CommentEmotion;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.comment.CommentRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableItem;
import com.infinitum.bookingqba.view.adapters.items.filter.ReferenceZoneViewItem;
import com.infinitum.bookingqba.view.adapters.items.filter.AmenitieViewItem;
import com.infinitum.bookingqba.view.adapters.items.filter.RentModeViewItem;
import com.infinitum.bookingqba.view.adapters.items.filter.StarViewItem;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;
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
import io.reactivex.schedulers.Schedulers;

public class RentViewModel extends android.arch.lifecycle.ViewModel {

    private static final int MAX_STAR = 5;

    private AmenitiesRepository amenitiesRepository;
    private RentRepository rentRepository;
    private ReferenceZoneRepository rzoneRepository;
    private MunicipalityRepository municipalityRepository;
    private CommentRepository commentRepository;
    private Map<String, List<CheckableItem>> filterMap;
    private LiveData<PagedList<RentListItem>> ldRentsList;

    @Inject
    public RentViewModel(AmenitiesRepository amenitiesRepository, RentRepository rentRepository
            , ReferenceZoneRepository rzoneRepository, CommentRepository commentRepository
            , MunicipalityRepository municipalityRepository) {
        this.amenitiesRepository = amenitiesRepository;
        this.rentRepository = rentRepository;
        this.rzoneRepository = rzoneRepository;
        this.commentRepository = commentRepository;
        this.municipalityRepository = municipalityRepository;
        filterMap = new HashMap<>();
    }

    //----------------------------------- GET METHOD -----------------------------------------//

    /**
     * Lista paginada de rentas segun tipo de orden y provincia
     *
     * @return
     */
    public LiveData<PagedList<RentListItem>> getLiveDataRentList(char orderType, String province) {
        DataSource.Factory<Integer, RentListItem> dataSource = rentRepository.allRentByOrderType(orderType, province).mapByPage(input -> {
            List<RentListItem> itemsUi = new ArrayList<>();
            RentListItem tempItem;
            for (RentAndGalery entity : input) {
                String imagePath = entity.getImageAtPos(0);
                tempItem = new RentListItem(entity.getId(), entity.getName(), imagePath, entity.getIsWished());
                tempItem.setRating(entity.getRating());
                tempItem.setAddress(entity.getAddress());
                tempItem.setRentMode(entity.getRentMode());
                tempItem.setPrice(entity.getPrice());
                itemsUi.add(tempItem);
            }
            return itemsUi;
        });
        LivePagedListBuilder<Integer, RentListItem> pagedListBuilder = new LivePagedListBuilder<>(dataSource, 10);
        ldRentsList = pagedListBuilder.build();
        return ldRentsList;
    }


    public Flowable<Resource<List<ListWishItem>>> getRentListWish(String province) {
        return rentRepository.allWishedRent(province).map(this::transformToWishList).subscribeOn(Schedulers.io());
    }

    public Flowable<Resource<List<GeoRent>>> getGeoRent() {
        return rentRepository.allRent().map(this::transformToGeoRent);
    }

    // --------------------------- RENT WISHED ---------------------------------------------//

    public Completable updateRentIsWished(String uuid, int isWished) {
        return rentRepository.updateIsWishedRent(uuid, isWished);
    }

    public Completable updateRent(RentEntity entity) {
        return rentRepository.update(entity);
    }

    // --------------------------- FILTER LIST --------------------------------------------- //

    public Flowable<Map<String, List<CheckableItem>>> getMapFilterItems() {
        if (filterMap != null && filterMap.size() > 0) {
            return Flowable.just(filterMap);
        } else {
            return getMapFlowable();
        }
    }

    private Flowable<Map<String, List<CheckableItem>>> getMapFlowable() {
        Flowable<List<CheckableItem>> amenitieFlow = amenitiesRepository.allAmenities()
                .subscribeOn(Schedulers.io())
                .map(this::transformAmenitieToMap);
        Flowable<List<CheckableItem>> rentModeFlow = rentRepository.allRentMode()
                .subscribeOn(Schedulers.io())
                .map(this::transformRentModeToMap);
        Flowable<List<CheckableItem>> municipalityFlow = municipalityRepository.allMunicipalities()
                .subscribeOn(Schedulers.io())
                .map(this::transformMunToMap);
        return Flowable.combineLatest(amenitieFlow, rentModeFlow, municipalityFlow ,(amenities, rentmode, municipalities) -> {
            filterMap.put("Amenities", amenities);
            filterMap.put("RentMode", rentmode);
            filterMap.put("Municipality", municipalities);
            return filterMap;
        }).subscribeOn(Schedulers.io());
    }

//    public Flowable<Resource<List<RentAndGalery>>> filter(Map<String,List<String>> filterParams){
//        return Flowable.empty().subscribeOn(Schedulers.io());
//    }

    private Observable<ViewModel> iterateViewItem(List<CheckableItem> viewModels) {
        return Observable.fromIterable(viewModels);
    }

    // ------------------------ RENT DETAIL --------------------------------------------- //

    public Flowable<Resource<RentItem>> getRentDetailById(String uuid) {
        return rentRepository
                .getRentDetailById(uuid)
                .map(this::parseRentDetailPojoToItem)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    private RentItem parseRentDetailPojoToItem(Resource<RentDetail> rentDetail) {
        RentItem item = new RentItem(rentDetail.data.getRentEntity());
        item.setAmenitieItems(convertAmenitiesPojoToParcel(rentDetail.data.getAmenitieNames()));
        item.setRentModeName(rentDetail.data.getRentModeNameObject());
        item.setGalerieItems(convertGaleriePojoToParcel(rentDetail.data.getGaleries()));
        item.setCommentItems(convertCommentPojoToParcel(rentDetail.data.getCommentEntities()));
        item.setPoiItems(convertPoisPojoToParcel(rentDetail.data.getRentPoiAndRelations()));
        return item;
    }

    /**
     * TODO TODABIA NO HAY USERID CUANDO SE EFECTUA EL LOGIN
     *
     * @param comment
     * @return
     */
    public Completable addComment(Comment comment) {
        CommentEntity entity = new CommentEntity(comment.getId(), comment.getUsername(), comment.getDescription(), comment.getRent(), comment.getUserid());
        entity.setEmotion(CommentEmotion.fromLevel(comment.getEmotion()));
        entity.setActive(comment.isActive());
        entity.setOwner(comment.isIs_owner());
        entity.setAvatar(null);
        entity.setCreated(DateUtils.dateStringToDate(comment.getCreated()));
        List<CommentEntity> list = new ArrayList<>();
        list.add(entity);
//        return commentRepository.insert(list);
        return Completable.complete();
    }

    public Completable addRating(float rating, String comment, String rent) {
        RatingEntity entity = new RatingEntity(UUID.randomUUID().toString(), rating, comment, rent);
        return rentRepository.addOrUpdateRating(entity);
//        return Completable.complete();
    }

    //-------------------------- RENT VISIT COUNT ---------------------------------------- //

    public Completable addOrUpdateRentVisitCount(String id, String rentId) {
        return rentRepository.addOrUpdateRentVisitCount(id, rentId);
    }

    // ------------------------- TRANSFORM METHOD ---------------------------------------- //

    private List<CheckableItem> transformAmenitieToMap(Resource<List<AmenitiesEntity>> listResource) {
        ArrayList<CheckableItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (AmenitiesEntity entity : listResource.data) {
                viewItemList.add(new CheckableItem(entity.getId(), entity.getName(), false));
            }
        }
        return viewItemList;
    }

    private List<CheckableItem> transformRZoneToMap(Resource<List<ReferenceZoneEntity>> listResource) {
        ArrayList<CheckableItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (ReferenceZoneEntity entity : listResource.data) {
                viewItemList.add(new CheckableItem(entity.getId(), entity.getName(), false));
            }
        }
        return viewItemList;
    }

    private List<CheckableItem> transformRentModeToMap(Resource<List<RentModeEntity>> listResource) {
        ArrayList<CheckableItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (RentModeEntity entity : listResource.data) {
                viewItemList.add(new CheckableItem(entity.getId(), entity.getName(), false));
            }
        }
        return viewItemList;
    }

    private List<CheckableItem> transformMunToMap(Resource<List<MunicipalityEntity>> listResource) {
        ArrayList<CheckableItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (MunicipalityEntity entity : listResource.data) {
                viewItemList.add(new CheckableItem(entity.getId(), entity.getName(), false));
            }
        }
        return viewItemList;
    }

    private ArrayList<RentGalerieItem> convertGaleriePojoToParcel(List<GalerieEntity> galerieEntities) {
        ArrayList<RentGalerieItem> detailGalerieItems = new ArrayList<>();
        for (GalerieEntity item : galerieEntities) {
            String imagePath = item.getImageLocalPath() != null ? item.getImageLocalPath() : item.getImageUrl();
            detailGalerieItems.add(new RentGalerieItem(imagePath));
        }
        return detailGalerieItems;
    }

    private ArrayList<RentCommentItem> convertCommentPojoToParcel(List<CommentEntity> commentEntities) {
        ArrayList<RentCommentItem> detailCommentItems = new ArrayList<>();
        for (CommentEntity item : commentEntities) {
            detailCommentItems.add(new RentCommentItem(item.getId(), item.getUsername(), item.getDescription(), item.getAvatar(), item.isOwner(), item.getCreated()));
        }
        return detailCommentItems;
    }

    private ArrayList<RentPoiItem> convertPoisPojoToParcel(List<RentPoiAndRelation> rentPoiAndRelations) {
        ArrayList<RentPoiItem> detailPoiItems = new ArrayList<>();
        for (RentPoiAndRelation item : rentPoiAndRelations) {
            PoiEntity poiEntity = item.getPoiAndRelationsObject().getPoiEntity();
            PoiTypeEntity poiTypeEntity = item.getPoiAndRelationsObject().getPoiTypeEntitySetObject();
            detailPoiItems.add(new RentPoiItem(poiEntity.getName(), poiTypeEntity.getImage()));
        }
        return detailPoiItems;
    }

    private ArrayList<RentAmenitieItem> convertAmenitiesPojoToParcel(List<RentAmenitieAndRelation> amenitieNameArrayList) {
        ArrayList<RentAmenitieItem> detailAmenitieItems = new ArrayList<>();
        for (RentAmenitieAndRelation item : amenitieNameArrayList) {
            detailAmenitieItems.add(new RentAmenitieItem(item.getAmenitieNameobject()));
        }
        return detailAmenitieItems;
    }

    private Resource<List<GeoRent>> transformToGeoRent(Resource<List<RentAndGalery>> listResource) {
        List<GeoRent> geoRentList = new ArrayList<>();
        GeoRent geoRent;
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentAndGalery rentAndGalery : listResource.data) {
                String imagePath = rentAndGalery.getImageAtPos(0);
                geoRent = new GeoRent(rentAndGalery.getId(), rentAndGalery.getName(), imagePath, rentAndGalery.getIsWished());
                geoRent.setGeoPoint(new GeoPoint(rentAndGalery.getLatitude(), rentAndGalery.getLongitude()));
                geoRent.setPrice(rentAndGalery.getPrice());
                geoRent.setRating(rentAndGalery.getRating());
                geoRent.setRentMode(rentAndGalery.getRentMode());
                geoRentList.add(geoRent);
            }
            return Resource.success(geoRentList);
        } else {
            return Resource.error("Null or empty values");
        }
    }

    private Resource<List<ListWishItem>> transformToWishList(Resource<List<RentAndGalery>> listResource) {
        List<ListWishItem> listWishItems = new ArrayList<>();
        ListWishItem item;
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentAndGalery rentAndGalery : listResource.data) {
                String imagePath = rentAndGalery.getImageAtPos(0);
                item = new ListWishItem(rentAndGalery.getId(), rentAndGalery.getName(), imagePath, rentAndGalery.getIsWished());
                item.setAddress(rentAndGalery.getAddress());
                item.setPrice(rentAndGalery.getPrice());
                item.setRating(rentAndGalery.getRating());
                item.setRentMode(rentAndGalery.getRentMode());
                listWishItems.add(item);
            }
            return Resource.success(listWishItems);
        } else {
            return Resource.error("Null or empty values");
        }
    }
}
