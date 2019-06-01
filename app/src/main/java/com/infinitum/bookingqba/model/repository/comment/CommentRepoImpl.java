package com.infinitum.bookingqba.model.repository.comment;

import android.util.Base64;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.CommentEntity;
import com.infinitum.bookingqba.model.local.tconverter.CommentEmotion;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CommentRepoImpl implements CommentRepository {

    private BookingQBADao qbaDao;
    private Retrofit retrofit;

    @Inject
    public CommentRepoImpl(BookingQBADao qbaDao, Retrofit retrofit) {
        this.qbaDao = qbaDao;
        this.retrofit = retrofit;
    }

    @Override
    public Single<List<CommentEntity>> fetchRemoteAndTransform(String dateValue) {
        return retrofit.create(ApiInterface.class)
                .getComment(dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    private List<CommentEntity> parseGsonToEntity(List<Comment> comments) {
        ArrayList<CommentEntity> listEntity = new ArrayList<>();
        CommentEntity entity;
        for (Comment item : comments) {
            entity = new CommentEntity(item.getId(), item.getUsername(), item.getDescription(),item.getRent(), item.getUserid());
            entity.setAvatar(Base64.decode(item.getAvatar(), Base64.DEFAULT));
            entity.setOwner(item.isIs_owner());
            entity.setCreated(DateUtils.dateStringToDate(item.getCreated()));
            entity.setActive(item.isActive());
            entity.setEmotion(CommentEmotion.fromLevel(item.getEmotion()));
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Completable insert(List<CommentEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertComment(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeComment(String dateValue) {
        return fetchRemoteAndTransform(dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    @Override
    public Flowable<Resource<List<CommentEntity>>> allComment() {
        return qbaDao.getAllComment()
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }
}
