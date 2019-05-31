package com.infinitum.bookingqba;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.database.BookingQBADatabase;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DAOAndroidTest {

    private BookingQBADatabase bookingQBADatabase;
    private BookingQBADao qbaDao;

    @Before
    public void initDB() {
        bookingQBADatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), BookingQBADatabase.class).build();
        qbaDao = bookingQBADatabase.dao();
    }

    @After
    public void closeDB() {
        bookingQBADatabase.close();
    }

    @Test
    public void basics() {
        //size of AmenitieEntity Table with 0 rows
        assertList(0);

        //Creating AmenityEntity
        String uuidAmenityFirst = UUID.randomUUID().toString();
        String nameAmenityFirt = "Aire Acondicionado";
        AmenitiesEntity amenitiesEntityFirst = new AmenitiesEntity(uuidAmenityFirst, nameAmenityFirt);
        assertNotNull(amenitiesEntityFirst.getId());
        assertNotEquals(0, amenitiesEntityFirst.getId().length());
        assertNotNull(amenitiesEntityFirst.getName());
        assertNotEquals(0, amenitiesEntityFirst.getName().length());
        qbaDao.addAmenity(amenitiesEntityFirst);
        assertAmenity(qbaDao, amenitiesEntityFirst);

        //Updating AmenityEntity
        AmenitiesEntity amenitiesEntityUpdated = new AmenitiesEntity(amenitiesEntityFirst.getId(), "Mini bar");
        qbaDao.updateAmenity(amenitiesEntityUpdated);
        assertAmenity(qbaDao, amenitiesEntityUpdated);

        //Deleting AmenityEntity size must be 0 after that
        qbaDao.deleteAmenity(amenitiesEntityUpdated);
        assertList(0);

    }

    /**
     * Get result size of a List and compare with
     * valueSizeExpected
     *
     * @param valueSizeExpected
     */
    private void assertList(int valueSizeExpected) {
        TestObserver<Integer> testObserverSize = new TestObserver<>();
        qbaDao.getAllAmenitiesSingle()
                .map(amenitiesEntities -> {
                    if (amenitiesEntities != null) {
                        return amenitiesEntities.size();
                    } else {
                        return 0;
                    }
                })
                .subscribe(testObserverSize);
        testObserverSize.assertValue(valueSizeExpected);
        testObserverSize.assertNoErrors();
        testObserverSize.assertComplete();
    }


    /**
     * This method execute two queries
     * One ==> getAllAmenitiesSingle to fetch all amenities
     * Two ==> getAmenityById to fetch single AmenityEntity by his UUID
     * And Then result are compared
     *
     * @param qbaDao
     * @param amenitiesEntityFirst
     */
    private void assertAmenity(BookingQBADao qbaDao, AmenitiesEntity amenitiesEntityFirst) {
        //Get All Amenities
        TestObserver<List<AmenitiesEntity>> listTestObserver = new TestObserver<>();
        qbaDao.getAllAmenitiesSingle().subscribe(listTestObserver);
        listTestObserver.assertNoErrors();
        listTestObserver.assertValue(list -> list != null);
        listTestObserver.assertValue(list -> list.size() == 1);
        AmenitiesEntity amenitiesEntityTwo = listTestObserver.values().get(0).get(0);
        assertTrue(areIdentical(amenitiesEntityFirst, amenitiesEntityTwo));

        //Get AmenityEntity by Id
        TestObserver<AmenitiesEntity> amenityTestObserver = new TestObserver<>();
        qbaDao.getAmenityByIdSingle(amenitiesEntityFirst.getId()).subscribe(amenityTestObserver);
        amenityTestObserver.assertNoErrors();
        amenityTestObserver.assertValue(amenitiesEntity -> amenitiesEntity != null);
        AmenitiesEntity amenitiesEntityById = amenityTestObserver.values().get(0);
        assertTrue(areIdentical(amenitiesEntityFirst, amenitiesEntityById));

    }

    /**
     * Compare two entities
     *
     * @param one
     * @param two
     * @return true or false
     */
    private boolean areIdentical(AmenitiesEntity one, AmenitiesEntity two) {
        return (one.getId().equals(two.getId()) &&
                one.getName().equals(two.getName()));
    }


}
