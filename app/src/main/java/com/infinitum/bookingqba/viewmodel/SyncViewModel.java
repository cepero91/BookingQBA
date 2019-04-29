package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.pojo.GaleryUpdateUtil;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.dbcommonsop.DBCommonOperationRepository;
import com.infinitum.bookingqba.model.repository.drawtype.DrawTypeRepository;
import com.infinitum.bookingqba.model.repository.galerie.GalerieRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.offer.OfferRepository;
import com.infinitum.bookingqba.model.repository.poi.PoiRepository;
import com.infinitum.bookingqba.model.repository.poitype.PoiTypeRepository;
import com.infinitum.bookingqba.model.repository.province.ProvinceRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.model.repository.rentamenities.RentAmenitiesRepository;
import com.infinitum.bookingqba.model.repository.rentdrawtype.RentDrawTypeRepository;
import com.infinitum.bookingqba.model.repository.rentmode.RentModeRepository;
import com.infinitum.bookingqba.model.repository.rentpoi.RentPoiRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

public class SyncViewModel extends ViewModel {

    private ProvinceRepository provinceRepository;
    private MunicipalityRepository municipalityRepository;
    private AmenitiesRepository amenitiesRepository;
    private PoiTypeRepository poiTypeRepository;
    private PoiRepository poiRepository;
    private RentRepository rentRepository;
    private RentModeRepository rentModeRepository;
    private ReferenceZoneRepository referenceZoneRepository;
    private DrawTypeRepository drawTypeRepository;
    private GalerieRepository galerieRepository;
    private RentAmenitiesRepository rentAmenitiesRepository;
    private RentPoiRepository rentPoiRepository;
    private RentDrawTypeRepository rentDrawTypeRepository;
    private OfferRepository offerRepository;
    private DBCommonOperationRepository DBCommonOperationRepository;

    private List<GaleryUpdateUtil> galeryUpdateUtilList;

    @Inject
    public SyncViewModel(ProvinceRepository provinceRepository, MunicipalityRepository municipalityRepository, AmenitiesRepository amenitiesRepository, PoiTypeRepository poiTypeRepository, PoiRepository poiRepository, RentRepository rentRepository, RentModeRepository rentModeRepository, ReferenceZoneRepository referenceZoneRepository, DrawTypeRepository drawTypeRepository, GalerieRepository galerieRepository, RentAmenitiesRepository rentAmenitiesRepository, RentPoiRepository rentPoiRepository, RentDrawTypeRepository rentDrawTypeRepository, OfferRepository offerRepository, DBCommonOperationRepository DBCommonOperationRepository) {
        this.provinceRepository = provinceRepository;
        this.municipalityRepository = municipalityRepository;
        this.amenitiesRepository = amenitiesRepository;
        this.poiTypeRepository = poiTypeRepository;
        this.poiRepository = poiRepository;
        this.rentRepository = rentRepository;
        this.rentModeRepository = rentModeRepository;
        this.referenceZoneRepository = referenceZoneRepository;
        this.drawTypeRepository = drawTypeRepository;
        this.galerieRepository = galerieRepository;
        this.rentAmenitiesRepository = rentAmenitiesRepository;
        this.rentPoiRepository = rentPoiRepository;
        this.rentDrawTypeRepository = rentDrawTypeRepository;
        this.offerRepository = offerRepository;
        this.DBCommonOperationRepository = DBCommonOperationRepository;
        this.galeryUpdateUtilList = new ArrayList<>();
    }

    public Single<List<GalerieEntity>> galerieList() {
        return galerieRepository.fetchRemoteAndTransform();
    }

    public Completable insertGalerie(List<GalerieEntity>entities){
        return galerieRepository.insertGalerie(entities);
    }

    public Flowable<List<GalerieEntity>> galerieEntityList() {
        return galerieRepository.allGalerieEntities();
    }

    public Completable updateGalerie(GalerieEntity galerieEntity){
        return galerieRepository.updateGalery(galerieEntity);
    }

    public Completable updateGaleryUpdateUtilList(){
        return galerieRepository.updateListGaleryUtil(galeryUpdateUtilList);
    }

    public void addGaleryUpdateUtil(String uuid, String path){
        galeryUpdateUtilList.add(new GaleryUpdateUtil(uuid,path));
    }

    public Single<ResponseBody> fetchImage(String url){
        return galerieRepository.fetchImage(url);
    }

    public Flowable<DatabaseUpdateEntity> remoteDatabaseUpdate(){
        return DBCommonOperationRepository.fetchRemoteAndTransform();
    }

    public Completable insertDatabaseUpdate(DatabaseUpdateEntity entity){
        return DBCommonOperationRepository.insert(entity);
    }

    // -------------------- SYNC ------------------- //

    public Flowable<Resource<DatabaseUpdateEntity>> getLastDatabaseUpdate(){
        return DBCommonOperationRepository.lastDatabaseUpdateLocal();
    }

    public Flowable<Resource<DatabaseUpdateEntity>> getDatabaseUpdateRemote(){
        return DBCommonOperationRepository.lastDatabaseUpdateRemote();
    }

    public Single<OperationResult> syncReferenceZone(String value){
        return referenceZoneRepository.syncronizeReferenceZone(value);
    }

    public Single<OperationResult> syncProvinces(String value){
        return provinceRepository.syncronizeProvinces(value);
    }

    public Single<OperationResult> syncMunicipalities(String value){
        return municipalityRepository.syncronizeMunicipalities(value);
    }

    public Single<OperationResult> syncAmenities(String value){
        return amenitiesRepository.syncronizeAmenities(value);
    }

    public Single<OperationResult> syncPoiTypes(String value){
        return poiTypeRepository.syncronizePoiTypes(value);
    }

    public Single<OperationResult> syncPois(String value){
        return poiRepository.syncronizePois(value);
    }

    public Single<OperationResult> syncRentsMode(String value){
        return rentModeRepository.syncronizeRentMode(value);
    }

    public Single<OperationResult> syncDrawTypes(String value){
        return drawTypeRepository.syncronizeDrawType(value);
    }

    public Single<OperationResult> syncRents(String value){
        return rentRepository.syncronizeRents(value);
    }

    public Single<OperationResult> syncRentAmenities(String value){
        return rentAmenitiesRepository.syncronizeRentAmenities(value);
    }

    public Single<OperationResult> syncRentPois(String value){
        return rentPoiRepository.syncronizeRentPoi(value);
    }

    public Single<OperationResult> syncRentDrawType(String value){
        return rentDrawTypeRepository.syncronizeRentDrawType(value);
    }

    public Single<OperationResult> syncOffers(String value){
        return offerRepository.syncronizeOffers(value);
    }
}
