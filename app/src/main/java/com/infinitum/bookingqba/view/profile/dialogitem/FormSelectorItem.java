package com.infinitum.bookingqba.view.profile.dialogitem;

public class FormSelectorItem {

    private String uuid;
    private String title;

    public FormSelectorItem(String uuid, String title) {
        this.uuid = uuid;
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
