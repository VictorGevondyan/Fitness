package com.flycode.jasonfit.fragment;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.jasonfit.JasonFitApplication;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.MainActivity;
import com.flycode.jasonfit.model.User;
import com.flycode.jasonfit.model.UserPreferences;
import com.flycode.jasonfit.util.MetricConverter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by victor on 3/7/17.
 */

public class SettingsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    @BindView(R.id.birthday) EditText birthdayEditText;
    @BindView(R.id.height) EditText heightEditText;
    @BindView(R.id.weight) EditText weightEditText;
    @BindView(R.id.spinner_weight) Spinner spinnerWeight;
    @BindView(R.id.spinner_height) Spinner spinnerHeight;
    @BindView(R.id.spinner_language) Spinner spinnerLanguage;
    @BindView(R.id.spinner_gender) Spinner spinnerGender;
    @BindView(R.id.spinner_nutrition) Spinner spinnerNutrition;

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

        heightEditText.setText(formattedHeight());
        weightEditText.setText(formattedWeight());
        birthdayEditText.setText(formattedBirthday());

        if (userPreferences.getWeightMeasurement().equals(User.METRICS.KG)) {
            spinnerWeight.setSelection(0);
        } else {
            spinnerWeight.setSelection(1);
        }
        spinnerWeight.setOnItemSelectedListener(weightItemSelected);


        if (userPreferences.getHeightMeasurement().equals(User.METRICS.CM)) {
            spinnerHeight.setSelection(0);
        } else {
            spinnerHeight.setSelection(1);
        }
        spinnerHeight.setOnItemSelectedListener(heightItemSelected);


        if (userPreferences.getLanguage().equals(User.LANGUAGE.ENGLISH)) {
            spinnerLanguage.setSelection(0);
        } else {
            spinnerLanguage.setSelection(1);
        }
        spinnerLanguage.setOnItemSelectedListener(languageItemClicked);

        if (userPreferences.getGender().equals(User.GENDER.MALE)) {
            spinnerGender.setSelection(0);
        } else {
            spinnerGender.setSelection(1);
        }
        spinnerGender.setOnItemSelectedListener(genderItemClicked);

        if (userPreferences.getNutrition().equals(User.NUTRITION.ALL)) {
            spinnerNutrition.setSelection(0);
        } else if (userPreferences.getNutrition().equals(User.NUTRITION.VEGETARIAN)) {
            spinnerNutrition.setSelection(1);
        } else {
            spinnerNutrition.setSelection(2);
        }
        spinnerNutrition.setOnItemSelectedListener(nutritionItemClicked);

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

    @OnClick(R.id.height)
    public void onSetHeight() {
        MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getActivity())
                .minValue(MetricConverter.convertHeight(135, userPreferences.getHeightMeasurement(), false))
                .maxValue(MetricConverter.convertHeight(210, userPreferences.getHeightMeasurement(), false))
                .defaultValue(MetricConverter.convertHeight(userPreferences.getHeight(), userPreferences.getHeightMeasurement(), false))
                .backgroundColor(getResources().getColor(R.color.colorWhite))
                .separatorColor(getResources().getColor(R.color.colorBlack))
                .textColor(getResources().getColor(R.color.colorBlack))
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new MaterialDialog.Builder(getActivity())
                .title(R.string.height)
                .customView(numberPicker, false)
                .positiveText(R.string.ok)
                .onPositive(dataChangedButtonCallback)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MaterialNumberPicker numberPicker = (MaterialNumberPicker) dialog.getCustomView();
                        userPreferences
                                .edit()
                                .putHeight(MetricConverter.convertHeight(numberPicker.getValue(), userPreferences.getHeightMeasurement(), true))
                                .apply();
                        heightEditText.setText(formattedHeight());
                    }
                })
                .show();


