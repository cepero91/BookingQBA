package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.AddressResponse;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;
import com.infinitum.bookingqba.model.remote.pojo.Offer;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentEdit;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.RentPoiAdd;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.geo.POIEntitySort;
import com.infinitum.bookingqba.view.adapters.items.addrent.MyRentItem;
import com.infinitum.bookingqba.view.profile.dialogitem.FormSelectorItem;
import com.infinitum.bookingqba.view.profile.dialogitem.SearchableSelectorModel;
import com.infinitum.bookingqba.view.profile.uploaditem.AmenitiesRentFormObject;
import com.infinitum.bookingqba.view.profile.uploaditem.GaleryFormObject;
import com.infinitum.bookingqba.view.profile.uploaditem.OfferFormObject;
import com.infinitum.bookingqba.view.profile.uploaditem.RentFormObject;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.poi.android.storage.AndroidPoiPersistenceManagerFactory;
import org.mapsforge.poi.storage.ExactMatchPoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategory;
import org.mapsforge.poi.storage.PoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategoryManager;
import org.mapsforge.poi.storage.PoiPersistenceManager;
import org.mapsforge.poi.storage.PointOfInterest;
import org.mapsforge.poi.storage.UnknownPoiCategoryException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

public class RentFormViewModel extends ViewModel {

    private RentRepository rentRepository;
    private ReferenceZoneRepository rzoneRepository;
    private MunicipalityRepository municipalityRepository;
    private AmenitiesRepository amenitiesRepository;
    private List<Poi> poiList;
    private PoiPersistenceManager mPersistenceManager;

    private boolean[] amenitiesSelected;
    private String[] amenitiesNames;
    private List<Amenities> remoteAmenities;
    private List<Amenities> localAmenities;


    @Inject
    public RentFormViewModel(RentRepository rentRepository, ReferenceZoneRepository rzoneRepository, MunicipalityRepository municipalityRepository, AmenitiesRepository amenitiesRepository) {
        this.rentRepository = rentRepository;
        this.rzoneRepository = rzoneRepository;
        this.municipalityRepository = municipalityRepository;
        this.amenitiesRepository = amenitiesRepository;
    }

    public void setLocalAmenities(List<Amenities> localAmenities) {
        this.localAmenities = localAmenities;
    }

    public Single<Resource<List<RentEdit>>> getRentById(String token, String uuid) {
        return rentRepository.rentById(token, uuid);
    }

    public Single<Resource<Pair<String[], boolean[]>>> getAllRemoteAmenities(String token) {
        if (amenitiesNames != null && amenitiesSelected != null) {
            return Single.just(Resource.success(new Pair<>(amenitiesNames, amenitiesSelected))).subscribeOn(Schedulers.io());
        } else {
            return amenitiesRepository.allRemoteAmenities(token)
                    .subscribeOn(Schedulers.io())
                    .map(listResource -> {
                        Pair<String[], boolean[]> pair = new Pair<>(new String[0], new boolean[0]);
                        if (listResource.data != null && listResource.data.size() > 0) {
                            remoteAmenities = listResource.data;
                            pair = setupAmenitiesParamsArray(listResource.data);
                        }
                        return Resource.success(pair);
                    }).onErrorReturn(Resource::error);
        }
    }


