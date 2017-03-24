package com.flycode.jasonfit.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.model.Food;
import com.flycode.jasonfit.model.Meal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MealActivity extends AppCompatActivity {

    @BindView(R.id.meal_info_container)
    LinearLayout mealInfoContainerLinearLayout;
    @BindView(R.id.title)
    TextView mealNameTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meal);

        ButterKnife.bind(this);

        setupToolbar();


        Intent incomingIntent = getIntent();
        Meal correspondingMeal = (Meal) incomingIntent.getSerializableExtra(Constants.EXTRAS.MEAL);

        String mealName = correspondingMeal.name;
        mealNameTextView.setText(mealName);

        String mealIngredients = correspondingMeal.ingredients;
        String mealInstructions = correspondingMeal.instructions;

        String[] descriptorTitles = getResources().getStringArray(R.array.meal_descriptors);
        CharSequence[] descriptorTexts = {
                attributedSequence(mealIngredients.replaceAll("\\\\n", System.getProperty("line.separator"))),
                attributedSequence(mealInstructions.replaceAll("\\\\n", System.getProperty("line.separator")))};


        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int descriptorIndex = 0; descriptorIndex < descriptorTitles.length; descriptorIndex++) {
            View descriptorView = layoutInflater.inflate(R.layout.item_meal_descriptor, mealInfoContainerLinearLayout, false);
            TextView descriptorTitleView = (TextView) descriptorView.findViewById(R.id.title);
            TextView descriptorTextView = (TextView) descriptorView.findViewById(R.id.text);
            descriptorTitleView.setText(descriptorTitles[descriptorIndex]);
            descriptorTextView.setText(descriptorTexts[descriptorIndex]);

            descriptorTextView.setMovementMethod(LinkMovementMethod.getInstance());

            mealInfoContainerLinearLayout.addView(descriptorView);
        }
    }

    private CharSequence attributedSequence(String sourceString) {
        Pattern pattern = Pattern.compile("\\$");
        Matcher matcher = pattern.matcher(sourceString);
        CharSequence attributedSequence = "";
        int stringStartIndex = 0;

        while (matcher.find()) {

            int sectionStartIndex = matcher.start();
            attributedSequence = TextUtils.concat(attributedSequence, sourceString.substring(stringStartIndex, sectionStartIndex));

            matcher.find();

            int sectionEndIndex = matcher.start() + 1;

            final String stringToSpan = sourceString.substring(sectionStartIndex, sectionEndIndex).replaceAll("\\$", "");

            final SpannableString spannableString = new SpannableString(stringToSpan);

            spannableString.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View textView) {


                    Food correspondFood = new Select()
                            .from(Food.class)
                            .where("UPPER(food) = UPPER(?)", stringToSpan)
                            .executeSingle();

                    if ( correspondFood != null ) {

                        Intent foodActivityIntent = new Intent(MealActivity.this, FoodActivity.class);
                        foodActivityIntent.putExtra(Constants.EXTRAS.FOOD, correspondFood);

                        startActivity(foodActivityIntent);

                    } else {
                        Toast.makeText(MealActivity.this, "Sorry, this food not found", Toast.LENGTH_LONG).show();

                        new MaterialDialog.Builder(MealActivity.this)
                                .title("Food not found")
                                .content("Sorry, this food not found")
                                .positiveText(R.string.ok)
//                                .negativeText(R.string.disagree)
                                .show();

                    }

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }

            }, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            attributedSequence = TextUtils.concat(attributedSequence, spannableString);

            stringStartIndex = sectionEndIndex;

        }

        return attributedSequence;

    }

    private void setupToolbar() {

        toolbar.setTitle(R.string.meal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

}
