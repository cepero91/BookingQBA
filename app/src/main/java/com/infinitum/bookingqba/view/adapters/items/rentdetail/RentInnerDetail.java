package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RentInnerDetail implements Parcelable {

    private String id;
    private String name;
    private String description;
    private String address;
    private String email;
    private String homePhone;
    private String personalPhone;
    private double price;
    private float rating;
    private int votes;
    private String rules;
    private String rentMode;
    private int maxBeds;
    private int maxBaths;
    private int capability;
    private int maxRooms;
    private double latitude;
    private double longitude;
    private int wished;
    private String checkin;
    private String chekout;
    private ArrayList<RentAmenitieItem> amenitieItems;
    private ArrayList<RentGalerieItem> galerieItems;
    private Map<PoiCategory,List<RentPoiItem>> poiItemMap;

    public RentInnerDetail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getPersonalPhone() {
        return personalPhone;
    }

    public void setPersonalPhone(String personalPhone) {
        this.personalPhone = personalPhone;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public int getMaxBeds() {
        return maxBeds;
    }

    public void setMaxBeds(int maxBeds) {
        this.maxBeds = maxBeds;
    }

    public int getMaxBaths() {
        return maxBaths;
    }

    public void setMaxBaths(int maxBaths) {
        this.maxBaths = maxBaths;
    }

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public int getMaxRooms() {
        return maxRooms;
    }

    public void setMaxRooms(int maxRooms) {
        this.maxRooms = maxRooms;
    }

    public ArrayList<RentAmenitieItem> getAmenitieItems() {
        return amenitieItems;
    }

    public void setAmenitieItems(ArrayList<RentAmenitieItem> amenitieItems) {
        this.amenitieItems = amenitieItems;
    }

    public ArrayList<RentGalerieItem> getGalerieItems() {
        return galerieItems;
    }

    public void setGalerieItems(ArrayList<RentGalerieItem> galerieItems) {
        this.galerieItems = galerieItems;
    }

    public Map<PoiCategory, List<RentPoiItem>> getPoiItemMap() {
        return poiItemMap;
    }

    public void setPoiItemMap(Map<PoiCategory, List<RentPoiItem>> poiItemMap) {
        this.poiItemMap = poiItemMap;
    }

    public int galerieSize() {
        return galerieItems.size();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String firstImage() {
        return galerieItems.get(0).getImage();
    }

    public int getWished() {
        return wished;
    }

    public void setWished(int wished) {
        this.wished = wished;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getChekout() {
        return chekout;
    }

    public void setChekout(String chekout) {
        this.chekout = chekout;
    }

    public String humanChekin(){
        SimpleDateFormat formatIn = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
             date = formatIn.parse(checkin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatOut = new SimpleDateFormat("HH:mm a");
        return formatOut.format(date);
    }

    public String humanChekout(){
        SimpleDateFormat formatIn = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = formatIn.parse(chekout);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatOut = new SimpleDateFormat("HH:mm a");
        return formatOut.format(date);
    }

    public String humanVotes() {
        if (votes > 0) {
            return String.format("(%s voto%s)", votes, votes > 1 ? "s" : "");
        } else {
            return "(sin valoración)";
        }
    }

    public String humanCapability() {
        return String.format("%s Huesp.", capability);
    }

    public String humanRoom() {
        return String.format("%s Cuarto%s", maxRooms, maxRooms > 1 ? "s" : "");
    }

    public String humanBed() {
        return String.format("%s Cama%s", maxBeds, maxBeds > 1 ? "s" : "");
    }

    public String humanBath() {
        return String.format("%s Baño%s", maxBaths, maxBaths > 1 ? "s" : "");
    }

    public String humanPrice(){
        return String.format("%.2f CUC/%s",price,rentMode);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.address);
        dest.writeString(this.email);
        dest.writeString(this.homePhone);
        dest.writeString(this.personalPhone);
        dest.writeDouble(this.price);
        dest.writeFloat(this.rating);
        dest.writeInt(this.votes);
        dest.writeString(this.rules);
        dest.writeString(this.rentMode);
        dest.writeInt(this.maxBeds);
        dest.writeInt(this.maxBaths);
        dest.writeInt(this.capability);
        dest.writeInt(this.maxRooms);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.wished);
        dest.writeString(this.checkin);
        dest.writeString(this.chekout);
        dest.writeTypedList(this.amenitieItems);
        dest.writeTypedList(this.galerieItems);
        dest.writeInt(this.poiItemMap.size());
        for (Map.Entry<PoiCategory, List<RentPoiItem>> entry : this.poiItemMap.entrySet()) {
            dest.writeParcelable(entry.getKey(), flags);
            dest.writeTypedList(entry.getValue());
        }
    }

    protected RentInnerDetail(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.address = in.readString();
        this.email = in.readString();
        this.homePhone = in.readString();
        this.personalPhone = in.readString();
        this.price = in.readDouble();
        this.rating = in.readFloat();
        this.votes = in.readInt();
        this.rules = in.readString();
        this.rentMode = in.readString();
        this.maxBeds = in.readInt();
        this.maxBaths = in.readInt();
        this.capability = in.readInt();
        this.maxRooms = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.wished = in.readInt();
        this.checkin = in.readString();
        this.chekout = in.readString();
        this.amenitieItems = in.createTypedArrayList(RentAmenitieItem.CREATOR);
        this.galerieItems = in.createTypedArrayList(RentGalerieItem.CREATOR);
        int poiItemMapSize = in.readInt();
        this.poiItemMap = new HashMap<PoiCategory, List<RentPoiItem>>(poiItemMapSize);
        for (int i = 0; i < poiItemMapSize; i++) {
            PoiCategory key = in.readParcelable(PoiCategory.class.getClassLoader());
            List<RentPoiItem> value = in.createTypedArrayList(RentPoiItem.CREATOR);
            this.poiItemMap.put(key, value);
        }
    }

    public static final Creator<RentInnerDetail> CREATOR = new Creator<RentInnerDetail>() {
        @Override
        public RentInnerDetail createFromParcel(Parcel source) {
            return new RentInnerDetail(source);
        }

        @Override
        public RentInnerDetail[] newArray(int size) {
            return new RentInnerDetail[size];
        }
    };
}
