package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CommentGroup {

    @SerializedName("imei")
    @Expose
    private String imei;

    @SerializedName("comments")
    @Expose
    private List<Comment> comments;

    public CommentGroup(String imei) {
        this.imei = imei;
        comments = new ArrayList<>();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }
}
