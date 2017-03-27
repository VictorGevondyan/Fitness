package com.flycode.jasonfit.adapter.viewHolder;

import android.content.Context;
import android.content.Intent;
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

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.FoodActivity;
import com.flycode.jasonfit.model.Food;
import com.flycode.jasonfit.model.Meal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.*;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by victor on 3/23/17.
 */


public class MealViewHolder extends ChildViewHolder {

    @BindView(R.id.title) TextView title;
    @BindView(R.id.meal_info_container) LinearLayout mealInfoContainerLinearLayout;

    public MealViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

    }

    public void bind(Meal meal) {
        Context context = title.getContext();

        mealInfoContainerLinearLayout.removeAllViews();

        String mealName = meal.name;
        title.setText(mealName);

        String mealIngredients = meal.ingredients;
        String mealInstructions = meal.instructions;

        String[] descriptorTitles = context.getResources().getStringArray(R.array.meal_descriptors);
        CharSequence[] descriptorTexts = {
                attributedSequence(mealIngredients.replaceAll("\\\\n", System.getProperty("line.separator")), context),
                attributedSequence(mealInstructions.replaceAll("\\\\n", System.getProperty("line.separator")), context)};

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
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

    private CharSequence attributedSequence(String sourceString, final Context context) {
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

                        Intent foodActivityIntent = new Intent(context, FoodActivity.class);
                        foodActivityIntent.putExtra(Constants.EXTRAS.FOOD, correspondFood);

                        context.startActivity(foodActivityIntent);

                    } else {

                        new MaterialDialog.Builder(context)
                                .title("Food not found")
                                .content("Sorry, this food not found")
                                .positiveText(R.string.ok)
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

}

