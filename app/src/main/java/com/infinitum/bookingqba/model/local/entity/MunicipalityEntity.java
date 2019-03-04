package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static android.arch.persistence.room.ForeignKey.SET_NULL;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static com.infinitum.bookingqba.util.Constants.MUNICIPALITY_TABLE_NAME;

@Entity(tableName = MUNICIPALITY_TABLE_NAME, indices = {@Index("province")},
        foreignKeys = {@ForeignKey(entity = ProvinceEntity.class
                , parentColumns = "id",
                childColumns = "province",
                onDelete = RESTRICT)})
public class MunicipalityEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "province")
    @NonNull
    private String province;

    public MunicipalityEntity(@NonNull String id, @NonNull String name, @NonNull String province) {
        this.id = id;
        this.name = name;
        this.province = province;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getProvince() {
        return province;
    }

    public void setProvince(@NonNull String province) {
        this.province = province;
    }
}