    private Pair<String[], boolean[]> setupAmenitiesParamsArray(List<Amenities> data) {
        amenitiesSelected = new boolean[data.size()];
        amenitiesNames = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            if (localAmenities != null && localAmenities.size() > 0 && i < localAmenities.size()) {
                amenitiesSelected[i] = data.get(i).getId().equals(localAmenities.get(i).getId());
            } else {
                amenitiesSelected[i] = false;
            }
            amenitiesNames[i] = data.get(i).getName();
        }
        return new Pair<>(amenitiesNames, amenitiesSelected);
    }

    public void checkAmenity(int pos, boolean checked) {
        amenitiesSelected[pos] = checked;
    }

    public List<String> getAmenitiesSelectedNames() {
        List<String> selectedNames = new ArrayList<>();
        for (int i = 0; i < amenitiesSelected.length; i++) {
            if (amenitiesSelected[i])
                selectedNames.add(amenitiesNames[i]);
        }
        return selectedNames;
    }

    private List<String> getAmenitiesSelectedUuid() {
        List<String> uuidAmenities = new ArrayList<>();
        if (amenitiesSelected != null) {
            for (int i = 0; i < amenitiesSelected.length; i++) {
                if (amenitiesSelected[i])
                    uuidAmenities.add(remoteAmenities.get(i).getId());
            }
        } else if (localAmenities != null) {
            for (int i = 0; i < localAmenities.size(); i++) {
                uuidAmenities.add(localAmenities.get(i).getId());
            }
        }
        return uuidAmenities;
    }

    private void transformPointOfInterestToPOI(Collection<PointOfInterest> pointOfInterests) {
        poiList = new ArrayList<>();
        for (PointOfInterest point : pointOfInterests) {
            Poi poi = new Poi();
            poi.setName(point.getName());
            poi.setId(UUID.randomUUID().toString());
            poi.setMinLat(point.getLatitude());
            poi.setMinLon(point.getLongitude());
            PoiCategory[] poiCategories = point.getCategories().toArray(new PoiCategory[point.getCategories().size()]);
            if (poiCategories.length > 1) {
                int id1 = poiCategories[0].getID();
                int id2 = poiCategories[1].getID();
                if (id1 != 396) {
                    poi.setCategory(id1);
                } else if (id2 != 396) {
                    poi.setCategory(id2);
                }
            }else if(poiCategories.length == 1){
                poi.setCategory(poiCategories[0].getID());
            }
            poiList.add(poi);
        }
    }

    public Flowable<Resource<List<MyRentItem>>> fetchRentByUserId(String token, String userId) {
        return rentRepository.allRentByUserId(token, userId)
                .map(this::transformToMyRent);
    }

    public Single<Resource<List<SearchableSelectorModel>>> getAllRemoteReferenceZone(String token) {
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

    public Single<List<ResponseResult>> sendRentToServer(String token, Map<String, Object> params) {
        Map<String, Object> finalMap = new HashMap<>();
        RentFormObject rentFormObject = (RentFormObject) params.get("rent");
        ArrayList<String> amenitiesUuid = (ArrayList<String>) getAmenitiesSelectedUuid();
        RentAmenities rentAmenities = new RentAmenities(rentFormObject.getUuid(), amenitiesUuid);
        ArrayList<String> imageFilePath = getImageFilePath((ArrayList<GaleryFormObject>) params.get("galery"));
        Rent newRent = transformRentFormToRent(rentFormObject);
        finalMap.put("rent", newRent);
        finalMap.put("amenities", rentAmenities);
        if (poiList != null && poiList.size() > 0)
            finalMap.put("poi", new RentPoiAdd(rentFormObject.getUuid(), poiList));
        if (imageFilePath != null && imageFilePath.size() > 0)
            finalMap.put("galery", imageFilePath);
        if (params.containsKey("offer")) {
            List<Offer> offerList = transformToOffer((List<OfferFormObject>) params.get("offer"));
            finalMap.put("offer", offerList);
        }
        return rentRepository.addRent(token, finalMap);
    }

    private ArrayList<String> getImageFilePath(ArrayList<GaleryFormObject> galery) {
        ArrayList<String> imageList = new ArrayList<>();
        for (int i = 0; i < galery.size(); i++) {
            if (!galery.get(i).isRemote()) {
                imageList.add(galery.get(i).getUrl());
            }
        }
        return imageList;
    }

    private List<Offer> transformToOffer(List<OfferFormObject> rentFormObjects) {
        List<Offer> offers = new ArrayList<>();
        Offer offer;
        for (OfferFormObject offerFormObject : rentFormObjects) {
            offer = new Offer();
            offer.setId(offerFormObject.getUuid());
            offer.setName(offerFormObject.getName());
            offer.setDescription(offerFormObject.getDescription());
            offer.setPrice(Double.parseDouble(offerFormObject.getPrice()));
            offer.setRent(offerFormObject.getRent());
            offers.add(offer);
        }
        return offers;
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
        rent.setCheckin(rentFormObject.getCheckin());
        rent.setCheckout(rentFormObject.getCheckout());
        rent.setUserid(rentFormObject.getUserid());
        return rent;
    }

    private Resource<List<MyRentItem>> transformToMyRent(Resource<List<RentEsential>> listResource) {
        List<MyRentItem> myRentItems = new ArrayList<>();
        if (listResource.data != null) {
            MyRentItem item;
            for (RentEsential rentEsential : listResource.data) {
                item = new MyRentItem();
                item.setUuid(rentEsential.getId());
                item.setName(rentEsential.getName());
                item.setPortrait(rentEsential.getPortrait());
                item.setPrice(rentEsential.getPrice());
                item.setRentMode(rentEsential.getRentMode());
                item.setActive(rentEsential.isActive());
                myRentItems.add(item);
            }
        } else {
            return Resource.error(listResource.message);
        }
        return Resource.success(myRentItems);
    }

    public Single<Map<String, Object>> poiAndReferenceZone(String token, String filePoi, double mLatitude, double mLongitude) {
        return Single.just(getPointOfInterestByLocation(filePoi, mLatitude, mLongitude))
                .map(pointOfInterests -> {
                    List<PointOfInterest> result = new ArrayList<>(pointOfInterests);
                    Collections.sort(result, new POIEntitySort(new LatLong(mLatitude, mLongitude)));
                    if (result.size() > 30) {
                        return result.subList(0, 30);
                    }
                    return result;
                })
                .flatMap(pointOfInterests -> {
                    String referenceZone = getReferenceNameByPoiCategory(pointOfInterests);
                    mPersistenceManager.close();
                    return getRefenceZoneByNameInfered(token, referenceZone, pointOfInterests);
                })
                .map(map -> {
                    if (map.containsKey("poi"))
                        transformPointOfInterestToPOI((Collection<PointOfInterest>) map.get("poi"));
                    return map;
                })
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("error", throwable);
                    return map;
                });
    }

    private Collection<PointOfInterest> getPointOfInterestByLocation(String poiFile, double mLatitude, double mLongitude) {
        mPersistenceManager = AndroidPoiPersistenceManagerFactory.getPoiPersistenceManager(poiFile);
        PoiCategoryManager categoryManager = mPersistenceManager.getCategoryManager();
        PoiCategoryFilter categoryFilter = new ExactMatchPoiCategoryFilter();
        for (String category : Constants.category) {
            try {
                categoryFilter.addCategory(categoryManager.getPoiCategoryByTitle(category));
            } catch (UnknownPoiCategoryException e) {
                e.printStackTrace();
            }
        }
        return mPersistenceManager.findNearPosition(new LatLong(mLatitude, mLongitude), 1000, categoryFilter, null, Integer.MAX_VALUE);
    }

    private String getReferenceNameByPoiCategory(Collection<PointOfInterest> pointOfInterests) {
        String referenceZoneResult = "Barriada";
        if (containBeach(pointOfInterests)) {
            referenceZoneResult = "Playa";
        } else if (containCategory(pointOfInterests, Constants.historic_category)) {
            referenceZoneResult = "Historia";
        } else if (containCategory(pointOfInterests, Constants.natural_category)) {
            referenceZoneResult = "Natural";
        }
        return referenceZoneResult;
    }

    private boolean containBeach(Collection<PointOfInterest> pointOfInterests) {
        for (PointOfInterest point : pointOfInterests) {
            PoiCategory[] poiCategories = point.getCategories().toArray(new PoiCategory[point.getCategories().size()]);
            for (PoiCategory poiCategory : poiCategories) {
                if (poiCategory.getTitle().equals("Marinas")) {
                    return true;
                } else if (poiCategory.getTitle().equals("Attractions") && point.getName().contains("Playa")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containCategory(Collection<PointOfInterest> pointOfInterests, String[] categories) {
        for (PointOfInterest point : pointOfInterests) {
            if (categoryIsInList(categories, point.getCategories())) {
                return true;
            }
        }
        return false;
    }

    private boolean categoryIsInList(String[] categoriesArray, Set<PoiCategory> categories) {
        PoiCategory[] poiCategories = categories.toArray(new PoiCategory[categories.size()]);
        for (PoiCategory poiCategory : poiCategories) {
            if (categoryExist(categoriesArray, poiCategory.getTitle())) {
                return true;
            }
        }
        return false;
    }

    private boolean categoryExist(String[] categories, String category) {
        for (String stringCategory : categories) {
            if (stringCategory.equals(category))
                return true;
        }
        return false;
    }

    public Single<Map<String, String>> addressAndMunicipalityByLocation(String token, double mLatitude, double mLongitude) {
        return rentRepository.addressByLocation(mLatitude, mLongitude)
                .subscribeOn(Schedulers.io())
                .flatMap(addressResponseResponse -> getMunicipalityByAddress(token, addressResponseResponse.body()));
    }

    private Single<Map<String, String>> getMunicipalityByAddress(String token, AddressResponse response) {
        Map<String, String> map = new HashMap<>();
        map.put("address", buildAddressText(response.getDisplayName()));
        return municipalityRepository.allRemoteMunicipalities(token)
                .map(listResource -> {
                    if (response.getAddress().getCounty() != null) {
                        if (listResource.data != null && listResource.data.size() > 0) {
                            Pair<String, String> pair = findTownNameInMunicipalities(response.getAddress().getCounty(), listResource.data);
                            if (!pair.first.equals("") && !pair.second.equals("")) {
                                map.put("uuid", pair.first);
                                map.put("name", pair.second);
                            }
                        }
                    }
                    return map;
                })
                .onErrorReturn(throwable -> {
                    map.put("error", throwable.getMessage());
                    return map;
                });
    }

    private Pair<String, String> findTownNameInMunicipalities(String town, List<Municipality> data) {
        for (Municipality municipality : data) {
            if (town.equalsIgnoreCase(municipality.getName())) {
                return new Pair<>(municipality.getId(), municipality.getName());
            }
        }
        return new Pair<>("", "");
    }

    private String buildAddressText(String displayName) {
        StringBuilder addressBuilder = new StringBuilder();
        String[] displayList = displayName.split(",");
        int size = displayList.length - 2;
        for (int i = 0; i < size; i++) {
            addressBuilder.append(displayList[i]);
            if (i < size - 1) {
                addressBuilder.append(", ");
            }
        }
        if (!addressBuilder.toString().isEmpty()) {
            return addressBuilder.toString();
        } else {
            return "";
        }
    }


    private Single<Map<String, Object>> getRefenceZoneByNameInfered(String token, String nameInfered, Collection<PointOfInterest> collection) {
        Map<String, Object> map = new HashMap<>();
        map.put("poi", collection);
        return getAllRemoteReferenceZone(token).map(listResource -> {
            if (listResource.data != null && listResource.data.size() > 0) {
                Pair<String, String> pair = findReferenceZoneOnRemoteList(nameInfered, listResource.data);
                if (!pair.first.equals("") && !pair.second.equals("")) {
                    map.put("uuid", pair.first);
                    map.put("name", pair.second);
                }
            }
            return map;
        });
    }


    private Pair<String, String> findReferenceZoneOnRemoteList(String nameInfered, List<SearchableSelectorModel> list) {
        Pair<String, String> pair = new Pair<>("", "");
        for (SearchableSelectorModel model : list) {
            if (model.getTitle().equals(nameInfered)) {
                pair = new Pair<>(model.getUuid(), model.getTitle());
            }
        }
        return pair;
    }
}