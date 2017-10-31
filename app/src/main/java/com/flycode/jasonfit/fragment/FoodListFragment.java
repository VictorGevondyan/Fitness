package com.flycode.jasonfit.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.FoodActivity;
import com.flycode.jasonfit.adapter.FoodListAdapter;
import com.flycode.jasonfit.model.Food;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Created - Schumakher on  3/7/17.
 */

public class FoodListFragment extends Fragment implements FoodListAdapter.OnFoodItemClickListener, FoodListAdapter.OnAddFoodItemClickListener {

    @BindView(R.id.food) RecyclerView foodRecyclerView;
    @BindView(R.id.tab_bar) TabLayout tabLayout;

    private Unbinder unbinder;
    private FoodListAdapter foodListAdapter;
    private String searchQuery;
    private String category;

    private List<Food> foodList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View foodListView = inflater.inflate(R.layout.fragment_food_list, container, false);

        unbinder = ButterKnife.bind(this, foodListView);

        searchQuery = "";
        category = "";

        foodList = new Select().from(Food.class).execute();

        foodListAdapter = new FoodListAdapter(foodList, this, this);

        foodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        foodRecyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        foodRecyclerView.setAdapter(foodListAdapter);

        setupTabLayout();

        return foodListView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onFoodItemClick(Food food) {
        Intent intent = new Intent(getActivity(), FoodActivity.class)
                .putExtra(Constants.EXTRAS.FOOD, food);
        startActivity(intent);
    }

