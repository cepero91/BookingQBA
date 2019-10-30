package com.infinitum.bookingqba.view.profile;


import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.customview.reviewratings.BarLabels;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentProfileBinding;
import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.Month;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.HorizontalScrollAdapter;
import com.infinitum.bookingqba.view.adapters.items.horizontal.HorizontalItem;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.interaction.ProfileInteraction;
import com.infinitum.bookingqba.viewmodel.RentAnaliticsViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.squareup.picasso.Picasso;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.graphics.Color.rgb;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;


public class ProfileFragment extends Fragment implements DiscreteScrollView.OnItemChangedListener<HorizontalScrollAdapter.ViewHolder>,
        DiscreteScrollView.ScrollStateChangeListener<HorizontalScrollAdapter.ViewHolder>, View.OnClickListener,
        NestedScrollView.OnScrollChangeListener {

    private FragmentProfileBinding profileBinding;
    private ProfileInteraction interaction;

    //MP
    private Typeface tfLight;
    private Typeface tfBold;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NetworkHelper networkHelper;

    private RentAnaliticsViewModel viewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    private HorizontalScrollAdapter horizontalScrollAdapter;
    private Rect scrollBounds;
    private boolean commentIsAnimated = false;
    private boolean revenueIsAnimated = false;
    private boolean reservationIsAnimated = false;


    private HorizontalItem singleHorizontalItem;
    private String[] rentSelect;
    private int lastPosSelected = -1;
    private String token;
    private String userId;
    private Chart lastChartToExport;
    private String lastNameToExport;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return profileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        profileBinding.setLoading(true);
        profileBinding.setInitialVisibility(View.GONE);

        token = sharedPreferences.getString(USER_TOKEN, "");
        userId = sharedPreferences.getString(USER_ID, "");

        tfLight = ResourcesCompat.getFont(getActivity(), R.font.montserrat_light);
        tfBold = ResourcesCompat.getFont(getActivity(), R.font.montserrat_bold);

        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentAnaliticsViewModel.class);

        setupViews();
        loadRentAnalitics();

    }

    private void setupViews() {
        profileBinding.ivExportRevenues.setOnClickListener(this);
        scrollBounds = new Rect();
        profileBinding.nestedScroll.getHitRect(scrollBounds);
        profileBinding.nestedScroll.setOnScrollChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        if (context instanceof ProfileInteraction)
            interaction = (ProfileInteraction) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        compositeDisposable.clear();
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void loadRentAnalitics() {
//            profileBinding.llRentTitleContent.setVisibility(View.GONE);
        disposable = viewModel.allRentByUserId(token, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null) {
                        updateSpinnerView(listResource.data);
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
//            disposable = viewModel.getRentByUuidList(Arrays.asList(rentSelect))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(rentAndGaleries -> {
//                        if (rentAndGaleries.size() > 1) {
//                            updateSpinnerView(rentAndGaleries);
//                        } else if (rentAndGaleries.size() == 1) {
//                            updateSingleView(rentAndGaleries);
//                        }
//                    }, Timber::e);
//            compositeDisposable.add(disposable);
    }

//    private void updateSingleView(ArrayList<HorizontalItem> horizontalItems) {
//        this.singleHorizontalItem = horizontalItems.get(0);
//        profileBinding.setShowSelect(false);
//        profileBinding.llRentTitleContent.setVisibility(View.VISIBLE);
//        profileBinding.tvRentName.setText(singleHorizontalItem.getRentName());
//        fetchRentAnalitic(singleHorizontalItem.getUuid(), null);
//    }

    private void updateSpinnerView(List<HorizontalItem> horizontalItems) {
//        profileBinding.setShowSelect(true);
        horizontalScrollAdapter = new HorizontalScrollAdapter(horizontalItems);
        profileBinding.discreteScroll.setSlideOnFling(true);
        profileBinding.discreteScroll.setAdapter(horizontalScrollAdapter);
        profileBinding.discreteScroll.addOnItemChangedListener(this);
        profileBinding.discreteScroll.addScrollStateChangeListener(this);
        profileBinding.discreteScroll.setItemTransitionTimeMillis(300);
        profileBinding.discreteScroll.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.9f).build());
        profileBinding.setInitialVisibility(View.VISIBLE);
    }

    //------------------------------------- HORIZONTAL SCROLL VIEW INTERFACES -------------------

    @Override
    public void onCurrentItemChanged(@Nullable HorizontalScrollAdapter.ViewHolder viewHolder, int adapterPosition) {
        viewHolder.showText();
        if (lastPosSelected != adapterPosition) {
            lastPosSelected = adapterPosition;
            commentIsAnimated = false;
            revenueIsAnimated = false;
            reservationIsAnimated = false;
            Picasso.get()
                    .load(horizontalScrollAdapter.getItem(adapterPosition).getPortrait())
                    .placeholder(R.drawable.placeholder)
                    .resize(600, 800)
                    .into(profileBinding.ivBackgroudRent);
            fetchRentAnalitic(horizontalScrollAdapter.getItem(adapterPosition).getUuid(),
                    horizontalScrollAdapter.getItem(adapterPosition).getRentMode());
        }
    }

    @Override
    public void onScrollStart(@NonNull HorizontalScrollAdapter.ViewHolder currentItemHolder, int adapterPosition) {
        currentItemHolder.hideText();
    }

    @Override
    public void onScrollEnd(@NonNull HorizontalScrollAdapter.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable HorizontalScrollAdapter.ViewHolder currentHolder, @Nullable HorizontalScrollAdapter.ViewHolder newCurrent) {

    }

    //-------------------------------------

    private void fetchRentAnalitic(String uuid, String rentMode) {
        if (networkHelper.isNetworkAvailable()) {
            profileBinding.setLoading(true);
            if (!disposable.isDisposed())
                disposable.dispose();
            disposable = viewModel.rentAnalitics(uuid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(analiticsGroup -> {
                        if (analiticsGroup != null) {
                            profileBinding.setLoading(false);
                            updateVisitLevelUI(analiticsGroup);
                            if (rentMode.equals("por noche")) {
                                profileBinding.setByNight(true);
                                updateRevenueLevelUI(analiticsGroup);
                                updateReservationSolvedLevelUI(analiticsGroup);
                                updateReservationLevelUI(analiticsGroup);
                            } else {
                                profileBinding.setByNight(false);
                            }
                            updateRatingLevelUI(analiticsGroup);
                            updateCommentLevelUI(analiticsGroup);
                            updatePositionLevelUI(analiticsGroup);

//                            profileBinding.pbDetailPercent.setProgress(((int) analiticsGroup.getProfilePercentAnalitics().getPercent()), true);


//
//                            configureCommentMPChart(analiticsGroup.getCommentReport(),
//                                    analiticsGroup.getEmotionReport());
//                            configureRatingMPChart(analiticsGroup.getRatingReport());
//
//                            updateRentDetailMissingFields(analiticsGroup);
//
//                            updatePositionView(analiticsGroup.getPositionReport());

                        }
                    }, throwable -> {
//                        Timber.e(throwable);
//                        if (throwable instanceof ConnectException) {
//                            profileBinding.progressPvCircularInout.stop();
//                            profileBinding.setNoConnection(true);
//                            AlertUtils.showErrorAlert(getActivity(), "Sin conexión");
//                        } else {
//                            AlertUtils.showErrorAlert(getActivity(), "Ooops!! un error a ocurrido");
//                        }
//                        resetAllViews();
                    });
        } else {
//            profileBinding.progressPvCircularInout.stop();
//            profileBinding.setNoConnection(true);
//            AlertUtils.showErrorAlert(getActivity(), "Sin conexión");
        }
    }


    private void updateVisitLevelUI(AnaliticsGroup analiticsGroup) {
        ValueAnimator visitAnimator = getAnimatorTextNumber(profileBinding.tvReviewCount, analiticsGroup.getVisitReport().getTotalVisit());
        ValueAnimator wishAnimator = getAnimatorTextNumber(profileBinding.tvWishedCount, analiticsGroup.getWishReport().getTotal());
        startNumberAnimation(visitAnimator, wishAnimator);
    }

    private void updateRevenueLevelUI(AnaliticsGroup analiticsGroup) {
        profileBinding.lineRevenueChart.getDescription().setEnabled(false);
        profileBinding.lineRevenueChart.setDrawGridBackground(false);

        if (analiticsGroup.getRevenueReport().getTotalRevenue() > 0) {
            ArrayList<Entry> revenueByMonth = new ArrayList<>();
            List<Month> monthList = analiticsGroup.getRevenueReport().getMonths();
            for (int i = 0; i < monthList.size(); i++) {
                revenueByMonth.add(new Entry(i, monthList.get(i).getRevenue()));
            }

            XAxis xAxis = profileBinding.lineRevenueChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTypeface(tfLight);
            xAxis.setAxisMinimum(0f);
            xAxis.setLabelCount(12, false);
            xAxis.setGranularity(1);
            xAxis.setLabelRotationAngle(45);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setValueFormatter((value, axis) -> {
                int index = (int) value % monthList.size();
                return monthList.get(index).getMonth().substring(0, 3);
            });

            //set max
            int max = analiticsGroup.getRevenueReport().getTotalRevenue() > 0 ? analiticsGroup.getRevenueReport().getTotalRevenue() : 100;

            YAxis leftAxis = profileBinding.lineRevenueChart.getAxisLeft();
            leftAxis.setTypeface(tfLight);
            leftAxis.setLabelCount(5, false);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            leftAxis.setAxisMaximum(max); // this replaces setStartAtZero(true)


            YAxis rightAxis = profileBinding.lineRevenueChart.getAxisRight();
            rightAxis.setTypeface(tfLight);
            rightAxis.setLabelCount(5, false);
            rightAxis.setAxisMinimum(0f);
            rightAxis.setAxisMaximum(max);
            rightAxis.setEnabled(false);

            LineDataSet d1 = new LineDataSet(revenueByMonth, "Ganancias totales(cuc) - " + analiticsGroup.getRevenueReport().getTotalRevenue());
            d1.setLineWidth(2.5f);
            d1.setCircleRadius(4.5f);
            d1.setColor(Color.rgb(64, 89, 128));
            d1.setFillColor(Color.rgb(64, 89, 128));
            d1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            d1.setCircleColor(Color.rgb(217, 184, 162));
            d1.setValueTypeface(tfLight);
            d1.setValueTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
            d1.setHighLightColor(Color.rgb(149, 165, 124));
            d1.setDrawValues(true);
            d1.setValueTypeface(tfBold);

            ArrayList<ILineDataSet> sets = new ArrayList<>();
            sets.add(d1);

            // set data
            profileBinding.lineRevenueChart.setData(new LineData(sets));

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            profileBinding.lineRevenueChart.invalidate();
        } else {
            profileBinding.lineRevenueChart.clear();
            profileBinding.lineRevenueChart.setNoDataText("No hay datos que mostrar");
            profileBinding.lineRevenueChart.setNoDataTextTypeface(tfBold);
        }
    }

    private void updateReservationSolvedLevelUI(AnaliticsGroup analiticsGroup) {
        ArrayList<BarEntry> values = new ArrayList<>();
        List<Month> monthList = analiticsGroup.getRevenueReport().getMonths();
        //set max
        int max = analiticsGroup.getRevenueReport().getTotalReservationCompleted() > 0 ? analiticsGroup.getRevenueReport().getTotalReservationCompleted() : 100;

        profileBinding.barReservationSolvedChart.getDescription().setEnabled(false);
        profileBinding.barReservationSolvedChart.setDrawGridBackground(false);

        XAxis xAxis = profileBinding.barReservationSolvedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(12, false);
        xAxis.setGranularity(1);
        xAxis.setLabelRotationAngle(45);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter((value, axis) -> {
            int index = (int) value % monthList.size();
            return monthList.get(index).getMonth().substring(0, 3);
        });

        YAxis leftAxis = profileBinding.barReservationSolvedChart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(max); // this replaces setStartAtZero(true)

        YAxis rightAxis = profileBinding.barReservationSolvedChart.getAxisRight();
        rightAxis.setEnabled(false);

        for (int i = 0; i < monthList.size(); i++) {
            values.add(new BarEntry(i, monthList.get(i).getReservationCompleted()));
        }

        BarDataSet set1;
        set1 = new BarDataSet(values, "Total - "+max);
        set1.setColors(Color.rgb(64, 89, 128));
        set1.setDrawValues(true);
        set1.setValueTypeface(tfBold);
        set1.setValueTextColor(Color.parseColor("#607D8B"));
        set1.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf((int)value));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        profileBinding.barReservationSolvedChart.setData(data);
        profileBinding.barReservationSolvedChart.setFitBars(true);


        profileBinding.barReservationSolvedChart.invalidate();

    }

    private void updateReservationLevelUI(AnaliticsGroup analiticsGroup) {
        profileBinding.pieReservationChart.setUsePercentValues(true);
        profileBinding.pieReservationChart.getDescription().setEnabled(false);
        profileBinding.pieReservationChart.setExtraOffsets(5, 5, 5, 5);

        profileBinding.pieReservationChart.setDragDecelerationFrictionCoef(0.95f);

        profileBinding.pieReservationChart.setCenterTextTypeface(tfLight);
        profileBinding.pieReservationChart.setDrawCenterText(false);

        profileBinding.pieReservationChart.setDrawHoleEnabled(true);
        profileBinding.pieReservationChart.setHoleColor(Color.WHITE);

        profileBinding.pieReservationChart.setTransparentCircleColor(Color.WHITE);
        profileBinding.pieReservationChart.setTransparentCircleAlpha(110);

        profileBinding.pieReservationChart.setHoleRadius(54f);
        profileBinding.pieReservationChart.setTransparentCircleRadius(57f);

        profileBinding.pieReservationChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        profileBinding.pieReservationChart.setRotationEnabled(true);
        profileBinding.pieReservationChart.setHighlightPerTapEnabled(true);


        Legend l = profileBinding.pieReservationChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        profileBinding.pieReservationChart.setDrawEntryLabels(false);

        if (analiticsGroup.getReservationReport().getTotalPending() +
                analiticsGroup.getReservationReport().getTotalAccepted() +
                analiticsGroup.getReservationReport().getTotalCanceled() +
                analiticsGroup.getReservationReport().getTotalBusyDays() > 0) {

            ArrayList<PieEntry> entries = new ArrayList<>();

            // NOTE: The order of the entries when being added to the entries array determines their position around the center of
            // the chart.
            int totalReservation = 0;
            if (analiticsGroup.getReservationReport().getTotalPending() > 0) {
                entries.add(new PieEntry(analiticsGroup.getReservationReport().getTotalPending(),
                        "Pendientes - " + analiticsGroup.getReservationReport().getTotalPending()));
                totalReservation += analiticsGroup.getReservationReport().getTotalPending();
            }
            if (analiticsGroup.getReservationReport().getTotalAccepted() > 0) {
                entries.add(new PieEntry(analiticsGroup.getReservationReport().getTotalAccepted(),
                        "Aceptadas - " + analiticsGroup.getReservationReport().getTotalAccepted()));
                totalReservation += analiticsGroup.getReservationReport().getTotalAccepted();
            }
            if (analiticsGroup.getReservationReport().getTotalCanceled() > 0) {
                entries.add(new PieEntry(analiticsGroup.getReservationReport().getTotalCanceled(),
                        "Canceladas - " + analiticsGroup.getReservationReport().getTotalCanceled()));
                totalReservation += analiticsGroup.getReservationReport().getTotalCanceled();
            }
            if (analiticsGroup.getReservationReport().getTotalBusyDays() > 0) {
                entries.add(new PieEntry(analiticsGroup.getReservationReport().getTotalBusyDays(),
                        "Días ocupados - " + analiticsGroup.getReservationReport().getTotalBusyDays()));
                totalReservation += analiticsGroup.getReservationReport().getTotalBusyDays();
            }

            PieDataSet dataSet = new PieDataSet(entries, "Total - " + totalReservation);

            dataSet.setDrawIcons(false);

            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colors = new ArrayList<>();
            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            data.setValueTypeface(tfLight);
            profileBinding.pieReservationChart.setData(data);

            // undo all highlights
            profileBinding.pieReservationChart.highlightValues(null);

            profileBinding.pieReservationChart.invalidate();

        } else {
            profileBinding.pieReservationChart.clear();
            profileBinding.pieReservationChart.setNoDataText("No hay datos que mostrar");
            profileBinding.pieReservationChart.setNoDataTextTypeface(tfBold);
        }
    }

    private void updateRatingLevelUI(AnaliticsGroup analiticsGroup) {
        int five = analiticsGroup.getRatingReport().getFiveStar();
        int four = analiticsGroup.getRatingReport().getFourStar();
        int three = analiticsGroup.getRatingReport().getThreeStar();
        int two = analiticsGroup.getRatingReport().getTwoStar();
        int one = analiticsGroup.getRatingReport().getOneStar();
        if (five + four + three + two + one > 0) {
            profileBinding.setHasRating(true);

            int raters[] = new int[]{
                    five, four, three, two, one
            };

            profileBinding.ratingReviews.setMaxBarValue(analiticsGroup.getRatingReport().getTotalVotes());
            profileBinding.ratingReviews.createRatingBars(analiticsGroup.getRatingReport().getTotalVotes(), BarLabels.STYPE5, Color.parseColor("#00BFA5"), raters);
            profileBinding.ratingBar.setRating(analiticsGroup.getRatingReport().getRatingAverage());
            profileBinding.tvRatingProm.setText(String.format(Locale.getDefault(), "%.1f", analiticsGroup.getRatingReport().getRatingAverage()));
            profileBinding.tvRatingUsers.setText(String.valueOf(analiticsGroup.getRatingReport().getTotalVotes()));
        } else {
            profileBinding.setHasRating(false);
        }
    }

    private void updateCommentLevelUI(AnaliticsGroup analiticsGroup) {
        profileBinding.pieEmotionChart.setUsePercentValues(true);
        profileBinding.pieEmotionChart.getDescription().setEnabled(false);
        profileBinding.pieEmotionChart.setExtraOffsets(5, 5, 5, 5);

        profileBinding.pieEmotionChart.setDragDecelerationFrictionCoef(0.95f);

        profileBinding.pieEmotionChart.setCenterTextTypeface(tfLight);
        profileBinding.pieEmotionChart.setDrawCenterText(false);

        profileBinding.pieEmotionChart.setDrawHoleEnabled(true);
        profileBinding.pieEmotionChart.setHoleColor(Color.WHITE);

        profileBinding.pieEmotionChart.setTransparentCircleColor(Color.WHITE);
        profileBinding.pieReservationChart.setTransparentCircleAlpha(110);

        profileBinding.pieEmotionChart.setHoleRadius(54f);
        profileBinding.pieEmotionChart.setTransparentCircleRadius(57f);

        profileBinding.pieEmotionChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        profileBinding.pieEmotionChart.setRotationEnabled(true);
        profileBinding.pieEmotionChart.setHighlightPerTapEnabled(true);

        // chart.spin(2000, 0, 360);

        Legend l = profileBinding.pieEmotionChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        profileBinding.pieEmotionChart.setDrawEntryLabels(false);

        if (analiticsGroup.getEmotionReport().getEmotionAverage() > 0) {
            ArrayList<PieEntry> entries = new ArrayList<>();

            // NOTE: The order of the entries when being added to the entries array determines their position around the center of
            // the chart.
            if (analiticsGroup.getEmotionReport().getTerribleCount() > 0)
                entries.add(new PieEntry(analiticsGroup.getEmotionReport().getTerribleCount(),
                        "Furiosos - " + analiticsGroup.getEmotionReport().getTerribleCount()));
            if (analiticsGroup.getEmotionReport().getBadCount() > 0)
                entries.add(new PieEntry(analiticsGroup.getEmotionReport().getBadCount(),
                        "Disgustados - " + analiticsGroup.getEmotionReport().getBadCount()));
            if (analiticsGroup.getEmotionReport().getOkCount() > 0)
                entries.add(new PieEntry(analiticsGroup.getEmotionReport().getOkCount(),
                        "Serios - " + analiticsGroup.getEmotionReport().getOkCount()));
            if (analiticsGroup.getEmotionReport().getGoodCount() > 0)
                entries.add(new PieEntry(analiticsGroup.getEmotionReport().getGoodCount(),
                        "Contentos - " + analiticsGroup.getEmotionReport().getGoodCount()));
            if (analiticsGroup.getEmotionReport().getExcellentCount() > 0)
                entries.add(new PieEntry(analiticsGroup.getEmotionReport().getExcellentCount(),
                        "Excelentes - " + analiticsGroup.getEmotionReport().getExcellentCount()));

            PieDataSet dataSet = new PieDataSet(entries, "Estados de opinión");

            dataSet.setDrawIcons(false);

            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colors = new ArrayList<>();
            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            data.setValueTypeface(tfLight);
            profileBinding.pieEmotionChart.setData(data);

            // undo all highlights
            profileBinding.pieEmotionChart.highlightValues(null);

            profileBinding.pieEmotionChart.invalidate();
        } else {
            profileBinding.pieEmotionChart.clear();
            profileBinding.pieEmotionChart.setNoDataText("No hay datos que mostrar");
            profileBinding.pieEmotionChart.setNoDataTextTypeface(tfBold);
        }

        profileBinding.tvTotalCommentYear.setText(String.valueOf(analiticsGroup.getCommentReport().getTotalYear()));
        profileBinding.tvTotalCommentMonth.setText(String.valueOf(analiticsGroup.getCommentReport().getTotalMonth()));
        profileBinding.tvTotalCommentToday.setText(String.valueOf(analiticsGroup.getCommentReport().getTotalDay()));

    }

    private void updatePositionLevelUI(AnaliticsGroup analiticsGroup) {
        int totalNacionalRent = analiticsGroup.getPositionReport().getTotalNationalRent();
        int placeNationalRating = analiticsGroup.getPositionReport().getPlaceNationalRating();
        int placeNationalView = analiticsGroup.getPositionReport().getPlaceNationalViews();

        if (placeNationalRating > 0) {
            profileBinding.setHasPosRat(true);
            profileBinding.tvPosRatingNat.setText(String.valueOf(placeNationalRating));
            profileBinding.tvPosRatingTotal.setText(String.valueOf(totalNacionalRent));
        } else {
            profileBinding.setHasPosRat(false);
        }

        if (placeNationalView > 0) {
            profileBinding.setHasPosVis(true);
            profileBinding.tvPosVisNat.setText(String.valueOf(placeNationalView));
            profileBinding.tvVisTotalNat.setText(String.valueOf(totalNacionalRent));
        } else {
            profileBinding.setHasPosVis(false);
        }

    }


