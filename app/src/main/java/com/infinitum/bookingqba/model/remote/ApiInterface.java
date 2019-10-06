package com.infinitum.bookingqba.model.remote;


import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.AddressResponse;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.BlockDay;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.model.remote.pojo.BookRequestInfo;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.CommentGroup;
import com.infinitum.bookingqba.model.remote.pojo.DatabaseUpdate;
import com.infinitum.bookingqba.model.remote.pojo.DisabledDays;
import com.infinitum.bookingqba.model.remote.pojo.DrawChange;
import com.infinitum.bookingqba.model.remote.pojo.DrawType;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;
import com.infinitum.bookingqba.model.remote.pojo.Offer;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.model.remote.pojo.PoiType;
import com.infinitum.bookingqba.model.remote.pojo.Province;
import com.infinitum.bookingqba.model.remote.pojo.RatingVote;
import com.infinitum.bookingqba.model.remote.pojo.RatingVoteGroup;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;
import com.infinitum.bookingqba.model.remote.pojo.RemovedList;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentDrawType;
import com.infinitum.bookingqba.model.remote.pojo.RentEdit;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.RentPoi;
import com.infinitum.bookingqba.model.remote.pojo.RentPoiAdd;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCountGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentWished;
import com.infinitum.bookingqba.model.remote.pojo.Reservation;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.SyncData;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.remote.pojo.UserEsentialData;

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

    //-------------------- sync data --------------------------------//

    @GET("/api/sync-data")
    Single<SyncData> syncData(@Query("value") String value);

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

    @GET("/api/pending-reservation/")
    Single<List<Reservation>> pendingReservation(@Header("Authorization") String token, @Query("userId") String userid);

    @GET("/api/user-book-request-info/")
    Single<List<BookRequestInfo>> bookRequestInfo(@Header("Authorization") String token, @Query("userId") String userid);

    @GET("/api/accepted-reservation/")
    Single<List<Reservation>> acceptedReservation(@Header("Authorization") String token, @Query("userId") String userid);

    @FormUrlEncoded
    @POST("/api/accept-reservation/")
    Single<ResponseResult> acceptReservation(@Header("Authorization") String token, @Field("uui") String uuid);

    @FormUrlEncoded
    @POST("/api/deny-reservation/")
    Single<ResponseResult> deniedReservation(@Header("Authorization") String token, @Field("uui") String uuid);

    @FormUrlEncoded
    @POST("/api/deny-reservation-user/")
    Single<ResponseResult> deniedReservationByUser(@Header("Authorization") String token, @Field("uui") String uuid);

    @GET("/api/user-data/")
    Single<UserEsentialData> userBookOwnerData(@Header("Authorization") String token, @Query("userId") String userid, @Query("rentId") String rentId);

    //------------------- RENTAS EN LISTA DE DESEO ---------------------------//

    @POST("/update-rent-wished/")
    Single<ResponseResult> updateRentWished(@Header("Authorization") String token, @Body RentWished rentWished);

    // ------------------ VOTACIONES DE ESTRELLAS DE LOS USUARIOS ------------//

    @POST("/update-rent-rating/")
    Single<ResponseResult> uploadRatingVotes(@Header("Authorization") String token, @Body RatingVoteGroup ratingVoteGroup);

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

    @GET("/api/rentby-id")
    Single<List<RentEdit>> rentById(@Header("Authorization") String token, @Query("uuid") String value);

    @GET
    Single<Response<AddressResponse>> addressByLocationOSM(@Url String url);

    @GET("/api/user-rents")
    Flowable<List<RentEsential>> allRentByUserId(@Header("Authorization") String token, @Query("userid") String value);

    @GET("/api/rents")
    Single<List<Rent>> getRents(@Header("Authorization") String token, @Query("value") String value);

    @POST("/api/rents-add")
    Single<ResponseResult> addRent(@Header("Authorization") String token, @Body Rent rent);

    @POST("/api/rentAmenities-add")
    Single<ResponseResult> addRentAmenities(@Header("Authorization") String token, @Body RentAmenities rentAmenities);

    @POST("/api/rentOffer-add")
    Single<ResponseResult> addRentOffer(@Header("Authorization") String token, @Body List<Offer> offers);

    @POST("/api/rentPoi-add")
    Single<ResponseResult> addRentPoi(@Header("Authorization") String token, @Body RentPoiAdd rentPoiAdd);

    @POST("/api/rentGalery-add")
    Single<ResponseResult> addRentGalery(@Header("Authorization") String token, @Body RequestBody requestBody);

    @POST("/api/bookrequest-add")
    Single<ResponseResult> sendBookRequest(@Header("Authorization") String token, @Body BookRequest bookRequest);

    @POST("/api/rating-add")
    Single<ResponseResult> sendRating(@Header("Authorization") String token, @Body RatingVote ratingVote);

    @GET("/api/drawchange-by-price")
    Single<List<DrawChange>> drawChangeByFinalPrice(@Header("Authorization") String token, @Query("value") double value);

    @GET("/api/disabled-dates")
    Single<DisabledDays> disabledDaysByRent(@Header("Authorization") String token, @Query("rentId") String value);

    @POST("/api/block-dates")
    Single<ResponseResult> blockDates(@Header("Authorization") String token, @Body BlockDay value);

    //------------------- COMMENT ---------------------//

    @GET("/api/comments")
    Single<List<Comment>> getComment(@Header("Authorization") String token, @Query("value") String value);

    @POST("/api/comments-add")
    Single<ResponseResult> sendComment(@Header("Authorization") String token, @Body Comment comment);

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
