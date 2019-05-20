package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import com.infinitum.bookingqba.model.local.entity.RentEntity;

import java.util.ArrayList;

public class RentItem {

    private RentInnerDetail rentInnerDetail;

    private ArrayList<RentCommentItem> commentItems;

    private ArrayList<RentOfferItem> offerItems;

    public RentItem() {
    }


    public RentInnerDetail getRentInnerDetail() {
        return rentInnerDetail;
    }

    public void setRentInnerDetail(RentInnerDetail rentInnerDetail) {
        this.rentInnerDetail = rentInnerDetail;
    }

    public ArrayList<RentCommentItem> getCommentItems() {
        return commentItems;
    }

    public void setCommentItems(ArrayList<RentCommentItem> commentItems) {
        this.commentItems = commentItems;
    }

    public ArrayList<RentOfferItem> getOfferItems() {
        return offerItems;
    }

    public void setOfferItems(ArrayList<RentOfferItem> offerItems) {
        this.offerItems = offerItems;
    }
}