//    private void resetAllViews() {
//        //------- Profile percent
//        profileBinding.pbDetailPercent.setProgress(0, true);
//        //------- Number
//        profileBinding.tvVisitCount.setText(getString(R.string.empty_short_text));
//        profileBinding.tvTotalListWish.setText(getString(R.string.empty_short_text));
//        //-------- Missing List
//        profileBinding.tvMissingList.setText(R.string.empty_text);
//
//        profileBinding.tvTotalYearComment.setText(getString(R.string.empty_short_text));
//        profileBinding.tvTotalMonthComment.setText(getString(R.string.empty_short_text));
//        profileBinding.tvTotalTodayComment.setText(getString(R.string.empty_short_text));
//
//        profileBinding.tvTotalVotes.setText(getString(R.string.empty_short_text));
//        profileBinding.tvVotesAverage.setText(getString(R.string.empty_short_text));
//
//    }

    private void startNumberAnimation(ValueAnimator... valueAnimator) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator);
        animatorSet.start();
    }

    @NonNull
    private ValueAnimator getAnimatorTextNumber(TextView textView, int number) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, number);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> textView.setText(animation.getAnimatedValue().toString()));
        return valueAnimator;
    }

    //------------------------------- MENU --------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                if (rentSelect.length > 1) {
//                    profileBinding.discreteScroll.scrollToPosition(lastPosSelected);
////                    fetchRentAnalitic(horizontalScrollAdapter.getItem(lastPosSelected).getUuid());
//                } else if (rentSelect.length == 1) {
//                    fetchRentAnalitic(singleHorizontalItem.getUuid(), null);
//                }
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_export_revenues:
                if (interaction != null)
                    interaction.exportToGalery(profileBinding.lineRevenueChart, "Ganancias");
                break;
        }
    }

    @Override
    public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (!commentIsAnimated && profileBinding.llContentComment.getVisibility() == View.VISIBLE) {
            if (profileBinding.llContentComment.getLocalVisibleRect(scrollBounds)) {
                if (!profileBinding.llContentComment.getLocalVisibleRect(scrollBounds)
                        || scrollBounds.height() < profileBinding.llContentComment.getHeight()) {
                    commentIsAnimated = true;
                    profileBinding.pieEmotionChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
                }
            }
        }
        if (!revenueIsAnimated && profileBinding.llContentRevenue.getVisibility() == View.VISIBLE) {
            if (profileBinding.llContentRevenue.getLocalVisibleRect(scrollBounds)) {
                if (!profileBinding.llContentRevenue.getLocalVisibleRect(scrollBounds)
                        || scrollBounds.height() < profileBinding.llContentRevenue.getHeight()) {
                    revenueIsAnimated = true;
                    profileBinding.lineRevenueChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
                }
            }
        }
        if (!reservationIsAnimated && profileBinding.llContentReservation.getVisibility() == View.VISIBLE) {
            if (profileBinding.llContentReservation.getLocalVisibleRect(scrollBounds)) {
                if (!profileBinding.llContentReservation.getLocalVisibleRect(scrollBounds)
                        || scrollBounds.height() < profileBinding.llContentReservation.getHeight()) {
                    reservationIsAnimated = true;
                    profileBinding.pieReservationChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
                }
            }
        }
    }


    //----------------------- MP -------------------------------//

