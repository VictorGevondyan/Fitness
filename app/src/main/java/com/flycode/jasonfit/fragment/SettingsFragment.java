package com.flycode.jasonfit.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.jasonfit.JasonFitApplication;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.MainActivity;
import com.flycode.jasonfit.model.User;
import com.flycode.jasonfit.model.UserPreferences;
import com.flycode.jasonfit.util.StringUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.weight_measurement) EditText weightMeasurementEditText;
    @BindView(R.id.height) EditText heightEditText;
    @BindView(R.id.weight) EditText weightEditText;

    @BindView(R.id.settingsCoordinatorLayout) CoordinatorLayout settingsCoordinatorLayout;

    private UserPreferences userPreferences;
    private Unbinder unbinder;

    private boolean mustShowSnackBar = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        unbinder = ButterKnife.bind(this, settingsView);
        userPreferences = User.sharedPreferences(JasonFitApplication.sharedApplication());

        heightEditText.setText(String.valueOf(userPreferences.getHeight()));
        weightEditText.setText(String.valueOf(userPreferences.getWeight()));
        birthdayEditText.setText(formattedBirthday());
        genderEditText.setText(formattedGender());
        nutritionEditText.setText(formattedNutrition());
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
        datePickerDialog.setMaxDate(Calendar.getInstance());

    }

    @OnClick(R.id.gender)
    public void onSetGender() {
        int selectedIndex = userPreferences.getGender().equals(User.GENDER.MALE) ? 0 : 1;

        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        materialDialogBuilder
                .title(R.string.gender)
                .items(R.array.gender)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                        String gender = which == 0 ? User.GENDER.MALE : User.GENDER.FEMALE;

                        String previousGender = userPreferences.getGender();
                        if( !previousGender.equals(gender) ){
                            mustShowSnackBar = true;
                        }

                        userPreferences
                                .edit()
                                .putGender(gender)
                                .apply();

                        genderEditText.setText(formattedGender());

                        showSnackbar();

                        return false;
                    }

                })
                .show();
    }

    @OnClick(R.id.nutrition)
    public void onSetNutrition() {
        String nutrition = userPreferences.getNutrition();
        int selectedIndex = 0;

        if (nutrition.equals(User.NUTRITION.ALL)) {
            selectedIndex = 0;
        } else if (nutrition.equals(User.NUTRITION.VEGETARIAN)) {
            selectedIndex = 1;
        } else if (nutrition.equals(User.NUTRITION.VEGAN)) {
            selectedIndex = 2;
        }

        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        materialDialogBuilder
                .title(R.string.nutrition)
                .items(R.array.nutrition)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        String nutrition = User.NUTRITION.ALL;

                        if (which == 0) {
                            nutrition = User.NUTRITION.ALL;
                        } else if (which == 1) {
                            nutrition = User.NUTRITION.VEGETARIAN;
                        } else if (which == 2) {
                            nutrition = User.NUTRITION.VEGAN;
                        }

                        String previousNutrition = userPreferences.getNutrition();
                        if( !previousNutrition.equals(nutrition) ){
                            mustShowSnackBar = true;
                        }

                        userPreferences
                                .edit()
                                .putNutrition(nutrition)
                                .apply();

                        nutritionEditText.setText(formattedNutrition());

                        showSnackbar();

                        return false;
                    }

                })
                .show();
    }

    @OnClick(R.id.language)
    public void onSetLanguage() {
        int selectedIndex = userPreferences.getLanguage().equals(User.LANGUAGE.ENGLISH) ? 0 : 1;

        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        materialDialogBuilder
                .title(R.string.language)
                .items(R.array.language)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                        String language = which == 0 ? User.LANGUAGE.ENGLISH : User.LANGUAGE.DEUTSCH;

                        String previousLanguage = userPreferences.getLanguage();
                        if( !previousLanguage.equals(language) ){
                            mustShowSnackBar = true;
                        }


                        userPreferences
                                .edit()
                                .putLanguage(language)
                                .apply();

                        languageEditText.setText(formattedLanguage());

                        showSnackbar();

                        return false;
                    }
                })
                .show();
    }

    @OnClick(R.id.height_measurement)
    public void onSetHeightMeasurement() {
        int selectedIndex = userPreferences.getHeightMeasurement().equals(User.MEASUREMENTS.CM) ? 0 : 1;

        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        materialDialogBuilder
                .title(R.string.measurement)
                .items(R.array.height_measurement)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                        String heightMeasurement = which == 0 ? User.MEASUREMENTS.CM : User.MEASUREMENTS.FOOT;

                        String previousHeightMeasurement = userPreferences.getHeightMeasurement();
                        if( !previousHeightMeasurement.equals(heightMeasurement) ){
                            mustShowSnackBar = true;
                        }

                        userPreferences
                                .edit()
                                .putHeightMeasurement(heightMeasurement)
                                .apply();

                        heightMeasurementEditText.setText(formattedHeightMeasurement());

                        showSnackbar();

                        return false;
                    }
                })
                .show();
    }

    @OnClick(R.id.weight_measurement)
    public void onSetWeightMeasurement() {
        int selectedIndex = userPreferences.getWeightMeasurement().equals(User.MEASUREMENTS.KG) ? 0 : 1;

        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        materialDialogBuilder
                .title(R.string.measurement)
                .items(R.array.weight_measurement)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                        String weightMeasurement = which == 0 ? User.MEASUREMENTS.KG : User.MEASUREMENTS.POUND;

                        String previousWeightMeasurement = userPreferences.getWeightMeasurement();
                        if( !previousWeightMeasurement.equals(weightMeasurement) ){
                            mustShowSnackBar = true;
                        }

                        userPreferences
                                .edit()
                                .putWeightMeasurement(weightMeasurement)
                                .apply();

                        weightMeasurementEditText.setText(formattedWeightMeasurement());

                        showSnackbar();

                        return false;
                    }
                })
                .show();

    }

    @OnClick(R.id.height)
    public void onSetHeight() {

        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        materialDialogBuilder
                .title(R.string.height)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .alwaysCallInputCallback()
                .input("", String.valueOf(userPreferences.getHeight()), new MaterialDialog.InputCallback() {

                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.length() == 0) {
                            dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            return;
                        }

                        try {
                            int height = Integer.valueOf(input.toString());

                            if (height < 135 || height > 210) {
                                dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                return;
                            }

                            int previousHeight = userPreferences.getHeight();

                            if (previousHeight != height) {
                                mustShowSnackBar = true;
                            }

                            userPreferences
                                    .edit()
                                    .putHeight(height)
                                    .apply();

                            heightEditText.setText(input);

                            dialog.getInputEditText().setError(null);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        } catch (NumberFormatException e) {
                            dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        }
                    }

                }).show();

        materialDialogBuilder.onPositive(dataChangedButtonCallback);

    }

    @OnClick(R.id.weight)
    public void onSetCurrentWeight() {

        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        materialDialogBuilder
                .title(R.string.weight)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .alwaysCallInputCallback()
                .input("", String.valueOf(userPreferences.getWeight()), new MaterialDialog.InputCallback() {

                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.length() == 0) {
                            dialog.getInputEditText().setError(getString(R.string.please_enter_valid_weight));
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            return;
                        }

                        try {
                            int weight = Integer.valueOf(input.toString());

                            if ( weight < 20 || weight > 200) {
                                dialog.getInputEditText().setError(getString(R.string.please_enter_valid_weight));
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                return;
                            }

                            int previousWeight = userPreferences.getWeight();

                            if (previousWeight != weight) {
                                mustShowSnackBar = true;
                            }

                            userPreferences
                                    .edit()
                                    .putWeight(weight)
                                    .apply();

                            weightEditText.setText(input);

                            dialog.getInputEditText().setError(null);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        } catch (NumberFormatException e) {
                            dialog.getInputEditText().setError(getString(R.string.please_enter_valid_weight));
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        }

                    }

                }).show();
        materialDialogBuilder.onPositive(dataChangedButtonCallback);
    }

    MaterialDialog.SingleButtonCallback dataChangedButtonCallback = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            showSnackbar();
        }
    };

    public void showSnackbar(){
        boolean showSnackbar = mustShowSnackBar && ( getActivity() instanceof MainActivity );
        mustShowSnackBar = false;

        if( !showSnackbar ) {
            return;
        }

        Snackbar
                .make(settingsCoordinatorLayout, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .show();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Calendar previousDate = Calendar.getInstance();
        previousDate.setTimeInMillis(userPreferences.getBirthday());

        int previousYear = previousDate.get(Calendar.YEAR);
        int previousMonth = previousDate.get(Calendar.MONTH);
        int previousDay = previousDate.get(Calendar.DAY_OF_MONTH);

        boolean dateIsChanged = ( previousYear != year ) || ( previousMonth != monthOfYear ) || ( previousDay != dayOfMonth );

        if ( dateIsChanged) {
            mustShowSnackBar = true;
        }


        long birthday = calendar.getTimeInMillis();
        userPreferences
                .edit()
                .putBirthday(birthday)
                .apply();

        birthdayEditText.setText(formattedBirthday());

        showSnackbar();

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
        return userPreferences.getHeightMeasurement().equals(User.MEASUREMENTS.CM) ? R.string.cm : R.string.foot;
    }

    private int formattedWeightMeasurement() {
        return userPreferences.getWeightMeasurement().equals(User.MEASUREMENTS.KG) ? R.string.kg : R.string.pound;
    }

    private String formattedNutrition() {
        String nutrition = userPreferences.getNutrition();
        int selectedIndex = 0;

        if (nutrition.equals(User.NUTRITION.ALL)) {
            selectedIndex = 0;
        } else if (nutrition.equals(User.NUTRITION.VEGETARIAN)) {
            selectedIndex = 1;
        } else if (nutrition.equals(User.NUTRITION.VEGAN)) {
            selectedIndex = 2;
        }

        return getResources().getStringArray(R.array.nutrition)[selectedIndex];
    }

}
