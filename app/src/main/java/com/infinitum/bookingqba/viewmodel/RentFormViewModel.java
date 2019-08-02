package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.AddressResponse;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.view.adapters.items.addrent.MyRentItem;
import com.infinitum.bookingqba.view.profile.dialogitem.FormSelectorItem;
import com.infinitum.bookingqba.view.profile.dialogitem.SearchableSelectorModel;
import com.infinitum.bookingqba.view.profile.uploaditem.RentFormObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class RentFormViewModel extends ViewModel {

    private RentRepository rentRepository;
    private ReferenceZoneRepository rzoneRepository;
    private MunicipalityRepository municipalityRepository;
    private AmenitiesRepository amenitiesRepository;

    @Inject
    public RentFormViewModel(RentRepository rentRepository, ReferenceZoneRepository rzoneRepository, MunicipalityRepository municipalityRepository, AmenitiesRepository amenitiesRepository) {
        this.rentRepository = rentRepository;
        this.rzoneRepository = rzoneRepository;
        this.municipalityRepository = municipalityRepository;
        this.amenitiesRepository = amenitiesRepository;
    }

    public Single<Response<AddressResponse>> addressByLocation(double latitude, double longitude) {
        return rentRepository.addressByLocation(latitude,longitude);
    }

    public Flowable<Resource<List<MyRentItem>>> fetchRentByUserId(String token, String userId) {
        return rentRepository.allRentByUserId(token, userId)
                .map(this::transformToMyRent);
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

    private Resource<List<MyRentItem>> transformToMyRent(Resource<List<RentEsential>> listResource) {
        List<MyRentItem> myRentItems = new ArrayList<>();
        if(listResource.data!=null) {
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
        }else{
            return Resource.error(listResource.message);
        }
        return Resource.success(myRentItems);
    }
}