//    private void setRatingChartData(RatingReport ratingStarAnalitics) {
//        int one = ratingStarAnalitics.getOneStar();
//        int two = ratingStarAnalitics.getTwoStar();
//        int three = ratingStarAnalitics.getThreeStar();
//        int four = ratingStarAnalitics.getFourStar();
//        int five = ratingStarAnalitics.getFiveStar();
//        if (one + two + three + four + five > 0) {
//            profileBinding.chartRating.setVisibility(View.VISIBLE);
//            float barWidth = 8f;
//            ArrayList<BarEntry> values = new ArrayList<>();
//            ArrayList<Integer> colors = new ArrayList<>();
//            colors.add(ColorTemplate.rgb("#F44336"));
//            colors.add(ColorTemplate.rgb("#FF9800"));
//            colors.add(ColorTemplate.rgb("#4CAF50"));
//            colors.add(ColorTemplate.rgb("#00BCD4"));
//            colors.add(ColorTemplate.rgb("#2196F3"));
//            values.add(new BarEntry(0, one));
//            values.add(new BarEntry(10, two));
//            values.add(new BarEntry(20, three));
//            values.add(new BarEntry(30, four));
//            values.add(new BarEntry(40, five));
//
//            BarDataSet set1;
//            set1 = new BarDataSet(values, "Votos efectuados");
//            set1.setColors(colors);
//            set1.setDrawIcons(false);
//            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1);
//            BarData data = new BarData(dataSets);
//            data.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf((int) value));
//            data.setValueTextSize(10f);
//            data.setValueTypeface(tfLight);
//            data.setBarWidth(barWidth);
//            profileBinding.chartRating.setData(data);
//            profileBinding.chartRating.invalidate();
//        } else {
//            profileBinding.chartRating.setVisibility(View.GONE);
//        }
//    }

