package com.infinitum.bookingqba.di.module;

import com.infinitum.bookingqba.view.base.BaseMapFragment;
import com.infinitum.bookingqba.view.calendar.CalendarFragment;
import com.infinitum.bookingqba.view.filter.FilterFragment;
import com.infinitum.bookingqba.view.home.HomeFragment;
import com.infinitum.bookingqba.view.info.InfoFragment;
import com.infinitum.bookingqba.view.listwish.ListWishFragment;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.infinitum.bookingqba.view.profile.AuthFragment;
import com.infinitum.bookingqba.view.profile.MapFormFragment;
import com.infinitum.bookingqba.view.profile.LoginFragment;
import com.infinitum.bookingqba.view.profile.MyRentsFragment;
import com.infinitum.bookingqba.view.profile.ProfileFragment;
import com.infinitum.bookingqba.view.rents.DialogDetailMenu;
import com.infinitum.bookingqba.view.rents.RentListFragment;
import com.infinitum.bookingqba.view.reservation.BookRequestListFragment;
import com.infinitum.bookingqba.view.reservation.ReservationListFragment;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

    @ContributesAndroidInjector
    HomeFragment bindHomeFragment();

    @ContributesAndroidInjector
    RentListFragment bindRentListFragment();

    @ContributesAndroidInjector
    FilterFragment bindFilterFragment();

    @ContributesAndroidInjector
    LoginFragment bindLoginFragment();

    @ContributesAndroidInjector
    MapFragment bindMapFragment();

    @ContributesAndroidInjector
    ProfileFragment bindProfileFragment();

    @ContributesAndroidInjector
    ListWishFragment bindListWishFragment();

    @ContributesAndroidInjector
    InfoFragment bindInfoFragment();

    @ContributesAndroidInjector
    AuthFragment bindAuthFragment();

    @ContributesAndroidInjector
    MapFormFragment bindMapFormFragment();

    @ContributesAndroidInjector
    MyRentsFragment bindMyRentsFragment();

    @ContributesAndroidInjector
    BaseMapFragment bindBaseMapFragment();

    @ContributesAndroidInjector
    ReservationListFragment bindReservationListFragment();

    @ContributesAndroidInjector
    DialogDetailMenu bindDialogDetailMenu();

    @ContributesAndroidInjector
    BookRequestListFragment bindBookRequestListFragment();

    @ContributesAndroidInjector
    CalendarFragment bindCalendarFragment();


}
