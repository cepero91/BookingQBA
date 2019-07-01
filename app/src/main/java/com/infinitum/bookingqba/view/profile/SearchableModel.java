package com.infinitum.bookingqba.view.profile;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchableModel implements Searchable {

    private String title;

    public SearchableModel(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public SearchableModel setTitle(String title) {
        this.title = title;
        return this;
    }
}