    private void setupTabLayout() {
        int[] foodCategories = new int[] {
                R.drawable.all,
                R.drawable.fruits,
                R.drawable.vegetables,
                R.drawable.dairy,
                R.drawable.drinks,
                R.drawable.sweets,
                R.drawable.meat,
                R.drawable.grain,
                R.drawable.nuts,
                R.drawable.spices
        };

        for (int foodCategory : foodCategories) {
            tabLayout.addTab(tabLayout.newTab().setIcon(foodCategory));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                From query = new Select().from(Food.class);

                if (position == 0) {
                    category = "";
                } else  if (position == 1) {
                    category = "Fruits";
                } else if (position == 2) {
                    category = "Vegetables";
                } else if (position == 3) {
                    category = "Dairy";
                } else if (position == 4) {
                    category = "Drinks";
                } else if (position == 5) {
                    category = "Sweets";
                } else if (position == 6) {
                    category = "Meat";
                } else if (position == 7) {
                    category = "Grains";
                } else if (position == 8) {
                    category = "Nuts";
                } else if (position == 9) {
                    category = "Spices";
                }

                List<Food> foodList;

                if (!category.isEmpty()) {
                    foodList = query.where("category = ?", category).execute();
                } else {
                    foodList = query.execute();
                }

                if (!searchQuery.isEmpty()) {
                    foodList = query
                            .where("food LIKE ?", searchQuery)
                            .execute();
                }

                foodListAdapter.setItems(foodList);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onAddFoodItemClickListener(final RecyclerView.ViewHolder viewHolder) {

        String count = String.valueOf(foodList.get(viewHolder.getAdapterPosition()).count);

        new MaterialDialog.Builder(getActivity())
                .title("type here food count in gramms")
                .inputRangeRes(1, 9, android.R.color.holo_red_light)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(null, count, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                        int inputInt = Integer.parseInt(input.toString());

                        if (inputInt < 0) {
                            return;
                        }

                        foodList.get(viewHolder.getAdapterPosition()).count = inputInt;
                        foodListAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                }).show();
    }

    @Override
    public void onRemoveFoodItemClickListener(Food food) {
//        if (!addedFoods.contains(food)) {
//            return;
//        }
//
//        addedFoods.remove(food);
    }


    @OnTextChanged(R.id.search_edit_text)
    public void onSearch(CharSequence charSequence) {
        From query = new Select().from(Food.class);
        searchQuery =  charSequence.toString();
        searchQuery = "%" + searchQuery + "%";

        if (!category.isEmpty()) {
            query.where("category = ?", category);
        }

        List<Food> foodList = query
                .where("food LIKE ?", searchQuery)
                .orderBy("food ASC")
                .execute();

        foodListAdapter.setItems(foodList);
    }

    @OnClick(R.id.calculate_button)
    public void onCalculateButtonClickListener() {

        if (foodList.size() == 0) {
            return;
        }

        Food food = new Food();

        for (int index = 0; index < foodList.size(); index++ ) {

            if (foodList.get(index).count == 0) {
                continue;
            }

            if (food.food == null) {

                food.food = foodList.get(index).food;
                food.foodDe = foodList.get(index).foodDe;
            } else {

                food.food += ( ", " + foodList.get(index).food);
                food.foodDe += ( ", " + foodList.get(index).foodDe);
            }

            food.water = roundedSum(food.water += foodList.get(index).water * foodList.get(index).count / 100);
            food.energy = roundedSum(food.energy += foodList.get(index).energy * foodList.get(index).count / 100);
            food.protein = roundedSum(food.protein += foodList.get(index).protein * foodList.get(index).count / 100);
            food.fat = roundedSum(food.fat += foodList.get(index).fat * foodList.get(index).count / 100);
            food.carbohydrate = roundedSum(food.carbohydrate += foodList.get(index).carbohydrate * foodList.get(index).count / 100);
            food.fiber = roundedSum(food.fiber += foodList.get(index).fiber * foodList.get(index).count / 100);
            food.sugars = roundedSum(food.sugars += foodList.get(index).sugars * foodList.get(index).count / 100);
            food.calcium = roundedSum(food.calcium += foodList.get(index).calcium * foodList.get(index).count / 100);
            food.iron = roundedSum(food.iron += foodList.get(index).iron * foodList.get(index).count / 100);
            food.magnesium = roundedSum(food.magnesium += foodList.get(index).magnesium * foodList.get(index).count / 100);
            food.phosphorus = roundedSum(food.phosphorus += foodList.get(index).phosphorus * foodList.get(index).count / 100);
            food.potassium = roundedSum(food.potassium += foodList.get(index).potassium * foodList.get(index).count / 100);
            food.sodium = roundedSum(food.sodium += foodList.get(index).sodium * foodList.get(index).count / 100);
            food.zinc = roundedSum(food.zinc += foodList.get(index).zinc * foodList.get(index).count / 100);
            food.vitaminC = roundedSum(food.vitaminC += foodList.get(index).vitaminC * foodList.get(index).count / 100);
            food.thiamin = roundedSum(food.thiamin += foodList.get(index).thiamin * foodList.get(index).count / 100);
            food.riboflavin = roundedSum(food.riboflavin += foodList.get(index).riboflavin * foodList.get(index).count / 100);
            food.niacin = roundedSum(food.niacin += foodList.get(index).niacin * foodList.get(index).count / 100);
            food.vitaminB6 = roundedSum(food.vitaminB6 += foodList.get(index).vitaminB6 * foodList.get(index).count / 100);
            food.folateDFE = roundedSum(food.folateDFE += foodList.get(index).folateDFE * foodList.get(index).count / 100);
            food.vitaminB12 = roundedSum(food.vitaminB12 += foodList.get(index).vitaminB12 * foodList.get(index).count / 100);
            food.vitaminARAE = roundedSum(food.vitaminARAE += foodList.get(index).vitaminARAE * foodList.get(index).count / 100);
            food.vitaminAIU = roundedSum(food.vitaminAIU += foodList.get(index).vitaminAIU * foodList.get(index).count / 100);
            food.vitaminE = roundedSum(food.vitaminE += foodList.get(index).vitaminE * foodList.get(index).count / 100);
            food.vitaminDD2D3 = roundedSum(food.vitaminDD2D3 += foodList.get(index).vitaminDD2D3 * foodList.get(index).count / 100);
            food.vitaminD = roundedSum(food.vitaminD += foodList.get(index).vitaminD * foodList.get(index).count / 100);
            food.vitaminK = roundedSum(food.vitaminK += foodList.get(index).vitaminK * foodList.get(index).count / 100);
            food.fattyAcidsTotalSaturated = roundedSum(food.fattyAcidsTotalSaturated += foodList.get(index).fattyAcidsTotalSaturated * foodList.get(index).count / 100);
            food.fattyAcidsTotalMonounsaturated = roundedSum(food.fattyAcidsTotalMonounsaturated += foodList.get(index).fattyAcidsTotalMonounsaturated * foodList.get(index).count / 100);
            food.fattyAcidsTotalPolyunsaturated = roundedSum(food.fattyAcidsTotalPolyunsaturated += foodList.get(index).fattyAcidsTotalPolyunsaturated * foodList.get(index).count / 100);
            food.fattyAcidsTotalTrans = roundedSum(food.fattyAcidsTotalTrans += foodList.get(index).fattyAcidsTotalTrans * foodList.get(index).count / 100);
            food.cholesterol = roundedSum(food.cholesterol += foodList.get(index).cholesterol * foodList.get(index).count / 100);
            food.caffeine = roundedSum(food.caffeine += foodList.get(index).caffeine * foodList.get(index).count / 100);
        }

        Intent intent = new Intent(getActivity(), FoodActivity.class)
                .putExtra(Constants.EXTRAS.FOOD, food);
        startActivity(intent);
    }

    private double roundedSum(double value) {
        return (double) Math.round(value * Math.pow(10, 4)) / (long) Math.pow(10, 4);
    }

    private class DividerDecoration extends RecyclerView.ItemDecoration {
        private Drawable divider;

        DividerDecoration(Context context) {
            divider = context.getResources().getDrawable(R.drawable.horizontal_divider);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }
}