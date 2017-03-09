package com.flycode.jasonfit.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Created by victor on 3/7/17.
 */

public class SettingsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    @BindView(R.id.nutrition) EditText nutritionEditText;
    @BindView(R.id.birthday) EditText birthdayEditText;
    @BindView(R.id.gender) EditText genderEditText;
    @BindView(R.id.language) EditText languageEditText;
    @BindView(R.id.height_measurement) EditText heightMeasurementEditText;
    @BindView(R.id.width_measurement) EditText weightMeasurementEditText;

    private UserPreferences userPreferences;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        unbinder = ButterKnife.bind(this, settingsView);
        userPreferences = User.sharedPreferences(JasonFitApplication.sharedApplication());

        birthdayEditText.setText(formattedBirthday());
        genderEditText.setText(formattedGender());
        languageEditText.setText(formattedLanguage());
        heightMeasurementEditText.setText(formattedHeightMeasurement());
        weightMeasurementEditText.setText(formattedWeightMeasurement());

        return settingsView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @OnClick(R.id.birthday)
    public void onSetBirthday() {
        Calendar initialTime = Calendar.getInstance();
        initialTime.setTimeInMillis(userPreferences.getBirthday());
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                initialTime.get(Calendar.YEAR),
                initialTime.get(Calendar.MONTH),
                initialTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.showYearPickerFirst(true);
        datePickerDialog.show(getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.gender)
    public void onSetGender() {
        int selectedIndex = userPreferences.getGender().equals(User.GENDER.MALE) ? 0 : 1;

        new MaterialDialog.Builder(getActivity())
                .title(R.string.gender)
                .items(R.array.gender)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        userPreferences
                                .edit()
                                .putGender(which == 0 ? User.GENDER.MALE : User.GENDER.FEMALE)
                                .apply();

                        genderEditText.setText(formattedGender());

                        return false;
                    }
                })
                .show();
    }

    @OnClick(R.id.language)
    public void onSetLanguage() {
        int selectedIndex = userPreferences.getGender().equals(User.LANGUAGE.ENGLISH) ? 0 : 1;

        new MaterialDialog.Builder(getActivity())
                .title(R.string.language)
                .items(R.array.language)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        userPreferences
                                .edit()
                                .putGender(which == 0 ? User.LANGUAGE.ENGLISH : User.LANGUAGE.DEUTSCH)
                                .apply();

                        languageEditText.setText(formattedLanguage());

                        return false;
                    }
                })
                .show();
    }

    @OnClick(R.id.height_measurement)
    public void onSetHeightMeasurement() {
        int selectedIndex = userPreferences.getHeightMeasurement().equals(User.MEASUREMENTS.CM) ? 0 : 1;

        new MaterialDialog.Builder(getActivity())
                .title(R.string.measurement)
                .items(R.array.height_measurement)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        userPreferences
                                .edit()
                                .putHeightMeasurement(which == 0 ? User.MEASUREMENTS.CM : User.MEASUREMENTS.FOOT)
                                .apply();

                        heightMeasurementEditText.setText(formattedHeightMeasurement());

                        return false;
                    }
                })
                .show();
    }

    @OnClick(R.id.height_measurement)
    public void onSetWeightMeasurement() {
        int selectedIndex = userPreferences.getWeightMeasurement().equals(User.MEASUREMENTS.KG) ? 0 : 1;

        new MaterialDialog.Builder(getActivity())
                .title(R.string.measurement)
                .items(R.array.weight_measurement)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        userPreferences
                                .edit()
                                .putHeightMeasurement(which == 0 ? User.MEASUREMENTS.KG : User.MEASUREMENTS.POUND)
                                .apply();

                        weightMeasurementEditText.setText(formattedWeightMeasurement());

                        return false;
                    }
                })
                .show();
    }

    @OnTextChanged(R.id.height)
    public void onHeightChanged(CharSequence input, int start, int count, int after) {
        if (input.length() == 0) {
            return;
        }

        try {
            int height = Integer.valueOf(input.toString());

            if (height <= 0) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.error)
                        .content(R.string.please_enter_valid_height)
                        .show();
                return;
            }

            userPreferences
                    .edit()
                    .putHeight(height)
                    .apply();
        } catch (NumberFormatException e) {
            e.printStackTrace();

            new MaterialDialog.Builder(getActivity())
                    .title(R.string.error)
                    .content(R.string.please_enter_valid_height)
                    .show();
        }
    }

    @OnTextChanged(R.id.weight)
    public void onWeightChanged(CharSequence input, int start, int count, int after) {
        if (input.length() == 0) {
            return;
        }

        try {
            int weight = Integer.valueOf(input.toString());

            if (weight <= 0) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.error)
                        .content(R.string.please_enter_valid_weight)
                        .show();
                return;
            }

            userPreferences
                    .edit()
                    .putWeight(weight)
                    .apply();
        } catch (NumberFormatException e) {
            e.printStackTrace();

            new MaterialDialog.Builder(getActivity())
                    .title(R.string.error)
                    .content(R.string.please_enter_valid_weight)
                    .show();
        }
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

    private int formattedGender() {
        return userPreferences.getGender().equals(User.GENDER.MALE) ? R.string.male : R.string.female;
    }

    private int formattedLanguage() {
        return userPreferences.getLanguage().equals(User.LANGUAGE.ENGLISH) ? R.string.english : R.string.deutsch;
    }

    private int formattedHeightMeasurement() {
        return userPreferences.getHeightMeasurement().equals(User.MEASUREMENTS.CM) ? R.string.cm: R.string.foot;
    }

    private int formattedWeightMeasurement() {
        return userPreferences.getWeightMeasurement().equals(User.MEASUREMENTS.KG) ? R.string.kg: R.string.pound;
    }
}
