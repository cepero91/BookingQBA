package com.infinitum.bookingqba.view.profile.uploaditem;

public class GaleryFormObject {

    private String uuid;
    private String url;
    private int version;

    public GaleryFormObject() {
        this.version = 0;
    }

    public GaleryFormObject(String uuid, String url) {
        this.uuid = uuid;
        this.url = url;
        this.version = 1;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
