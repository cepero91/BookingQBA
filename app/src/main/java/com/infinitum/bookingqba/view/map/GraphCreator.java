package com.infinitum.bookingqba.view.map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.UnitConverterUtil;
import com.infinitum.bookingqba.util.ZipUtil;
import com.infinitum.bookingqba.util.file.CopyAssets;
import com.infinitum.bookingqba.util.file.CopyCreator;
import com.infinitum.bookingqba.util.file.CopyListener;

import org.mapsforge.core.model.LatLong;
import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class GraphCreator {

    private GraphHopper graphHopper;
    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private SharedPreferences sharedPreferences;
    private final Context context;
    private boolean routeIsReady = false;

    GraphCreator(Context context) {
        this.context = context;
    }

    public GraphCreator share(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        return this;
    }

    public GraphCreator composite(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
        return this;
    }

    public void config(){
        String routePath = sharedPreferences.getString(Constants.ROUTE_PATH,"");
        boolean routeIsReady = sharedPreferences.getBoolean(Constants.ROUTE_IS_READY,false);
        if(routeIsReady && !routePath.isEmpty()){
            routeExist();
        }else {
            routeNotExist();
        }
    }

    public boolean routeIsReady(){
        return routeIsReady;
    }

    private void routeExist() {
        disposable = Completable.fromAction(this::configureGraphHopper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toSingleDefault(true)
                .onErrorReturnItem(false)
                .subscribe(aBoolean -> {
                    if(aBoolean){
                        Timber.e("GRAPHCREATOR ====> route is ready to use");
                        Toast.makeText(context, "Ruta lista...", Toast.LENGTH_SHORT).show();
                        routeIsReady = true;
                    }
                },Timber::e);
        compositeDisposable.add(disposable);
    }

    private void routeNotExist() {
        disposable = Completable.fromAction(this::prepareFileRouteFromAsset)
                .subscribeOn(Schedulers.io())
                .andThen(Completable.fromAction(this::unzipFileRoute).subscribeOn(Schedulers.io()))
                .andThen(Completable.fromAction(this::deleteZipFile).subscribeOn(Schedulers.io()))
                .andThen(Completable.fromAction(this::configureGraphHopper).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .toSingleDefault(true)
                .onErrorReturnItem(false)
                .subscribe(aBoolean -> {
                    if(aBoolean){
                        saveRouteFileInToSharePreferences();
                        Timber.e("GRAPHCREATOR ====> route is ready to use");
                        Toast.makeText(context, "Ruta lista...", Toast.LENGTH_SHORT).show();
                        routeIsReady = true;
                    }
                },Timber::e);
        compositeDisposable.add(disposable);
    }

    private void prepareFileRouteFromAsset() {
        Timber.e("GRAPHCREATOR ====> prepareFileRouteFromAsset()");
        CopyAssets.with(context)
                .from("route-gh")
                .copy();
    }

    private void unzipFileRoute() {
        Timber.e("GRAPHCREATOR ====> unzipFileRoute()");
        String routeZipFile = context.getFilesDir().getAbsolutePath() + File.separator + "route-gh" + File.separator + "route.zip";
        File file = new File(routeZipFile);
        if (file.exists()) {
            String destine = context.getFilesDir().getAbsolutePath() + File.separator + "route-gh";
            try {
                Timber.e("GRAPHCREATOR ====> unziping file");
                ZipUtil.unzip(new File(routeZipFile), new File(destine));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteZipFile() {
        Timber.e("GRAPHCREATOR ====> deleteZipFile()");
        String routeZipFile = context.getFilesDir().getAbsolutePath() + File.separator + "route-gh" + File.separator + "route.zip";
        File file = new File(routeZipFile);
        if (file.exists() && file.delete()) {
            Timber.e("GRAPHCREATOR ====> zip eliminado");
        }
    }

    private boolean configureGraphHopper() {
        Timber.e("GRAPHCREATOR ====> configureGraphHopper()");
        String routeDir = context.getFilesDir().getAbsolutePath() + File.separator + "route-gh";
        File file = new File(routeDir);
        if (file.exists() && file.isDirectory() && file.list().length > 0) {
            graphHopper = new GraphHopper().forMobile();
            graphHopper.getCHFactoryDecorator().addWeighting("shortest");
            Timber.e("GRAPHCREATOR ====> loading graphhopper");
            boolean load = graphHopper.load(routeDir);
            Timber.e("found graph " + graphHopper.getGraphHopperStorage().toString() + ", nodes:" + graphHopper.getGraphHopperStorage().getNodes());
            return load;
        }
        return false;
    }

    private void saveRouteFileInToSharePreferences(){
        Timber.e("GRAPHCREATOR ====> route file path saved");
        String routeDir = context.getFilesDir().getAbsolutePath() + File.separator + "route-gh";
        sharedPreferences.edit().putString(Constants.ROUTE_PATH,routeDir).apply();
        sharedPreferences.edit().putBoolean(Constants.ROUTE_IS_READY,true).apply();
    }


    public Single<RouteHelper> calculateRoute(LatLong from, LatLong to, String vehicle){
        return Single.just(calcPath(from, to, vehicle)).subscribeOn(Schedulers.io());
    }

    private RouteHelper calcPath(LatLong from, LatLong to, String vehicle) {
        GHRequest req = new GHRequest(from.latitude, from.longitude, to.latitude, to.longitude).
                setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
        req.getHints().put(Parameters.Routing.INSTRUCTIONS, "true");
        req.setVehicle(vehicle);
        GHResponse resp = graphHopper.route(req);
        PathWrapper pathWrapper = resp.getBest();
        Pair<String, String> distanceAndUnit = UnitConverterUtil.getPairDistanceUnit(pathWrapper.getDistance());
        String timeValue = String.format(Locale.getDefault(),"%d", TimeUnit.MILLISECONDS.toMinutes(pathWrapper.getTime()));
        return new RouteHelper(pathWrapper,distanceAndUnit.first,distanceAndUnit.second,timeValue,"min");
    }

    private String getMinFromLong(long time) {
        return String.format(context.getString(R.string.min_msg), TimeUnit.MILLISECONDS.toMinutes(time));
    }

    public PathLayer createPathLayer(PathWrapper response, Map map) {
        Style style = Style.builder()
                .fixed(true)
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(0x9900cc33)
                .strokeWidth(4 * context.getResources().getDisplayMetrics().density)
                .build();
        PathLayer pathLayer = new PathLayer(map, style);
        List<GeoPoint> geoPoints = new ArrayList<>();
        PointList pointList = response.getPoints();
        for (int i = 0; i < pointList.getSize(); i++)
            geoPoints.add(new GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)));
        pathLayer.setPoints(geoPoints);
        return pathLayer;
    }

    public void cleanComposite(){
        Timber.e("GRAPHCREATOR ====> cleanComposite()");
        if(compositeDisposable!=null) {
            Timber.e("GRAPHCREATOR ====> cleaning composite()");
            compositeDisposable.clear();
        }
    }


}
