package com.flycode.jasonfit.activity.activity;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.flycode.jasonfit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.male) RadioButton maleRadioButton;
    @BindView(R.id.female) RadioButton femaleRadioButton;

    @BindView(R.id.english) RadioButton englishRadioButton;
    @BindView(R.id.deutsch) RadioButton deutchRadioButton;

    @BindView(R.id.cm) RadioButton cmRadioButton;
    @BindView(R.id.foot) RadioButton footRadioButton;

    @BindView(R.id.kg) RadioButton kgRadioButton;
    @BindView(R.id.pound) RadioButton poundRadioButton;

    @BindView(R.id.try_for_free)
    LinearLayout tryLinearLayout;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        unbinder = ButterKnife.bind(this);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }

    @OnClick({R.id.male, R.id.female})
    public void onGenderChecked(View view) {
        if (view.getId() == R.id.male) {
            if (maleRadioButton.isChecked()) {
                femaleRadioButton.setChecked(false);
            }
        } else if (view.getId() == R.id.female) {
            if (femaleRadioButton.isChecked()) {
                maleRadioButton.setChecked(false);
            }
        }

    }

    @OnClick({R.id.english, R.id.deutsch})
    public void onLanguageChecked(View view) {
        if (view.getId() == R.id.english) {
            if (englishRadioButton.isChecked()) {
                deutchRadioButton.setChecked(false);
            }
        } else if (view.getId() == R.id.female) {
            if (deutchRadioButton.isChecked()) {
                englishRadioButton.setChecked(false);
            }
        }
    }

    @OnClick({R.id.cm, R.id.foot})
    public void onMeasurementChecked(View view) {
        if (view.getId() == R.id.male) {
            if (cmRadioButton.isChecked()) {
                footRadioButton.setChecked(false);
            }
        } else if (view.getId() == R.id.female) {
            if (footRadioButton.isChecked()) {
                cmRadioButton.setChecked(false);
            }
        }

    }

    @OnClick({R.id.kg, R.id.pound})
    public void onWeightChecked(View view) {
        if (view.getId() == R.id.male) {
            if (kgRadioButton.isChecked()) {
                poundRadioButton.setChecked(false);
            }
        } else if (view.getId() == R.id.female) {
            if (poundRadioButton.isChecked()) {
                kgRadioButton.setChecked(false);
            }
        }
    }

    @OnClick({R.id.try_for_free})
    public void onTryClicked(View view) {

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);

    }


}
