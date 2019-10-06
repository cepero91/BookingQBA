package com.infinitum.bookingqba.util;

import android.util.Pair;

import com.infinitum.bookingqba.R;

import org.mapsforge.poi.storage.PoiCategory;

import java.util.Arrays;

public final class CategoryUtil {

    public static final int[] general_categories_id = new int[]{
            0, //	"Restaurants"
            2, //	"Fast food restaurants"
            4, //	"Public grills"
            6, //   "Bar"
            7, //   "Cafes"
            9, //   "Ice Cream Shop"
            19, //	"Bicycle rental stations"
            21, //	"Car rental stations"
            24,    // "Fuel stations"
            26,    // "Taxi stands"
            29, //	"ATMs and cash points"
            30, //	"Banks"
            31, //	"Currency exchange places"
            34,    // "Hospitals"
            41,    // "Arts centers"
            42,    // "Cinemas"
            45,    // "Nightclubs (Dancing)"
            47,    // "Theatres and operas"
            58,    // "Churches, mosques, temples"
            72,    // "Airport terminals"
            97, //	"Handicrafts"
            154, //	"Castles"
            157, //	"Memorials"
            158, //	"Monuments"
            159, //	"Ruins"
            164, //	"Historic"
            167, //	"Fishing sites"
            171, // Marina
            172, //	"Miniature golf places"
            173, //	"Nature reserves"
            174, //	"Parks"
            180, //	"Swimming pools"
            212, //	"Natural"
            215, //	"Travel agents"
            301, //	"Supermarket"
            304, //	"Tobacco"
            336, //	"Golf"
            360, //	"Swimming"
            369, //	"Attractions"
            371, //	"Camp sites"
            380, //	"Museums"
            381, //	"Picnic sites"
    };

    public static final int[] historic_category_id = new int[]{
            380, //	"Museums"
            154, //	"Castles"
            157, //	"Memorials"
            158, //	"Monuments"
            159, //	"Ruins"
            164, //	"Historic"
    };

    public static final int[] cultural_category_id = new int[]{
            41,    // "Arts centers"
            42,    // "Cinemas"
            45,    // "Nightclubs (Dancing)"
            47,    // "Theatres and operas"
            97, //	"Handicrafts"
    };

    public static final int[] natural_category_id = new int[]{
            173, //	"Nature reserves"
            212, //	"Natural"
            371, //	"Camp sites"
            381, //	"Picnic sites"
    };

    public static final String[] general_category_names = new String[]{
            "Restaurantes",
            "Comida Rápida",
            "Parrilladas",
            "Bares",
            "Cafés",
            "Heladerías",
            "Alquiler de Bicicletas",
            "Alquiler de Autos",
            "Estación de Servicios",
            "Parada de Taxis",
            "Cajero automático",
            "Bancos",
            "Cambio de Divisas",
            "Hospitales",
            "Centros de Arte",
            "Cines",
            "Centros Nocturnos",
            "Teatros u Óperas",
            "Religión",
            "Terminales de Aeropuerto",
            "Artesanías",
            "Castillos",
            "Memoriales",
            "Monumentos",
            "Ruinas",
            "Históricos",
            "Sitios de Pesca",
            "Marinas",
            "Mini Golf",
            "Reservas Naturales",
            "Parques",
            "Piscinas",
            "Natural",
            "Agentes de Viaje",
            "Supermercados",
            "Tabacos",
            "Golf",
            "Bañiarios",
            "Atracciónes",
            "Campismos",
            "Museos",
            "Sitios de Picnic",
    };

    public static String categoryById(int id) {
        int pos = -1;
        for (int i = 0; i < general_categories_id.length; i++) {
            if (general_categories_id[i] == id) {
                pos = i;
                break;
            }
        }
        return pos != -1 ? general_category_names[pos] : "";
    }

