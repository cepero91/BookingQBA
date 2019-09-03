package com.infinitum.bookingqba.model.repository.comment;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.CommentEntity;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CommentRepository {

    Completable insert(List<CommentEntity> entities);

    Single<Resource<ResponseResult>> send(String token, Comment comment);

    Single<OperationResult> syncronizeComment(String token, String dateValue);

    Flowable<Resource<List<CommentEntity>>> allComment();
}
