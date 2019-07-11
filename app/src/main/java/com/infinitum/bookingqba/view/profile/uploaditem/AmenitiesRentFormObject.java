package com.infinitum.bookingqba.view.profile.uploaditem;

public class AmenitiesRentFormObject {

    private String rentUuid;
    private String amenityUuid;

    public AmenitiesRentFormObject(String rentUuid, String amenityUuid) {
        this.rentUuid = rentUuid;
        this.amenityUuid = amenityUuid;
    }

    public String getRentUuid() {
        return rentUuid;
    }

    public void setRentUuid(String rentUuid) {
        this.rentUuid = rentUuid;
    }

    public String getAmenityUuid() {
        return amenityUuid;
    }

    public void setAmenityUuid(String amenityUuid) {
        this.amenityUuid = amenityUuid;
    }
}
