package com.infinitum.bookingqba.model.repository.rent;

import java.util.List;
import java.util.Map;

import static com.infinitum.bookingqba.model.repository.dbcommonsop.DBCommonOperationRepoImpl.SEPARATOR;

public class FilterRepositoryUtil {
    public static final String RENTMODE = "RentMode";
    public static final String MUNICIPALITY = "Municipality";
    public static final String AMENITIES = "Amenities";
    public static final String PRICE = "Price";

    public static String generalQuery(Map<String, List<String>> filterParams) {
        String generalQuery = "";
        if (filterParams.containsKey(RENTMODE) && filterParams.size() == 1) {
            generalQuery = filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1");
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.size() == 1) {
            generalQuery = filterWithMunicipality(filterParams.get(MUNICIPALITY), "Rent", "q1");
        } else if (filterParams.containsKey(AMENITIES) && filterParams.size() == 1) {
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES), "Rent", "q1");
        } else if (filterParams.containsKey(PRICE) && filterParams.size() == 1) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            generalQuery = filterWithPriceRange(min, max, "Rent", "q1");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1"));
            generalQuery = filterWithMunicipality(filterParams.get(MUNICIPALITY), subquery, "q2");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(AMENITIES)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1"));
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES), subquery, "q2");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1"));
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            generalQuery = filterWithPriceRange(min, max, subquery, "q2");
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(AMENITIES)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)", filterWithMunicipality(filterParams.get(MUNICIPALITY), "Rent", "q1"));
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES), subquery, "q2");
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery = String.format("(%s)",filterWithMunicipality(filterParams.get(MUNICIPALITY),"Rent","q1"));
            generalQuery = filterWithPriceRange(min,max,subquery,"q2");
        } else if (filterParams.containsKey(AMENITIES) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery = String.format("(%s)",filterWithAmenities(filterParams.get(AMENITIES),"Rent","q1"));
            generalQuery = filterWithPriceRange(min,max,subquery,"q2");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(AMENITIES) && filterParams.size() == 3) {
            String subquery0 = String.format("(%s)",filterWithRentMode(filterParams.get(RENTMODE),"Rent","q1"));
            String subquery1 = String.format("(%s)",filterWithMunicipality(filterParams.get(MUNICIPALITY),subquery0,"q2"));
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES),subquery1,"q3");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(AMENITIES) && filterParams.containsKey(PRICE)
                && filterParams.size() == 4) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery0 = String.format("(%s)",filterWithRentMode(filterParams.get(RENTMODE),"Rent","q1"));
            String subquery1 = String.format("(%s)",filterWithMunicipality(filterParams.get(MUNICIPALITY),subquery0,"q2"));
            String subquery2 = String.format("(%s)",filterWithAmenities(filterParams.get(AMENITIES),subquery1,"q3"));
            generalQuery = filterWithPriceRange(min,max,subquery2,"q4");
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(AMENITIES)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery0 = String.format("(%s)",filterWithMunicipality(filterParams.get(MUNICIPALITY),"Rent","q1"));
            String subquery1 = String.format("(%s)",filterWithAmenities(filterParams.get(AMENITIES),subquery0,"q2"));
            generalQuery = filterWithPriceRange(min,max,subquery1,"q3");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(AMENITIES)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery0 = String.format("(%s)",filterWithRentMode(filterParams.get(RENTMODE),"Rent","q1"));
            String subquery1 = String.format("(%s)",filterWithAmenities(filterParams.get(AMENITIES),subquery0,"q2"));
            generalQuery = filterWithPriceRange(min,max,subquery1,"q3");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery0 = String.format("(%s)",filterWithRentMode(filterParams.get(RENTMODE),"Rent","q1"));
            String subquery1 = String.format("(%s)",filterWithMunicipality(filterParams.get(MUNICIPALITY),subquery0,"q2"));
            generalQuery = filterWithPriceRange(min,max,subquery1,"q3");
        }
        return generalQuery;
    }

    private static String filterWithRentMode(List<String> rentModes, String subquery, String alias) {
        String stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " JOIN RentMode On " + alias + ".rentMode = RentMode.id " +
                "WHERE RentMode.id IN (" + convertListToCommaSeparated(rentModes) + ") GROUP BY " + alias + ".id";
        return stringQuery;
    }

    private static String filterWithAmenities(List<String> amenities, String subquery, String alias) {
        String stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " JOIN RentAmenities on " + alias + ".id = RentAmenities.rentId " +
                "JOIN Amenities on Amenities.id = RentAmenities.amenityId WHERE RentAmenities.amenityId " +
                "IN (" + convertListToCommaSeparated(amenities) + ") GROUP BY " + alias + ".id having count (*) = " + amenities.size() + "";
        return stringQuery;
    }

    private static String filterWithMunicipality(List<String> municipality, String subquery, String alias) {
        String stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " JOIN Municipality on Municipality.id = " + alias + ".municipality " +
                "WHERE " + alias + ".municipality IN(" + convertListToCommaSeparated(municipality) + ") " +
                "GROUP BY " + alias + ".id";
        return stringQuery;
    }

    private static String filterWithPriceRange(float min, float max, String subquery, String alias) {
        String stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " WHERE " + alias + ".price " +
                "BETWEEN " + min + " AND " + max + "";
        return stringQuery;
    }

    private static String convertListToCommaSeparated(List<String> ids) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            stringBuilder.append("'").append(ids.get(i)).append("'");
            if (i < ids.size() - 1)
                stringBuilder.append(SEPARATOR);
        }
        return stringBuilder.toString();
    }
}