//    private void configureCommentMPChart(CommentReport generalCommentAnalitics, EmotionReport commentsEmotionAnalitics) {
//        profileBinding.tvTotalYearComment.setText(String.valueOf(generalCommentAnalitics.getTotalYear()));
//        profileBinding.tvTotalMonthComment.setText(String.valueOf(generalCommentAnalitics.getTotalMonth()));
//        profileBinding.tvTotalTodayComment.setText(String.valueOf(generalCommentAnalitics.getTotalDay()));
//
//        profileBinding.pieEmotion.getDescription().setEnabled(false);
//        profileBinding.pieEmotion.setExtraOffsets(5, 0, 5, 0);
//
//        profileBinding.pieEmotion.setCenterTextTypeface(tfLight);
//        profileBinding.pieEmotion.setCenterText(generateCenterText());
//        profileBinding.pieEmotion.setCenterTextSize(10);
//
//        profileBinding.pieEmotion.setDrawHoleEnabled(true);
//        profileBinding.pieEmotion.setHoleColor(Color.WHITE);
//
//        profileBinding.pieEmotion.setTransparentCircleColor(Color.WHITE);
//        profileBinding.pieEmotion.setTransparentCircleAlpha(110);
//
//        profileBinding.pieEmotion.setHoleRadius(60f);
//        profileBinding.pieEmotion.setTransparentCircleRadius(63f);
//
//        profileBinding.pieEmotion.setDrawCenterText(true);
//
//        profileBinding.pieEmotion.setRotationAngle(0);
//        // enable rotation of the chart by touch
//        profileBinding.pieEmotion.setRotationEnabled(false);
//        profileBinding.pieEmotion.setHighlightPerTapEnabled(true);
//
//        profileBinding.pieEmotion.animateY(1400);
//
//        Legend l = profileBinding.pieEmotion.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);
//
//        // entry label styling
//        profileBinding.pieEmotion.setDrawEntryLabels(false);
//
//        setEmotionChartData(commentsEmotionAnalitics);
//    }

