package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.CommentEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.OfferEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class RentDetail {

    @Embedded
    RentEntity rentEntity;

    @Relation(parentColumn = "referenceZone", entityColumn = "id",entity = ReferenceZoneEntity.class, projection = {"name"})
    private Set<String> referenceZoneName;

    @Relation(parentColumn = "rentMode", entityColumn = "id",entity = RentModeEntity.class,projection = {"name"})
    private Set<String> rentModeName;

    @Relation(parentColumn = "id", entityColumn = "rentId",entity = RentPoiEntity.class)
    private List<RentPoiAndRelation> rentPoiAndRelations;

    @Relation(parentColumn = "id", entityColumn = "rentId",entity = RentAmenitiesEntity.class)
    private List<RentAmenitieAndRelation> amenitieNames;

    @Relation(parentColumn = "id", entityColumn = "rent")
    private List<GalerieEntity> galeries;

    @Relation(parentColumn = "id", entityColumn = "rent")
    private List<CommentEntity> commentEntities;

    @Relation(parentColumn = "id", entityColumn = "rent")
    private List<OfferEntity> offerEntities;

    public RentDetail(RentEntity rentEntity) {
        this.rentEntity = rentEntity;
    }

    public Set<String> getRentModeName() {
        return rentModeName;
    }

    public void setRentModeName(Set<String> rentModeName) {
        this.rentModeName = rentModeName;
    }

    public Set<String> getReferenceZoneName() {
        return referenceZoneName;
    }

    public void setReferenceZoneName(Set<String> referenceZoneName) {
        this.referenceZoneName = referenceZoneName;
    }

    public List<RentAmenitieAndRelation> getAmenitieNames() {
        return amenitieNames;
    }

    public void setAmenitieNames(List<RentAmenitieAndRelation> amenitieNames) {
        this.amenitieNames = amenitieNames;
    }

    public List<GalerieEntity> getGaleries() {
        return galeries;
    }

    public void setGaleries(List<GalerieEntity> galeries) {
        this.galeries = galeries;
    }

    public RentEntity getRentEntity() {
        return rentEntity;
    }

    public void setRentEntity(RentEntity rentEntity) {
        this.rentEntity = rentEntity;
    }

    public List<RentPoiAndRelation> getRentPoiAndRelations() {
        return rentPoiAndRelations;
    }

    public void setRentPoiAndRelations(List<RentPoiAndRelation> rentPoiAndRelations) {
        this.rentPoiAndRelations = rentPoiAndRelations;
    }

    public String getRentModeNameObject(){
        return (String) rentModeName.toArray()[0];
    }

    public String getReferenceZoneNameObject(){
        return (String) referenceZoneName.toArray()[0];
    }

    public List<CommentEntity> getCommentEntities() {
        List<CommentEntity> commentActives = new ArrayList<>();
        for(CommentEntity entity: commentEntities) {
            if(entity.isActive()){
                commentActives.add(entity);
            }
        }
        Collections.sort(commentActives, (o1, o2) -> o2.getCreated().compareTo(o1.getCreated()));
        return commentActives;
    }

    public void setCommentEntities(List<CommentEntity> commentEntities) {
        this.commentEntities = commentEntities;
    }

    public List<OfferEntity> getOfferEntities() {
        return offerEntities;
    }

    public void setOfferEntities(List<OfferEntity> offerEntities) {
        this.offerEntities = offerEntities;
    }
}
