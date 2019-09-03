package com.infinitum.bookingqba.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.CommentEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.OfferEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.pojo.PoiAndRelations;
import com.infinitum.bookingqba.model.local.pojo.RentAmenitieAndRelation;
import com.infinitum.bookingqba.model.local.pojo.RentAndDependencies;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.local.pojo.RentPoiAndRelation;
import com.infinitum.bookingqba.model.local.tconverter.CommentEmotion;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.DisabledDays;
import com.infinitum.bookingqba.model.remote.pojo.DrawChange;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.comment.CommentRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.poitype.PoiTypeRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableItem;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentInnerDetail;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentOfferItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;

import org.mapsforge.core.model.LatLong;
import org.oscim.core.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RentViewModel extends android.arch.lifecycle.ViewModel {

    private static final int MAX_STAR = 5;

    private AmenitiesRepository amenitiesRepository;
    private RentRepository rentRepository;
    private ReferenceZoneRepository rzoneRepository;
    private MunicipalityRepository municipalityRepository;
    private CommentRepository commentRepository;
    private PoiTypeRepository poiTypeRepository;
    private Map<String, List<CheckableItem>> filterMap;
    private LiveData<PagedList<GeoRent>> ldRentsList;

    @Inject
    public RentViewModel(AmenitiesRepository amenitiesRepository, RentRepository rentRepository
            , ReferenceZoneRepository rzoneRepository, CommentRepository commentRepository
            , MunicipalityRepository municipalityRepository, PoiTypeRepository poiTypeRepository) {
        this.amenitiesRepository = amenitiesRepository;
        this.rentRepository = rentRepository;
        this.rzoneRepository = rzoneRepository;
        this.commentRepository = commentRepository;
        this.municipalityRepository = municipalityRepository;
        this.poiTypeRepository = poiTypeRepository;
        filterMap = new HashMap<>();
    }

    //----------------------------------- GET METHOD -----------------------------------------//

    /**
     * Lista paginada de rentas segun tipo de orden y provincia
     *
     * @return
     */
    public Flowable<Resource<List<GeoRent>>> getLiveDataRentList(char orderType, String province) {
        return rentRepository.allRentByOrderType(orderType, province).map(this::transformPaginadedData).map(Resource::success).onErrorReturn(Resource::error);
    }


    public Flowable<Resource<List<ListWishItem>>> getRentListWish(String province) {
        return rentRepository.allWishedRent(province)
                .subscribeOn(Schedulers.io())
                .map(this::transformToWishList)
                .onErrorReturn(Resource::error);
    }

    public Flowable<Resource<List<GeoRent>>> getGeoRent() {
        return rentRepository
                .allRent()
                .map(this::transformToGeoRent);
    }

    public Flowable<Resource<List<GeoRent>>> getGeoRentNearLatLon(LatLong latLong, double range) {
        return rentRepository
                .rentNearLocation(latLong, range)
                .map(this::transformToGeoRent);
    }

    // --------------------------- RENT WISHED ---------------------------------------------//

    public Completable updateRentIsWished(String uuid, int isWished) {
        return rentRepository.updateIsWishedRent(uuid, isWished);
    }

    public Completable updateRent(String uuid, int wished) {
        return rentRepository.updateIsWishedRent(uuid, wished);
    }

    // --------------------------- FILTER LIST --------------------------------------------- //

    public Flowable<Map<String, List<CheckableItem>>> getMapFilterItems(String province) {
        if (filterMap != null && filterMap.size() > 0) {
            return Flowable.just(filterMap);
        } else {
            return getMapFlowable(province);
        }
    }

    public void changeStateFilterItem(int pos, String levelParam) {
        CheckableItem item = filterMap.get(levelParam).get(pos);
        boolean currentState = item.isChecked();
        item.setChecked(!currentState);
    }

    private Flowable<Map<String, List<CheckableItem>>> getMapFlowable(String province) {
        Flowable<List<CheckableItem>> amenitieFlow = amenitiesRepository.allLocalAmenities()
                .subscribeOn(Schedulers.io())
                .map(this::transformAmenitieToMap);
        Flowable<List<CheckableItem>> rentModeFlow = rentRepository.allRentMode()
                .subscribeOn(Schedulers.io())
                .map(this::transformRentModeToMap);
        Flowable<List<CheckableItem>> municipalityFlow = municipalityRepository.allMunicipalitiesByProvince(province)
                .subscribeOn(Schedulers.io())
                .map(this::transformMunToMap);
        Flowable<List<CheckableItem>> poiTypeFlow = poiTypeRepository.allLocalPoiType()
                .subscribeOn(Schedulers.io())
                .map(this::transformPoiTypeToMap);
        Flowable<List<CheckableItem>> maxPrice = rentRepository.maxRentPrice()
                .subscribeOn(Schedulers.io())
                .map(aDouble -> {
                    List<CheckableItem> checkableItems = new ArrayList<>();
                    checkableItems.add(new CheckableItem(UUID.randomUUID().toString(), String.valueOf(aDouble), false));
                    return checkableItems;
                });
        return Flowable.zip(amenitieFlow, rentModeFlow, municipalityFlow, poiTypeFlow, maxPrice,
                (amenities, rentmode, municipalities, poitypes, max) -> {
                    filterMap.put("Amenities", amenities);
                    filterMap.put("RentMode", rentmode);
                    filterMap.put("Municipality", municipalities);
                    filterMap.put("PoiType", poitypes);
                    filterMap.put("Price", max);
                    return filterMap;
                }).subscribeOn(Schedulers.io());
    }


    public Flowable<Resource<List<GeoRent>>> filter(Map<String, List<String>> filterParams, String province) {
        return rentRepository.filterRents(filterParams, province)
                .map(this::transformPaginadedData).map(Resource::success).onErrorReturn(Resource::error);
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
        RentItem rentItem = new RentItem();
        RentInnerDetail innerDetail = new RentInnerDetail();
        RentEntity rentEntity = rentDetail.data.getRentEntity();
        innerDetail.setId(rentEntity.getId());
        innerDetail.setName(rentEntity.getName());
        innerDetail.setAddress(rentEntity.getAddress());
        innerDetail.setPersonalPhone(rentEntity.getPhoneNumber());
        innerDetail.setHomePhone(rentEntity.getPhoneHomeNumber());
        innerDetail.setEmail(rentEntity.getEmail());
        innerDetail.setDescription(rentEntity.getDescription());
        innerDetail.setPrice(rentEntity.getPrice());
        innerDetail.setRating(rentEntity.getRating());
        innerDetail.setVotes(rentEntity.getRatingCount());
        innerDetail.setRules(rentEntity.getRules());
        innerDetail.setRentMode(rentDetail.data.getRentModeNameObject());
        innerDetail.setMaxBeds(rentEntity.getMaxBeds());
        innerDetail.setMaxBaths(rentEntity.getMaxBath());
        innerDetail.setCapability(rentEntity.getCapability());
        innerDetail.setMaxRooms(rentEntity.getMaxRooms());
        innerDetail.setLatitude(rentEntity.getLatitude());
        innerDetail.setLongitude(rentEntity.getLongitude());
        innerDetail.setWished(rentEntity.getIsWished());
        innerDetail.setAmenitieItems(convertAmenitiesPojoToParcel(rentDetail.data.getAmenitieNames()));
        innerDetail.setRentPoiItems(convertPoisPojoToParcel(rentDetail.data.getRentPoiAndRelations()));
        innerDetail.setGalerieItems(convertGaleriePojoToParcel(rentDetail.data.getGaleries()));
        rentItem.setRentInnerDetail(innerDetail);
        rentItem.setCommentItems(convertCommentPojoToParcel(rentDetail.data.getCommentEntities()));
        rentItem.setOfferItems(convertOfferPojoToParcel(rentDetail.data.getOfferEntities()));
        return rentItem;
    }


    public Completable addComment(Comment comment) {
        CommentEntity entity = new CommentEntity(comment.getId(), comment.getUsername(), comment.getDescription(), comment.getRent(), comment.getUserid());
        entity.setEmotion(CommentEmotion.fromLevel(comment.getEmotion()));
        entity.setActive(comment.isActive());
        entity.setOwner(comment.isOwner());
        entity.setAvatar(null);
        entity.setCreated(DateUtils.dateStringToDate(comment.getCreated()));
        List<CommentEntity> list = new ArrayList<>();
        list.add(entity);
        return commentRepository.insert(list);
    }

    public Single<Resource<ResponseResult>> sendComment(String token, Comment comment) {
        return commentRepository.send(token, comment);
    }

    public Completable addRating(float rating, String comment, String rent) {
        RatingEntity entity = new RatingEntity(UUID.randomUUID().toString(), rating, comment, rent, 0);
        return rentRepository.addOrUpdateRating(entity);
    }

    public Single<Pair<Float, String>> getLastRentVote(String rent) {
        return rentRepository.getLastRentVote(rent)
                .subscribeOn(Schedulers.io())
                .map(entity -> {
                    if (entity != null) {
                        return new Pair<>(entity.getRating(), entity.getComment());
                    } else {
                        return new Pair<>(0f, "");
                    }
                }).onErrorReturn(throwable -> {
                    Timber.e(throwable);
                    return new Pair<>(0f, "");
                });
    }

    //-------------------------- RENT RESERVATION ---------------------------------------//

    public Single<Resource<ResponseResult>> sendBookRequest(String token, BookRequest bookRequest) {
        return rentRepository.sendBookRequest(token, bookRequest);
    }

    public Single<Resource<Map<String, Double>>> drawChangeByFinalPrice(String token, double finalPrice) {
        return rentRepository.drawChangeByFinalPrice(token, finalPrice)
                .map(this::getStringDoubleMap).map(Resource::success).onErrorReturn(Resource::error).subscribeOn(Schedulers.io());
    }

    @NonNull
    private Map<String, Double> getStringDoubleMap(Resource<List<DrawChange>> listResource) {
        Map<String, Double> hashMap = new HashMap<>();
        if (listResource.data != null) {
            for (DrawChange drawChange : listResource.data) {
                hashMap.put(drawChange.getName(), drawChange.getValue());
            }
            return hashMap;
        } else {
            return hashMap;
        }
    }

    public Single<Resource<DisabledDays>> disabledDays(String token, String rentUuid) {
        return rentRepository.disabledDaysByRent(token, rentUuid);
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

    private List<CheckableItem> transformPoiTypeToMap(Resource<List<PoiTypeEntity>> listResource) {
        ArrayList<CheckableItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (PoiTypeEntity entity : listResource.data) {
                viewItemList.add(new CheckableItem(String.valueOf(entity.getId()), entity.getName(), false));
            }
        }
        return viewItemList;
    }

    private ArrayList<RentGalerieItem> convertGaleriePojoToParcel(List<GalerieEntity> galerieEntities) {
        ArrayList<RentGalerieItem> detailGalerieItems = new ArrayList<>();
        for (GalerieEntity item : galerieEntities) {
            String imagePath = item.getImageLocalPath() != null ? item.getImageLocalPath() : item.getImageUrl();
            detailGalerieItems.add(new RentGalerieItem(item.getId(), imagePath));
        }
        return detailGalerieItems;
    }

    private ArrayList<RentCommentItem> convertCommentPojoToParcel(List<CommentEntity> commentEntities) {
        ArrayList<RentCommentItem> detailCommentItems = new ArrayList<>();
        for (CommentEntity item : commentEntities) {
            detailCommentItems.add(new RentCommentItem(item.getId(), item.getUsername(), item.getDescription(), item.getAvatar(), item.isOwner(), CommentEmotion.fromEmotion(item.getEmotion()), item.getCreated()));
        }
        return detailCommentItems;
    }

    private ArrayList<RentPoiItem> convertPoisPojoToParcel(List<RentPoiAndRelation> rentPoiAndRelations) {
        ArrayList<RentPoiItem> detailPoiItems = new ArrayList<>();
        for (RentPoiAndRelation item : rentPoiAndRelations) {
            PoiEntity poiEntity = item.getPoiAndRelationsObject().getPoiEntity();
            PoiTypeEntity poiTypeEntity = item.getPoiAndRelationsObject().getPoiTypeEntitySetObject();
            detailPoiItems.add(new RentPoiItem(poiEntity.getName()));
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

    private ArrayList<RentOfferItem> convertOfferPojoToParcel(List<OfferEntity> offerEntityList) {
        ArrayList<RentOfferItem> offerItems = new ArrayList<>();
        for (OfferEntity entity : offerEntityList) {
            offerItems.add(new RentOfferItem(entity.getId(), entity.getName(), entity.getDescription(), entity.getPrice()));
        }
        return offerItems;
    }

    private Resource<List<GeoRent>> transformToGeoRent(Resource<List<RentAndDependencies>> listResource) {
        List<GeoRent> geoRentList = new ArrayList<>();
        GeoRent geoRent;
        if (listResource.data != null && listResource.data.size() > 0) {
            for (RentAndDependencies rentAndDependencies : listResource.data) {
                String imagePath = rentAndDependencies.getImageAtPos(0);
                geoRent = new GeoRent(rentAndDependencies.getId(), rentAndDependencies.getName(), imagePath);
                geoRent.setGeoPoint(new GeoPoint(rentAndDependencies.getLatitude(), rentAndDependencies.getLongitude()));
                geoRent.setPrice(rentAndDependencies.getPrice());
                geoRent.setRating(rentAndDependencies.getRating());
                geoRent.setRatingCount(rentAndDependencies.getRatingCount());
                geoRent.setRentMode(rentAndDependencies.getRentMode());
                geoRent.setPoiItems(transformPoiEntityToPoiItem(new LatLong(rentAndDependencies.getLatitude(), rentAndDependencies.getLongitude()), rentAndDependencies.getRentPoiAndRelations()));
                geoRentList.add(geoRent);
            }
            return Resource.success(geoRentList);
        } else {
            return Resource.error("Datos nulos o vacios");
        }
    }

    private List<PoiItem> transformPoiEntityToPoiItem(LatLong latLong, List<RentPoiAndRelation> rentPoiAndRelations) {
        List<PoiItem> poiItems = new ArrayList<>();
        PoiItem poiItem;
        for (RentPoiAndRelation rentPoiAndRelation : rentPoiAndRelations) {
            poiItem = new PoiItem();
            PoiAndRelations poiAndRelations = rentPoiAndRelation.getPoiAndRelationsObject();
            PoiTypeEntity poiTypeEntity = poiAndRelations.getPoiTypeEntitySetObject();
            poiItem.setId(poiAndRelations.poiEntity.getId());
            poiItem.setLatitude(poiAndRelations.poiEntity.getMinLat());
            poiItem.setLongitude(poiAndRelations.poiEntity.getMinLon());
            poiItem.setName(poiAndRelations.poiEntity.getName());
            poiItem.setCategory(poiTypeEntity.getName());
            poiItems.add(poiItem);
        }
        return poiItems;
    }

    private Resource<List<ListWishItem>> transformToWishList(Resource<List<RentAndGalery>> listResource) {
        List<ListWishItem> listWishItems = new ArrayList<>();
        ListWishItem item;
        for (RentAndGalery rentAndGalery : listResource.data) {
            String imagePath = rentAndGalery.getImageAtPos(0);
            item = new ListWishItem(rentAndGalery.getId(), rentAndGalery.getName(), imagePath);
            item.setAddress(rentAndGalery.getAddress());
            item.setPrice(rentAndGalery.getPrice());
            item.setRating(rentAndGalery.getRating());
            item.setRentMode(rentAndGalery.getRentMode());
            listWishItems.add(item);
        }
        return Resource.success(listWishItems);
    }


    private List<GeoRent> transformPaginadedData(Resource<List<RentAndDependencies>> listResource) {
        List<GeoRent> geoRentList = new ArrayList<>();
        GeoRent geoRent;
        if(listResource.data!=null) {
            for (RentAndDependencies rentAndDependencies : listResource.data) {
                String imagePath = rentAndDependencies.getImageAtPos(0);
                geoRent = new GeoRent(rentAndDependencies.getId(), rentAndDependencies.getName(), imagePath);
                geoRent.setAddress(rentAndDependencies.getAddress());
                geoRent.setGeoPoint(new GeoPoint(rentAndDependencies.getLatitude(), rentAndDependencies.getLongitude()));
                geoRent.setPrice(rentAndDependencies.getPrice());
                geoRent.setRating(rentAndDependencies.getRating());
                geoRent.setRatingCount(rentAndDependencies.getRatingCount());
                geoRent.setRentMode(rentAndDependencies.getRentMode());
                geoRent.setPoiItems(transformPoiEntityToPoiItem(new LatLong(rentAndDependencies.getLatitude(), rentAndDependencies.getLongitude()), rentAndDependencies.getRentPoiAndRelations()));
                geoRent.setWished(rentAndDependencies.getIsWished());
                geoRentList.add(geoRent);
            }
        }
        return geoRentList;

    }


}