//                .inputType(InputType.TYPE_CLASS_NUMBER)
//                .alwaysCallInputCallback()
//                .input("", String.valueOf(userPreferences.getHeight()), new MaterialDialog.InputCallback() {
//
//                    @SuppressWarnings("ConstantConditions")
//                    @Override
//                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                        if (input.length() == 0) {
//                            dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
//                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
//                            return;
//                        }
//
//                        try {
//                            int height = Integer.valueOf(input.toString());
//                            height = MetricConverter.convertHeight(height, userPreferences.getHeightMeasurement(), true);
//
//                            if (height < 135 || height > 210) {
//                                dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
//                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
//                                return;
//                            }
//
//                            int previousHeight = userPreferences.getHeight();
//
//                            if (previousHeight != height) {
//                                mustShowSnackBar = true;
//                            }
//
//                            userPreferences
//                                    .edit()
//                                    .putHeight(height)
//                                    .apply();
//
//                            heightEditText.setText(formattedHeight());
//
//                            dialog.getInputEditText().setError(null);
//                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
//                        } catch (NumberFormatException e) {
//                            dialog.getInputEditText().setError(getString(R.string.please_enter_valid_height));
//                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
//                        }
//                    }
//
//                }).show();
    }

    @OnClick(R.id.weight)
    public void onSetCurrentWeight() {

        Activity parentActivity = getActivity();
        if( parentActivity instanceof MainActivity ){
            return;
        }

        MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getActivity())
                .minValue(MetricConverter.convertWeight(20, userPreferences.getWeightMeasurement(), false))
                .maxValue(MetricConverter.convertWeight(200, userPreferences.getWeightMeasurement(), false))
                .defaultValue(MetricConverter.convertWeight(userPreferences.getWeight(), userPreferences.getWeightMeasurement(), false))
                .backgroundColor(getResources().getColor(R.color.colorWhite))
                .separatorColor(getResources().getColor(R.color.colorBlack))
                .textColor(getResources().getColor(R.color.colorBlack))
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new MaterialDialog.Builder(getActivity())
                .title(R.string.weight)
                .customView(numberPicker, false)
                .positiveText(R.string.ok)
                .onPositive(dataChangedButtonCallback)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MaterialNumberPicker numberPicker = (MaterialNumberPicker) dialog.getCustomView();
                        userPreferences
                                .edit()
                                .putWeight(MetricConverter.convertWeight(numberPicker.getValue(), userPreferences.getWeightMeasurement(), true))
                                .apply();
                        weightEditText.setText(formattedWeight());
                    }
                })
                .show();

