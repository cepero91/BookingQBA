package com.infinitum.bookingqba.util;

public final class Constants {

    //CRASH REPORT
    public static final String ACRA_MAIL_TO_REPORT = "bookingqba.error@gmail.com";

    //API
    public static final String BASE_URL_API = "http://192.168.137.1:8000";
    public static final int API_CACHE_SIZE = 10 * 1024 * 1024;
    public static final long CONNECT_TIMEOUT = 30000;
    public static final long READ_TIMEOUT = 30000;
    public static final long WRITE_TIMEOUT = 30000;

    //IMAGE RESIZE
    public static final int THUMB_WIDTH = 480;
    public static final int THUMB_HEIGHT = 320;

    //LOGIN TAG
    public static final String LOGIN_TAG = "login";

    //NOTIFICATION
    public static final String NOTIFICATION_DEFAULT = "default";
    public static final int NOTIFICATION_ID = 1;

    //REQUEST ACTIVITY FOR RESULT
    public static final int MY_REQUEST_CODE = 4;

    //WORK SERVICES
    public static final String PERIODICAL_WORK_NAME = "MyPeriodicalWork";

    //HEADER TITLE HOME
    public static final String HEADER_POP = "Lo más popular";
    public static final String HEADER_NEW = "Lo más nuevo";

    //SHARED PREFERENCES KEY
    public static final String PREF_ENTITY_DOWNLOAD_INDEX = "dbDownloadLevel";
    public static final String PREF_DOWNLOAD_LEVEL = "downloadLevel";
    public static final String PREF_DOWNLOAD_SUCCESS = "downloadSuccess";
    public static final String PREF_FIRST_OPEN = "firstOpen";
    public static final String PREF_LAST_DOWNLOAD_DATE = "lastDownloadDate";
    public static final String ALTERNATIVE_SYNC = "altSync";

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
    public static final String COMMENT_TABLE_NAME = "Comment";
    public static final String RATING_TABLE_NAME = "Rating";
    public static final String REFERENCE_ZONE_TABLE_NAME = "ReferenceZone";
    public static final String OFFER_TABLE_NAME = "Offer";
    public static final String RENT_AMENITIES_TABLE_NAME = "RentAmenities";
    public static final String RENT_DRAW_TYPE_TABLE_NAME = "RentDrawType";
    public static final String RENT_POI_TABLE_NAME = "RentPoi";
    public static final String RENT_VISIT_COUNT_TABLE_NAME = "RentVisitCount";
    public static final String DATABASE_UPDATE_TABLE_NAME = "DatabaseUpdate";

    //PERMISSION
    public static final String PERMISSION = "permission";
    public static final String IMEI = "imei";
    public static final int PERMISSION_GRANTED = 1;
    public static final int PERMISSION_NOT_GRANTED = -1;


    //NAVIGATION
    public static final String PROVINCE_UUID = "province";
    public static final String PROVINCE_SPINNER_INDEX = "spinnerIndex";
    public static final String PROVINCE_UUID_DEFAULT = "cfa7f0d7-2b67-4245-9157-48ba2117f63c";
    public static final char ORDER_TYPE_POPULAR = 'r'; // Represent attrib rating
    public static final char ORDER_TYPE_NEW = 'c'; // Represent attrib created
    public static final int FROM_DETAIL_TO_MAP = 6;
    public static final int FROM_DETAIL_REFRESH = 12;
    public static final int FROM_DETAIL_REFRESH_SHOW_GROUP = 18;
    public static final int FROM_DETAIL_SHOW_GROUP = 24;

    //USER LOGIN
    public static final String USER_IS_AUTH = "userIsAuth";
    public static final String IS_PROFILE_ACTIVE = "profileActive";
    public static final String USER_NAME = "userName";
    public static final String USER_ID = "userId";
    public static final String USER_TOKEN = "userToken";
    public static final String USER_RENTS = "userRents";
    public static final String USER_AVATAR = "userAvatar";

    public static final String MAP_PATH = "mapDir";

    //BASIC
    public static final String LEVEL_ENTITY = "entity";
    public static final String LEVEL_GALERY = "galery";
    public static final String LEVEL_GALERY_PAUSED = "paused";
    public static final int LEVEL_PROGRESS_ENTITY = 1;
    public static final int LEVEL_PROGRESS_GALERY = 2;
    public static final int PROGRESS_ERROR = 2;
    public static final int PROGRESS_SUCCESS = 4;
    public static final String GPS_ACTIVE = "gpsActive";
    public static final String USER_GPS = "user_gps";

}
