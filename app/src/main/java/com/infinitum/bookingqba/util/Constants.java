package com.infinitum.bookingqba.util;

public final class Constants {

    public static final String DONT_OPEN_SWIPE_TO_DELETE_DIALOG = "swipe";
    public static final String DONT_OPEN_FORM_INPUT_HELPER_DIALOG = "formHelper";

    public static final int PERMISSION_REQUEST_CODE = 98;

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
    public static final int LOGIN_REQUEST_CODE = 7;

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
    public static final String WISHED_TABLE_NAME = "Wished";
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
    public static final char ORDER_TYPE_MOST_COMMENTED = 'c'; // Represent attrib rating
    public static final char ORDER_TYPE_MOST_RATING = 'r'; // Represent attrib created
    public static final int FROM_DETAIL_TO_MAP = 6;
    public static final int FROM_DETAIL_REFRESH = 12;
    public static final int FROM_DETAIL_REFRESH_SHOW_GROUP = 18;
    public static final int FROM_DETAIL_SHOW_GROUP = 24;
    public static final int FROM_RESERVATION_DETAIL_TO_LIST = 29;

    //USER LOGIN
    public static final String USER_IS_AUTH = "userIsAuth";
    public static final String NAV_HEADER_REQUIRED_UPDATE = "navHeaderReqUpdate";
    public static final String USER_IS_RENTS_OWNER = "userIsRentOwner";
    public static final String IS_PROFILE_ACTIVE = "profileActive";
    public static final String USER_NAME = "userName";
    public static final String USER_ID = "userId";
    public static final String USER_TOKEN = "userToken";
    public static final String USER_RENTS = "userRents";
    public static final String USER_AVATAR = "userAvatar";
    public static final String LAST_EMAIL_REGISTER = "lastEmailRegister";
    public static final String USER_HAS_ACTIVE_RENT = "userHasActiveRent";

    public static final String MAP_PATH = "mapDir";
    public static final String ROUTE_PATH = "routeDir";
    public static final String ROUTE_IS_READY = "routeIsReady";

    public static final String BY_HOURS_UUID = "7c9ea827-82e0-47e9-84c3-cc87de60ac67";
    public static final String BY_NIGHT_UUID = "7a35e4a9-04d6-45ca-b7b2-2aeb49a6e5fa";

    public static final String FILTER_RADAR_RENT_MODE = "rm";
    public static final String FILTER_RADAR_RADIUS = "rd";

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

    public static final int LOCATION_REQUEST_CODE = 1240;
    public static final int READ_PHONE_REQUEST_CODE = 1241;
    public static final int WRITE_EXTERNAL_REQUEST_CODE = 1242;

    public static final int REQUEST_CHECK_SETTINGS = 0x1;


    public static final String FILTER_AMENITIES = "Amenities";
    public static final String FILTER_RENTMODE = "RentMode";
    public static final String FILTER_MUNICIPALITIES = "Municipality";
    public static final String FILTER_POITYPES = "PoiType";
    public static final String FILTER_PRICE = "Price";
    public static final String FILTER_CAPABILITY = "Capability";
    public static final String FILTER_ORDER = "Order";


    //--------------------- RENT RESERVATION ----------------------
    public static final String RENT_ID = "rentId";
    public static final String PRICE = "price";
    public static final String MAXCAPABILITY = "maxcapability";
    public static final String NIGHT_COUNT = "nightcount";
    public static final String DATE_RANGE = "dateRange";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";


    //------------------- REFERENCES ZONE ----
    public static final String HISTORIC = "Histórico";
    public static final String URBAN = "Barriada";
    public static final String BEACH = "Playa o Costa";
    public static final String CULTURE = "Cultural";
    public static final String NATURAL = "Natural";

    public static final String CONNEXION_ERROR_MSG = "Al parecer algo ocurre con tu internet. Intente más tarde";
    public static final String NO_COMMENT_MSG = "No hay comentarios que mostrar. Anímate y sé el primero.";
    public static final String OPERATIONAL_ERROR_MSG = "Al parecer un error ha ocurrido.";
    public static final String EMPTY_ERROR_MSG = "Lo sentimos, no hay elementos que mostrar";
    public static final String NO_UPDATE_NEEDED = "Los datos ya están actualizados";

}