//    private void setEmotionChartData(EmotionReport commentsEmotionAnalitics) {
//        int terrible = commentsEmotionAnalitics.getTerribleCount();
//        int bad = commentsEmotionAnalitics.getBadCount();
//        int ok = commentsEmotionAnalitics.getOkCount();
//        int good = commentsEmotionAnalitics.getGoodCount();
//        int excellent = commentsEmotionAnalitics.getExcellentCount();
//        ArrayList<PieEntry> entries1 = new ArrayList<>();
//        ArrayList<Integer> colors = new ArrayList<>();
//
//        if (terrible + bad + ok + good + excellent > 0) {
//            profileBinding.pieEmotion.setVisibility(View.VISIBLE);
//            if (commentsEmotionAnalitics.getTerribleCount() > 0) {
//                entries1.add(new PieEntry(commentsEmotionAnalitics.getTerribleCount(), "Terrible "));
//                colors.add(ColorTemplate.rgb("#F44336"));
//            }
//            if (commentsEmotionAnalitics.getBadCount() > 0) {
//                entries1.add(new PieEntry(commentsEmotionAnalitics.getBadCount(), "Malo "));
//                colors.add(ColorTemplate.rgb("#FF9800"));
//            }
//            if (commentsEmotionAnalitics.getOkCount() > 0) {
//                entries1.add(new PieEntry(commentsEmotionAnalitics.getOkCount(), "Mejorable "));
//                colors.add(ColorTemplate.rgb("#4CAF50"));
//            }
//            if (commentsEmotionAnalitics.getGoodCount() > 0) {
//                entries1.add(new PieEntry(commentsEmotionAnalitics.getGoodCount(), "Bueno "));
//                colors.add(ColorTemplate.rgb("#00BCD4"));
//            }
//            if (commentsEmotionAnalitics.getExcellentCount() > 0) {
//                entries1.add(new PieEntry(commentsEmotionAnalitics.getExcellentCount(), "Excelente "));
//                colors.add(ColorTemplate.rgb("#2196F3"));
//            }
//
//            PieDataSet ds1 = new PieDataSet(entries1, "");
//            ds1.setColors(colors);
//            ds1.setSliceSpace(2f);
//            ds1.setValueTextColor(Color.WHITE);
//            ds1.setValueTextSize(12f);
//
//            PieData d = new PieData(ds1);
//            d.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf((int) value));
//            d.setValueTypeface(tfLight);
//
//            profileBinding.pieEmotion.setData(d);
//            profileBinding.pieEmotion.invalidate();
//        } else {
//            profileBinding.pieEmotion.setDrawCenterText(false);
//            profileBinding.pieEmotion.setVisibility(View.GONE);
//        }
//
//    }

//    private SpannableString generateCenterText() {
//        SpannableString s = new SpannableString("Clasificación\ndel comentario");
//        s.setSpan(new RelativeSizeSpan(1f), 0, 13, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 13, s.length(), 0);
//        return s;
//    }
//


}

