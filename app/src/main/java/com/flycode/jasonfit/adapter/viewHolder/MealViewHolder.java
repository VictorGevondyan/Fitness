package com.flycode.jasonfit.adapter.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.FoodActivity;
import com.flycode.jasonfit.model.Food;
import com.flycode.jasonfit.model.Meal;
import com.flycode.jasonfit.model.Translation;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        final Context context = title.getContext();

        mealInfoContainerLinearLayout.removeAllViews();

        String mealName = meal.name;
        title.setText(mealName);

        String[] descriptorTitles = context.getResources().getStringArray(R.array.meal_descriptors);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        addIngredients(context, meal, layoutInflater, descriptorTitles[0]);
        addInstructions(context, meal, layoutInflater, descriptorTitles[0]);
    }

    private void addIngredients(final Context context, Meal meal, LayoutInflater layoutInflater, String title) {
        String[] ingredientStrings = meal.ingredients.split("],\\[");
        Ingredient[] ingredients = new Ingredient[ingredientStrings.length];

        for (int index = 0 ; index < ingredientStrings.length ; index++) {
            Ingredient ingredient = new Ingredient();
            String ingredientString = ingredientStrings[index];
            ingredientString = ingredientString.replaceAll("\\[", "");
            ingredientString = ingredientString.replaceAll("]", "");
            String[] parts = ingredientString.split(",");
            ingredient.setQuantity(parts[0]);
            ingredient.setUnit(parts[1].replaceAll("'", ""));
            ingredient.setFood(parts[2].replaceAll("'", ""));

            ingredients[index] = ingredient;
        }

        View descriptorView = layoutInflater.inflate(R.layout.item_meal_descriptor, mealInfoContainerLinearLayout, false);
        TextView descriptorTitleView = (TextView) descriptorView.findViewById(R.id.title);
        LinearLayout descriptorContainer = (LinearLayout) descriptorView.findViewById(R.id.container);

        descriptorTitleView.setText(title);

        for (int descriptorIndex = 0; descriptorIndex < ingredients.length; descriptorIndex++) {
            Ingredient ingredient = ingredients[descriptorIndex];

            View parameterView = layoutInflater.inflate(R.layout.item_ingridient, mealInfoContainerLinearLayout, false);
            TextView quantityTextView = (TextView) parameterView.findViewById(R.id.quantity);
            TextView unitTextView = (TextView) parameterView.findViewById(R.id.unit);
            Button foodButton = (Button) parameterView.findViewById(R.id.food);

            final Food food = (Food) new Select().from(Food.class).where("food = ?", ingredient.getFood()).execute().get(0);
            foodButton.setText(food.getTranslatedName(context));

            if (ingredient.getUnit().isEmpty()) {
                unitTextView.setVisibility(View.GONE);
            } else {
                Translation translation = (Translation) new Select().from(Translation.class).where("name = ?", ingredient.getUnit()).execute().get(0);
                unitTextView.setText(translation.getTranslatedName(context));
            }
            if (Integer.valueOf(ingredient.getQuantity()) < 1) {
                quantityTextView.setVisibility(View.GONE);
            } else {
                quantityTextView.setText(ingredient.getQuantity());
            }

            foodButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, FoodActivity.class).putExtra(Constants.EXTRAS.FOOD, food));
                }
            });

            descriptorContainer.addView(parameterView);
        }

        mealInfoContainerLinearLayout.addView(descriptorView);
    }

    private void addInstructions(final Context context, Meal meal, LayoutInflater layoutInflater, String title) {
        String[] sectionStrings = meal.instructions.split("]],\\[");
        String[] sectionTitles = new String[sectionStrings.length];

        View descriptorView = layoutInflater.inflate(R.layout.item_meal_descriptor, mealInfoContainerLinearLayout, false);
        TextView descriptorTitleView = (TextView) descriptorView.findViewById(R.id.title);
        LinearLayout descriptorContainer = (LinearLayout) descriptorView.findViewById(R.id.container);

        descriptorTitleView.setText(title);

        for (int sectionIndex = 0 ; sectionIndex < sectionStrings.length ; sectionIndex++) {
            String sectionString = sectionStrings[sectionIndex];
            String sectionTitle = sectionString.split("',\\[")[0].replaceAll("'", "").replaceAll("]", "").replaceAll("\\[", "");
            String[] instructionStrings = sectionString.split("',\\[")[1].split("],\\[");

            View sectionView = layoutInflater.inflate(R.layout.item_instruction_section, mealInfoContainerLinearLayout, false);
            TextView sectionTitleView = (TextView) sectionView.findViewById(R.id.title);
            LinearLayout sectionContainer = (LinearLayout) sectionView.findViewById(R.id.container);

            Translation sectionTranslation = (Translation) new Select().from(Translation.class).where("name = ?", sectionTitle).execute().get(0);
            sectionTitleView.setText(sectionTranslation.getTranslatedName(context));

            Instruction[] instructions = new Instruction[instructionStrings.length];

            for (int index = 0 ; index < instructionStrings.length ; index++) {
                Instruction instruction = new Instruction();
                String instructionString = instructionStrings[index];
                instructionString = instructionString.replaceAll("\\[", "");
                instructionString = instructionString.replaceAll("]", "");
                String[] parts = instructionString.split(",");
                instruction.setAction(parts[0].replaceAll("'", ""));
                instruction.setQuantity(parts[1]);
                instruction.setUnit(parts[2].replaceAll("'", ""));
                instruction.setFood(parts[3].replaceAll("'", ""));

                instructions[index] = instruction;
            }

            for (int descriptorIndex = 0; descriptorIndex < instructions.length; descriptorIndex++) {
                Instruction instruction = instructions[descriptorIndex];

                View parameterView = layoutInflater.inflate(R.layout.item_instruction, mealInfoContainerLinearLayout, false);
                TextView actionTextView = (TextView) parameterView.findViewById(R.id.action);
                TextView quantityTextView = (TextView) parameterView.findViewById(R.id.quantity);
                TextView unitTextView = (TextView) parameterView.findViewById(R.id.unit);
                Button foodButton = (Button) parameterView.findViewById(R.id.food);

                Translation actionTranslation = (Translation) new Select().from(Translation.class).where("name = ?", instruction.getAction()).execute().get(0);
                actionTextView.setText(actionTranslation.getTranslatedName(context));

                if (instruction.getFood().isEmpty()) {
                    foodButton.setVisibility(View.GONE);
                } else {
                    final Food food = (Food) new Select().from(Food.class).where("food = ?", instruction.getFood()).execute().get(0);
                    foodButton.setText(food.getTranslatedName(context));

                    foodButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, FoodActivity.class).putExtra(Constants.EXTRAS.FOOD, food));
                        }
                    });
                }
                if (instruction.getUnit().isEmpty()) {
                    unitTextView.setVisibility(View.GONE);
                } else {
                    Translation translation = (Translation) new Select().from(Translation.class).where("name = ?", instruction.getUnit()).execute().get(0);
                    unitTextView.setText(translation.getTranslatedName(context));
                }
                if (Integer.valueOf(instruction.getQuantity()) < 1) {
                    quantityTextView.setVisibility(View.GONE);
                } else {
                    quantityTextView.setText(instruction.getQuantity());
                }

                sectionContainer.addView(parameterView);
            }

            descriptorContainer.addView(sectionView);
        }

        mealInfoContainerLinearLayout.addView(descriptorView);
    }

    private class Ingredient {
        private String quantity;
        private String unit;
        private String food;

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getFood() {
            return food;
        }

        public void setFood(String food) {
            this.food = food;
        }
    }

    private class Instruction {
        private String action;
        private String quantity;
        private String unit;
        private String food;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getFood() {
            return food;
        }

        public void setFood(String food) {
            this.food = food;
        }
    }
}

