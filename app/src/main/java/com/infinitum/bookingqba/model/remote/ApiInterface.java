package com.infinitum.bookingqba.model.remote;


import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.CommentGroup;
import com.infinitum.bookingqba.model.remote.pojo.DatabaseUpdate;
import com.infinitum.bookingqba.model.remote.pojo.DrawType;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;
import com.infinitum.bookingqba.model.remote.pojo.Offer;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.model.remote.pojo.PoiType;
import com.infinitum.bookingqba.model.remote.pojo.Province;
import com.infinitum.bookingqba.model.remote.pojo.RatingVoteGroup;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;
import com.infinitum.bookingqba.model.remote.pojo.RemovedList;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentDrawType;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.RentPoi;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCountGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentWished;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    //------------------- REMOVED ---------------------------//

    @GET("/api/removeds")
    Flowable<List<RemovedList>> getRemoveds(@Query("value") String value);

    //------------------- DATABASE UPDATE ---------------------------//

    @GET("/api/date-count-update")
    Flowable<DatabaseUpdate> getDatabaseUpdate();

    //------------------- USER ---------------------------//

    @FormUrlEncoded
    @POST("/api-login/")
    Single<Response<User>> login(@Field("param1")String param1, @Field("param2")String param2, @Field("param3")String param3);

    @FormUrlEncoded
    @POST("/api-login/")
    Single<Response<User>> login(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("/api-register-user/")
    Single<ResponseResult> register(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("/api-user-activation/")
    Single<ResponseResult> activeUser(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("/api-activation-resend/")
    Single<ResponseResult> resendActivationCode(@FieldMap Map<String, String> map);

    @GET("secret")
    Call<String> getSecret(@Header("Authorization") String token);


    //------------------- RENTAS EN LISTA DE DESEO ---------------------------//

    @POST("/update-rent-wished/")
    Single<ResponseResult> updateRentWished(@Header("Authorization") String token, @Body RentWished rentWished);

    // ------------------ VOTACIONES DE ESTRELLAS DE LOS USUARIOS ------------//

    @POST("/update-rent-rating/")
    Single<ResponseResult> updateRatingVotes(@Header("Authorization") String token, @Body RatingVoteGroup ratingVoteGroup);

    // ------------------ COMENTARIOS DE USUARIOS ------------//

    @POST("/update-rent-comment/")
    Single<ResponseResult> updateRentComment(@Header("Authorization") String token, @Body CommentGroup commentGroup);


    //------------------- CONTADOR DE VISITAS ---------------------------//

    @POST("/update-visit-count/")
    Single<ResponseResult> updateRentVisitCount(@Header("Authorization") String token, @Body RentVisitCountGroup rentVisitCountGroup);

    //------------------- ANALITICS ---------------------------//

    @GET("/api/statistic/")
    Single<AnaliticsGroup> rentAnalitics(@Query("value") String uuid);

    @GET("/api/statistic/")
    Observable<Response<AnaliticsGroup>> rentAnaliticsObservable(@Query("value") String uuid);

    //------------------- PROVINCIAS ---------------------//
    /**
     * Obtener provincias
     * @return lista de provincias
     */
    @GET("/api/provinces")
    Single<List<Province>> getProvinces(@Header("Authorization") String token, @Query("value") String value);

    //------------------- MUNICIPIOS ---------------------//

    @GET("/api/municipalities")
    Single<List<Municipality>> getMunicipality(@Header("Authorization") String token, @Query("value") String value);

    @GET("/api/municipalities-all")
    Single<List<Municipality>> getAllMunicipality(@Header("Authorization") String token);

    //------------------- FACILIDADES ---------------------//

    @GET("/api/amenities")
    Single<List<Amenities>> getAmenities(@Header("Authorization") String token, @Query("value") String value);

    @GET("/api/amenities-all")
    Single<List<Amenities>> getAllAmenities(@Header("Authorization") String token);

    //------------------- TIPO DE LUGAR DE INTERES ---------------------//

    @GET("/api/poiTypes")
    Single<List<PoiType>> getPoiTypes(@Header("Authorization") String token, @Query("value") String value);

    //------------------- LUGAR DE INTERES ---------------------//

    @GET("/api/pois")
    Single<List<Poi>> getPois(@Header("Authorization") String token, @Query("value") String value);

    //------------------- RENTAS ---------------------//

    @GET("/api/user-rents")
    Flowable<List<Rent>> allRentByUserId(@Header("Authorization") String token, @Query("userid") String value);

    @GET("/api/rents")
    Single<List<Rent>> getRents(@Header("Authorization") String token, @Query("value") String value);

    @POST("/api/rents-add")
    Single<ResponseResult> addRent(@Header("Authorization") String token, @Body Rent rent);

    @POST("/api/rentAmenities-add")
    Single<ResponseResult> addRentAmenities(@Header("Authorization") String token, @Body RentAmenities rentAmenities);

    @POST("/api/rentGalery-add")
    Single<ResponseResult> addRentGalery(@Header("Authorization") String token, @Body RequestBody requestBody);

    //------------------- COMMENT ---------------------//

    @GET("/api/comments")
    Single<List<Comment>> getComment(@Header("Authorization") String token, @Query("value") String value);


    //------------------- MODO DE RENTA ---------------------//

    @GET("/api/rentsMode")
    Single<List<RentMode>> getRentsMode(@Header("Authorization") String token, @Query("value") String value);


    @GET("/api/rentsMode-all")
    Single<List<RentMode>> getAllRentsMode(@Header("Authorization") String token);

    //------------------- ZONA DE REFERENCIA ---------------------//

    @GET("/api/referenceZones")
    Single<List<ReferenceZone>> getReferencesZone(@Header("Authorization") String token, @Query("value") String value);

    @GET("/api/referenceZones-all")
    Single<List<ReferenceZone>> getAllReferencesZone(@Header("Authorization") String token);

    //------------------- TIPO DE MONEDA ---------------------//

    @GET("/api/drawTypes")
    Single<List<DrawType>> getDrawsType(@Header("Authorization") String token, @Query("value") String value);

    //------------------- GALERIA ---------------------//

    @GET("/api/galeries")
    Single<List<Galerie>> getGaleries(@Header("Authorization") String token, @Query("value") String value);

    //------------------- IMAGEN ---------------------//

    @GET
    Single<ResponseBody> getSingleImage(@Url String url);

    //------------------- OFERTA ---------------------//

    @GET("/api/offers")
    Single<List<Offer>> getOffers(@Header("Authorization") String token, @Query("value") String value);

    //------------------- RENTA_FACILIDAD ---------------------//

    @GET("/api/rent/amenities")
    Single<List<RentAmenities>> getRentsAmenities(@Header("Authorization") String token, @Query("value") String value);

    //------------------- RENTA_TIPO_MONEDA ---------------------//

    @GET("/api/rent/drawTypes")
    Single<List<RentDrawType>> getRentsDrawType(@Header("Authorization") String token, @Query("value") String value);

    //------------------- RENTA_LUGAR_INTERES ---------------------//

    @GET("/api/rent/pois")
    Single<List<RentPoi>> getRentsPoi(@Header("Authorization") String token, @Query("value") String value);

}
