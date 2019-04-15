package com.infinitum.bookingqba.di.module;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.AssetSQLiteOpenHelperFactory;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.database.BookingQBADatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.infinitum.bookingqba.util.Constants.DATABASE_NAME;

@Module
public class DatabaseModule {
    private static volatile BookingQBADatabase INSTANCE = null;

    @Singleton
    @Provides
    BookingQBADatabase bookingQBADatabase(Context context) {
//        return get(context);
        return Room
                .databaseBuilder(context, BookingQBADatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
//                .openHelperFactory(new AssetSQLiteOpenHelperFactory())
                .build();
    }

    synchronized static BookingQBADatabase get(Context ctxt) {
        if (INSTANCE == null) {
            INSTANCE = create(ctxt);
        }

        return (INSTANCE);
    }

    static BookingQBADatabase create(Context ctxt) {
        RoomDatabase.Builder<BookingQBADatabase> b =
                Room.databaseBuilder(ctxt.getApplicationContext(), BookingQBADatabase.class,
                        DATABASE_NAME);

        return (b.openHelperFactory(new AssetSQLiteOpenHelperFactory()).build());
    }


    @Singleton
    @Provides
    BookingQBADao provideDao(Context context) {
        return bookingQBADatabase(context).dao();
    }
}
