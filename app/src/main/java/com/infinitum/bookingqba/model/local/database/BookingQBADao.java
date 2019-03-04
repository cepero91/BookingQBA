package com.infinitum.bookingqba.model.local.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.OfferEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentDrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;

import java.util.List;

import io.reactivex.Flowable;
import timber.log.Timber;

@Dao
public abstract class BookingQBADao {

    //------------------------- PROVINCIAS ---------------------------//

    /**
     * Insercion individual
     * @param entity ProvinceEntity
     * @return
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addProvince(ProvinceEntity entity);

    /**
     * Inserta una coleccion de Provicias
     * @param list ProvinceEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addProvinces(List<ProvinceEntity> list);

    /**
     * Actualizacion individual
     * @param entity ProvinceEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateProvince(ProvinceEntity entity);

    /**
     * Actualiza coleccion de Provicias
     * @param list ProvinceEntity
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateProvinces(List<ProvinceEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list ProvinceEntity
     */
    @Transaction
    public void upsertProvince(List<ProvinceEntity> list){
        for(ProvinceEntity entity: list){
            if(addProvince(entity) == -1){
                updateProvince(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista provincias
     * @return lista de provincias
     */
    @Query("SELECT * FROM Province")
    public abstract Flowable<List<ProvinceEntity>>getAllProvinces();

    //------------------------- MUNICIPIOS ---------------------------//

    /**
     * Insercion individual de municipio
     * @param entity MunicipalityEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addMunicipality(MunicipalityEntity entity);

    /**
     * Inserta una coleccion de Municipios
     * @param list MunicipalityEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addMunicipalities(List<MunicipalityEntity> list);

    /**
     * Actualizacion individual de municipio
     * @param entity MunicipalityEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateMunicipality(MunicipalityEntity entity);

    /**
     * Actualizacion una coleccion de Municipios
     * @param list MunicipalityEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateMunicipalities(List<MunicipalityEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list MunicipalityEntity
     */
    @Transaction
    public void upsertMunicipalities(List<MunicipalityEntity> list){
        for(MunicipalityEntity entity: list){
            if(addMunicipality(entity) == -1){
                updateMunicipality(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista municipios
     * @return lista de municipios
     */
    @Query("SELECT * FROM Municipality")
    public abstract Flowable<List<MunicipalityEntity>>getAllMunicipalities();


    //------------------------- FACILIDADES ---------------------------//

    /**
     * Insercion individual de Facilidades
     * @param entity AmenitiesEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addAmenity(AmenitiesEntity entity);

    /**
     * Inserta una coleccion de Facilidades
     * @param list AmenitiesEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addAmenities(List<AmenitiesEntity> list);

    /**
     * Actualizacion individual de Facilidades
     * @param entity AmenitiesEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateAmenity(AmenitiesEntity entity);

    /**
     * Actualizacion una coleccion de Facilidades
     * @param list AmenitiesEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateAmenities(List<AmenitiesEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list AmenitiesEntity
     */
    @Transaction
    public void upsertAmenities(List<AmenitiesEntity> list){
        for(AmenitiesEntity entity: list){
            if(addAmenity(entity) == -1){
                updateAmenity(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista Facilidades
     * @return lista de Facilidades
     */
    @Query("SELECT * FROM Amenities")
    public abstract Flowable<List<AmenitiesEntity>>getAllAmenities();


    //------------------------- TIPO DE LUGAR DE INTERES ---------------------------//

    /**
     * Insercion individual de Tipo de Lugar de Interes
     * @param entity PoiTypeEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addPoiType(PoiTypeEntity entity);

    /**
     * Inserta una coleccion de Tipo de Lugar de Interes
     * @param list PoiTypeEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addPoiTypes(List<PoiTypeEntity> list);

    /**
     * Actualizacion individual de Tipo de Lugar de Interes
     * @param entity PoiTypeEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updatePoiType(PoiTypeEntity entity);

    /**
     * Actualizacion una coleccion de Tipo de Lugar de Interes
     * @param list PoiTypeEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updatePoiTypes(List<PoiTypeEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list PoiTypeEntity
     */
    @Transaction
    public void upsertPoiTypes(List<PoiTypeEntity> list){
        for(PoiTypeEntity entity: list){
            if(addPoiType(entity) == -1){
                updatePoiType(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista Tipo de Lugar de Interes
     * @return lista de Tipo de Lugar de Interes
     */
    @Query("SELECT * FROM PoiType")
    public abstract Flowable<List<PoiTypeEntity>>getAllPoiTypes();

    //------------------------- LUGAR DE INTERES ---------------------------//

    /**
     * Insercion individual de Lugar de Interes
     * @param entity PoiEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addPoi(PoiEntity entity);

    /**
     * Inserta una coleccion de Lugar de Interes
     * @param list PoiEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addPois(List<PoiEntity> list);

    /**
     * Actualizacion individual de Lugar de Interes
     * @param entity PoiEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updatePoi(PoiEntity entity);

    /**
     * Actualizacion una coleccion de Lugar de Interes
     * @param list PoiEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updatePois(List<PoiEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list  PoiEntity
     */
    @Transaction
    public void upsertPoi(List<PoiEntity> list){
        for(PoiEntity entity: list){
            if(addPoi(entity) == -1){
                updatePoi(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista Lugar de Interes
     * @return lista de Lugar de Interes
     */
    @Query("SELECT * FROM Poi")
    public abstract Flowable<List<PoiEntity>>getAllPois();

    //------------------------- RENTAS ---------------------------//

    /**
     * Insercion individual de Renta
     * @param entity RentEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRent(RentEntity entity);

    /**
     * Inserta una coleccion de Rentas
     * @param list RentEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRents(List<RentEntity> list);

    /**
     * Actualizacion de Renta
     * @param entity RentEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRent(RentEntity entity);

    /**
     * Actualizacion una coleccion de Rentas
     * @param list RentEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRents(List<RentEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list RentEntity
     */
    @Transaction
    public void upsertRents(List<RentEntity> list){
        for(RentEntity entity: list){
            if(addRent(entity) == -1){
                updateRent(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista Rents
     * @return lista de Rentas
     */
    @Query("SELECT * FROM Rent")
    public abstract Flowable<List<RentEntity>>getAllRents();

    //------------------------- MODOS DE RENTA ---------------------------//

    /**
     * Insercion individual de Modo de Renta
     * @param entity RentModeEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRentMode(RentModeEntity entity);

    /**
     * Inserta una coleccion de Modo de Rentas
     * @param list RentModeEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRentsMode(List<RentModeEntity> list);

    /**
     * Actualizacion de Modo de Renta
     * @param entity RentModeEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentMode(RentModeEntity entity);

    /**
     * Actualizacion una coleccion de Modo de Rentas
     * @param list RentModeEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentsMode(List<RentModeEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list RentModeEntity
     */
    @Transaction
    public void upsertRentsMode(List<RentModeEntity> list){
        for(RentModeEntity entity: list){
            if(addRentMode(entity) == -1){
                updateRentMode(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista Modo de renta
     * @return lista de Modos de Renta
     */
    @Query("SELECT * FROM RentMode")
    public abstract Flowable<List<RentModeEntity>>getAllRentsMode();

    //------------------------- ZONA DE REFERENCIA ---------------------------//

    /**
     * Insercion Zona Referencia
     * @param entity ReferenceZoneEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addReferenceZone(ReferenceZoneEntity entity);

    /**
     * Inserta una coleccion de Zonas de Referencia
     * @param list ReferenceZoneEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addReferencesZone(List<ReferenceZoneEntity> list);

    /**
     * Actualizacion de Zona de Referencia
     * @param entity ReferenceZoneEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateReferenceZone(ReferenceZoneEntity entity);

    /**
     * Actualiza una coleccion de Zonas de Referencia
     * @param list ReferenceZoneEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateReferencesMode(List<ReferenceZoneEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list ReferenceZoneEntity
     */
    @Transaction
    public void upsertReferencesZone(List<ReferenceZoneEntity> list){
        for(ReferenceZoneEntity entity: list){
            if(addReferenceZone(entity) == -1){
                updateReferenceZone(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista Modo de renta
     * @return lista de Modos de Renta
     */
    @Query("SELECT * FROM ReferenceZone")
    public abstract Flowable<List<ReferenceZoneEntity>>getAllReferencesZone();

    //------------------------- TIPO DE MONEDA ---------------------------//

    /**
     * Insercion Tipo de Modena
     * @param entity DrawTypeEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addDrawType(DrawTypeEntity entity);

    /**
     * Inserta una coleccion de Tipo de Modena
     * @param list DrawTypeEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addDrawsType(List<DrawTypeEntity> list);

    /**
     * Actualizacion de Tipo de Modena
     * @param entity DrawTypeEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateDrawType(DrawTypeEntity entity);

    /**
     * Actualiza una coleccion de Tipo de Modena
     * @param list DrawTypeEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateDrawsType(List<DrawTypeEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list DrawTypeEntity
     */
    @Transaction
    public void upsertDrawsType(List<DrawTypeEntity> list){
        for(DrawTypeEntity entity: list){
            if(addDrawType(entity) == -1){
                updateDrawType(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista Modo de renta
     * @return lista de Modos de Renta
     */
    @Query("SELECT * FROM DrawType")
    public abstract Flowable<List<DrawTypeEntity>>getAllDrawsType();

    //------------------------- GALERIA ---------------------------//

    /**
     * Insercion Galeria
     * @param entity GalerieEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addGalerie(GalerieEntity entity);

    /**
     * Inserta una coleccion de Galeria
     * @param list GalerieEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addGaleries(List<GalerieEntity> list);

    /**
     * Actualizacion de Galeria
     * @param entity GalerieEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateGalerie(GalerieEntity entity);

    /**
     * Actualiza una coleccion de Galeria
     * @param list GalerieEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateGaleries(List<GalerieEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list GalerieEntity
     */
    @Transaction
    public void upsertGaleries(List<GalerieEntity> list){
        for(GalerieEntity entity: list){
            if(addGalerie(entity) == -1){
                updateGalerie(entity);
                Timber.d("%s Updated", entity.getImageUrl());
            }else{
                Timber.d("%s Inserted", entity.getImageUrl());
            }
        }
    }

    /**
     * Consulta devuelve lista Galeria
     * @return lista de GalerieEntity
     */
    @Query("SELECT * FROM Galerie")
    public abstract Flowable<List<GalerieEntity>>getAllGaleries();

    //------------------------- OFERTA ---------------------------//

    /**
     * Insercion Oferta
     * @param entity OfferEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addOffer(OfferEntity entity);

    /**
     * Inserta una coleccion de Oferta
     * @param list OfferEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addOffers(List<OfferEntity> list);

    /**
     * Actualizacion de Oferta
     * @param entity OfferEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateOffer(OfferEntity entity);

    /**
     * Actualiza una coleccion de Oferta
     * @param list OfferEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateOffers(List<OfferEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list OfferEntity
     */
    @Transaction
    public void upsertOffers(List<OfferEntity> list){
        for(OfferEntity entity: list){
            if(addOffer(entity) == -1){
                updateOffer(entity);
                Timber.d("%s Updated", entity.getName());
            }else{
                Timber.d("%s Inserted", entity.getName());
            }
        }
    }

    /**
     * Consulta devuelve lista Oferta
     * @return lista de OfferEntity
     */
    @Query("SELECT * FROM Offer")
    public abstract Flowable<List<OfferEntity>>getAllOffers();

    //------------------------- RENTA_FACILIDADES ---------------------------//

    /**
     * Insercion Renta_Facilidades
     * @param entity RentAmenitiesEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRentAmenities(RentAmenitiesEntity entity);

    /**
     * Inserta una coleccion de Renta_Facilidades
     * @param list RentAmenitiesEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRentsAmenities(List<RentAmenitiesEntity> list);

    /**
     * Actualizacion de Renta_Facilidades
     * @param entity RentAmenitiesEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentAmenities(RentAmenitiesEntity entity);

    /**
     * Actualiza una coleccion de Renta_Facilidades
     * @param list RentAmenitiesEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentsAmenities(List<RentAmenitiesEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list RentAmenitiesEntity
     */
    @Transaction
    public void upsertRentAmenities(List<RentAmenitiesEntity> list){
        for(RentAmenitiesEntity entity: list){
            if(addRentAmenities(entity) == -1){
                updateRentAmenities(entity);
                Timber.d("%s Updated", entity.getRentId()+" "+entity.getAmenityId());
            }else{
                Timber.d("%s Inserted", entity.getRentId()+" "+entity.getAmenityId());
            }
        }
    }

    /**
     * Consulta devuelve lista Renta_Facilidades
     * @return lista de RentAmenitiesEntity
     */
    @Query("SELECT * FROM RentAmenities")
    public abstract Flowable<List<RentAmenitiesEntity>>getAllRentsAmenities();

    //------------------------- RENTA_TIPO_MONEDA ---------------------------//

    /**
     * Insercion Renta_Tipo_Moneda
     * @param entity RentDrawTypeEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRentDrawType(RentDrawTypeEntity entity);

    /**
     * Inserta una coleccion de Renta_Tipo_Moneda
     * @param list RentDrawTypeEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRentsDrawType(List<RentDrawTypeEntity> list);

    /**
     * Actualizacion de Renta_Tipo_Moneda
     * @param entity RentDrawTypeEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentDrawType(RentDrawTypeEntity entity);

    /**
     * Actualiza una coleccion de Renta_Tipo_Moneda
     * @param list RentDrawTypeEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentsDrawType(List<RentDrawTypeEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list RentDrawTypeEntity
     */
    @Transaction
    public void upsertRentsDrawType(List<RentDrawTypeEntity> list){
        for(RentDrawTypeEntity entity: list){
            if(addRentDrawType(entity) == -1){
                updateRentDrawType(entity);
                Timber.d("%s Updated", entity.getRentId()+" "+entity.getDrawTypeId());
            }else{
                Timber.d("%s Inserted", entity.getRentId()+" "+entity.getDrawTypeId());
            }
        }
    }

    /**
     * Consulta devuelve lista Renta_Tipo_Moneda
     * @return lista de RentDrawTypeEntity
     */
    @Query("SELECT * FROM RentDrawType")
    public abstract Flowable<List<RentDrawTypeEntity>>getAllRentsDrawType();

    //------------------------- RENTA_PUNTO_DE_INTERES ---------------------------//

    /**
     * Insercion Renta_Punto_Interes
     * @param entity RentPoiEntity
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long addRentPoi(RentPoiEntity entity);

    /**
     * Inserta una coleccion de Renta_Punto_Interes
     * @param list RentPoiEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addRentsPoi(List<RentPoiEntity> list);

    /**
     * Actualizacion de Renta_Punto_Interes
     * @param entity RentPoiEntity
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentPoi(RentPoiEntity entity);

    /**
     * Actualiza una coleccion de Renta_Punto_Interes
     * @param list RentPoiEntity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void updateRentsPoi(List<RentPoiEntity> list);

    /**
     * Metodo de Insert or Update
     * para evitar conflictos entre entidades
     * relacionadas
     * @param list RentPoiEntity
     */
    @Transaction
    public void upsertRentsPoi(List<RentPoiEntity> list){
        for(RentPoiEntity entity: list){
            if(addRentPoi(entity) == -1){
                updateRentPoi(entity);
                Timber.d("%s Updated", entity.getRentId()+" "+entity.getPoiId());
            }else{
                Timber.d("%s Inserted", entity.getRentId()+" "+entity.getPoiId());
            }
        }
    }

    /**
     * Consulta devuelve lista Renta_Punto_Interes
     * @return lista de RentPoiEntity
     */
    @Query("SELECT * FROM RentPoi")
    public abstract Flowable<List<RentPoiEntity>>getAllRentsPoi();




}
