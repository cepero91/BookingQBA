package com.infinitum.bookingqba.view.adapters.items.comment;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class CommentItem implements ViewModel{

    private String id;
    private String title;
    private String bodyMessage;
    private String relativeDate;

    public CommentItem(String id, String title, String bodyMessage, String relativeDate) {
        this.id = id;
        this.title = title;
        this.bodyMessage = bodyMessage;
        this.relativeDate = relativeDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public void setBodyMessage(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

    public String getRelativeDate() {
        return relativeDate;
    }

    public void setRelativeDate(String relativeDate) {
        this.relativeDate = relativeDate;
    }
}
