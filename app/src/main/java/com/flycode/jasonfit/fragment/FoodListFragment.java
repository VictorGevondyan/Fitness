package com.flycode.jasonfit.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DividerItemDecoration;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Created by Schumakher on 3/7/17.
 */

public class FoodListFragment extends Fragment implements FoodListAdapter.OnFoodItemClickListener {
    @BindView(R.id.food) RecyclerView foodRecyclerView;
    @BindView(R.id.tab_bar) TabLayout tabLayout;

    private Unbinder unbinder;
    private FoodListAdapter foodListAdapter;
    private String searchQuery;
    private String category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View foodListView = inflater.inflate(R.layout.fragment_food_list, container, false);

        unbinder = ButterKnife.bind(this, foodListView);

        searchQuery = "";
        category = "";

        List<Food> foodList = new Select().from(Food.class).execute();

        foodListAdapter = new FoodListAdapter(foodList, this);

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

                if (!category.isEmpty()) {
                    query.where("category = ?", category);
                }

                List<Food> foodList = query
                        .where("food LIKE ?", searchQuery)
                        .execute();
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
    public void onSearch(CharSequence charSequence, int i, int i1, int i2) {
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
}