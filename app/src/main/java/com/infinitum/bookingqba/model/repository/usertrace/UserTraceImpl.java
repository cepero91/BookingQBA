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
import com.infinitum.bookingqba.model.local.entity.WishedRentEntity;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;

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
    public Single<OperationResult> traceRentWishedToServer(String token) {
        if (sharedPreferences.getBoolean(USER_IS_AUTH, false)) {
            return qbaDao.getAllWishedRents(sharedPreferences.getString(USER_ID, ""))
                    .map(this::tranformEntityToRentWished)
                    .flatMap(rentWishedResource -> sendWishedToServer(token, rentWishedResource))
                    .map(responseResultResource -> {
                        if (responseResultResource.data != null && responseResultResource.data.getCode() == 200) {
                            return OperationResult.success();
                        } else {
                            return OperationResult.error(responseResultResource.message);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .onErrorReturn(OperationResult::error);
        } else {
            return Single.just(OperationResult.error("Se enviaran las Rentas deseadas una vez autenticado el usuario"));
        }
    }

    @Override
    public Single<OperationResult> traceVisitCountToServer(String token) {
        return qbaDao.getAllRentVisitCount()
                .subscribeOn(Schedulers.io())
                .map(this::tranformEntityToRentVisitCountGroup)
                .flatMap(rentVisitCountGroupResource -> sendVisitToServer(token, rentVisitCountGroupResource))
                .onErrorReturn(Resource::error)
                .flatMapCompletable(this::removeVisitCount)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    @Override
    public Single<OperationResult> traceCommentToServer(String token) {
        return qbaDao.getAllInactiveComment()
                .subscribeOn(Schedulers.io())
                .map(this::tranformEntityToCommentGroup)
                .flatMap(commentGroupResource -> sendCommentToServer(token, commentGroupResource))
                .onErrorReturn(Resource::error)
                .flatMapCompletable(this::removeInactiveComment)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    @Override
    public Single<OperationResult> traceRatingToServer(String token) {
        return qbaDao.getAllRating()
                .map(this::tranformEntityToRatingVoteGroup)
                .flatMap(ratingVoteGroupResource -> sendRatingVoteToServer(token, ratingVoteGroupResource))
                .subscribeOn(Schedulers.io())
                .onErrorReturn(Resource::error)
                .flatMapCompletable(this::updateRatingVotes)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    private Completable removeVisitCount(Resource<ResponseResult> resultResource) {
        if (resultResource.data != null && resultResource.data.getCode() == 200) {
            return Completable.fromAction(() -> qbaDao.deleteAllVisitoCount()).subscribeOn(Schedulers.io());
        } else {
            return Completable.error(new ResponseResultException(resultResource.message));
        }
    }

    private Completable updateRatingVotes(Resource<ResponseResult> resultResource) {
        if (resultResource.data != null && resultResource.data.getCode() == 200) {
            return Completable.fromAction(() -> qbaDao.updateAllRatingVersionToOne()).subscribeOn(Schedulers.io());
        } else {
            return Completable.error(new ResponseResultException(resultResource.message));
        }
    }

    private Completable removeInactiveComment(Resource<ResponseResult> resultResource) {
        if (resultResource.data != null && resultResource.data.getCode() == 200) {
            return Completable.fromAction(() -> qbaDao.deleteAllInactiveComment());
        } else {
            return Completable.error(new ResponseResultException(resultResource.message));
        }
    }

    @Override
    public Single<OperationResult> removeHistory() {
        return Completable.fromAction(() -> qbaDao.deleteAllVisitoCount())
                .andThen(Completable.fromAction(() -> qbaDao.deleteAllInactiveComment()))
                .andThen(Completable.fromAction(() -> qbaDao.deleteAllRatingVotes()))
                .subscribeOn(Schedulers.io())
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    private Single<Resource<ResponseResult>> sendWishedToServer(String token, Resource<RentWished> rentWishedResource) {
        if (rentWishedResource.data != null) {
            return retrofit.create(ApiInterface.class)
                    .updateRentWished(token, rentWishedResource.data)
                    .subscribeOn(Schedulers.io())
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        } else {
            return Single.just(Resource.error(rentWishedResource.message));
        }
    }

    private Single<Resource<ResponseResult>> sendRatingVoteToServer(String token, Resource<RatingVoteGroup> ratingVoteGroupResource) {
        if (ratingVoteGroupResource.data != null) {
            return retrofit.create(ApiInterface.class)
                    .uploadRatingVotes(token, ratingVoteGroupResource.data)
                    .map(Resource::success)
                    .subscribeOn(Schedulers.io())
                    .onErrorReturn(Resource::error);
        } else {
            return Single.just(Resource.error(ratingVoteGroupResource.message));
        }
    }

    private Single<Resource<ResponseResult>> sendCommentToServer(String token, Resource<CommentGroup> commentGroupResource) {
        if (commentGroupResource.data != null) {
            return retrofit.create(ApiInterface.class)
                    .updateRentComment(token, commentGroupResource.data)
                    .subscribeOn(Schedulers.io())
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        } else {
            return Single.just(Resource.error(commentGroupResource.message));
        }
    }

    private Single<Resource<ResponseResult>> sendVisitToServer(String token, Resource<RentVisitCountGroup> rentVisitCountGroupResource) {
        if (rentVisitCountGroupResource.data != null) {
            return retrofit.create(ApiInterface.class)
                    .updateRentVisitCount(token, rentVisitCountGroupResource.data)
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
            return Resource.error("No exite UNIVERSAL ID en este dispositivo");
        } else {
            RentVisitCountGroup rentVisitCountGroup = new RentVisitCountGroup(uniqueDeviceID);
            if (rentVisitCountEntities.size() > 0) {
                for (RentVisitCountEntity entity : rentVisitCountEntities) {
                    rentVisitCountGroup.addRentVisitCount(new RentVisitCount(entity.getRentId(), entity.getVisitCount()));
                }
                return Resource.success(rentVisitCountGroup);
            } else {
                return Resource.error(new EmptyResultSetException("Datos nulos o vacios"));
            }
        }

    }

    private Resource<RentWished> tranformEntityToRentWished(List<WishedRentEntity> entities) {
        String userId = sharedPreferences.getString(USER_ID, "");
        if (!sharedPreferences.getBoolean(USER_IS_AUTH, false)) {
            return Resource.error("Rentas deseadas seran validas una vez autenticado el usuario");
        } else {
            RentWished rentWished = new RentWished(userId, new ArrayList<>());
            if (entities.size() > 0) {
                for (WishedRentEntity entity : entities) {
                    rentWished.addRentId(entity.getRent());
                }
            }
            return Resource.success(rentWished);
        }

    }

    private Resource<RatingVoteGroup> tranformEntityToRatingVoteGroup(List<RatingEntity> entities) {
        RatingVoteGroup ratingVoteGroup = new RatingVoteGroup();
        if (entities.size() > 0) {
            for (RatingEntity entity : entities) {
                ratingVoteGroup.addRatingVote(new RatingVote(entity.getRent(), entity.getRating(), entity.getComment(), entity.getUserId()));
            }
            return Resource.success(ratingVoteGroup);
        } else {
            return Resource.error(new EmptyResultSetException("Datos nulos o vacios"));
        }

    }

    private Resource<CommentGroup> tranformEntityToCommentGroup(List<CommentEntity> entities) {
        CommentGroup commentGroup = new CommentGroup();
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
                comment.setOwner(entity.isOwner());
                commentGroup.addComment(comment);
            }
            return Resource.success(commentGroup);
        } else {
            return Resource.error(new EmptyResultSetException("Datos nulos o vacios"));
        }
    }


}