//        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());
//
//        materialDialogBuilder
//                .title(R.string.weight)
//                .inputType(InputType.TYPE_CLASS_NUMBER)
//                .alwaysCallInputCallback()
//                .input("", String.valueOf(userPreferences.getWeight()), new MaterialDialog.InputCallback() {
//
//                    @SuppressWarnings("ConstantConditions")
//                    @Override
//                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                        if (input.length() == 0) {
//                            dialog.getInputEditText().setError(getString(R.string.please_enter_valid_weight));
//                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
//                            return;
//                        }
//
//                        try {
//                            int weight = Integer.valueOf(input.toString());
//                            weight = MetricConverter.convertWeight(weight, userPreferences.getWeightMeasurement(), true);
//
//                            if ( weight < 20 || weight > 200) {
//                                dialog.getInputEditText().setError(getString(R.string.please_enter_valid_weight));
//                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
//                                return;
//                            }
//
//                            int previousWeight = userPreferences.getWeight();
//
//                            if (previousWeight != weight) {
//                                mustShowSnackBar = true;
//                            }
//
//                            userPreferences
//                                    .edit()
//                                    .putWeight(weight)
//                                    .apply();
//
//                            weightEditText.setText(formattedWeight());
//
//                            dialog.getInputEditText().setError(null);
//                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
//                        } catch (NumberFormatException e) {
//                            dialog.getInputEditText().setError(getString(R.string.please_enter_valid_weight));
//                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
//                        }
//                    }
//
//                }).show();
//        materialDialogBuilder.onPositive(dataChangedButtonCallback);

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

    private String formattedGender() {
        return getString(userPreferences.getGender().equals(User.GENDER.MALE) ? R.string.male : R.string.female);
    }

    private String formattedLanguage() {
        return getString(userPreferences.getLanguage().equals(User.LANGUAGE.ENGLISH) ? R.string.english : R.string.deutsch);
    }

    private String formattedHeightMeasurement() {
        return getString(userPreferences.getHeightMeasurement().equals(User.METRICS.CM) ? R.string.cm : R.string.inch);
    }

    private String formattedWeightMeasurement() {
        return getString(userPreferences.getWeightMeasurement().equals(User.METRICS.KG) ? R.string.kg : R.string.lbs);
    }

    private String formattedNutrition() {
        String nutrition = userPreferences.getNutrition();
        int selectedIndex = 0;

        switch (nutrition) {
            case User.NUTRITION.ALL:
                selectedIndex = 0;
                break;
            case User.NUTRITION.VEGETARIAN:
                selectedIndex = 1;
                break;
            case User.NUTRITION.VEGAN:
                selectedIndex = 2;
                break;
        }

        return getResources().getStringArray(R.array.nutrition)[selectedIndex];
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private String formattedHeight() {
        return new StringBuilder()
                .append(MetricConverter.convertHeight(userPreferences.getHeight(), userPreferences.getHeightMeasurement(), false))
                .append(" ")
                .append(formattedHeightMeasurement())
                .toString();
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private String formattedWeight() {
        return new StringBuilder()
                .append(MetricConverter.convertWeight(userPreferences.getWeight(), userPreferences.getWeightMeasurement(), false))
                .append(" ")
                .append(formattedWeightMeasurement())
                .toString();
    }

    //_______________WEIGHT___________________________-

    AdapterView.OnItemSelectedListener weightItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int selectedIndex = userPreferences.getWeightMeasurement().equals(User.METRICS.KG) ? 0 : 1;
            String weightMeasurement = selectedIndex == 0 ? User.METRICS.KG : User.METRICS.POUND;

            if( position != selectedIndex ){
                mustShowSnackBar = true;
                weightMeasurement = position == 0 ? User.METRICS.KG : User.METRICS.POUND;
            }

            userPreferences
                    .edit()
                    .putWeightMeasurement(weightMeasurement)
                    .apply();

            weightEditText.setText(formattedWeight());

            showSnackbar();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //________________HEIGHT__________________________-

    AdapterView.OnItemSelectedListener heightItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int selectedIndex = userPreferences.getHeightMeasurement().equals(User.METRICS.CM) ? 0 : 1;
            String heightMeasurement = selectedIndex == 0 ? User.METRICS.CM : User.METRICS.FOOT;

                            if( position != selectedIndex) {
                                mustShowSnackBar = true;
                                heightMeasurement = position == 0 ? User.METRICS.CM : User.METRICS.FOOT;
                            }

                            userPreferences
                                    .edit()
                                    .putHeightMeasurement(heightMeasurement)
                                    .apply();

                            heightEditText.setText(formattedHeight());

                            showSnackbar();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //___________________LANGUAGE_______________________-

    AdapterView.OnItemSelectedListener languageItemClicked = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int selectedIndex = userPreferences.getLanguage().equals(User.LANGUAGE.ENGLISH) ? 0 : 1;
            String language = selectedIndex == 0 ? User.LANGUAGE.ENGLISH : User.LANGUAGE.DEUTSCH;



                            if (position != selectedIndex ) {
                                mustShowSnackBar = true;
                                language = position == 0 ? User.LANGUAGE.ENGLISH : User.LANGUAGE.DEUTSCH;
                            }


                            userPreferences
                                    .edit()
                                    .putLanguage(language)
                                    .apply();

                            showSnackbar();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //___________________GENDER_______________-

    AdapterView.OnItemSelectedListener genderItemClicked = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int selectedIndex = userPreferences.getGender().equals(User.GENDER.MALE) ? 0 : 1;
            String gender = selectedIndex == 0 ? User.GENDER.MALE : User.GENDER.FEMALE;


                            if( position != selectedIndex) {
                                mustShowSnackBar = true;
                                gender = position == 0 ? User.GENDER.MALE : User.GENDER.FEMALE;
                            }

                            userPreferences
                                    .edit()
                                    .putGender(gender)
                                    .apply();

                            showSnackbar();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //_____________NUTRITION________________-

    AdapterView.OnItemSelectedListener nutritionItemClicked = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String nutrition = userPreferences.getNutrition();
            int selectedIndex = 0;

            switch (nutrition) {
                case User.NUTRITION.ALL:
                    selectedIndex = 0;
                    break;
                case User.NUTRITION.VEGETARIAN:
                    selectedIndex = 1;
                    break;
                case User.NUTRITION.VEGAN:
                    selectedIndex = 2;
                    break;
            }


                            switch (position) {
                                case 0:
                                    nutrition = User.NUTRITION.ALL;
                                    break;
                                case 1:
                                    nutrition = User.NUTRITION.VEGETARIAN;
                                    break;
                                case 2:
                                    nutrition = User.NUTRITION.VEGAN;
                                    break;
                            }


                            if( position != selectedIndex) {
                                mustShowSnackBar = true;
                            }

                            userPreferences
                                    .edit()
                                    .putNutrition(nutrition)
                                    .apply();


                            showSnackbar();

                        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
