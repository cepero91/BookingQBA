package com.infinitum.bookingqba.util;

public final class Constants {

    //API
    public static final String BASIC_URL_API = "http://192.168.137.1:8000";

    //SHARED PREFERENCES KEY
    public static final String PREF_ENTITY_DOWNLOAD_INDEX = "dbDownloadLevel";
    public static final String PREF_DOWNLOAD_LEVEL = "downloadLevel";
    public static final String PREF_DOWNLOAD_SUCCESS = "downloadSuccess";
    public static final String PREF_FIRST_OPEN = "firstOpen";

    //FILES
    public static final String ROOT_GALERY_FOLDER_NAME = "galery";

    //DATABASE
    public static final String DATABASE_NAME = "bookingQBA.db";
    public static final String PROVINCE_TABLE_NAME = "Province";
    public static final String MUNICIPALITY_TABLE_NAME = "Municipality";
    public static final String AMENITIES_TABLE_NAME = "Amenities";
    public static final String DRAW_TYPE_TABLE_NAME = "DrawType";
    public static final String POI_TYPE_TABLE_NAME = "PoiType";
    public static final String POI_TABLE_NAME = "Poi";
    public static final String CHANGE_RATE_TABLE_NAME = "ChangeRate";
    public static final String RENT_MODE_TABLE_NAME = "RentMode";
    public static final String RENT_TABLE_NAME = "Rent";
    public static final String GALERIE_TABLE_NAME = "Galerie";
    public static final String REFERENCE_ZONE_TABLE_NAME = "ReferenceZone";
    public static final String OFFER_TABLE_NAME = "Offer";
    public static final String RENT_AMENITIES_TABLE_NAME = "RentAmenities";
    public static final String RENT_DRAW_TYPE_TABLE_NAME = "RentDrawType";
    public static final String RENT_POI_TABLE_NAME = "RentPoi";
    public static final String RENT_VISIT_COUNT_TABLE_NAME = "RentVisitCount";

    //PERMISSION
    public static final String PERMISSION = "permission";
    public static final String IMEI = "imei";
    public static final int PERMISSION_GRANTED = 1;
    public static final int PERMISSION_NOT_GRANTED = -1;


    //NAVIGATION
    public static final String PROVINCE_UUID = "province";
    public static final String PROVINCE_UUID_DEFAULT = "cfa7f0d7-2b67-4245-9157-48ba2117f63c";
    public static final char ORDER_TYPE_POPULAR = 'r'; // Represent attrib rating
    public static final char ORDER_TYPE_NEW = 'c'; // Represent attrib created


    //USER LOGIN
    public static final String USER_IS_AUTH = "userIsAuth";
    public static final String USER_NAME = "userName";
    public static final String USER_TOKEN = "userToken";
    public static final String USER_RENTS = "userRents";

    public static final String MAP_PATH = "mapDir";

}
