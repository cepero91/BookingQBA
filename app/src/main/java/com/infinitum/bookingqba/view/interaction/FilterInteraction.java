package com.infinitum.bookingqba.view.interaction;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;

import java.util.List;
import java.util.Map;

public interface FilterInteraction {

    void onFilterElement(Resource<List<RentAndGalery>> resourceResult);

    void onFilterClean();

}
