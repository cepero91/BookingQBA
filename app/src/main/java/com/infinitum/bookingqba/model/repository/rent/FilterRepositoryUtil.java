package com.infinitum.bookingqba.model.repository.rent;

import android.util.Pair;

import java.util.List;
import java.util.Map;

import static com.infinitum.bookingqba.util.StringUtils.convertListToCommaSeparated;


public class FilterRepositoryUtil {
    public static final String RENTMODE = "RentMode";
    public static final String MUNICIPALITY = "Municipality";
    public static final String AMENITIES = "Amenities";
    public static final String PRICE = "Price";
    public static final String POI = "Poi";
    public static final String ORDER = "Order";
    public static final String CAPABILITY = "Capability";


    public static String generalQuery(Map<String, List<String>> filterParams, String province) {
        String generalQuery = "";
        if (filterParams.containsKey(RENTMODE) && filterParams.size() == 1) {
            generalQuery = filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1", province);
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.size() == 1) {
            generalQuery = filterWithMunicipality(filterParams.get(MUNICIPALITY), "Rent", "q1");
        } else if (filterParams.containsKey(AMENITIES) && filterParams.size() == 1) {
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES), "Rent", "q1", province);
        } else if (filterParams.containsKey(PRICE) && filterParams.size() == 1) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            generalQuery = filterWithPriceRange(min, max, "Rent", "q1", province);
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1", ""));
            generalQuery = filterWithMunicipality(filterParams.get(MUNICIPALITY), subquery, "q2");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(AMENITIES)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1", province));
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES), subquery, "q2", "");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1", province));
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            generalQuery = filterWithPriceRange(min, max, subquery, "q2", "");
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(AMENITIES)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)", filterWithMunicipality(filterParams.get(MUNICIPALITY), "Rent", "q1"));
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES), subquery, "q2", "");
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery = String.format("(%s)", filterWithMunicipality(filterParams.get(MUNICIPALITY), "Rent", "q1"));
            generalQuery = filterWithPriceRange(min, max, subquery, "q2", "");
        } else if (filterParams.containsKey(AMENITIES) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery = String.format("(%s)", filterWithAmenities(filterParams.get(AMENITIES), "Rent", "q1", province));
            generalQuery = filterWithPriceRange(min, max, subquery, "q2", "");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(AMENITIES) && filterParams.size() == 3) {
            String subquery0 = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1", ""));
            String subquery1 = String.format("(%s)", filterWithMunicipality(filterParams.get(MUNICIPALITY), subquery0, "q2"));
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES), subquery1, "q3", "");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(AMENITIES) && filterParams.containsKey(PRICE)
                && filterParams.size() == 4) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery0 = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1", ""));
            String subquery1 = String.format("(%s)", filterWithMunicipality(filterParams.get(MUNICIPALITY), subquery0, "q2"));
            String subquery2 = String.format("(%s)", filterWithAmenities(filterParams.get(AMENITIES), subquery1, "q3", ""));
            generalQuery = filterWithPriceRange(min, max, subquery2, "q4", "");
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(AMENITIES)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery0 = String.format("(%s)", filterWithMunicipality(filterParams.get(MUNICIPALITY), "Rent", "q1"));
            String subquery1 = String.format("(%s)", filterWithAmenities(filterParams.get(AMENITIES), subquery0, "q2", ""));
            generalQuery = filterWithPriceRange(min, max, subquery1, "q3", "");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(AMENITIES)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery0 = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1", province));
            String subquery1 = String.format("(%s)", filterWithAmenities(filterParams.get(AMENITIES), subquery0, "q2", ""));
            generalQuery = filterWithPriceRange(min, max, subquery1, "q3", "");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            String subquery0 = String.format("(%s)", filterWithRentMode(filterParams.get(RENTMODE), "Rent", "q1", ""));
            String subquery1 = String.format("(%s)", filterWithMunicipality(filterParams.get(MUNICIPALITY), subquery0, "q2"));
            generalQuery = filterWithPriceRange(min, max, subquery1, "q3", "");
        }
        return generalQuery;
    }

    private static String provinceJoinQuery(String province, String alias) {
        return " JOIN Municipality ON " +
                alias +
                ".municipality = Municipality.id JOIN Province ON Province.id = province" +
                " WHERE province = " +
                String.format("'%s'", province);
    }

    private static String filterWithRentMode(List<String> rentModes, String subquery, String alias, String joinProvince) {
        String stringQuery;
        if (joinProvince.length() > 0) {
            stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " JOIN RentMode On " + alias + ".rentMode = RentMode.id " +
                    provinceJoinQuery(joinProvince, alias) +
                    "AND RentMode.id IN (" + convertListToCommaSeparated(rentModes) + ")";
        } else {
            stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " JOIN RentMode On " + alias + ".rentMode = RentMode.id " +
                    "WHERE RentMode.id IN (" + convertListToCommaSeparated(rentModes) + ")";
        }
        return stringQuery;
    }

    private static String filterWithAmenities(List<String> amenities, String subquery, String alias, String joinProvince) {
        String stringQuery;
        if (joinProvince.length() > 0) {
            stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " JOIN RentAmenities on " + alias + ".id = RentAmenities.rentId " +
                    "JOIN Amenities on Amenities.id = RentAmenities.amenityId " +
                    provinceJoinQuery(joinProvince, alias) +
                    " AND RentAmenities.amenityId " +
                    "IN (" + convertListToCommaSeparated(amenities) + ") GROUP BY " + alias + ".id having count (*) = " + amenities.size() + " " +
                    "ORDER BY " + alias + ".rating DESC";
        } else {
            stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " JOIN RentAmenities on " + alias + ".id = RentAmenities.rentId " +
                    "JOIN Amenities on Amenities.id = RentAmenities.amenityId WHERE RentAmenities.amenityId " +
                    "IN (" + convertListToCommaSeparated(amenities) + ") GROUP BY " + alias + ".id having count (*) = " + amenities.size() + " " +
                    "ORDER BY " + alias + ".rating DESC";
        }
        return stringQuery;
    }

    private static String filterWithMunicipality(List<String> municipality, String subquery, String alias) {
        String stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " JOIN Municipality on Municipality.id = " + alias + ".municipality " +
                "WHERE " + alias + ".municipality IN(" + convertListToCommaSeparated(municipality) + ") " +
                "GROUP BY " + alias + ".id ORDER BY " + alias + ".rating DESC";
        return stringQuery;
    }

    private static String filterWithPriceRange(float min, float max, String subquery, String alias, String joinProvince) {
        String stringQuery;
        if (joinProvince.length() > 0) {
            stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " " +
                    provinceJoinQuery(joinProvince, alias) +
                    " AND " + alias + ".price " +
                    "BETWEEN " + min + " AND " + max + " ORDER BY " + alias + ".rating DESC";
        } else {
            stringQuery = "SELECT * FROM " + subquery + " AS " + alias + " WHERE " + alias + ".price " +
                    "BETWEEN " + min + " AND " + max + " ORDER BY " + alias + ".rating DESC";
        }
        return stringQuery;
    }


    public static String buildFilterQuery(Map<String, List<String>> filterParams, String provinceId) {
        Pair<String, String> secondLevelPair = buildSecondLevelQuery(filterParams, provinceId);
        if (filterParams.containsKey(ORDER)) {
            String alias;
            if (filterParams.get(ORDER).get(0).equals("r")) {
                alias = secondLevelPair.first.isEmpty() ? "Rent" : secondLevelPair.first;
                return secondLevelPair.second + " ORDER BY " + alias + ".rating DESC, " +
                        alias + ".ratingCount DESC";
            } else {
                alias = secondLevelPair.first.isEmpty() ? "q2" : secondLevelPair.first;
                return "SELECT q2.*,avg(Comment.emotion) as x, count(Comment.id) as y FROM(" + secondLevelPair.second + ") as q2 " +
                        "JOIN Comment ON Comment.rent = " + alias + ".id " +
                        "GROUP BY " + alias + ".id order by x desc, y desc";
            }
        } else {
            return secondLevelPair.second;
        }
    }

    private static String buildFirstLevelQuery(Map<String, List<String>> filterParams, String provinceId) {
        StringBuilder generalQueryBuilder = new StringBuilder();
        String principalQuery = "SELECT Rent.* FROM Rent ";
        String provinceJoin = " JOIN Municipality ON Municipality.id = Rent.municipality ";
        generalQueryBuilder.append(principalQuery);
        if (!filterParams.containsKey(MUNICIPALITY)) {
            String joinClause = buildJoinClause(filterParams, provinceJoin);
            String matchClause = buildMatchClause(filterParams, provinceId);
            String groupClause = buildGroupByClause(filterParams);
            generalQueryBuilder.append(joinClause).append(matchClause).append(groupClause);
        } else {
            String joinClause = buildJoinClause(filterParams, "");
            String matchClause = buildMatchClause(filterParams, "");
            String groupClause = buildGroupByClause(filterParams);
            generalQueryBuilder.append(joinClause).append(matchClause).append(groupClause);
        }
        String gg = generalQueryBuilder.toString();
        return generalQueryBuilder.toString();
    }

    private static Pair<String, String> buildSecondLevelQuery(Map<String, List<String>> filterParams, String provinceId) {
        String firstLevelQuery = buildFirstLevelQuery(filterParams, provinceId);
        Pair<String, String> secondLevelQueryPair = new Pair<>("", firstLevelQuery);
        if (filterParams.containsKey(POI)) {
            String secondLevelQuery = "SELECT q2.* FROM(" + firstLevelQuery + ") as q2 " +
                    "JOIN RentPoi ON RentPoi.rentId = q2.id " +
                    "JOIN Poi ON Poi.id = RentPoi.poiId " +
                    "WHERE Poi.category IN(" + convertListToCommaSeparated(filterParams.get(POI)) + ") " +
                    "GROUP BY q2.id HAVING COUNT(DISTINCT Poi.category) = " + filterParams.get(POI).size();
            secondLevelQueryPair = new Pair<>("q2", secondLevelQuery);
        }
        return secondLevelQueryPair;
    }


    private static String buildJoinClause(Map<String, List<String>> filterParams, String provinceJoin) {
        StringBuilder joinBuilder = new StringBuilder();
        String amenitiesJoin = " JOIN RentAmenities ON RentAmenities.rentId = Rent.id ";
        if (filterParams.containsKey(AMENITIES)) {
            joinBuilder.append(amenitiesJoin);
        }
        return joinBuilder.append(provinceJoin).toString();
    }

    private static String buildMatchClause(Map<String, List<String>> filterParams, String provinceId) {
        StringBuilder matchBuilder = new StringBuilder();
        String clauseAnd = " AND";
        String clauseWhere = " WHERE";
        boolean hasWhere = false;
        if (filterParams.containsKey(AMENITIES)) {
            String amenitiesMatch = " RentAmenities.amenityId IN (" + convertListToCommaSeparated(filterParams.get(AMENITIES)) + ") ";
            matchBuilder.append(clauseWhere).append(amenitiesMatch);
            hasWhere = true;
        }
        if (filterParams.containsKey(RENTMODE)) {
            String rentModeMatch = " Rent.rentMode IN (" + convertListToCommaSeparated(filterParams.get(RENTMODE)) + ")";
            if (hasWhere) {
                matchBuilder.append(clauseAnd).append(rentModeMatch);
            } else {
                matchBuilder.append(clauseWhere).append(rentModeMatch);
                hasWhere = true;
            }
        }
        if (filterParams.containsKey(MUNICIPALITY)) {
            String municipalityMatch = " Rent.municipality IN (" + convertListToCommaSeparated(filterParams.get(MUNICIPALITY)) + ")";
            if (hasWhere) {
                matchBuilder.append(clauseAnd).append(municipalityMatch);
            } else {
                matchBuilder.append(clauseWhere).append(municipalityMatch);
                hasWhere = true;
            }
        }
        if (filterParams.containsKey(CAPABILITY)) {
            int capability = Integer.parseInt(filterParams.get(CAPABILITY).get(0));
            String capabilityMatch = " Rent.capability = " + capability;
            if (hasWhere) {
                matchBuilder.append(clauseAnd).append(capabilityMatch);
            } else {
                matchBuilder.append(clauseWhere).append(capabilityMatch);
                hasWhere = true;
            }
        }
        if (filterParams.containsKey(PRICE)) {
            float minPrice = Float.parseFloat(filterParams.get(PRICE).get(0));
            float maxPrice = Float.parseFloat(filterParams.get(PRICE).get(1));
            String priceMatch = " Rent.price BETWEEN " + minPrice + " AND " + maxPrice;
            if (hasWhere) {
                matchBuilder.append(clauseAnd).append(priceMatch);
            } else {
                matchBuilder.append(clauseWhere).append(priceMatch);
                hasWhere = true;
            }
        }
        if (!provinceId.isEmpty()) {
            String provinceMatch = " Municipality.province = '" + provinceId + "'";
            if (hasWhere) {
                matchBuilder.append(clauseAnd).append(provinceMatch);
            } else {
                matchBuilder.append(clauseWhere).append(provinceMatch);
            }
        }
        return matchBuilder.toString();
    }

    private static String buildGroupByClause(Map<String, List<String>> filterParams) {
        if (filterParams.containsKey(AMENITIES)) {
            return " GROUP BY Rent.id HAVING COUNT(RentAmenities.amenityId) = " + filterParams.get(AMENITIES).size() + "";
        }
        return "";
    }


}
