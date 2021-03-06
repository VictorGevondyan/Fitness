package com.flycode.jasonfit.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.StatsData;
import com.flycode.jasonfit.model.User;
import com.flycode.jasonfit.model.UserPreferences;
import com.flycode.jasonfit.util.MetricConverter;
import com.flycode.jasonfit.util.StringUtil;
import com.flycode.jasonfit.util.UserNormsUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created - Schumakher on  3/7/17.
 */

public class StatsFragment extends Fragment {

    @BindView(R.id.chart) LineChart lineChart;
    @BindView(R.id.calendar) LinearLayout calendarLayout;
    @BindView(R.id.calendar_title) TextView calendarTitleTextView;
    @BindView(R.id.body_mass_overweight) TextView bodyMassOverweight;
    @BindView(R.id.body_mass_overweight_title) TextView bodyMassOverweightTitle;
    @BindView(R.id.calories_burnt_this_week) TextView burntThisWeek;
    @BindView(R.id.calories_burnt_last_week) TextView burntLastWeek;
    @BindView(R.id.calories_burnt_record_week) TextView burntRecordWeek;
    @BindView(R.id.calendar_left) ImageButton calendarLeftButton;
    @BindView(R.id.calendar_right) ImageButton calendarRightButton;

    private UserPreferences userPreferences;

    private Unbinder unbinder;

    private  ArrayList<View> calendarItems;

    private int weekShiftCounter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        calendarItems = new ArrayList<>();
        userPreferences = User.sharedPreferences(getActivity());

        View statsView = inflater.inflate(R.layout.fragment_stats, container, false);
        weekShiftCounter = 0;

        unbinder = ButterKnife.bind(this, statsView);

        setupView(inflater);

