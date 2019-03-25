package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
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

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

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


    @Inject
    public SyncViewModel(ProvinceRepository provinceRepository, MunicipalityRepository municipalityRepository, AmenitiesRepository amenitiesRepository, PoiTypeRepository poiTypeRepository, PoiRepository poiRepository, RentRepository rentRepository, RentModeRepository rentModeRepository, ReferenceZoneRepository referenceZoneRepository, DrawTypeRepository drawTypeRepository, GalerieRepository galerieRepository, RentAmenitiesRepository rentAmenitiesRepository) {
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
    }

    public Single<List<ProvinceEntity>> provinceList() {
        return provinceRepository.fetchRemoteAndTransform();
    }

    public Completable insertProvinces(List<ProvinceEntity>entities){
        return provinceRepository.insertProvinces(entities);
    }

    public Single<List<MunicipalityEntity>> municipalityList() {
        return municipalityRepository.fetchRemoteAndTransform();
    }

    public Completable insertMunicipality(List<MunicipalityEntity>entities){
        return municipalityRepository.insertMunicipalities(entities);
    }

    public Single<List<AmenitiesEntity>> amenitiesList() {
        return amenitiesRepository.fetchRemoteAndTransform();
    }

    public Completable insertAmenities(List<AmenitiesEntity>entities){
        return amenitiesRepository.insertAmenities(entities);
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

    public Single<List<ReferenceZoneEntity>> referenceZoneList() {
        return referenceZoneRepository.fetchRemoteAndTransform();
    }

    public Completable insertReferenceZone(List<ReferenceZoneEntity>entities){
        return referenceZoneRepository.insertReferencesMode(entities);
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
}
