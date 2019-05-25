package com.infinitum.bookingqba.model.local.database;

import android.arch.paging.DataSource;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.CommentEntity;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.OfferEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentDrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
import com.infinitum.bookingqba.model.local.entity.RentVisitCountEntity;
import com.infinitum.bookingqba.model.local.pojo.GaleryUpdateUtil;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.local.tconverter.DateTypeConverter;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.Rent;

import org.oscim.core.GeoPoint;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import timber.log.Timber;

@Dao
public abstract class BookingQBADao {

    //------------------------- DATABASE COMMON OPERATION ---------------------------//

    @Transaction
    @RawQuery
    public abstract long deleteAll(SupportSQLiteQuery query);

    //------------------------- DATABASE UPDATED ---------------------------//

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addDatabaseUpdate(DatabaseUpdateEntity entity);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateDatabaseUpdate(DatabaseUpdateEntity entity);


    @Transaction
    public void upsertDatabaseUpdate(DatabaseUpdateEntity entity) {
            if (addDatabaseUpdate(entity) == -1) {
                updateDatabaseUpdate(entity);
                Timber.d("%s Updated", entity.getId());
            } else {
                Timber.d("%s Inserted", entity.getId());
            }
    }


    @Query("SELECT * FROM DatabaseUpdate ORDER BY lastDateUpdateEntity DESC LIMIT 1")
    public abstract Flowable<List<DatabaseUpdateEntity>> getLastDatabaseUpdate();

    //------------------------- PROVINCIAS ---------------------------//

