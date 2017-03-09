package com.flycode.jasonfit.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.JasonFitApplication;
import com.flycode.jasonfit.activity.model.User;
import com.flycode.jasonfit.activity.model.UserPreferences;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by victor on 3/7/17.
 */

public class SettingsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    @BindView(R.id.birthday) EditText birthdayEditText;

    @BindView(R.id.male) RadioButton maleRadioButton;
    @BindView(R.id.female) RadioButton femaleRadioButton;

    @BindView(R.id.english) RadioButton englishRadioButton;
    @BindView(R.id.deutsch) RadioButton deutschRadioButton;

    @BindView(R.id.cm) RadioButton cmRadioButton;
    @BindView(R.id.foot) RadioButton footRadioButton;

    @BindView(R.id.kg) RadioButton kgRadioButton;
    @BindView(R.id.pound) RadioButton poundRadioButton;

    private UserPreferences userPreferences;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        unbinder = ButterKnife.bind(this, settingsView);
        userPreferences = User.sharedPreferences(JasonFitApplication.sharedApplication());

        birthdayEditText.setText(formattedBirthday());

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

    @OnClick({R.id.english, R.id.deutsch})
    public void onLanguageChecked(View view) {
        if (view.getId() == R.id.english) {
            if (englishRadioButton.isChecked()) {
                deutschRadioButton.setChecked(false);
            }
        } else if (view.getId() == R.id.female) {
            if (deutschRadioButton.isChecked()) {
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

    @OnClick(R.id.birthday)
    public void onSetBirthday() {
        Calendar initialTime = Calendar.getInstance();
        initialTime.set(Calendar.YEAR, 1994);
        initialTime.set(Calendar.MONTH, Calendar.MAY);
        initialTime.set(Calendar.DAY_OF_MONTH, 10);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                initialTime.get(Calendar.YEAR),
                initialTime.get(Calendar.MONTH),
                initialTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.showYearPickerFirst(true);
        datePickerDialog.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        userPreferences
                .edit()
                .putBirthday(calendar.getTimeInMillis())
                .apply();

        birthdayEditText.setText(formattedBirthday());
    }

    private String formattedBirthday() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(new Date(userPreferences.getBirthday()));
    }
}
