package com.infinitum.bookingqba.view.adapters.items.reservation;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.infinitum.bookingqba.model.remote.ReservationType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookInfoItem {

    private String id;
    private Date startDate;
    private Date endDate;
    private Date created;
    private String userName;
    private int capability;
    private int hostCount;
    private String userId;
    private String rentId;
    private String rentName;
    private String aditional;
    private String rentAvatar;
    private ReservationType state;

    public BookInfoItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public String getRentAvatar() {
        return rentAvatar;
    }

    public void setRentAvatar(String rentAvatar) {
        this.rentAvatar = rentAvatar;
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

    public String getAditional() {
        return aditional;
    }

    public void setAditional(String aditional) {
        this.aditional = aditional;
    }

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public ReservationType getState() {
        return state;
    }

    public void setState(ReservationType state) {
        this.state = state;
    }

    public void setRawState(int state) {
        switch (state) {
            case 0:
                this.state = ReservationType.PENDING;
                break;
            case 1:
                this.state = ReservationType.CHECKED;
                break;
            case 2:
                this.state = ReservationType.ACCEPTED;
                break;
            case 3:
                this.state = ReservationType.DENIED;
                break;
            case 4:
                this.state = ReservationType.OCUPATE;
                break;
            default:
                this.state = ReservationType.PENDING;
                break;
        }
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

    public int getHostCount() {
        return hostCount;
    }

    public void setHostCount(int hostCount) {
        this.hostCount = hostCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRentName() {
        return rentName;
    }

    public void setRentName(String rentName) {
        this.rentName = rentName;
    }


    public String humanNightCount() {
        long nightCountInMillis = endDate.getTime() - startDate.getTime();
        int nightCount = (int) TimeUnit.DAYS.convert(nightCountInMillis, TimeUnit.MILLISECONDS);
        return String.format("%s noche%s", nightCount, nightCount > 1 ? "s" : "");
    }

    public String humanCreated() {
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build();
        return TimeAgo.using(created.getTime(), messages);
    }

    public String humanRelativeTime() {
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build();
        return TimeAgo.using(endDate.getTime(), messages);
    }

    public String humanHostCount() {
        return String.valueOf(hostCount);
    }

    public String humanShortDate() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(startDate) + " -- " + DateFormat.getDateInstance(DateFormat.SHORT).format(endDate);
    }

    public String humanCapability() {
        return capability > 1 ? String.format("%s Huespedes", capability) : String.format("%s Huesped");
    }

    public String humanDayFrom(){
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(startDate);
    }

    public String humanMonthFrom(){
        SimpleDateFormat format = new SimpleDateFormat("MMM");
        return format.format(startDate);
    }

    public String humanWeekDayFrom(){
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        return format.format(startDate);
    }

    public String humanDayUntil(){
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(endDate);
    }

    public String humanMonthUntil(){
        SimpleDateFormat format = new SimpleDateFormat("MMM");
        return format.format(endDate);
    }

    public String humanWeekDayUntil(){
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        return format.format(endDate);
    }

}
