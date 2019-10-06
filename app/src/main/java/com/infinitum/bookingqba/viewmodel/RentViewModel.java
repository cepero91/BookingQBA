package com.infinitum.bookingqba.viewmodel;


import android.arch.lifecycle.LiveData;
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
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.pojo.PoiAndRelations;
import com.infinitum.bookingqba.model.local.pojo.RentAmenitieAndRelation;
import com.infinitum.bookingqba.model.local.pojo.RentAndDependencies;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.local.pojo.RentPoiAndRelation;
import com.infinitum.bookingqba.model.local.tconverter.CommentEmotion;
import com.infinitum.bookingqba.model.remote.pojo.BlockDay;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.DisabledDays;
import com.infinitum.bookingqba.model.remote.pojo.DrawChange;
import com.infinitum.bookingqba.model.remote.pojo.RatingVote;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.comment.CommentRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.poitype.PoiTypeRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.view.adapters.items.filter.BaseFilterItem;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableFilterItem;
import com.infinitum.bookingqba.view.adapters.items.filter.NumberFilterItem;
import com.infinitum.bookingqba.view.adapters.items.filter.RangeFilterItem;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.PoiCategory;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentInnerDetail;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentOfferItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;

import org.mapsforge.core.model.LatLong;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
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

import static com.infinitum.bookingqba.util.Constants.FILTER_AMENITIES;
import static com.infinitum.bookingqba.util.Constants.FILTER_CAPABILITY;
import static com.infinitum.bookingqba.util.Constants.FILTER_MUNICIPALITIES;
import static com.infinitum.bookingqba.util.Constants.FILTER_ORDER;
import static com.infinitum.bookingqba.util.Constants.FILTER_POITYPES;
import static com.infinitum.bookingqba.util.Constants.FILTER_PRICE;
import static com.infinitum.bookingqba.util.Constants.FILTER_RENTMODE;

public class RentViewModel extends android.arch.lifecycle.ViewModel {

    private static final int MAX_STAR = 5;

    private AmenitiesRepository amenitiesRepository;
    private RentRepository rentRepository;
    private ReferenceZoneRepository rzoneRepository;
    private MunicipalityRepository municipalityRepository;
    private CommentRepository commentRepository;
    private PoiTypeRepository poiTypeRepository;
    private Map<String, List<? extends BaseFilterItem>> filterMap;
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

    public Flowable<Resource<List<RentEsential>>> allRentByUserId(String token, String userid){
        return rentRepository.allRentByUserId(token, userid);
    }

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

