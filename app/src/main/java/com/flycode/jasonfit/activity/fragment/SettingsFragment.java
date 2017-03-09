package com.flycode.jasonfit.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.flycode.jasonfit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by victor on 3/7/17.
 */

public class SettingsFragment extends Fragment {
    @BindView(R.id.male) RadioButton maleRadioButton;
    @BindView(R.id.female) RadioButton femaleRadioButton;

    @BindView(R.id.english) RadioButton englishRadioButton;
    @BindView(R.id.deutch) RadioButton deutchRadioButton;

    @BindView(R.id.cm) RadioButton cmRadioButton;
    @BindView(R.id.foot) RadioButton footRadioButton;

    @BindView(R.id.kg) RadioButton kgRadioButton;
    @BindView(R.id.pound) RadioButton poundRadioButton;


    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        unbinder = ButterKnife.bind(this, settingsView);

        return settingsView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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

    @OnClick({R.id.english, R.id.deutch})
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

}
