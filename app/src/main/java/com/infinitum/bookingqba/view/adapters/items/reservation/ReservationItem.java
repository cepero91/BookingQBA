package com.infinitum.bookingqba.view.adapters.items.reservation;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ReservationItem implements Parcelable {

    private String id;
    private Date startDate;
    private Date endDate;
    private Date created;
    private String userName;
    private int capability;
    private int hostCount;
    private String userAvatar;
    private String userId;
    private String rentId;
    private String rentName;
    private String aditionalNote;

    public ReservationItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public int getHostCount() {
        return hostCount;
    }

    public void setHostCount(int hostCount) {
        this.hostCount = hostCount;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public String getRentName() {
        return rentName;
    }

    public void setRentName(String rentName) {
        this.rentName = rentName;
    }

    public String getAditionalNote() {
        return aditionalNote;
    }

    public void setAditionalNote(String aditionalNote) {
        this.aditionalNote = aditionalNote;
    }

    public String humanCapability() {
        return capability > 1 ? String.format("%s Huespedes", capability) : String.format("%s Huesped");
    }

    public String humanShortCapability() {
        return String.valueOf(capability);
    }

    public String humanHostCount() {
        return String.valueOf(hostCount);
    }

    public String humanDayFrom() {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(startDate);
    }

    public String humanMonthFrom() {
        SimpleDateFormat format = new SimpleDateFormat("MMM");
        return format.format(startDate);
    }

    public String humanWeekDayFrom() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        return format.format(startDate);
    }

    public String humanDayUntil() {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(endDate);
    }

    public String humanMonthUntil() {
        SimpleDateFormat format = new SimpleDateFormat("MMM");
        return format.format(endDate);
    }

    public String humanWeekDayUntil() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        return format.format(endDate);
    }

    public String humanMediumDate() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(startDate) + "--" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(endDate);
    }

    public String humanShortDate() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(startDate) + " -- " + DateFormat.getDateInstance(DateFormat.SHORT).format(endDate);
    }

    public String humanRelativeTime() {
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build();
        return TimeAgo.using(endDate.getTime(), messages);
    }

    public String humanCreated() {
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build();
        return TimeAgo.using(created.getTime(), messages);
    }

    public String humanNightCount() {
        long nightCountInMillis = endDate.getTime() - startDate.getTime();
        int nightCount = (int) TimeUnit.DAYS.convert(nightCountInMillis, TimeUnit.MILLISECONDS);
        return String.format("%s noche%s", nightCount, nightCount > 1 ? "s" : "");
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.startDate != null ? this.startDate.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeString(this.userName);
        dest.writeInt(this.capability);
        dest.writeInt(this.hostCount);
        dest.writeString(this.userAvatar);
        dest.writeString(this.userId);
        dest.writeString(this.rentId);
        dest.writeString(this.rentName);
        dest.writeString(this.aditionalNote);
    }

    protected ReservationItem(Parcel in) {
        this.id = in.readString();
        long tmpStartDate = in.readLong();
        this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.userName = in.readString();
        this.capability = in.readInt();
        this.hostCount = in.readInt();
        this.userAvatar = in.readString();
        this.userId = in.readString();
        this.rentId = in.readString();
        this.rentName = in.readString();
        this.aditionalNote = in.readString();
    }

    public static final Creator<ReservationItem> CREATOR = new Creator<ReservationItem>() {
        @Override
        public ReservationItem createFromParcel(Parcel source) {
            return new ReservationItem(source);
        }

        @Override
        public ReservationItem[] newArray(int size) {
            return new ReservationItem[size];
        }
    };
}
