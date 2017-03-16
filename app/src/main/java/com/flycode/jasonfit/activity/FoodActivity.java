package com.flycode.jasonfit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.Food;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodActivity extends AppCompatActivity {
    private static final String EXPEND_SETTINGS = "expendSettings";

    @BindView(R.id.food_info_container) LinearLayout foorInfoContainerLinearLayout;
    @BindView(R.id.title) TextView foodNameTextView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private static final int[] DESCRIPTOR_NAMES = new int[] {
            R.array.proximities,
            R.array.minerals,
            R.array.vitamins,
            R.array.lipids,
            R.array.other
    };

    private LinearLayout[] parameterContainers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        ButterKnife.bind(this);

        toolbar.setTitle(R.string.food);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                onBackPressed();
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        Food food = (Food) getIntent().getSerializableExtra(Constants.EXTRAS.FOOD);

        foodNameTextView.setText(food.food);

        String[] descriptors = getResources().getStringArray(R.array.food_descriptors);
        double[][] values = Food.getValues(food);

        parameterContainers = new LinearLayout[descriptors.length];

        for (int descriptorIndex = 0 ; descriptorIndex < descriptors.length ; descriptorIndex++) {
            View descriptorView = layoutInflater.inflate(R.layout.item_food_descriptor, foorInfoContainerLinearLayout, false);
            LinearLayout parameterContainerLinearLayout = (LinearLayout) descriptorView.findViewById(R.id.container);
            foorInfoContainerLinearLayout.addView(descriptorView);

            parameterContainers[descriptorIndex] = parameterContainerLinearLayout;

            TextView descriptorName = (TextView) descriptorView.findViewById(R.id.title);
            descriptorName.setText(descriptors[descriptorIndex]);

            String[] parameters = getResources().getStringArray(DESCRIPTOR_NAMES[descriptorIndex]);

            for (int parameterIndex = 0 ; parameterIndex < parameters.length ; parameterIndex++) {
                View parameterView = layoutInflater.inflate(R.layout.item_food_parameter, parameterContainerLinearLayout, false);
                TextView nameTextView = (TextView) parameterView.findViewById(R.id.name);
                TextView valueTextView = (TextView) parameterView.findViewById(R.id.value);

                nameTextView.setText(parameters[parameterIndex]);
                valueTextView.setText(String.valueOf(values[descriptorIndex][parameterIndex]));

                parameterContainerLinearLayout.addView(parameterView);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}