package com.infinitum.bookingqba.view.profile.uploaditem;

public class GaleryFormObject {

    private String uuid;
    private String url;
    private boolean remote;

    public GaleryFormObject(String uuid) {
        this.uuid = uuid;
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

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }
}
