package com.infinitum.bookingqba.view.profile;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentProfileBinding;
import com.infinitum.bookingqba.model.remote.pojo.CommentsEmotionAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.GeneralCommentAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.RatingStarAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.RentPositionAnalitics;
import com.infinitum.bookingqba.view.adapters.SpinnerAdapter;
import com.infinitum.bookingqba.view.adapters.items.chart.PieBean;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;
import com.infinitum.bookingqba.viewmodel.RentAnaliticsViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.openxu.cview.chart.bean.BarBean;
import com.openxu.cview.chart.bean.ChartLable;
import com.openxu.cview.chart.piechart.PieChartLayout;
import com.openxu.utils.DensityUtil;
import com.rey.material.widget.Spinner;
import com.squareup.picasso.Picasso;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.graphics.Color.rgb;
import static com.infinitum.bookingqba.util.Constants.BASE_URL_API;
import static com.infinitum.bookingqba.util.Constants.USER_AVATAR;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_RENTS;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding profileBinding;

    //MP
    private Typeface tfLight;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    private RentAnaliticsViewModel viewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    private CommonSpinnerList commonSpinnerList;
    private String[] rentSelect;
    private int lastPosSelected;

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

        tfLight = ResourcesCompat.getFont(getActivity(), R.font.poppinsregular);

        setHasOptionsMenu(true);

        profileBinding.setShowSelect(false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentAnaliticsViewModel.class);

        profileBinding.chartRating.setNoDataText("Cargando datos");

        loadUserData();

        loadRentAnalitics();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        compositeDisposable.clear();
    }

    private void loadRentAnalitics() {
        Set<String> stringSet = sharedPreferences.getStringSet(USER_RENTS, null);
        rentSelect = stringSet.toArray(new String[stringSet.size()]);
        if (rentSelect.length > 0) {
            profileBinding.tvRentName.setVisibility(View.GONE);
            disposable = viewModel.getRentSpinnerList(Arrays.asList(rentSelect))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(commonSpinnerList -> {
                        if (commonSpinnerList.getArrayNames().length > 1) {
                            updateSpinnerView(commonSpinnerList);
                        } else if (commonSpinnerList.getArrayNames().length == 1) {
                            updateSingleView(commonSpinnerList);
                        }
                    }, Timber::e);
            compositeDisposable.add(disposable);
        }
    }

    private void loadUserData() {
        String avatar = sharedPreferences.getString(USER_AVATAR, "");
        String url = BASE_URL_API + "/" + avatar;
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.user_placeholder)
                .into(profileBinding.userAvatar);
    }

    private void updateSingleView(CommonSpinnerList commonSpinnerList) {
        this.commonSpinnerList = commonSpinnerList;
        profileBinding.setShowSelect(false);
        profileBinding.tvRentName.setVisibility(View.VISIBLE);
        profileBinding.tvRentName.setText(commonSpinnerList.getArrayNames()[0]);
        profileBinding.tvMsg.setText("");
        fetchRentAnalitic(commonSpinnerList.getUuidOnPos(0));
    }

    private void updateSpinnerView(CommonSpinnerList commonSpinnerList) {
        this.commonSpinnerList = commonSpinnerList;
        String[] arrNames = commonSpinnerList.getArrayNames();
        profileBinding.setItems(arrNames);
        profileBinding.setShowSelect(true);
//        profileBinding.spinnerRents.post(() -> {
//            int height = profileBinding.spinnerRents.getHeight();
//            profileBinding.spinnerRents.setDropDownVerticalOffset(height);
//        });
        profileBinding.spinnerRents.setAdapter(new SpinnerAdapter(getActivity(), R.layout.spinner_profile_text_layout, arrNames));
        profileBinding.spinnerRents.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                lastPosSelected = position;
                String uuid = commonSpinnerList.getUuidOnPos(position);
                profileBinding.tvMsg.setText("");
                fetchRentAnalitic(uuid);
            }
        });
    }

    private void fetchRentAnalitic(String uuid) {
        profileBinding.setIsLoading(true);
        profileBinding.progressPvCircularInout.start();
        disposable = viewModel.rentAnalitics(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(analiticsGroup -> {
                    if (analiticsGroup != null) {
                        profileBinding.setIsLoading(false);
                        profileBinding.progressPvCircularInout.stop();

                        profileBinding.pbDetailPercent.setProgress(((int) analiticsGroup.getProfilePercentAnalitics().getPercent()), true);

                        ValueAnimator visitAnimator = getAnimatorTextNumber(profileBinding.tvVisitCount, analiticsGroup.getVisitAnalitics().getTotalVisit());
                        ValueAnimator wishAnimator = getAnimatorTextNumber(profileBinding.tvTotalListWish, analiticsGroup.getWishAnalitics().getTotalWish());
                        startNumberAnimation(visitAnimator, wishAnimator);

                        configureCommentMPChart(analiticsGroup.getGeneralCommentAnalitics(),
                                analiticsGroup.getCommentsEmotionAnalitics());
                        configureRatingMPChart(analiticsGroup.getRatingStarAnalitics());

                        List<String> missing = analiticsGroup.getProfilePercentAnalitics().getMissingList();
                        if (missing.size() > 0) {
                            profileBinding.llContentProfile.setVisibility(View.VISIBLE);
                            StringBuilder commaSeparate = new StringBuilder();
                            for (int i = 0; i < missing.size(); i++) {
                                if (i < missing.size() - 1) {
                                    commaSeparate.append(missing.get(i)).append(" \n");
                                } else {
                                    commaSeparate.append(missing.get(i));
                                }
                            }
                            profileBinding.tvMissingList.setText(commaSeparate.toString());
                        } else {
                            profileBinding.llContentProfile.setVisibility(View.GONE);
                        }

                        updatePositionView(analiticsGroup.getRentPositionAnalitics());

                    }
                }, throwable -> {
                    Timber.e(throwable);
                    if (throwable instanceof ConnectException) {
                        profileBinding.tvMsg.setText("Sin conexión");
                    } else {
                        profileBinding.tvMsg.setText("Ooops!! un error a ocurrido");
                    }
                    resetAllViews();
                });
        compositeDisposable.add(disposable);
    }

    private void resetAllViews() {
        //------- Profile percent
        profileBinding.pbDetailPercent.setProgress(0, true);
        //------- Number
        profileBinding.tvVisitCount.setText(getString(R.string.empty_short_text));
        profileBinding.tvTotalListWish.setText(getString(R.string.empty_short_text));
        //-------- Missing List
        profileBinding.tvMissingList.setText(R.string.empty_text);

    }

    private void startNumberAnimation(ValueAnimator... valueAnimator) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator);
        animatorSet.start();
    }

    @NonNull
    private ValueAnimator getAnimatorTextNumber(TextView textView, int number) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, number);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> textView.setText(animation.getAnimatedValue().toString()));
        return valueAnimator;
    }

    //------------------------------- MENU --------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (rentSelect.length > 1) {
                    fetchRentAnalitic(commonSpinnerList.getUuidOnPos(lastPosSelected));
                } else if (rentSelect.length == 1) {
                    fetchRentAnalitic(commonSpinnerList.getUuidOnPos(0));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------- MP -------------------------------//
    private void configureRatingMPChart(RatingStarAnalitics ratingStarAnalitics) {
        profileBinding.tvTotalVotes.setText(String.valueOf(ratingStarAnalitics.getTotalVotes()));
        profileBinding.tvVotesAverage.setText(String.valueOf(ratingStarAnalitics.getRatingAverage()));

        profileBinding.chartRating.setDrawBarShadow(false);
        profileBinding.chartRating.setDrawValueAboveBar(true);
        profileBinding.chartRating.getDescription().setEnabled(false);
        profileBinding.chartRating.setDrawGridBackground(false);

        String[] mLabels = new String[]{"1 Estrella", "2 Estrellas", "3 Estrellas", "4 Estrellas", "5 Estrellas"};

        XAxis xl = profileBinding.chartRating.getXAxis();
        xl.setValueFormatter((value, axis) -> mLabels[(int) value / 10]);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tfLight);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);

        YAxis yl = profileBinding.chartRating.getAxisLeft();
        yl.setTypeface(tfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f);

        YAxis yr = profileBinding.chartRating.getAxisRight();
        yr.setTypeface(tfLight);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        profileBinding.chartRating.setFitBars(true);
        profileBinding.chartRating.animateY(2500);

        Legend l = profileBinding.chartRating.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        profileBinding.chartRating.setScaleEnabled(false);
        setRatingChartData(ratingStarAnalitics);
    }


    private void setRatingChartData(RatingStarAnalitics ratingStarAnalitics) {
        float barWidth = 8f;
        ArrayList<BarEntry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ColorTemplate.rgb("#F44336"));
        colors.add(ColorTemplate.rgb("#FF9800"));
        colors.add(ColorTemplate.rgb("#4CAF50"));
        colors.add(ColorTemplate.rgb("#00BCD4"));
        colors.add(ColorTemplate.rgb("#2196F3"));
        values.add(new BarEntry(0, ratingStarAnalitics.getOneStar()));
        values.add(new BarEntry(10, ratingStarAnalitics.getTwoStar()));
        values.add(new BarEntry(20, ratingStarAnalitics.getThreeStar()));
        values.add(new BarEntry(30, ratingStarAnalitics.getFourStar()));
        values.add(new BarEntry(40, ratingStarAnalitics.getFiveStar()));

        BarDataSet set1;
        set1 = new BarDataSet(values, "Votos efectuados");
        set1.setColors(colors);
        set1.setDrawIcons(false);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        data.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf((int) value));
        data.setValueTextSize(10f);
        data.setValueTypeface(tfLight);
        data.setBarWidth(barWidth);
        profileBinding.chartRating.setData(data);
        profileBinding.chartRating.invalidate();
    }

    private void configureCommentMPChart(GeneralCommentAnalitics generalCommentAnalitics, CommentsEmotionAnalitics commentsEmotionAnalitics) {
        profileBinding.tvTotalYearComment.setText(String.valueOf(generalCommentAnalitics.getTotalYear()));
        profileBinding.tvTotalMonthComment.setText(String.valueOf(generalCommentAnalitics.getTotalMonth()));
        profileBinding.tvTotalTodayComment.setText(String.valueOf(generalCommentAnalitics.getTotalDay()));

        profileBinding.pieEmotion.getDescription().setEnabled(false);
        profileBinding.pieEmotion.setExtraOffsets(5, 0, 5, 0);

        profileBinding.pieEmotion.setDragDecelerationFrictionCoef(0.95f);

        profileBinding.pieEmotion.setCenterTextTypeface(tfLight);
        profileBinding.pieEmotion.setCenterText(generateCenterText());

        profileBinding.pieEmotion.setDrawHoleEnabled(true);
        profileBinding.pieEmotion.setHoleColor(Color.WHITE);

        profileBinding.pieEmotion.setTransparentCircleColor(Color.WHITE);
        profileBinding.pieEmotion.setTransparentCircleAlpha(110);

        profileBinding.pieEmotion.setHoleRadius(60f);
        profileBinding.pieEmotion.setTransparentCircleRadius(63f);

        profileBinding.pieEmotion.setDrawCenterText(true);

        profileBinding.pieEmotion.setRotationAngle(0);
        // enable rotation of the chart by touch
        profileBinding.pieEmotion.setRotationEnabled(true);
        profileBinding.pieEmotion.setHighlightPerTapEnabled(true);

        profileBinding.pieEmotion.animateY(1400);

        Legend l = profileBinding.pieEmotion.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        profileBinding.pieEmotion.setDrawEntryLabels(false);

        setEmotionChartData(commentsEmotionAnalitics);
    }


    private void setEmotionChartData(CommentsEmotionAnalitics commentsEmotionAnalitics) {
        ArrayList<PieEntry> entries1 = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (commentsEmotionAnalitics.getTerribleCount() > 0) {
            entries1.add(new PieEntry(commentsEmotionAnalitics.getTerribleCount(), "Terrible "));
            colors.add(ColorTemplate.rgb("#F44336"));
        }
        if (commentsEmotionAnalitics.getBadCount() > 0) {
            entries1.add(new PieEntry(commentsEmotionAnalitics.getBadCount(), "Malo "));
            colors.add(ColorTemplate.rgb("#FF9800"));
        }
        if (commentsEmotionAnalitics.getOkCount() > 0) {
            entries1.add(new PieEntry(commentsEmotionAnalitics.getOkCount(), "Mejorable "));
            colors.add(ColorTemplate.rgb("#4CAF50"));
        }
        if (commentsEmotionAnalitics.getGoodCount() > 0) {
            entries1.add(new PieEntry(commentsEmotionAnalitics.getGoodCount(), "Bueno "));
            colors.add(ColorTemplate.rgb("#00BCD4"));
        }
        if (commentsEmotionAnalitics.getExcellentCount() > 0) {
            entries1.add(new PieEntry(commentsEmotionAnalitics.getExcellentCount(), "Excelente "));
            colors.add(ColorTemplate.rgb("#2196F3"));
        }

        PieDataSet ds1 = new PieDataSet(entries1, "");
        ds1.setColors(colors);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);

        PieData d = new PieData(ds1);
        d.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf((int) value));
        d.setValueTypeface(tfLight);

        profileBinding.pieEmotion.setData(d);
        profileBinding.pieEmotion.invalidate();
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Clasificación\ndel comentario");
        s.setSpan(new RelativeSizeSpan(1f), 0, 13, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 13, s.length(), 0);
        return s;
    }

    private void updatePositionView(RentPositionAnalitics rentPositionAnalitics) {
        int totalNacionalRent = rentPositionAnalitics.getTotalNationalRent();
        int totalProvinceRent = rentPositionAnalitics.getTotalProvinceRent();
        int placeNationalRating = rentPositionAnalitics.getPlaceNationalRating();
        int placeProvinceRating = rentPositionAnalitics.getPlaceProvinceRating();
        int placeNationalView = rentPositionAnalitics.getPlaceNationalViews();
        int placeProvinceView = rentPositionAnalitics.getPlaceProvinceViews();
        String placeNatString = "Lugar <b>%s</b> de <b>%s</b> - <b>Nacional</b>";
        String placeProvString = "Lugar <b>%s</b> de <b>%s</b> - <b>Provincial</b>";

        if (placeNationalRating > 0) {
            String htmlText = String.format(placeNatString, placeNationalRating, totalNacionalRent);
            profileBinding.tvPlaceNationalRating.setText(Html.fromHtml(htmlText));
        } else {
            profileBinding.tvPlaceNationalRating.setText("Sin actividad");
        }

        if (placeProvinceRating > 0) {
            String htmlText = String.format(placeProvString, placeProvinceRating, totalProvinceRent);
            profileBinding.tvPlaceProvinceRating.setText(Html.fromHtml(htmlText));
        } else {
            profileBinding.tvPlaceProvinceRating.setText("Sin actividad");
        }

        if (placeNationalView > 0) {
            String htmlText = String.format(placeNatString, placeNationalView, totalNacionalRent);
            profileBinding.tvPlaceNationalView.setText(Html.fromHtml(htmlText));
        } else {
            profileBinding.tvPlaceNationalView.setText("Sin actividad");
        }

        if (placeProvinceView > 0) {
            String htmlText = String.format(placeProvString, placeProvinceView, totalProvinceRent);
            profileBinding.tvPlaceProvinceView.setText(Html.fromHtml(htmlText));
        } else {
            profileBinding.tvPlaceProvinceView.setText("Sin actividad");
        }

    }

}