    public Flowable<Resource<List<GeoRent>>> getGeoRentNearLatLon(LatLong latLong, Map<String,Object> params) {
        return rentRepository
                .rentNearLocation(latLong, params)
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

    public Flowable<Map<String, List<? extends BaseFilterItem>>> getMapFilterItems(String province) {
        if (filterMap != null && filterMap.size() > 0) {
            return Flowable.just(filterMap).subscribeOn(Schedulers.io());
        } else {
            return getMapFlowable(province);
        }
    }

    private Flowable<Map<String, List<? extends BaseFilterItem>>> getMapFlowable(String province) {
        Flowable<List<CheckableFilterItem>> amenitieFlow = amenitiesRepository.allLocalAmenities()
                .map(this::transformAmenitieToMap).subscribeOn(Schedulers.io());
        Flowable<List<CheckableFilterItem>> rentModeFlow = rentRepository.allRentMode()
                .map(this::transformRentModeToMap).subscribeOn(Schedulers.io());
        Flowable<List<CheckableFilterItem>> municipalityFlow = municipalityRepository.allMunicipalitiesByProvince(province)
                .map(this::transformMunToMap).subscribeOn(Schedulers.io());
        Flowable<List<CheckableFilterItem>> poiTypeFlow = poiTypeRepository.allLocalPoiType()
                .map(this::transformPoiTypeToMap).subscribeOn(Schedulers.io());
        Flowable<List<RangeFilterItem>> maxPrice = rentRepository.maxRentPrice()
                .map(aDouble -> {
                    List<RangeFilterItem> rangeFilterItems = new ArrayList<>();
                    rangeFilterItems.add(new RangeFilterItem(UUID.randomUUID().toString(), aDouble.floatValue()));
                    return rangeFilterItems;
                }).subscribeOn(Schedulers.io());
        Flowable<List<NumberFilterItem>> maxCapability = rentRepository.maxRentCapability()
                .map(aInteger -> {
                    List<NumberFilterItem> numberFilterItems = new ArrayList<>();
                    numberFilterItems.add(new NumberFilterItem(UUID.randomUUID().toString(), aInteger));
                    return numberFilterItems;
                }).subscribeOn(Schedulers.io());
        return Flowable.zip(amenitieFlow, rentModeFlow, municipalityFlow, poiTypeFlow, maxPrice, maxCapability,
                (amenities, rentmode, municipalities, poitypes, max, capability) -> {
                    filterMap.put(FILTER_AMENITIES, amenities);
                    filterMap.put(FILTER_RENTMODE, rentmode);
                    filterMap.put(FILTER_MUNICIPALITIES, municipalities);
                    filterMap.put(FILTER_POITYPES, poitypes);
                    filterMap.put(FILTER_PRICE, max);
                    filterMap.put(FILTER_CAPABILITY, capability);
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
        innerDetail.setReferenceZone(rentDetail.data.getReferenceZoneNameObject());
        innerDetail.setMaxBeds(rentEntity.getMaxBeds());
        innerDetail.setMaxBaths(rentEntity.getMaxBath());
        innerDetail.setCapability(rentEntity.getCapability());
        innerDetail.setMaxRooms(rentEntity.getMaxRooms());
        innerDetail.setLatitude(rentEntity.getLatitude());
        innerDetail.setLongitude(rentEntity.getLongitude());
        innerDetail.setWished(rentEntity.getIsWished());
        innerDetail.setCheckin(rentEntity.getCheckin());
        innerDetail.setChekout(rentEntity.getCheckout());
        innerDetail.setAmenitieItems(convertAmenitiesPojoToParcel(rentDetail.data.getAmenitieNames()));
        innerDetail.setPoiItemMap(convertPoisPojoToParcel(rentDetail.data.getRentPoiAndRelations()));
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

    public Completable addRating(Map<String, Object> params) {
        RatingEntity entity = new RatingEntity((String) params.get("id"), (float) params.get("rating"),
                (String) params.get("comment"), (String) params.get("userId"),
                (String) params.get("rent"), 0);
        return rentRepository.addOrUpdateRating(entity);
    }

    public Single<Resource<ResponseResult>> sendRating(String token, RatingVote ratingVote) {
        return rentRepository.sendRatingVote(token, ratingVote);
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

    public Single<Resource<ResponseResult>> blockDays(String token, BlockDay blockDay) {
        return rentRepository.blockDates(token, blockDay);
    }

    //-------------------------- RENT VISIT COUNT ---------------------------------------- //

    public Completable addOrUpdateRentVisitCount(String id, String rentId) {
        return rentRepository.addOrUpdateRentVisitCount(id, rentId);
    }

    // ------------------------- TRANSFORM METHOD ---------------------------------------- //

    private List<CheckableFilterItem> transformAmenitieToMap(Resource<List<AmenitiesEntity>> listResource) {
        ArrayList<CheckableFilterItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (AmenitiesEntity entity : listResource.data) {
                viewItemList.add(new CheckableFilterItem(entity.getId(), entity.getName(), false, FILTER_AMENITIES));
            }
        }
        return viewItemList;
    }

    private List<CheckableFilterItem> transformRentModeToMap(Resource<List<RentModeEntity>> listResource) {
        ArrayList<CheckableFilterItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (RentModeEntity entity : listResource.data) {
                viewItemList.add(new CheckableFilterItem(entity.getId(), entity.getName(), false, FILTER_RENTMODE));
            }
        }
        return viewItemList;
    }

    private List<CheckableFilterItem> transformMunToMap(Resource<List<MunicipalityEntity>> listResource) {
        ArrayList<CheckableFilterItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (MunicipalityEntity entity : listResource.data) {
                viewItemList.add(new CheckableFilterItem(entity.getId(), entity.getName(), false, FILTER_MUNICIPALITIES));
            }
        }
        return viewItemList;
    }

    private List<CheckableFilterItem> transformPoiTypeToMap(Resource<List<PoiTypeEntity>> listResource) {
        ArrayList<CheckableFilterItem> viewItemList = new ArrayList<>();
        if (listResource.data != null) {
            for (PoiTypeEntity entity : listResource.data) {
                viewItemList.add(new CheckableFilterItem(String.valueOf(entity.getId()), entity.getName(), false, FILTER_POITYPES));
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

    private Map<PoiCategory, List<RentPoiItem>> convertPoisPojoToParcel(List<RentPoiAndRelation> rentPoiAndRelations) {
        HashMap<PoiCategory, List<RentPoiItem>> hashMap = new HashMap<>();
        if (rentPoiAndRelations.size() > 0) {
            for (RentPoiAndRelation rentPoiAndRelation : rentPoiAndRelations) {
                PoiTypeEntity poiTypeEntity = rentPoiAndRelation.getPoiAndRelationsObject().getPoiTypeEntitySetObject();
                PoiCategory poiCategory = new PoiCategory(poiTypeEntity.getId(), poiTypeEntity.getName());
                RentPoiItem rentPoiItem = new RentPoiItem(rentPoiAndRelation.getPoiAndRelationsObject().poiEntity.getId(),
                        rentPoiAndRelation.getPoiAndRelationsObject().poiEntity.getName(),
                        rentPoiAndRelation.getPoiAndRelationsObject().poiEntity.getMinLat(),
                        rentPoiAndRelation.getPoiAndRelationsObject().poiEntity.getMinLon());
                if(!hashMap.containsKey(poiCategory)) {
                    ArrayList<RentPoiItem> poiItems = new ArrayList<>();
                    poiItems.add(rentPoiItem);
                    hashMap.put(poiCategory, poiItems);
                }else{
                    hashMap.get(poiCategory).add(rentPoiItem);
                }
            }
            return hashMap;
        }
        return hashMap;
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
                geoRent.setPoiItemMap(convertPoisPojoToParcel(rentAndDependencies.getRentPoiAndRelations()));
                geoRent.setReferenceZone(rentAndDependencies.getReferenceZone());
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
        if (listResource.data != null) {
            for (RentAndDependencies rentAndDependencies : listResource.data) {
                String imagePath = rentAndDependencies.getImageAtPos(0);
                geoRent = new GeoRent(rentAndDependencies.getId(), rentAndDependencies.getName(), imagePath);
                geoRent.setAddress(rentAndDependencies.getAddress());
                geoRent.setGeoPoint(new GeoPoint(rentAndDependencies.getLatitude(), rentAndDependencies.getLongitude()));
                geoRent.setPrice(rentAndDependencies.getPrice());
                geoRent.setRating(rentAndDependencies.getRating());
                geoRent.setRatingCount(rentAndDependencies.getRatingCount());
                geoRent.setRentMode(rentAndDependencies.getRentMode());
                geoRent.setPoiItemMap(convertPoisPojoToParcel(rentAndDependencies.getRentPoiAndRelations()));
                geoRent.setWished(rentAndDependencies.getIsWished());
                geoRentList.add(geoRent);
            }
        }
        return geoRentList;

    }


}
