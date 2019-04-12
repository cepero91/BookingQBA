package com.infinitum.bookingqba.model.local.pojo;

public class GaleryUpdateUtil {

    private String uuid;
    private String imageLocalPath;

    public GaleryUpdateUtil(String uuid, String imageLocalPath) {
        this.uuid = uuid;
        this.imageLocalPath = imageLocalPath;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }
}
