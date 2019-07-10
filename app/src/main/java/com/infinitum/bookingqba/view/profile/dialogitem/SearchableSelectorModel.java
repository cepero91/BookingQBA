package com.infinitum.bookingqba.view.profile.dialogitem;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchableSelectorModel implements Searchable {

    private String uuid;
    private String title;

    public SearchableSelectorModel(String uuid, String title) {
        this.uuid = uuid;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SearchableSelectorModel setTitle(String title) {
        this.title = title;
        return this;
    }
}
