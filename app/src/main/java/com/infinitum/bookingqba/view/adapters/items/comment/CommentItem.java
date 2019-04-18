package com.infinitum.bookingqba.view.adapters.items.comment;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class CommentItem extends BaseItem{

    private String bodyMessage;
    private String relativeDate;

    public CommentItem(String id, String mName) {
        super(id, mName);
    }

    public CommentItem(String id, String mName, String bodyMessage, String relativeDate) {
        super(id, mName);
        this.bodyMessage = bodyMessage;
        this.relativeDate = relativeDate;
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
