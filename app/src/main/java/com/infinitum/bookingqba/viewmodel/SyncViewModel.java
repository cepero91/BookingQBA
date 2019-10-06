package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.pojo.GaleryUpdateUtil;
import com.infinitum.bookingqba.model.remote.pojo.RemovedList;
import com.infinitum.bookingqba.model.remote.pojo.SyncData;
import com.infinitum.bookingqba.model.repository.amenities.AmenitiesRepository;
import com.infinitum.bookingqba.model.repository.comment.CommentRepository;
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
    private CommentRepository commentRepository;
    private DBCommonOperationRepository dBCommonOperationRepository;

    private List<GaleryUpdateUtil> galeryUpdateUtilList;

    @Inject
    public SyncViewModel(ProvinceRepository provinceRepository, MunicipalityRepository municipalityRepository, AmenitiesRepository amenitiesRepository, PoiTypeRepository poiTypeRepository, PoiRepository poiRepository, RentRepository rentRepository, RentModeRepository rentModeRepository, ReferenceZoneRepository referenceZoneRepository, DrawTypeRepository drawTypeRepository, GalerieRepository galerieRepository, RentAmenitiesRepository rentAmenitiesRepository, RentPoiRepository rentPoiRepository, RentDrawTypeRepository rentDrawTypeRepository, OfferRepository offerRepository, CommentRepository commentRepository, DBCommonOperationRepository dBCommonOperationRepository) {
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
        this.commentRepository = commentRepository;
        this.dBCommonOperationRepository = dBCommonOperationRepository;
        this.galeryUpdateUtilList = new ArrayList<>();
    }

    public Flowable<List<GalerieEntity>> galeriesEntityVersionOne() {
        return galerieRepository.allGaleriesVersionOne();
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
        return dBCommonOperationRepository.fetchRemoteAndTransform();
    }

    // -------------------- SYNC ------------------- //

    public Single<OperationResult> insertDatabaseUpdate(){
        return dBCommonOperationRepository.insertFromRemote();
    }

    public Flowable<Resource<DatabaseUpdateEntity>> getDatabaseUpdateLocal(){
        return dBCommonOperationRepository.lastDatabaseUpdateLocal();
    }

    public Flowable<Resource<DatabaseUpdateEntity>> getDatabaseUpdateRemote(){
        return dBCommonOperationRepository.lastDatabaseUpdateRemote();
    }

    public Single<OperationResult> syncReferenceZone(String token, String value){
        return referenceZoneRepository.syncronizeReferenceZone(token, value);
    }

    public Single<OperationResult> syncProvinces(String token, String value){
        return provinceRepository.syncronizeProvinces(token, value);
    }

    public Single<OperationResult> syncMunicipalities(String token, String value){
        return municipalityRepository.syncronizeMunicipalities(token, value);
    }

    public Single<OperationResult> syncAmenities(String token, String value){
        return amenitiesRepository.syncronizeAmenities(token, value);
    }

    public Single<OperationResult> syncPoiTypes(String token, String value){
        return poiTypeRepository.syncronizePoiTypes(token, value);
    }

    public Single<OperationResult> syncPois(String token, String value){
        return poiRepository.syncronizePois(token, value);
    }

    public Single<OperationResult> syncRentsMode(String token, String value){
        return rentModeRepository.syncronizeRentMode(token, value);
    }

    public Single<OperationResult> syncDrawTypes(String token, String value){
        return drawTypeRepository.syncronizeDrawType(token, value);
    }

    public Single<OperationResult> syncRents(String token, String value){
        return rentRepository.syncronizeRents(token, value);
    }

    public Single<OperationResult> syncRentAmenities(String token, String value){
        return rentAmenitiesRepository.syncronizeRentAmenities(token, value);
    }

    public Single<OperationResult> syncRentPois(String token, String value){
        return rentPoiRepository.syncronizeRentPoi(token, value);
    }

    public Single<OperationResult> syncRentDrawType(String token, String value){
        return rentDrawTypeRepository.syncronizeRentDrawType(token, value);
    }

    public Single<OperationResult> syncOffers(String token, String value){
        return offerRepository.syncronizeOffers(token, value);
    }

    public Single<OperationResult> syncGaleries(String token, String value){
        return galerieRepository.syncronizeGaleries(token, value);
    }

    public Single<OperationResult> syncComment(String token, String value){
        return commentRepository.syncronizeComment(token, value);
    }

    public Single<OperationResult> removedsItem(String value){
        return dBCommonOperationRepository.deleteAll(value);
//        return Single.just(OperationResult.success());
    }

    public Flowable<SyncData> syncData(String value){
        return dBCommonOperationRepository.getSyncData(value).toFlowable();
    }
}
