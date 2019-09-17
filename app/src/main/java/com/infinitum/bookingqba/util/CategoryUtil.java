package com.infinitum.bookingqba.util;

import com.infinitum.bookingqba.R;

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
            24,	// "Fuel stations"
            26,	// "Taxi stands"
            29, //	"ATMs and cash points"
            30, //	"Banks"
            31, //	"Currency exchange places"
            34,	// "Hospitals"
            41,	// "Arts centers"
            42,	// "Cinemas"
            45,	// "Nightclubs (Dancing)"
            47,	// "Theatres and operas"
            58,	// "Churches, mosques, temples"
            72,	// "Airport terminals"
            97, //	"Handicrafts"
            154, //	"Castles"
            157, //	"Memorials"
            158, //	"Monuments"
            159, //	"Ruins"
            164, //	"Historic"
            167, //	"Fishing sites"
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
            41,	// "Arts centers"
            42,	// "Cinemas"
            45,	// "Nightclubs (Dancing)"
            47,	// "Theatres and operas"
            97, //	"Handicrafts"
    };

    public static final int[] natural_category_id = new int[]{
            173, //	"Nature reserves"
            174, //	"Parks"
            212, //	"Natural"
            371, //	"Camp sites"
            381, //	"Picnic sites"
    };

    public static final String[] general_category_names = new String[]{
            "Restaurante",
            "Comida Rápida",
            "Parrillada",
            "Bar",
            "Café",
            "Heladería",
            "Alquiler de Bicicletas",
            "Alquiler de Autos",
            "Estación de Servicio",
            "Parada de Taxis",
            "Cajero automático",
            "Banco",
            "Cambio de Divisas",
            "Hospital",
            "Centro de Arte",
            "Cine",
            "Centro Nocturno",
            "Teatro u Ópera",
            "Iglesia o Templo",
            "Terminal de Aeropuerto",
            "Artesanía",
            "Castillo",
            "Memorial",
            "Monumento",
            "Ruina",
            "Histórico",
            "Sitio de Pesca",
            "Mini Golf",
            "Reserva Natural",
            "Parque",
            "Piscina",
            "Natural",
            "Agente de Viaje",
            "Supermercado",
            "Tabaco",
            "Golf",
            "Bañiario",
            "Atracción",
            "Campismo",
            "Museo",
            "Picnic",
    };

    public static String categoryById(int id){
        int pos = -1;
        for(int i=0; i < general_categories_id.length; i++){
            if(general_categories_id[i] == id){
                pos = i;
                break;
            }
        }
        return pos!=-1?general_category_names[pos]:"";
    }

//    0, //	"Restaurants"
//            2, //	"Fast food restaurants"
//            4, //	"Public grills"
//            6, //   "Bar"
//            7, //   "Cafes"
//            9, //   "Ice Cream Shop"
//            19, //	"Bicycle rental stations"
//            21, //	"Car rental stations"
//            24,	// "Fuel stations"
//            26,	// "Taxi stands"
//            29, //	"ATMs and cash points"
//            30, //	"Banks"
    private int getImageResourceByCategoryID(int category) {
        switch (category) {
            case 0:
                return R.drawable.ic_restaurant_map;
            case 2:
                return R.drawable.ic_food_map;
            case 4:
                return R.drawable.ic_food_map;
            case 6:
                return R.drawable.ic_fa_beer_line;
            case 7:
                return R.drawable.ic_fa_coffee_line;
            case 9:
                return R.drawable.ic_fa_spoon_line;
            case 21:
                return R.drawable.ic_fa_automobile_line;
            case 26:
                return R.drawable.ic_fa_cab_line;
            case 29:
                return R.drawable.ic_fa_usd_line;
            case 30:
                return R.drawable.ic_fa_money_line;
            case 34:
                return R.drawable.ic_fa_hospital_line;
            case 41:
                return R.drawable.ic_fa_picture_o_line;
            case 42:
                return R.drawable.ic_fa_video_camera_line;
            case 45:
                return R.drawable.ic_fa_music_line;
            case 47:
                return R.drawable.ic_fa_building_line;
            case 154:
                return R.drawable.ic_fa_institution_line;
            case 157:
                return R.drawable.ic_fa_street_view_line;
            case 158:
                return R.drawable.ic_fa_camera_line;
            case 159:
                return R.drawable.ic_fa_camera_retro_line;
            case 164:
                return R.drawable.ic_fa_history_line;
            case 171:
                return R.drawable.ic_fa_anchor_line;
            case 173:
                return R.drawable.ic_fa_tree_line;
            case 212:
                return R.drawable.ic_fa_leaf_line;
            case 369:
                return R.drawable.ic_fa_diamond_line;
            case 381:
                return R.drawable.ic_fa_fire_line;
            default:
                return R.drawable.ic_fa_diamond_line;
        }
    }

}
