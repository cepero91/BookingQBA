package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.infinitum.bookingqba.model.local.tconverter.DateTypeConverter;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.infinitum.bookingqba.util.Constants.CHANGE_RATE_TABLE_NAME;

@Entity(tableName = CHANGE_RATE_TABLE_NAME, indices = {@Index("drawType")},
        foreignKeys = {@ForeignKey(entity = DrawTypeEntity.class,
                parentColumns = "id",
                childColumns = "drawType",
                onDelete = CASCADE)})
@TypeConverters({DateTypeConverter.class})
public class ChangeRateEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @NonNull
    private Date date;

    @NonNull
    private float purchase;

    @NonNull
    private float sale;

    @NonNull
    private String drawType;

    public ChangeRateEntity(@NonNull String id, @NonNull Date date, @NonNull float purchase, @NonNull float sale, @NonNull String drawType) {
        this.id = id;
        this.date = date;
        this.purchase = purchase;
        this.sale = sale;
        this.drawType = drawType;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    @NonNull
    public float getPurchase() {
        return purchase;
    }

    public void setPurchase(@NonNull float purchase) {
        this.purchase = purchase;
    }

    @NonNull
    public float getSale() {
        return sale;
    }

    public void setSale(@NonNull float sale) {
        this.sale = sale;
    }

    @NonNull
    public String getDrawType() {
        return drawType;
    }

    public void setDrawType(@NonNull String drawType) {
        this.drawType = drawType;
    }
}
