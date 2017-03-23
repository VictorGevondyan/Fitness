package com.flycode.jasonfit.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.MealsTypesListAdapter;
import com.flycode.jasonfit.model.Food;
import com.flycode.jasonfit.model.Meal;
import com.flycode.jasonfit.model.MealsType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Schumakher on 3/7/17.
 */

public class MealListFragment extends Fragment {

    @BindView(R.id.meals_recycler) RecyclerView mealsRecyclerView;

    private Unbinder unbinder;
    private MealsTypesListAdapter mealsTypesListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mealsView = inflater.inflate(R.layout.fragment_meal_list, container, false);

        unbinder = ButterKnife.bind(this, mealsView);



        ArrayList<MealsType> mealTypesList = new ArrayList<>();
//        ArrayList<Meal> correspondingMeals = new ArrayList<>();

        String[] mealTypes = getResources().getStringArray(R.array.meal_types);

        ArrayList<String> mealTypeNames = new ArrayList<>();

        int index;
        for( index = 0; index < mealTypes.length; index++ ){
            mealTypeNames.add(mealTypes[index]);
        }


        String mealTypeName;
        int mealTypesIndex;
        for( mealTypesIndex = 0; mealTypesIndex < mealTypeNames.size(); mealTypesIndex++ ) {

            mealTypeName = mealTypeNames.get(mealTypesIndex);

            List<Meal> correspondingMealsList = new Select()
                    .from(Meal.class)
                    .where("type = ?", mealTypeName)
                    .execute();

            mealTypesList.add( new MealsType(mealTypeName, correspondingMealsList) );

        }




        mealsTypesListAdapter = new MealsTypesListAdapter(getActivity(), mealTypesList);

        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mealsRecyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        mealsRecyclerView.setAdapter(mealsTypesListAdapter);

        return mealsView;
    }

    /*@Override
    public void onMealItemClick(Meal meal) {
        String mealType = meal.type;
    }*/

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
