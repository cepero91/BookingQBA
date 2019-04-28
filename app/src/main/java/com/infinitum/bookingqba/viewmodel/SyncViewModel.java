package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
import com.infinitum.bookingqba.model.local.pojo.GaleryUpdateUtil;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.databaseupdate.DatabaseUpdateRepository;
import com.infinitum.bookingqba.model.repository.drawtype.DrawTypeRepository;
import com.infinitum.bookingqba.model.repository.galerie.GalerieRepository;
import com.infinitum.bookingqba.model.repository.municipality.MunicipalityRepository;
import com.infinitum.bookingqba.model.repository.poi.PoiRepository;
import com.infinitum.bookingqba.model.repository.poitype.PoiTypeRepository;
import com.infinitum.bookingqba.model.repository.province.ProvinceRepository;
import com.infinitum.bookingqba.model.repository.referencezone.ReferenceZoneRepository;
import com.infinitum.bookingqba.model.repository.rent.RentRepository;
import com.infinitum.bookingqba.model.repository.rentamenities.RentAmenitiesRepository;
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
    private DatabaseUpdateRepository databaseUpdateRepository;

    private List<GaleryUpdateUtil> galeryUpdateUtilList;

    @Inject
    public SyncViewModel(ProvinceRepository provinceRepository, MunicipalityRepository municipalityRepository, AmenitiesRepository amenitiesRepository, PoiTypeRepository poiTypeRepository, PoiRepository poiRepository, RentRepository rentRepository, RentModeRepository rentModeRepository, ReferenceZoneRepository referenceZoneRepository, DrawTypeRepository drawTypeRepository, GalerieRepository galerieRepository, RentAmenitiesRepository rentAmenitiesRepository, RentPoiRepository rentPoiRepository, DatabaseUpdateRepository databaseUpdateRepository) {
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
        this.databaseUpdateRepository = databaseUpdateRepository;
        galeryUpdateUtilList = new ArrayList<>();
    }

    public Single<List<PoiTypeEntity>> poiTypeList() {
        return poiTypeRepository.fetchRemoteAndTransform();
    }

    public Completable insertPoiType(List<PoiTypeEntity>entities){
        return poiTypeRepository.insertPoiTypes(entities);
    }

    public Single<List<PoiEntity>> poiList() {
        return poiRepository.fetchRemoteAndTransform();
    }

    public Completable insertPoi(List<PoiEntity>entities){
        return poiRepository.insertPois(entities);
    }

    public Single<List<RentModeEntity>> rentModeList() {
        return rentModeRepository.fetchRemoteAndTransform();
    }

    public Completable insertRentMode(List<RentModeEntity>entities){
        return rentModeRepository.insertRentsMode(entities);
    }

    public Single<List<DrawTypeEntity>> drawTypeList() {
        return drawTypeRepository.fetchRemoteAndTransform();
    }

    public Completable insertDrawType(List<DrawTypeEntity>entities){
        return drawTypeRepository.insertDrawType(entities);
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

    public Single<List<RentEntity>> rentList() {
        return rentRepository.fetchRemoteAndTransform();
    }

    public Completable insertRent(List<RentEntity>entities){
        return rentRepository.insertRents(entities);
    }

    public Single<List<RentAmenitiesEntity>> rentAmenitiesList() {
        return rentAmenitiesRepository.fetchRemoteAndTransform();
    }

    public Completable insertRentAmenities(List<RentAmenitiesEntity>entities){
        return rentAmenitiesRepository.insert(entities);
    }

    public Single<List<RentPoiEntity>> rentPoiList() {
        return rentPoiRepository.fetchRemoteAndTransform();
    }

    public Completable insertRentPois(List<RentPoiEntity>entities){
        return rentPoiRepository.insert(entities);
    }

    public Flowable<DatabaseUpdateEntity> remoteDatabaseUpdate(){
        return databaseUpdateRepository.fetchRemoteAndTransform();
    }

    public Completable insertDatabaseUpdate(DatabaseUpdateEntity entity){
        return databaseUpdateRepository.insert(entity);
    }

    // -------------------- SYNC ------------------- //

    public Flowable<Resource<DatabaseUpdateEntity>> getLastDatabaseUpdate(){
        return databaseUpdateRepository.getLastDatabaseUpdate();
    }

    public Flowable<Resource<DatabaseUpdateEntity>> getDatabaseUpdateRemote(){
        return databaseUpdateRepository.lastDatabaseUpdateRemote();
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
}
