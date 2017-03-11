package com.flycode.jasonfit.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.model.ProgressData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Schumakher on 3/7/17.
 */

public class StatsFragment extends Fragment {

    @BindView(R.id.chart) LineChart lineChart;

    @BindView(R.id.calendar) LinearLayout calendar;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View statsView = inflater.inflate(R.layout.fragment_stats, container, false);

        unbinder = ButterKnife.bind(this, statsView);

        createChart();

        createCalendar(inflater);

        return statsView;

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        unbinder.unbind();
    }

    private void createChart(){

        ArrayList<ProgressData> progressDataArrayList = new ArrayList<>();

        int index;
        ProgressData progressData;
        for( index = 0; index < 10; index++ ){
            progressData = new ProgressData( index, index);
            progressDataArrayList.add(progressData);
        }

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (ProgressData data : progressDataArrayList) {

            // turn your data into Entry objects
            entries.add( new Entry( data.getDate(), data.getWeight()) );

        }


        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(getResources().getColor(R.color.colorBlue));
        dataSet.setValueTextColor(getResources().getColor(R.color.colorGray)); // styling, ...


        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh

    }

    private void createCalendar( LayoutInflater layoutInflater ){

        int index;
        for( index = 0; index < 7; index++ ){
            View itemCalendar = layoutInflater.inflate( R.layout.item_calendar, calendar, false);
            calendar.addView(itemCalendar);
        }

    }

}
