package com.infinitum.bookingqba.model.remote;


import com.infinitum.bookingqba.model.remote.pojo.Amenities;
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

import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
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

    @GET("secret")
    Call<String> getSecret(@Header("Authorization") String token);


    //------------------- RENTAS EN LISTA DE DESEO ---------------------------//

    @POST("/update-rent-wished/")
    Single<ResponseResult> updateRentWished(@Body RentWished rentWished);

    // ------------------ VOTACIONES DE ESTRELLAS DE LOS USUARIOS ------------//

    @POST("/update-rent-rating/")
    Single<ResponseResult> updateRatingVotes(@Body RatingVoteGroup ratingVoteGroup);

    // ------------------ COMENTARIOS DE USUARIOS ------------//

    @POST("/update-rent-comment/")
    Single<ResponseResult> updateRentComment(@Body CommentGroup commentGroup);


    //------------------- CONTADOR DE VISITAS ---------------------------//

    @POST("/update-visit-count/")
    Single<ResponseResult> updateRentVisitCount(@Body RentVisitCountGroup rentVisitCountGroup);


    //------------------- PROVINCIAS ---------------------//
    /**
     * Obtener provincias
     * @return lista de provincias
     */
    @GET("/api/provinces")
    Single<List<Province>> getProvinces(@Query("value") String value);

    //------------------- MUNICIPIOS ---------------------//

    @GET("/api/municipalities")
    Single<List<Municipality>> getMunicipality(@Query("value") String value);

    //------------------- FACILIDADES ---------------------//

    @GET("/api/amenities")
    Single<List<Amenities>> getAmenities(@Query("value") String value);

    //------------------- TIPO DE LUGAR DE INTERES ---------------------//

    @GET("/api/poiTypes")
    Single<List<PoiType>> getPoiTypes(@Query("value") String value);

    //------------------- LUGAR DE INTERES ---------------------//

    @GET("/api/pois")
    Single<List<Poi>> getPois(@Query("value") String value);

    //------------------- RENTAS ---------------------//

    @GET("/api/rents")
    Single<List<Rent>> getRents(@Query("value") String value);

    //------------------- COMMENT ---------------------//

    @GET("/api/comments")
    Single<List<Comment>> getComment(@Query("value") String value);


    //------------------- MODO DE RENTA ---------------------//

    @GET("/api/rentsMode")
    Single<List<RentMode>> getRentsMode(@Query("value") String value);

    //------------------- ZONA DE REFERENCIA ---------------------//

    @GET("/api/referenceZones")
    Single<List<ReferenceZone>> getReferencesZone(@Query("value") String value);

    //------------------- TIPO DE MONEDA ---------------------//

    @GET("/api/drawTypes")
    Single<List<DrawType>> getDrawsType(@Query("value") String value);

    //------------------- GALERIA ---------------------//

    @GET("/api/galeries")
    Single<List<Galerie>> getGaleries(@Query("value") String value);

    //------------------- IMAGEN ---------------------//

    @GET
    Single<ResponseBody> getSingleImage(@Url String url);

    //------------------- OFERTA ---------------------//

    @GET("/api/offers")
    Single<List<Offer>> getOffers(@Query("value") String value);

    //------------------- RENTA_FACILIDAD ---------------------//

    @GET("/api/rent/amenities")
    Single<List<RentAmenities>> getRentsAmenities(@Query("value") String value);

    //------------------- RENTA_TIPO_MONEDA ---------------------//

    @GET("/api/rent/drawTypes")
    Single<List<RentDrawType>> getRentsDrawType(@Query("value") String value);

    //------------------- RENTA_LUGAR_INTERES ---------------------//

    @GET("/api/rent/pois")
    Single<List<RentPoi>> getRentsPoi(@Query("value") String value);

}
