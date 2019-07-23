package com.infinitum.bookingqba.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.infinitum.bookingqba.model.OperationResult;
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
import com.infinitum.bookingqba.model.local.pojo.RentAmenitieAndRelation;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.local.pojo.RentPoiAndRelation;
import com.infinitum.bookingqba.model.local.tconverter.CommentEmotion;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.comment.CommentRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableItem;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentInnerDetail;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentOfferItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;
import com.infinitum.bookingqba.view.profile.uploaditem.AmenitiesRentFormObject;
import com.infinitum.bookingqba.view.profile.uploaditem.RentFormObject;
import com.infinitum.bookingqba.view.profile.dialogitem.FormSelectorItem;
import com.infinitum.bookingqba.view.profile.dialogitem.SearchableSelectorModel;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
        DataSource.Factory<Integer, RentListItem> dataSource = rentRepository.allRentByOrderType(orderType, province).mapByPage(this::transformPaginadedData);
        LivePagedListBuilder<Integer, RentListItem> pagedListBuilder = new LivePagedListBuilder<>(dataSource, 10);
        ldRentsList = pagedListBuilder.build();
        return ldRentsList;
    }

    public LiveData<PagedList<RentListItem>> getAllRentByZone(String province, String zone) {
        DataSource.Factory<Integer, RentListItem> dataSource = rentRepository.allRentByZone(province, zone).mapByPage(this::transformPaginadedData);
        LivePagedListBuilder<Integer, RentListItem> pagedListBuilder = new LivePagedListBuilder<>(dataSource, 10);
        ldRentsList = pagedListBuilder.build();
        return ldRentsList;
    }

    public Flowable<Resource<List<ListWishItem>>> getRentListWish(String province) {
        return rentRepository.allWishedRent(province)
                .subscribeOn(Schedulers.io())
                .map(this::transformToWishList)
                .onErrorReturn(Resource::error);
    }

    public Flowable<Resource<List<GeoRent>>> getGeoRent() {
        return rentRepository.allRent().map(this::transformToGeoRent);
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
        return Flowable.combineLatest(amenitieFlow, rentModeFlow, municipalityFlow, (amenities, rentmode, municipalities) -> {
            filterMap.put("Amenities", amenities);
            filterMap.put("RentMode", rentmode);
            filterMap.put("Municipality", municipalities);
            return filterMap;
        }).subscribeOn(Schedulers.io());
    }

    public LiveData<PagedList<RentListItem>> filter(Map<String, List<String>> filterParams, String province) {
        DataSource.Factory<Integer, RentListItem> dataSource = rentRepository.filterRents(filterParams, province)
                .mapByPage(this::transformPaginadedData);
        LivePagedListBuilder<Integer, RentListItem> pagedListBuilder = new LivePagedListBuilder<>(dataSource, 10);
        ldRentsList = pagedListBuilder.build();
        return ldRentsList;
    }

    //------------------------- ADD NEW RENT -------------------------------------------- //

    public Flowable<Resource<List<String>>> fetchRentByUserId(String token, String userId) {
        return rentRepository.allRentByUserId(token, userId).map(new Function<Resource<List<Rent>>, Resource<List<String>>>() {
            @Override
            public Resource<List<String>> apply(Resource<List<Rent>> listResource) throws Exception {
                List<String> stringList = new ArrayList<>();
                if (listResource.data != null) {
                    for (Rent rent : listResource.data)
                        stringList.add(rent.getName());
                }
                return Resource.success(stringList);
            }
        });
    }

    public Single<Resource<List<SearchableSelectorModel>>> getAllRemoteReferenceZone(String token) {
//        List<SearchableSelectorModel> formSelectorItemList = new ArrayList<>();
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Playa"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Historia"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Natural"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Barriada"));
//
//        return Single.just(Resource.success(formSelectorItemList)).delay(3000, TimeUnit.MILLISECONDS);
        return rzoneRepository.allRemoteReferencesZone(token)
                .subscribeOn(Schedulers.io())
                .map(this::transformReferenceZoneToFormSelector)
                .onErrorReturn(Resource::error);
    }

    private Resource<List<SearchableSelectorModel>> transformReferenceZoneToFormSelector(Resource<List<ReferenceZone>> listResource) {
        if (listResource.data != null && listResource.data.size() > 0) {
            List<SearchableSelectorModel> formSelectorItems = new ArrayList<>();
            for (ReferenceZone referenceZone : listResource.data) {
                formSelectorItems.add(new SearchableSelectorModel(referenceZone.getId(), referenceZone.getName()));
            }
            return Resource.success(formSelectorItems);
        } else {
            return Resource.error("Datos nulos o vacios");
        }
    }

    public Single<Resource<List<FormSelectorItem>>> getAllRemoteAmenities(String token) {
//        List<FormSelectorItem> formSelectorItemList = new ArrayList<>();
//        formSelectorItemList.add(new FormSelectorItem(UUID.randomUUID().toString(), "Aire Acondicionado"));
//        formSelectorItemList.add(new FormSelectorItem(UUID.randomUUID().toString(), "Mini bar"));
//        formSelectorItemList.add(new FormSelectorItem(UUID.randomUUID().toString(), "Cocina"));
//        formSelectorItemList.add(new FormSelectorItem(UUID.randomUUID().toString(), "Refrigerador"));
//        formSelectorItemList.add(new FormSelectorItem(UUID.randomUUID().toString(), "Agua caliente"));
//        formSelectorItemList.add(new FormSelectorItem(UUID.randomUUID().toString(), "Entrada independiente"));
//        formSelectorItemList.add(new FormSelectorItem(UUID.randomUUID().toString(), "Parqueo"));
//        formSelectorItemList.add(new FormSelectorItem(UUID.randomUUID().toString(), "Wifi"));
//
//        return Single.just(Resource.success(formSelectorItemList));

        return amenitiesRepository.allRemoteAmenities(token)
                .subscribeOn(Schedulers.io())
                .map(this::transformAmenitiesToFormSelector)
                .onErrorReturn(Resource::error);
    }

    private Resource<List<FormSelectorItem>> transformAmenitiesToFormSelector(Resource<List<Amenities>> listResource) {
        if (listResource.data != null && listResource.data.size() > 0) {
            List<FormSelectorItem> formSelectorItems = new ArrayList<>();
            for (Amenities amenities : listResource.data) {
                formSelectorItems.add(new FormSelectorItem(amenities.getId(), amenities.getName()));
            }
            return Resource.success(formSelectorItems);
        } else {
            return Resource.error("Datos nulos o vacios");
        }
    }

    public Single<Resource<List<SearchableSelectorModel>>> getAllRemoteMunicipalities(String token) {
//        List<SearchableSelectorModel> formSelectorItemList = new ArrayList<>();
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Arroyo Naranjo"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"San Miguel del Padron"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Boyeros"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Centro Habana"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Regla"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Guanabacoa"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Habana Vieja"));
//
//        return Single.just(Resource.success(formSelectorItemList)).delay(3000, TimeUnit.MILLISECONDS);

        return municipalityRepository.allRemoteMunicipalities(token)
                .subscribeOn(Schedulers.io())
                .map(this::transformMunicipalitiesToFormSelector)
                .onErrorReturn(Resource::error);
    }

    private Resource<List<SearchableSelectorModel>> transformMunicipalitiesToFormSelector(Resource<List<Municipality>> listResource) {
        if (listResource.data != null && listResource.data.size() > 0) {
            List<SearchableSelectorModel> formSelectorItems = new ArrayList<>();
            for (Municipality municipality : listResource.data) {
                SearchableSelectorModel searchableSelectorModel = new SearchableSelectorModel(municipality.getId(), municipality.getName());
                formSelectorItems.add(searchableSelectorModel);
            }
            return Resource.success(formSelectorItems);
        } else {
            return Resource.error("Datos nulos o vacios");
        }
    }

    public Single<Resource<List<SearchableSelectorModel>>> getAllRemoteRentMode(String token) {
//        List<SearchableSelectorModel> formSelectorItemList = new ArrayList<>();
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Por Horas"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Por Habitacion"));
//        formSelectorItemList.add(new SearchableSelectorModel(UUID.randomUUID().toString(),"Por Noche"));
//
//
//        return Single.just(Resource.success(formSelectorItemList)).delay(3000, TimeUnit.MILLISECONDS);

        return rentRepository.allRemoteRentMode(token)
                .subscribeOn(Schedulers.io())
                .map(this::transformRentModeToFormSelector)
                .onErrorReturn(Resource::error);
    }

    private Resource<List<SearchableSelectorModel>> transformRentModeToFormSelector(Resource<List<RentMode>> listResource) {
        if (listResource.data != null && listResource.data.size() > 0) {
            List<SearchableSelectorModel> formSelectorItems = new ArrayList<>();
            for (RentMode rentMode : listResource.data) {
                SearchableSelectorModel searchableSelectorModel = new SearchableSelectorModel(rentMode.getId(), rentMode.getName());
                formSelectorItems.add(searchableSelectorModel);
            }
            return Resource.success(formSelectorItems);
        } else {
            return Resource.error("Datos nulos o vacios");
        }
    }

    public Single<OperationResult> sendRentToServer(String token, RentFormObject rentFormObject,
                                                    ArrayList<String> amenitiesRentFormObjects,
                                                    ArrayList<String> imagesPath) {
        Rent newRent = transformRentFormToRent(rentFormObject);
        RentAmenities rentAmenities = new RentAmenities(rentFormObject.getUuid(), amenitiesRentFormObjects);
        return rentRepository.addRent(token, newRent, rentAmenities, imagesPath);
    }


    private Rent transformRentFormToRent(RentFormObject rentFormObject) {
        Rent rent = new Rent();
        rent.setId(rentFormObject.getUuid());
        rent.setName(rentFormObject.getRentName());
        rent.setAddress(rentFormObject.getAddress());
        rent.setDescription(rentFormObject.getDescription());
        rent.setEmail(rentFormObject.getEmail());
        rent.setPhoneNumber(rentFormObject.getPhoneNumber());
        rent.setPhoneHomeNumber(rentFormObject.getPhoneHomeNumber());
        rent.setMaxRooms(Integer.parseInt(rentFormObject.getMaxRooms()));
        rent.setMaxBath(Integer.parseInt(rentFormObject.getMaxBaths()));
        rent.setMaxBeds(Integer.parseInt(rentFormObject.getMaxBeds()));
        rent.setCapability(Integer.parseInt(rentFormObject.getCapability()));
        rent.setRentMode(rentFormObject.getRentMode());
        rent.setRules(rentFormObject.getRules());
        rent.setPrice(rentFormObject.getPrice());
        rent.setLatitude(rentFormObject.getLatitude());
        rent.setLongitude(rentFormObject.getLongitude());
        rent.setMunicipality(rentFormObject.getMunicipality());
        rent.setReferenceZone(rentFormObject.getReferenceZone());
        rent.setUserid("1");
        return rent;
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
        return commentRepository.insert(list);
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

    private ArrayList<RentOfferItem> convertOfferPojoToParcel(List<OfferEntity> offerEntityList) {
        ArrayList<RentOfferItem> offerItems = new ArrayList<>();
        for (OfferEntity entity : offerEntityList) {
            offerItems.add(new RentOfferItem(entity.getId(), entity.getName(), entity.getDescription(), entity.getPrice()));
        }
        return offerItems;
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
            return Resource.error("Datos nulos o vacios");
        }
    }

    private Resource<List<ListWishItem>> transformToWishList(Resource<List<RentAndGalery>> listResource) {
        List<ListWishItem> listWishItems = new ArrayList<>();
        ListWishItem item;
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
    }

    @NonNull
    private List<RentListItem> transformPaginadedData(List<RentAndGalery> input) {
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
    }


}
