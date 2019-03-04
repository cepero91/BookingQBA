package com.infinitum.bookingqba.model.remote;


import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.DrawType;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;
import com.infinitum.bookingqba.model.remote.pojo.Offer;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.model.remote.pojo.PoiType;
import com.infinitum.bookingqba.model.remote.pojo.Province;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;
import com.infinitum.bookingqba.model.remote.pojo.Removed;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentDrawType;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.RentPoi;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    //------------------- PROVINCIAS ---------------------//

    /**
     * Obtener provincias
     * @return lista de provincias
     */
    @GET("/api/provincias")
    Single<List<Province>> getProvinces();

    //------------------- MUNICIPIOS ---------------------//

    /**
     * Obtener municipios
     * @return lista de municipios
     */
    @GET("/api/municipios")
    Single<List<Municipality>> getMunicipality();

    //------------------- FACILIDADES ---------------------//

    /**
     * Obtener facilidades
     * @return lista de facilidades
     */
    @GET("/api/facilidades")
    Single<List<Amenities>> getAmenities();

    //------------------- TIPO DE LUGAR DE INTERES ---------------------//

    /**
     * Obtener tipos de lugar de interes
     * @return lista de tipos de lugar de interes
     */
    @GET("/api/tipolugarinteres")
    Single<List<PoiType>> getPoiTypes();

    //------------------- LUGAR DE INTERES ---------------------//

    /**
     * Obtener lugar de interes
     * @return lista de lugar de interes
     */
    @GET("/api/lugarinteres")
    Single<List<Poi>> getPois();

    //------------------- RENTAS ---------------------//

    /**
     * Obtener lugar de interes
     * @return lista de lugar de interes
     */
    @GET("/api/hospedajes")
    Single<List<Rent>> getRents();

    //------------------- MODO DE RENTA ---------------------//

    /**
     * Obtener modos de renta
     * @return lista de modos de renta
     */
    @GET("/api/modalidadrenta")
    Single<List<RentMode>> getRentsMode();

    //------------------- ZONA DE REFERENCIA ---------------------//

    /**
     * Obtener zonas de referencia
     * @return lista de zonas de referencia
     */
    @GET("/api/zonareferencial")
    Single<List<ReferenceZone>> getReferencesZone();

    //------------------- TIPO DE MONEDA ---------------------//

    /**
     * Obtener tipos de moneda
     * @return lista de tipos de moneda
     */
    @GET("/api/tipomoneda")
    Single<List<DrawType>> getDrawsType();

    //------------------- GALERIA ---------------------//

    /**
     * Obtener galeria
     * @return lista galeria
     */
    @GET("/api/galeria")
    Single<List<Galerie>> getGaleries();

    //------------------- IMAGEN ---------------------//

    /**
     * Obtener imagen
     * @return responseBody
     */
    @GET()
    Single<ResponseBody> getSingleImages(@Url String url);

    //------------------- OFERTA ---------------------//

    /**
     * Obtener ofertas
     * @return lista ofertas
     */
    @GET("/api/ofertas")
    Single<List<Offer>> getOffers();

    //------------------- RENTA_FACILIDAD ---------------------//

    /**
     * Obtener renta_facilidad
     * @return lista renta_facilidad
     */
    @GET("/api/hospedajefacilidad")
    Single<List<RentAmenities>> getRentsAmenities();

    //------------------- RENTA_TIPO_MONEDA ---------------------//

    /**
     * Obtener renta_tipo_modena
     * @return lista renta_tipo_moneda
     */
    @GET("/api/hospedajetipomoneda")
    Single<List<RentDrawType>> getRentsDrawType();

    //------------------- RENTA_LUGAR_INTERES ---------------------//

    /**
     * Obtener renta_lugar_interes
     * @return lista renta_lugar_interes
     */
    @GET("/api/hospedajelugarinteres")
    Single<List<RentPoi>> getRentsPoi();




    @GET("/api/hospedajes")
    Call<List<Rent>> getHospedajesCall();

    @GET("/api/hospedajes")
    Single<List<Rent>> getHospedajes();

    @GET("/api/hospedajes")
    Observable<List<Rent>> getHospedajesZip();

    @GET("/api/hospedajes_fecha_gt")
    Observable<List<Rent>> getHospedajesFechaGt(@Query("value") String value);

    @GET("/api/removidos")
    Observable<List<Removed>> getRemovidos(@Query("value") String value);

    ///////////----IMAGENES--------/////////

    @GET
    Call<ResponseBody> getImage(@Url String url);

    @GET
    Observable<ResponseBody> getImageZip(@Url String url);

    @GET
    Observable<ResponseBody> getImageThread(@Url String url);






}
