package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("autor")
    @Expose
    private String autor;

    @SerializedName("bodyMessage")
    @Expose
    private String bodyMessage;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public Comment(String id, String autor, String bodyMessage, String createdAt) {
        this.id = id;
        this.autor = autor;
        this.bodyMessage = bodyMessage;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public void setBodyMessage(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