    /**
     * Insercion individual
     *
     * @param entity ProvinceEntity
     * @return
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addProvince(ProvinceEntity entity);

    /**
     * Inserta una coleccion de Provicias
     *
     * @param list ProvinceEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addProvinces(List<ProvinceEntity> list);

    /**
     * Actualizacion individual
     *
     * @param entity ProvinceEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateProvince(ProvinceEntity entity);

    /**
     * Actualiza coleccion de Provicias
     *
     * @param list ProvinceEntity
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateProvinces(List<ProvinceEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     *
     * @param list ProvinceEntity
     */
    @Transaction
    public void upsertProvince(List<ProvinceEntity> list) {
        for (ProvinceEntity entity : list) {
            if (addProvince(entity) == -1) {
                updateProvince(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista provincias
     *
     * @return lista de provincias
     */
    @Query("SELECT * FROM Province")
    public abstract Flowable<List<ProvinceEntity>> getAllProvinces();

    //------------------------- MUNICIPIOS ---------------------------//

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addMunicipality(MunicipalityEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addMunicipalities(List<MunicipalityEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateMunicipality(MunicipalityEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateMunicipalities(List<MunicipalityEntity> list);


    @Transaction
    public void upsertMunicipalities(List<MunicipalityEntity> list) {
        for (MunicipalityEntity entity : list) {
            if (addMunicipality(entity) == -1) {
                updateMunicipality(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Query("SELECT * FROM Municipality")
    public abstract Flowable<List<MunicipalityEntity>> getAllMunicipalities();

    @Query("SELECT Municipality.* FROM Municipality JOIN Province ON Province.id = Municipality.province AND Province.id = :province")
    public abstract Flowable<List<MunicipalityEntity>> getAllMunicipalitiesByProvince(String province);


    //------------------------- FACILIDADES ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addAmenity(AmenitiesEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addAmenities(List<AmenitiesEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateAmenity(AmenitiesEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateAmenities(List<AmenitiesEntity> list);


    @Transaction
    public void upsertAmenities(List<AmenitiesEntity> list) {
        for (AmenitiesEntity entity : list) {
            if (addAmenity(entity) == -1) {
                updateAmenity(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Query("SELECT * FROM Amenities")
    public abstract Flowable<List<AmenitiesEntity>> getAllAmenities();


    //------------------------- TIPO DE LUGAR DE INTERES ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addPoiType(PoiTypeEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addPoiTypes(List<PoiTypeEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updatePoiType(PoiTypeEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updatePoiTypes(List<PoiTypeEntity> list);


    @Transaction
    public void upsertPoiTypes(List<PoiTypeEntity> list) {
        for (PoiTypeEntity entity : list) {
            if (addPoiType(entity) == -1) {
                updatePoiType(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Query("SELECT * FROM PoiType")
    public abstract Flowable<List<PoiTypeEntity>> getAllPoiTypes();

    //------------------------- LUGAR DE INTERES ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addPoi(PoiEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addPois(List<PoiEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updatePoi(PoiEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updatePois(List<PoiEntity> list);


    @Transaction
    public void upsertPoi(List<PoiEntity> list) {
        for (PoiEntity entity : list) {
            if (addPoi(entity) == -1) {
                updatePoi(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Query("SELECT * FROM Poi")
    public abstract Flowable<List<PoiEntity>> getAllPois();

    //------------------------- COMMENT ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addComment(CommentEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addComments(List<CommentEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateComment(CommentEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateComments(List<CommentEntity> list);


    @Transaction
    public void upsertComment(List<CommentEntity> list) {
        for (CommentEntity entity : list) {
            if (addComment(entity) == -1) {
                updateComment(entity);
                Timber.d("%s Updated", entity.getDescription());
            } else {
                Timber.d("%s Inserted", entity.getDescription());
            }
        }
    }


    @Query("SELECT * FROM Comment ORDER BY created DESC")
    public abstract Flowable<List<CommentEntity>> getAllComment();

    @Query("SELECT * FROM Comment WHERE Comment.active = 0")
    public abstract Single<List<CommentEntity>> getAllInactiveComment();

    //------------------------- RENTVISITCOUNT ---------------------//

    @Transaction
    @Query("SELECT * FROM RentVisitCount")
    public abstract Single<List<RentVisitCountEntity>> getAllRentVisitCount();

    @Transaction
    @RawQuery(observedEntities = RentVisitCountEntity.class)
    public abstract long addOrUpdateRentVisit(SupportSQLiteQuery query);

    @Transaction
    @Query("DELETE FROM RentVisitCount")
    public abstract void deleteAllVisitoCount();

    @Transaction
    @Query("DELETE FROM Comment WHERE Comment.active = 0")
    public abstract void deleteAllInactiveComment();

    @Transaction
    @Query("DELETE FROM Rating")
    public abstract void deleteAllRatingVotes();

    //------------------------- RENTAS ---------------------------//

    @Transaction
    @Query("SELECT EXISTS (SELECT 1 FROM Rent WHERE id = :uuid)")
    public abstract boolean existRent(String uuid);


    public boolean existRents(List<String> uuids) {
        boolean existAll = true;
        for (String uuid : uuids) {
            if (!existRent(uuid)) {
                existAll = false;
                break;
            }
        }
        return existAll;
    }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRent(RentEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRents(List<RentEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRent(RentEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRents(List<RentEntity> list);


    @Transaction
    public void upsertRents(List<RentEntity> list) {
        for (RentEntity entity : list) {
            if (addRent(entity) == -1) {
                updateRent(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Transaction
    @Query("SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
            "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) ")
    public abstract Flowable<List<RentAndGalery>> getAllRents();

    /**
     * Este metodo devuelve solo la primera imagen de una renta
     *
     * @return
     */
    @Transaction
    @Query("SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
            "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
            "LEFT JOIN Municipality ON Rent.municipality = Municipality.id " +
            "LEFT JOIN Province ON Municipality.province = Province.id WHERE " +
            "Province.id = :province ORDER BY Rent.rating DESC LIMIT 3")
    public abstract Flowable<List<RentAndGalery>> getFivePopRents(String province);

    @Transaction
    @Query("SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
            "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
            "LEFT JOIN Municipality ON Rent.municipality = Municipality.id " +
            "LEFT JOIN Province ON Municipality.province = Province.id WHERE " +
            "Province.id = :province ORDER BY Rent.created DESC LIMIT 3")
    public abstract Flowable<List<RentAndGalery>> getFiveNewRents(String province);

    /**
     * Metodo de listado con paginado
     * @param province
     * @return
     */
    @Transaction
    @Query("SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
            "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
            "LEFT JOIN Municipality ON Rent.municipality = Municipality.id " +
            "LEFT JOIN Province ON Municipality.province = Province.id WHERE " +
            "Province.id = :province ORDER BY Rent.rating DESC")
    public abstract DataSource.Factory<Integer, RentAndGalery> getAllPopRent(String province);

    @Transaction
    @Query("SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
            "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
            "LEFT JOIN Municipality ON Rent.municipality = Municipality.id " +
            "LEFT JOIN Province ON Municipality.province = Province.id WHERE " +
            "Province.id = :province ORDER BY Rent.created DESC")
    public abstract DataSource.Factory<Integer, RentAndGalery> getAllNewRent(String province);

    @Transaction
    @TypeConverters(DateTypeConverter.class)
    @Query("SELECT Rent.* FROM Rent WHERE Rent.id=:uuid")
    public abstract Flowable<RentDetail> getRentDetailById(String uuid);

    @Transaction
    @Query("SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
            "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
            "LEFT JOIN Municipality ON Rent.municipality = Municipality.id " +
            "LEFT JOIN Province ON Municipality.province = Province.id WHERE " +
            "Province.id = :province AND Rent.isWished = 1")
    public abstract Flowable<List<RentAndGalery>> getAllWishedRents(String province);

    @Transaction
    @Query("SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished FROM Rent " +
            "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
            "LEFT JOIN Municipality ON Rent.municipality = Municipality.id " +
            "LEFT JOIN Province ON Municipality.province = Province.id WHERE " +
            "Province.id = :province AND Rent.referenceZone = :zone ORDER BY Rent.rating DESC")
    public abstract DataSource.Factory<Integer, RentAndGalery> getAllRentByZone(String province, String zone);

    @Transaction
    @Query("SELECT * FROM Rent WHERE Rent.isWished = 1")
    public abstract Single<List<RentEntity>> getAllWishedRents();

    @Transaction
    @RawQuery(observedEntities = RentEntity.class)
    public abstract long updateRentIsWished(SupportSQLiteQuery query);

    @Transaction
    @RawQuery(observedEntities = RentEntity.class)
    public abstract DataSource.Factory<Integer, RentAndGalery> filterRents(SupportSQLiteQuery query);

    @Transaction
    @RawQuery(observedEntities = RentEntity.class)
    public abstract Single<List<RentEntity>> getRentByStringUuidCommaSeparate(SupportSQLiteQuery query);

    //------------------------- RATING ---------------------------//

    @Transaction
    @RawQuery(observedEntities = RatingEntity.class)
    public abstract long addOrUpdateRating(SupportSQLiteQuery query);

    @Transaction
    @Query("SELECT * FROM Rating WHERE rent = :rent")
    public abstract Single<RatingEntity> getLastRentVote(String rent);

    @Transaction
    @Query("SELECT * FROM Rating")
    public abstract Single<List<RatingEntity>> getAllRating();


    //------------------------- MODOS DE RENTA ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRentMode(RentModeEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRentsMode(List<RentModeEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentMode(RentModeEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentsMode(List<RentModeEntity> list);


    @Transaction
    public void upsertRentsMode(List<RentModeEntity> list) {
        for (RentModeEntity entity : list) {
            if (addRentMode(entity) == -1) {
                updateRentMode(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Query("SELECT * FROM RentMode")
    public abstract Flowable<List<RentModeEntity>> getAllRentsMode();

    //------------------------- ZONA DE REFERENCIA ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addReferenceZone(ReferenceZoneEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addReferencesZone(List<ReferenceZoneEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateReferenceZone(ReferenceZoneEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateReferencesMode(List<ReferenceZoneEntity> list);


    @Transaction
    public void upsertReferencesZone(List<ReferenceZoneEntity> list) {
        for (ReferenceZoneEntity entity : list) {
            if (addReferenceZone(entity) == -1) {
                updateReferenceZone(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Query("SELECT ReferenceZone.* FROM ReferenceZone JOIN Rent ON Rent.referenceZone = ReferenceZone.id " +
            "JOIN Municipality ON Rent.municipality = Municipality.id " +
            "JOIN Province on Province.id = Municipality.province " +
            "WHERE Province.id = :province GROUP BY ReferenceZone.id")
    public abstract Flowable<List<ReferenceZoneEntity>> getAllReferencesZone(String province);


    //------------------------- TIPO DE MONEDA ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addDrawType(DrawTypeEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addDrawsType(List<DrawTypeEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateDrawType(DrawTypeEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateDrawsType(List<DrawTypeEntity> list);


    @Transaction
    public void upsertDrawsType(List<DrawTypeEntity> list) {
        for (DrawTypeEntity entity : list) {
            if (addDrawType(entity) == -1) {
                updateDrawType(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Query("SELECT * FROM DrawType")
    public abstract Flowable<List<DrawTypeEntity>> getAllDrawsType();

    //------------------------- GALERIA ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addGalerie(GalerieEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addGaleries(List<GalerieEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateGalerie(GalerieEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateGaleries(List<GalerieEntity> list);


    public void upsertGaleries(List<GalerieEntity> list) {
        for (GalerieEntity entity : list) {
            if (addGalerie(entity) == -1) {
                updateGalerie(entity);
                Timber.d("%s Updated", entity.getImageUrl());
            } else {
                Timber.d("%s Inserted", entity.getImageUrl());
            }
        }
    }

    @Transaction
    @Query("UPDATE Galerie SET imageLocalPath=:imageLocalPath, version=2 WHERE id=:uuid")
    public abstract void updateImageLocalPathGalerie(String uuid, String imageLocalPath);

    @Transaction
    public void updateListGaleryUpdateUtil(List<GaleryUpdateUtil> list) {
        for (GaleryUpdateUtil galeryUpdate : list) {
            updateImageLocalPathGalerie(galeryUpdate.getUuid(), galeryUpdate.getImageLocalPath());
        }
    }


    @Query("SELECT * FROM Galerie")
    public abstract Flowable<List<GalerieEntity>> getAllGaleries();

    @Query("SELECT * FROM Galerie WHERE version = 1")
    public abstract Flowable<List<GalerieEntity>> getAllGaleriesVersionOne();

    //------------------------- OFERTA ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addOffer(OfferEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addOffers(List<OfferEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateOffer(OfferEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateOffers(List<OfferEntity> list);


    @Transaction
    public void upsertOffers(List<OfferEntity> list) {
        for (OfferEntity entity : list) {
            if (addOffer(entity) == -1) {
                updateOffer(entity);
                Timber.d("%s Updated", entity.getName());
            } else {
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }


    @Query("SELECT * FROM Offer")
    public abstract Flowable<List<OfferEntity>> getAllOffers();

    //------------------------- RENTA_FACILIDADES ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRentAmenities(RentAmenitiesEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRentsAmenities(List<RentAmenitiesEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentAmenities(RentAmenitiesEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentsAmenities(List<RentAmenitiesEntity> list);


    @Transaction
    public void upsertRentAmenities(List<RentAmenitiesEntity> list) {
        for (RentAmenitiesEntity entity : list) {
            if (addRentAmenities(entity) == -1) {
                updateRentAmenities(entity);
                Timber.d("%s Updated", entity.getRentId() + " " + entity.getAmenityId());
            } else {
                Timber.d("%s Inserted", entity.getRentId() + " " + entity.getAmenityId());
            }
        }
    }


    @Query("SELECT * FROM RentAmenities")
    public abstract Flowable<List<RentAmenitiesEntity>> getAllRentsAmenities();

    //------------------------- RENTA_TIPO_MONEDA ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRentDrawType(RentDrawTypeEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRentsDrawType(List<RentDrawTypeEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentDrawType(RentDrawTypeEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentsDrawType(List<RentDrawTypeEntity> list);


    @Transaction
    public void upsertRentsDrawType(List<RentDrawTypeEntity> list) {
        for (RentDrawTypeEntity entity : list) {
            if (addRentDrawType(entity) == -1) {
                updateRentDrawType(entity);
                Timber.d("%s Updated", entity.getRentId() + " " + entity.getDrawTypeId());
            } else {
                Timber.d("%s Inserted", entity.getRentId() + " " + entity.getDrawTypeId());
            }
        }
    }


    @Query("SELECT * FROM RentDrawType")
    public abstract Flowable<List<RentDrawTypeEntity>> getAllRentsDrawType();

    //------------------------- RENTA_PUNTO_DE_INTERES ---------------------------//


    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRentPoi(RentPoiEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRentsPoi(List<RentPoiEntity> list);


    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentPoi(RentPoiEntity entity);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentsPoi(List<RentPoiEntity> list);


    @Transaction
    public void upsertRentsPoi(List<RentPoiEntity> list) {
        for (RentPoiEntity entity : list) {
            if (addRentPoi(entity) == -1) {
                updateRentPoi(entity);
                Timber.d("%s Updated", entity.getRentId() + " " + entity.getPoiId());
            } else {
                Timber.d("%s Inserted", entity.getRentId() + " " + entity.getPoiId());
            }
        }
    }

    @Query("SELECT * FROM RentPoi")
    public abstract Flowable<List<RentPoiEntity>> getAllRentsPoi();


}
