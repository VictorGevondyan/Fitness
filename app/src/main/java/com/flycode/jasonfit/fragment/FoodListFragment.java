package com.flycode.jasonfit.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.flycode.jasonfit.Constants;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.FoodActivity;
import com.flycode.jasonfit.adapter.FoodListAdapter;
import com.flycode.jasonfit.model.Food;

import java.util.ArrayList;
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

    private ArrayList<Food> addedFoods = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View foodListView = inflater.inflate(R.layout.fragment_food_list, container, false);

        unbinder = ButterKnife.bind(this, foodListView);

        searchQuery = "";
        category = "";

        List<Food> foodList = new Select().from(Food.class).execute();

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
    public void onAddFoodItemClickListener(Food food) {
        addedFoods.add(food);
    }

    @Override
    public void onRemoveFoodItemClickListener(Food food) {
        if (!addedFoods.contains(food)) {
            return;
        }

        addedFoods.remove(food);
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

        if (addedFoods.size() == 0) {
            return;
        }

        Food food = new Food();

        for (int index = 0; index < addedFoods.size(); index++ ) {

            if (food.food == null) {

                food.food = addedFoods.get(index).food;
                food.foodDe = addedFoods.get(index).foodDe;
            } else {

                food.food += ( ", " + addedFoods.get(index).food);
                food.foodDe += ( ", " + addedFoods.get(index).foodDe);
            }

            food.water = roundedSum(food.water += addedFoods.get(index).water);
            food.energy = roundedSum(food.energy += addedFoods.get(index).energy);
            food.protein = roundedSum(food.protein += addedFoods.get(index).protein);
            food.fat = roundedSum(food.fat += addedFoods.get(index).fat);
            food.carbohydrate = roundedSum(food.carbohydrate += addedFoods.get(index).carbohydrate);
            food.fiber = roundedSum(food.fiber += addedFoods.get(index).fiber);
            food.sugars = roundedSum(food.sugars += addedFoods.get(index).sugars);
            food.calcium = roundedSum(food.calcium += addedFoods.get(index).calcium);
            food.iron = roundedSum(food.iron += addedFoods.get(index).iron);
            food.magnesium = roundedSum(food.magnesium += addedFoods.get(index).magnesium);
            food.phosphorus = roundedSum(food.phosphorus += addedFoods.get(index).phosphorus);
            food.potassium = roundedSum(food.potassium += addedFoods.get(index).potassium);
            food.sodium = roundedSum(food.sodium += addedFoods.get(index).sodium);
            food.zinc = roundedSum(food.zinc += addedFoods.get(index).zinc);
            food.vitaminC = roundedSum(food.vitaminC += addedFoods.get(index).vitaminC);
            food.thiamin = roundedSum(food.thiamin += addedFoods.get(index).thiamin);
            food.riboflavin = roundedSum(food.riboflavin += addedFoods.get(index).riboflavin);
            food.niacin = roundedSum(food.niacin += addedFoods.get(index).niacin);
            food.vitaminB6 = roundedSum(food.vitaminB6 += addedFoods.get(index).vitaminB6);
            food.folateDFE = roundedSum(food.folateDFE += addedFoods.get(index).folateDFE);
            food.vitaminB12 = roundedSum(food.vitaminB12 += addedFoods.get(index).vitaminB12);
            food.vitaminARAE = roundedSum(food.vitaminARAE += addedFoods.get(index).vitaminARAE);
            food.vitaminAIU = roundedSum(food.vitaminAIU += addedFoods.get(index).vitaminAIU);
            food.vitaminE = roundedSum(food.vitaminE += addedFoods.get(index).vitaminE);
            food.vitaminDD2D3 = roundedSum(food.vitaminDD2D3 += addedFoods.get(index).vitaminDD2D3);
            food.vitaminD = roundedSum(food.vitaminD += addedFoods.get(index).vitaminD);
            food.vitaminK = roundedSum(food.vitaminK += addedFoods.get(index).vitaminK);
            food.fattyAcidsTotalSaturated = roundedSum(food.fattyAcidsTotalSaturated += addedFoods.get(index).fattyAcidsTotalSaturated);
            food.fattyAcidsTotalMonounsaturated = roundedSum(food.fattyAcidsTotalMonounsaturated += addedFoods.get(index).fattyAcidsTotalMonounsaturated);
            food.fattyAcidsTotalPolyunsaturated = roundedSum(food.fattyAcidsTotalPolyunsaturated += addedFoods.get(index).fattyAcidsTotalPolyunsaturated);
            food.fattyAcidsTotalTrans = roundedSum(food.fattyAcidsTotalTrans += addedFoods.get(index).fattyAcidsTotalTrans);
            food.cholesterol = roundedSum(food.cholesterol += addedFoods.get(index).cholesterol);
            food.caffeine = roundedSum(food.caffeine += addedFoods.get(index).caffeine);
        }

        Intent intent = new Intent(getActivity(), FoodActivity.class)
                .putExtra(Constants.EXTRAS.FOOD, food);
        startActivity(intent);
    }

    private double roundedSum(double value) {
        return (double) Math.round(value * Math.pow(10, 4)) / (long) Math.pow(10, 4);
    }
}