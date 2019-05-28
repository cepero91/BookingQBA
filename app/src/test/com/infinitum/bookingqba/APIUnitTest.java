package com.infinitum.bookingqba;

import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.CommentsEmotionAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.GeneralCommentAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.ProfilePercentAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.RatingStarAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.RentPositionAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.model.remote.pojo.VisitAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.WishAnalitics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.BASE_URL_API;
import static com.infinitum.bookingqba.util.Constants.CONNECT_TIMEOUT;
import static com.infinitum.bookingqba.util.Constants.READ_TIMEOUT;
import static com.infinitum.bookingqba.util.Constants.WRITE_TIMEOUT;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class APIUnitTest {
    private ApiInterface apiInterface;

    @Before
    public void createService() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);

        apiInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient.build())
                .build()
                .create(ApiInterface.class);
    }


    @Test
    public void getAllAmenities() {
        String uuidRent = "8a2203b4-9105-404e-8b18-31c19decb388";
        TestObserver<AnaliticsGroup> testObserver = new TestObserver<>();

        apiInterface.rentAnaliticsObservable(uuidRent)
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        testObserver.assertComplete();
    }

    @Test
    public void userLogin() {
        String username = "cepero";
        String password = "qwertyuiop";
        String imei = "357091099552455";

        User user = new User("dac72e30cc532c02e7aa8d8d2c957bdfa0691904", "Jose Manuel");
        user.setAvatar("static/users/client-face2_kJQQYi2_9QrXNtz.png");
        String[] rent = new String[]{
                "4488add4-7513-4e0a-ad67-c3c75383ebd4",
                "72abd0c0-84a0-4945-bafc-ca242efd7fff",
                "07333e75-91e8-46a4-9e19-0b52d4b1294f",
                "8a2203b4-9105-404e-8b18-31c19decb388",
                "2755ed13-c65c-4dc6-849f-768ad6e3baf2"
        };
        user.setUserid("1");
        user.setRentsId(new ArrayList<>(Arrays.asList(rent)));

        TestObserver<String> testObserver = new TestObserver<>();

        apiInterface.login(username,password,imei)
                .map(new Function<Response<User>, String>() {
                    @Override
                    public String apply(Response<User> response) throws Exception {
                        return response.body().getToken();
                    }
                })
                .subscribe(testObserver);

        testObserver.assertValue("dac72e30cc532c02e7aa8d8d2c957bdfa0691904");
        testObserver.assertNoErrors();
        testObserver.assertComplete();
    }


}