        return statsView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void createChart(){
        List<StatsData> statsData = new Select()
                .from(StatsData.class)
                .execute();

        List<Entry> entryList = new ArrayList<>();

        for (int index = 0; index <= statsData.size() - 1; index++) {
            float weight = MetricConverter.convertWeight(statsData.get(index).weight, userPreferences.getWeightMeasurement(), false);
            entryList.add(new Entry(index, weight));
        }

        LineDataSet chartDataSet = new LineDataSet(entryList, "weight");
        chartDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        chartDataSet.setColor(getResources().getColor(R.color.colorBlack));
        chartDataSet.setCircleColor(getResources().getColor(R.color.colorBlack));
        chartDataSet.setDrawCircleHole(false);
        chartDataSet.setCircleRadius(5f);
        chartDataSet.setLineWidth(3f);
        chartDataSet.setDrawValues(false);

        LineData lineData = new LineData(chartDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        Description description = new Description();
        description.setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDescription(description);
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getAxisLeft().setSpaceMin(1);
        lineChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf(Math.round(value));
            }
        });
        lineChart.getAxisRight().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf(Math.round(value));
            }
        });
    }

    private void createCalendar( LayoutInflater layoutInflater ){
        for(int index = 0; index < 7; index++ ){
            View itemCalendar = layoutInflater.inflate( R.layout.item_calendar, calendarLayout, false);
            calendarLayout.addView(itemCalendar);
            calendarItems.add(itemCalendar);
        }
    }

    private void setupView(LayoutInflater inflater) {
        createChart();
        createCalendar(inflater);
        setupCalendarTitle(0);
        setupOverWeight();
        setupCalendarView(0);
        setupBurntCalories();
    }

    private void setupCalendarTitle(int weekShiftCount) {

        Calendar calendar = Calendar.getInstance();

        //___monday_____

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        //shift of days means what we go back fot eg -7 days -> 1 week earlier, it is handy to de/incre ment it by  *7 steps
        calendar.add(Calendar.DAY_OF_YEAR, weekShiftCount * 7);

        String monthMonday = new SimpleDateFormat("MMM", Locale.US).format(calendar.getTime());
        String dayMonday = new SimpleDateFormat("dd", Locale.US).format(calendar.getTime());

        //___sunday_____

        calendar.add(Calendar.DAY_OF_YEAR, 6);
        String monthSunday = new SimpleDateFormat("MMM", Locale.US).format(calendar.getTime());
        String daySunday = new SimpleDateFormat("dd", Locale.US).format(calendar.getTime());

        String calendarTitle = monthMonday + " " + dayMonday +
                " - " + monthSunday + " " + daySunday;
        calendarTitleTextView.setText(calendarTitle);
    }

    private void setupOverWeight() {
//        ArrayList<Float> weightArray = new ArrayList<>();
//        StatsData statsData;

//        for (int index = 0; index < 7; index++) {
//            Calendar calendar = getCalendarForWeek(0, index);
//            statsData = getStatsDataForCalendar(calendar);

//            if (statsData != null) {
//                weightArray.add(MetricConverter.convertWeight(statsData.weight, userPreferences.getWeightMeasurement(), false));
//            }
//        }

        float lastWeight = userPreferences.getWeight();
        float height = userPreferences.getHeight();
        double bMI = UserNormsUtil.getBMI(lastWeight, height);
        String overweightCategory = UserNormsUtil.getOverweightType(getActivity(), bMI);

        bodyMassOverweight.setText(String.valueOf(bMI));
        bodyMassOverweightTitle.setText(overweightCategory);
    }

    private void setupCalendarView(int weekShiftCount) {
        String[] weekDays = this.getResources().getStringArray(R.array.week_days);

        StatsData statsData;
        Calendar todayCalendar = Calendar.getInstance();

        for (int index = 0; index < 7; index++) {
            TextView dayOfWeekTextView = (TextView) calendarItems.get(index).findViewById(R.id.calendar_day_of_week);
            dayOfWeekTextView.setText(weekDays[index]);

            TextView multiplier = (TextView) calendarItems.get(index).findViewById(R.id.calendar_multiplier);
            String multiplierString;

            TextView metricTextView = (TextView) calendarItems.get(index).findViewById(R.id.calendar_metric);
            TextView weightTextView = (TextView) calendarItems.get(index).findViewById(R.id.calendar_weight);

            Calendar calendar = getCalendarForWeek(weekShiftCount * 7, index);
            statsData = getStatsDataForCalendar(calendar);

            if(todayCalendar.get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR)){
                dayOfWeekTextView.setAlpha(0.7f);
            } else {
                dayOfWeekTextView.setAlpha(1.0f);
            }

            if (statsData != null) {
                int multiplierNumber = statsData.multiplier;
                if( multiplierNumber == 0 ){
                    multiplierString = "";
                } else {
                    multiplierString = String.valueOf(statsData.multiplier) + "x";
                }

                weightTextView.setText(
                        String.valueOf(StringUtil.formattedDigitValue(MetricConverter.convertWeight(
                                                statsData.weight,
                                                userPreferences.getWeightMeasurement(),
                                                false))));
                metricTextView.setText(userPreferences.getWeightMeasurement().equals(User.METRICS.KG) ? R.string.kg : R.string.lbs);
            } else {
                weightTextView.setText("");
                multiplierString = "";
                metricTextView.setText("");
            }

            multiplier.setText(multiplierString);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupBurntCalories() {

        double burntCaloriesThisWeek = 0;

        StatsData statsData;
        ArrayList<Double> burntCaloriesThisWeekArray = new ArrayList<>();
        String kcalString = getResources().getString(R.string.kcal);

        for (int i = 0; i < 7; i++) {

            Calendar calendar = getCalendarForWeek(0, i);
            statsData = getStatsDataForCalendar(calendar);

            if (statsData != null) {
                burntCaloriesThisWeek += statsData.burntCalories;
                burntCaloriesThisWeekArray.add(statsData.burntCalories);
            }
        }

        String burntThisWeekString = String.valueOf(burntCaloriesThisWeek) + kcalString;
        burntThisWeek.setText(burntThisWeekString);

        String burntRecordWeekString = "0.0";

        if (burntCaloriesThisWeekArray.size() != 0) {

            burntRecordWeekString = String.valueOf(Collections.max(burntCaloriesThisWeekArray));
        }

        burntRecordWeek.setText(burntRecordWeekString + kcalString);

        //_________burnt last week____________

        double burntCaloriesLastWeek = 0;

        //noinspection UnusedAssignment
        statsData = null;

        for (int index = 0; index < 7; index++) {
            Calendar calendar = getCalendarForWeek(-7, index);
            statsData = getStatsDataForCalendar(calendar);

            if (statsData != null) {
                burntCaloriesLastWeek += statsData.burntCalories;
            }
        }

        burntLastWeek.setText(String.valueOf(burntCaloriesLastWeek) + kcalString);

    }

    private Calendar getCalendarForWeek(int weekShift, int dayOfWeek) {
        ArrayList<Integer> calendarWeekDays = new ArrayList<>(Arrays.asList(Calendar.MONDAY, Calendar.TUESDAY,
                Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
                Calendar.SATURDAY, Calendar.SUNDAY));

        Calendar currentCalendar = Calendar.getInstance();

        currentCalendar.set(Calendar.DAY_OF_WEEK, calendarWeekDays.get(dayOfWeek));
        //shift of days means what we go back fot eg -7 days -> 1 week earlier, it is handy to de/incre ment it by  *7 steps
        currentCalendar.add(Calendar.DAY_OF_YEAR, weekShift);

        return currentCalendar;
    }

    private StatsData getStatsDataForCalendar(Calendar calendar) {
        int currentDayOfYearInWeek = calendar.get(Calendar.DAY_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        try {
            return new Select()
                    .from(StatsData.class)
                    .where("year = ?", currentYear)
                    .where("dayOfYear = ?", currentDayOfYearInWeek)
                    .executeSingle();
        } catch (Exception ignored) {
            return null;
        }
    }

    @OnClick(R.id.calendar_left)
    public void onCalendarNavigateBack() {
        weekShiftCounter += -1;

        setupCalendarView(weekShiftCounter);

        setupCalendarTitle(weekShiftCounter);
    }

    @OnClick(R.id.calendar_right)
    public void onCalendarNavigateForward() {
        weekShiftCounter += 1;

        setupCalendarView(weekShiftCounter);

        setupCalendarTitle(weekShiftCounter);
    }
}
