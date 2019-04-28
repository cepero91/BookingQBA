package com.infinitum.bookingqba.model;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

public class OperationResult {

    @NonNull
    public final Result result;

    @Nullable
    public final String message;

    public OperationResult(@NonNull Result result, @Nullable String message) {
        this.result = result;
        this.message = message;
    }

    public static OperationResult success(String message){
        return new OperationResult(Result.SUCCESS,message);
    }

    public static OperationResult success(){
        return new OperationResult(Result.SUCCESS,null);
    }

    public static OperationResult error(String message){
        return new OperationResult(Result.ERROR,message);
    }

    public static OperationResult error(Throwable throwable){
        return new OperationResult(Result.ERROR,throwable.getMessage());
    }

    @NonNull
    public Result getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return result == Result.SUCCESS?"SUCCESS "+message:"ERROR "+message;
    }

    public enum Result{
        SUCCESS,
        ERROR
    }
}