    public static Pair<Integer,String> giveCorrectCategoryid(PoiCategory[] poiCategory) {
        Pair<Integer,String> pairResult = new Pair<>(-1,"");
        if (poiCategory.length > 1) {
            int id1 = poiCategory[0].getID();
            int id2 = poiCategory[1].getID();
            if (id1 != 396) {
                if (isValidCategory(id1)) {
                    pairResult = new Pair<>(id1,poiCategory[0].getTitle());
                } else if(isValidCategory(poiCategory[0].getParent().getID())){
                    pairResult = new Pair<>(poiCategory[0].getParent().getID(),poiCategory[0].getParent().getTitle());
                }
            } else if (id2 != 396) {
                if (isValidCategory(id2)) {
                    pairResult = new Pair<>(id2,poiCategory[1].getTitle());
                } else if(isValidCategory(poiCategory[1].getParent().getID())){
                    pairResult = new Pair<>(poiCategory[1].getParent().getID(),poiCategory[1].getParent().getTitle());
                }
            }
        } else {
            if(isValidCategory(poiCategory[0].getID())){
                pairResult = new Pair<>(poiCategory[0].getID(),poiCategory[0].getTitle());
            }else if(isValidCategory(poiCategory[0].getParent().getID())){
                pairResult = new Pair<>(poiCategory[0].getParent().getID(),poiCategory[0].getParent().getTitle());
            }
        }
        return pairResult;
    }

    public static boolean isValidCategory(int id) {
        for (int aGeneral_categories_id : general_categories_id) {
            if (aGeneral_categories_id == id) {
                return true;
            }
        }
        return false;
    }


    public static int getImageResourceByCategoryID(int category) {
        switch (category) {
            case 0:
                return R.drawable.ic_restaurant_15_maki;
            case 2:
                return R.drawable.ic_fast_food_15_maki;
            case 4:
                return R.drawable.ic_bbq_15_maki;
            case 6:
                return R.drawable.ic_bar_15_maki;
            case 7:
                return R.drawable.ic_cafe_15_maki;
            case 9:
                return R.drawable.ic_bicycle_15_maki;
            case 19:
                return R.drawable.ic_ice_cream_15_maki;
            case 21:
                return R.drawable.ic_car_rental_15_maki;
            case 24:
                return R.drawable.ic_fuel_15_maki;
            case 26:
                return R.drawable.ic_car_15_maki;
            case 29:
                return R.drawable.ic_bank_15_maki;
            case 30:
                return R.drawable.ic_bank_15_maki;
            case 31:
                return R.drawable.ic_bank_15_maki;
            case 34:
                return R.drawable.ic_hospital_15_maki;
            case 41:
                return R.drawable.ic_art_gallery_15_maki;
            case 42:
                return R.drawable.ic_cinema_15_maki;
            case 45:
                return R.drawable.ic_music_15_maki;
            case 47:
                return R.drawable.ic_theatre_15_maki;
            case 58:
                return R.drawable.ic_religious_christian_15_maki;
            case 72:
                return R.drawable.ic_airport_15_maki;
            case 97:
                return R.drawable.ic_gift_15_maki;
            case 154:
                return R.drawable.ic_castle_15_maki;
            case 157:
                return R.drawable.ic_embassy_15_maki;
            case 158:
                return R.drawable.ic_monument_15_maki;
            case 159:
                return R.drawable.ic_landmark_15_maki;
            case 164:
                return R.drawable.ic_library_15_maki;
            case 167:
                return R.drawable.ic_aquarium_15_maki;
            case 171:
                return R.drawable.ic_ferry_15_maki;
            case 172:
                return R.drawable.ic_golf_15_maki;
            case 173:
                return R.drawable.ic_natural_15_maki;
            case 174:
                return R.drawable.ic_park_15_maki;
            case 180:
                return R.drawable.ic_swimming_15_maki;
            case 212:
                return R.drawable.ic_park_alt1_15_maki;
            case 215:
                return R.drawable.ic_rail_metro_15_maki;
            case 301:
                return R.drawable.ic_shop_15_maki;
            case 304:
                return R.drawable.ic_triangle_15_maki;
            case 336:
                return R.drawable.ic_golf_15_maki;
            case 360:
                return R.drawable.ic_swimming_15_maki;
            case 369:
                return R.drawable.ic_attraction_15_maki;
            case 371:
                return R.drawable.ic_campsite_15_maki;
            case 380:
                return R.drawable.ic_museum_15_maki;
            case 381:
                return R.drawable.ic_picnic_site_15_maki;
            default:
                return R.drawable.ic_attraction_15_maki;
        }
    }

}
