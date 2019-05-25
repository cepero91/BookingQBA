package com.infinitum.bookingqba.model.repository.usertrace;

import android.arch.persistence.room.EmptyResultSetException;
import android.content.SharedPreferences;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.CommentEntity;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentVisitCountEntity;
import com.infinitum.bookingqba.model.local.tconverter.CommentEmotion;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.CommentGroup;
import com.infinitum.bookingqba.model.remote.pojo.RatingVote;
import com.infinitum.bookingqba.model.remote.pojo.RatingVoteGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCount;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCountGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentWished;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.errors.ResponseResultException;
import com.infinitum.bookingqba.util.DateUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.IMEI;

public class UserTraceImpl implements UserTraceRepository {

    private BookingQBADao qbaDao;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferences;

    @Inject
    public UserTraceImpl(BookingQBADao qbaDao, Retrofit retrofit, SharedPreferences sharedPreferences) {
        this.qbaDao = qbaDao;
        this.retrofit = retrofit;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Single<OperationResult> traceRentWishedToServer() {
        return qbaDao.getAllWishedRents()
                .subscribeOn(Schedulers.io())
                .map(this::tranformEntityToRentWished)
                .flatMap(this::sendWishedToServer)
                .map(responseResultResource -> {
                    if(responseResultResource.data!=null && responseResultResource.data.getCode()==200){
                        return OperationResult.success();
                    }else{
                        return OperationResult.error(responseResultResource.message);
                    }
                })
                .onErrorReturn(OperationResult::error);
    }

    @Override
    public Single<OperationResult> traceVisitCountToServer() {
        return qbaDao.getAllRentVisitCount()
                .subscribeOn(Schedulers.io())
                .map(this::tranformEntityToRentVisitCountGroup)
                .flatMap(this::sendVisitToServer)
                .onErrorReturn(Resource::error)
                .flatMapCompletable(this::removeVisitCount)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    @Override
    public Single<OperationResult> traceCommentToServer() {
        return qbaDao.getAllInactiveComment()
                .subscribeOn(Schedulers.io())
                .map(this::tranformEntityToCommentGroup)
                .flatMap(this::sendCommentToServer)
                .onErrorReturn(Resource::error)
                .flatMapCompletable(this::removeInactiveComment)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    @Override
    public Single<OperationResult> traceRatingToServer() {
        return qbaDao.getAllRating()
                .subscribeOn(Schedulers.io())
                .map(this::tranformEntityToRatingVoteGroup)
                .flatMap(this::sendRatingVoteToServer)
                .map(responseResultResource -> {
                    if(responseResultResource.data.getCode()==200){
                        return OperationResult.success();
                    }else{
                        return OperationResult.error(responseResultResource.message);
                    }
                })
                .onErrorReturn(OperationResult::error);
    }

    private Completable removeVisitCount(Resource<ResponseResult> resultResource){
        if(resultResource.data!= null && resultResource.data.getCode()==200) {
            return Completable.fromAction(() -> qbaDao.deleteAllVisitoCount());
        }else{
            return Completable.error(new ResponseResultException(resultResource.message));
        }
    }

    private Completable removeRatingVotes(Resource<ResponseResult> resultResource){
        if(resultResource.data!= null && resultResource.data.getCode()==200) {
            return Completable.fromAction(() -> qbaDao.deleteAllRatingVotes());
        }else {
            return Completable.error(new ResponseResultException(resultResource.message));
        }
    }

    private Completable removeInactiveComment(Resource<ResponseResult> resultResource){
        if(resultResource.data!= null && resultResource.data.getCode()==200) {
            return Completable.fromAction(() -> qbaDao.deleteAllInactiveComment());
        }else{
            return Completable.error(new ResponseResultException(resultResource.message));
        }
    }

    @Override
    public Single<OperationResult> removeHistory() {
        return Completable.fromAction(() -> qbaDao.deleteAllVisitoCount())
                .andThen(Completable.fromAction(()-> qbaDao.deleteAllInactiveComment()))
                .andThen(Completable.fromAction(()-> qbaDao.deleteAllRatingVotes()))
                .subscribeOn(Schedulers.io())
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    private Single<Resource<ResponseResult>> sendWishedToServer(Resource<RentWished> rentWishedResource) {
        if (rentWishedResource.data != null) {
            return retrofit.create(ApiInterface.class)
                    .updateRentWished(rentWishedResource.data)
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        } else {
            return Single.just(Resource.error(rentWishedResource.message));
        }
    }

    private Single<Resource<ResponseResult>> sendRatingVoteToServer(Resource<RatingVoteGroup> ratingVoteGroupResource) {
        if (ratingVoteGroupResource.data != null) {
            return retrofit.create(ApiInterface.class)
                    .updateRatingVotes(ratingVoteGroupResource.data)
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        } else {
            return Single.just(Resource.error(ratingVoteGroupResource.message));
        }
    }

    private Single<Resource<ResponseResult>> sendCommentToServer(Resource<CommentGroup> commentGroupResource) {
        if (commentGroupResource.data != null) {
            return retrofit.create(ApiInterface.class)
                    .updateRentComment(commentGroupResource.data)
                    .subscribeOn(Schedulers.io())
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        } else {
            return Single.just(Resource.error(commentGroupResource.message));
        }
    }

    private Single<Resource<ResponseResult>> sendVisitToServer(Resource<RentVisitCountGroup> rentVisitCountGroupResource) {
        if (rentVisitCountGroupResource.data != null) {
            return retrofit.create(ApiInterface.class)
                    .updateRentVisitCount(rentVisitCountGroupResource.data)
                    .subscribeOn(Schedulers.io())
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        } else {
            return Single.just(Resource.error(rentVisitCountGroupResource.message));
        }
    }

    private Resource<RentVisitCountGroup> tranformEntityToRentVisitCountGroup(List<RentVisitCountEntity> rentVisitCountEntities) {
        String uniqueDeviceID = sharedPreferences.getString(IMEI, "");
        if (uniqueDeviceID.equals("")) {
            return Resource.error("No exite IMEI en este dispositivo");
        } else {
            RentVisitCountGroup rentVisitCountGroup = new RentVisitCountGroup(uniqueDeviceID);
            if (rentVisitCountEntities.size() > 0) {
                for (RentVisitCountEntity entity : rentVisitCountEntities) {
                    rentVisitCountGroup.addRentVisitCount(new RentVisitCount(entity.getRentId(), entity.getVisitCount()));
                    Timber.e("VISIT COUNT to send ====> rent %s, count %s", entity.getRentId(), entity.getVisitCount());
                }
                return Resource.success(rentVisitCountGroup);
            } else {
                return Resource.error(new EmptyResultSetException("Datos nulos o vacios"));
            }
        }

    }

    private Resource<RentWished> tranformEntityToRentWished(List<RentEntity> entities) {
        String uniqueDeviceID = sharedPreferences.getString(IMEI, "");
        if (uniqueDeviceID.equals("")) {
            return Resource.error("No exite IMEI en este dispositivo");
        } else {
            RentWished rentWished = new RentWished(uniqueDeviceID);
            if (entities.size() > 0) {
                for (RentEntity entity : entities) {
                    rentWished.addRentId(entity.getId());
                    Timber.e("WHISHED to send ====> rent %s, name %s", entity.getId(), entity.getName());
                }
                return Resource.success(rentWished);
            } else {
                return Resource.error(new EmptyResultSetException("Datos nulos o vacios"));
            }
        }

    }

    private Resource<RatingVoteGroup> tranformEntityToRatingVoteGroup(List<RatingEntity> entities) {
        String uniqueDeviceID = sharedPreferences.getString(IMEI, "");
        if (uniqueDeviceID.equals("")) {
            return Resource.error("No exite IMEI en este dispositivo");
        } else {
            RatingVoteGroup ratingVoteGroup = new RatingVoteGroup(uniqueDeviceID);
            if (entities.size() > 0) {
                for (RatingEntity entity : entities) {
                    ratingVoteGroup.addRatingVote(new RatingVote(entity.getRent(), entity.getRating(), entity.getComment()));
                    Timber.e("RATING to send ====> rent %s, name %s", entity.getRent(), String.valueOf(entity.getRating()));
                }
                return Resource.success(ratingVoteGroup);
            } else {
                return Resource.error(new EmptyResultSetException("Datos nulos o vacios"));
            }
        }
    }

    private Resource<CommentGroup> tranformEntityToCommentGroup(List<CommentEntity> entities) {
        String uniqueDeviceID = sharedPreferences.getString(IMEI, "");
        if (uniqueDeviceID.equals("")) {
            return Resource.error("No exite IMEI en este dispositivo");
        } else {
            CommentGroup commentGroup = new CommentGroup(uniqueDeviceID);
            if (entities.size() > 0) {
                for (CommentEntity entity : entities) {
                    Comment comment = new Comment(entity.getId(), entity.getUsername());
                    comment.setUserid(entity.getUserid());
                    comment.setDescription(entity.getDescription());
                    comment.setRent(entity.getRent());
                    comment.setCreated(DateUtils.parseDateToString(entity.getCreated()));
                    comment.setAvatar("");
                    comment.setActive(false);
                    comment.setEmotion(CommentEmotion.fromEmotion(entity.getEmotion()));
                    comment.setIs_owner(entity.isOwner());
                    commentGroup.addComment(comment);
                    Timber.e("COMMENT to send ====> rent %s, name %s", entity.getUsername(), entity.getDescription());
                }
                return Resource.success(commentGroup);
            } else {
                return Resource.error(new EmptyResultSetException("Datos nulos o vacios"));
            }
        }

    }


}
