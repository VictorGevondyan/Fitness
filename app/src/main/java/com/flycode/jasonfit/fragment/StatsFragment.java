package com.flycode.jasonfit.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.StatsData;
import com.flycode.jasonfit.model.User;
import com.flycode.jasonfit.model.UserPreferences;
import com.flycode.jasonfit.util.UserNormsUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created - Schumakher on  3/7/17.
 */

public class StatsFragment extends Fragment {

    @BindView(R.id.chart) LineChart lineChart;

    @BindView(R.id.calendar) LinearLayout calendar;

    @BindView(R.id.calendar_title) TextView calendarTitle;
    @BindView(R.id.body_mass_overweight) TextView bodyMassOverweight;
    @BindView(R.id.body_mass_overweight_title) TextView bodyMassOverweightTitle;
    @BindView(R.id.calories_burnt_this_week) TextView burntThisWeek;
    @BindView(R.id.calories_burnt_last_week) TextView burntLastWeek;
    @BindView(R.id.calories_burnt_record_week) TextView burntRecordWeek;

    private UserPreferences userPreferences;

    private Unbinder unbinder;

    private  ArrayList<View> calendarItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        calendarItems = new ArrayList<>();
        userPreferences = User.sharedPreferences(getActivity());

        View statsView = inflater.inflate(R.layout.fragment_stats, container, false);

        unbinder = ButterKnife.bind(this, statsView);

        createChart();

        createCalendar(inflater);

        setupView();

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

        for (int i = 0; i < statsData.size() - 1; i++) {
            int weight = statsData.get(i).weight;
            entryList.add(new Entry(i, weight));
        }

        LineDataSet chartDataSet = new LineDataSet(entryList, "weight");
        chartDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        chartDataSet.setColor(getResources().getColor(R.color.colorGrayDark));
        chartDataSet.setLineWidth(3);

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

        int index;
        for( index = 0; index < 7; index++ ){
            View itemCalendar = layoutInflater.inflate( R.layout.item_calendar, calendar, false);
            calendar.addView(itemCalendar);
            calendarItems.add(itemCalendar);
        }
    }

    private void setupView() {

        setupCalendarTitle();
        setupOverWeight();
        setupCalendarView();
        setupBurntCalories();
    }

    private void setupCalendarTitle() {
        Calendar currentCalendar = Calendar.getInstance();

        currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        int yearMonday = currentCalendar.get(Calendar.YEAR);
        int monthMonday = currentCalendar.get(Calendar.MONTH)+1;
        int dayMonday = currentCalendar.get(Calendar.DAY_OF_MONTH);

        currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        int yearSunday = currentCalendar.get(Calendar.YEAR);
        int monthSunday = currentCalendar.get(Calendar.MONTH)+1;
        int daySunday = currentCalendar.get(Calendar.DAY_OF_MONTH);

        String calendarTitleString = getResources().getString(R.string.week) +
                yearMonday + "/" + monthMonday + "/" + dayMonday + " - " +
                yearSunday + "/" +monthSunday + "/" + daySunday;
        calendarTitle.setText(calendarTitleString);
    }

    private void setupOverWeight() {
        ArrayList<Integer> weightArray = new ArrayList<>();
        StatsData statsData;

        for (int i = 0; i < 7; i++) {
            statsData = getWeekStatsData(Calendar.DAY_OF_WEEK, i);

            if (statsData != null) {
                weightArray.add(statsData.weight);
            }
        }

        int weightArraySize = weightArray.size();
        int lastWeight = weightArray.get(weightArraySize - 1);
        int height = userPreferences.getHeight();
        double bMI = UserNormsUtil.getBMI(lastWeight, height);
        String overweightCategory = UserNormsUtil.getOverweightType(getActivity(), bMI);


        bodyMassOverweight.setText(String.valueOf(bMI));
        bodyMassOverweightTitle.setText(overweightCategory);
    }

    private void setupCalendarView() {

        String[] weekDays = this.getResources().getStringArray(R.array.week_days);

        StatsData statsData;

        for (int i = 0; i < 7; i++) {

            TextView dayOfWeek = (TextView) calendarItems.get(i).findViewById(R.id.calendar_day_of_week);
            dayOfWeek.setText(weekDays[i]);

            TextView multiplier = (TextView) calendarItems.get(i).findViewById(R.id.calendar_multiplier);
            multiplier.setText("1x");

            TextView metric = (TextView) calendarItems.get(i).findViewById(R.id.calendar_metric);
            metric.setText(userPreferences.getWeightMeasurement());

            TextView weight = (TextView) calendarItems.get(i).findViewById(R.id.calendar_weight);

            statsData = getWeekStatsData(Calendar.DAY_OF_WEEK, i);

            if (statsData != null) {
                weight.setText(String.valueOf(statsData.weight));
            } else {
                weight.setText("_");
            }
        }
    }

    private void setupBurntCalories() {
        double burntCaloriesThisWeek = 0;

        StatsData statsData;
        ArrayList<Double> burntCaloriesWeek = new ArrayList<>();
        String kcal = getResources().getString(R.string.kcal);

        for (int i = 0; i < 7; i++) {

            statsData = getWeekStatsData(Calendar.DAY_OF_WEEK, i);

            if (statsData != null) {
                burntCaloriesThisWeek += statsData.burntCalories;
                burntCaloriesWeek.add(statsData.burntCalories);
            }
        }

        String burntThisWeekString = String.valueOf(burntCaloriesThisWeek) + kcal;
        burntThisWeek.setText(burntThisWeekString);

        String burntRecordWeekString = String .valueOf(Collections.max(burntCaloriesWeek)) + kcal;
        burntRecordWeek.setText(burntRecordWeekString);
    }

    private StatsData getWeekStatsData(int dayOfWeek, int index) {

        ArrayList<Integer> calendarWeekDays = new ArrayList<>(Arrays.asList(Calendar.MONDAY, Calendar.TUESDAY,
                Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
                Calendar.SATURDAY, Calendar.SUNDAY));

        StatsData statsData = null;

        Calendar currentCalendar = Calendar.getInstance();

        int currentYear = currentCalendar.get(Calendar.YEAR);

        currentCalendar.set(dayOfWeek, calendarWeekDays.get(index));
        int currentDayOfYearInWeek = currentCalendar.get(Calendar.DAY_OF_YEAR);

        try {

            statsData = new Select()
                    .from(StatsData.class)
                    .where("year = ?", currentYear)
                    .where("dayOfYear = ?", currentDayOfYearInWeek)
                    .executeSingle();
        } catch (Exception ignored) {
        }

        return statsData;
    }
}
